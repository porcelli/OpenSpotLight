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
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.ArtifactLoaderGroup;
import org.openspotlight.federation.data.load.DatabaseCustomArtifactLoader;
import org.openspotlight.federation.data.load.DatabaseStreamLoader;
import org.openspotlight.federation.data.load.db.DatabaseSupport;

/**
 * This test class is to assert the fire changing action on view stream artifact
 * when it's columns changes. It should be done
 * 
 * @author feu
 * 
 */
public class ViewStreamArtifactChangingTest {

	@Before
	public void cleanDatabaseFiles() throws Exception {
		delete("./target/test-data/ViewStreamArtifactChangingTest"); //$NON-NLS-1$
	}

	@Test
	public void shouldFireStreamChangeWhenViewWithSelectStarChange()
			throws Exception {

		Configuration configuration = createH2DbConfiguration("ViewStreamArtifactChangingTest"); //$NON-NLS-1$
		DbBundle dbBundle = (DbBundle) configuration.getRepositoryByName(
				"H2 Repository") //$NON-NLS-1$
				.getProjectByName("h2 Group") //$NON-NLS-1$
				.getBundleByName("H2 Connection"); //$NON-NLS-1$
		Connection conn = DatabaseSupport.createConnection(dbBundle);

		conn
				.prepareStatement(
						"create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)") //$NON-NLS-1$
				.execute();
		conn.prepareStatement(
				"create view exampleView as select * from exampleTable") //$NON-NLS-1$
				.execute();
		conn.close();
		ArtifactLoaderGroup loader = new ArtifactLoaderGroup(
				new DatabaseStreamLoader(), new DatabaseCustomArtifactLoader());

		loader.loadArtifactsFromMappings(dbBundle);

		dbBundle.getInstanceMetadata().getSharedData().markAsSaved();

		conn = DatabaseSupport.createConnection(dbBundle);

		conn.prepareStatement("drop table exampleTable") //$NON-NLS-1$
				.execute();

		conn.prepareStatement(
				"create table exampleTable(changed_columns int not null)") //$NON-NLS-1$
				.execute();
		conn.prepareStatement(
				"create view exampleView as select * from exampleTable") //$NON-NLS-1$
				.execute();
		conn.close();
		loader.loadArtifactsFromMappings(dbBundle);
		StreamArtifact view = dbBundle
				.getStreamArtifactByName("PUBLIC/VIEW/DB/EXAMPLEVIEW");
		assertThat(view, is(notNullValue()));
		Set<ConfigurationNode> dirtyNodes = dbBundle.getInstanceMetadata()
				.getSharedData().getDirtyNodes();
		assertThat(dirtyNodes.contains(view), is(true));
	}
}