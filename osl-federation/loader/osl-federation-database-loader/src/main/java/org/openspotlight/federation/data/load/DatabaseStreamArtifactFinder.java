package org.openspotlight.federation.data.load;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.PatternMatcher.isMatchingWithoutCaseSentitiveness;
import static org.openspotlight.federation.data.load.db.DatabaseSupport.createConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.load.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript.DatabaseArtifactNameHandler;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.AbstractArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinder;

public class DatabaseStreamArtifactFinder extends AbstractArtifactFinder<StreamArtifact>
    implements ArtifactFinder<StreamArtifact> {

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

    public boolean canAcceptArtifactSource( final ArtifactSource artifactSource ) {
        return artifactSource instanceof DbArtifactSource;
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

    public StreamArtifact findByPath( final ArtifactSource artifactSource,
                                      final String path ) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> retrieveAllArtifactNames( final ArtifactSource artifactSource,
                                                 final String initialPath ) {
        if (!(artifactSource instanceof DbArtifactSource)) {
            return emptySet();
        }
        final DbArtifactSource dbBundle = (DbArtifactSource)artifactSource;
        try {
            Connection conn = null;
            final Set<String> loadedNames = new HashSet<String>();

            try {
                conn = createConnection(dbBundle);
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
            } finally {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            }
        } catch (final Exception e) {
            logAndReturnNew(e, ConfigurationException.class);
        }
        return emptySet();
    }

}
