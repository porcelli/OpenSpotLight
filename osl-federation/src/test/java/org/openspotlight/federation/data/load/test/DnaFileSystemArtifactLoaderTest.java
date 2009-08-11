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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeType;
import org.openspotlight.federation.data.InstanceMetadata.SharedData;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.Project;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.DnaFileSystemArtifactLoader;

/**
 * Test for class {@link DnaFileSystemArtifactLoader}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class DnaFileSystemArtifactLoaderTest extends AbstractArtifactLoaderTest {
    
    @Override
    @Before
    public void createArtifactLoader() {
        this.artifactLoader = new DnaFileSystemArtifactLoader();
    }
    
    @Override
    @Before
    public void createConfiguration() throws Exception {
        this.configuration = new Configuration();
        final Repository repository = new Repository(this.configuration,
                this.REPOSITORY_NAME);
        this.configuration.setNumberOfParallelThreads(4);
        final Project project = new Project(repository, this.PROJECT_NAME);
        final Bundle bundle = new Bundle(project, this.BUNDLE_NAME);
        final String basePath = new File("../osl-federation/")
                .getCanonicalPath()
                + "/";
        bundle.setInitialLookup(basePath);
        final ArtifactMapping artifactMapping = new ArtifactMapping(bundle,
                "src/");
        new Included(artifactMapping, "main/java/**/*.java");
    }
    
    public Bundle createConfigurationForChangeListen() throws Exception {
        this.configuration = new Configuration();
        final Repository repository = new Repository(this.configuration,
                "Local target folder");
        this.configuration.setNumberOfParallelThreads(4);
        final Project project = new Project(repository, "Osl Federation");
        final Bundle bundle = new Bundle(project, "Target folder");
        final String basePath = new File(
                "../osl-federation/target/test-data/DnaFileSystemArtifactLoaderTest/")
                .getCanonicalPath()
                + "/";
        bundle.setInitialLookup(basePath);
        final ArtifactMapping artifactMapping = new ArtifactMapping(bundle,
                "aFolder/");
        new Included(artifactMapping, "*.txt");
        return bundle;
    }
    
    @Test
    public void shouldListenChanges() throws Exception {
        new File("target/test-data/DnaFileSystemArtifactLoaderTest/aFolder/")
                .mkdirs();
        final File textFile = new File(
                "target/test-data/DnaFileSystemArtifactLoaderTest/aFolder/willBeChanged.txt");
        FileOutputStream fos = new FileOutputStream(textFile);
        fos.write("new text content".getBytes());
        fos.flush();
        fos.close();
        
        final Bundle bundle = this.createConfigurationForChangeListen();
        final SharedData sharedData = bundle.getInstanceMetadata()
                .getSharedData();
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        sharedData.markAsSaved();
        
        fos = new FileOutputStream(textFile);
        fos.write("changed text content".getBytes());
        fos.flush();
        fos.close();
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        
        assertThat(sharedData.getDirtyNodes().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().get(0).getType(),
                is(ItemChangeType.CHANGED));
        textFile.delete();
    }
    
    @Test
    public void shouldListenExclusions() throws Exception {
        new File("target/test-data/DnaFileSystemArtifactLoaderTest/aFolder/")
                .mkdirs();
        final File textFile = new File(
                "target/test-data/DnaFileSystemArtifactLoaderTest/aFolder/willBeExcluded.txt");
        final FileOutputStream fos = new FileOutputStream(textFile);
        fos.write("new text content".getBytes());
        fos.flush();
        fos.close();
        
        final Bundle bundle = this.createConfigurationForChangeListen();
        final SharedData sharedData = bundle.getInstanceMetadata()
                .getSharedData();
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        sharedData.markAsSaved();
        
        assertThat(textFile.delete(), is(true));
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        
        assertThat(sharedData.getDirtyNodes().size(), is(0));
        assertThat(sharedData.getNodeChangesSinceLastSave().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().get(0).getType(),
                is(ItemChangeType.EXCLUDED));
    }
    
    @Test
    public void shouldListenInclusions() throws Exception {
        new File("target/test-data/DnaFileSystemArtifactLoaderTest/aFolder/")
                .mkdirs();
        final File textFile = new File(
                "target/test-data/DnaFileSystemArtifactLoaderTest/aFolder/newTextFile.txt");
        final FileOutputStream fos = new FileOutputStream(textFile);
        fos.write("new text content".getBytes());
        fos.flush();
        fos.close();
        
        final Bundle bundle = this.createConfigurationForChangeListen();
        final SharedData sharedData = bundle.getInstanceMetadata()
                .getSharedData();
        sharedData.markAsSaved();
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        final StreamArtifact sa = (StreamArtifact) sharedData.getDirtyNodes()
                .iterator().next();
        
        for (final ItemChangeEvent<ConfigurationNode> change : sharedData
                .getNodeChangesSinceLastSave()) {
            System.out.println(change.getType() + " " + change.getNewItem());
        }
        assertThat(sharedData.getNodeChangesSinceLastSave().get(0).getType(),
                is(ItemChangeType.ADDED));
        assertThat(sharedData.getDirtyNodes().size(), is(1));
        assertThat(sharedData.getNodeChangesSinceLastSave().size(), is(1));
        
    }
    
}
