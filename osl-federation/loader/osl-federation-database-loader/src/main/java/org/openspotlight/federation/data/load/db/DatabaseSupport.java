package org.openspotlight.federation.data.load.db;

import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.sql.Connection;

import org.openspotlight.federation.data.impl.DbBundle;

/**
 * Helper methods to manipulate database resources.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class DatabaseSupport {
	/**
	 * Helper method to create a connection using a {@link DbBundle}.
	 * 
	 * @param dbBundle
	 * @return the connection itself
	 * @throws Exception
	 */
	public static Connection createConnection(DbBundle dbBundle) throws Exception {
		checkNotNull("dbBundle", dbBundle); //$NON-NLS-1$
		Connection connection = null;
		forName(dbBundle.getDriverClass());
		if (dbBundle.getUser() == null) {
			connection = getConnection(dbBundle.getInitialLookup());
		} else {
			connection = getConnection(dbBundle.getInitialLookup(), dbBundle
					.getUser(), dbBundle.getPassword());
		}
		return connection;
	}
}
