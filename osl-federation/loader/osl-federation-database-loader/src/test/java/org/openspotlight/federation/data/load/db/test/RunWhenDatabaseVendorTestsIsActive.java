package org.openspotlight.federation.data.load.db.test;

/**
 * Marker interface to activate the {@link DatabaseStreamTest database stream
 * tests} only when the system property <b>runDatabaseVendorTests</b> is set to
 * true. This should be done because on a common environment you do not have
 * Oracle, MySql, SqlServer, Db2 and so on installed and running on the system.
 * 
 * @author feu
 * 
 */
public interface RunWhenDatabaseVendorTestsIsActive {
	//
}
