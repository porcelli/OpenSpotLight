package org.openspotlight.federation.processing.internal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.LocalSourceArtifactFinderByRepositoryProviderFactory;
import org.openspotlight.federation.loader.ExampleBundleProcessor;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory;
import org.openspotlight.federation.processing.BundleProcessorManagerImpl;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class BundleProcessorManagerTest {

    private static DefaultJcrDescriptor                                 descriptor;
    private static DetailedJcrLoggerFactory                             loggerFactory;
    private static AuthenticatedUser                                    user;
    private static GlobalSettings                                       settings;
    private static BundleProcessorManagerImpl                           bundleProcessor;
    private static ArtifactSource                                       source;
    private static LocalSourceArtifactFinderByRepositoryProviderFactory artifactFinderFactory;

    @BeforeClass
    public static void setupResources() throws Exception {
        descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
        bundleProcessor = BundleProcessorManagerImpl.INSTANCE;
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        final SLGraph graph = factory.createGraph(descriptor);

        source = new ArtifactSource();
        source.setName("classpath");
        source.setInitialLookup("./src");

        final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        user = securityFactory.createIdentityManager(descriptor).authenticate(simpleUser, "password");
        settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(1000);
        settings.setNumberOfParallelThreads(4);
        loggerFactory = new DetailedJcrLoggerFactory(descriptor);
        artifactFinderFactory = new LocalSourceArtifactFinderByRepositoryProviderFactory(source, false);
    }

    @Test
    public void shouldProcessMappedArtifacts() throws Exception {
        final Repository repository = new Repository();
        repository.setActive(true);
        repository.setName("repository");
        final Group group = new Group();
        group.setActive(true);
        group.setName("Group name");
        group.setRepository(repository);
        repository.getGroups().add(group);
        final BundleProcessorType bundleType = new BundleProcessorType();
        bundleType.setActive(true);
        bundleType.setGroup(group);
        bundleType.setType(ExampleBundleProcessor.class);
        group.getBundleTypes().add(bundleType);
        final BundleSource bundleSource = new BundleSource();
        bundleType.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(bundleType);
        bundleSource.setRelative(source.getInitialLookup());
        bundleSource.getIncludeds().add("**/*.java");

        bundleProcessor.executeBundles(user, descriptor, settings, artifactFinderFactory, loggerFactory, repository);

    }

}
