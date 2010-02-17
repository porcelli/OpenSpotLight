package org.openspotlight.bundle.language.java.bundle.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Ignore;
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
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.remote.server.UserAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class JavaStressExampleDataCreationTest {

	public static void main(final String... args) throws Exception {
		final JavaStressExampleDataCreationTest test = new JavaStressExampleDataCreationTest();
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

	Logger logger = LoggerFactory.getLogger(getClass());

	public void setupResourcesAndCreateData() throws Exception {
		JcrConnectionProvider.createFromData(descriptor)
				.closeRepositoryAndCleanResources();
		final javax.jcr.Repository repository = JcrConnectionProvider
				.createFromData(descriptor).getRepository();

		final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
		final RemoteRepository remote = saFactory
				.getRemoteRepository(repository);

		final Registry registry = LocateRegistry
				.createRegistry(Registry.REGISTRY_PORT);
		registry.bind("jackrabbit.repository", remote);

		RemoteGraphSessionServer server = null;
		server = new RemoteGraphSessionServer(new UserAuthenticator() {

			public boolean canConnect(final String userName,
					final String password, final String clientHost) {
				return true;
			}

			public boolean equals(final Object o) {
				return this.getClass().equals(o.getClass());
			}
		}, 7070, 60 * 1000 * 10L, descriptor);
		System.err.println("Server waiting connections on port 7070");

		final Repository repo = new Repository();
		repo.setName("name");
		repo.setActive(true);
		repositoryName = repo.getName();
		final ArtifactSource includedSource = new ArtifactSource();
		includedSource.setRepository(repo);
		includedSource.setName("classpath");
		includedSource
				.setInitialLookup("./src/test/resources/stringArtifacts/stressData");
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
		bundleJarSource.getIncludeds().add("**/*.jar");
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
				new FileOutputStream(
						"target/test-data/exported-stress-data.xml"));
		session.exportSystemView(node.getPath(), bufferedOutputStream, false,
				false);
		bufferedOutputStream.flush();
		bufferedOutputStream.close();

		final NodeIterator contexts = node.getNodes("name/contexts").nextNode()
				.getNodes();

		while (contexts.hasNext()) {
			final Node ctxNode = contexts.nextNode();
			final BufferedOutputStream o = new BufferedOutputStream(
					new FileOutputStream("target/test-data/exported-"
							+ ctxNode.getName() + ".xml"));
			logger.debug("exported-" + ctxNode.getName() + ".xml");
			session.exportSystemView(node.getPath(), o, false, false);
			bufferedOutputStream.flush();
			bufferedOutputStream.close();

		}

		System.err
				.println("Done test! Server is still waiting connections on port 7070");
		while (true) {
			Thread.sleep(5000);
		}

	}
}
