/**
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
package org.openspotlight.federation.finder.test;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class FileSystemOriginArtifactLoaderTest {

    private FileSystemOriginArtifactLoader loader = new FileSystemOriginArtifactLoader();
    private ArtifactSource                 artifactSource;
    private ArtifactSource                 stringArtifactSource;

    @Before
    public void prepareArtifactSource() throws Exception {
        final Repository repository = new Repository();
        repository.setName("repositoryName");
        artifactSource = new ArtifactSource();
        artifactSource.setRepository(repository);
        artifactSource.setName("classpath");
        artifactSource.setBinary(true);
        artifactSource.setInitialLookup("./src/test/resources/artifacts/not_changed");

        stringArtifactSource = new ArtifactSource();
        stringArtifactSource.setRepository(repository);
        stringArtifactSource.setName("classpath");
        stringArtifactSource.setInitialLookup("./src/test/resources/artifacts/not_changed");

    }

    /**
     * Should find by relative path.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindByRelativePath() throws Exception {
        final StreamArtifact streamArtifact1 = loader.findByPath(StreamArtifact.class, artifactSource,
                                                                 "/folder/subfolder/file_not_changed1",null);
        final StreamArtifact streamArtifact2 = loader.findByRelativePath(StreamArtifact.class, artifactSource, streamArtifact1,
                                                                         "../file_not_changed1",null);
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact2.getArtifactCompleteName(), is("/folder/file_not_changed1"));
    }

    /**
     * Should load not changed artifact.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldLoadNotChangedArtifact() throws Exception {
        final ArtifactWithSyntaxInformation streamArtifact1 = loader.findByPath(StreamArtifact.class, artifactSource,
                                                                                "folder/file_not_changed1",null);
        final ArtifactWithSyntaxInformation streamArtifact2 = loader.findByPath(StreamArtifact.class, artifactSource,
                                                                                "folder/subfolder/file_not_changed1",null);
        final ArtifactWithSyntaxInformation streamArtifact3 = loader.findByPath(StreamArtifact.class, artifactSource,
                                                                                "folder/subfolder/anothersubfolder/file_not_changed1",null);
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

    /**
     * Should find by relative path.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindStringByRelativePath() throws Exception {
        final StringArtifact streamArtifact1 = loader.findByPath(StringArtifact.class, stringArtifactSource,
                                                                 "/folder/subfolder/file_not_changed1",null);
        final StringArtifact streamArtifact2 = loader.findByRelativePath(StringArtifact.class, stringArtifactSource,
                                                                         streamArtifact1, "../file_not_changed1",null);
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact2.getArtifactCompleteName(), is("/folder/file_not_changed1"));

    }

    /**
     * Should load not changed artifact.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldLoadNotChangedStringArtifact() throws Exception {
        final ArtifactWithSyntaxInformation streamArtifact1 = loader.findByPath(StringArtifact.class, stringArtifactSource,
                                                                                "folder/file_not_changed1",null);
        final ArtifactWithSyntaxInformation streamArtifact2 = loader.findByPath(StringArtifact.class, stringArtifactSource,
                                                                                "folder/subfolder/file_not_changed1",null);
        final ArtifactWithSyntaxInformation streamArtifact3 = loader.findByPath(StringArtifact.class, stringArtifactSource,
                                                                                "folder/subfolder/anothersubfolder/file_not_changed1",null);
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

}
