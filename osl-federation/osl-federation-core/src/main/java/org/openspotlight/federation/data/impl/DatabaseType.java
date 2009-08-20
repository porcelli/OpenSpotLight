package org.openspotlight.federation.data.impl;

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
    H2,
    /**
     * MySQL Database.
     */
    MYSQL,
    /**
     * Postgre Sql Database.
     */
    POSTGRES,
    /**
     * Oracle version before 9i
     */
    ORACLE,
    /**
     * Oracle version 9i and after
     */
    ORACLE9(ORACLE),
    /**
     * SQL Server
     */
    SQL_SERVER,
    /**
     * DB2
     */
    DB2;
    
    private final DatabaseType parent;
    
    private DatabaseType(){
    	this.parent=null;
    }
    
    private DatabaseType(DatabaseType parent){
    	this.parent = parent;
    }
    
    /**
     * 
     * @return the parent type
     */
    public DatabaseType getParent(){
    	return this.parent;
    }
    
    
}