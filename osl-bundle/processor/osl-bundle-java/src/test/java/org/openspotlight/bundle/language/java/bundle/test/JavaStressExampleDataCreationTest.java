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
package org.openspotlight.bundle.language.java.bundle.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.JavaBinaryProcessor;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.bundle.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.bundle.processing.DefaultBundleProcessorManager;
import org.openspotlight.bundle.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.storage.StorageSessionimport org.openspotlight.storage.domain.RegularPartitionition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

@Ignore
public class JavaStressExampleDataCreationTest {

    public static void main(final String... args) throws Exception {
        final JavaStressExampleDataCreationTest test = new JavaStressExampleDataCreationTest();
        try {
            test.setupResourcesAndCreateData();
        } catch (final Exception e) {
            e.printStackTrace();
        } catch (final AssertionError e) {
            e.printStackTrace();
        } finally {
            JcrConnectionProvider.createFromData(descriptor).closeRepositoryAndCleanResources();

        }

    }

    private ExecutionContextFactory includedFilesContextFactory;
    private GlobalSettings settings;

    private Group group;

    private final String username = "username";
    private final String password = "password";
    private static final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;

    Logger logger = LoggerFactory.getLogger(getClass());

    public void setupResourcesAndCreateData() throws Exception {
        Injector injector = Guice.createInjector(new JRedisStorageModule(StStorageSessionlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

        injector.getInstance(JRedisFactory.class).getRegularPartitionrPartition.GRAPH).flushall();

        SLGraph graph = injector.getInstance(SLGraph.class);


        JcrConnectionProvider.createFromData(descriptor).closeRepositoryAndCleanResources();
        new RemoteGraphSessionServer(new UserAuthenticator() {

            public boolean canConnect(final String userName,
                                      final String password,
                                      final String clientHost) {
                return true;
            }

            public boolean equals(final Object o) {
                return this.getClass().equals(o.getClass());
            }
        }, 7070, 60 * 1000 * 10L, descriptor, graph);
        System.err.println("Server waiting connections on port 7070");

        final Repository repo = new Repository();
        repo.setName("name");
        repo.setActive(true);
        final ArtifactSource includedSource = new ArtifactSource();
        includedSource.setRepository(repo);
        includedSource.setName("classpath");
        includedSource.setInitialLookup("./src/test/resources/stringArtifacts/stressData");

        includedFilesContextFactory = injector.getInstance(ExecutionContextFactory.class);

        settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(1000);
        GlobalSettingsSupport.initializeScheduleMap(settings);
        group = new Group();
        group.setName("sampleGroup");
        group.setRepository(repo);
        repo.getGroups().add(group);
        group.setActive(true);

        final BundleProcessorType jarProcessor = new BundleProcessorType();
        jarProcessor.setActive(true);
        jarProcessor.setName("jar processor");
        jarProcessor.setGroup(group);
        jarProcessor.setGlobalPhase(JavaGlobalPhase.class);
        jarProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
        group.getBundleTypes().add(jarProcessor);

        final BundleSource bundleJarSource = new BundleSource();
        jarProcessor.getSources().add(bundleJarSource);
        bundleJarSource.setBundleProcessorType(jarProcessor);
        bundleJarSource.setRelative("jar/");
        bundleJarSource.getIncludeds().add("**/*.jar");
        ExecutionContext ctx = includedFilesContextFactory.createExecutionContext(username, password, descriptor,
                group.getRootRepository());
        ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
        ctx.getDefaultConfigurationManager().saveRepository(repo);
        final GlobalExecutionStatus result = DefaultBundleProcessorManager.INSTANCE.executeBundles(username, password,
                descriptor,
                includedFilesContextFactory,
                settings, group);
        Assert.assertThat(result, Is.is(GlobalExecutionStatus.SUCCESS));
        ctx = includedFilesContextFactory.createExecutionContext(username, password, descriptor, group.getRootRepository());
        final SLNode ctxRoot = ctx.getGraphSession().getContext(JavaConstants.ABSTRACT_CONTEXT).getRootNode();
        final SLNode objectNode = ctxRoot.getNode("java.lang").getNode("Object");
        Assert.assertThat(objectNode, Is.is(IsNull.notNullValue()));

        final SessionWithLock session = ctx.getDefaultConnectionProvider().openSession();
        final Node node = session.getRootNode().getNode(SLConsts.DEFAULT_JCR_ROOT_NAME);
        new File("target/test-data/").mkdirs();
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                new FileOutputStream(
                        "target/test-data/exported-stress-data.xml"));
        session.exportSystemView(node.getPath(), bufferedOutputStream, false, false);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        final NodeIterator contexts = node.getNodes("name/contexts").nextNode().getNodes();

        while (contexts.hasNext()) {
            final Node ctxNode = contexts.nextNode();
            final BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream("target/test-data/exported-"
                    + ctxNode.getName() + ".xml"));
            logger.debug("exported-" + ctxNode.getName() + ".xml");
            session.exportSystemView(node.getPath(), o, false, false);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        }

        System.err.println("Done test! Server is still waiting connections on port 7070");
        while (true) {
            Thread.sleep(5000);
        }

    }
}
