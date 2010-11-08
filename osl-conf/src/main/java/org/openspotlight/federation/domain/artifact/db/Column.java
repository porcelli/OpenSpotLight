/**
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

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name("column")
public class Column implements SimpleNodeType, Serializable {
    private static final long         serialVersionUID = -1495010016475838851L;

    private int                       columnSize;

    private int                       decimalSize;

    private volatile transient String description;

    private volatile transient int    hashCode;

    private String                    name;

    private NullableSqlType           nullable;

    private transient TableArtifact   table;

    private ColumnType                type;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Column)) { return false; }
        final Column that = (Column) o;
        return Equals.eachEquality(Arrays.of(table, name), Arrays.andOf(that.table, that.name));
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getDecimalSize() {
        return decimalSize;
    }

    @KeyProperty
    public String getName() {
        return name;
    }

    public NullableSqlType getNullable() {
        return nullable;
    }

    @ParentProperty
    public TableArtifact getTable() {
        return table;
    }

    public ColumnType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int res = hashCode;
        if (res == 0) {
            res = HashCodes.hashOf(table, name);
            hashCode = res;
        }
        return res;
    }

    public void setColumnSize(final int columnSize) {
        this.columnSize = columnSize;
    }

    public void setDecimalSize(final int decimalSize) {
        this.decimalSize = decimalSize;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setNullable(final NullableSqlType nullable) {
        this.nullable = nullable;
    }

    public void setTable(final TableArtifact table) {
        this.table = table;
    }

    public void setType(final ColumnType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String toString = description;
        if (toString == null) {
            toString = "Column " + name + " " + (table != null ? table.toString() : "no_table ");
            description = toString;
        }
        return toString;
    }
}
