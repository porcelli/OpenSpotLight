package org.openspotlight.federation.data.load.db;

/**
 * Valid column names to be used on a Metadata select statement. For
 * "documentation purposes" this was created as a enum, to make explict
 * (also in compiled versions) the column names needed on select statements.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public enum ColumnsNamesForMetadataSelect {
	/**
	 * The script itself. Should not be returned on data select, but is
	 * mandatory on content select.
	 */
	SQL_CONTENT,
	/**
	 * The script catalog. Should be returned on data select, but not on
	 * content select.
	 */
	CATALOG_NAME,
	/**
	 * The script schema. Should be returned on data select, but not on
	 * content select.
	 */
	SCHEMA_NAME,
	/**
	 * The script name inside database. Should be returned on data select,
	 * but not on content select.
	 */
	NAME,
	/**
	 * Any aditional information for the script. Should be returned on data
	 * select, but not on content select.
	 */
	REMARKS

}