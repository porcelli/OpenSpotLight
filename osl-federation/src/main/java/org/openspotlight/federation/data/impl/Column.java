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
package org.openspotlight.federation.data.impl;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.sql.Types;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * Metadata for database columns.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
@StaticMetadata(keyPropertyName = "name", keyPropertyType = String.class, validParentTypes = { TableArtifact.class }, propertyNames = {
        "type", "nullable" }, propertyTypes = { Column.ColumnType.class,
        Column.Nullable.class })
public class Column implements ConfigurationNode {
    
    /**
     * Enum for column types from {@link Types} constants.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum ColumnType {
        
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
    
    /**
     * SQL nullable type.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum Nullable {
        /**
         * Can be null.
         */
        NULL,
        /**
         * Can not be null.
         */
        NOT_NULL,
        /**
         * Wasn't possible to find if it is nullable or not
         */
        DONT_KNOW
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 814911484835529535L;
    
    private static final String NULLABLE = "nullable"; //$NON-NLS-1$
    
    private static final String TYPE = "type"; //$NON-NLS-1$
    
    private final InstanceMetadata instanceMetadata;
    
    /**
     * Create a column inside a table
     * 
     * @param table
     * @param columnName
     */
    public Column(final TableArtifact table, final String columnName) {
        this.instanceMetadata = createWithKeyProperty(this, table, columnName);
        checkCondition("noColumn", //$NON-NLS-1$
                table.getColumnByName(columnName) == null);
        table.getInstanceMetadata().addChild(this);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public int compareTo(final ConfigurationNode o) {
        return this.instanceMetadata.compare(this, o);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        return this.instanceMetadata.equals(obj);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }
    
    /**
     * 
     * @return the name
     */
    public String getName() {
        return (String) this.instanceMetadata.getKeyPropertyValue();
    }
    
    /**
     * 
     * @return nullable property
     */
    public Nullable getNullable() {
        return this.instanceMetadata.getProperty(NULLABLE);
    }
    
    /**
     * 
     * @return the column type
     */
    public ColumnType getType() {
        return this.instanceMetadata.getProperty(TYPE);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return this.instanceMetadata.hashCode();
    }
    
    /**
     * Set nullable property.
     * 
     * @param nullable
     */
    public void setNullable(final Nullable nullable) {
        this.instanceMetadata.setProperty(NULLABLE, nullable);
    }
    
    /**
     * Set the column type
     * 
     * @param type
     */
    public void setType(final ColumnType type) {
        this.instanceMetadata.setProperty(TYPE, type);
    }
    
}
