package org.openspotlight.federation.data.load.db.test;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.data.impl.RoutineArtifact;
import org.openspotlight.federation.data.impl.TableArtifact;
import org.openspotlight.federation.data.impl.ViewArtifact;
import org.openspotlight.federation.data.impl.RoutineArtifact.RoutineType;
import org.openspotlight.federation.data.load.DatabaseCustomArtifactLoader;
import org.openspotlight.federation.domain.CustomArtifact;
import org.openspotlight.federation.domain.DbArtifactSource;

@SuppressWarnings("all")
public class DatabaseCustomTest {

	@BeforeClass
	public static void loadDriver() throws Exception {
		Class.forName("org.h2.Driver");
	}

	private final DatabaseCustomArtifactLoader artifactLoader = new DatabaseCustomArtifactLoader();

	@Before
	public void cleanDatabaseFiles() throws Exception {
		delete("./target/test-data"); //$NON-NLS-1$
	}

	@Test
	public void shouldLoadProceduresAndFunctions() throws Exception {
		final DbArtifactSource bundle = (DbArtifactSource) createH2DbConfiguration(
				"DatabaseArtifactLoaderTest").getRepositoryByName(
				"H2 Repository").getGroupByName("h2 Group").getArtifactSourceByName(
				"H2 Connection");
		bundle
				.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
		Connection connection = getConnection(
				"jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
				"sa", "");

		connection
				.prepareStatement(
						"create alias newExampleFunction for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.increment\" ")
				.execute();
		connection
				.prepareStatement(
						"create alias newExampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"")
				.execute();
		connection.commit();
		connection.close();

		this.artifactLoader.loadArtifactsFromMappings(bundle);

		final RoutineArtifact exampleProcedure = (RoutineArtifact) bundle
				.getCustomArtifactByName("PUBLIC/PROCEDURE/DB/NEWEXAMPLEPROCEDURE");
		final RoutineArtifact exampleFunction = (RoutineArtifact) bundle
				.getCustomArtifactByName("PUBLIC/FUNCTION/DB/NEWEXAMPLEFUNCTION");
		assertThat(exampleProcedure.getType(), is(RoutineType.PROCEDURE));
		assertThat(exampleFunction.getType(), is(RoutineType.FUNCTION));
		connection = getConnection(
				"jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
				"sa", "");

		connection.prepareStatement("drop alias newExampleProcedure ")
				.execute();
		connection.prepareStatement("drop alias newExampleFunction ").execute();
		connection.commit();
		connection.close();

	}

	@Test
	public void shouldLoadTablesAndViews() throws Exception {
		final DbArtifactSource bundle = (DbArtifactSource) createH2DbConfiguration(
				"DatabaseArtifactLoaderTest").getRepositoryByName(
				"H2 Repository").getGroupByName("h2 Group").getArtifactSourceByName(
				"H2 Connection");
		bundle
				.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
		final Connection connection = getConnection(
				"jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
				"sa", "");
		connection
				.prepareStatement(
						"create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)")
				.execute();
		connection
				.prepareStatement(
						"create view exampleView (s_was_i, dp_was_s, i_was_f, f_was_dp) as select i,s,f,dp from exampleTable")
				.execute();
		connection.commit();
		connection.close();

		this.artifactLoader.loadArtifactsFromMappings(bundle);

		final Set<String> loadedNames = bundle.getCustomArtifactNames();
		final CustomArtifact exampleTable = bundle
				.getCustomArtifactByName("PUBLIC/TABLE/DB/EXAMPLETABLE");
		final CustomArtifact exampleView = bundle
				.getCustomArtifactByName("PUBLIC/VIEW/DB/EXAMPLEVIEW");
		assertThat(exampleTable, is(TableArtifact.class));
		assertThat(exampleView, is(ViewArtifact.class));
	}

}
