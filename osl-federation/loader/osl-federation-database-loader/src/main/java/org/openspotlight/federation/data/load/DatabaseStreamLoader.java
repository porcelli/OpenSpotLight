package org.openspotlight.federation.data.load;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.PatternMatcher.isMatchingWithoutCaseSentitiveness;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.CallableStatement;
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
import org.openspotlight.federation.data.impl.ArtifactSource;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.impl.DbArtifactSource;
import org.openspotlight.federation.data.impl.StreamArtifactAboutToChange;
import org.openspotlight.federation.data.load.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript.DatabaseArtifactNameHandler;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript.DatabaseStreamHandler;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript.PreferedType;
import org.openspotlight.federation.template.CustomizedStringTemplate;

/**
 * This artifact loader loads all scripts used to create different database
 * artifacts. This artifacts should be loaded as a {@link StreamArtifactAboutToChange} with
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
 * &lt;scriptType&gt;FUNCTION&lt;/scriptType&gt;
 * &lt;database&gt;DB2&lt;/database&gt;
 * &lt;preferedType&gt;SQL&lt;/preferedType&gt;
 * &lt;dataSelect&gt;&lt;![CDATA[
 * select
 * funcschema as schema_name,
 * funcname as name,
 * '' as catalog_name
 * from syscat.FUNCTIONS
 * ]]&gt;&lt;/dataSelect&gt;
 * &lt;contentSelect&gt;&lt;![CDATA[
 * select body
 * from syscat.FUNCTIONS
 * where
 * funcschema = '$schema_name$' and
 * funcname  = '$name$'
 * ]]&gt;&lt;/contentSelect&gt;
 * &lt;/script&gt;
 * </pre>
 * 
 * <b>Template way</b>
 * 
 * <pre>
 * &lt;script&gt;
 * &lt;scriptType&gt;INDEX&lt;/scriptType&gt;
 * &lt;database&gt;DB2&lt;/database&gt;
 * &lt;preferedType&gt;TEMPLATE&lt;/preferedType&gt;
 * &lt;template&gt;&lt;![CDATA[
 * create index $name$ on $detail.tabname$ ( $detail.colnames$ )
 * ]]&gt;&lt;/template&gt;
 * 
 * &lt;dataSelect&gt;&lt;![CDATA[
 * select
 * indschema as schema_name,
 * indname as name,
 * '' as catalog_name
 * from syscat.INDEXES
 * ]]&gt;&lt;/dataSelect&gt;
 * &lt;templatesSelect&gt;&lt;![CDATA[
 * select
 * tabname, colnames
 * from syscat.indexes
 * where
 * indschema = '$schema_name$' and
 * indname = '$name$'
 * ]]&gt;&lt;/templatesSelect&gt;
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
 * There's a few ways to change the behavior of stream artifact loading for
 * databases.
 * <ul>
 * <li>During a loading process of {@link PreferedType#SQL sql type} it takes
 * the content as the first column for the resulting select. To change the
 * column to use (the second one, for example) as the {@link StreamArtifactAboutToChange}
 * content, just add the
 * <code> &lt;contentColumnToUse&gt;2&lt;/contentColumnToUse&gt; </code> xml
 * attribute.</li>
 * <li>To change the {@link ColumnsNamesForMetadataSelect column names used
 * during the data select} just add the following xml fragment with the desired
 * information:
 * 
 * <pre>
 * 	&lt;columnAliasMap enum-type=&quot;column&quot;&gt;
 * 		&lt;entry&gt;
 * 			&lt;column&gt;valid_enum_type&lt;/column&gt;
 * 			&lt;string&gt;newName&lt;/string&gt;
 * 		&lt;/entry&gt;
 * 	&lt;/columnAliasMap&gt;
 * </pre>
 * 
 * </li>
 * 
 * <li>Also is important to know: In any place where is possible to use a valid
 * sql statements, its possible also to use callable statements. Use the common
 * java syntax:
 * 
 * <pre>
 * {call exampleCallable('stringParameter',3,null)}
 * </pre>
 * 
 * Remember: this exampleCallable should return a result set.</li>
 * <li>When it's necessary to return a different type when the other one doesn't
 * have any result (example: {@link PreferedType#TEMPLATE} when
 * {@link PreferedType#SQL} doesn't have any result) just add the attribute
 * 
 * <pre>
 * &lt;tryAgainIfNoResult&gt;true&lt;/tryAgainIfNoResult&gt;
 * </pre>
 * 
 * to the xml file.</li>
 * </ul>
 * 
 * There's also some callback classes to be used on situations where just the
 * {@link PreferedType#SQL} or {@link PreferedType#TEMPLATE} isn't enough. Just
 * implement the interface {@link DatabaseArtifactNameHandler} to decide if an
 * artifact should be loaded or not, and implement the interface
 * {@link DatabaseStreamLoader} to change the behavior of artifact loading for
 * the artifact been loaded. Here it's possible also to ignore this artifact.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 * TODO MySql 4 table and indice stream loading (easy)
 * TODO Postgresql 7 all artifact loading (medium)
 * TODO SqlServer 2000 trigger (easy)
 * TODO oracle 8 table loading (already done, but not on SCM)
 * TODO Script validation from database (hard - not done yet)
 */
public class DatabaseStreamLoader extends AbstractArtifactLoader {

	/**
	 * {@link ThreadExecutionContext} class for loading database
	 * {@link StreamArtifactAboutToChange stream artifacts} using direct sql or template
	 * filled with sql.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 */
	protected static class DatabaseThreadExecutionContext extends
			DefaultThreadExecutionContext {

		/** The conn. */
		private Connection conn;

		/**
		 * {@inheritDoc}
		 */
		public byte[] loadArtifactOrReturnNullToIgnore(final ArtifactSource bundle,
				final ArtifactMapping mapping, final String artifactName,
				final GlobalExecutionContext globalContext) throws Exception {
			final DbArtifactSource dbBundle = (DbArtifactSource) bundle;

			final StringTokenizer tok = new StringTokenizer(artifactName, "/"); //$NON-NLS-1$
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

			final Class<? extends DatabaseStreamHandler> streamHandlerType = scriptDescription
					.getStreamHandlerClass();
			final DatabaseStreamHandler streamHandler;
			if (streamHandlerType != null) {
				streamHandler = streamHandlerType.newInstance();
			} else {
				streamHandler = null;
			}
			byte[] content;
			switch (scriptDescription.getPreferedType()) {
			case SQL:
				content = this.loadFromSql(catalog, schema, name,
						scriptDescription, streamHandler);
				break;
			case TEMPLATE:
				content = this.loadFromTemplate(catalog, schema, name,
						scriptDescription, streamHandler);
				break;
			default:
				content = null;
				logAndReturn(new ConfigurationException("Invalid prefered type"));
			}
			if (content == null) {
				if (scriptDescription.isTryAgainIfNoResult()) {
					switch (scriptDescription.getPreferedType()) {
					case SQL:
						content = this.loadFromTemplate(catalog, schema, name,
								scriptDescription, streamHandler);

						break;
					case TEMPLATE:
						content = this.loadFromSql(catalog, schema, name,
								scriptDescription, streamHandler);
						break;

					}
				}
			}
			if (content == null) {
				return null;
			}
			if (streamHandler != null) {
				final byte[] newContent = streamHandler.afterStreamProcessing(
						schema, scriptType, catalog, name, content, this.conn);
				return newContent;
			}
			return content;

		}

		/**
		 * Loads the stream content by using a sql statement to fill it.
		 * 
		 * @param catalog
		 *            the catalog
		 * @param schema
		 *            the schema
		 * @param name
		 *            the name
		 * @param scriptDescription
		 *            the script description
		 * @param streamHandler
		 *            the stream handler
		 * 
		 * @return the stream loaded from a sql query
		 * 
		 * @throws Exception
		 *             the exception
		 */
		private byte[] loadFromSql(final String catalog, final String schema,
				final String name,
				final DatabaseMetadataScript scriptDescription,
				final DatabaseStreamHandler streamHandler) throws Exception {
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
			if (streamHandler != null) {
				streamHandler.beforeFillTemplate(schema, scriptDescription
						.getScriptType(), catalog, name, template, this.conn);
			}

			final String sql = template.toString();

			ResultSet resultSet = null;
			try {
				resultSet = executeStatement(sql, this.conn);
				if (resultSet.next()) {
					final int columnToUse = scriptDescription
							.getContentColumnToUse() != null ? scriptDescription
							.getContentColumnToUse().intValue()
							: 1;
					final String content = resultSet.getString(columnToUse);
					return content != null ? content.getBytes() : null;
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
		 * @param catalog
		 *            the catalog
		 * @param schema
		 *            the schema
		 * @param name
		 *            the name
		 * @param scriptDescription
		 *            the script description
		 * @param streamHandler
		 *            the stream handler
		 * 
		 * @return the stream loaded from a sql query and its
		 *         {@link StringTemplate}
		 * 
		 * @throws Exception
		 *             the exception
		 */
		private byte[] loadFromTemplate(final String catalog,
				final String schema, final String name,
				final DatabaseMetadataScript scriptDescription,
				final DatabaseStreamHandler streamHandler) throws Exception {
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
				if ((sql != null) && (sql.trim().length() != 0)) {
					resultSet = executeStatement(sql, this.conn);
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
								contentTemplate.setAttribute(columnName,
										content);
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
				}
				if (streamHandler != null) {
					streamHandler.beforeFillTemplate(schema, scriptDescription
							.getScriptType(), catalog, name, contentTemplate,
							this.conn);
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
		public void threadExecutionAboutToStart(final ArtifactSource bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalExecutionContext) {
			try {
				final DbArtifactSource dbBundle = (DbArtifactSource) bundle;
				this.conn = createConnection(dbBundle);
			} catch (final Exception e) {
				logAndReturnNew(e, ConfigurationException.class);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void threadExecutionFinished(final ArtifactSource bundle,
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
	 * This context will be used to load all database data.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 */
	protected static class GlobalDatabaseContext extends
			DefaultGlobalExecutionContext {

		/**
		 * Fill name based on the column names configured on
		 * {@link DatabaseMetadataScript} xml file.
		 * 
		 * @param script
		 *            the script
		 * @param resultSet
		 *            the result set
		 * @param nameHandler
		 *            the name handler
		 * 
		 * @return the artifact name
		 * 
		 * @throws SQLException
		 *             the SQL exception
		 */
		private String fillName(final DatabaseMetadataScript script,
				final ResultSet resultSet,
				final DatabaseArtifactNameHandler nameHandler)
				throws SQLException {
			final StringBuilder buffer = new StringBuilder();
			String catalogColumnName = script.getColumnAliasMap().get(
					ColumnsNamesForMetadataSelect.catalog_name);
			String nameColumnName = script.getColumnAliasMap().get(
					ColumnsNamesForMetadataSelect.name);
			String schemaColumnName = script.getColumnAliasMap().get(
					ColumnsNamesForMetadataSelect.schema_name);
			catalogColumnName = catalogColumnName != null ? catalogColumnName
					: ColumnsNamesForMetadataSelect.catalog_name.name();
			nameColumnName = nameColumnName != null ? nameColumnName
					: ColumnsNamesForMetadataSelect.name.name();
			schemaColumnName = schemaColumnName != null ? schemaColumnName
					: ColumnsNamesForMetadataSelect.schema_name.name();

			final String catalog = resultSet.getString(catalogColumnName);
			final String name = nameHandler == null ? resultSet
					.getString(nameColumnName) : nameHandler.fixName(resultSet
					.getString(nameColumnName));
			final String schema = resultSet.getString(schemaColumnName);
			buffer.append(schema);
			buffer.append('/');
			buffer.append(script.getScriptType().name());
			buffer.append('/');
			if ((catalog != null) && !"".equals(catalog.trim())) {
				buffer.append(catalog);
				buffer.append('/');
			}
			buffer.append(name);
			final String result = buffer.toString();
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set<String> getAllArtifactNames(final ArtifactSource bundle,
				final ArtifactMapping mapping) throws ConfigurationException {
			if (!(bundle instanceof DbArtifactSource)) {
				return emptySet();
			}
			final DbArtifactSource dbBundle = (DbArtifactSource) bundle;
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
						final Class<? extends DatabaseArtifactNameHandler> dataHandlerType = scriptDescription
								.getNameHandlerClass();
						final DatabaseArtifactNameHandler nameHandler = dataHandlerType != null ? dataHandlerType
								.newInstance()
								: null;

						final ResultSet resultSet = executeStatement(
								scriptDescription.getDataSelect(), conn);
						walkingOnResult: while (resultSet.next()) {
							final String result = this.fillName(
									scriptDescription, resultSet, nameHandler);
							if (nameHandler != null) {
								final boolean shouldProcess = nameHandler
										.shouldIncludeName(result, scriptType,
												resultSet);
								if (!shouldProcess) {
									continue walkingOnResult;
								}
							}
							final String starting = mapping.getRelative();
							if (isMatchingWithoutCaseSentitiveness(result,
									starting + "*")) {
								loadedNames.add(result);
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
		public Integer withThreadPoolSize(final ArtifactSource bundle) {
			final Integer defaultValue = super.withThreadPoolSize(bundle);
			if (!(bundle instanceof DbArtifactSource)) {
				return defaultValue;
			}
			final DbArtifactSource dbBundle = (DbArtifactSource) bundle;
			final Integer maxConnections = dbBundle.getMaxConnections();
			if ((maxConnections != null)
					&& (maxConnections.compareTo(defaultValue) < 0)) {
				return maxConnections;
			}
			return defaultValue;
		}

	}

	/**
	 * Execute the statement as a normal SQL query or as a
	 * {@link CallableStatement} if the script starts with '{'.
	 * 
	 * @param sql
	 *            the sql
	 * @param connection
	 *            the connection
	 * 
	 * @return the result set
	 * 
	 * @throws SQLException
	 *             if anything wrong happens
	 */
	static ResultSet executeStatement(final String sql,
			final Connection connection) throws SQLException {
		ResultSet rs;
		if (sql.trim().startsWith("{")) {
			rs = connection.prepareCall(sql).executeQuery();
		} else {
			rs = connection.prepareStatement(sql).executeQuery();
		}
		return rs;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String fixMapping(final String mapString, final ArtifactSource bundle,
			final ArtifactMapping mapping) {
		return mapping.getRelative() + mapString;
	}

}
