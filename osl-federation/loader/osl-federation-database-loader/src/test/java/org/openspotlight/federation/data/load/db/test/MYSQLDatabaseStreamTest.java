package org.openspotlight.federation.data.load.db.test;

import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createMySqlDbConfiguration;

import java.sql.Connection;
import java.util.EnumSet;
import java.util.Set;

import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.ScriptType;

@SuppressWarnings("all")
public class MYSQLDatabaseStreamTest extends DatabaseStreamTest implements
		RunWhenDatabaseVendorTestsIsActive {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DbBundle createValidConfigurationWithMappings() {
		final Configuration configuration = createMySqlDbConfiguration();
		return (DbBundle) configuration.getRepositoryByName("mysql Repository") //$NON-NLS-1$
				.getProjectByName("mysql Group") //$NON-NLS-1$
				.getBundleByName("mysql Connection"); //$NON-NLS-1$
	}

	@Override
	protected void fillDatabase(final Connection conn) throws Exception {
		// conn.prepareStatement(
		// "CREATE TABLE example_table ( id INT,  data VARCHAR(100) ) ")
		// .execute();
		// conn.prepareStatement(
		// "CREATE TRIGGER example_trigger BEFORE INSERT ON example_table "
		// + " FOR EACH ROW BEGIN "
		// + "   update example_table set id=1; " + " END")
		// .execute();
		// conn.prepareStatement(
		// "CREATE PROCEDURE example_proc (OUT param1 INT) " + " BEGIN "
		// + "  SELECT COUNT(*) INTO param1 FROM example_table; "
		// + "END").execute();
		// conn.prepareStatement(
		// " CREATE FUNCTION example_function (n1 INT, n2 INT, n3 INT, n4 INT) "
		// + "   RETURNS INT " + "    DETERMINISTIC "
		// + "     BEGIN " + "      return n1; " + "     END")
		// .execute();
	}

	@Override
	protected void resetDatabase(final Connection conn) throws Exception {
		// conn.prepareStatement("drop TRIGGER example_trigger ").execute();
		// conn.prepareStatement("drop TABLE example_table ").execute();
		// conn.prepareStatement("drop PROCEDURE example_proc ").execute();
		// conn.prepareStatement(" drop FUNCTION example_function ").execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Set<ScriptType> typesToAssert() {
		return EnumSet.of(ScriptType.TABLE, ScriptType.TRIGGER,
				ScriptType.FUNCTION, ScriptType.PROCEDURE, ScriptType.VIEW);
	}

}
