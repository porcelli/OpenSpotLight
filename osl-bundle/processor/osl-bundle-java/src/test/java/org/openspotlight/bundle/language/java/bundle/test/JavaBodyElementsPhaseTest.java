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
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.bundle.common.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.*;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.bundle.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.bundle.processing.DefaultBundleProcessorManager;
import org.openspotlight.bundle.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.storage.StorageSessionimport org.openspotlight.storage.domain.RegularPartitionition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class JavaBodyElementsPhaseTest {

    public static void main(final String... args) throws Exception {
        final JavaBodyElementsPhaseTest test = new JavaBodyElementsPhaseTest();

        try {
            final javax.jcr.Repository repository = JcrConnectionProvider.createFromData(descriptor).getRepository();
            Injector injector = Guice.createInjector(new JRedisStorageModule(StStorageSessionlushMode.AUTO,
                    ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                    repositoryPath("repository")),
                    new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

            injector.getInstance(JRedisFactory.class).getRegularPartitionrPartition.GRAPH).flushall();

            SLGraph graph = injector.getInstance(SLGraph.class);


            final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
            final RemoteRepository remote = saFactory.getRemoteRepository(repository);

            final Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            registry.bind("jackrabbit.repository", remote);

            RemoteGraphSessionServer server = null;
            try {
                server = new RemoteGraphSessionServer(new UserAuthenticator() {

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
            } finally {
                if (server != null) {
                    server.shutdown();
                }
            }

            test.setupResources();
            try {
                test.shouldResoulveExpectedTokens();
            } catch (final Exception e) {
                e.printStackTrace();
            } catch (final AssertionError e) {
                e.printStackTrace();
            }
            System.err.println("Server is still waiting connections on port 7070");
            while (true) {
                Thread.sleep(5000);
            }

        } finally {
            test.closeResources();
        }
    }

    private ExecutionContextFactory includedFilesContextFactory;

    private GlobalSettings settings;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Group group;

    private final String username = "username";

    private final String password = "password";
    private static final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;

    @After
    public void closeResources() {
        final RepositoryImpl repo = (RepositoryImpl) JcrConnectionProvider.createFromData(descriptor).getRepository();
        repo.shutdown();
    }

    @Before
    public void setupResources() throws Exception {
        logger.info("starting test");
        JcrConnectionProvider.createFromData(descriptor).closeRepositoryAndCleanResources();
        final Repository repo = new Repository();
        repo.setName("OSL");
        repo.setActive(true);
        final ArtifactSource includedSource = new ArtifactSource();
        includedSource.setRepository(repo);
        includedSource.setName("junit");
        includedSource.setInitialLookup("src/test/resources/stringArtifacts/junit-4.3.1");

        Injector injector = Guice.createInjector(new JRedisStorageModule(StorStorageSessionshMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule());

        includedFilesContextFactory = injector.getInstance(ExecutionContextFactory.class);

        settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(1000);
        GlobalSettingsSupport.initializeScheduleMap(settings);
        group = new Group();
        group.setName("common");
        group.setRepository(repo);
        repo.getGroups().add(group);
        group.setActive(true);

        final BundleProcessorType jarProcessor = new BundleProcessorType();
        jarProcessor.setActive(true);
        jarProcessor.setName("jar-sources");
        jarProcessor.setGroup(group);
        jarProcessor.setGlobalPhase(JavaGlobalPhase.class);
        jarProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
        group.getBundleTypes().add(jarProcessor);
        final BundleSource jarSource = new BundleSource();
        jarProcessor.getSources().add(jarSource);
        jarSource.setBundleProcessorType(jarProcessor);
        jarSource.setRelative("/lib/");
        jarSource.getIncludeds().add("/lib/luni-few-classes.jar");

        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.getBundleProperties().put(JavaConstants.JAR_CLASSPATH, "/lib/luni-few-classes.jar");
        commonProcessor.setActive(true);
        commonProcessor.setName("common-sources");
        commonProcessor.setGroup(group);
        commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
        commonProcessor.getArtifactPhases().add(JavaLexerAndParserTypesPhase.class);
        commonProcessor.getArtifactPhases().add(JavaParserPublicElementsPhase.class);
        commonProcessor.getArtifactPhases().add(JavaBodyElementsPhase.class);
        group.getBundleTypes().add(commonProcessor);

        final BundleSource bundleSource = new BundleSource();
        commonProcessor.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(commonProcessor);
        bundleSource.setRelative("/src/");
        bundleSource.getIncludeds().add("/src/**/*.java");
        final ExecutionContext ctx = includedFilesContextFactory.createExecutionContext(username, password, descriptor,
                group.getRootRepository());
        ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
        ctx.getDefaultConfigurationManager().saveRepository(repo);
        ctx.closeResources();
    }

    @Test
    public void shouldResoulveExpectedTokens() throws Exception {
        logger.info("about to execute bundle");
        final GlobalExecutionStatus result = DefaultBundleProcessorManager.INSTANCE.executeBundles(username, password,
                descriptor,
                includedFilesContextFactory,
                settings, group);
        logger.info("bundle executed");
        Assert.assertThat(result, Is.is(GlobalExecutionStatus.SUCCESS));

        final ExecutionContext context = includedFilesContextFactory.createExecutionContext(username, password, descriptor,
                group.getRootRepository());
        final SLContext ctx = context.getGraphSession().getContext(SLConsts.DEFAULT_GROUP_CONTEXT);
        final Node groupNode = ctx.getRootNode().getNode(group.getUniqueName());

        final Node defaultPackageNode = groupNode.getNode(JavaConstants.DEFAULT_PACKAGE);
        final Node classNode = defaultPackageNode.getNode("ExampleClass");
        Assert.assertThat(classNode, Is.is(IsNull.notNullValue()));
        Assert.assertThat(classNode, Is.is(JavaTypeClass.class));
        final Node enumNode = defaultPackageNode.getNode("ExampleEnum");
        Assert.assertThat(enumNode, Is.is(IsNull.notNullValue()));
        Assert.assertThat(enumNode, Is.is(JavaTypeEnum.class));

        final Node examplePackageNode = groupNode.getNode("example.pack");
        final Node anotherClassNode = examplePackageNode.getNode("AnotherExampleClass");
        Assert.assertThat(anotherClassNode, Is.is(IsNull.notNullValue()));
        Assert.assertThat(anotherClassNode, Is.is(JavaTypeClass.class));
        final Node anotherInnerClassNode = anotherClassNode.getNode("InnerClass");
        Assert.assertThat(anotherInnerClassNode, Is.is(IsNull.notNullValue()));
        Assert.assertThat(anotherInnerClassNode, Is.is(JavaTypeClass.class));
        final Node anotherEnumNode = examplePackageNode.getNode("AnotherExampleEnum");
        Assert.assertThat(anotherEnumNode, Is.is(IsNull.notNullValue()));
        Assert.assertThat(anotherEnumNode, Is.is(JavaTypeEnum.class));
        final Node exampleSubPackageNode = groupNode.getNode("example.pack.subpack");
        final Node classOnConcrete = exampleSubPackageNode.getNode("ClassWithLotsOfStuff");
        Assert.assertThat(classOnConcrete, Is.is(IsNull.notNullValue()));
        Assert.assertThat(classOnConcrete, Is.is(JavaTypeClass.class));

        final AbstractTypeBind link = context.getGraphSession().getLink(AbstractTypeBind.class, classOnConcrete, null).iterator().next();
        final Node classOnAbstract = link.getTarget();
        System.err.println(" abstract " + classOnAbstract.getID());
        System.err.println(" concrete " + classOnConcrete.getID());
        System.err.println(" abstract " + classOnAbstract.getContext().getID());
        System.err.println(" concrete " + classOnConcrete.getContext().getID());
        final Node doSomethingMethodNode = classOnConcrete.getNode("doSomething()");

        final Set<Node> nodes = classOnAbstract.getNodes();
        for (final Node n : nodes) {
            System.err.println(n.getName());
        }

        Assert.assertThat(doSomethingMethodNode, Is.is(IsNull.notNullValue()));
        Assert.assertThat(doSomethingMethodNode, Is.is(JavaMethodMethod.class));

    }
}
