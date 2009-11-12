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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.domain.ArtifactMapping;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.JavaArtifactSource;
import org.openspotlight.federation.loader.ArtifactLoader;

/**
 * Test for class {@link AbstractArtifactLoader}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "all" )
public abstract class AbstractArtifactLoaderTest extends NodeTest {

    private static class SampleArtifactLoader extends AbstractArtifactLoader {

        @Override
        protected GlobalExecutionContext createGlobalExecutionContext() {
            return new DefaultGlobalExecutionContext() {

                public Set<String> getAllArtifactNames( final ArtifactSource bundle,
                                                        final ArtifactMapping mapping ) throws ConfigurationException {
                    if (bundle.getStreamArtifacts().size() == 0) {
                        return setOf("1", "2", "3", "4");
                    } else {
                        return Collections.emptySet();
                    }
                }

            };
        }

        @Override
        protected ThreadExecutionContext createThreadExecutionContext() {
            return new DefaultThreadExecutionContext() {

                public byte[] loadArtifactOrReturnNullToIgnore( final ArtifactSource bundle,
                                                                final ArtifactMapping mapping,
                                                                final String artifactName,
                                                                final GlobalExecutionContext context ) throws Exception {
                    return artifactName.getBytes();
                }

            };
        }

    }

    protected ArtifactLoader artifactLoader;

    protected String         BUNDLE_NAME     = "b-1,1,1";

    protected Configuration  configuration;

    protected String         PROJECT_NAME    = "p-1,1";

    protected String         REPOSITORY_NAME = "r1";      ;

    @Before
    public void createArtifactLoader() {
        this.artifactLoader = new SampleArtifactLoader() {

        };
    }

    @Before
    public void createConfiguration() throws Exception {
        this.configuration = this.createSampleData();
    }

    @Test
    public void shouldLoadArtifacts() throws Exception {
        final ArtifactSource bundle = this.configuration.getRepositoryByName(this.REPOSITORY_NAME).getGroupByName(
                                                                                                                  this.PROJECT_NAME).getArtifactSourceByName(
                                                                                                                                                             this.BUNDLE_NAME);
        this.artifactLoader.loadArtifactsFromMappings(bundle);
        if (!(bundle instanceof JavaArtifactSource)) {
            assertThat(bundle.getStreamArtifacts().size(), is(not(0)));
        }
    }

}
