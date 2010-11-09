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

import static com.google.common.collect.Lists.newLinkedList;
import static org.openspotlight.common.util.Strings.concatPaths;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.util.Files;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.PartitionFactory.RegularPartitions;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;
import org.openspotlight.task.ExecutorInstance;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test class for {@link org.openspotlight.common.util.Files}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@Ignore
public class FilesStressTest {
    private static final String FROM = "../../", TO = "/tmp";

    private final Injector      injector;

    public FilesStressTest() {
        injector = Guice.createInjector(
                new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                        ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                new SimplePersistModule(), new DetailedLoggerModule());

    }

    @Test
    // 61s copying directories
        public
        void shouldLoadFileNamesFaster()
            throws Exception {

        final Set<String> names = Files.listFileNamesFrom(FROM, false);
        final List<Callable<Void>> callables = newLinkedList();
        for (final String fileName: names) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call()
                    throws Exception {
                    final File file = new File(fileName);
                    if (file.isDirectory()) {
                        return null;
                    }
                    final FileInputStream fis = new FileInputStream(file);
                    final String newDirName = concatPaths(TO,
                            fileName.substring(0, fileName.lastIndexOf("/")));
                    new File(newDirName).mkdirs();
                    final FileOutputStream fos = new FileOutputStream(concatPaths(TO,
                            fileName));
                    IOUtils.copy(fis, fos);
                    return null;
                }
            });
        }
        final List<Future<Void>> futures = ExecutorInstance.INSTANCE
                .invokeAll(callables);
        for (final Future<Void> f: futures) {
            f.get();
        }

    }

    @Test
    // 16s reading directories and pasting on redis
        public
        void shouldLoadFileNamesUnderRedis()
            throws Exception {
        final Set<String> names = Files.listFileNamesFrom(FROM, false);
        final List<Callable<Void>> callables = newLinkedList();
        for (final String fileName: names) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call()
                    throws Exception {
                    final File file = new File(fileName);
                    if (file.isDirectory()) {
                        return null;
                    }
                    final FileInputStream fis = new FileInputStream(file);
                    final JRedisFactory factory = injector
                            .getInstance(JRedisFactory.class);
                    final ByteArrayOutputStream fos = new ByteArrayOutputStream();
                    IOUtils.copy(fis, fos);
                    factory.getFrom(RegularPartitions.FEDERATION).set(
                            fileName.replaceAll("[ ]", ""), fos.toByteArray());
                    return null;
                }
            });
        }
        final List<Future<Void>> futures = ExecutorInstance.INSTANCE
                .invokeAll(callables);
        for (final Future<Void> f: futures) {
            f.get();
        }
    }

    @Test
    // 30s (51s) (87s) to load 1/6 of the data
    // 190s for all data
        public
        void shouldLoadFileNamesUnderRedisUsingSimplePersist()
            throws Exception {
        final Set<String> names = Files.listFileNamesFrom(FROM, false);
        final List<Callable<Void>> callables = newLinkedList();
        final int sizeByThree = names.size() / 6;
        int i = 0;
        for (final String fileName: names) {
            if (i++ >= sizeByThree) {
                break;
            }
            callables.add(new Callable<Void>() {
                @Override
                public Void call()
                    throws Exception {
                    final File file = new File(fileName);
                    if (file.isDirectory()) {
                        return null;
                    }
                    final FileInputStream fis = new FileInputStream(file);
                    final StringArtifact artifact = Artifact
                            .createArtifact(StringArtifact.class, fileName,
                                    ChangeType.INCLUDED);
                    artifact.setMappedTo(fileName);
                    artifact.setMappedFrom(fileName);
                    artifact.setLastChange(System.currentTimeMillis());
                    artifact.setOriginalName(fileName);
                    final SimplePersistCapable<StorageNode, StorageSession> simplePersist = injector
                            .getInstance(SimplePersistFactory.class)
                            .createSimplePersist(RegularPartitions.FEDERATION);

                    final JRedisFactory factory = injector
                            .getInstance(JRedisFactory.class);
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(fis));
                    String line;
                    final List<String> content = newLinkedList();
                    while ((line = reader.readLine()) != null) {
                        content.add(line);
                    }
                    artifact.getContent().setTransient(content);
                    simplePersist.convertBeanToNode(artifact);
                    return null;
                }
            });
        }
        final List<Future<Void>> futures = ExecutorInstance.INSTANCE
                .invokeAll(callables);
        for (final Future<Void> f: futures) {
            f.get();
        }
    }

    @Test
    public void shouldLoadOneFileNamesUnderRedisUsingSimplePersist()
            throws Exception {
        final Set<String> names = Files.listFileNamesFrom(FROM, false);
        final String fileName = names.iterator().next();

        final File file = new File(fileName);
        final FileInputStream fis = new FileInputStream(file);
        final StringArtifact artifact = Artifact.createArtifact(StringArtifact.class,
                fileName, ChangeType.INCLUDED);
        artifact.setMappedTo(fileName);
        artifact.setMappedFrom(fileName);
        artifact.setLastChange(System.currentTimeMillis());
        artifact.setOriginalName(fileName);
        final SimplePersistCapable<StorageNode, StorageSession> simplePersist = injector
            .getInstance(SimplePersistFactory.class).createSimplePersist(
                        RegularPartitions.FEDERATION);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        String line;
        final List<String> content = newLinkedList();
        while ((line = reader.readLine()) != null) {
            content.add(line);
        }
        artifact.getContent().setTransient(content);
        simplePersist.convertBeanToNode(artifact);

    }

}
