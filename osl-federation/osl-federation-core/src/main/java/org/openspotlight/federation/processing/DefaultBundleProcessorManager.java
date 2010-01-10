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

package org.openspotlight.federation.processing;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.processing.internal.BundleProcessorExecution;
import org.openspotlight.federation.registry.ArtifactTypeRegistry;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

// TODO: Auto-generated Javadoc
/**
 * The {@link DefaultBundleProcessorManager} is the class reposable to get an
 * {@link GlobalSettings} and to process all {@link Artifact artifacts} on this
 * {@link GlobalSettings}. The {@link DefaultBundleProcessorManager} should get the
 * {@link ArtifactSource bundle's} {@link BundleProcessorType types} and find
 * all the {@link BundleProcessor processors} for each
 * {@link BundleProcessorType type} . After all {@link BundleProcessor
 * processors} was found, the {@link DefaultBundleProcessorManager} should
 * distribute the processing job in some threads obeying the
 * {@link GlobalSettings#getNumberOfParallelThreads() number of threads}
 * configured for this {@link Repository}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public enum DefaultBundleProcessorManager implements BundleProcessorManager {

	INSTANCE;

	public GlobalExecutionStatus executeBundles(final String username,
			final String password, final JcrConnectionDescriptor descriptor,
			final ExecutionContextFactory contextFactory,
			final GlobalSettings settings, final Group... groups)
			throws Exception {
		final GlobalExecutionStatus result = new BundleProcessorExecution(
				username, password, descriptor, contextFactory, settings,
				groups, ArtifactTypeRegistry.INSTANCE
						.getRegisteredArtifactTypes()).execute();
		return result;
	}

	public void executeBundlesInBackground(final String username,
			final String password, final JcrConnectionDescriptor descriptor,
			final ExecutionContextFactory contextFactory,
			final GlobalSettings settings, final Group... groups) {
		new Thread(new Runnable() {
			public void run() {
				try {
					new BundleProcessorExecution(username, password,
							descriptor, contextFactory, settings, groups,
							ArtifactTypeRegistry.INSTANCE
									.getRegisteredArtifactTypes()).execute();

				} catch (final Exception e) {
					Exceptions.catchAndLog(e);
				}

			}
		}).start();
	}
}
