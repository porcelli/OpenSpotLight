/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.federation.scheduler;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrowNew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Schedulable;
import org.openspotlight.federation.data.impl.ScheduleData;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.processing.BundleProcessor;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.util.ConfigurationNodes;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * This interface is to be used on Scheduler implementations to execute the
 * {@link BundleProcessor} on specified interfals.
 * 
 * Inside this Interface there's also some helper classes and a factory class
 * with the default implementation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public interface Scheduler {

	/**
	 * Sets the bundle processor manager.
	 * 
	 * @param bundleProcessorManager
	 *            the new bundle processor manager
	 */
	public void setBundleProcessorManager(
			BundleProcessorManager bundleProcessorManager);

	/**
	 * Load or reload the configuration.
	 * 
	 * @param configurationManager
	 *            the configuration provider
	 * 
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	public void loadConfiguration(ConfigurationManager configurationManager)
			throws ConfigurationException;

	/**
	 * Shutdown the execution scheduling.
	 * 
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	public void shutdown() throws ConfigurationException;

	/**
	 * Starts the execution scheduling.
	 * 
	 * @throws JobExecutionException
	 *             the job execution exception
	 */
	public void start() throws JobExecutionException;

	/**
	 * Fire immediate execution for a {@link Schedulable} node.
	 * 
	 * @param bundles
	 *            the bundles
	 * 
	 * @throws JobExecutionException
	 *             the job execution exception
	 */
	public <T extends ConfigurationNode> void fireImmediateExecution(
			Bundle... bundles) throws JobExecutionException;

	/**
	 * A factory class used to create default instances of {@link Scheduler}.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 */
	public static final class Factory {

		/**
		 * Creates the scheduler.
		 * 
		 * @return the scheduler
		 */
		public static Scheduler createScheduler() {
			return QuartzScheduler.INSTANCE;
		}

		/**
		 * {@link Scheduler} implementation using Quartz.
		 * 
		 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
		 */
		private static enum QuartzScheduler implements Scheduler {

			/** Default instance. */
			INSTANCE;

			/** The scheduler. */
			private final org.quartz.Scheduler scheduler;

			/**
			 * Instantiates a new quartz scheduler.
			 */
			private QuartzScheduler() {
				try {
					this.scheduler = new StdSchedulerFactory().getScheduler();
				} catch (SchedulerException e) {
					throw logAndReturnNew(e, ConfigurationException.class);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			public <T extends ConfigurationNode> void fireImmediateExecution(
					Bundle... bundles) throws JobExecutionException {
				try {
					for (Bundle b : bundles) {
						long startTime = System.currentTimeMillis() + 1000L;
						Date date = new Date(startTime);
						SimpleTrigger trigger = new SimpleTrigger("only once: "
								+ date + " " + b.toString(), null, date, null,
								0, 0L);
						JobDetail detail = createJob(b);
						scheduler.scheduleJob(detail, trigger);
					}
				} catch (Exception e) {
					throw logAndReturnNew(e, JobExecutionException.class);
				}
			}

			/**
			 * The Class OslJob.
			 */
			private static class OslJob implements Job {

				/**
				 * {@inheritDoc}
				 */
				public void execute(JobExecutionContext ctx)
						throws org.quartz.JobExecutionException {
					try {
						JobDataMap dataMap = ctx.getJobDetail().getJobDataMap();
						BundleProcessorManager bundleProcessorManager = (BundleProcessorManager) dataMap
								.get(JobDataKeys.BUNDLE_PROCESSOR_MANAGER);
						String bundleId = (String) dataMap
								.get(JobDataKeys.BUNDLE_TO_PROCESS_ID);
						String versionId = (String) dataMap
								.get(JobDataKeys.BUNDLE_VERSION_ID);
						ConfigurationManager manager = (ConfigurationManager) dataMap
								.get(JobDataKeys.CONFIGURATION_MANAGER);
						Configuration configuration = manager
								.load(LazyType.LAZY);
						Bundle bundle = manager.findNodeByUuidAndVersion(
								configuration, Bundle.class, bundleId,
								versionId);

						bundleProcessorManager.processBundles(Arrays
								.asList(bundle));
					} catch (Exception e) {
						throw logAndReturnNew(e,
								org.quartz.JobExecutionException.class);
					}
				}

			}

			/** The configuration provider. */
			private ConfigurationManager configurationManager;

			/**
			 * The Enum JobDataKeys.
			 */
			private static enum JobDataKeys {

				/** The BUNDLE processor manager. */
				BUNDLE_PROCESSOR_MANAGER,
				/** The BUNDLE to process id. */
				BUNDLE_TO_PROCESS_ID,
				/** The CONFIGURATION manager. */
				CONFIGURATION_MANAGER,
				/** The BUNDLE version id. */
				BUNDLE_VERSION_ID;
			}

			private List<Pair<String, String>> jobEntries = new ArrayList<Pair<String, String>>();

			/**
			 * Creates the job.
			 * 
			 * @param bundle
			 *            the bundle
			 * 
			 * @return the job detail
			 */
			private synchronized JobDetail createJob(Bundle bundle) {
				String name = bundle.getInstanceMetadata().getPath();
				String group = bundle.getInstanceMetadata().getDefaultParent()
						.getInstanceMetadata().getPath();
				JobDetail detail = new JobDetail(name, group, OslJob.class);
				jobEntries.add(new Pair<String, String>(name, group));
				String uniqueId = bundle.getInstanceMetadata()
						.getSavedUniqueId();
				assert uniqueId != null;
				JobDataMap detailMap = detail.getJobDataMap();
				detailMap.put(JobDataKeys.BUNDLE_TO_PROCESS_ID, uniqueId);
				detailMap.put(JobDataKeys.BUNDLE_PROCESSOR_MANAGER,
						bundleProcessorManager);
				detailMap.put(JobDataKeys.CONFIGURATION_MANAGER,
						configurationManager);
				// FIXME not using the version id here
				return detail;
			}

			/**
			 * {@inheritDoc}
			 */
			public synchronized void loadConfiguration(
					ConfigurationManager newConfigurationManager)
					throws ConfigurationException {
				try {
					this.configurationManager = newConfigurationManager;
					for (Pair<String, String> entry : jobEntries) {
						this.scheduler.deleteJob(entry.getK1(), entry.getK2());
					}
					Configuration configuration = configurationManager
							.load(LazyType.LAZY);
					Set<Bundle> bundles = ConfigurationNodes
							.findAllNodesOfType(configuration, Bundle.class);
					for (Bundle bundle : bundles) {
						for (ScheduleData data : bundle
								.getScheduleDataForThisBundle()) {

							JobDetail detail = createJob(bundle);

							CronTrigger trigger = new CronTrigger(data
									.getDescription(), bundle.getName(), bundle
									.getRootGroup().getName(),
									bundle.getName(), data.getCronInformation());
							scheduler.scheduleJob(detail, trigger);
						}
					}
				} catch (Exception e) {
					logAndThrowNew(e, ConfigurationException.class);
				}

			}

			/** The bundle processor manager. */
			private BundleProcessorManager bundleProcessorManager;

			/**
			 * {@inheritDoc}
			 */
			public void setBundleProcessorManager(
					BundleProcessorManager bundleProcessorManager) {
				this.bundleProcessorManager = bundleProcessorManager;
			}

			/**
			 * {@inheritDoc}
			 */
			public void shutdown() throws ConfigurationException {
				try {
					scheduler.shutdown(true);
				} catch (SchedulerException e) {
					throw logAndReturnNew(e, ConfigurationException.class);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			public void start() throws JobExecutionException {
				try {
					scheduler.start();
				} catch (SchedulerException e) {
					throw logAndReturnNew(e, ConfigurationException.class);
				}
			}

		}
	}

}
