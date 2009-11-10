package org.openspotlight.federation.data.load.db.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.load.DatabaseCustomArtifactLoader;
import org.openspotlight.federation.data.load.DatabaseStreamLoader;
import org.openspotlight.federation.data.load.db.DatabaseSupport;
import org.openspotlight.federation.domain.CustomArtifact;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.loader.ArtifactLoaderGroup;

/**
 * During a column changing, its table needs to be marked as changed also. This
 * test is to assert this behavior.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class ColumnChangingFiresTableChangeTest {

	@Before
	public void cleanDatabaseFiles() throws Exception {
		delete("./target/test-data/ColumnChangingFiresTableChangeTest"); //$NON-NLS-1$
	}

	@Test
	public void columnChangeShouldFireTableChange() throws Exception {

		final Configuration configuration = createH2DbConfiguration("ColumnChangingFiresTableChangeTest"); //$NON-NLS-1$
		final DbArtifactSource dbBundle = (DbArtifactSource) configuration.getRepositoryByName(
				"H2 Repository") //$NON-NLS-1$
				.getGroupByName("h2 Group") //$NON-NLS-1$
				.getArtifactSourceByName("H2 Connection"); //$NON-NLS-1$
		Connection conn = DatabaseSupport.createConnection(dbBundle);

		conn
				.prepareStatement(
						"create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)") //$NON-NLS-1$
				.execute();
		conn.close();
		final ArtifactLoaderGroup loader = new ArtifactLoaderGroup(
				new DatabaseStreamLoader(), new DatabaseCustomArtifactLoader());

		loader.loadArtifactsFromMappings(dbBundle);

		dbBundle.getInstanceMetadata().getSharedData().markAsSaved();

		conn = DatabaseSupport.createConnection(dbBundle);

		conn.prepareStatement("drop table exampleTable") //$NON-NLS-1$
				.execute();

		conn.prepareStatement(
				"create table exampleTable(changed_columns int not null)") //$NON-NLS-1$
				.execute();
		conn.close();
		loader.loadArtifactsFromMappings(dbBundle);
		final CustomArtifact table = dbBundle
				.getCustomArtifactByName("PUBLIC/TABLE/DB/EXAMPLETABLE");
		assertThat(table, is(notNullValue()));
		final Set<ConfigurationNode> dirtyNodes = dbBundle
				.getInstanceMetadata().getSharedData().getDirtyNodes();
		assertThat(dirtyNodes.contains(table), is(true));
	}

}
