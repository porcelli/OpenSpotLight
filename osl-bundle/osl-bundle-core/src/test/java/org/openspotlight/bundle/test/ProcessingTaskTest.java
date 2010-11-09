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
package org.openspotlight.bundle.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.bundle.test.ExampleExecutionHistory.getData;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.openspotlight.common.Triple;
import org.openspotlight.domain.Repository;
import org.openspotlight.domain.RepositoryBuilder;
import org.openspotlight.federation.domain.artifact.Artifact;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 4, 2010 Time: 3:08:11 PM To change this template use File | Settings | File
 * Templates.
 */
public class ProcessingTaskTest extends AbstractBundleTest {

    @Override
    public Repository createRepository() {
        return RepositoryBuilder.newRepositoryNamed("repository")
                .withGroup("group").withArtifactSource("file://src", "/")
                .withTasks(ExampleProcessingTask.class)
                .withTasks(AnotherExampleProcessingTask.class)
                .withTasks(AnotherExampleProcessingTask.class)
                .withTasks(AnotherExampleProcessingTask.class)
                .withTasks(AnotherExampleProcessingTask.class)
                .withTasks(AnotherOneExampleProcessingTask.class)
                .andCreate();
    }

    @Test
    public void shouldProcessFederatedSources()
        throws Exception {
        getScheduler().fireSchedulable(null, null, getArtifactSource());
        final Iterator<Triple<Class<? extends Callable>, Artifact, String>> it = getData().iterator();
        assertThat(it.next(),
            is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(ExampleProcessingTask.class, null, "bundle task 1")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(
            AnotherExampleProcessingTask.class, null, "bundle task 2")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(
            AnotherExampleProcessingTask.class, null, "bundle task 3")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(
            AnotherExampleProcessingTask.class, null, "bundle task 4")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(
            AnotherExampleProcessingTask.class, null, "bundle task 5")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(
            AnotherOneExampleProcessingTask.class, null, "bundle task 6")));
    }
}
