package org.openspotlight.federation.data.load;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.ScriptType;

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
					.getString(ColumnsNamesForMetadataSelect.CATALOG_NAME
							.name());
			String name = resultSet
					.getString(ColumnsNamesForMetadataSelect.NAME.name());
			String schema = resultSet
					.getString(ColumnsNamesForMetadataSelect.SCHEMA_NAME.name());
			buffer.append(schema);
			buffer.append('/');
			buffer.append(scriptType.name());
			buffer.append('/');
			if (catalog != null) {
				buffer.append(catalog);
				buffer.append('/');
			}
			buffer.append(name);
			buffer.append('/');
			String result = buffer.toString();
			return result;
		}

	};

	protected static class DatabaseThreadExecutionContext extends
			DefaultThreadExecutionContext {

		private Connection conn;

		public byte[] loadArtifactOrReturnNullToIgnore(Bundle bundle, ArtifactMapping mapping,
				String artifactName, GlobalExecutionContext globalContext)
				throws Exception {
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
			String sql = scriptDescription.getContentSelect();
			sql = sql
					.replace(
							":"		+ ColumnsNamesForMetadataSelect.NAME.name(), "\'" //$NON-NLS-1$ //$NON-NLS-2$
									+ name + "\'"); //$NON-NLS-1$
			sql = sql
					.replace(
							":"		+ ColumnsNamesForMetadataSelect.SCHEMA_NAME.name(), "\'" //$NON-NLS-1$ //$NON-NLS-2$
									+ schema + "\'"); //$NON-NLS-1$
			if (catalog != null) {
				sql = sql
						.replace(
								":"		+ ColumnsNamesForMetadataSelect.CATALOG_NAME.name(), "\'" //$NON-NLS-1$ //$NON-NLS-2$
										+ catalog + "\'"); //$NON-NLS-1$
			}
			ResultSet resultSet = null;
			try {
				resultSet = this.conn.prepareStatement(sql).executeQuery();
				resultSet.next();
				String content = resultSet
						.getString(ColumnsNamesForMetadataSelect.SQL_CONTENT
								.name());
				return content.getBytes();
			} finally {
				if (resultSet != null)
					resultSet.close();
			}
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
