package org.openspotlight.federation.data.load.db.test;

import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.finder.db.DatabaseSupport.createConnection;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.federation.data.load.DatabaseStreamArtifactFinder;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Configuration;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.db.ScriptType;
import org.openspotlight.federation.loader.ArtifactLoader;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test is intended to be used to test scripts to retrieve stream artifacts for a given {@link DatabaseType}. Most of the
 * environments used to run <code>mvn clean install</code> would not have all the database types. But there's a need to have a
 * test for each type on the source code. On this cases, the tests will be annotated with {@link Ignore} annotation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "all" )
public abstract class DatabaseStreamTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void cleanFiles() throws Exception {
        delete("./target/test-data/" + this.getClass().getSimpleName() + "/");
    }

    /**
     * Here a valid configuration to connect on the target database should be created. The necessary data to be created here are
     * the database connection and also the artifact mappings to load all artifacts been tested for a given type.
     * 
     * @return a valid database configuration
     */
    protected abstract DbArtifactSource createValidConfigurationWithMappings();

    /**
     * Fill the data necessary to run the database tests. For example, here it could be created procedure, triggers and so on.
     * 
     * @param conn
     * @throws Exception
     */
    protected void fillDatabase( final Connection conn ) throws Exception {
        //
    }

    /**
     * Here's an option to reset all filled data on the database.
     * 
     * @param conn
     * @throws Exception
     */
    protected void resetDatabase( final Connection conn ) throws Exception {
        //
    };

    /**
     * This test method will load all artifacts from the configuration and assert if all artifacts of the {@link #typesToAssert()}
     * are loaded.
     * 
     * @throws Exception
     */
    @Test
    public void shouldLoadAllValidTypes() throws Exception {
        if (this instanceof RunWhenDatabaseVendorTestsIsActive) {
            if ("true".equals(System.getProperty("runDatabaseVendorTests"))) {
                this.validateAllTypes();
            } else {
                this.logger.warn(format("Ignoring test {0} because system property {1} isn't set to true.",
                                        this.getClass().getSimpleName(), "runDatabaseVendorTests"));
            }
        } else {
            this.validateAllTypes();
        }

    }

    /**
     * @return the types to load during the test.
     */
    protected abstract Set<ScriptType> typesToAssert();

    private void validateAllTypes() throws Exception {
        final DbArtifactSource bundle = this.createValidConfigurationWithMappings();
        Connection conn = createConnection(bundle);
        this.fillDatabase(conn);
        if (!conn.isClosed()) {
            conn.close();
        }
        final DatabaseStreamArtifactFinder finder = new DatabaseStreamArtifactFinder();
        final Configuration configuration = new Configuration();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);
        configuration.setNumberOfParallelThreads(4);

        final ArtifactLoader loader = ArtifactLoader.Factory.createNewLoader(configuration,
                                                                             ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE, finder);

        conn = createConnection(bundle);
        this.resetDatabase(conn);
        if (!conn.isClosed()) {
            conn.close();
        }

        final Iterable<Artifact> loadedArtifacts = loader.loadArtifactsFromSource(bundle);
        final Set<String> failMessages = new HashSet<String>();
        lookingTypes: for (final ScriptType typeToAssert : this.typesToAssert()) {
            for (final Artifact artifact : loadedArtifacts) {
                final StreamArtifact streamArtifact = (StreamArtifact)artifact;
                final String relativeName = streamArtifact.getArtifactCompleteName();
                if (relativeName.contains(typeToAssert.name())) {
                    assertThat(streamArtifact.getContent(), is(notNullValue()));
                    continue lookingTypes;
                }
            }
            failMessages.add(format("Type {0} was not found in any of strings: {1}", //$NON-NLS-1$
                                    typeToAssert, loadedArtifacts));
        }
        if (!failMessages.isEmpty()) {
            fail(failMessages.toString());
        }
        for (final Artifact artifact : loadedArtifacts) {
            final StreamArtifact streamArtifact = (StreamArtifact)artifact;
            final String name = "./target/test-data/" + this.getClass().getSimpleName() + "/"
                                + streamArtifact.getArtifactCompleteName().replaceAll(" ", "");// DB2 has
            // some
            // spaces
            final String dirName = name.substring(0, name.lastIndexOf('/'));
            new File(dirName).mkdirs();
            final OutputStream fos = new BufferedOutputStream(new FileOutputStream(name + ".sql"));
            final InputStream is = new ByteArrayInputStream(streamArtifact.getContent().getBytes());
            while (true) {
                final int data = is.read();
                if (data == -1) {
                    break;
                }
                fos.write(data);
            }
            fos.flush();
            fos.close();
        }

    }

}
