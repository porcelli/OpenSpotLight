package org.openspotlight.federation.processing.internal;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.LocalSourceArtifactFinderByRepositoryProviderFactory;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory;
import org.openspotlight.federation.processing.BundleProcessorManagerImpl;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class BundleProcessorManagerTest {

	private static DefaultJcrDescriptor descriptor;
	private static DetailedJcrLoggerFactory loggerFactory;
	private static AuthenticatedUser user;
	private static GlobalSettings settings;
	private static BundleProcessorManagerImpl bundleProcessor;
	private static ArtifactSource source;
	private static LocalSourceArtifactFinderByRepositoryProviderFactory artifactFinderFactory;

	@BeforeClass
	public static void setupResources() throws Exception {

		ExampleBundleProcessor.allStatus.clear();
		BundleProcessorManagerTest.descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
		BundleProcessorManagerTest.bundleProcessor = BundleProcessorManagerImpl.INSTANCE;

		BundleProcessorManagerTest.source = new ArtifactSource();
		BundleProcessorManagerTest.source.setName("classpath");
		BundleProcessorManagerTest.source
				.setInitialLookup("../../../OpenSpotLight");

		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		BundleProcessorManagerTest.user = securityFactory
				.createIdentityManager(BundleProcessorManagerTest.descriptor)
				.authenticate(simpleUser, "password");
		BundleProcessorManagerTest.settings = new GlobalSettings();
		BundleProcessorManagerTest.settings
				.setDefaultSleepingIntervalInMilliseconds(1000);
		BundleProcessorManagerTest.settings.setNumberOfParallelThreads(8);
		BundleProcessorManagerTest.loggerFactory = new DetailedJcrLoggerFactory(
				BundleProcessorManagerTest.descriptor);
		BundleProcessorManagerTest.artifactFinderFactory = new LocalSourceArtifactFinderByRepositoryProviderFactory(
				BundleProcessorManagerTest.source, false);
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
		bundleSource.setRelative("/osl-federation");
		bundleSource.getIncludeds().add("**/*.java");

		BundleProcessorManagerTest.bundleProcessor.executeBundles(
				BundleProcessorManagerTest.user,
				BundleProcessorManagerTest.descriptor,
				BundleProcessorManagerTest.settings,
				BundleProcessorManagerTest.artifactFinderFactory,
				BundleProcessorManagerTest.loggerFactory, repository);
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.ERROR), Is.is(false));
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
				.is(false));
	}

}
