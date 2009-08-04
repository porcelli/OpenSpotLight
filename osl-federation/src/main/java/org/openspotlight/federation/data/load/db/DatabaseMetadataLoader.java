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

package org.openspotlight.federation.data.load.db;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.util.HashMap;
import java.util.Map;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.Column;
import org.openspotlight.federation.data.impl.TableArtifact;
import org.openspotlight.federation.data.impl.Column.ColumnType;
import org.openspotlight.federation.data.impl.Column.Nullable;
import org.openspotlight.federation.data.load.ArtifactLoader;

/**
 * The {@link DatabaseMetadataLoader} loads common database artifacts. This data
 * loaded will be used by Database {@link ArtifactLoader artifact loaders} to
 * load database data.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface DatabaseMetadataLoader {
    
    /**
     * {@link ColumnDescription} to be used to create new {@link Column column
     * metadata}.
     * 
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static final class ColumnDescription {
        
        private final String columnName;
        
        private final ColumnType type;
        
        private final Nullable nullable;
        private final Integer columnSize;
        private final Integer decimalSize;
        private final int hashCode;
        
        /**
         * Constructor to fill all final fields.
         * 
         * @param columnName
         * @param type
         * @param nullable
         * @param columnSize
         * @param decimalSize
         */
        public ColumnDescription(final String columnName,
                final ColumnType type, final Nullable nullable,
                final Integer columnSize, final Integer decimalSize) {
            this.columnName = columnName;
            this.type = type;
            this.nullable = nullable;
            this.columnSize = columnSize;
            this.decimalSize = decimalSize;
            this.hashCode = hashOf(columnName, type, nullable, columnSize,
                    decimalSize);
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ColumnDescription)) {
                return false;
            }
            final ColumnDescription that = (ColumnDescription) o;
            return eachEquality(of(this.columnName, this.type, this.nullable,
                    this.columnSize, this.decimalSize),
                    andOf(that.columnName, that.type, that.nullable,
                            that.columnSize, that.decimalSize));
        }
        
        /**
         * 
         * @return the column name
         */
        public String getColumnName() {
            return this.columnName;
        }
        
        /**
         * 
         * @return the column size
         */
        public Integer getColumnSize() {
            return this.columnSize;
        }
        
        /**
         * 
         * @return the decimal size
         */
        public Integer getDecimalSize() {
            return this.decimalSize;
        }
        
        /**
         * 
         * @return the nullable
         */
        public Nullable getNullable() {
            return this.nullable;
        }
        
        /**
         * 
         * @return the type
         */
        public ColumnType getType() {
            return this.type;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
    }
    
    /**
     * Valid column names to be used on a Metadata select statement. For
     * "documentation purposes" this was created as a enum, to make explict
     * (also in compiled versions) the column names needed on select statements.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum ColumnsNamesForMetadataSelect {
        /**
         * The script itself
         */
        SQL,
        /**
         * The script catalog
         */
        CATALOG,
        /**
         * The script schema
         */
        SCHEMA,
        /**
         * The script name inside database.
         */
        NAME,
        /**
         * Any aditional information for the script.
         */
        REMARKS
        
    }
    
    /**
     * This inner class describe an script by storing its type, contents and
     * name. From this class, the common database sql will be extracted in a
     * common way in {@link BasicDatabaseMetadataLoader}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static final class ScriptDescription {
        
        private final String catalog;
        private final String schema;
        private final DatabaseType databaseType;
        private final String databaseUrl;
        private final String sql;
        private final String name;
        private final ScriptType type;
        private final String remarks;
        private final int hashCode;
        private volatile String description = null;
        
        /**
         * Constructor to fill the final fields.
         * 
         * @param databaseType
         * @param databaseUrl
         * @param schema
         * @param catalog
         * @param sql
         * @param name
         * @param type
         * @param remarks
         */
        public ScriptDescription(final DatabaseType databaseType,
                final String databaseUrl, final String schema,
                final String catalog, final String sql, final String name,
                final ScriptType type, final String remarks) {
            checkNotNull("sql", sql); //$NON-NLS-1$
            checkNotEmpty("name", name); //$NON-NLS-1$
            checkNotNull("databaseType", databaseType); //$NON-NLS-1$
            checkNotEmpty("databaseUrl", databaseUrl); //$NON-NLS-1$
            checkNotNull("type", type); //$NON-NLS-1$
            this.catalog = catalog;
            this.schema = schema;
            this.sql = sql;
            this.name = name;
            this.type = type;
            this.databaseType = databaseType;
            this.databaseUrl = databaseUrl;
            this.remarks = remarks;
            this.hashCode = hashOf(databaseUrl, databaseType, catalog, schema,
                    name, type);
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ScriptDescription)) {
                return false;
            }
            final ScriptDescription that = (ScriptDescription) o;
            return eachEquality(of(this.databaseUrl, this.databaseType,
                    this.catalog, this.schema, this.name, this.type), andOf(
                    that.databaseUrl, that.databaseType, that.catalog,
                    that.schema, that.name, that.type));
        }
        
        /**
         * 
         * @return the artifact catalog
         */
        public String getCatalog() {
            return this.catalog;
        }
        
        /**
         * 
         * @return the database type
         */
        public DatabaseType getDatabaseType() {
            return this.databaseType;
        }
        
        /**
         * 
         * @return the database url
         */
        public String getDatabaseUrl() {
            return this.databaseUrl;
        }
        
        /**
         * 
         * @return the name of this script
         */
        public String getName() {
            return this.name;
        }
        
        /**
         * 
         * @return aditional information to describe this script
         */
        public String getRemarks() {
            return this.remarks;
        }
        
        /**
         * 
         * @return the artifact schema
         */
        public String getSchema() {
            return this.schema;
        }
        
        /**
         * 
         * @return the script sql
         */
        public String getSql() {
            return this.sql;
        }
        
        /**
         * 
         * @return the {@link ScriptType type of script} for this script
         */
        public ScriptType getType() {
            return this.type;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (this.description == null) {
                this.description = format(
                        "{0}/{1}/{2}/{3}", this.catalog, this.schema, this.type //$NON-NLS-1$
                                .toString().toLowerCase(), this.name);
            }
            return this.description;
        }
        
    }
    
    /**
     * Class with column description to fill the {@link TableArtifact} and
     * {@link Column} on federation metadata.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static final class TableDescription {
        private final String catalog;
        private final String schema;
        private final String tableName;
        private final int hashCode;
        
        private volatile String description = null;
        
        private final Map<String, ColumnDescription> columns = new HashMap<String, ColumnDescription>();
        
        /**
         * Constructor to fnalize all final fields.
         * 
         * @param catalog
         * @param schema
         * @param tableName
         */
        public TableDescription(final String catalog, final String schema,
                final String tableName) {
            super();
            this.catalog = catalog;
            this.schema = schema;
            this.tableName = tableName;
            this.hashCode = hashOf(catalog, schema, tableName);
        }
        
        /**
         * Adds a new column to the internal column map
         * 
         * @param d
         */
        public void addColumn(final ColumnDescription d) {
            this.columns.put(d.getColumnName(), d);
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TableDescription)) {
                return false;
            }
            final TableDescription that = (TableDescription) o;
            return eachEquality(of(this.catalog, this.schema, this.tableName),
                    andOf(that.catalog, that.schema, that.tableName));
        }
        
        /**
         * 
         * @return the catalog
         */
        public String getCatalog() {
            return this.catalog;
        }
        
        /**
         * 
         * @return the column map
         */
        public Map<String, ColumnDescription> getColumns() {
            return this.columns;
        }
        
        /**
         * 
         * @return the schema
         */
        public String getSchema() {
            return this.schema;
        }
        
        /**
         * 
         * @return the table name
         */
        public String getTableName() {
            return this.tableName;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (this.description == null) {
                this.description = format("{0}/{1}/{2}/{3}", this.catalog, //$NON-NLS-1$
                        this.schema, ScriptType.TABLE, this.tableName);
            }
            return this.description;
        }
        
    }
    
    /**
     * 
     * @return description of all types on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadAllTypes()
            throws ConfigurationException;
    
    /**
     * 
     * @return description of all custom types on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadCustomTypes()
            throws ConfigurationException;
    
    /**
     * 
     * @return description of all table creation scripts on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadIndexScripts()
            throws ConfigurationException;
    
    /**
     * 
     * @return description of all procedures on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadProcedures()
            throws ConfigurationException;
    
    /**
     * 
     * This method will load all table hierarchy metadata
     * 
     * @return the loaded metadata from tables and columns
     * 
     * @throws ConfigurationException
     * 
     */
    public abstract TableDescription[] loadTableMetadata()
            throws ConfigurationException;
    
    /**
     * 
     * @return description of all table creation scripts on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadTableScripts()
            throws ConfigurationException;
    
    /**
     * 
     * @return description of all triggers on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadTriggers()
            throws ConfigurationException;
    
    /**
     * 
     * @return description of all view creation scripts on that database
     * @throws ConfigurationException
     */
    public abstract ScriptDescription[] loadViewScripts()
            throws ConfigurationException;
    
}
