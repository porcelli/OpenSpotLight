package org.openspotlight.federation.finder;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import static org.openspotlight.common.util.PatternMatcher.isMatchingWithoutCaseSentitiveness;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.Column;
import org.openspotlight.federation.domain.ColumnType;
import org.openspotlight.federation.domain.DatabaseCustomArtifact;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.NullableSqlType;
import org.openspotlight.federation.domain.RoutineArtifact;
import org.openspotlight.federation.domain.RoutineParameter;
import org.openspotlight.federation.domain.RoutineParameterType;
import org.openspotlight.federation.domain.RoutineType;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.domain.ViewArtifact;
import org.openspotlight.federation.finder.db.ScriptType;

@SuppressWarnings("unused")
public class DatabaseCustomArtifactFinder extends
		AbstractDatabaseArtifactFinder<DatabaseCustomArtifact> {

	/**
	 * {@link ColumnDescription} to be used to create new {@link Column column
	 * metadata}.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 */
	private static final class ColumnDescription {

		private final String columnName;

		private final Integer columnSize;

		private final Integer decimalSize;
		private final int hashCode;
		private final NullableSqlType nullable;
		private final ColumnType type;

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
			hashCode = hashOf(columnName, type, nullable, columnSize,
					decimalSize);
		}

		/**
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
			return eachEquality(of(columnName, type, nullable, columnSize,
					decimalSize), andOf(that.columnName, that.type,
					that.nullable, that.columnSize, that.decimalSize));
		}

		/**
		 * @return the column name
		 */
		public String getColumnName() {
			return columnName;
		}

		/**
		 * @return the column size
		 */
		public Integer getColumnSize() {
			return columnSize;
		}

		/**
		 * @return the decimal size
		 */
		public Integer getDecimalSize() {
			return decimalSize;
		}

		/**
		 * @return the nullable
		 */
		public NullableSqlType getNullable() {
			return nullable;
		}

		/**
		 * @return the type
		 */
		public ColumnType getType() {
			return type;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return hashCode;
		}

	}

	protected static class DatabaseCustomArtifactInternalLoader {

		public Map<String, DatabaseItemDescription> loadDatabaseMetadata(
				final DatabaseMetaData metadata) throws ConfigurationException {
			try {
				final Map<String, TableDescription> tableMetadata = loadTableMetadata(metadata);
				final Map<String, RoutineDescription> routineMetadata = loadRoutineMetadata(metadata);
				final Map<String, DatabaseItemDescription> result = new HashMap<String, DatabaseItemDescription>(
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
				final String catalog = rs.getString("PROCEDURE_CAT"); //$NON-NLS-1$
				final String schema = rs.getString("PROCEDURE_SCHEM"); //$NON-NLS-1$
				final String name = rs.getString("PROCEDURE_NAME"); //$NON-NLS-1$
				final String remarks = rs.getString("REMARKS"); //$NON-NLS-1$
				final int type = rs.getInt("PROCEDURE_TYPE"); //$NON-NLS-1$
				final RoutineDescription newMetadata = new RoutineDescription(
						catalog, schema, name, remarks, RoutineType
								.getTypeByInt(type));
				final ResultSet columnsRs = metadata.getProcedureColumns(
						catalog, schema, name, null);
				while (columnsRs.next()) {
					final String columnName = columnsRs
							.getString("COLUMN_NAME"); //$NON-NLS-1$
					final int columnType = columnsRs.getInt("DATA_TYPE"); //$NON-NLS-1$
					final int routineType = columnsRs.getInt("COLUMN_TYPE"); //$NON-NLS-1$
					final int length = columnsRs.getInt("LENGTH"); //$NON-NLS-1$
					final int scale = columnsRs.getInt("SCALE"); //$NON-NLS-1$
					final int nullable = columnsRs.getInt("NULLABLE"); //$NON-NLS-1$
					final RoutineParameterDescription parameter = new RoutineParameterDescription(
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
				final ResultSet tableResultSet = metadata.getTables(catalog,
						schema, tableName, of("TABLE", "VIEW")); //$NON-NLS-1$//$NON-NLS-2$
				while (tableResultSet.next()) {
					final String tableType = tableResultSet
							.getString("TABLE_TYPE"); //$NON-NLS-1$
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
		private final String catalog;
		protected final Map<String, RoutineParameterDescription> columns = new HashMap<String, RoutineParameterDescription>();
		private volatile String description = null;
		private final int hashCode;
		private final String name;
		private final String remarks;

		private final String schema;

		private final RoutineType type;

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
		@SuppressWarnings("synthetic-access")
		public RoutineDescription(final String catalog, final String schema,
				final String name, final String remarks, final RoutineType type) {
			this.catalog = catalog;
			this.schema = schema;
			this.name = name;
			this.remarks = remarks;
			this.type = type;
			hashCode = hashOf(catalog, schema, name, type);
		}

		/**
		 * Adds a new parameter to the internal parameter map
		 * 
		 * @param d
		 */
		public void addParameter(final RoutineParameterDescription d) {
			columns.put(d.getName(), d);
		}

		/**
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
			return eachEquality(
					of(this.getClass(), catalog, schema, name, type), andOf(
							that.getClass(), that.catalog, that.schema,
							that.name, that.type));
		}

		/**
		 * @return catalog name
		 */
		public String getCatalog() {
			return catalog;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return remarks
		 */
		public String getRemarks() {
			return remarks;
		}

		/**
		 * @return the column map
		 */
		public Map<String, RoutineParameterDescription> getRoutineParameters() {
			return columns;
		}

		/**
		 * @return schema name
		 */
		public String getSchema() {
			return schema;
		}

		/**
		 * @return the type
		 */
		public RoutineType getType() {
			return type;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return hashCode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (description == null) {
				if (catalog != null) {
					description = format("{0}/{1}/{2}/{3}", //$NON-NLS-1$
							schema, type, catalog, name);
				} else {
					description = format("{0}/{1}/{2}", //$NON-NLS-1$
							schema, type, name);

				}
			}
			return description;
		}
	}

	private static class RoutineParameterDescription {
		private final String columnName;

		private final Integer columnSize;

		private final Integer decimalSize;
		private final int hashCode;
		private final NullableSqlType nullable;
		private final RoutineParameterType parameterType;
		private final ColumnType type;

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
			hashCode = hashOf(columnName, type, nullable, columnSize,
					decimalSize, parameterType);
		}

		/**
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
			return eachEquality(of(columnName, type, nullable, columnSize,
					decimalSize, parameterType), andOf(that.columnName,
					that.type, that.nullable, that.columnSize,
					that.decimalSize, that.parameterType));
		}

		/**
		 * @return the column size
		 */
		public Integer getColumnSize() {
			return columnSize;
		}

		/**
		 * @return the decimal size
		 */
		public Integer getDecimalSize() {
			return decimalSize;
		}

		/**
		 * @return the column name
		 */
		public String getName() {
			return columnName;
		}

		/**
		 * @return the nullable
		 */
		public NullableSqlType getNullable() {
			return nullable;
		}

		/**
		 * @return the parameter type
		 */
		public RoutineParameterType getParameterType() {
			return parameterType;
		}

		/**
		 * @return the type
		 */
		public ColumnType getType() {
			return type;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return hashCode;
		}

	}

	/**
	 * Class with column description to fill the {@link TableArtifact} and
	 * {@link Column} on federation metadata.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 */
	private static class TableDescription extends DatabaseItemDescription {
		protected final String catalog;
		protected final Map<String, ColumnDescription> columns = new HashMap<String, ColumnDescription>();
		protected volatile String description = null;
		protected final int hashCode;

		protected final String schema;

		protected final String tableName;

		/**
		 * Constructor to initialize all final fields.
		 * 
		 * @param catalog
		 * @param schema
		 * @param tableName
		 */
		@SuppressWarnings("synthetic-access")
		public TableDescription(final String catalog, final String schema,
				final String tableName) {
			this.catalog = catalog;
			this.schema = schema;
			this.tableName = tableName;
			hashCode = hashOf(catalog, schema, tableName);
		}

		/**
		 * Adds a new column to the internal column map
		 * 
		 * @param d
		 */
		public void addColumn(final ColumnDescription d) {
			columns.put(d.getColumnName(), d);
		}

		/**
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
			return eachEquality(
					of(this.getClass(), catalog, schema, tableName), andOf(that
							.getClass(), that.catalog, that.schema,
							that.tableName));
		}

		/**
		 * @return the catalog
		 */
		public String getCatalog() {
			return catalog;
		}

		/**
		 * @return the column map
		 */
		public Map<String, ColumnDescription> getColumns() {
			return columns;
		}

		/**
		 * @return the schema
		 */
		public String getSchema() {
			return schema;
		}

		/**
		 * @return the table name
		 */
		public String getTableName() {
			return tableName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return hashCode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (description == null) {
				if (catalog != null) {
					description = format("{0}/{1}/{2}/{3}", //$NON-NLS-1$
							schema, ScriptType.TABLE, catalog, tableName);
				} else {
					description = format("{0}/{1}/{2}", //$NON-NLS-1$
							schema, ScriptType.TABLE, tableName);

				}
			}
			return description;
		}

	}

	/**
	 * Class with column description to fill the {@link TableArtifact} and
	 * {@link Column} on federation metadata for Table views.
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
		public ViewDescription(final String catalog, final String schema,
				final String tableName) {
			super(catalog, schema, tableName);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (description == null) {
				if (catalog != null) {
					description = format("{0}/{1}/{2}/{3}", //$NON-NLS-1$
							schema, ScriptType.VIEW, catalog, tableName);
				} else {
					description = format("{0}/{1}/{2}", //$NON-NLS-1$
							schema, ScriptType.VIEW, tableName);

				}
			}
			return description;
		}

	}

	private final ConcurrentHashMap<ArtifactSource, Map<String, org.openspotlight.federation.finder.DatabaseCustomArtifactFinder.DatabaseItemDescription>> resultCache = new ConcurrentHashMap<ArtifactSource, Map<String, DatabaseItemDescription>>();

	public DatabaseCustomArtifactFinder(final DbArtifactSource artifactSource) {
		super(artifactSource);
	}

	@Override
	public synchronized void closeResources() {
		super.closeResources();
		resultCache.clear();
	}

	/**
	 * This method loads routine metadata and also remove unused metadata
	 * related to this {@link DatabaseItemDescription}.
	 * 
	 * @param bundle
	 * @param metadata
	 */
	private DatabaseCustomArtifact createRoutineMetadata(
			final ArtifactSource bundle, final DatabaseItemDescription metadata) {
		final RoutineDescription routineMetadata = (RoutineDescription) metadata;
		final RoutineArtifact routine = Artifact.createArtifact(
				RoutineArtifact.class, "/" + metadata.toString(),
				ChangeType.INCLUDED);
		routine.loadProperties();
		for (final RoutineParameterDescription colDesc : routineMetadata
				.getRoutineParameters().values()) {
			final RoutineParameter column = new RoutineParameter();
			column.setName(colDesc.getName());
			column.setRoutine(routine);
			routine.getParameters().add(column);
			column.setColumnSize(colDesc.getColumnSize());
			column.setParameterType(colDesc.getParameterType());
			column.setDecimalSize(colDesc.getDecimalSize());
			column.setNullable(colDesc.getNullable());
			column.setType(colDesc.getType());
		}
		return routine;
	}

	/**
	 * This method loads table metadata and also remove unused metadata related
	 * to this {@link DatabaseItemDescription}.
	 * 
	 * @param bundle
	 * @param metadata
	 */
	private DatabaseCustomArtifact createTableMetadata(
			final ArtifactSource bundle, final DatabaseItemDescription metadata) {
		final TableDescription tableMetadata = (TableDescription) metadata;
		TableArtifact table;

		if (tableMetadata instanceof ViewDescription) {
			table = Artifact.createArtifact(ViewArtifact.class, "/"
					+ metadata.toString(), ChangeType.INCLUDED);
			metadata.toString();
		} else {
			table = Artifact.createArtifact(TableArtifact.class, "/"
					+ metadata.toString(), ChangeType.INCLUDED);
		}
		table.loadProperties();
		for (final ColumnDescription colDesc : tableMetadata.getColumns()
				.values()) {
			final Column column = new Column();
			column.setName(colDesc.getColumnName());
			column.setTable(table);
			table.getColumns().add(column);
			column.setColumnSize(colDesc.getColumnSize());
			column.setDecimalSize(colDesc.getDecimalSize());
			column.setNullable(colDesc.getNullable());
			column.setType(colDesc.getType());
		}
		return table;
	}

	public DatabaseCustomArtifact findByPath(final String path) {
		try {

			final DbArtifactSource dbBundle = (DbArtifactSource) artifactSource;
			final Map<String, DatabaseItemDescription> resultMap = getResultFrom(dbBundle);
			final DatabaseItemDescription metadata = resultMap.get(path);
			DatabaseCustomArtifact result;
			if (metadata instanceof TableDescription) {
				result = createTableMetadata(artifactSource, metadata);
			} else {// Its a routine description
				result = createRoutineMetadata(artifactSource, metadata);
			}
			return result;
		} catch (final Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	private synchronized Map<String, org.openspotlight.federation.finder.DatabaseCustomArtifactFinder.DatabaseItemDescription> getResultFrom(
			final DbArtifactSource source) throws Exception {
		Map<String, DatabaseItemDescription> result = resultCache.get(source);
		if (result == null) {
			final DatabaseCustomArtifactInternalLoader loader = new DatabaseCustomArtifactInternalLoader();
			final Connection conn = getConnectionFromSource(source);
			synchronized (conn) {
				result = loader.loadDatabaseMetadata(conn.getMetaData());
			}
			resultCache.put(source, result);
		}
		return result;

	}

	@Override
	public Set<String> retrieveAllArtifactNames(final String initialPath) {
		try {
			final String pathToMatch = initialPath.endsWith("/") ? initialPath
					+ "*" : initialPath + "/*";
			final Set<String> artifactNames = new HashSet<String>();
			final DbArtifactSource dbBundle = (DbArtifactSource) artifactSource;
			final Map<String, DatabaseItemDescription> result = getResultFrom(dbBundle);
			for (final Map.Entry<String, org.openspotlight.federation.finder.DatabaseCustomArtifactFinder.DatabaseItemDescription> entry : result
					.entrySet()) {
				if (isMatchingWithoutCaseSentitiveness(entry.getKey(),
						pathToMatch)) {
					artifactNames.add(entry.getKey());
				}

			}
			return artifactNames;
		} catch (final Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

}
