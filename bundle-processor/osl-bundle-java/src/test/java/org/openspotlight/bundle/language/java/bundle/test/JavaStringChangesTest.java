package org.openspotlight.bundle.language.java.bundle.test;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.language.java.bundle.JavaLexerAndParserTypesPhase;
import org.openspotlight.bundle.language.java.bundle.JavaParserPublicElementsPhase;
import org.openspotlight.bundle.language.java.bundle.JavaTreePhase;
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
import org.openspotlight.federation.processing.DefaultBundleProcessorManager;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public class JavaStringChangesTest {

	private ExecutionContextFactory includedFilesContextFactory;
	private ExecutionContextFactory changedFilesContextFactory;
	private ExecutionContextFactory removedFilesContextFactory;
	private GlobalSettings settings;
	private Group group;

	private final String username = "username";

	private final String password = "password";
	private final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
	private String repositoryName;

	@Before
	public void setupResources() throws Exception {
		final Repository repo = new Repository();
		repo.setName("name");
		repo.setActive(true);
		repositoryName = repo.getName();
		final ArtifactSource includedSource = new ArtifactSource();
		includedSource.setRepository(repo);
		includedSource.setName("classpath");
		includedSource
				.setInitialLookup("./src/test/resources/stringArtifacts/new_file");
		includedFilesContextFactory = TestExecutionContextFactory
				.createFactory(ArtifactFinderType.LOCAL_SOURCE, includedSource);

		final ArtifactSource changedSource = new ArtifactSource();
		changedSource.setRepository(repo);
		changedSource.setName("classpath");
		changedSource
				.setInitialLookup("./src/test/resources/stringArtifacts/changed_file");
		changedFilesContextFactory = TestExecutionContextFactory.createFactory(
				ArtifactFinderType.LOCAL_SOURCE, changedSource);

		final ArtifactSource removedSource = new ArtifactSource();
		removedSource.setRepository(repo);
		removedSource.setName("classpath");
		removedSource
				.setInitialLookup("./src/test/resources/stringArtifacts/removed_file");
		removedFilesContextFactory = TestExecutionContextFactory.createFactory(
				ArtifactFinderType.LOCAL_SOURCE, removedSource);

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
		bundleSource.setRelative("tests/");
		bundleSource.getIncludeds().add("**/*.java");
		final ExecutionContext ctx = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
		ctx.getDefaultConfigurationManager().saveRepository(repo);
		ctx.closeResources();
	}

	@Ignore
	@Test
	public void shouldRemoveDeletedInnerClassWhenItIsRemovedFromFile()
			throws Exception {
		// FIXME write this test!
	}

	@Ignore
	@Test
	public void shouldRemoveDeletedPublicClassWhenItsFileIsRemoved()
			throws Exception {
		// FIXME write this test!
		DefaultBundleProcessorManager.INSTANCE.executeBundles(username,
				password, descriptor, includedFilesContextFactory, settings,
				group);
		final ExecutionContext context = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		final SLContext ctx = context.getGraphSession().getContext(
				SLConsts.DEFAULT_GROUP_CONTEXT);
		final SLNode groupNode = ctx.getRootNode().getNode(
				group.getUniqueName());
		final SLNode packageNode = groupNode.getNode("org.openspotlight.test");
		final SLNode classNode = packageNode.getNode("ExamplePublicClass");
		Assert.assertThat(classNode, Is.is(IsNull.notNullValue()));

	}
}
