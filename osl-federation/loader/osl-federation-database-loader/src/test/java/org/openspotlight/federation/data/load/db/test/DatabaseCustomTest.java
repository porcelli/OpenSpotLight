package org.openspotlight.federation.data.load.db.test;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.CustomArtifact;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.RoutineArtifact;
import org.openspotlight.federation.domain.RoutineType;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.domain.ViewArtifact;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinder;
import org.openspotlight.federation.loader.ArtifactLoader;
import org.openspotlight.federation.loader.ArtifactLoaderFactory;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;

@SuppressWarnings( "all" )
public class DatabaseCustomTest {

    @BeforeClass
    public static void loadDriver() throws Exception {
        Class.forName("org.h2.Driver");
    }

    private ArtifactLoader               artifactLoader;
    private DatabaseCustomArtifactFinder finder;

    @Before
    public void setup() throws Exception {
        delete("./target/test-data"); //$NON-NLS-1$

        this.finder = new DatabaseCustomArtifactFinder();
        final GlobalSettings configuration = new GlobalSettings();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);
        configuration.setNumberOfParallelThreads(4);

        this.artifactLoader = ArtifactLoaderFactory.createNewLoader(configuration, ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE,
                                                                    this.finder);
    }

    @Test
    public void shouldLoadProceduresAndFunctions() throws Exception {
        final DbArtifactSource bundle = (DbArtifactSource)createH2DbConfiguration("DatabaseArtifactLoaderTest").getArtifactSources().iterator().next();
        bundle.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        Connection connection = getConnection("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db", "sa", "");

        connection.prepareStatement(
                                    "create alias newExampleFunction for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.increment\" ").execute();
        connection.prepareStatement(
                                    "create alias newExampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"").execute();
        connection.commit();
        connection.close();

        final Iterable<Artifact> loadedArtifacts = this.artifactLoader.loadArtifactsFromSource(bundle);
        assertThat(loadedArtifacts, is(notNullValue()));
        assertThat(loadedArtifacts.iterator().hasNext(), is(true));

        final RoutineArtifact exampleProcedure = (RoutineArtifact)this.finder.findByPath(bundle,
                                                                                         "PUBLIC/PROCEDURE/DB/NEWEXAMPLEPROCEDURE");
        final RoutineArtifact exampleFunction = (RoutineArtifact)this.finder.findByPath(bundle,
                                                                                        "PUBLIC/FUNCTION/DB/NEWEXAMPLEFUNCTION");
        assertThat(exampleProcedure.getType(), is(RoutineType.PROCEDURE));
        assertThat(exampleFunction.getType(), is(RoutineType.FUNCTION));
        connection = getConnection("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db", "sa", "");

        connection.prepareStatement("drop alias newExampleProcedure ").execute();
        connection.prepareStatement("drop alias newExampleFunction ").execute();
        connection.commit();
        connection.close();

    }

    @Test
    public void shouldLoadTablesAndViews() throws Exception {
        final DbArtifactSource bundle = (DbArtifactSource)createH2DbConfiguration("DatabaseArtifactLoaderTest").getArtifactSources().iterator().next();
        bundle.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        final Connection connection = getConnection("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
                                                    "sa", "");
        bundle.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        connection.prepareStatement(
                                    "create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)").execute();
        connection.prepareStatement(
                                    "create view exampleView (s_was_i, dp_was_s, i_was_f, f_was_dp) as select i,s,f,dp from exampleTable").execute();
        connection.commit();
        connection.close();

        final Iterable<Artifact> loadedArtifacts = this.artifactLoader.loadArtifactsFromSource(bundle);
        assertThat(loadedArtifacts, is(notNullValue()));
        assertThat(loadedArtifacts.iterator().hasNext(), is(true));

        final CustomArtifact exampleTable = this.finder.findByPath(bundle, "PUBLIC/TABLE/DB/EXAMPLETABLE");
        final CustomArtifact exampleView = this.finder.findByPath(bundle, "PUBLIC/VIEW/DB/EXAMPLEVIEW");
        assertThat(exampleTable, is(TableArtifact.class));
        assertThat(exampleView, is(ViewArtifact.class));
    }
}
