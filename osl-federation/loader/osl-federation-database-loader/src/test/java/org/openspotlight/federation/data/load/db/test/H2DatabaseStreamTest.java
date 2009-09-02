package org.openspotlight.federation.data.load.db.test;

import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.ScriptType;

@SuppressWarnings("all")
public class H2DatabaseStreamTest extends DatabaseStreamTest {

	@Before
	public void cleanDatabaseFiles() throws Exception {
		delete("./target/test-data/H2DatabaseStreamTest"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DbBundle createValidConfigurationWithMappings() {
		final Configuration configuration = createH2DbConfiguration("H2DatabaseStreamTest"); //$NON-NLS-1$
		return (DbBundle) configuration.getRepositoryByName("H2 Repository") //$NON-NLS-1$
				.getProjectByName("h2 Group") //$NON-NLS-1$
				.getBundleByName("H2 Connection"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillDatabase(final Connection conn) throws Exception {
		H2Support.fillDatabaseArtifacts(conn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Set<ScriptType> typesToAssert() {
		return EnumSet.of(ScriptType.VIEW, ScriptType.FUNCTION,
				ScriptType.PROCEDURE, ScriptType.INDEX, ScriptType.TABLE,
				ScriptType.TRIGGER);
	}

}
