package org.openspotlight.federation.domain.artifact.db;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;

import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.artifact.Artifact;

public class PrimaryKeyConstraintArtifact extends ConstraintArtifact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3232090373069518849L;
	private String constraintName;
	private String catalogName;
	private String schemaName;

	private String tableName;
	private String columnName;

	@Override
	public boolean contentEquals(final Artifact other) {
		if (!equals(other)) {
			return false;
		}
		final PrimaryKeyConstraintArtifact that = (PrimaryKeyConstraintArtifact) other;
		return eachEquality(of(tableName, columnName), andOf(that.tableName,
				that.columnName));
	}

	@SuppressWarnings("unchecked")
	public boolean equals(final Object o) {
		if (!(o instanceof PrimaryKeyConstraintArtifact)) {
			return false;
		}
		final PrimaryKeyConstraintArtifact that = (PrimaryKeyConstraintArtifact) o;

		return eachEquality(
				of(constraintName, catalogName, schemaName, getDatabaseName(),
						getDatabaseType(), getServerName(), getUrl()),
				andOf(of(that.constraintName, that.catalogName,
						that.schemaName, that.getDatabaseName(), that
								.getDatabaseType(), that.getServerName(), that
								.getUrl())));
	}

	public String getCatalogName() {
		return catalogName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getConstraintName() {
		return constraintName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public int hashCode() {
		return HashCodes
				.hashOf(constraintName, catalogName, schemaName,
						getDatabaseName(), getDatabaseType(), getServerName(),
						getUrl());
	}

	public void setCatalogName(final String catalogName) {
		this.catalogName = catalogName;
	}

	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	public void setConstraintName(final String constraintName) {
		this.constraintName = constraintName;
	}

	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

}
