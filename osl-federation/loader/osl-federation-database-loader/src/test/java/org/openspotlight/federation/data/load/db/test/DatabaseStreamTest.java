package org.openspotlight.federation.data.load.db.test;

import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;
import static org.openspotlight.federation.data.util.ConfigurationNodes.findAllNodesOfType;

import java.sql.Connection;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.DatabaseStreamLoader;
import org.openspotlight.federation.data.load.db.ScriptType;

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
public abstract class DatabaseStreamTest {

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
	protected abstract void fillDatabase(Connection conn) throws Exception;

	/**
	 * Here's an option to reset all filled data on the database.
	 * 
	 * @param conn
	 * @throws Exception
	 */
	protected void resetDatabase(Connection conn) throws Exception {
		//
	};

	/**
	 * 
	 * @return the types to load during the test.
	 */
	protected abstract Set<ScriptType> typesToAssert();

	/**
	 * This test method will load all artifacts from the configuration and
	 * assert if all artifacts of the {@link #typesToAssert()} are loaded.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("boxing")
	@Test
	public void shouldLoadAllValidTypes() throws Exception {
		DbBundle bundle = createValidConfigurationWithMappings();
		Connection conn = createConnection(bundle);
		fillDatabase(conn);
		if (!conn.isClosed())
			conn.close();
		DatabaseStreamLoader loader = new DatabaseStreamLoader();
		loader.loadArtifactsFromMappings(bundle);
		Set<StreamArtifact> loadedArtifacts = findAllNodesOfType(bundle,
				StreamArtifact.class);
		lookingTypes: for (ScriptType typeToAssert : typesToAssert()) {
			for (StreamArtifact streamArtifact : loadedArtifacts) {
				String relativeName = streamArtifact.getRelativeName();
				if (relativeName.contains(typeToAssert.name())) {
					assertThat(streamArtifact.getDataSha1(), is(notNullValue()));
					assertThat(streamArtifact.getData(), is(notNullValue()));
					assertThat(streamArtifact.getData().available() > 0,
							is(true));
					continue lookingTypes;
				}
			}
			fail(format("Type {0} was not found in any of strings: {1}", //$NON-NLS-1$
					typeToAssert, loadedArtifacts));

		}
		conn = createConnection(bundle);
		resetDatabase(conn);
		if (!conn.isClosed())
			conn.close();

	}

}
