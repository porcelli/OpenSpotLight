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
package org.openspotlight.federation.finder;

import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.PatternMatcher.isMatchingWithoutCaseSentitiveness;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.db.Column;
import org.openspotlight.federation.domain.artifact.db.ColumnType;
import org.openspotlight.federation.domain.artifact.db.DatabaseCustomArtifact;
import org.openspotlight.federation.domain.artifact.db.ForeignKeyConstraintArtifact;
import org.openspotlight.federation.domain.artifact.db.NullableSqlType;
import org.openspotlight.federation.domain.artifact.db.PrimaryKeyConstraintArtifact;
import org.openspotlight.federation.domain.artifact.db.RoutineArtifact;
import org.openspotlight.federation.domain.artifact.db.RoutineParameter;
import org.openspotlight.federation.domain.artifact.db.RoutineParameterType;
import org.openspotlight.federation.domain.artifact.db.RoutineType;
import org.openspotlight.federation.domain.artifact.db.TableArtifact;
import org.openspotlight.federation.domain.artifact.db.ViewArtifact;

public class DatabaseCustomArtifactFinder extends
AbstractDatabaseArtifactFinder<DatabaseCustomArtifact> {

	public static enum Constraints {
		FOREIGN_KEY, PRIMARY_KEY
	}

	protected static class DatabaseCustomArtifactInternalLoader {

		public Map<String, DatabaseCustomArtifact> loadDatabaseMetadata(
				final DatabaseMetaData metadata) throws ConfigurationException {
			try {
				final Map<String, DatabaseCustomArtifact> tableMetadata = loadTableMetadata(metadata);
				final Map<String, RoutineArtifact> routineMetadata = loadRoutineMetadata(metadata);
				final Map<String, DatabaseCustomArtifact> result = new HashMap<String, DatabaseCustomArtifact>(
						tableMetadata.size() + routineMetadata.size());
				result.putAll(tableMetadata);
				result.putAll(routineMetadata);
				return result;
			} catch (final Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}

		@SuppressWarnings("boxing")
		private Map<String, RoutineArtifact> loadRoutineMetadata(
				final DatabaseMetaData metadata) throws SQLException {

			final ResultSet rs = metadata.getProcedures(null, null, null);
			final Map<String, RoutineArtifact> result = new HashMap<String, RoutineArtifact>(
					rs.getFetchSize());
			while (rs.next()) {
				final String catalog = rs.getString("PROCEDURE_CAT"); //$NON-NLS-1$
				final String schema = rs.getString("PROCEDURE_SCHEM"); //$NON-NLS-1$
				final String name = rs.getString("PROCEDURE_NAME"); //$NON-NLS-1$
				final int type = rs.getInt("PROCEDURE_TYPE"); //$NON-NLS-1$
				final RoutineType typeAsEnum = RoutineType.getTypeByInt(type);
				String description;
				if (catalog != null) {
					description = MessageFormat.format("/{0}/{1}/{2}/{3}", //$NON-NLS-1$
							schema, typeAsEnum, catalog, name);
				} else {
					description = MessageFormat.format("/{0}/{1}/{2}", //$NON-NLS-1$
							schema, typeAsEnum, name);

				}

				final RoutineArtifact newMetadata = Artifact
				.createArtifact(RoutineArtifact.class, description,
						ChangeType.INCLUDED);
				newMetadata.setCatalogName(catalog);
				newMetadata.setSchemaName(schema);
				newMetadata.setArtifactName(name);
				newMetadata.setType(typeAsEnum);
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
					final RoutineParameter parameter = new RoutineParameter();
					parameter.setName(columnName);
					parameter.setRoutine(newMetadata);
					parameter.setColumnSize(length);
					parameter.setType(ColumnType.getTypeByInt(columnType));
					parameter.setNullable(NullableSqlType
							.getNullableByInt(nullable));
					parameter.setDecimalSize(scale);
					parameter.setParameterType(RoutineParameterType
							.getTypeByInt(routineType));
					newMetadata.getParameters().add(parameter);
				}

				result.put(description, newMetadata);
			}

			return result;
		}

		@SuppressWarnings("boxing")
		private Map<String, DatabaseCustomArtifact> loadTableMetadata(
				final DatabaseMetaData metadata) throws SQLException {
			final ResultSet tableRs = metadata.getTables(null, null, null, of(
					"TABLE", "VIEW")); //$NON-NLS-1$//$NON-NLS-2$

			final Map<String, DatabaseCustomArtifact> tableMetadata = new HashMap<String, DatabaseCustomArtifact>();
			while (tableRs.next()) {
				final String catalog = tableRs.getString("TABLE_CAT"); //$NON-NLS-1$
				final String schema = tableRs.getString("TABLE_SCHEM"); //$NON-NLS-1$
				final String tableName = tableRs.getString("TABLE_NAME"); //$NON-NLS-1$

				final String tableType = tableRs.getString("TABLE_TYPE"); //$NON-NLS-1$
				String description;
				if (catalog != null) {
					description = MessageFormat.format("/{0}/{1}/{2}/{3}", //$NON-NLS-1$
							schema, catalog, tableType, tableName);
				} else {
					description = MessageFormat.format("/{0}/{1}/{2}", //$NON-NLS-1$
							schema, tableType, tableName);

				}

				TableArtifact desc = null;

				if (tableMetadata.containsKey(description)
						&& !description.startsWith(Constraints.FOREIGN_KEY
								.toString())
								&& !description.startsWith(Constraints.PRIMARY_KEY
										.toString())) {
					desc = (TableArtifact) tableMetadata.get(description);
				} else {
					if ("VIEW".equals(tableType)) { //$NON-NLS-1$
						desc = Artifact.createArtifact(ViewArtifact.class,
								description, ChangeType.INCLUDED);
					} else {
						desc = Artifact.createArtifact(TableArtifact.class,
								description, ChangeType.INCLUDED);
					}
				}
				desc.setSchemaName(schema);
				desc.setCatalogName(catalog);
				desc.setTableName(tableName);
				final Map<String, Set<String>> pkMap = new HashMap<String, Set<String>>();
				final ResultSet pkRs = metadata.getPrimaryKeys(catalog, schema,
						tableName);
				while (pkRs.next()) {
					final String pkColumn = pkRs.getString("COLUMN_NAME");
					final String pkName = pkRs.getString("PK_NAME");
					Set<String> set = pkMap.get(pkColumn);
					if (set == null) {
						set = new HashSet<String>();
						pkMap.put(pkColumn, set);
					}
					set.add(pkName);
				}

				final ResultSet fkRs = metadata.getExportedKeys(catalog,
						schema, tableName);
				while (fkRs.next()) {
					final String fkName = fkRs.getString("FK_NAME");
					final String thatCatalog = fkRs.getString("FKTABLE_CAT");
					final String thatSchema = fkRs.getString("FKTABLE_SCHEM");
					final String thatTable = fkRs.getString("FKTABLE_NAME");
					final String thatColumn = fkRs.getString("FKCOLUMN_NAME");
					final String fromCatalog = fkRs.getString("PKTABLE_CAT");
					final String fromSchema = fkRs.getString("PKTABLE_SCHEM");
					final String fromTable = fkRs.getString("PKTABLE_NAME");
					final String fromColumn = fkRs.getString("PKCOLUMN_NAME");

					final String keyArtifactName = MessageFormat.format(
							"/{0}/{1}", Constraints.FOREIGN_KEY, fkName);
					final ForeignKeyConstraintArtifact fk = Artifact
					.createArtifact(ForeignKeyConstraintArtifact.class,
							keyArtifactName, ChangeType.INCLUDED);
					fk.setToCatalogName(thatCatalog);
					fk.setToColumnName(thatColumn);
					fk.setToSchemaName(thatSchema);
					fk.setToTableName(thatTable);
					fk.setFromCatalogName(fromCatalog);
					fk.setFromColumnName(fromColumn);
					fk.setFromSchemaName(fromSchema);
					fk.setFromTableName(fromTable);
					fk.setConstraintName(fkName);
					tableMetadata.put(keyArtifactName, fk);
				}

				final ResultSet columnRs = metadata.getColumns(catalog, schema,
						tableName, null);

				while (columnRs.next()) {

					final String columnName = columnRs.getString("COLUMN_NAME"); //$NON-NLS-1$
					final ColumnType type = ColumnType.getTypeByInt(columnRs
							.getInt("DATA_TYPE")); //$NON-NLS-1$
					final NullableSqlType nullable = NullableSqlType
					.getNullableByInt(columnRs.getInt("NULLABLE")); //$NON-NLS-1$
					final Integer columnSize = columnRs.getInt("COLUMN_SIZE"); //$NON-NLS-1$
					final Integer decimalSize = columnRs
					.getInt("DECIMAL_DIGITS"); //$NON-NLS-1$

					Column column = new Column();
					column.setTable(desc);
					column.setName(columnName);
					findingColumn: if (desc.getColumns().contains(column)) {
						for (final Column c : desc.getColumns()) {
							if (c.equals(column)) {
								column = c;
								break findingColumn;
							}
						}
					}
					final Set<String> pks = pkMap.get(columnName);
					if (pks != null) {
						for (final String pkName : pks) {
							final String pkArtifactName = MessageFormat.format(
"/{0}/{1}",
											Constraints.PRIMARY_KEY, pkName);
							final PrimaryKeyConstraintArtifact pk = Artifact
							.createArtifact(
									PrimaryKeyConstraintArtifact.class,
									pkArtifactName, ChangeType.INCLUDED);
							pk.setConstraintName(pkName);
							pk.setColumnName(column.getName());
							pk.setTableName(column.getTable().getTableName());
							pk.setCatalogName(column.getTable()
									.getCatalogName());
							pk.setSchemaName(column.getTable().getSchemaName());
							tableMetadata.put(pkArtifactName, pk);
						}
					}
					column.setType(type);
					column.setNullable(nullable);
					column.setColumnSize(columnSize);
					column.setDecimalSize(decimalSize);
					desc.getColumns().add(column);
				}
				tableMetadata.put(description, desc);

			}
			return tableMetadata;
		}

	}

	private final ConcurrentHashMap<ArtifactSource, Map<String, DatabaseCustomArtifact>> resultCache = new ConcurrentHashMap<ArtifactSource, Map<String, DatabaseCustomArtifact>>();

	public DatabaseCustomArtifactFinder(final DbArtifactSource artifactSource) {
		super(DatabaseCustomArtifact.class, artifactSource);
	}

	@Override
	public synchronized void closeResources() {
		super.closeResources();
		resultCache.clear();
	}

	private synchronized Map<String, DatabaseCustomArtifact> getResultFrom(
			final DbArtifactSource source) throws Exception {
		Assertions.checkNotEmpty("source.databaseName", source
				.getDatabaseName());
		Assertions.checkNotEmpty("source.serverName", source.getServerName());
		Assertions.checkNotEmpty("source.initialLookup", source
				.getInitialLookup());

		Map<String, DatabaseCustomArtifact> result = resultCache.get(source);
		if (result == null) {
			final DatabaseCustomArtifactInternalLoader loader = new DatabaseCustomArtifactInternalLoader();
			final Connection conn = getConnectionFromSource(source);
			synchronized (conn) {
				result = loader.loadDatabaseMetadata(conn.getMetaData());
				for (final DatabaseCustomArtifact artifact : result.values()) {
					artifact.setServerName(source.getServerName());
					artifact.setDatabaseName(source.getDatabaseName());
					artifact.setDatabaseType(source.getType());
					artifact.setUrl(source.getInitialLookup());
				}
			}
			resultCache.put(source, result);
		}
		return result;

	}

	protected DatabaseCustomArtifact internalFindByPath(final String path) {
		try {

			final DbArtifactSource dbBundle = (DbArtifactSource) artifactSource;
			final Map<String, DatabaseCustomArtifact> resultMap = getResultFrom(dbBundle);
			final DatabaseCustomArtifact metadata = resultMap.get(path);
			return metadata;
		} catch (final Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	@Override
	protected Set<String> internalRetrieveAllArtifactNames(
			final String initialPath) {
		try {
			String pathToMatch;
			if(Strings.isEmpty(initialPath)){
				pathToMatch = "*";
			}else{
				if(!initialPath.endsWith("*")){
					pathToMatch = initialPath + "*";
				}else{
					pathToMatch = initialPath;
				}
				if(!initialPath.startsWith("*")){
					pathToMatch = "*"+initialPath;
				}else{
					pathToMatch = initialPath;
				}
				if(!pathToMatch.endsWith("**")){
					pathToMatch = pathToMatch + "*";
				}
				if(!pathToMatch.startsWith("**")){
					pathToMatch = "*"+pathToMatch;
				}
			}
			final Set<String> artifactNames = new HashSet<String>();
			final DbArtifactSource dbBundle = (DbArtifactSource) artifactSource;
			final Map<String, DatabaseCustomArtifact> result = getResultFrom(dbBundle);
			for (final Map.Entry<String, DatabaseCustomArtifact> entry : result
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
