package org.openspotlight.bundle.language.java.bundle.test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.JavaBinaryProcessor;
import org.openspotlight.bundle.language.java.bundle.JavaBodyElementsPhase;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.language.java.bundle.JavaLexerAndParserTypesPhase;
import org.openspotlight.bundle.language.java.bundle.JavaParserPublicElementsPhase;
import org.openspotlight.bundle.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.common.concurrent.NeedsSyncronizationSet;
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
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.remote.server.UserAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaBodyElementsPhaseTest {

	public static void main(final String... args) throws Exception {
		final JavaBodyElementsPhaseTest test = new JavaBodyElementsPhaseTest();

		try {
			final javax.jcr.Repository repository = JcrConnectionProvider
					.createFromData(descriptor).getRepository();

			final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
			final RemoteRepository remote = saFactory
					.getRemoteRepository(repository);

			final Registry registry = LocateRegistry
					.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("jackrabbit.repository", remote);

			RemoteGraphSessionServer server = null;
			try {
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
			} finally {
				if (server != null) {
					server.shutdown();
				}
			}

			test.setupResources();
			try {
				test.shouldResoulveExpectedTokens();
			} catch (final Exception e) {
				e.printStackTrace();
			} catch (final AssertionError e) {
				e.printStackTrace();
			}
			System.err
					.println("Server is still waiting connections on port 7070");
			while (true) {
				Thread.sleep(5000);
			}

		} finally {
			test.closeResources();
		}
	}

	private ExecutionContextFactory includedFilesContextFactory;

	private GlobalSettings settings;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Group group;

	private final String username = "username";

	private final String password = "password";
	private static final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
	private String repositoryName;

	@After
	public void closeResources() {
		final RepositoryImpl repo = (RepositoryImpl) JcrConnectionProvider
				.createFromData(descriptor).getRepository();
		repo.shutdown();
	}

	@Before
	public void setupResources() throws Exception {
		logger.info("starting test");
		JcrConnectionProvider.createFromData(descriptor)
				.closeRepositoryAndCleanResources();
		final Repository repo = new Repository();
		repo.setName("OSL");
		repo.setActive(true);
		repositoryName = repo.getName();
		final ArtifactSource includedSource = new ArtifactSource();
		includedSource.setRepository(repo);
		includedSource.setName("junit");
		includedSource
				.setInitialLookup("src/test/resources/stringArtifacts/junit-4.3.1");
		includedFilesContextFactory = TestExecutionContextFactory
				.createFactory(ArtifactFinderType.FILESYSTEM, includedSource);

		settings = new GlobalSettings();
		settings.setDefaultSleepingIntervalInMilliseconds(1000);
		settings.setNumberOfParallelThreads(1);
		settings
				.setArtifactFinderRegistryClass(SampleJavaArtifactRegistry.class);
		GlobalSettingsSupport.initializeScheduleMap(settings);
		group = new Group();
		group.setName("common");
		group.setRepository(repo);
		repo.getGroups().add(group);
		group.setActive(true);

		final BundleProcessorType jarProcessor = new BundleProcessorType();
		jarProcessor.setActive(true);
		jarProcessor.setName("jar-sources");
		jarProcessor.setGroup(group);
		jarProcessor.setGlobalPhase(JavaGlobalPhase.class);
		jarProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
		group.getBundleTypes().add(jarProcessor);
		final BundleSource jarSource = new BundleSource();
		jarProcessor.getSources().add(jarSource);
		jarSource.setBundleProcessorType(jarProcessor);
		jarSource.setRelative("/lib/");
		jarSource.getIncludeds().add("/lib/luni-few-classes.jar");

		final BundleProcessorType commonProcessor = new BundleProcessorType();
		commonProcessor.getBundleProperties().put(JavaConstants.JAR_CLASSPATH,
				"/lib/luni-few-classes.jar");
		commonProcessor.setActive(true);
		commonProcessor.setName("common-sources");
		commonProcessor.setGroup(group);
		commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
		commonProcessor.getArtifactPhases().add(
				JavaLexerAndParserTypesPhase.class);
		commonProcessor.getArtifactPhases().add(
				JavaParserPublicElementsPhase.class);
		commonProcessor.getArtifactPhases().add(JavaBodyElementsPhase.class);
		group.getBundleTypes().add(commonProcessor);

		final BundleSource bundleSource = new BundleSource();
		commonProcessor.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(commonProcessor);
		bundleSource.setRelative("/src/");
		bundleSource.getIncludeds().add("/src/**/*.java");
		final ExecutionContext ctx = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
		ctx.getDefaultConfigurationManager().saveRepository(repo);
		ctx.closeResources();
	}

	@Test
	public void shouldResoulveExpectedTokens() throws Exception {
		logger.info("about to execute bundle");
		final GlobalExecutionStatus result = DefaultBundleProcessorManager.INSTANCE
				.executeBundles(username, password, descriptor,
						includedFilesContextFactory, settings, group);
		logger.info("bundle executed");
		Assert.assertThat(result, Is.is(GlobalExecutionStatus.SUCCESS));

		final ExecutionContext context = includedFilesContextFactory
				.createExecutionContext(username, password, descriptor,
						repositoryName);
		final SLContext ctx = context.getGraphSession().getContext(
				SLConsts.DEFAULT_GROUP_CONTEXT);
		final SLNode groupNode = ctx.getRootNode().getNode(
				group.getUniqueName());

		final SLNode defaultPackageNode = groupNode
				.getNode(JavaConstants.DEFAULT_PACKAGE);
		final SLNode classNode = defaultPackageNode.getNode("ExampleClass");
		Assert.assertThat(classNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(classNode, Is.is(JavaTypeClass.class));
		final SLNode enumNode = defaultPackageNode.getNode("ExampleEnum");
		Assert.assertThat(enumNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(enumNode, Is.is(JavaTypeEnum.class));

		final SLNode examplePackageNode = groupNode.getNode("example.pack");
		final SLNode anotherClassNode = examplePackageNode
				.getNode("AnotherExampleClass");
		Assert.assertThat(anotherClassNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(anotherClassNode, Is.is(JavaTypeClass.class));
		final SLNode anotherInnerClassNode = anotherClassNode
				.getNode("InnerClass");
		Assert.assertThat(anotherInnerClassNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(anotherInnerClassNode, Is.is(JavaTypeClass.class));
		final SLNode anotherEnumNode = examplePackageNode
				.getNode("AnotherExampleEnum");
		Assert.assertThat(anotherEnumNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(anotherEnumNode, Is.is(JavaTypeEnum.class));
		final SLNode exampleSubPackageNode = groupNode
				.getNode("example.pack.subpack");
		final SLNode classOnConcrete = exampleSubPackageNode
				.getNode("ClassWithLotsOfStuff");
		Assert.assertThat(classOnConcrete, Is.is(IsNull.notNullValue()));
		Assert.assertThat(classOnConcrete, Is.is(JavaTypeClass.class));

		final AbstractTypeBind link = context.getGraphSession().getLinks(
				AbstractTypeBind.class, classOnConcrete, null).iterator()
				.next();
		final SLNode classOnAbstract = link.getTarget();
		System.err.println(" abstract " + classOnAbstract.getID());
		System.err.println(" concrete " + classOnConcrete.getID());
		System.err.println(" abstract " + classOnAbstract.getContext().getID());
		System.err.println(" concrete " + classOnConcrete.getContext().getID());
		final SLNode doSomethingMethodNode = classOnConcrete
				.getNode("doSomething()");

		final NeedsSyncronizationSet<SLNode> nodes = classOnAbstract.getNodes();
		for (final SLNode n : nodes) {
			System.err.println(n.getName());
		}

		Assert.assertThat(doSomethingMethodNode, Is.is(IsNull.notNullValue()));
		Assert.assertThat(doSomethingMethodNode, Is.is(JavaMethodMethod.class));

	}
}
