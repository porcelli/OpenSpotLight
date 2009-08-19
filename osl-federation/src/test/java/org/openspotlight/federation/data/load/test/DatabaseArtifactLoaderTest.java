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

package org.openspotlight.federation.data.load.test;

import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createDbConfiguration;

import java.sql.Connection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeType;
import org.openspotlight.federation.data.InstanceMetadata.SharedData;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.DatabaseArtifactLoader;

/**
 * Test for class {@link DatabaseArtifactLoader}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class DatabaseArtifactLoaderTest {
    
    @BeforeClass
    public static void loadDriver() throws Exception {
        forName("org.h2.Driver");
    }
    
    private DatabaseArtifactLoader artifactLoader;
    private Configuration configuration;
    
    @Before
    public void cleanDatabase() throws Exception {
        delete("./target/test-data/DatabaseArtifactLoaderTest/h2");
    }
    
    @Before
    public void createArtifactLoader() {
        this.artifactLoader = new DatabaseArtifactLoader();
    }
    
    @Test
    public void shouldListenChangesOnStreams() throws Exception {
        
        final DbBundle bundle = (DbBundle) createDbConfiguration(
                "DatabaseArtifactLoaderTest").getRepositoryByName(
                "H2 Repository").getProjectByName("h2 Project")
                .getBundleByName("H2 Connection");
        bundle
                .setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/changes/db");
        final SharedData sharedData = bundle.getInstanceMetadata()
                .getSharedData();
        
        Connection connection = getConnection(
                "jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/changes/db",
                "sa", "");
        connection
                .prepareStatement(
                        "create alias exampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"")
                .execute();
        
        connection.commit();
        connection.close();
        
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        sharedData.markAsSaved();
        connection = getConnection(
                "jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/changes/db",
                "sa", "");
        connection.prepareStatement("drop alias exampleProcedure ").execute();
        
        connection
                .prepareStatement(
                        "create alias exampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.anotherFlagProcedure\"")
                .execute();
        connection.commit();
        connection.close();
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        
        assertThat(sharedData.getDirtyNodes().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().get(0).getType(),
                is(ItemChangeType.CHANGED));
    }
    
    @Test
    public void shouldListenExclusionsOnStreams() throws Exception {
        final DbBundle bundle = (DbBundle) createDbConfiguration(
                "DatabaseArtifactLoaderTest").getRepositoryByName(
                "H2 Repository").getProjectByName("h2 Project")
                .getBundleByName("H2 Connection");
        bundle
                .setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/exclusions/db");
        final SharedData sharedData = bundle.getInstanceMetadata()
                .getSharedData();
        
        Connection connection = getConnection(
                "jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/exclusions/db",
                "sa", "");
        connection
                .prepareStatement(
                        "create alias exampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"")
                .execute();
        connection.commit();
        connection.close();
        
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        sharedData.markAsSaved();
        
        connection = getConnection(
                "jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/exclusions/db",
                "sa", "");
        connection.prepareStatement("drop alias exampleProcedure ").execute();
        connection.commit();
        connection.close();
        
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        
        assertThat(sharedData.getDirtyNodes().size(), is(0));
        assertThat(sharedData.getNodeChangesSinceLastSave().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().get(0).getType(),
                is(ItemChangeType.EXCLUDED));
    }
    
    @Test
    public void shouldListenInclusionsOnStreams() throws Exception {
        final DbBundle bundle = (DbBundle) createDbConfiguration(
                "DatabaseArtifactLoaderTest").getRepositoryByName(
                "H2 Repository").getProjectByName("h2 Project")
                .getBundleByName("H2 Connection");
        bundle
                .setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        
        final SharedData sharedData = bundle.getInstanceMetadata()
                .getSharedData();
        sharedData.markAsSaved();
        final Connection connection = getConnection(
                "jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
                "sa", "");
        connection
                .prepareStatement(
                        "create alias exampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"")
                .execute();
        connection.commit();
        connection.close();
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        
        assertThat(sharedData.getDirtyNodes().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().get(0).getType(),
                is(ItemChangeType.ADDED));
        
    }
    
}
