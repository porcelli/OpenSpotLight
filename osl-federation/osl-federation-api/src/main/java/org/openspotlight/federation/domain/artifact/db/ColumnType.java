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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum for column types from {@link Types} constants.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("boxing")
public enum ColumnType {
    
    /**
     * Enum for sql type BIT.
     */
    BIT(Types.BIT),
    /**
     * Enum for sql type TINYINT.
     */
    TINYINT(Types.TINYINT),
    /**
     * Enum for sql type SMALLINT.
     */
    SMALLINT(Types.SMALLINT),
    /**
     * Enum for sql type INTEGER.
     */
    INTEGER(Types.INTEGER),
    /**
     * Enum for sql type BIGINT.
     */
    BIGINT(Types.BIGINT),
    /**
     * Enum for sql type FLOAT.
     */
    FLOAT(Types.FLOAT),
    /**
     * Enum for sql type REAL.
     */
    REAL(Types.REAL),
    /**
     * Enum for sql type DOUBLE.
     */
    DOUBLE(Types.DOUBLE),
    /**
     * Enum for sql type NUMERIC.
     */
    NUMERIC(Types.NUMERIC),
    /**
     * Enum for sql type DECIMAL.
     */
    DECIMAL(Types.DECIMAL),
    /**
     * Enum for sql type CHAR.
     */
    CHAR(Types.CHAR),
    /**
     * Enum for sql type VARCHAR.
     */
    VARCHAR(Types.VARCHAR),
    /**
     * Enum for sql type LONGVARCHAR.
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    /**
     * Enum for sql type DATE.
     */
    DATE(Types.DATE),
    /**
     * Enum for sql type TIME.
     */
    TIME(Types.TIME),
    /**
     * Enum for sql type TIMESTAMP.
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     * Enum for sql type BINARY.
     */
    BINARY(Types.BINARY),
    /**
     * Enum for sql type VARBINARY.
     */
    VARBINARY(Types.VARBINARY),
    /**
     * Enum for sql type LONGVARBINARY.
     */
    LONGVARBINARY(Types.LONGVARBINARY),
    /**
     * Enum for sql type NULL.
     */
    NULL(Types.NULL),
    /**
     * Enum for sql type OTHER.
     */
    OTHER(Types.OTHER),
    /**
     * Enum for sql type JAVA_OBJECT.
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /**
     * Enum for sql type DISTINCT.
     */
    DISTINCT(Types.DISTINCT),
    /**
     * Enum for sql type STRUCT.
     */
    STRUCT(Types.STRUCT),
    /**
     * Enum for sql type ARRAY.
     */
    ARRAY(Types.ARRAY),
    /**
     * Enum for sql type BLOB.
     */
    BLOB(Types.BLOB),
    /**
     * Enum for sql type CLOB.
     */
    CLOB(Types.CLOB),
    /**
     * Enum for sql type REF.
     */
    REF(Types.REF),
    /**
     * Enum for sql type DATALINK.
     */
    DATALINK(Types.DATALINK),
    /**
     * Enum for sql type BOOLEAN.
     */
    BOOLEAN(Types.BOOLEAN);
    
    /**
     * Internal cache
     */
    private static final Map<Integer, ColumnType> typesCache = new HashMap<Integer, ColumnType>();
    static {
        for (final ColumnType t : values()) {
            typesCache.put(t.getSqlTypeValue(), t);
        }
    }
    
    /**
     * Static factory method
     * 
     * @param sqlType
     * @return the correct column type by sql int constant
     */
    public static ColumnType getTypeByInt(final int sqlType) {
        return typesCache.get(sqlType);
    }
    
    private final int sqlTypeValue;
    
    private ColumnType(final int sqlTypeValue) {
        this.sqlTypeValue = sqlTypeValue;
    }
    
    /**
     * 
     * @return the int value equivalent to {@link Types} constants
     */
    public int getSqlTypeValue() {
        return this.sqlTypeValue;
    }
    
}