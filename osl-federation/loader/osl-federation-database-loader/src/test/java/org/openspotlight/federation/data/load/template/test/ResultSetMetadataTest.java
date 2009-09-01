package org.openspotlight.federation.data.load.template.test;

import static java.lang.Class.forName;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.data.load.db.test.H2Support;

public class ResultSetMetadataTest {

	@BeforeClass
	public static void loadDriver() throws Exception {
		forName("org.h2.Driver");
	}

	@Before
	public void cleanAndFillFreshDatabase() throws Exception {
		delete("./target/test-data/ResultSetMetadataTest/h2");
		Connection conn = DriverManager
				.getConnection("jdbc:h2:./target/test-data/ResultSetMetadataTest/h2/db");
		H2Support.fillDatabaseArtifacts(conn);
		conn.commit();
		conn.close();
	}

	@SuppressWarnings("boxing")
	@Test
	public void shouldReadMetadataFromSelect() throws Exception {
		Connection conn = DriverManager
				.getConnection("jdbc:h2:./target/test-data/ResultSetMetadataTest/h2/db");
		ResultSet resultSet = conn
				.prepareStatement(
						" select TABLE_CATALOG AS CATALOG_NAME, TABLE_SCHEMA AS SCHEMA_NAME, TABLE_NAME AS NAME from INFORMATION_SCHEMA.TABLES where TABLE_TYPE='TABLE'")
				.executeQuery();
		ResultSetMetaData metadata = resultSet.getMetaData();
		List<String> columnNames = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME",
				"NAME");

		for (int i = 1, count = metadata.getColumnCount(); i <= count; i++) {
			String name = metadata.getColumnLabel(i);
			assertThat(columnNames.contains(name), is(true));
		}

	}

}
