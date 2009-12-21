package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class ExportedFk implements SimpleNodeType, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1892651847613896234L;
	private Column column;
	private String fkName;
	private String pkName;

	private String tableName;

	private String tableSchema;

	private String tableCatalog;

	private String columnName;

	private volatile int hashCode;

	public boolean equals(final Object o) {
		if (!(o instanceof ExportedFk)) {
			return false;
		}
		final ExportedFk that = (ExportedFk) o;
		return Equals.eachEquality(Arrays.of(fkName, pkName, column,
				columnName, tableCatalog, tableName, tableSchema), Arrays
				.andOf(that.fkName, that.pkName, that.column, that.columnName,
						that.tableCatalog, that.tableName, that.tableSchema));
	}

	@ParentProperty
	public Column getColumn() {
		return column;
	}

	@KeyProperty
	public String getColumnName() {
		return columnName;
	}

	public String getFkName() {
		return fkName;
	}

	public String getPkName() {
		return pkName;
	}

	@KeyProperty
	public String getTableCatalog() {
		return tableCatalog;
	}

	@KeyProperty
	public String getTableName() {
		return tableName;
	}

	@KeyProperty
	public String getTableSchema() {
		return tableSchema;
	}

	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HashCodes.hashOf(fkName, pkName, column, columnName,
					tableCatalog, tableName, tableSchema);
			hashCode = result;
		}
		return result;
	}

	public void setColumn(final Column column) {
		this.column = column;
	}

	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	public void setFkName(final String fkName) {
		this.fkName = fkName;
	}

	public void setPkName(final String pkName) {
		this.pkName = pkName;
	}

	public void setTableCatalog(final String tableCatalog) {
		this.tableCatalog = tableCatalog;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public void setTableSchema(final String tableSchema) {
		this.tableSchema = tableSchema;
	}
}
