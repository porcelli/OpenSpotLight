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
import static org.openspotlight.federation.data.util.ConfiguratonNodes.findAllNodesOfType;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.Files;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.Project;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.ArtifactLoaderGroup;
import org.openspotlight.federation.data.load.DatabaseArtifactLoader;
import org.openspotlight.federation.data.load.XmlConfigurationManager;
import org.openspotlight.federation.data.load.db.DatabaseType;
import org.openspotlight.federation.data.load.db.test.H2DatabaseTest;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.processing.BundleProcessor.GraphContext;

@SuppressWarnings("all")
public class DbStreamArtifactProcessing {
    
    @BeforeClass
    public static void setupH2() throws Exception {
        Files.delete("./target/test-data/DbStreamArtifactProcessing/h2/");
        final H2DatabaseTest dbTest = new H2DatabaseTest() {
            
            @Override
            protected String getUrl() {
                return "jdbc:h2:./target/test-data/DbStreamArtifactProcessing/h2/db";
            }
            
        };
        dbTest.createConnection();
        dbTest.loadConfig();
        dbTest.shouldPrepareItems();
        dbTest.closeConnection();
        
    }
    
    private Configuration createDbConfiguration() throws Exception {
        final Configuration configuration = new Configuration();
        final Repository h2Repository = new Repository(configuration,
                "H2 Repository");
        configuration.setNumberOfParallelThreads(4);
        h2Repository.setActive(true);
        final Project h2Project = new Project(h2Repository, "h2 Project");
        h2Project.setActive(true);
        final DbBundle h2Bundle = new DbBundle(h2Project, "H2 Connection");
        h2Bundle.setActive(true);
        h2Bundle.setUser("sa");
        h2Bundle.setType(DatabaseType.H2);
        h2Bundle
                .setInitialLookup("jdbc:h2:./target/test-data/DbStreamArtifactProcessing/h2/db");
        h2Bundle.setDriverClass("org.h2.Driver");
        final ArtifactMapping h2ArtifactMapping = new ArtifactMapping(h2Bundle,
                "DB/PUBLIC/");
        final Included h2IncludedTrigger = new Included(h2ArtifactMapping,
                "trigger/*");
        final Included h2IncludedProcedure = new Included(h2ArtifactMapping,
                "procedure/*");
        final Included h2IncludedTable = new Included(h2ArtifactMapping,
                "table/*");
        final Included h2IncludedFunction = new Included(h2ArtifactMapping,
                "function/*");
        final Included h2IncludedView = new Included(h2ArtifactMapping,
                "view/*");
        final Included h2IncludedIndex = new Included(h2ArtifactMapping,
                "index/*");
        final BundleProcessorType h2CommonProcessor = new BundleProcessorType(
                h2Bundle,
                "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        h2CommonProcessor.setActive(true);
        return configuration;
    }
    
    private Configuration loadAllArtifactsFromThisConfiguration(
            final Configuration configuration) throws Exception {
        final ArtifactLoaderGroup group = new ArtifactLoaderGroup(
                new DatabaseArtifactLoader());
        final Set<Bundle> bundles = findAllNodesOfType(configuration,
                Bundle.class);
        for (final Bundle bundle : bundles) {
            group.loadArtifactsFromMappings(bundle);
            
        }
        return configuration;
        
    }
    
    @Test
    public void shouldCreateValidXmlConfigurationForh2SourceCode()
            throws Exception {
        final XmlConfigurationManager configurationManager = new XmlConfigurationManager(
                "./target/test-data/DbStreamArtifactProcessing/h2-configuration.xml");
        final Configuration configuration = this.createDbConfiguration();
        configurationManager.save(configuration);
    }
    
    @Test
    public void shouldLoadAllArtifactsFromh2SourceCode() throws Exception {
        final Configuration configuration = this
                .loadAllArtifactsFromThisConfiguration(this
                        .createDbConfiguration());
        final Set<Bundle> bundles = findAllNodesOfType(configuration,
                Bundle.class);
        for (final Bundle bundle : bundles) {
            assertThat(bundle.getStreamArtifacts().size() > 0, is(true));
        }
    }
    
    @Test
    public void shouldProcessAllValidh2SourceCode() throws Exception {
        final Configuration configuration = this
                .loadAllArtifactsFromThisConfiguration(this
                        .createDbConfiguration());
        
        final BundleProcessorManager manager = new BundleProcessorManager();
        final GraphContext graphContext = mock(GraphContext.class);
        final Set<StreamArtifact> artifacts = findAllNodesOfType(configuration,
                StreamArtifact.class);
        final Repository repository = configuration
                .getRepositoryByName("H2 Repository");
        manager.processRepository(repository, graphContext);
        assertThat(LogPrinterBundleProcessor.count.get(), is(artifacts.size()));
    }
    
}
