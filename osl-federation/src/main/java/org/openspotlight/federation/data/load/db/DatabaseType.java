package org.openspotlight.federation.data.load.db;

/**
 * All the database valid types should have an entry on file
 * <b>/osl-federation
 * /src/main/resources/configuration/dbMetadataScripts.xml</b> and also on
 * this enum.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public enum DatabaseType {
    /**
     * The first type supported, just to be possible to do tests on a pure
     * java way.
     */
    H2
}