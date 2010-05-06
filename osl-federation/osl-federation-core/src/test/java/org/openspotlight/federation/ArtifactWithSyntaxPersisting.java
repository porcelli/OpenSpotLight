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
package org.openspotlight.federation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.domain.artifact.SyntaxInformationType;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class ArtifactWithSyntaxPersisting {

    public static void main( final String... args ) throws Exception {
        final ArtifactWithSyntaxPersisting test = new ArtifactWithSyntaxPersisting();
        test.shouldPersistLotsOfStuff();
    }

    Random         r           = new Random();

    String[]       sampleLines = {"ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD\n",
        "     SDFSDFSDFSDFS FDS DFSDF\n", "     sdfsfasdfasd FDS DFSDF\n", "\n"};

    String[]       samplePaths = {"dir1", "dir2", "dirthreebigger", "anotherPath"};

    int            maxPath     = 16;

    private List<String> content     = null;

    Set<StringArtifact> createLotsOfStuff() {
        final Set<StringArtifact> stuffList = new HashSet<StringArtifact>();
        for (int i = 0; i < 100; i++) {
            final StringArtifact sa = createNewDummyArtifact();
            stuffList.add(sa);
        }
        return stuffList;
    }

    private StringArtifact createNewDummyArtifact() {
        final int pathListSize = r.nextInt(maxPath - 2) + 2;
        final StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathListSize; i++) {
            final int pathIndex = r.nextInt(samplePaths.length);
            path.append(samplePaths[pathIndex]);
            if (i + 1 != pathListSize) {
                path.append("/");
            }
        }
        final StringArtifact sa = Artifact.createArtifact(StringArtifact.class, path.toString(), ChangeType.INCLUDED);
        sa.getContent().setTransient(getContent());
        for (int i = 0; i < 16000; i++) {
            sa.addSyntaxInformation(i, i, i, i, SyntaxInformationType.COMMENT, null);
        }

        return sa;
    }

    private List<String> getContent() {

        if (content == null) {
            final List<String> list = newLinkedList();
            for (int i = 0; i < 4000; i++) {
                final String line = sampleLines[r.nextInt(sampleLines.length)];
                list.add(line);
            }
            content = list;
        }
        return content;
    }

    @Test
    public void shouldPersistLotsOfStuff() throws Exception {
        final Set<StringArtifact> lotsOfStuff = createLotsOfStuff();

        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                                                                         ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                                                                         repositoryPath("repository")),
                                                 new SimplePersistModule(), new DetailedLoggerModule(),
                                                 new DefaultExecutionContextFactoryModule());

        STStorageSession session = injector.getProvider(STStorageSession.class).get();
        SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist = injector.getInstance(SimplePersistFactory.class).createSimplePersist(
                                                                                                                                                 SLPartition.FEDERATION);

        int count = 0;
        final long start = System.currentTimeMillis();
        for (final StringArtifact a : lotsOfStuff) {
            count++;
            simplePersist.convertBeanToNode(a);
            if (count % 10 == 0) {
                System.out.println(count);
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println("spent on jcr convert and saving: " + (end - start) / 1000 + "s");
    }

}
