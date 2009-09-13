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

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.impl.Schedulable;
import org.openspotlight.federation.data.processing.BundleProcessor;
import org.openspotlight.federation.data.processing.BundleProcessorManager;

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
	 * @param configurationProvider
	 *            the configuration provider
	 * 
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	public void loadConfiguration(ConfigurationProvider configurationProvider)
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
	 * @param schedulable
	 *            the schedulable
	 * 
	 * @throws JobExecutionException
	 *             the job execution exception
	 */
	public <T extends ConfigurationNode> void fireImmediateExecution(
			Schedulable<T> schedulable) throws JobExecutionException;

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
		 * 
		 */
		private static enum QuartzScheduler implements Scheduler {

			/**
			 * Default instance.
			 */
			INSTANCE;
			
			/**
			 * {@inheritDoc}
			 */
			public <T extends ConfigurationNode> void fireImmediateExecution(
					Schedulable<T> schedulable) throws JobExecutionException {
				// TODO Auto-generated method stub

			}

			/**
			 * {@inheritDoc}
			 */
			public void loadConfiguration(
					ConfigurationProvider configurationProvider)
					throws ConfigurationException {
				// TODO Auto-generated method stub

			}

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
				// TODO Auto-generated method stub

			}

			public void start() throws JobExecutionException {
				// TODO Auto-generated method stub

			}

		}
	}

}
