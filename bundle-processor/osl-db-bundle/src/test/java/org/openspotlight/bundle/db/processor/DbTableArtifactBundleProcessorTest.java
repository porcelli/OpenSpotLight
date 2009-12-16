package org.openspotlight.bundle.db.processor;

import static org.openspotlight.common.util.Files.delete;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.db.DatabaseSupport;

public class DbTableArtifactBundleProcessorTest {

	private Repository repository;

	private DbArtifactSource dbSource;

	private Repository createSampleConfiguration() {
		final Repository repository = new Repository();
		repository.setName("sampleRepository");
		repository.setActive(true);
		final Group group = new Group();
		group.setName("sampleGroup");
		group.setRepository(repository);
		repository.getGroups().add(group);
		group.setActive(true);
		final DbArtifactSource artifactSource = new DbArtifactSource();
		repository.getArtifactSources().add(artifactSource);
		artifactSource.setRepository(repository);
		artifactSource.setName("h2");
		artifactSource.setActive(true);
		artifactSource.setUser("sa");
		artifactSource.setPassword("sa");
		artifactSource.setMaxConnections(4);
		artifactSource.setType(DatabaseType.H2);
		artifactSource
				.setInitialLookup("jdbc:h2:./target/test-data/DbTableArtifactBundleProcessorTest/h2/db");
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
		group.getBundleTypes().add(commonProcessor);
		final BundleProcessorType customProcessor = new BundleProcessorType();
		customProcessor.setActive(true);
		customProcessor.setGroup(group);
		group.getBundleTypes().add(customProcessor);
		return repository;
	}

	@Before
	public void setupDatabase() throws Exception {
		delete("./target/test-data/DbTableArtifactBundleProcessorTest"); //$NON-NLS-1$

		repository = createSampleConfiguration();

		dbSource = (DbArtifactSource) repository.getArtifactSources()
				.iterator().next();

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
	}

	@Test
	public void shouldExecuteBundleProcessor() throws Exception {

	}

}
