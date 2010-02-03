package org.openspotlight.bundle.language.java.bundle.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.jcr.Node;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.JavaBinaryProcessor;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
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
import org.openspotlight.federation.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;

public class JavaExampleDataCreationTest {

	public static void main(final String... args) throws Exception {
		final JavaExampleDataCreationTest test = new JavaExampleDataCreationTest();
		try {
			test.setupResourcesAndCreateData();
		} catch (final Exception e) {
			e.printStackTrace();
		} catch (final AssertionError e) {
			e.printStackTrace();
		} finally {
			JcrConnectionProvider.createFromData(descriptor)
					.closeRepositoryAndCleanResources();

		}

	}

	private ExecutionContextFactory includedFilesContextFactory;
	private GlobalSettings settings;

	private Group group;

	private final String username = "username";
	private final String password = "password";
	private static final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;

	private String repositoryName;

	@Before
	public void setupResourcesAndCreateData() throws Exception {
		JcrConnectionProvider.createFromData(descriptor)
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

		final BundleProcessorType jarProcessor = new BundleProcessorType();
		jarProcessor.setActive(true);
		jarProcessor.setName("jar processor");
		jarProcessor.setGroup(group);
		jarProcessor.setGlobalPhase(JavaGlobalPhase.class);
		jarProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
		group.getBundleTypes().add(jarProcessor);

		final BundleSource bundleJarSource = new BundleSource();
		jarProcessor.getSources().add(bundleJarSource);
		bundleJarSource.setBundleProcessorType(jarProcessor);
		bundleJarSource.setRelative("jar/");
		bundleJarSource.getIncludeds().add("**/luni-few-classes.jar");
		ExecutionContext ctx = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
		ctx.getDefaultConfigurationManager().saveRepository(repo);
		final GlobalExecutionStatus result = DefaultBundleProcessorManager.INSTANCE
				.executeBundles(username, password, descriptor,
						includedFilesContextFactory, settings, group);
		Assert.assertThat(result, Is.is(GlobalExecutionStatus.SUCCESS));
		ctx = includedFilesContextFactory.createExecutionContext(username,
				password, descriptor, repositoryName);
		final SLNode ctxRoot = ctx.getGraphSession().getContext(
				JavaConstants.ABSTRACT_CONTEXT).getRootNode();
		final SLNode objectNode = ctxRoot.getNode("java.lang")
				.getNode("Object");
		Assert.assertThat(objectNode, Is.is(IsNull.notNullValue()));

		final SessionWithLock session = ctx.getDefaultConnectionProvider()
				.openSession();
		final Node node = session.getRootNode().getNode(
				SLConsts.DEFAULT_JCR_ROOT_NAME);
		new File("target/test-data/").mkdirs();
		final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream("target/test-data/exportedData.xml"));
		session.exportSystemView(node.getPath(), bufferedOutputStream, false,
				false);
		bufferedOutputStream.flush();
		bufferedOutputStream.close();

	}

}
