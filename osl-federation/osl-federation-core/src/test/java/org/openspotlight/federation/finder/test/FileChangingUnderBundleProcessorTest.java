package org.openspotlight.federation.finder.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.*;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.*;
import org.openspotlight.federation.domain.artifact.*;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.federation.processing.*;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;         

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * Created by User: feu - Date: May 24, 2010 - Time: 11:10:38 AM
 */
@Ignore
public class FileChangingUnderBundleProcessorTest {

    public static class FileChangingProcessor implements BundleProcessorGlobalPhase<StringArtifact> {

        public static List<ArtifactChanges<Artifact>> currentChanges = newLinkedList();

        @Override
        public void didFinishProcessing(ArtifactChanges<Artifact> changes, ExecutionContext context, CurrentProcessorContext currentContext) {

        }

        @Override
        public Set<Class<? extends StringArtifact>> getArtifactTypes() {
            return SLCollections.<Class<? extends StringArtifact>>setOf(StringArtifact.class);
        }

        @Override
        public SaveBehavior getSaveBehavior() {
            return SaveBehavior.PER_ARTIFACT;
        }

        @Override
        public void selectArtifactsToBeProcessed(CurrentProcessorContext currentContext, ExecutionContext context, ArtifactChanges<Artifact> changes, ArtifactsToBeProcessed<Artifact> toBeReturned) throws Exception {

        }
    }


    private static class RepositoryData {
        public final GlobalSettings settings;
        public final Repository repository;
        public final Group group;
        public final ArtifactSource artifactSource;

        public RepositoryData(
                final GlobalSettings settings, final Repository repository, final Group group,
                final ArtifactSource artifactSource) {
            this.settings = settings;
            this.repository = repository;
            this.group = group;
            this.artifactSource = artifactSource;
        }
    }

    private static ExecutionContextFactory contextFactory;
    private static RepositoryData data;
    private static DefaultScheduler scheduler;

    @AfterClass
    public static void closeResources() throws Exception {
        contextFactory.closeResources();
    }

    private static RepositoryData createRepositoryData() {
        final GlobalSettings settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(1000);

        GlobalSettingsSupport.initializeScheduleMap(settings);
        final Repository repository = new Repository();
        repository.setName("sampleRepository");
        repository.setActive(true);
        final Group group = new Group();
        group.setName("sampleGroup");
        group.setRepository(repository);
        repository.getGroups().add(group);
        group.setActive(true);
        final ArtifactSource artifactSource = new ArtifactSource();
        repository.getArtifactSources().add(artifactSource);
        artifactSource.setRepository(repository);
        artifactSource.setName("example files");
        artifactSource.setActive(true);
        artifactSource.setBinary(true);
        artifactSource.setInitialLookup("target/test-data/FileChangingUnderBundleProcessorTest");

        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setFrom("bundle-changing-resources");
        mapping.setTo("bundle-changing-resources");
        artifactSource.getMappings().add(mapping);
        mapping.getIncludeds().add("**");
        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.setActive(true);
        commonProcessor.setGroup(group);
        commonProcessor.setGlobalPhase(FileChangingProcessor.class);
        group.getBundleTypes().add(commonProcessor);

        final BundleSource bundleSource = new BundleSource();
        commonProcessor.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(commonProcessor);
        bundleSource.setRelative("/bundle-changing-resources/");
        bundleSource.getIncludeds().add("**");

        return new RepositoryData(settings, repository, group, artifactSource);
    }

    @BeforeClass
    public static void setupResources() throws Exception {
        delete("./target/test-data/FileChangingUnderBundleProcessorTest"); //$NON-NLS-1$

        JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

        injector.getInstance(JRedisFactory.class).getFrom(SLPartition.GRAPH).flushall();
        data = createRepositoryData();
        contextFactory = injector.getInstance(ExecutionContextFactory.class);

        final ExecutionContext context = contextFactory.createExecutionContext("username", "password",
                DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                data.repository);

        context.getDefaultConfigurationManager().saveGlobalSettings(data.settings);
        context.getDefaultConfigurationManager().saveRepository(data.repository);
        context.closeResources();

        scheduler = DefaultScheduler.INSTANCE;
        scheduler.initializeSettings(contextFactory, "user", "password", DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        scheduler.refreshJobs(data.settings, SLCollections.setOf(data.repository));
        scheduler.startScheduler();

    }

    @After
    public void closeTestResources() {
        contextFactory.closeResources();
    }

    private void reloadArtifactsAndCallBundleProcessor() {
        scheduler.fireSchedulable("username", "password", data.artifactSource);
        scheduler.fireSchedulable("username", "password", data.group);
    }

    @Test
    public void shouldStoreTheCorrectChanges() throws Exception {
        reloadArtifactsAndCallBundleProcessor();

        final ExecutionContext context = contextFactory.createExecutionContext("", "", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                data.repository);
        Set<String> list = context.getPersistentArtifactManager().getInternalMethods().retrieveNames(StreamArtifact.class, null);
        for (String s : list)
            System.err.println(s);
        final StreamArtifact jarArtifact = context.getPersistentArtifactManager().findByPath(StreamArtifact.class,
                "/jars/resources/dynamo-file-gen-1.0.1.jar");
        Assert.assertThat(jarArtifact.getLastProcessStatus(), Is.is(LastProcessStatus.PROCESSED));
        Assert.assertThat(jarArtifact.getUniqueContextName(), Is.is(IsNull.notNullValue()));
    }

    private static void storeOrChangeFile(String rootPath, String fileName, String... contentInLines){

    }

    private static void deleteFile(String rootPath, String fileName){
        
    }

    private static boolean assertSameContent(ChangeType changeType, ArtifactChanges<Artifact> changes, String fileName, String... contentInLines){
        return false;
    }


}
