package org.openspotlight.bundle.test;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.openspotlight.bundle.context.ExecutionContextModule;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.Scheduler;
import org.openspotlight.bundle.scheduler.SchedulerModule;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.domain.Schedulable;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.loader.PersistentConfigurationManagerModule;
import org.openspotlight.graph.GraphModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 5, 2010 Time: 2:55:10 PM To change this template use File | Settings | File
 * Templates.
 */
public abstract class AbstractBundleTest {

    public abstract Repository createRepository();

    private Repository     repository;

    private Scheduler      scheduler;

    private Group          group;

    private ArtifactSource artifactSource;

    public Repository getRepository() {
        return repository;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Group getGroup() {
        return group;
    }

    public ArtifactSource getArtifactSource() {
        return artifactSource;
    }

    @Before
    public void setup()
        throws Exception {

        final Injector injector = createInjector();

        repository = createRepository();
        group = repository.getGroups().iterator().next();
        artifactSource = group.getArtifactSources().iterator().next();
        scheduler = injector.getInstance(Scheduler.class);

        ExampleExecutionHistory.resetData();

    }

    protected Injector createInjector() {
        final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap = newHashMap();
        //                    schedulableMap.put(Group.class, SampleGroupSchedulableCommand.class);
        final List<Class<? extends OriginArtifactLoader>> loaderRegistry = newArrayList();
        loaderRegistry.add(FileSystemOriginArtifactLoader.class);
        final Injector injector = Guice.createInjector(
                            new SchedulerModule(schedulableMap), new ExecutionContextModule(loaderRegistry),
                            new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                                    ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                            new PersistentConfigurationManagerModule(),
                            new SimplePersistModule(),
                            new GraphModule());

        return injector;
    }

}
