/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
