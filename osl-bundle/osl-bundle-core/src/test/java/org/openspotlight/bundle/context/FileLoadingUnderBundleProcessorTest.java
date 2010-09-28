package org.openspotlight.bundle.context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jredis.JRedis;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.*;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * Created by User: feu - Date: May 24, 2010 - Time: 11:10:38 AM
 */
public class FileLoadingUnderBundleProcessorTest {

//    private static final String FROM_ROOT_PATH = "/Users/feu/much-data";
//    private static final String TO_FEDERATED = "files", FROM = TO_FEDERATED;

    private static AtomicInteger count = new AtomicInteger(0);

    private static final String FROM_ROOT_PATH = "./";
    private static final String TO_FEDERATED = "src", FROM = TO_FEDERATED;


    private static Injector injector;


    public static class FileChangingProcessor implements BundleProcessorGlobalPhase<StringArtifact>, BundleProcessorArtifactPhase<StringArtifact> {
        private AtomicInteger internalCount = new AtomicInteger(0);


        @Override
        public void didFinishProcessing(ArtifactChanges<Artifact> changes, ExecutionContext context, CurrentProcessorContext currentContext) {
            count.addAndGet(internalCount.get());
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

        @Override
        public void beforeProcessArtifact(StringArtifact artifact, CurrentProcessorContext currentContext, ExecutionContext context) {

        }

        @Override
        public void didFinishToProcessArtifact(StringArtifact artifact, LastProcessStatus status, CurrentProcessorContext currentContext, ExecutionContext context) {

        }

        @Override
        public Class<StringArtifact> getArtifactType() {
            return StringArtifact.class;
        }

        @Override
        public LastProcessStatus processArtifact(StringArtifact artifact, CurrentProcessorContext currentContext, ExecutionContext context) throws Exception {
            List<String> content = artifact.getContent().get(context.getPersistentArtifactManager().getSimplePersist());

            if (content != null && content.size() != 0) {
                internalCount.incrementAndGet();
            }
            return LastProcessStatus.PROCESSED;
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
        settings.setDefaultSleepingIntervalInMilliseconds(50);

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
        artifactSource.setBinary(false);
        artifactSource.setInitialLookup(FROM_ROOT_PATH);

        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setFrom(FROM);
        mapping.setTo(TO_FEDERATED);
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
        bundleSource.setRelative(TO_FEDERATED);
        bundleSource.getIncludeds().add("**");

        return new RepositoryData(settings, repository, group, artifactSource);
    }

    @Before
    public void setupResources() throws Exception {

        count.set(0);

        JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();
        injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));
        JRedis jredis = injector.getInstance(JRedisFactory.class).getFrom(SLPartition.FEDERATION);
        jredis.flushall();
        jredis.save();
        if (jredis.dbsize() > 0) throw new Exception("database not cleaned");


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

    private void reloadArtifactsAndCallBundleProcessor() throws Exception {
        scheduler.fireSchedulable("username", "password", data.artifactSource);
        scheduler.fireSchedulable("username", "password", data.group);

    }

    @Test
    public void shouldStoreTheCorrectChanges() throws Exception {
        reloadArtifactsAndCallBundleProcessor();
        assertThat(count.get() > 50, is(true));
    }


}