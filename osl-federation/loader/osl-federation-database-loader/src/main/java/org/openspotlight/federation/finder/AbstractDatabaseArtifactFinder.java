package org.openspotlight.federation.finder;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.PatternMatcher.isMatchingWithoutCaseSentitiveness;
import static org.openspotlight.federation.finder.db.DatabaseSupport.createConnection;

import java.sql.CallableStatement;
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
import java.util.concurrent.ConcurrentHashMap;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.finder.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript;
import org.openspotlight.federation.finder.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.finder.db.ScriptType;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript.DatabaseArtifactNameHandler;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript.DatabaseStreamHandler;
import org.openspotlight.federation.template.CustomizedStringTemplate;

public abstract class AbstractDatabaseArtifactFinder<A extends Artifact> extends AbstractArtifactFinder<A> {

    /**
     * Execute the statement as a normal SQL query or as a {@link CallableStatement} if the script starts with '{'.
     * 
     * @param sql the sql
     * @param connection the connection
     * @return the result set
     * @throws SQLException if anything wrong happens
     */
    static ResultSet executeStatement( final String sql,
                                       final Connection connection ) throws SQLException {
        ResultSet rs;
        if (sql.trim().startsWith("{")) {
            rs = connection.prepareCall(sql).executeQuery();
        } else {
            rs = connection.prepareStatement(sql).executeQuery();
        }
        return rs;
    }

    /**
     * Loads the stream content by using a sql statement to fill it.
     * 
     * @param catalog the catalog
     * @param schema the schema
     * @param name the name
     * @param scriptDescription the script description
     * @param streamHandler the stream handler
     * @return the stream loaded from a sql query
     * @throws Exception the exception
     */
    protected static byte[] loadFromSql( final String catalog,
                                         final String schema,
                                         final String name,
                                         final DatabaseMetadataScript scriptDescription,
                                         final DatabaseStreamHandler streamHandler,
                                         final Connection conn ) throws Exception {
        final Map<ColumnsNamesForMetadataSelect, String> columnValues = new EnumMap<ColumnsNamesForMetadataSelect, String>(
                                                                                                                           ColumnsNamesForMetadataSelect.class);

        columnValues.put(ColumnsNamesForMetadataSelect.catalog_name, catalog);
        columnValues.put(ColumnsNamesForMetadataSelect.schema_name, schema);
        columnValues.put(ColumnsNamesForMetadataSelect.name, name);

        final StringTemplate template = new StringTemplate(scriptDescription.getContentSelect(), DefaultTemplateLexer.class);
        for (final Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues.entrySet()) {
            template.setAttribute(entry.getKey().name(), entry.getValue());
        }
        if (streamHandler != null) {
            streamHandler.beforeFillTemplate(schema, scriptDescription.getScriptType(), catalog, name, template, conn);
        }

        final String sql = template.toString();

        ResultSet resultSet = null;
        try {
            resultSet = executeStatement(sql, conn);
            if (resultSet.next()) {
                final int columnToUse = scriptDescription.getContentColumnToUse() != null ? scriptDescription.getContentColumnToUse().intValue() : 1;
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
     * Loads the stream content by using a {@link StringTemplate} and a sql statement to fill it.
     * 
     * @param catalog the catalog
     * @param schema the schema
     * @param name the name
     * @param scriptDescription the script description
     * @param streamHandler the stream handler
     * @return the stream loaded from a sql query and its {@link StringTemplate}
     * @throws Exception the exception
     */
    protected static byte[] loadFromTemplate( final String catalog,
                                              final String schema,
                                              final String name,
                                              final DatabaseMetadataScript scriptDescription,
                                              final DatabaseStreamHandler streamHandler,
                                              final Connection conn ) throws Exception {
        final Map<ColumnsNamesForMetadataSelect, String> columnValues = new EnumMap<ColumnsNamesForMetadataSelect, String>(
                                                                                                                           ColumnsNamesForMetadataSelect.class);

        columnValues.put(ColumnsNamesForMetadataSelect.catalog_name, catalog);
        columnValues.put(ColumnsNamesForMetadataSelect.schema_name, schema);
        columnValues.put(ColumnsNamesForMetadataSelect.name, name);

        final StringTemplate template = new StringTemplate(scriptDescription.getTemplatesSelect(), DefaultTemplateLexer.class);
        for (final Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues.entrySet()) {
            template.setAttribute(entry.getKey().name(), entry.getValue());
        }

        final String sql = template.toString();

        ResultSet resultSet = null;
        try {
            final String templateString = scriptDescription.getTemplate();
            final CustomizedStringTemplate contentTemplate = new CustomizedStringTemplate(templateString,
                                                                                          DefaultTemplateLexer.class);
            for (final Map.Entry<ColumnsNamesForMetadataSelect, String> entry : columnValues.entrySet()) {
                contentTemplate.setAttribute(entry.getKey().name(), entry.getValue());
            }
            int count = 0;
            boolean hasAnyResult = false;
            String attributeName = null;
            if (sql != null && sql.trim().length() != 0) {
                resultSet = executeStatement(sql, conn);
                while (resultSet.next()) {
                    final List<String> columnsFromDatabase = new ArrayList<String>();
                    if (!hasAnyResult) {
                        hasAnyResult = true;
                        final StringBuilder baseForTemplate = new StringBuilder("detail.{");
                        final ResultSetMetaData metadata = resultSet.getMetaData();
                        count = metadata.getColumnCount();
                        for (int i = 1; i <= count; i++) {
                            final String columnName = metadata.getColumnLabel(i).toLowerCase();
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
                    final Object[] valuesAsArray = columnsFromDatabase.toArray();
                    contentTemplate.setAttributeArray(attributeName, valuesAsArray);
                }
                resultSet.close();
                if (!hasAnyResult) {
                    logAndReturn(new IllegalStateException("no result on " + sql));

                }
            }
            if (streamHandler != null) {
                streamHandler.beforeFillTemplate(schema, scriptDescription.getScriptType(), catalog, name, contentTemplate, conn);
            }

            final String result = contentTemplate.toString();
            return result.getBytes();

        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    private final Map<DbArtifactSource, Connection> connectionMap = new ConcurrentHashMap<DbArtifactSource, Connection>();

    public AbstractDatabaseArtifactFinder() {
        super();
    }

    public boolean canAcceptArtifactSource( final ArtifactSource artifactSource ) {
        return artifactSource instanceof DbArtifactSource;
    }

    @Override
    public synchronized void closeResources() {
        final ArrayList<Connection> connections = new ArrayList<Connection>(this.connectionMap.values());
        for (final Connection conn : connections) {
            try {
                conn.close();
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
            }
        }
        this.connectionMap.clear();
    }

    /**
     * Fill name based on the column names configured on {@link DatabaseMetadataScript} xml file.
     * 
     * @param script the script
     * @param resultSet the result set
     * @param nameHandler the name handler
     * @return the artifact name
     * @throws SQLException the SQL exception
     */
    private String fillName( final DatabaseMetadataScript script,
                             final ResultSet resultSet,
                             final DatabaseArtifactNameHandler nameHandler ) throws SQLException {
        final StringBuilder buffer = new StringBuilder();
        String catalogColumnName = script.getColumnAliasMap().get(ColumnsNamesForMetadataSelect.catalog_name);
        String nameColumnName = script.getColumnAliasMap().get(ColumnsNamesForMetadataSelect.name);
        String schemaColumnName = script.getColumnAliasMap().get(ColumnsNamesForMetadataSelect.schema_name);
        catalogColumnName = catalogColumnName != null ? catalogColumnName : ColumnsNamesForMetadataSelect.catalog_name.name();
        nameColumnName = nameColumnName != null ? nameColumnName : ColumnsNamesForMetadataSelect.name.name();
        schemaColumnName = schemaColumnName != null ? schemaColumnName : ColumnsNamesForMetadataSelect.schema_name.name();

        final String catalog = resultSet.getString(catalogColumnName);
        final String name = nameHandler == null ? resultSet.getString(nameColumnName) : nameHandler.fixName(resultSet.getString(nameColumnName));
        final String schema = resultSet.getString(schemaColumnName);
        buffer.append(schema);
        buffer.append('/');
        buffer.append(script.getScriptType().name());
        buffer.append('/');
        if (catalog != null && !"".equals(catalog.trim())) {
            buffer.append(catalog);
            buffer.append('/');
        }
        buffer.append(name);
        final String result = buffer.toString();
        return result;
    }

    protected synchronized Connection getConnectionFromSource( final DbArtifactSource dbBundle ) throws Exception {

        Connection conn = this.connectionMap.get(dbBundle);
        if (conn == null) {
            conn = createConnection(dbBundle);
            this.connectionMap.put(dbBundle, conn);
        }
        return conn;

    }

    public Set<String> retrieveAllArtifactNames( final ArtifactSource artifactSource,
                                                 final String initialPath ) {
        if (!(artifactSource instanceof DbArtifactSource)) {
            return emptySet();
        }
        final DbArtifactSource dbBundle = (DbArtifactSource)artifactSource;
        try {
            final Connection conn = this.getConnectionFromSource(dbBundle);
            synchronized (conn) {
                final Set<String> loadedNames = new HashSet<String>();

                try {
                    final DatabaseType databaseType = dbBundle.getType();
                    for (final ScriptType scriptType : ScriptType.values()) {
                        final DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE.getScript(
                                                                                                                          databaseType,
                                                                                                                          scriptType);
                        if (scriptDescription == null) {
                            continue;
                        }
                        final Class<? extends DatabaseArtifactNameHandler> dataHandlerType = scriptDescription.getNameHandlerClass();
                        final DatabaseArtifactNameHandler nameHandler = dataHandlerType != null ? dataHandlerType.newInstance() : null;

                        final ResultSet resultSet = executeStatement(scriptDescription.getDataSelect(), conn);
                        walkingOnResult: while (resultSet.next()) {
                            final String result = this.fillName(scriptDescription, resultSet, nameHandler);
                            if (nameHandler != null) {
                                final boolean shouldProcess = nameHandler.shouldIncludeName(result, scriptType, resultSet);
                                if (!shouldProcess) {
                                    continue walkingOnResult;
                                }
                            }
                            if (isMatchingWithoutCaseSentitiveness(result, initialPath + "*")) {
                                loadedNames.add(result);
                            }

                        }
                        resultSet.close();
                    }
                    return loadedNames;
                } catch (final Exception e) {
                    logAndReturnNew(e, ConfigurationException.class);
                }

            }
        } catch (final Exception e) {
            logAndReturnNew(e, ConfigurationException.class);
        }
        return emptySet();
    }

}
