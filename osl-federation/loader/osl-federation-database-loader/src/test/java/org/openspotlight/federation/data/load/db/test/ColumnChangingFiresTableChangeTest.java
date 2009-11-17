package org.openspotlight.federation.data.load.db.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.load.DatabaseStreamArtifactFinder;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.db.DatabaseSupport;
import org.openspotlight.federation.loader.ArtifactLoader;
import org.openspotlight.federation.loader.ArtifactLoaderFactory;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;

/**
 * During a column changing, its table needs to be marked as changed also. This test is to assert this behavior.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "all" )
public class ColumnChangingFiresTableChangeTest {

    @Before
    public void cleanDatabaseFiles() throws Exception {
        delete("./target/test-data/ColumnChangingFiresTableChangeTest"); //$NON-NLS-1$
    }

    @Test
    public void columnChangeShouldFireTableChange() throws Exception {

        final Repository repository = createH2DbConfiguration("ColumnChangingFiresTableChangeTest"); //$NON-NLS-1$
        final DbArtifactSource dbBundle = (DbArtifactSource)repository.getArtifactSources().iterator().next(); //$NON-NLS-1$
        Connection conn = DatabaseSupport.createConnection(dbBundle);

        conn.prepareStatement(
                              "create table exampleTable(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)") //$NON-NLS-1$
        .execute();
        conn.close();
        final DatabaseStreamArtifactFinder finder = new DatabaseStreamArtifactFinder();
        final GlobalSettings configuration = new GlobalSettings();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);
        configuration.setNumberOfParallelThreads(4);

        ArtifactLoader loader = ArtifactLoaderFactory.createNewLoader(configuration,
                                                                      ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE, finder);

        final Iterable<Artifact> firstLoadedItems = loader.loadArtifactsFromSource(dbBundle);
        loader.closeResources();
        conn = DatabaseSupport.createConnection(dbBundle);

        conn.prepareStatement("drop table exampleTable") //$NON-NLS-1$
        .execute();

        conn.prepareStatement("create table exampleTable(changed_columns int not null)") //$NON-NLS-1$
        .execute();
        conn.close();

        loader = ArtifactLoaderFactory.createNewLoader(configuration, ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE, finder);

        final Iterable<Artifact> lastLoadedItems = loader.loadArtifactsFromSource(dbBundle);
        loader.closeResources();
        boolean found = false;
        all: for (final Artifact first : firstLoadedItems) {
            if (first.getArtifactName().equals("EXAMPLETABLE")) {
                assertThat(first.equals(first), is(true));
                assertThat(first.contentEquals(first), is(true));
                for (final Artifact last : lastLoadedItems) {
                    if (last.getArtifactName().equals("EXAMPLETABLE")) {
                        assertThat(last.equals(first), is(true));
                        assertThat(last.contentEquals(first), is(false));
                        found = true;
                        break all;
                    }
                }
            }
        }
        assertThat(found, is(true));
    }

}
