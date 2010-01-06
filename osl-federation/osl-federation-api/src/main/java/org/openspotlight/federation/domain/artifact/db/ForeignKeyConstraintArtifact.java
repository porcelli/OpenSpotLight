package org.openspotlight.federation.domain.artifact.db;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;

import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.persist.annotation.Name;

@Name("database")
public class ForeignKeyConstraintArtifact extends ConstraintArtifact {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3232090373069518849L;
	private String constraintName;

	private String fromCatalogName;

	private String fromSchemaName;

	private String toCatalogName;

	private String toSchemaName;

	private String fromTableName;

	private String fromColumnName;

	private String toTableName;

	private String toColumnName;

	@Override
	public boolean contentEquals(final Artifact other) {
		if (!equals(other)) {
			return false;
		}
		final ForeignKeyConstraintArtifact that = (ForeignKeyConstraintArtifact) other;
		return eachEquality(

		of(fromCatalogName, fromSchemaName, toCatalogName, toSchemaName,
				fromTableName, fromColumnName, toTableName, toColumnName),

		andOf(that.fromCatalogName, that.fromSchemaName, that.toCatalogName,
				that.toSchemaName, that.fromTableName, that.fromColumnName,
				that.toTableName, that.toColumnName));
	}

	@SuppressWarnings("unchecked")
	public boolean equals(final Object o) {
		if (!(o instanceof ForeignKeyConstraintArtifact)) {
			return false;
		}
		final ForeignKeyConstraintArtifact that = (ForeignKeyConstraintArtifact) o;

		return eachEquality(

		of(constraintName, getDatabaseName(), getDatabaseType(),
				getServerName(), getUrl()),

		andOf(that.constraintName, that.getDatabaseName(), that
				.getDatabaseType(), that.getServerName(), that.getUrl()));
	}

	public String getConstraintName() {
		return constraintName;
	}

	public String getFromCatalogName() {
		return fromCatalogName;
	}

	public String getFromColumnName() {
		return fromColumnName;
	}

	public String getFromSchemaName() {
		return fromSchemaName;
	}

	public String getFromTableName() {
		return fromTableName;
	}

	public String getToCatalogName() {
		return toCatalogName;
	}

	public String getToColumnName() {
		return toColumnName;
	}

	public String getToSchemaName() {
		return toSchemaName;
	}

	public String getToTableName() {
		return toTableName;
	}

	public int hashCode() {
		return HashCodes.hashOf(constraintName, getDatabaseName(),
				getDatabaseType(), getServerName(), getUrl());
	}

	public void setConstraintName(final String constraintName) {
		this.constraintName = constraintName;
	}

	public void setFromCatalogName(final String fromCatalogName) {
		this.fromCatalogName = fromCatalogName;
	}

	public void setFromColumnName(final String fromColumnName) {
		this.fromColumnName = fromColumnName;
	}

	public void setFromSchemaName(final String fromSchemaName) {
		this.fromSchemaName = fromSchemaName;
	}

	public void setFromTableName(final String fromTableName) {
		this.fromTableName = fromTableName;
	}

	public void setToCatalogName(final String toCatalogName) {
		this.toCatalogName = toCatalogName;
	}

	public void setToColumnName(final String toColumnName) {
		this.toColumnName = toColumnName;
	}

	public void setToSchemaName(final String toSchemaName) {
		this.toSchemaName = toSchemaName;
	}

	public void setToTableName(final String toTableName) {
		this.toTableName = toTableName;
	}

}
