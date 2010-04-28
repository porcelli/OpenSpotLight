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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.federation.data.load.DNASvnArtifactFinder;
import org.openspotlight.federation.domain.DnaSvnArtifactSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.StringArtifact;

/**
 * Test for class {@link DNASvnArtifactFinder}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */

@SuppressWarnings( "all" )
public class DnaSvnArtifactLoaderTest {

    @Test
    public void shouldLoadAFile() throws Exception {
        final DnaSvnArtifactSource bundle = new DnaSvnArtifactSource();
        bundle.setActive(true);
        bundle.setName("DNA SVN");
        bundle.setInitialLookup("https://openspotlight.dev.java.net/svn/openspotlight/trunk/");
        bundle.setUserName("feuteston");
        bundle.setPassword("jakadeed");
        final Repository repository = new Repository();
        repository.setName("repository");
        bundle.setRepository(repository);

        final DNASvnArtifactFinder finder = new DNASvnArtifactFinder();
        final StringArtifact sa = finder.findByPath(StringArtifact.class, bundle, "source/osl/pom.xml");

        assertThat(sa, is(notNullValue()));
        assertThat(sa.getContent(), is(notNullValue()));

        finder.closeResources();
    }

    @Ignore
    // this test is working, but it takes a long time
    @Test
    public void shouldRetrieveArtifactNames() throws Exception {

        final DnaSvnArtifactSource bundle = new DnaSvnArtifactSource();
        bundle.setActive(true);
        bundle.setName("DNA SVN");
        bundle.setInitialLookup("https://openspotlight.dev.java.net/svn/openspotlight/trunk/");
        bundle.setUserName("feuteston");
        bundle.setPassword("jakadeed");
        final Repository repository = new Repository();
        repository.setName("repository");
        bundle.setRepository(repository);
        final DNASvnArtifactFinder finder = new DNASvnArtifactFinder();
        final Set<String> allNames = finder.getInternalMethods().retrieveOriginalNames(StringArtifact.class, bundle,
                                                                                       "source/osl/osl-common/src/main/java/org/openspotlight/common/");
        assertThat(allNames.size(), is(not(0)));

        finder.closeResources();
        for (final String n : allNames) {
            System.out.println(n);
        }
    }

}
