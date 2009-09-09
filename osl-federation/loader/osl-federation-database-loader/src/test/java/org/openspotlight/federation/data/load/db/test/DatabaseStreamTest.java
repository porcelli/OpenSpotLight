package org.openspotlight.federation.data.load.db.test;

import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;
import static org.openspotlight.federation.data.util.ConfigurationNodes.findAllNodesOfType;

import java.io.BufferedOutputStream;
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
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.DatabaseStreamLoader;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test is intended to be used to test scripts to retrieve stream artifacts
 * for a given {@link DatabaseType}. Most of the environments used to run
 * <code>mvn clean install</code> would not have all the database types. But
 * there's a need to have a test for each type on the source code. On this
 * cases, the tests will be annotated with {@link Ignore} annotation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public abstract class DatabaseStreamTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Before
	public void cleanFiles() throws Exception {
		delete("./target/test-data/" + this.getClass().getSimpleName() + "/");
	}

	/**
	 * Here a valid configuration to connect on the target database should be
	 * created. The necessary data to be created here are the database
	 * connection and also the artifact mappings to load all artifacts been
	 * tested for a given type.
	 * 
	 * @return a valid database configuration
	 */
	protected abstract DbBundle createValidConfigurationWithMappings();

	/**
	 * Fill the data necessary to run the database tests. For example, here it
	 * could be created procedure, triggers and so on.
	 * 
	 * @param conn
	 * @throws Exception
	 */
	protected void fillDatabase(final Connection conn) throws Exception {
		//
	}

	/**
	 * Here's an option to reset all filled data on the database.
	 * 
	 * @param conn
	 * @throws Exception
	 */
	protected void resetDatabase(final Connection conn) throws Exception {
		//
	};

	/**
	 * This test method will load all artifacts from the configuration and
	 * assert if all artifacts of the {@link #typesToAssert()} are loaded.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldLoadAllValidTypes() throws Exception {
		if (this instanceof RunWhenDatabaseVendorTestsIsActive) {
			if ("true".equals(System.getProperty("runDatabaseVendorTests"))) {
				this.validateAllTypes();
			} else {
				this.logger
						.warn(format(
								"Ignoring test {0} because system property {1} isn't set to true.",
								this.getClass().getSimpleName(),
								"runDatabaseVendorTests"));
			}
		} else {
			this.validateAllTypes();
		}

	}

	/**
	 * 
	 * @return the types to load during the test.
	 */
	protected abstract Set<ScriptType> typesToAssert();

	private void validateAllTypes() throws Exception {
		final DbBundle bundle = this.createValidConfigurationWithMappings();
		Connection conn = createConnection(bundle);
		this.fillDatabase(conn);
		if (!conn.isClosed()) {
			conn.close();
		}
		final DatabaseStreamLoader loader = new DatabaseStreamLoader();
		loader.loadArtifactsFromMappings(bundle);
		conn = createConnection(bundle);
		this.resetDatabase(conn);
		if (!conn.isClosed()) {
			conn.close();
		}

		final Set<StreamArtifact> loadedArtifacts = findAllNodesOfType(bundle,
				StreamArtifact.class);
		final Set<String> failMessages = new HashSet<String>();
		lookingTypes: for (final ScriptType typeToAssert : this.typesToAssert()) {
			for (final StreamArtifact streamArtifact : loadedArtifacts) {
				final String relativeName = streamArtifact.getRelativeName();
				if (relativeName.contains(typeToAssert.name())) {
					assertThat(streamArtifact.getDataSha1(), is(notNullValue()));
					assertThat(streamArtifact.getData(), is(notNullValue()));
					assertThat(streamArtifact.getData().available() > 0,
							is(true));
					continue lookingTypes;
				}
			}
			failMessages.add(format(
					"Type {0} was not found in any of strings: {1}", //$NON-NLS-1$
					typeToAssert, loadedArtifacts));
		}
		if (!failMessages.isEmpty()) {
			fail(failMessages.toString());
		}
		for (final StreamArtifact loaded : loadedArtifacts) {
			final String name = "./target/test-data/"
					+ this.getClass().getSimpleName() + "/"
					+ loaded.getRelativeName().replaceAll(" ", "");// DB2 has
																	// some
																	// spaces
			final String dirName = name.substring(0, name.lastIndexOf('/'));
			new File(dirName).mkdirs();
			final OutputStream fos = new BufferedOutputStream(
					new FileOutputStream(name + ".sql"));
			final InputStream is = loaded.getData();
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
