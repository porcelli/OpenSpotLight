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
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Excluded;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.JavaBundle;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;

/**
 * Test class to be used on configuration node tests.
 * 
 * @author Luiz Fernando Teston (Feu Teston)
 */
@SuppressWarnings("all")
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
        assertThat(configuration.getNumberOfParallelThreads(), is(1));
        assertThat(repository.getGroups().size(), is(not(0)));
        assertThat(repository.getGroupNames().size(), is(not(0)));
        
        final Group project = repository.getGroupByName("p-1,1");
        assertThat(project.getActive(), is(true));
        assertThat(project.getRepository(), is(repository));
        assertThat(project.getBundleNames().size(), is(not(0)));
        assertThat(project.getBundles().size(), is(not(0)));
        
        final Group innerProject = project.getGroupByName("ip-1,1");
        assertThat(innerProject.getActive(), is(true));
        assertThat(innerProject.getRepository(), is(repository));
        assertThat(innerProject.getBundleNames().size(), is(0));
        assertThat(innerProject.getBundles().size(), is(0));
        
        final JavaBundle javaBundle = (JavaBundle) project
                .getBundleByName("jb-1,1,1");
        assertThat(javaBundle, is(notNullValue()));
        assertThat(javaBundle.getActive(), is(true));
        assertThat(javaBundle.getInitialLookup(), is("initialLookup"));
        assertThat(javaBundle.getVirtualMachineVersion(), is("1.5"));
        assertThat(javaBundle.getProject(), is(project));
        
        final Bundle bundle = project.getBundleByName("b-1,1,1");
        assertThat(bundle, is(notNullValue()));
        assertThat(bundle.getActive(), is(true));
        assertThat(bundle.getInitialLookup(), is("initialLookup"));
        assertThat(bundle.getProject(), is(project));
        assertThat(bundle.getArtifactMappings().size(), is(not(0)));
        assertThat(bundle.getArtifactMappingNames().size(), is(not(0)));
        if (verifyArtifacts) {
            assertThat(bundle.getStreamArtifacts().size(), is(not(0)));
            assertThat(bundle.getStreamArtifacts().size(), is(not(0)));
        }
        final ArtifactMapping artifactMapping = bundle
                .getArtifactMappingByName("rm-1,1,1,1");
        assertThat(artifactMapping, is(notNullValue()));
        assertThat(artifactMapping.getExcludeds().iterator().next().getName(),
                is("**/*.excluded"));
        assertThat(artifactMapping.getIncludeds().iterator().next().getName(),
                is("*"));
        
        if (verifyArtifacts) {
            final StreamArtifact artifact = bundle
                    .getStreamArtifactByName("r-1,1,1,1");
            // THIS IS TRANSIENT : Artifact.getData()
            assertThat(artifact.getDataSha1(), is(notNullValue()));
        }
    }
    
    public Configuration createSampleData() {
        
        final int[] numbers = new int[] { 1, 2 };
        
        final Configuration configuration = new Configuration();
        configuration.setNumberOfParallelThreads(1);
        
        for (final int i : numbers) {
            final Repository repository = new Repository(configuration, "r-"
                    + i);
            repository.setActive(true);
            for (final int j : numbers) {
                final Group project = new Group(repository, "p-" + i + ","
                        + j);
                project.setActive(true);
                final Group innerProject = new Group(project, "ip-" + i
                        + "," + j);
                innerProject.setActive(true);
                for (final int k : numbers) {
                    
                    final JavaBundle javaBundle = new JavaBundle(project, "jb-"
                            + i + "," + j + "," + k);
                    javaBundle.setActive(true);
                    javaBundle.setInitialLookup("initialLookup");
                    javaBundle.setVirtualMachineVersion("1.5");
                    
                    final Bundle bundle = new Bundle(project, "b-" + i + ","
                            + j + "," + k);
                    bundle.setActive(true);
                    bundle.setInitialLookup("initialLookup");
                    for (final int l : numbers) {
                        final ArtifactMapping artifactMapping = new ArtifactMapping(
                                bundle, "rm-" + i + "," + j + "," + k + "," + l);
                        new Included(artifactMapping, "*");
                        new Excluded(artifactMapping, "**/*.excluded");
                    }
                    for (final int m : numbers) {
                        final StreamArtifact Artifact = new StreamArtifact(
                                bundle, "r-" + i + "," + j + "," + k + "," + m);
                        Artifact.setData(new ByteArrayInputStream("new example"
                                .getBytes()));
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
    
}
