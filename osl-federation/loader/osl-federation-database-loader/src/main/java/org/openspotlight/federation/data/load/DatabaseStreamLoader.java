package org.openspotlight.federation.data.load;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.template.CustomizedStringTemplate;

public class DatabaseStreamLoader extends AbstractArtifactLoader {

	/**
	 * This context will be used to load all database data
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	protected static class GlobalDatabaseContext extends
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

		public Set<String> getAllArtifactNames(Bundle bundle,
				ArtifactMapping mapping) throws ConfigurationException {
			if (!(bundle instanceof DbBundle)) {
				return emptySet();
			}
			DbBundle dbBundle = (DbBundle) bundle;
			try {
				Connection conn = null;
				Set<String> loadedNames = new HashSet<String>();

				try {
					conn = createConnection(dbBundle);
					DatabaseType databaseType = dbBundle.getType();
					for (ScriptType scriptType : ScriptType.values()) {
						DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE
								.getScript(databaseType, scriptType);
						if (scriptDescription == null)
							continue;
						ResultSet resultSet = conn.prepareStatement(
								scriptDescription.getDataSelect())
								.executeQuery();
						while (resultSet.next()) {
							String result = fillName(scriptType, resultSet);
							String starting = mapping.getRelative();
							if (result.startsWith(starting)) {
								loadedNames.add(result.substring(starting
										.length()));
							}
						}
						resultSet.close();
					}
					return loadedNames;
				} catch (Exception e) {
					logAndReturnNew(e, ConfigurationException.class);
				} finally {
					if (conn != null && !conn.isClosed()) {
						conn.close();
					}
				}
			} catch (Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
			return emptySet();
		}

		private String fillName(ScriptType scriptType, ResultSet resultSet)
				throws SQLException {
			StringBuilder buffer = new StringBuilder();
			String catalog = resultSet
					.getString(ColumnsNamesForMetadataSelect.catalog_name
							.name());
			String name = resultSet
					.getString(ColumnsNamesForMetadataSelect.name.name());
			String schema = resultSet
					.getString(ColumnsNamesForMetadataSelect.schema_name.name());
			buffer.append(schema);
			buffer.append('/');
			buffer.append(scriptType.name());
			buffer.append('/');
			if (catalog != null && !"".equals(catalog.trim())) {
				buffer.append(catalog);
				buffer.append('/');
			}
			buffer.append(name);
			String result = buffer.toString();
			return result;
		}

	};

	protected static class DatabaseThreadExecutionContext extends
			DefaultThreadExecutionContext {

		private Connection conn;

		public byte[] loadArtifactOrReturnNullToIgnore(Bundle bundle,
				ArtifactMapping mapping, String artifactName,
				GlobalExecutionContext globalContext) throws Exception {
			DbBundle dbBundle = (DbBundle) bundle;
			String completeArtifactName = mapping.getRelative() + artifactName;
			StringTokenizer tok = new StringTokenizer(completeArtifactName, "/"); //$NON-NLS-1$
			int numberOfTokens = tok.countTokens();
			String catalog;
			String schema = tok.nextToken();
			String typeAsString = tok.nextToken();
			if (numberOfTokens == 4) {
				catalog = tok.nextToken();
			} else {
				catalog = null;
			}
			String name = tok.nextToken();
			ScriptType scriptType = ScriptType.valueOf(typeAsString);
			DatabaseType databaseType = dbBundle.getType();
			DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE
					.getScript(databaseType, scriptType);
			if (scriptDescription == null) {
				return null;
			}
			switch (scriptDescription.getPreferedType()) {
			case SQL:
				return loadFromSql(catalog, schema, name, scriptDescription);
			case TEMPLATE:
				return loadFromTemplate(catalog, schema, name,
						scriptDescription);
			default:
				logAndReturn(new ConfigurationException("Invalid prefered type"));
			}
			return null;
		}

		private byte[] loadFromTemplate(String catalog, String schema,
				String name, DatabaseMetadataScript scriptDescription)
				throws Exception {
			Map<ColumnsNamesForMetadataSelect, String> columnValues = new EnumMap<ColumnsNamesForMetadataSelect, String>(
					ColumnsNamesForMetadataSelect.class);

			columnValues.put(ColumnsNamesForMetadataSelect.catalog_name,
					catalog);
			columnValues.put(ColumnsNamesForMetadataSelect.schema_name, schema);
			columnValues.put(ColumnsNamesForMetadataSelect.name, name);

			StringTemplate template = new StringTemplate(scriptDescription
					.getTemplatesSelect(), DefaultTemplateLexer.class);
			for (Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues
					.entrySet()) {
				template.setAttribute(entry.getKey().name(), entry.getValue());
			}

			String sql = template.toString();

			ResultSet resultSet = null;
			try {
				resultSet = this.conn.prepareStatement(sql).executeQuery();
				String templateString = scriptDescription.getTemplate();
				CustomizedStringTemplate contentTemplate = new CustomizedStringTemplate(
						templateString, DefaultTemplateLexer.class);
				for (Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues
						.entrySet()) {
					contentTemplate.setAttribute(entry.getKey().name(), entry
							.getValue());
				}
				int count = 0;
				boolean hasAnyResult = false;
				String attributeName = null;
				while (resultSet.next()) {
					List<String> columnsFromDatabase = new ArrayList<String>();
					if (!hasAnyResult) {
						hasAnyResult = true;
						StringBuilder baseForTemplate = new StringBuilder(
								"detail.{");
						ResultSetMetaData metadata = resultSet.getMetaData();
						count = metadata.getColumnCount();
						for (int i = 1; i <= count; i++) {
							String columnName = metadata.getColumnLabel(i)
									.toLowerCase();
							String content = resultSet.getString(i);
							contentTemplate.setAttribute(columnName, content);
							columnsFromDatabase.add(content);

							baseForTemplate.append(columnName);
							if (i != count)
								baseForTemplate.append(',');
						}
						baseForTemplate.append('}');
						attributeName = baseForTemplate.toString();
					} else {
						for (int i = 1; i <= count; i++) {
							columnsFromDatabase.add(resultSet.getString(i));
						}
					}
					Object[] valuesAsArray = columnsFromDatabase.toArray();
					contentTemplate.setAttributeArray(attributeName,
							valuesAsArray);
				}
				resultSet.close();
				if (!hasAnyResult) {
					logAndReturn(new IllegalStateException("no result on "
							+ sql));

				}
				String result = contentTemplate.toString();
				return result.getBytes();

			} finally {
				if (resultSet != null)
					resultSet.close();
			}
		}

		private byte[] loadFromSql(String catalog, String schema, String name,
				DatabaseMetadataScript scriptDescription) throws Exception {
			Map<ColumnsNamesForMetadataSelect, String> columnValues = new EnumMap<ColumnsNamesForMetadataSelect, String>(
					ColumnsNamesForMetadataSelect.class);

			columnValues.put(ColumnsNamesForMetadataSelect.catalog_name,
					catalog);
			columnValues.put(ColumnsNamesForMetadataSelect.schema_name, schema);
			columnValues.put(ColumnsNamesForMetadataSelect.name, name);

			StringTemplate template = new StringTemplate(scriptDescription
					.getContentSelect(), DefaultTemplateLexer.class);
			for (Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues
					.entrySet()) {
				template.setAttribute(entry.getKey().name(), entry.getValue());
			}

			String sql = template.toString();

			ResultSet resultSet = null;
			try {
				resultSet = this.conn.prepareStatement(sql).executeQuery();
				if (resultSet.next()) {
					String content = resultSet.getString(1);
					return content.getBytes();
				}
			} catch (Exception e) {
				logAndReturn("Error on Sql " + sql, e);
			} finally {
				if (resultSet != null)
					resultSet.close();
			}
			return null;
		}

		@Override
		public void threadExecutionAboutToStart(Bundle bundle,
				ArtifactMapping mapping,
				GlobalExecutionContext globalExecutionContext) {
			try {
				DbBundle dbBundle = (DbBundle) bundle;
				this.conn = createConnection(dbBundle);
			} catch (Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
		}

		@Override
		public void threadExecutionFinished(Bundle bundle,
				ArtifactMapping mapping,
				GlobalExecutionContext globalExecutionContext) {
			try {
				if (this.conn != null && !this.conn.isClosed()) {
					this.conn.close();
				}
			} catch (Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
		}

	}

	@Override
	protected GlobalExecutionContext createGlobalExecutionContext() {
		return new GlobalDatabaseContext();
	}

	@Override
	protected ThreadExecutionContext createThreadExecutionContext() {
		return new DatabaseThreadExecutionContext();
	}

}
