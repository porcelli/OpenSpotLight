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

package org.openspotlight.federation.data.processing.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createDbConfiguration;
import static org.openspotlight.federation.data.util.ConfigurationNodes.findAllNodesOfType;

import java.sql.Connection;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.Files;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.ArtifactLoaderGroup;
import org.openspotlight.federation.data.load.DatabaseStreamLoader;
import org.openspotlight.federation.data.load.db.test.H2Support;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphSession;

@SuppressWarnings("all")
public class DbStreamArtifactProcessing {

	@BeforeClass
	public static void setupH2() throws Exception {
		Files.delete("./target/test-data/DbStreamArtifactProcessing/h2/");
		Configuration configuration = createDbConfiguration("DbStreamArtifactProcessing");
		DbBundle bundle = (DbBundle) configuration.getRepositoryByName(
				"H2 Repository") //$NON-NLS-1$
				.getProjectByName("h2 Project") //$NON-NLS-1$
				.getBundleByName("H2 Connection"); //$NON-NLS-1$
		Connection conn = createConnection(bundle);
		try {
			H2Support.fillDatabaseArtifacts(conn);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	private Configuration loadAllArtifactsFromThisConfiguration(
			final Configuration configuration) throws Exception {
		final ArtifactLoaderGroup group = new ArtifactLoaderGroup(
				new DatabaseStreamLoader());
		final Set<Bundle> bundles = findAllNodesOfType(configuration,
				Bundle.class);
		for (final Bundle bundle : bundles) {
			group.loadArtifactsFromMappings(bundle);

		}
		return configuration;

	}

	@Test
	public void shouldLoadAllArtifactsFromh2SourceCode() throws Exception {
		final Configuration configuration = this
				.loadAllArtifactsFromThisConfiguration(createDbConfiguration("DbStreamArtifactProcessing"));
		final Set<Bundle> bundles = findAllNodesOfType(configuration,
				Bundle.class);
		for (final Bundle bundle : bundles) {
			assertThat(bundle.getStreamArtifacts().size() > 0, is(true));
		}
	}

	@Test
	public void shouldProcessAllValidh2SourceCode() throws Exception {
		final Configuration configuration = this
				.loadAllArtifactsFromThisConfiguration(createDbConfiguration("DbStreamArtifactProcessing"));
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);

		final BundleProcessorManager manager = new BundleProcessorManager(graph);
		final Set<StreamArtifact> artifacts = findAllNodesOfType(configuration,
				StreamArtifact.class);
		final Repository repository = configuration
				.getRepositoryByName("H2 Repository");
		manager.processRepository(repository);
		assertThat(LogPrinterBundleProcessor.count.get(), is(artifacts.size()));
	}

}
