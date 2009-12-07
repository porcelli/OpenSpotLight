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
package org.openspotlight.federation.context;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.LogEventType;

public class DefaultExecutionContextFactoryTest {

	private ExecutionContext context;

	private final ExecutionContextFactory factory = DefaultExecutionContextFactory
			.createFactory();

	@After
	public void closeResources() throws Exception {
		factory.closeResources();
	}

	@Before
	public void setupContext() throws Exception {
		context = factory.createExecutionContext("testUser", "testPassword",
				DefaultJcrDescriptor.TEMP_DESCRIPTOR, "test");
	}

	@Test
	public void shouldUseAllResourcesInsideContext() throws Exception {
		final ArtifactFinder<StreamArtifact> streamArtifactFinder = context
				.getArtifactFinder(StreamArtifact.class);
		Assert.assertThat(streamArtifactFinder, Is.is(IsNull.notNullValue()));
		final ArtifactFinder<TableArtifact> tableArtifactFinder = context
				.getArtifactFinder(TableArtifact.class);
		Assert.assertThat(tableArtifactFinder, Is.is(IsNull.notNullValue()));
		final ConfigurationManager configurationManager = context
				.getDefaultConfigurationManager();
		Assert.assertThat(configurationManager, Is.is(IsNull.notNullValue()));
		final JcrConnectionProvider connectionProvider = context
				.getDefaultConnectionProvider();
		Assert.assertThat(connectionProvider, Is.is(IsNull.notNullValue()));
		final SLGraphSession graphSession = context.getGraphSession();
		Assert.assertThat(graphSession, Is.is(IsNull.notNullValue()));
		final DetailedLogger logger = context.getLogger();
		Assert.assertThat(logger, Is.is(IsNull.notNullValue()));
		streamArtifactFinder.findByPath("/tmp");
		tableArtifactFinder.findByPath("/tmp");

		configurationManager.saveGlobalSettings(new GlobalSettings());

		graphSession.createContext("new context");

		logger.log(context.getUser(), LogEventType.DEBUG, "test");

	}

}
