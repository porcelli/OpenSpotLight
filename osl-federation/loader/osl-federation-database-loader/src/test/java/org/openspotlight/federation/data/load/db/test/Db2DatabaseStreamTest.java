package org.openspotlight.federation.data.load.db.test;

import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createDb2Configuration;

import java.util.EnumSet;
import java.util.Set;

import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.ScriptType;

@SuppressWarnings("all")
public class Db2DatabaseStreamTest extends DatabaseStreamTest implements
		RunWhenDatabaseVendorTestsIsActive {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DbBundle createValidConfigurationWithMappings() {
		final Configuration configuration = createDb2Configuration();
		return (DbBundle) configuration.getRepositoryByName("db2 Repository") //$NON-NLS-1$
				.getGroupByName("db2 Group") //$NON-NLS-1$
				.getBundleByName("db2 Connection"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Set<ScriptType> typesToAssert() {
		return EnumSet.of(ScriptType.TABLE, ScriptType.TRIGGER,
				ScriptType.PROCEDURE, ScriptType.FUNCTION, ScriptType.VIEW,
				ScriptType.INDEX, ScriptType.CONSTRAINT, ScriptType.FK);
	}

	// @Override
	// protected void fillDatabase(Connection conn) throws Exception {
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
	// + "     BEGIN " + "      return n1; " + "     END").execute();
	// }
	//
	// @Override
	// protected void resetDatabase(Connection conn) throws Exception {
	// conn.prepareStatement("drop TRIGGER example_trigger ").execute();
	// conn.prepareStatement("drop TABLE example_table ").execute();
	// conn.prepareStatement("drop PROCEDURE example_proc ").execute();
	// conn.prepareStatement(" drop FUNCTION example_function ").execute();
	// }

}
