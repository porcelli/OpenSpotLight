package org.openspotlight.bundle.language.java.bundle.test;

import org.openspotlight.bundle.common.AbstractTestServerClass;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.bundle.JavaBinaryProcessor;
import org.openspotlight.bundle.language.java.bundle.JavaBodyElementsPhase;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.language.java.bundle.JavaLexerAndParserTypesPhase;
import org.openspotlight.bundle.language.java.bundle.JavaParserPublicElementsPhase;
import org.openspotlight.common.util.Collections;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class JavaBundleTest extends AbstractTestServerClass {

	public static void main(final String... args) {
		final JavaBundleTest test = new JavaBundleTest();
		test.doWorkAndExposeServers();
	}

	private String repositoryName;
	private ExecutionContextFactory contextFactory;
	private GlobalSettings settings;
	private Group group;
	private final String username = "sa";
	private final String password = "sa";

	@Override
	protected void doWork(final JcrConnectionProvider provider)
	throws Exception {
		final Repository repo = new Repository();
		repo.setName("name");
		repo.setActive(true);
		repositoryName = repo.getName();
		contextFactory = DefaultExecutionContextFactory.createFactory();

		final ArtifactSource artifactSource = new ArtifactSource();
		repo.getArtifactSources().add(artifactSource);
		artifactSource.setRepository(repo);
		artifactSource.setName("junit 4.3.1 files");
		artifactSource.setActive(true);
		artifactSource.setInitialLookup("./src/test/resources/junit-4.3.1");
		final ArtifactSourceMapping jarMapping = new ArtifactSourceMapping();
		jarMapping.setSource(artifactSource);
		artifactSource.getMappings().add(jarMapping);
		jarMapping.setFrom("jar/");
		jarMapping.setTo("/");
		jarMapping.getIncludeds().add("**/*.jar");
		final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
		mapping.setSource(artifactSource);
		artifactSource.getMappings().add(mapping);
		mapping.setFrom("src/");
		mapping.setTo("src/");
		mapping.getIncludeds().add("**/*.java");

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
		jarProcessor.setGroup(group);
		jarProcessor.setGlobalPhase(JavaGlobalPhase.class);
		jarProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
		group.getBundleTypes().add(jarProcessor);

		final BundleSource jarSource = new BundleSource();
		jarProcessor.getSources().add(jarSource);
		jarSource.setBundleProcessorType(jarProcessor);
		jarSource.setRelative("/jar");
		jarSource.getIncludeds().add("/jar/luni-few-classes.jar");

		final BundleProcessorType commonProcessor = new BundleProcessorType();
		commonProcessor.getBundleProperties().put(JavaConstants.JAR_CLASSPATH,
		"/jar/luni-few-classes.jar");
		commonProcessor.setActive(true);
		commonProcessor.setName("source processor");
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
		bundleSource.setRelative("src/");
		bundleSource.getIncludeds().add("**/*.java");
		final ExecutionContext ctx = contextFactory.createExecutionContext(
				username, password, getDescriptor(), repositoryName);
		ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
		ctx.getDefaultConfigurationManager().saveRepository(repo);

		DefaultScheduler.INSTANCE.initializeSettings(contextFactory, "user",
				"password", getDescriptor());
		DefaultScheduler.INSTANCE
		.refreshJobs(settings, Collections.setOf(repo));
		DefaultScheduler.INSTANCE.startScheduler();

		DefaultScheduler.INSTANCE.fireSchedulable("username", "password",
				artifactSource);
		DefaultScheduler.INSTANCE
		.fireSchedulable("username", "password", group);

	}

	@Override
	protected JcrConnectionDescriptor getDescriptor() {
		return DefaultJcrDescriptor.TEMP_DESCRIPTOR;
	}

	@Override
	public String getExportedFileName() {
		return "target/test-data/JavaBundleTest/junit-4.3.1-exported.xml";
	}

	@Override
	protected boolean shutdownAtFinish() {
		return false;
	}

}
