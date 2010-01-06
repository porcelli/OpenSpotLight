package org.openspotlight.federation.domain.artifact.db;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;

import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.artifact.Artifact;

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

	public int hashCode() {
		return HashCodes.hashOf(constraintName, getDatabaseName(),
				getDatabaseType(), getServerName(), getUrl());
	}

}
