package org.openspotlight.federation.data.load.db.test;

import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createSqlServerDbConfiguration;

import java.sql.Connection;
import java.util.EnumSet;
import java.util.Set;

import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.ScriptType;

@SuppressWarnings("all")
public class SqlServerDatabaseStreamTest extends DatabaseStreamTest implements
		RunWhenDatabaseVendorTestsIsActive {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DbBundle createValidConfigurationWithMappings() {
		final Configuration configuration = createSqlServerDbConfiguration();
		return (DbBundle) configuration.getRepositoryByName(
				"sqlserver Repository") //$NON-NLS-1$
				.getGroupByName("sqlserver Group") //$NON-NLS-1$
				.getBundleByName("sqlserver Connection"); //$NON-NLS-1$
	}

	@Override
	protected void fillDatabase(final Connection conn) throws Exception {
		conn.prepareStatement(
				"CREATE TABLE example_table ( id INT,  data VARCHAR(100) ) ")
				.execute();
		conn.prepareStatement(
				" create trigger example_trigger ON example_table "
						+ " FOR INSERT, UPDATE "
						+ " AS RAISERROR (50009, 16, 10)").execute();
		conn.prepareStatement(
				"create view example_view as select * from example_table")
				.execute();
		conn
				.prepareStatement(
						"ALTER TABLE example_table "
								+ " ADD CONSTRAINT example_constraint CHECK (id >= 1 )")
				.execute();
		conn.prepareStatement(
				" CREATE FUNCTION example_function() " + " RETURNS int "
						+ " AS " + " BEGIN " + "  RETURN(1); " + " END ")
				.execute();
	}

	@Override
	protected void resetDatabase(final Connection conn) throws Exception {
		conn.prepareStatement("drop TRIGGER example_trigger ").execute();
		conn.prepareStatement("drop view example_view ").execute();
		conn.prepareStatement("drop TABLE example_table ").execute();
		// conn.prepareStatement("drop PROCEDURE example_proc ").execute();
		conn.prepareStatement(" drop FUNCTION example_function ").execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Set<ScriptType> typesToAssert() {
		return EnumSet.of(ScriptType.TABLE, ScriptType.TRIGGER,
				ScriptType.PROCEDURE, ScriptType.FUNCTION, ScriptType.VIEW,
				ScriptType.CONSTRAINT, ScriptType.FK);
	}

}
