package org.openspotlight.federation.data.load.db;

/**
 * Script types to be used inside the artifact loader implementation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public enum ScriptType {
    /**
     * Trigger type.
     */
    TRIGGER,
    /**
     * Procedure type.
     */
    PROCEDURE,
    /**
     * Function type.
     */
    FUNCTION,
    /**
     * View creation script.
     */
    VIEW,
    /**
     * Table creation script.
     */
    TABLE,
    /**
     * Index creation script.
     */
    INDEX,
    /**
     * Custom artifacts not common to all databases, such as oracle types or
     * oracle packages.
     */
    CUSTOM
}