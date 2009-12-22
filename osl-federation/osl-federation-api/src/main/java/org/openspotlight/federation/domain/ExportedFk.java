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
		return Equals.eachEquality(Arrays.of(fkName, column, columnName,
				tableCatalog, tableName, tableSchema), Arrays.andOf(
				that.fkName, that.column, that.columnName, that.tableCatalog,
				that.tableName, that.tableSchema));
	}

	@ParentProperty
	public Column getColumn() {
		return column;
	}

	@KeyProperty
	public String getColumnName() {
		return columnName;
	}

	@KeyProperty
	public String getFkName() {
		return fkName;
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
			result = HashCodes.hashOf(fkName, column, columnName, tableCatalog,
					tableName, tableSchema);
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
