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
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Column;
import org.openspotlight.federation.data.impl.ColumnType;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.NullableSqlType;
import org.openspotlight.federation.data.impl.RoutineArtifact;
import org.openspotlight.federation.data.impl.RoutineParameter;
import org.openspotlight.federation.data.impl.TableArtifact;
import org.openspotlight.federation.data.impl.ViewArtifact;
import org.openspotlight.federation.data.impl.RoutineArtifact.RoutineType;
import org.openspotlight.federation.data.impl.RoutineParameter.RoutineParameterType;
import org.openspotlight.federation.data.load.db.ScriptType;

public class DatabaseCustomArtifactLoader extends AbstractArtifactLoader {

	protected static class DatabaseCustomThreadContext extends
			DefaultThreadExecutionContext {

		public byte[] loadArtifactOrReturnNullToIgnore(Bundle bundle,
				ArtifactMapping mapping, String artifactName,
				GlobalExecutionContext globalContext) throws Exception {
			DatabaseCustomGlobalContext context = (DatabaseCustomGlobalContext) globalContext;
			String completeName = mapping.getRelative() + artifactName;
			DatabaseItemDescription metadata = context.getLoadedMetadata().get(
					completeName);

			if (metadata instanceof TableDescription) {
				TableDescription tableMetadata = (TableDescription) metadata;
				TableArtifact table = (TableArtifact) bundle
						.getCustomArtifactByName(metadata.toString());
				if (table == null) {
					if (tableMetadata instanceof ViewDescription) {
						table = new ViewArtifact(bundle, metadata.toString());
					} else {
						table = new TableArtifact(bundle, metadata.toString());
					}
				}
				for (final String columnName : table.getColumnNames()) {
					if (!tableMetadata.getColumns().containsKey(columnName)) {
						final Column column = table.getColumnByName(columnName);
						table.removeColumn(column);
					}
				}

				for (final ColumnDescription colDesc : tableMetadata
						.getColumns().values()) {
					Column column = table.getColumnByName(colDesc
							.getColumnName());
					if (column == null) {
						column = new Column(table, colDesc.getColumnName());
					}
					column.setColumnSize(colDesc.getColumnSize());
					column.setDecimalSize(colDesc.getDecimalSize());
					column.setNullable(colDesc.getNullable());
					column.setType(colDesc.getType());
				}
			} else if (metadata instanceof RoutineDescription) {
				RoutineDescription routineMetadata = (RoutineDescription) metadata;
				RoutineArtifact routine = (RoutineArtifact) bundle
						.getCustomArtifactByName(metadata.toString());
				if (routine == null) {
					routine = new RoutineArtifact(bundle, metadata.toString());

				}
				for (final String columnName : routine
						.getRoutineParameterNames()) {
					if (!routineMetadata.getRoutineParameters().containsKey(
							columnName)) {
						final RoutineParameter column = routine
								.getRoutineParameterByName(columnName);
						routine.removeRoutineParameter(column);
					}
				}

				for (final RoutineParameterDescription colDesc : routineMetadata
						.getRoutineParameters().values()) {
					RoutineParameter column = routine
							.getRoutineParameterByName(colDesc.getName());
					if (column == null) {
						column = new RoutineParameter(routine, colDesc
								.getName());
					}
					column.setColumnSize(colDesc.getColumnSize());
					column.setParameterType(colDesc.getParameterType());
					column.setDecimalSize(colDesc.getDecimalSize());
					column.setNullable(colDesc.getNullable());
					column.setType(colDesc.getType());
				}
			}
			return null;
		}

	}

	protected static class DatabaseCustomGlobalContext extends
			DefaultGlobalExecutionContext {

		@Override
		public Integer withThreadPoolSize(Bundle bundle) {
			Integer defaultValue = super.withThreadPoolSize(bundle);
			if (!(bundle instanceof DbBundle)) {
				return defaultValue;
			}
			DbBundle dbBundle = (DbBundle) bundle;
			Integer maxConnections = dbBundle.getMaxConnections();
			if (maxConnections != null
					&& maxConnections.compareTo(defaultValue) < 0) {
				return maxConnections;
			}
			return defaultValue;
		}

		private final DatabaseCustomArtifactInternalLoader internalLoader = new DatabaseCustomArtifactInternalLoader();

		public Map<String, DatabaseItemDescription> getLoadedMetadata() {
			return this.loadedMetadata;
		}

		private Map<String, DatabaseItemDescription> loadedMetadata = null;

		private void loadMetadata(DbBundle dbBundle) throws SQLException {
			Connection conn = null;
			try {
				conn = createConnection(dbBundle);
				DatabaseMetaData metadata = conn.getMetaData();
				this.loadedMetadata = this.internalLoader
						.loadDatabaseMetadata(metadata);

			} catch (Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			} finally {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			}

		}

		public synchronized Set<String> getAllArtifactNames(Bundle bundle,
				ArtifactMapping mapping) throws ConfigurationException {
			if (!(bundle instanceof DbBundle)) {
				return emptySet();
			}

			try {
				Set<String> artifactNames = new HashSet<String>();
				if (this.loadedMetadata == null) {
					DbBundle dbBundle = (DbBundle) bundle;
					loadMetadata(dbBundle);
				}
				for (Map.Entry<String, DatabaseItemDescription> entry : this.loadedMetadata
						.entrySet()) {
					if (entry.getKey().startsWith(mapping.getRelative())) {
						String name = entry.getKey().substring(
								mapping.getRelative().length());
						artifactNames.add(name);
					}
				}
				return artifactNames;
			} catch (Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
			return emptySet();
		}

	}

	/**
	 * {@link ColumnDescription} to be used to create new {@link Column column
	 * metadata}.
	 * 
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	private static final class ColumnDescription {

		private final String columnName;

		private final ColumnType type;

		private final NullableSqlType nullable;
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
				final ColumnType type, final NullableSqlType nullable,
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
		public NullableSqlType getNullable() {
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
	 * Class with column description to fill the {@link TableArtifact} and
	 * {@link Column} on federation metadata for Table views.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	private static final class ViewDescription extends TableDescription {

		/**
		 * Constructor to initialize all final fields.
		 * 
		 * @param catalog
		 * @param schema
		 * @param tableName
		 */
		public ViewDescription(final String catalog, final String schema,
				final String tableName) {
			super(catalog, schema, tableName);
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (this.description == null) {
				if (this.catalog != null) {
					this.description = format(
							"{0}/{1}/{2}/{3}", //$NON-NLS-1$
							this.schema, ScriptType.VIEW, this.catalog,
							this.tableName);
				} else {
					this.description = format("{0}/{1}/{2}", //$NON-NLS-1$
							this.schema, ScriptType.VIEW, this.tableName);

				}
			}
			return this.description;
		}

	}

	/**
	 * This is just a marker class to describe database items
	 * 
	 * @author feu
	 * 
	 */
	private abstract static class DatabaseItemDescription {
		//
	}

	/**
	 * Class with description for procedures and triggers.
	 * 
	 * @author feu
	 * 
	 */
	private static class RoutineDescription extends DatabaseItemDescription {
		private final String catalog;
		private final String schema;
		private final String name;
		private final String remarks;
		private final RoutineType type;
		private final int hashCode;

		protected final Map<String, RoutineParameterDescription> columns = new HashMap<String, RoutineParameterDescription>();

		private volatile String description = null;

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
		public RoutineDescription(String catalog, String schema, String name,
				String remarks, RoutineType type) {
			this.catalog = catalog;
			this.schema = schema;
			this.name = name;
			this.remarks = remarks;
			this.type = type;
			this.hashCode = hashOf(catalog, schema, name, type);
		}

		/**
		 * 
		 * @return the column map
		 */
		public Map<String, RoutineParameterDescription> getRoutineParameters() {
			return this.columns;
		}

		/**
		 * Adds a new parameter to the internal parameter map
		 * 
		 * @param d
		 */
		public void addParameter(final RoutineParameterDescription d) {
			this.columns.put(d.getName(), d);
		}

		/**
		 * 
		 * @return catalog name
		 */
		public String getCatalog() {
			return this.catalog;
		}

		/**
		 * 
		 * @return schema name
		 */
		public String getSchema() {
			return this.schema;
		}

		/**
		 * 
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * 
		 * @return remarks
		 */
		public String getRemarks() {
			return this.remarks;
		}

		/**
		 * 
		 * @return the type
		 */
		public RoutineType getType() {
			return this.type;
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
			if (!(o instanceof RoutineDescription)) {
				return false;
			}
			final RoutineDescription that = (RoutineDescription) o;
			return eachEquality(of(this.getClass(), this.catalog, this.schema,
					this.name, this.type), andOf(that.getClass(), that.catalog,
					that.schema, that.name, that.type));
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
		private final String columnName;

		private final ColumnType type;

		private final NullableSqlType nullable;
		private final Integer columnSize;
		private final Integer decimalSize;
		private final RoutineParameterType parameterType;
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
		public RoutineParameterDescription(final String columnName,
				final ColumnType type, final NullableSqlType nullable,
				final Integer columnSize, final Integer decimalSize,
				final RoutineParameterType parameterType) {
			this.columnName = columnName;
			this.type = type;
			this.nullable = nullable;
			this.columnSize = columnSize;
			this.parameterType = parameterType;
			this.decimalSize = decimalSize;
			this.hashCode = hashOf(columnName, type, nullable, columnSize,
					decimalSize, parameterType);
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
			if (!(o instanceof RoutineParameter)) {
				return false;
			}
			final RoutineParameterDescription that = (RoutineParameterDescription) o;
			return eachEquality(of(this.columnName, this.type, this.nullable,
					this.columnSize, this.decimalSize, this.parameterType),
					andOf(that.columnName, that.type, that.nullable,
							that.columnSize, that.decimalSize,
							that.parameterType));
		}

		/**
		 * 
		 * @return the column name
		 */
		public String getName() {
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
		 * @return the parameter type
		 */
		public RoutineParameterType getParameterType() {
			return this.parameterType;
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
		public NullableSqlType getNullable() {
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
	 * Class with column description to fill the {@link TableArtifact} and
	 * {@link Column} on federation metadata.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	private static class TableDescription extends DatabaseItemDescription {
		protected final String catalog;
		protected final String schema;
		protected final String tableName;
		protected final int hashCode;

		protected volatile String description = null;

		protected final Map<String, ColumnDescription> columns = new HashMap<String, ColumnDescription>();

		/**
		 * Constructor to initialize all final fields.
		 * 
		 * @param catalog
		 * @param schema
		 * @param tableName
		 */
		public TableDescription(final String catalog, final String schema,
				final String tableName) {
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
			return eachEquality(of(this.getClass(), this.catalog, this.schema,
					this.tableName), andOf(that.getClass(), that.catalog,
					that.schema, that.tableName));
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
				if (this.catalog != null) {
					this.description = format(
							"{0}/{1}/{2}/{3}", //$NON-NLS-1$
							this.schema, ScriptType.TABLE, this.catalog,
							this.tableName);
				} else {
					this.description = format("{0}/{1}/{2}", //$NON-NLS-1$
							this.schema, ScriptType.TABLE, this.tableName);

				}
			}
			return this.description;
		}

	}

	protected static class DatabaseCustomArtifactInternalLoader {

		public Map<String, DatabaseItemDescription> loadDatabaseMetadata(
				final DatabaseMetaData metadata) throws ConfigurationException {
			try {
				Map<String, TableDescription> tableMetadata = loadTableMetadata(metadata);
				Map<String, RoutineDescription> routineMetadata = loadRoutineMetadata(metadata);
				Map<String, DatabaseItemDescription> result = new HashMap<String, DatabaseItemDescription>(
						tableMetadata.size() + routineMetadata.size());
				result.putAll(tableMetadata);
				result.putAll(routineMetadata);
				return result;
			} catch (final Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}

		@SuppressWarnings("boxing")
		private Map<String, RoutineDescription> loadRoutineMetadata(
				final DatabaseMetaData metadata) throws SQLException {

			final ResultSet rs = metadata.getProcedures(null, null, null);
			final Map<String, RoutineDescription> result = new HashMap<String, RoutineDescription>(
					rs.getFetchSize());
			while (rs.next()) {
				String catalog = rs.getString("PROCEDURE_CAT"); //$NON-NLS-1$
				String schema = rs.getString("PROCEDURE_SCHEM"); //$NON-NLS-1$
				String name = rs.getString("PROCEDURE_NAME"); //$NON-NLS-1$
				String remarks = rs.getString("REMARKS"); //$NON-NLS-1$
				int type = rs.getInt("PROCEDURE_TYPE"); //$NON-NLS-1$
				RoutineDescription newMetadata = new RoutineDescription(
						catalog, schema, name, remarks, RoutineType
								.getTypeByInt(type));
				final ResultSet columnsRs = metadata.getProcedureColumns(
						catalog, schema, name, null);
				while (columnsRs.next()) {
					String columnName = columnsRs.getString("COLUMN_NAME"); //$NON-NLS-1$
					int columnType = columnsRs.getInt("DATA_TYPE"); //$NON-NLS-1$
					int routineType = columnsRs.getInt("COLUMN_TYPE"); //$NON-NLS-1$
					int length = columnsRs.getInt("LENGTH"); //$NON-NLS-1$
					int scale = columnsRs.getInt("SCALE"); //$NON-NLS-1$
					int nullable = columnsRs.getInt("NULLABLE"); //$NON-NLS-1$
					RoutineParameterDescription parameter = new RoutineParameterDescription(
							columnName, ColumnType.getTypeByInt(columnType),
							NullableSqlType.getNullableByInt(nullable), length,
							scale, RoutineParameterType
									.getTypeByInt(routineType));
					newMetadata.addParameter(parameter);
				}
				result.put(newMetadata.toString(), newMetadata);
			}

			return result;
		}

		@SuppressWarnings("boxing")
		private Map<String, TableDescription> loadTableMetadata(
				final DatabaseMetaData metadata) throws SQLException {
			final ResultSet rs = metadata.getColumns(null, null, null, null);
			final Map<String, TableDescription> tableMetadata = new HashMap<String, TableDescription>(
					rs.getFetchSize());
			while (rs.next()) {
				final String catalog = rs.getString("TABLE_CAT"); //$NON-NLS-1$
				final String schema = rs.getString("TABLE_SCHEM"); //$NON-NLS-1$
				final String tableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
				final String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
				final ColumnType type = ColumnType.getTypeByInt(rs
						.getInt("DATA_TYPE")); //$NON-NLS-1$
				final NullableSqlType nullable = NullableSqlType
						.getNullableByInt(rs.getInt("NULLABLE")); //$NON-NLS-1$
				final Integer columnSize = rs.getInt("COLUMN_SIZE"); //$NON-NLS-1$
				final Integer decimalSize = rs.getInt("DECIMAL_DIGITS"); //$NON-NLS-1$
				ResultSet tableResultSet = metadata.getTables(catalog, schema,
						tableName, of("TABLE", "VIEW")); //$NON-NLS-1$//$NON-NLS-2$
				while (tableResultSet.next()) {
					String tableType = tableResultSet.getString("TABLE_TYPE"); //$NON-NLS-1$
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
						final ColumnDescription colDesc = new ColumnDescription(
								columnName, type, nullable, columnSize,
								decimalSize);
						desc.addColumn(colDesc);
					}
				}
			}
			return tableMetadata;
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
