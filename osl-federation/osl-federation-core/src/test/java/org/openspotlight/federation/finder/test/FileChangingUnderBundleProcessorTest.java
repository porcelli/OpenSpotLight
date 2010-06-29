package org.openspotlight.federation.finder.test;

import com.google.common.collect.ImmutableList;
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
import org.openspotlight.federation.domain.artifact.ChangeType;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newLinkedList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.common.util.Strings.concatPaths;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * Created by User: feu - Date: May 24, 2010 - Time: 11:10:38 AM
 */
public class FileChangingUnderBundleProcessorTest {

    private static final String FROM_ROOT_PATH = "./target/test-data/FileChangingUnderBundleProcessorTest";
    private static final String TO_FEDERATED = "bundle-changing-resources", FROM = TO_FEDERATED;
    private static final String FROM_COMPLETE_ROOT_PATH = concatPaths(FROM_ROOT_PATH, FROM);
    private static Injector injector;


    private static class Change {
        @Override
        public String toString() {
            return "Change{" +
                    "changeType=" + changeType +
                    ", artifactNameWithPath='" + artifactNameWithPath + '\'' +
                    '}';
        }

        private final ChangeType changeType;
        private final String artifactName;
        private final String artifactNameWithPath;
        private final String[] content;

        private Change(StringArtifact artifact) {
            this.changeType = artifact.getChangeType();
            this.artifactName = artifact.getArtifactName();
            this.artifactNameWithPath = artifact.getArtifactCompleteName();
            this.content = artifact.getContent().get(null).toArray(new String[]{});

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Change change = (Change) o;

            if (artifactName != null ? !artifactName.equals(change.artifactName) : change.artifactName != null)
                return false;
            if (artifactNameWithPath != null ? !artifactNameWithPath.equals(change.artifactNameWithPath) : change.artifactNameWithPath != null)
                return false;
            if (changeType != change.changeType) return false;
            if (!Arrays.equals(content, change.content)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = changeType != null ? changeType.hashCode() : 0;
            result = 31 * result + (artifactName != null ? artifactName.hashCode() : 0);
            result = 31 * result + (artifactNameWithPath != null ? artifactNameWithPath.hashCode() : 0);
            result = 31 * result + (content != null ? Arrays.hashCode(content) : 0);
            return result;
        }
    }


    private static class GroupedChanges {
        @Override
        public String toString() {
            return "GroupedChanges{" +
                    "included=" + included +
                    ", excluded=" + excluded +
                    ", changed=" + changed +
                    ", notChanged=" + notChanged +
                    '}';
        }

        static List<Change> convert(Set<Artifact> artifacts) {
            ImmutableList.Builder<Change> builder = ImmutableList.builder();
            for (Artifact a : artifacts) {
                builder.add(new Change((StringArtifact) a));
            }
            return builder.build();
        }

        private final List<Change> included;
        private final List<Change> excluded;
        private final List<Change> changed;
        private final List<Change> notChanged;

        private GroupedChanges(ArtifactChanges changes) {
            this.included = convert(changes.getIncludedArtifacts());
            this.excluded = convert(changes.getExcludedArtifacts());
            this.changed = convert(changes.getChangedArtifacts());
            this.notChanged = convert(changes.getNotChangedArtifacts());
        }

        public List<Change> getIncluded() {
            return included;
        }

        public List<Change> getExcluded() {
            return excluded;
        }

        public List<Change> getChanged() {
            return changed;
        }

        public List<Change> getNotChanged() {
            return notChanged;
        }
    }

    public static class FileChangingProcessor implements BundleProcessorGlobalPhase<StringArtifact> {

        public static Map<Integer, GroupedChanges> currentChanges = new ConcurrentHashMap<Integer, GroupedChanges>();

        public static AtomicInteger count = new AtomicInteger(0);

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
            List<Artifact> allChanges = newLinkedList();
            allChanges.addAll(changes.getIncludedArtifacts());
            allChanges.addAll(changes.getNotChangedArtifacts());
            allChanges.addAll(changes.getExcludedArtifacts());
            allChanges.addAll(changes.getChangedArtifacts());
            for (Artifact a : allChanges) {
                StringArtifact sa = (StringArtifact) a;
                List<String> content = sa.getContent().get(context.getPersistentArtifactManager().getSimplePersist());
                sa.getContent().setTransient(content);

            }
            currentChanges.put(count.get(), new GroupedChanges(changes));
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

        FileChangingProcessor.count.set(0);
        FileChangingProcessor.currentChanges.clear();
        delete(FROM_ROOT_PATH); //$NON-NLS-1$

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
        FileChangingProcessor.count.incrementAndGet();

    }

    @Test
    public void shouldStoreTheCorrectChanges() throws Exception {
        int size = 10;
        for (int i = 0; i < size; i++) {
            storeOrChangeFile(FROM_COMPLETE_ROOT_PATH, "included_on_1st_running/" + i, Integer.toString(i), "2", "3");
        }
        reloadArtifactsAndCallBundleProcessor();
        reloadArtifactsAndCallBundleProcessor();
        for (int i = 0; i < size; i++) {
            storeOrChangeFile(FROM_COMPLETE_ROOT_PATH, "included_on_1st_running/" + i, Integer.toString(i), "2", "4");
        }

        reloadArtifactsAndCallBundleProcessor();
        for (int i = 0; i < size; i++) {
            deleteFile(FROM_COMPLETE_ROOT_PATH, "included_on_1st_running/" + i);
        }

        reloadArtifactsAndCallBundleProcessor();
        reloadArtifactsAndCallBundleProcessor();
        assertThat("got the 0 processing ", FileChangingProcessor.currentChanges.containsKey(0), is(true));
        assertThat("got the 1 processing ", FileChangingProcessor.currentChanges.containsKey(1), is(true));
        assertThat("got the 2 processing ", FileChangingProcessor.currentChanges.containsKey(2), is(true));
        assertThat("got the 3 processing ", FileChangingProcessor.currentChanges.containsKey(3), is(true));
        assertThat("got the 4 processing ", FileChangingProcessor.currentChanges.containsKey(4), is(true));
        for (int i = 0; i < size; i++) {
            assertThat("change type is included", isSameContent(ChangeType.INCLUDED, FileChangingProcessor.currentChanges.get(0), TO_FEDERATED, "included_on_1st_running/" + i, Integer.toString(i), "2", "3"), is(true));
            assertThat("change type is not changed", isSameContent(ChangeType.NOT_CHANGED, FileChangingProcessor.currentChanges.get(1), TO_FEDERATED, "included_on_1st_running/" + i, Integer.toString(i), "2", "3"), is(true));
            assertThat("change type is changed", isSameContent(ChangeType.CHANGED, FileChangingProcessor.currentChanges.get(2), TO_FEDERATED, "included_on_1st_running/" + i, Integer.toString(i), "2", "4"), is(true));
            assertThat("change type is excluded", isSameContent(ChangeType.EXCLUDED, FileChangingProcessor.currentChanges.get(3), TO_FEDERATED, "included_on_1st_running/" + i, Integer.toString(i), "2", "4"), is(true));

        }
        assertThat("there's no one included on last processing", FileChangingProcessor.currentChanges.get(4).getIncluded().size(), is(0));
        assertThat("there's no one excluded on last processing", FileChangingProcessor.currentChanges.get(4).getExcluded().size(), is(0));
        assertThat("there's no one not changed on last processing", FileChangingProcessor.currentChanges.get(4).getNotChanged().size(), is(0));
        assertThat("there's no one changed on last processing", FileChangingProcessor.currentChanges.get(4).getChanged().size(), is(0));


    }

    private static final Map<String,File> oppenedFiles = new HashMap<String,File>();

    private static void storeOrChangeFile(String rootPath, String relativeFileName, String... contentInLines) throws Exception {

        String fileName = concatPaths(rootPath, relativeFileName);
        File file = oppenedFiles.get(fileName);
        if(file==null){
        String dirName = fileName.substring(0, fileName.lastIndexOf('/'));
        new File(dirName).mkdirs();
        file = new File(fileName);
        oppenedFiles.put(fileName,file);
        }

            FileWriter writer = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (String s : contentInLines) {
            bufferedWriter.write(s);
            if (!s.endsWith("\n")) bufferedWriter.write('\n');
        }
        bufferedWriter.flush();
        bufferedWriter.close();


    }

    private static void deleteFile(String rootPath, String fileName) {
        File file = oppenedFiles.get(concatPaths(rootPath, fileName));

        if (!file.delete())
            throw new IllegalStateException("can't delete file " + concatPaths(rootPath, fileName));
    }

    private static boolean isSameContent(ChangeType changeType, GroupedChanges changes, String root, String relativeFileName, String... contentInLines) {
        String fileName = concatPaths("/", root, relativeFileName);
        List<Change> foundSet = null;
        switch (changeType) {
            case INCLUDED:
                foundSet = changes.getIncluded();
                break;
            case NOT_CHANGED:
                foundSet = changes.getNotChanged();
                break;
            case EXCLUDED:
                foundSet = changes.getExcluded();
                break;
            case CHANGED:
                foundSet = changes.getChanged();
                break;
        }
        if (foundSet.size() == 0)
            throw new RuntimeException("Any item found for " + changeType + "=" + relativeFileName + " on " + changes);
        for (Change a : foundSet) {
            if (a.artifactNameWithPath.equals(fileName)) {
                for (int i = 0, size = contentInLines.length; i < size; i++) {
                    if (!contentInLines[i].equals(a.content[i]))
                        throw new IllegalStateException(" line " + i + " from " + fileName + " is " + a.content[i] + " and not " + contentInLines[i]);
                }
                return true;
            }
        }
        return false;
    }


}
