package org.openspotlight.bundle.db.processor;

import static org.openspotlight.common.util.Files.delete;

import java.sql.Connection;
import java.util.Set;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.util.Collections;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.ArtifactFinderRegistry;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.db.DatabaseSupport;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;

public class DbTableArtifactBundleProcessorTest {

	public static class SampleDbArtifactRegistry implements
			ArtifactFinderRegistry {

		public Set<ArtifactFinderBySourceProvider> getRegisteredArtifactFinderProviders() {
			return Collections
					.<ArtifactFinderBySourceProvider> setOf(new DatabaseCustomArtifactFinderBySourceProvider());
		}

	}

	@Ignore
	@Test
	public void shouldExecuteBundleProcessor() throws Exception {
		delete("./target/test-data/DbTableArtifactBundleProcessorTest"); //$NON-NLS-1$

		final GlobalSettings settings = new GlobalSettings();
		settings.setDefaultSleepingIntervalInMilliseconds(1000);
		settings.setNumberOfParallelThreads(4);
		settings.setArtifactFinderRegistryClass(SampleDbArtifactRegistry.class);
		GlobalSettingsSupport.initializeScheduleMap(settings);
		final Repository repository = new Repository();
		repository.setName("sampleRepository");
		repository.setActive(true);
		final Group group = new Group();
		group.setName("sampleGroup");
		group.setRepository(repository);
		repository.getGroups().add(group);
		group.setActive(true);
		final DbArtifactSource artifactSource = new DbArtifactSource();
		artifactSource.setServerName("server name");
		repository.getArtifactSources().add(artifactSource);
		artifactSource.setRepository(repository);
		artifactSource.setName("h2");
		artifactSource.setActive(true);
		artifactSource.setUser("sa");
		artifactSource.setPassword("sa");
		artifactSource.setMaxConnections(4);
		artifactSource.setType(DatabaseType.H2);
		artifactSource
				.setInitialLookup("jdbc:h2:./target/test-data/DbTableArtifactBundleProcessorTest/h2/db;DB_CLOSE_ON_EXIT=FALSE");
		artifactSource.setDriverClass("org.h2.Driver");

		final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
		mapping.setSource(artifactSource);
		artifactSource.getMappings().add(mapping);
		mapping.setFrom("*/");
		mapping.setTo("/databaseArtifacts");
		artifactSource.getMappings().add(mapping);
		mapping.getIncludeds().add("*");
		final BundleProcessorType commonProcessor = new BundleProcessorType();
		commonProcessor.setActive(true);
		commonProcessor.setGroup(group);
		commonProcessor.setType(DbTableArtifactBundleProcessor.class);
		group.getBundleTypes().add(commonProcessor);

		final BundleSource bundleSource = new BundleSource();
		commonProcessor.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(commonProcessor);
		bundleSource.setRelative("/databaseArtifacts");
		bundleSource.getIncludeds().add("*");

		final DbArtifactSource dbSource = (DbArtifactSource) repository
				.getArtifactSources().iterator().next();

		final Connection conn = DatabaseSupport.createConnection(dbSource);

		conn
				.prepareStatement(
						"create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)") //$NON-NLS-1$
				.execute();
		conn
				.prepareStatement(
						"create view exampleView (s_was_i, dp_was_s, i_was_f, f_was_dp) as select i,s,f,dp from exampleTable") //$NON-NLS-1$
				.execute();

		conn.close();

		final ExecutionContextFactory contextFactory = DefaultExecutionContextFactory
				.createFactory();
		final ExecutionContext context = contextFactory.createExecutionContext(
				"username", "password", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
				repository.getName());

		context.getDefaultConfigurationManager().saveGlobalSettings(settings);
		context.getDefaultConfigurationManager().saveRepository(repository);
		context.closeResources();

		final DefaultScheduler scheduler = DefaultScheduler.INSTANCE;
		scheduler.initializeSettings(contextFactory, "user", "password",
				DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		scheduler.refreshJobs(settings, Collections.setOf(repository));
		scheduler.startScheduler();
		scheduler.fireSchedulable("username", "password", artifactSource);
		Thread.sleep(5000);
		scheduler.fireSchedulable("username", "password", group);
		Thread.sleep(5000);

		final ExecutionContext executionContext = contextFactory
				.createExecutionContext("username", "password",
						DefaultJcrDescriptor.TEMP_DESCRIPTOR, repository
								.getName());
		final SLContext groupContext = executionContext.getGraphSession()
				.getContext(SLConsts.DEFAULT_GROUP_CONTEXT);
		final SLNode groupNode = groupContext.getRootNode().getNode(
				group.getUniqueName());
		Assert.assertThat(groupNode, Is.is(IsNull.notNullValue()));
		final SLNode exampleTableNode = groupNode.getNode("EXAMPLETABLE");
		Assert.assertThat(exampleTableNode, Is.is(IsNull.notNullValue()));

		scheduler.stopScheduler();
		Assert.fail("verify if the expected graph nodes was created or not");

	}

}
