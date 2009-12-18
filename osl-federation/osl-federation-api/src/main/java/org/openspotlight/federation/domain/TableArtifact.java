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
package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name("database")
public class TableArtifact extends DatabaseCustomArtifact implements
		SimpleNodeType, Serializable {
	private static final long serialVersionUID = -4527063248944852023L;

	private String tableName;
	private String catalogName;
	private String schemaName;
	private Set<Column> columns = new HashSet<Column>();

	@Override
	public boolean contentEquals(final Artifact other) {
		if (!(other instanceof TableArtifact)) {
			return false;
		}
		final TableArtifact that = (TableArtifact) other;
		return Equals.eachEquality(getColumns(), that.getColumns());
	}

	public String getCatalogName() {
		return catalogName;
	}

	public Set<Column> getColumns() {
		return columns;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void loadProperties() {
		final StringTokenizer tok = new StringTokenizer(
				getArtifactCompleteName(), "/"); //$NON-NLS-1$
		if (tok.countTokens() == 4) {
			setSchemaName(tok.nextToken());
			tok.nextToken();// puts away its table type
			setCatalogName(tok.nextToken());
			setTableName(tok.nextToken());
		} else {

			setSchemaName(tok.nextToken());
			tok.nextToken();// puts away its table type
			setTableName(tok.nextToken());
		}
	}

	public void setCatalogName(final String catalogName) {
		this.catalogName = catalogName;
	}

	public void setColumns(final Set<Column> columns) {
		this.columns = columns;
	}

	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

}
