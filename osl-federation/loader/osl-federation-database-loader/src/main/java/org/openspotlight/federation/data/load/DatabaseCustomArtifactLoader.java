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

package org.openspotlight.federation.data.load;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.Column;
import org.openspotlight.federation.data.impl.ColumnType;
import org.openspotlight.federation.data.impl.NullableSqlType;
import org.openspotlight.federation.data.impl.RoutineArtifact;
import org.openspotlight.federation.data.impl.RoutineParameter;
import org.openspotlight.federation.data.impl.TableArtifact;
import org.openspotlight.federation.data.impl.ViewArtifact;
import org.openspotlight.federation.data.impl.RoutineArtifact.RoutineType;
import org.openspotlight.federation.data.impl.RoutineParameter.RoutineParameterType;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.domain.ArtifactMapping;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.CustomArtifact;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.loader.ArtifactLoader;

/**
 * Artifact loader responsible to load information from database using jdbc metadata. It can load information from tables, views
 * and routines using just the jdbc driver. This loades does not get {@link StreamArtifact} information. It just get information
 * for {@link CustomArtifact}. This loader has some different behaviors also, such as firing table changes when its columns
 * changes, and also when some view columns changes, the stream artifact related to it changes also.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class DatabaseCustomArtifactLoader extends AbstractArtifactLoader {

    /**
     * {@link ColumnDescription} to be used to create new {@link Column column metadata}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static final class ColumnDescription {

        private final String          columnName;

        private final Integer         columnSize;

        private final Integer         decimalSize;
        private final int             hashCode;
        private final NullableSqlType nullable;
        private final ColumnType      type;

        /**
         * Constructor to fill all final fields.
         * 
         * @param columnName
         * @param type
         * @param nullable
         * @param columnSize
         * @param decimalSize
         */
        public ColumnDescription(
                                  final String columnName, final ColumnType type, final NullableSqlType nullable,
                                  final Integer columnSize, final Integer decimalSize ) {
            this.columnName = columnName;
            this.type = type;
            this.nullable = nullable;
            this.columnSize = columnSize;
            this.decimalSize = decimalSize;
            this.hashCode = hashOf(columnName, type, nullable, columnSize, decimalSize);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings( "unchecked" )
        @Override
        public boolean equals( final Object o ) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ColumnDescription)) {
                return false;
            }
            final ColumnDescription that = (ColumnDescription)o;
            return eachEquality(of(this.columnName, this.type, this.nullable, this.columnSize, this.decimalSize),
                                andOf(that.columnName, that.type, that.nullable, that.columnSize, that.decimalSize));
        }

        /**
         * @return the column name
         */
        public String getColumnName() {
            return this.columnName;
        }

        /**
         * @return the column size
         */
        public Integer getColumnSize() {
            return this.columnSize;
        }

        /**
         * @return the decimal size
         */
        public Integer getDecimalSize() {
            return this.decimalSize;
        }

        /**
         * @return the nullable
         */
        public NullableSqlType getNullable() {
            return this.nullable;
        }

        /**
         * @return the type
         */
        public ColumnType getType() {
            return this.type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }

    }

    protected static class DatabaseCustomArtifactInternalLoader {

        public Map<String, DatabaseItemDescription> loadDatabaseMetadata( final DatabaseMetaData metadata )
            throws ConfigurationException {
            try {
                final Map<String, TableDescription> tableMetadata = this.loadTableMetadata(metadata);
                final Map<String, RoutineDescription> routineMetadata = this.loadRoutineMetadata(metadata);
                final Map<String, DatabaseItemDescription> result = new HashMap<String, DatabaseItemDescription>(
                                                                                                                 tableMetadata.size()
                                                                                                                 + routineMetadata.size());
                result.putAll(tableMetadata);
                result.putAll(routineMetadata);
                return result;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }

        @SuppressWarnings( "boxing" )
        private Map<String, RoutineDescription> loadRoutineMetadata( final DatabaseMetaData metadata ) throws SQLException {

            final ResultSet rs = metadata.getProcedures(null, null, null);
            final Map<String, RoutineDescription> result = new HashMap<String, RoutineDescription>(rs.getFetchSize());
            while (rs.next()) {
                final String catalog = rs.getString("PROCEDURE_CAT"); //$NON-NLS-1$
                final String schema = rs.getString("PROCEDURE_SCHEM"); //$NON-NLS-1$
                final String name = rs.getString("PROCEDURE_NAME"); //$NON-NLS-1$
                final String remarks = rs.getString("REMARKS"); //$NON-NLS-1$
                final int type = rs.getInt("PROCEDURE_TYPE"); //$NON-NLS-1$
                final RoutineDescription newMetadata = new RoutineDescription(catalog, schema, name, remarks,
                                                                              RoutineType.getTypeByInt(type));
                final ResultSet columnsRs = metadata.getProcedureColumns(catalog, schema, name, null);
                while (columnsRs.next()) {
                    final String columnName = columnsRs.getString("COLUMN_NAME"); //$NON-NLS-1$
                    final int columnType = columnsRs.getInt("DATA_TYPE"); //$NON-NLS-1$
                    final int routineType = columnsRs.getInt("COLUMN_TYPE"); //$NON-NLS-1$
                    final int length = columnsRs.getInt("LENGTH"); //$NON-NLS-1$
                    final int scale = columnsRs.getInt("SCALE"); //$NON-NLS-1$
                    final int nullable = columnsRs.getInt("NULLABLE"); //$NON-NLS-1$
                    final RoutineParameterDescription parameter = new RoutineParameterDescription(
                                                                                                  columnName,
                                                                                                  ColumnType.getTypeByInt(columnType),
                                                                                                  NullableSqlType.getNullableByInt(nullable),
                                                                                                  length,
                                                                                                  scale,
                                                                                                  RoutineParameterType.getTypeByInt(routineType));
                    newMetadata.addParameter(parameter);
                }
                result.put(newMetadata.toString(), newMetadata);
            }

            return result;
        }

        @SuppressWarnings( "boxing" )
        private Map<String, TableDescription> loadTableMetadata( final DatabaseMetaData metadata ) throws SQLException {
            final ResultSet rs = metadata.getColumns(null, null, null, null);
            final Map<String, TableDescription> tableMetadata = new HashMap<String, TableDescription>(rs.getFetchSize());
            while (rs.next()) {
                final String catalog = rs.getString("TABLE_CAT"); //$NON-NLS-1$
                final String schema = rs.getString("TABLE_SCHEM"); //$NON-NLS-1$
                final String tableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
                final String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
                final ColumnType type = ColumnType.getTypeByInt(rs.getInt("DATA_TYPE")); //$NON-NLS-1$
                final NullableSqlType nullable = NullableSqlType.getNullableByInt(rs.getInt("NULLABLE")); //$NON-NLS-1$
                final Integer columnSize = rs.getInt("COLUMN_SIZE"); //$NON-NLS-1$
                final Integer decimalSize = rs.getInt("DECIMAL_DIGITS"); //$NON-NLS-1$
                final ResultSet tableResultSet = metadata.getTables(catalog, schema, tableName, of("TABLE", "VIEW")); //$NON-NLS-1$//$NON-NLS-2$
                while (tableResultSet.next()) {
                    final String tableType = tableResultSet.getString("TABLE_TYPE"); //$NON-NLS-1$
                    TableDescription desc;
                    if ("VIEW".equals(tableType)) { //$NON-NLS-1$
                        desc = new ViewDescription(catalog, schema, tableName);
                    } else {
                        desc = new TableDescription(catalog, schema, tableName);
                    }

                    if (tableMetadata.containsKey(desc.toString())) {
                        desc = tableMetadata.get(desc.toString());
                    } else {
                        tableMetadata.put(desc.toString(), desc);
                    }
                    if (!desc.getColumns().containsKey(columnName)) {
                        final ColumnDescription colDesc = new ColumnDescription(columnName, type, nullable, columnSize,
                                                                                decimalSize);
                        desc.addColumn(colDesc);
                    }
                }
            }
            return tableMetadata;
        }

    }

    /**
     * {@link GlobalExecutionContext} used on this {@link ArtifactLoader}. This {@link GlobalExecutionContext} also fire changes
     * on {@link StreamArtifact} related to changed views and fires changes on {@link TableArtifact tables } related to changed
     * {@link Column columns}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    protected static class DatabaseCustomGlobalContext extends DefaultGlobalExecutionContext {

        private final DatabaseCustomArtifactInternalLoader internalLoader = new DatabaseCustomArtifactInternalLoader();

        private final AggregateNodesListener               listener       = new AggregateNodesListener();

        private Map<String, DatabaseItemDescription>       loadedMetadata = null;

        /**
         * This method searchs changed {@link Column columns} and fires {@link ItemChangeEvent change events} on the related
         * {@link TableArtifact tables}.
         * 
         * @param bundle
         */
        private void fireChangeOnChangedTables( final ArtifactSource bundle ) {
            final Set<ConfigurationNode> allChanges = new HashSet<ConfigurationNode>();
            allChanges.addAll(this.listener.getChangedNodes());
            allChanges.addAll(this.listener.getInsertedNodes());
            allChanges.addAll(this.listener.getRemovedNodes());

            for (final ConfigurationNode node : allChanges) {
                if (node instanceof Column) {
                    final Column column = (Column)node;
                    final ConfigurationNode parent = column.getInstanceMetadata().getDefaultParent();
                    if (parent instanceof TableArtifact) {
                        final TableArtifact t = (TableArtifact)parent;
                        if (!bundle.getInstanceMetadata().getSharedData().getDirtyNodes().contains(t)) {
                            bundle.getInstanceMetadata().getSharedData().fireNodeChange(t, t);
                        }
                    }
                }
            }
        }

        /**
         * This method searchs changed {@link ViewArtifact} and fires {@link ItemChangeEvent change events} on the related
         * {@link StreamArtifact view stream artifacts}.
         * 
         * @param bundle
         */
        private void fireChangeOnChangedViewStreams( final ArtifactSource bundle ) {
            final Set<ConfigurationNode> changedNodes = new HashSet<ConfigurationNode>();
            changedNodes.addAll(this.listener.getChangedNodes());
            for (final ConfigurationNode node : changedNodes) {
                if (node instanceof ViewArtifact) {
                    final ViewArtifact view = (ViewArtifact)node;
                    final ConfigurationNode parent = view.getInstanceMetadata().getDefaultParent();
                    StreamArtifact stream = null;
                    if (parent instanceof ArtifactSource) {
                        final ArtifactSource b = (ArtifactSource)parent;
                        stream = b.getStreamArtifactByName(view.getRelativeName());
                    } else if (parent instanceof Group) {
                        final Group g = (Group)parent;
                        stream = g.getStreamArtifactByName(view.getRelativeName());
                    } else {
                        logAndReturn(new IllegalStateException(
                                                               format("Unexpected type for bundle parent: {0}", parent.getClass())));
                    }
                    if (stream != null) {
                        if (!bundle.getInstanceMetadata().getSharedData().getDirtyNodes().contains(stream)) {
                            stream.getInstanceMetadata().getSharedData().fireNodeChange(stream, stream);
                        }
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public synchronized Set<String> getAllArtifactNames( final ArtifactSource bundle,
                                                             final ArtifactMapping mapping ) throws ConfigurationException {
            if (!(bundle instanceof DbArtifactSource)) {
                return emptySet();
            }

            try {
                final Set<String> artifactNames = new HashSet<String>();
                if (this.loadedMetadata == null) {
                    final DbArtifactSource dbBundle = (DbArtifactSource)bundle;
                    this.loadMetadata(dbBundle);
                }
                for (final Map.Entry<String, DatabaseItemDescription> entry : this.loadedMetadata.entrySet()) {
                    if (entry.getKey().startsWith(mapping.getRelative())) {
                        final String name = entry.getKey().substring(mapping.getRelative().length());
                        artifactNames.add(name);
                    }
                }
                return artifactNames;
            } catch (final Exception e) {
                logAndReturnNew(e, ConfigurationException.class);
            }
            return emptySet();
        }

        /**
         * @return the loaded metadata
         */
        public Map<String, DatabaseItemDescription> getLoadedMetadata() {
            return this.loadedMetadata;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void globalExecutionAboutToStart( final ArtifactSource bundle ) {
            bundle.getInstanceMetadata().getSharedData().addNodeListenerForAGivenType(this.listener, Column.class);
            bundle.getInstanceMetadata().getSharedData().addNodeListenerForAGivenType(this.listener, ViewArtifact.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void globalExecutionFinished( final ArtifactSource bundle ) {
            bundle.getInstanceMetadata().getSharedData().removeNodeListener(this.listener);
            this.fireChangeOnChangedTables(bundle);

            this.fireChangeOnChangedViewStreams(bundle);

            this.listener.clearData();
        }

        /**
         * This method loads metadata from the given bundle
         * 
         * @param dbBundle
         * @throws SQLException
         */
        private void loadMetadata( final DbArtifactSource dbBundle ) throws SQLException {
            Connection conn = null;
            try {
                conn = createConnection(dbBundle);
                final DatabaseMetaData metadata = conn.getMetaData();
                this.loadedMetadata = this.internalLoader.loadDatabaseMetadata(metadata);

            } catch (final Exception e) {
                logAndReturnNew(e, ConfigurationException.class);
            } finally {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer withThreadPoolSize( final ArtifactSource bundle ) {
            final Integer defaultValue = super.withThreadPoolSize(bundle);
            if (!(bundle instanceof DbArtifactSource)) {
                return defaultValue;
            }
            final DbArtifactSource dbBundle = (DbArtifactSource)bundle;
            final Integer maxConnections = dbBundle.getMaxConnections();
            if (maxConnections != null && maxConnections.compareTo(defaultValue) < 0) {
                return maxConnections;
            }
            return defaultValue;
        }

    }

    /**
     * {@link ThreadExecutionContext} used to load database artifacts from jdbc driver.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    protected static class DatabaseCustomThreadContext extends DefaultThreadExecutionContext {

        /**
         * {@inheritDoc}
         */
        public byte[] loadArtifactOrReturnNullToIgnore( final ArtifactSource bundle,
                                                        final ArtifactMapping mapping,
                                                        final String artifactName,
                                                        final GlobalExecutionContext globalContext ) {
            final DatabaseCustomGlobalContext context = (DatabaseCustomGlobalContext)globalContext;
            final String completeName = mapping.getRelative() + artifactName;
            final DatabaseItemDescription metadata = context.getLoadedMetadata().get(completeName);

            if (metadata instanceof TableDescription) {
                this.loadTableMetadata(bundle, metadata);
            } else if (metadata instanceof RoutineDescription) {
                this.loadRoutineMetadata(bundle, metadata);
            }
            return null;
        }

        /**
         * This method loads routine metadata and also remove unused metadata related to this {@link DatabaseItemDescription}.
         * 
         * @param bundle
         * @param metadata
         */
        private void loadRoutineMetadata( final ArtifactSource bundle,
                                          final DatabaseItemDescription metadata ) {
            final RoutineDescription routineMetadata = (RoutineDescription)metadata;
            RoutineArtifact routine = (RoutineArtifact)bundle.getCustomArtifactByName(metadata.toString());
            if (routine == null) {
                routine = new RoutineArtifact(bundle, metadata.toString());

            }
            for (final String columnName : routine.getRoutineParameterNames()) {
                if (!routineMetadata.getRoutineParameters().containsKey(columnName)) {
                    final RoutineParameter column = routine.getRoutineParameterByName(columnName);
                    routine.removeRoutineParameter(column);
                }
            }

            for (final RoutineParameterDescription colDesc : routineMetadata.getRoutineParameters().values()) {
                RoutineParameter column = routine.getRoutineParameterByName(colDesc.getName());
                if (column == null) {
                    column = new RoutineParameter(routine, colDesc.getName());
                }
                column.setColumnSize(colDesc.getColumnSize());
                column.setParameterType(colDesc.getParameterType());
                column.setDecimalSize(colDesc.getDecimalSize());
                column.setNullable(colDesc.getNullable());
                column.setType(colDesc.getType());
            }
        }

        /**
         * This method loads table metadata and also remove unused metadata related to this {@link DatabaseItemDescription}.
         * 
         * @param bundle
         * @param metadata
         */
        private void loadTableMetadata( final ArtifactSource bundle,
                                        final DatabaseItemDescription metadata ) {
            final TableDescription tableMetadata = (TableDescription)metadata;
            TableArtifact table = (TableArtifact)bundle.getCustomArtifactByName(metadata.toString());
            if (table == null) {
                if (tableMetadata instanceof ViewDescription) {
                    table = new ViewArtifact(bundle, metadata.toString());
                } else {
                    table = new TableArtifact(bundle, metadata.toString());
                }
            }
            final List<Column> columnsToRemove = new ArrayList<Column>();
            for (final String columnName : table.getColumnNames()) {
                if (!tableMetadata.getColumns().containsKey(columnName)) {
                    final Column column = table.getColumnByName(columnName);
                    columnsToRemove.add(column);
                }
            }
            for (final Column columnToRemove : columnsToRemove) {
                table.removeColumn(columnToRemove);
            }
            for (final ColumnDescription colDesc : tableMetadata.getColumns().values()) {
                Column column = table.getColumnByName(colDesc.getColumnName());
                if (column == null) {
                    column = new Column(table, colDesc.getColumnName());
                }
                column.setColumnSize(colDesc.getColumnSize());
                column.setDecimalSize(colDesc.getDecimalSize());
                column.setNullable(colDesc.getNullable());
                column.setType(colDesc.getType());
            }
        }

    }

    /**
     * This is just a marker class to describe database items
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private abstract static class DatabaseItemDescription {
        //
    }

    /**
     * Class with description for procedures and triggers.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static class RoutineDescription extends DatabaseItemDescription {
        private final String                                     catalog;
        protected final Map<String, RoutineParameterDescription> columns     = new HashMap<String, RoutineParameterDescription>();
        private volatile String                                  description = null;
        private final int                                        hashCode;
        private final String                                     name;
        private final String                                     remarks;

        private final String                                     schema;

        private final RoutineType                                type;

        /**
         * Constructor to initialize final fields.
         * 
         * @param catalog
         * @param schema
         * @param name
         * @param remarks
         * @param type
         * @param hashCode
         * @param description
         */
        @SuppressWarnings( "synthetic-access" )
        public RoutineDescription(
                                   final String catalog, final String schema, final String name, final String remarks,
                                   final RoutineType type ) {
            this.catalog = catalog;
            this.schema = schema;
            this.name = name;
            this.remarks = remarks;
            this.type = type;
            this.hashCode = hashOf(catalog, schema, name, type);
        }

        /**
         * Adds a new parameter to the internal parameter map
         * 
         * @param d
         */
        public void addParameter( final RoutineParameterDescription d ) {
            this.columns.put(d.getName(), d);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals( final Object o ) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RoutineDescription)) {
                return false;
            }
            final RoutineDescription that = (RoutineDescription)o;
            return eachEquality(of(this.getClass(), this.catalog, this.schema, this.name, this.type), andOf(that.getClass(),
                                                                                                            that.catalog,
                                                                                                            that.schema,
                                                                                                            that.name, that.type));
        }

        /**
         * @return catalog name
         */
        public String getCatalog() {
            return this.catalog;
        }

        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return remarks
         */
        public String getRemarks() {
            return this.remarks;
        }

        /**
         * @return the column map
         */
        public Map<String, RoutineParameterDescription> getRoutineParameters() {
            return this.columns;
        }

        /**
         * @return schema name
         */
        public String getSchema() {
            return this.schema;
        }

        /**
         * @return the type
         */
        public RoutineType getType() {
            return this.type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (this.description == null) {
                if (this.catalog != null) {
                    this.description = format("{0}/{1}/{2}/{3}", //$NON-NLS-1$
                                              this.schema, this.type, this.catalog, this.name);
                } else {
                    this.description = format("{0}/{1}/{2}", //$NON-NLS-1$
                                              this.schema, this.type, this.name);

                }
            }
            return this.description;
        }
    }

    private static class RoutineParameterDescription {
        private final String               columnName;

        private final Integer              columnSize;

        private final Integer              decimalSize;
        private final int                  hashCode;
        private final NullableSqlType      nullable;
        private final RoutineParameterType parameterType;
        private final ColumnType           type;

        /**
         * Constructor to fill all final fields.
         * 
         * @param columnName
         * @param type
         * @param nullable
         * @param columnSize
         * @param decimalSize
         * @param parameterType
         */
        public RoutineParameterDescription(
                                            final String columnName, final ColumnType type, final NullableSqlType nullable,
                                            final Integer columnSize, final Integer decimalSize,
                                            final RoutineParameterType parameterType ) {
            this.columnName = columnName;
            this.type = type;
            this.nullable = nullable;
            this.columnSize = columnSize;
            this.parameterType = parameterType;
            this.decimalSize = decimalSize;
            this.hashCode = hashOf(columnName, type, nullable, columnSize, decimalSize, parameterType);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings( "unchecked" )
        @Override
        public boolean equals( final Object o ) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RoutineParameter)) {
                return false;
            }
            final RoutineParameterDescription that = (RoutineParameterDescription)o;
            return eachEquality(of(this.columnName, this.type, this.nullable, this.columnSize, this.decimalSize,
                                   this.parameterType), andOf(that.columnName, that.type, that.nullable, that.columnSize,
                                                              that.decimalSize, that.parameterType));
        }

        /**
         * @return the column size
         */
        public Integer getColumnSize() {
            return this.columnSize;
        }

        /**
         * @return the decimal size
         */
        public Integer getDecimalSize() {
            return this.decimalSize;
        }

        /**
         * @return the column name
         */
        public String getName() {
            return this.columnName;
        }

        /**
         * @return the nullable
         */
        public NullableSqlType getNullable() {
            return this.nullable;
        }

        /**
         * @return the parameter type
         */
        public RoutineParameterType getParameterType() {
            return this.parameterType;
        }

        /**
         * @return the type
         */
        public ColumnType getType() {
            return this.type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }

    }

    /**
     * Class with column description to fill the {@link TableArtifact} and {@link Column} on federation metadata.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static class TableDescription extends DatabaseItemDescription {
        protected final String                         catalog;
        protected final Map<String, ColumnDescription> columns     = new HashMap<String, ColumnDescription>();
        protected volatile String                      description = null;
        protected final int                            hashCode;

        protected final String                         schema;

        protected final String                         tableName;

        /**
         * Constructor to initialize all final fields.
         * 
         * @param catalog
         * @param schema
         * @param tableName
         */
        @SuppressWarnings( "synthetic-access" )
        public TableDescription(
                                 final String catalog, final String schema, final String tableName ) {
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
        public void addColumn( final ColumnDescription d ) {
            this.columns.put(d.getColumnName(), d);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals( final Object o ) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TableDescription)) {
                return false;
            }
            final TableDescription that = (TableDescription)o;
            return eachEquality(of(this.getClass(), this.catalog, this.schema, this.tableName), andOf(that.getClass(),
                                                                                                      that.catalog, that.schema,
                                                                                                      that.tableName));
        }

        /**
         * @return the catalog
         */
        public String getCatalog() {
            return this.catalog;
        }

        /**
         * @return the column map
         */
        public Map<String, ColumnDescription> getColumns() {
            return this.columns;
        }

        /**
         * @return the schema
         */
        public String getSchema() {
            return this.schema;
        }

        /**
         * @return the table name
         */
        public String getTableName() {
            return this.tableName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (this.description == null) {
                if (this.catalog != null) {
                    this.description = format("{0}/{1}/{2}/{3}", //$NON-NLS-1$
                                              this.schema, ScriptType.TABLE, this.catalog, this.tableName);
                } else {
                    this.description = format("{0}/{1}/{2}", //$NON-NLS-1$
                                              this.schema, ScriptType.TABLE, this.tableName);

                }
            }
            return this.description;
        }

    }

    /**
     * Class with column description to fill the {@link TableArtifact} and {@link Column} on federation metadata for Table views.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static final class ViewDescription extends TableDescription {

        /**
         * Constructor to initialize all final fields.
         * 
         * @param catalog
         * @param schema
         * @param tableName
         */
        public ViewDescription(
                                final String catalog, final String schema, final String tableName ) {
            super(catalog, schema, tableName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (this.description == null) {
                if (this.catalog != null) {
                    this.description = format("{0}/{1}/{2}/{3}", //$NON-NLS-1$
                                              this.schema, ScriptType.VIEW, this.catalog, this.tableName);
                } else {
                    this.description = format("{0}/{1}/{2}", //$NON-NLS-1$
                                              this.schema, ScriptType.VIEW, this.tableName);

                }
            }
            return this.description;
        }

    }

    @Override
    protected GlobalExecutionContext createGlobalExecutionContext() {
        return new DatabaseCustomGlobalContext();
    }

    @Override
    protected ThreadExecutionContext createThreadExecutionContext() {
        return new DatabaseCustomThreadContext();
    }

}
