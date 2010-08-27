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
import org.junit.Ignore;
import org.openspotlight.bundle.common.AbstractTestServerClass;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.*;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.*;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

@Ignore
public class JavaBundleTest extends AbstractTestServerClass {

    public static void main(final String... args) {
        final JavaBundleTest test = new JavaBundleTest();
        test.doWorkAndExposeServers();
    }

    private ExecutionContextFactory contextFactory;
    private GlobalSettings settings;
    private Group group;
    private final String username = "sa";
    private final String password = "sa";

    private SLGraph graph;

    @Override
    protected void doWork(final JcrConnectionProvider provider) throws Exception {
        final Repository repo = new Repository();
        repo.setName("name");
        repo.setActive(true);
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("name")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

        injector.getInstance(JRedisFactory.class).getFrom(SLPartition.GRAPH).flushall();
        graph = injector.getInstance(SLGraph.class);
        contextFactory = injector.getInstance(ExecutionContextFactory.class);

        final ArtifactSource artifactSource = new ArtifactSource();
        repo.getArtifactSources().add(artifactSource);
        artifactSource.setRepository(repo);
        artifactSource.setName("junit 4.3.1 files");
        artifactSource.setActive(true);
        artifactSource.setInitialLookup("./src/test/resources/junit-4.3.1");
        final ArtifactSourceMapping jarMapping = new ArtifactSourceMapping();
        jarMapping.setSource(artifactSource);
        artifactSource.getMappings().add(jarMapping);
        jarMapping.setFrom("jar/");
        jarMapping.setTo("/");
        jarMapping.getIncludeds().add("**/*.jar");
        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setFrom("src/");
        mapping.setTo("src/");
        mapping.getIncludeds().add("**/*.java");

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
        jarProcessor.setGroup(group);
        jarProcessor.setGlobalPhase(JavaGlobalPhase.class);
        jarProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
        group.getBundleTypes().add(jarProcessor);

        final BundleSource jarSource = new BundleSource();
        jarProcessor.getSources().add(jarSource);
        jarSource.setBundleProcessorType(jarProcessor);
        jarSource.setRelative("/jar");
        jarSource.getIncludeds().add("/jar/luni-few-classes.jar");

        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.getBundleProperties().put(JavaConstants.JAR_CLASSPATH, "/jar/luni-few-classes.jar");
        commonProcessor.setActive(true);
        commonProcessor.setName("source processor");
        commonProcessor.setGroup(group);
        commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
        commonProcessor.getArtifactPhases().add(JavaLexerAndParserTypesPhase.class);
        commonProcessor.getArtifactPhases().add(JavaParserPublicElementsPhase.class);
        commonProcessor.getArtifactPhases().add(JavaBodyElementsPhase.class);
        group.getBundleTypes().add(commonProcessor);

        final BundleSource bundleSource = new BundleSource();
        commonProcessor.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(commonProcessor);
        bundleSource.setRelative("src/");
        bundleSource.getIncludeds().add("**/*.java");
        final ExecutionContext ctx = contextFactory.createExecutionContext(username, password, getDescriptor(),
                group.getRootRepository());
        ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
        ctx.getDefaultConfigurationManager().saveRepository(repo);

        DefaultScheduler.INSTANCE.initializeSettings(contextFactory, "user", "password", getDescriptor());
        DefaultScheduler.INSTANCE.refreshJobs(settings, SLCollections.setOf(repo));
        DefaultScheduler.INSTANCE.startScheduler();

        DefaultScheduler.INSTANCE.fireSchedulable("username", "password", artifactSource);
        DefaultScheduler.INSTANCE.fireSchedulable("username", "password", group);

    }

    @Override
    protected SLGraph getGraph() {
        return graph;
    }

    @Override
    protected JcrConnectionDescriptor getDescriptor() {
        return DefaultJcrDescriptor.TEMP_DESCRIPTOR;
    }

    @Override
    public String getExportedFileName() {
        return "target/test-data/JavaBundleTest/junit-4.3.1-exported.xml";
    }

    @Override
    protected boolean shutdownAtFinish() {
        return false;
    }

}
