package org.openspotlight.federation.finder.db;

/**
 * Script types to be used inside the artifact loader implementation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public enum ScriptType {
	/**
	 * Constraint creation script.
	 */
	CONSTRAINT,
	/**
	 * Foreign key information.
	 */
	FK,
	/**
	 * Function type.
	 */
	FUNCTION,
	/**
	 * Index creation script.
	 */
	INDEX,
	/**
	 * Package creation script.
	 */
	PACKAGE,
	/**
	 * Procedure type.
	 */
	PROCEDURE,
	/**
	 * Sequence creation script.
	 */
	SEQUENCE,
	/**
	 * Table creation script.
	 */
	TABLE,
	/**
	 * Tablespace creation script.
	 */
	TABLESPACE,
	/**
	 * Trigger type.
	 */
	TRIGGER,
	/**
	 * View creation script.
	 */
	VIEW
}