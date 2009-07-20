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

package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Project;
import org.openspotlight.federation.data.impl.Repository;

public class NodeTest {
    
    public NodeTest() {
        super();
    }
    
    public void assertTheSameInitialDataOnSomeNodes(
            final Configuration configuration, final boolean verifyArtifacts) {
        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getRepositories().size(), is(not(0)));
        assertThat(configuration.getRepositoryNames().size(), is(not(0)));
        final Repository repository = configuration.getRepositoryByName("r-1");
        assertThat(repository, is(notNullValue()));
        assertThat(repository.getActive(), is(true));
        assertThat(repository.getConfiguration(), is(configuration));
        assertThat(repository.getNumberOfParallelThreads(), is(1));
        assertThat(repository.getProjects().size(), is(not(0)));
        assertThat(repository.getProjectNames().size(), is(not(0)));
        
        final Project project = repository.getProjectByName("p-1,1");
        assertThat(project.getActive(), is(true));
        assertThat(project.getRepository(), is(repository));
        assertThat(project.getBundleNames().size(), is(not(0)));
        assertThat(project.getBundles().size(), is(not(0)));
        
        final Bundle bundle = project.getBundleByName("b-1,1,1");
        assertThat(bundle, is(notNullValue()));
        assertThat(bundle.getActive(), is(true));
        assertThat(bundle.getInitialLookup(), is("initialLookup"));
        assertThat(bundle.getType(), is("type"));
        assertThat(bundle.getProject(), is(project));
        assertThat(bundle.getArtifactMappings().size(), is(not(0)));
        assertThat(bundle.getArtifactMappingNames().size(), is(not(0)));
        if (verifyArtifacts) {
            assertThat(bundle.getArtifacts().size(), is(not(0)));
            assertThat(bundle.getArtifacts().size(), is(not(0)));
        }
        final ArtifactMapping artifactMapping = bundle
                .getArtifactMappingByName("rm-1,1,1,1");
        assertThat(artifactMapping, is(notNullValue()));
        assertThat(artifactMapping.getActive(), is(true));
        assertThat(artifactMapping.getBundle(), is(bundle));
        assertThat(artifactMapping.getExcluded(), is("**/*.excluded"));
        assertThat(artifactMapping.getIncluded(), is("*"));
        
        if (verifyArtifacts) {
            final StreamArtifact artifact = bundle.getArtifactByName("r-1,1,1,1");
            // THIS IS TRANSIENT : Artifact.getData()
            assertThat(artifact.getDataSha1(), is(notNullValue()));
        }
    }
    
    public Configuration createSampleData() {
        
        final int[] numbers = new int[] { 1, 2, 3, 4, 5 };
        
        final Configuration configuration = new Configuration();
        for (final int i : numbers) {
            final Repository repository = new Repository("r-" + i,
                    configuration);
            repository.setActive(true);
            repository.setNumberOfParallelThreads(1);
            for (final int j : numbers) {
                final Project project = new Project("p-" + i + "," + j,
                        repository);
                project.setActive(true);
                for (final int k : numbers) {
                    final Bundle bundle = new Bundle("b-" + i + "," + j + ","
                            + k, project);
                    bundle.setActive(true);
                    bundle.setInitialLookup("initialLookup");
                    bundle.setType("type");
                    for (final int l : numbers) {
                        final ArtifactMapping ArtifactMapping = new ArtifactMapping(
                                "rm-" + i + "," + j + "," + k + "," + l, bundle);
                        ArtifactMapping.setActive(true);
                        ArtifactMapping.setExcluded("**/*.excluded");
                        ArtifactMapping.setIncluded("*");
                        
                    }
                    for (final int m : numbers) {
                        final StreamArtifact Artifact = new StreamArtifact("r-" + i + ","
                                + j + "," + k + "," + m, bundle);
                        Artifact.setData(new ByteArrayInputStream(new byte[0]));
                        Artifact.setDataSha1("sha1");
                    }
                }
            }
        }
        return configuration;
    }
    
    @Test
    public void shouldCreateSampleData() {
        final Configuration configuration = this.createSampleData();
        this.assertTheSameInitialDataOnSomeNodes(configuration, true);
    }
    
    @Test
    public void shouldFindArtifactByName() throws Exception {
        final Configuration configuration = this.createSampleData();
        final StreamArtifact artifact = configuration.findByName("initialLookup",
                "r-1,1,1,1");
        assertThat(artifact, is(notNullValue()));
    }
    
    @Test
    public void shouldRetturnNullWhenFindingArtifactWithInvalidName()
            throws Exception {
        final Configuration configuration = this.createSampleData();
        StreamArtifact artifact = configuration.findByName("initialLookup",
                "invalidName");
        assertThat(artifact, is(nullValue()));
        artifact = configuration.findByName("invalidName", "invalidName");
        assertThat(artifact, is(nullValue()));
    }
    
}