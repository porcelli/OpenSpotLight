package org.openspotlight.federation.data.load;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.Connection;
import java.sql.Driver;
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
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.load.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.template.CustomizedStringTemplate;

/**
 * This artifact loader loads all scripts used to create different database
 * artifacts. This artifacts should be loaded as a {@link StreamArtifact} with
 * the SQL used to create it. In some cases the database implementation doesn't
 * have a great way to load this scripts. In other cases this DDL scripts should
 * be pretty complex. To be possible to work in both cases, there's a way to
 * create a template for this scripts. So, it's possible to get the database ddl
 * script content using a template or not.
 * 
 * We have a {@link DatabaseType} enum and also a {@link ScriptType} enum. This
 * enums should be used to identify the database and script types in a unique
 * way.
 * 
 * To load a given {@link ScriptType} for a given {@link DatabaseType} it's
 * necessary to create a xml script in the format
 * <b>osl-database-artifact-loader/src/main
 * /resources/configuration/DatabaseType-ScriptType.xml</b>.
 * 
 * There's two ways to create this scripts: to use template or to use direct sql
 * to get the ddl scripts. The following content examples should be used as a
 * base to that files:
 * 
 * <b>SQL way</b>
 * 
 * <pre>
 * &lt;script&gt;
 * 	&lt;scriptType&gt;FUNCTION&lt;/scriptType&gt;
 * 	&lt;database&gt;DB2&lt;/database&gt;
 * 	&lt;preferedType&gt;SQL&lt;/preferedType&gt;
 * 	&lt;dataSelect&gt;&lt;![CDATA[
 * 		select 
 * 			funcschema as schema_name, 
 * 			funcname as name, 
 * 			'' as catalog_name 
 * 		from syscat.FUNCTIONS
 *       ]]&gt;&lt;/dataSelect&gt;
 * 	&lt;contentSelect&gt;&lt;![CDATA[
 * 		select body
 * 		from syscat.FUNCTIONS
 * 		where
 * 			funcschema = '$schema_name$' and
 * 			funcname  = '$name$'
 *    	 ]]&gt;&lt;/contentSelect&gt;
 * &lt;/script&gt;
 * </pre>
 * 
 * <b>Template way</b>
 * 
 * <pre>
 * &lt;script&gt;
 * 	&lt;scriptType&gt;INDEX&lt;/scriptType&gt;
 * 	&lt;database&gt;DB2&lt;/database&gt;
 * 	&lt;preferedType&gt;TEMPLATE&lt;/preferedType&gt;
 * 	&lt;template&gt;&lt;![CDATA[
 * 	create index $name$ on $detail.tabname$ ( $detail.colnames$ )
 * 	]]&gt;&lt;/template&gt;
 * 
 * 	&lt;dataSelect&gt;&lt;![CDATA[
 * 		select 
 * 			indschema as schema_name, 
 * 			indname as name, 
 * 			'' as catalog_name 
 * 		from syscat.INDEXES
 *       ]]&gt;&lt;/dataSelect&gt;
 * 	&lt;templatesSelect&gt;&lt;![CDATA[
 * 		select 
 * 			tabname, colnames 
 * 		from syscat.indexes 
 * 		where
 * 		    indschema = '$schema_name$' and
 * 		    indname = '$name$'
 * 	 ]]&gt;&lt;/templatesSelect&gt;
 * &lt;/script&gt;
 * </pre>
 * 
 * <b>Few tips:</b>
 * <ul>
 * <li>The data content select is necessary in both cases, and the column names
 * should be the same as in both examples.</li>
 * <li>All fields are mandatory in data content select, except for CATALOG_NAME.
 * </li>
 * <li>In both cases all fields from data content select must be used in the
 * target select, using a syntax for {@link StringTemplate}.</li>
 * <li>In SQL case, the select should return just one column and one row. If
 * there's more than one row or column, just the first one will be used. If
 * there's no return for the select, that artifact been loaded should be
 * ignored.</li>
 * <li>In template case, the template section and templatesSelect section should
 * be filled.</li>
 * <li>In template case, the following ways to access select fields should be
 * used: $name$ for results from the first select, $name$ for results from the
 * templatesSelect on the first row, and $detail.name$ to iterate all rows. Note
 * that this last case should be done in a {@link StringTemplate} way. Please
 * take a look on the other xmls or the {@link StringTemplate} documentation for
 * more details.</li>
 * <li>Theres a test case called DatabaseStreamTest. This test case should be
 * used to test all valid {@link ScriptType script types} for all valid
 * {@link DatabaseType database types}. When necessary, the {@link Driver}
 * dependency should be included inside the pom's <b>withDatabaseVendorTests</b>
 * profile. The test should implement RunWhenDatabaseVendorTestsIsActive
 * interface. So, the tests and the profile should be active just when the
 * system property <b>runDatabaseVendorTests</b> is set to true.</li>
 * </ul>
 * 
 * @author feu
 * 
 */
public class DatabaseStreamLoader extends AbstractArtifactLoader {

	/**
	 * {@link ThreadExecutionContext} class for loading database
	 * {@link StreamArtifact stream artifacts} using direct sql or template
	 * filled with sql.
	 * 
	 * @author feu
	 * 
	 */
	protected static class DatabaseThreadExecutionContext extends
			DefaultThreadExecutionContext {

		private Connection conn;

		/**
		 * {@inheritDoc}
		 */
		public byte[] loadArtifactOrReturnNullToIgnore(final Bundle bundle,
				final ArtifactMapping mapping, final String artifactName,
				final GlobalExecutionContext globalContext) throws Exception {
			final DbBundle dbBundle = (DbBundle) bundle;
			final String completeArtifactName = mapping.getRelative()
					+ artifactName;
			final StringTokenizer tok = new StringTokenizer(
					completeArtifactName, "/"); //$NON-NLS-1$
			final int numberOfTokens = tok.countTokens();
			String catalog;
			final String schema = tok.nextToken();
			final String typeAsString = tok.nextToken();
			if (numberOfTokens == 4) {
				catalog = tok.nextToken();
			} else {
				catalog = null;
			}
			final String name = tok.nextToken();
			final ScriptType scriptType = ScriptType.valueOf(typeAsString);
			final DatabaseType databaseType = dbBundle.getType();
			final DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE
					.getScript(databaseType, scriptType);
			if (scriptDescription == null) {
				return null;
			}
			switch (scriptDescription.getPreferedType()) {
			case SQL:
				return this.loadFromSql(catalog, schema, name,
						scriptDescription);
			case TEMPLATE:
				return this.loadFromTemplate(catalog, schema, name,
						scriptDescription);
			default:
				logAndReturn(new ConfigurationException("Invalid prefered type"));
			}
			return null;
		}

		/**
		 * Loads the stream content by using a sql statement to fill it.
		 * 
		 * 
		 * @param catalog
		 * @param schema
		 * @param name
		 * @param scriptDescription
		 * @return the stream loaded from a sql query
		 * @throws Exception
		 */
		private byte[] loadFromSql(final String catalog, final String schema,
				final String name,
				final DatabaseMetadataScript scriptDescription)
				throws Exception {
			final Map<ColumnsNamesForMetadataSelect, String> columnValues = new EnumMap<ColumnsNamesForMetadataSelect, String>(
					ColumnsNamesForMetadataSelect.class);

			columnValues.put(ColumnsNamesForMetadataSelect.catalog_name,
					catalog);
			columnValues.put(ColumnsNamesForMetadataSelect.schema_name, schema);
			columnValues.put(ColumnsNamesForMetadataSelect.name, name);

			final StringTemplate template = new StringTemplate(
					scriptDescription.getContentSelect(),
					DefaultTemplateLexer.class);
			for (final Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues
					.entrySet()) {
				template.setAttribute(entry.getKey().name(), entry.getValue());
			}

			final String sql = template.toString();

			ResultSet resultSet = null;
			try {
				resultSet = this.conn.prepareStatement(sql).executeQuery();
				if (resultSet.next()) {
					final String content = resultSet.getString(1);
					return content.getBytes();
				}
			} catch (final Exception e) {
				logAndReturn("Error on Sql " + sql, e);
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
			return null;
		}

		/**
		 * Loads the stream content by using a {@link StringTemplate} and a sql
		 * statement to fill it.
		 * 
		 * 
		 * @param catalog
		 * @param schema
		 * @param name
		 * @param scriptDescription
		 * @return the stream loaded from a sql query and its
		 *         {@link StringTemplate}
		 * @throws Exception
		 */
		private byte[] loadFromTemplate(final String catalog,
				final String schema, final String name,
				final DatabaseMetadataScript scriptDescription)
				throws Exception {
			final Map<ColumnsNamesForMetadataSelect, String> columnValues = new EnumMap<ColumnsNamesForMetadataSelect, String>(
					ColumnsNamesForMetadataSelect.class);

			columnValues.put(ColumnsNamesForMetadataSelect.catalog_name,
					catalog);
			columnValues.put(ColumnsNamesForMetadataSelect.schema_name, schema);
			columnValues.put(ColumnsNamesForMetadataSelect.name, name);

			final StringTemplate template = new StringTemplate(
					scriptDescription.getTemplatesSelect(),
					DefaultTemplateLexer.class);
			for (final Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues
					.entrySet()) {
				template.setAttribute(entry.getKey().name(), entry.getValue());
			}

			final String sql = template.toString();

			ResultSet resultSet = null;
			try {
				resultSet = this.conn.prepareStatement(sql).executeQuery();
				final String templateString = scriptDescription.getTemplate();
				final CustomizedStringTemplate contentTemplate = new CustomizedStringTemplate(
						templateString, DefaultTemplateLexer.class);
				for (final Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues
						.entrySet()) {
					contentTemplate.setAttribute(entry.getKey().name(), entry
							.getValue());
				}
				int count = 0;
				boolean hasAnyResult = false;
				String attributeName = null;
				while (resultSet.next()) {
					final List<String> columnsFromDatabase = new ArrayList<String>();
					if (!hasAnyResult) {
						hasAnyResult = true;
						final StringBuilder baseForTemplate = new StringBuilder(
								"detail.{");
						final ResultSetMetaData metadata = resultSet
								.getMetaData();
						count = metadata.getColumnCount();
						for (int i = 1; i <= count; i++) {
							final String columnName = metadata
									.getColumnLabel(i).toLowerCase();
							final String content = resultSet.getString(i);
							contentTemplate.setAttribute(columnName, content);
							columnsFromDatabase.add(content);

							baseForTemplate.append(columnName);
							if (i != count) {
								baseForTemplate.append(',');
							}
						}
						baseForTemplate.append('}');
						attributeName = baseForTemplate.toString();
					} else {
						for (int i = 1; i <= count; i++) {
							columnsFromDatabase.add(resultSet.getString(i));
						}
					}
					final Object[] valuesAsArray = columnsFromDatabase
							.toArray();
					contentTemplate.setAttributeArray(attributeName,
							valuesAsArray);
				}
				resultSet.close();
				if (!hasAnyResult) {
					logAndReturn(new IllegalStateException("no result on "
							+ sql));

				}
				final String result = contentTemplate.toString();
				return result.getBytes();

			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void threadExecutionAboutToStart(final Bundle bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalExecutionContext) {
			try {
				final DbBundle dbBundle = (DbBundle) bundle;
				this.conn = createConnection(dbBundle);
			} catch (final Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void threadExecutionFinished(final Bundle bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalExecutionContext) {
			try {
				if ((this.conn != null) && !this.conn.isClosed()) {
					this.conn.close();
				}
			} catch (final Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
		}

	}

	/**
	 * This context will be used to load all database data
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	protected static class GlobalDatabaseContext extends
			DefaultGlobalExecutionContext {

		private String fillName(final ScriptType scriptType,
				final ResultSet resultSet) throws SQLException {
			final StringBuilder buffer = new StringBuilder();
			final String catalog = resultSet
					.getString(ColumnsNamesForMetadataSelect.catalog_name
							.name());
			final String name = resultSet
					.getString(ColumnsNamesForMetadataSelect.name.name());
			final String schema = resultSet
					.getString(ColumnsNamesForMetadataSelect.schema_name.name());
			buffer.append(schema);
			buffer.append('/');
			buffer.append(scriptType.name());
			buffer.append('/');
			if ((catalog != null) && !"".equals(catalog.trim())) {
				buffer.append(catalog);
				buffer.append('/');
			}
			buffer.append(name);
			final String result = buffer.toString();
			return result;
		}

		public Set<String> getAllArtifactNames(final Bundle bundle,
				final ArtifactMapping mapping) throws ConfigurationException {
			if (!(bundle instanceof DbBundle)) {
				return emptySet();
			}
			final DbBundle dbBundle = (DbBundle) bundle;
			try {
				Connection conn = null;
				final Set<String> loadedNames = new HashSet<String>();

				try {
					conn = createConnection(dbBundle);
					final DatabaseType databaseType = dbBundle.getType();
					for (final ScriptType scriptType : ScriptType.values()) {
						final DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE
								.getScript(databaseType, scriptType);
						if (scriptDescription == null) {
							continue;
						}
						final ResultSet resultSet = conn.prepareStatement(
								scriptDescription.getDataSelect())
								.executeQuery();
						while (resultSet.next()) {
							final String result = this.fillName(scriptType,
									resultSet);
							final String starting = mapping.getRelative();
							if (result.startsWith(starting)) {
								loadedNames.add(result.substring(starting
										.length()));
							}
						}
						resultSet.close();
					}
					return loadedNames;
				} catch (final Exception e) {
					logAndReturnNew(e, ConfigurationException.class);
				} finally {
					if ((conn != null) && !conn.isClosed()) {
						conn.close();
					}
				}
			} catch (final Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
			return emptySet();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Integer withThreadPoolSize(final Bundle bundle) {
			final Integer defaultValue = super.withThreadPoolSize(bundle);
			if (!(bundle instanceof DbBundle)) {
				return defaultValue;
			}
			final DbBundle dbBundle = (DbBundle) bundle;
			final Integer maxConnections = dbBundle.getMaxConnections();
			if ((maxConnections != null)
					&& (maxConnections.compareTo(defaultValue) < 0)) {
				return maxConnections;
			}
			return defaultValue;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GlobalExecutionContext createGlobalExecutionContext() {
		return new GlobalDatabaseContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ThreadExecutionContext createThreadExecutionContext() {
		return new DatabaseThreadExecutionContext();
	}

}
