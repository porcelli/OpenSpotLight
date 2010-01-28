package org.openspotlight.bundle.language.java.bundle.test;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.language.java.bundle.JavaLexerAndParserTypesPhase;
import org.openspotlight.bundle.language.java.bundle.JavaParserPublicElementsPhase;
import org.openspotlight.bundle.language.java.bundle.JavaTreePhase;
import org.openspotlight.bundle.language.java.bundle.test.JavaStringArtifactProcessingTest.SampleJavaArtifactRegistry;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.context.TestExecutionContextFactory;
import org.openspotlight.federation.context.TestExecutionContextFactory.ArtifactFinderType;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.processing.DefaultBundleProcessorManager;
import org.openspotlight.federation.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.federation.processing.internal.ExampleBundleProcessor;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

@Ignore
public class JavaPublicElementsPhaseTest {

	private ExecutionContextFactory includedFilesContextFactory;
	private GlobalSettings settings;
	private Group group;

	// FIXME test new parser stuff

	private final String username = "username";

	private final String password = "password";
	private final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
	private String repositoryName;

	@Before
	public void setupResources() throws Exception {

		JcrConnectionProvider.createFromData(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR)
				.closeRepositoryAndCleanResources();

		final Repository repo = new Repository();
		repo.setName("name");
		repo.setActive(true);
		repositoryName = repo.getName();
		final ArtifactSource includedSource = new ArtifactSource();
		includedSource.setRepository(repo);
		includedSource.setName("classpath");
		includedSource
				.setInitialLookup("./src/test/resources/stringArtifacts/exampleFiles");
		includedFilesContextFactory = TestExecutionContextFactory
				.createFactory(ArtifactFinderType.LOCAL_SOURCE, includedSource);

		settings = new GlobalSettings();
		settings.setDefaultSleepingIntervalInMilliseconds(1000);
		settings.setNumberOfParallelThreads(1);
		settings
				.setArtifactFinderRegistryClass(SampleJavaArtifactRegistry.class);
		GlobalSettingsSupport.initializeScheduleMap(settings);
		group = new Group();
		group.setName("sampleGroup");
		group.setRepository(repo);
		repo.getGroups().add(group);
		group.setActive(true);

		final BundleProcessorType commonProcessor = new BundleProcessorType();
		commonProcessor.setActive(true);
		commonProcessor.setGroup(group);
		commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
		commonProcessor.getArtifactPhases().add(
				JavaLexerAndParserTypesPhase.class);
		commonProcessor.getArtifactPhases().add(
				JavaParserPublicElementsPhase.class);
		commonProcessor.getArtifactPhases().add(JavaTreePhase.class);
		group.getBundleTypes().add(commonProcessor);

		final BundleSource bundleSource = new BundleSource();
		commonProcessor.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(commonProcessor);
		bundleSource.setRelative("src/");
		bundleSource.getIncludeds().add("**/*.java");
		final ExecutionContext ctx = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
		ctx.getDefaultConfigurationManager().saveRepository(repo);
		ctx.closeResources();
	}

	@Test
	public void shouldResoulveExpectedTokens() throws Exception {
		final GlobalExecutionStatus result = DefaultBundleProcessorManager.INSTANCE
				.executeBundles(username, password, descriptor,
						includedFilesContextFactory, settings, group);
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.ERROR), Is.is(false));
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
				.is(false));
		Assert.assertThat(result, Is.is(GlobalExecutionStatus.SUCCESS));

		final ExecutionContext context = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		final SLContext ctx = context.getGraphSession().getContext(
				SLConsts.DEFAULT_GROUP_CONTEXT);
		final SLNode groupNode = ctx.getRootNode().getNode(
				group.getUniqueName());

		final SLNode packageNode = groupNode
				.getNode(JavaConstants.DEFAULT_PACKAGE);
		final SLNode classNode = packageNode.getNode("ClassOnDefaultPackage");
		Assert.assertThat(classNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(classNode, Is.is(JavaTypeClass.class));

	}
}
