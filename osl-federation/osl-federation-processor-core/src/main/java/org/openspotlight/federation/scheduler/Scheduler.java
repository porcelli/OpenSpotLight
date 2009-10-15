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
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Schedulable;
import org.openspotlight.federation.data.impl.ScheduleData;
import org.openspotlight.federation.data.load.ConfigurationManagerProvider;
import org.openspotlight.federation.data.processing.BundleProcessor;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.util.ConfigurationNodes;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * This interface is to be used on Scheduler implementations to execute the {@link BundleProcessor} on specified interfals. Inside
 * this Interface there's also some helper classes and a factory class with the default implementation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public interface Scheduler {

    /**
     * A factory class used to create default instances of {@link Scheduler}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    public static final class Factory {

        /**
         * {@link Scheduler} implementation using Quartz.
         * 
         * @author Luiz Fernando Teston - feu.teston@caravelatech.com
         */
        static enum QuartzScheduler implements Scheduler {

            /** Default instance. */
            INSTANCE;

            /** The provider. */
            private ConfigurationManagerProvider     provider;

            /** The Constant BUNDLE_PROCESSOR_MANAGER. */
            static final String                      BUNDLE_PROCESSOR_MANAGER       = "bundle_processor_manager";

            /** The Constant BUNDLE_TO_PROCESS_ID. */
            static final String                      BUNDLE_TO_PROCESS_ID           = "bundle_to_process_id";

            /** The Constant CONFIGURATION_MANAGER_PROVIDER. */
            static final String                      CONFIGURATION_MANAGER_PROVIDER = "configuration_manager_provider";

            /** The Constant BUNDLE_VERSION_ID. */
            static final String                      BUNDLE_VERSION_ID              = "bundle_version_id";

            /** The scheduler. */
            private final org.quartz.Scheduler       scheduler;

            /** The job entries. */
            private final List<Pair<String, String>> jobEntries                     = new ArrayList<Pair<String, String>>();

            /** The bundle processor manager. */
            private BundleProcessorManager           bundleProcessorManager;

            /**
             * Instantiates a new quartz scheduler.
             */
            private QuartzScheduler() {
                try {
                    this.scheduler = new StdSchedulerFactory().getScheduler();
                } catch (final SchedulerException e) {
                    throw logAndReturnNew(e, ConfigurationException.class);
                }
            }

            /**
             * Creates the job.
             * 
             * @param bundle the bundle
             * @return the job detail
             */
            private synchronized JobDetail createJobDetail( final Bundle bundle ) {
                final String name = bundle.getInstanceMetadata().getPath();
                final String group = bundle.getInstanceMetadata().getDefaultParent().getInstanceMetadata().getPath();
                final JobDetail detail = new JobDetail(name, group, OslJob.class);
                jobEntries.add(new Pair<String, String>(name, group));
                final String uniqueId = bundle.getInstanceMetadata().getSavedUniqueId();
                assert uniqueId != null;
                final JobDataMap detailMap = detail.getJobDataMap();
                detailMap.put(BUNDLE_TO_PROCESS_ID, uniqueId);
                detailMap.put(BUNDLE_PROCESSOR_MANAGER, this.bundleProcessorManager);
                detailMap.put(CONFIGURATION_MANAGER_PROVIDER, this.provider);
                // FIXME not using the version id here
                return detail;
            }

            /**
             * {@inheritDoc}
             */
            public <T extends ConfigurationNode> void fireImmediateExecution( final Bundle... bundles )
                throws JobExecutionException {
                try {
                    for (final Bundle b : bundles) {
                        final long startTime = System.currentTimeMillis() + 1000L;
                        final Date date = new Date(startTime);
                        final SimpleTrigger trigger = new SimpleTrigger("only once: " + date + " " + b.toString(), null, date,
                                                                        null, 0, 0L);
                        final JobDetail detail = createJobDetail(b);
                        scheduler.scheduleJob(detail, trigger);
                    }
                } catch (final Exception e) {
                    throw logAndReturnNew(e, JobExecutionException.class);
                }
            }

            /**
             * {@inheritDoc}
             */
            public synchronized void loadConfiguration( final Configuration configuration ) throws ConfigurationException {
                try {

                    for (final Pair<String, String> entry : jobEntries) {
                        this.scheduler.deleteJob(entry.getK1(), entry.getK2());
                    }
                    final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(configuration, Bundle.class);
                    for (final Bundle bundle : bundles) {
                        for (final ScheduleData data : bundle.getScheduleDataForThisBundle()) {

                            final JobDetail detail = createJobDetail(bundle);

                            final CronTrigger trigger = new CronTrigger(data.getDescription(), bundle.getName(),
                                                                        bundle.getRootGroup().getName(), bundle.getName(),
                                                                        data.getCronInformation());
                            scheduler.scheduleJob(detail, trigger);
                        }
                    }
                } catch (final Exception e) {
                    logAndThrowNew(e, ConfigurationException.class);
                }

            }

            /**
             * {@inheritDoc}
             */
            public void setBundleProcessorManager( final BundleProcessorManager bundleProcessorManager ) {
                this.bundleProcessorManager = bundleProcessorManager;
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.scheduler.Scheduler#setConfigurationManagerProvider(org.openspotlight.federation.data.load.ConfigurationManagerProvider)
             */
            public void setConfigurationManagerProvider( final ConfigurationManagerProvider provider ) {
                this.provider = provider;
            }

            /**
             * {@inheritDoc}
             */
            public void shutdown() throws ConfigurationException {
                try {
                    scheduler.shutdown(true);
                } catch (final SchedulerException e) {
                    throw logAndReturnNew(e, ConfigurationException.class);
                }
            }

            /**
             * {@inheritDoc}
             */
            public void start() throws JobExecutionException {
                try {
                    scheduler.start();
                } catch (final SchedulerException e) {
                    throw logAndReturnNew(e, ConfigurationException.class);
                }
            }

        }

        /**
         * Creates the scheduler.
         * 
         * @return the scheduler
         */
        public static Scheduler createScheduler() {
            return QuartzScheduler.INSTANCE;
        }
    }

    /**
     * Fire immediate execution for a {@link Schedulable} node.
     * 
     * @param bundles the bundles
     * @throws JobExecutionException the job execution exception
     */
    public <T extends ConfigurationNode> void fireImmediateExecution( Bundle... bundles ) throws JobExecutionException;

    /**
     * Load or reload the configuration.
     * 
     * @param configuration the configuration
     * @throws ConfigurationException the configuration exception
     */
    public void loadConfiguration( Configuration configuration ) throws ConfigurationException;

    /**
     * Sets the bundle processor manager.
     * 
     * @param bundleProcessorManager the new bundle processor manager
     */
    public void setBundleProcessorManager( BundleProcessorManager bundleProcessorManager );

    /**
     * Sets the configuration manager provider.
     * 
     * @param provider the new configuration manager provider
     */
    public void setConfigurationManagerProvider( ConfigurationManagerProvider provider );

    /**
     * Shutdown the execution scheduling.
     * 
     * @throws ConfigurationException the configuration exception
     */
    public void shutdown() throws ConfigurationException;

    /**
     * Starts the execution scheduling.
     * 
     * @throws JobExecutionException the job execution exception
     */
    public void start() throws JobExecutionException;

}
