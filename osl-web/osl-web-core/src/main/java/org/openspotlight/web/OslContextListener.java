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
package org.openspotlight.web;

import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.SLScheduler;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

/**
 * The listener interface for receiving oslContext events. The class that is
 * interested in processing a oslContext event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addOslContextListener<code> method. When
 * the oslContext event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see OslContextEvent
 */
public class OslContextListener implements ServletContextListener,
		OslDataConstants {

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	public void contextDestroyed(final ServletContextEvent arg0) {
		WebExecutionContextFactory.INSTANCE.contextStopped();
		final SLScheduler scheduler = DefaultScheduler.INSTANCE;
		scheduler.stopScheduler();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	public void contextInitialized(final ServletContextEvent arg0) {
		try {
			final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.DEFAULT_DESCRIPTOR;
			arg0.getServletContext().setAttribute(CONTEXT__JCR_DESCRIPTOR,
					descriptor);
			WebExecutionContextFactory.INSTANCE.contextStarted();
			final ExecutionContextFactory factory = WebExecutionContextFactory.INSTANCE
					.getFactory();
			final ExecutionContext context = factory.createExecutionContext(
					SLConsts.SYSTEM_USER, SLConsts.SYSTEM_PASSWORD, descriptor,
					SLConsts.DEFAULT_REPOSITORY_NAME);

			final GlobalSettings settings = context
					.getDefaultConfigurationManager().getGlobalSettings();
			final Set<Repository> repositories = context
					.getDefaultConfigurationManager().getAllRepositories();
			final SLScheduler scheduler = DefaultScheduler.INSTANCE;
			scheduler.initializeSettings(factory, SLConsts.SYSTEM_USER,
					SLConsts.SYSTEM_PASSWORD, descriptor);
			scheduler.refreshJobs(settings, repositories);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
		}
	}

}
