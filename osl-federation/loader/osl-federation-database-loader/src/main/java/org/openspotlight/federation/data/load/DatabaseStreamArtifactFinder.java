package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Exceptions.logAndReturn;

import java.sql.Connection;
import java.util.StringTokenizer;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.AbstractDatabaseArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript;
import org.openspotlight.federation.finder.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.finder.db.ScriptType;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript.DatabaseStreamHandler;

public class DatabaseStreamArtifactFinder extends AbstractDatabaseArtifactFinder<StreamArtifact>
    implements ArtifactFinder<StreamArtifact> {

    public StreamArtifact findByPath( final ArtifactSource artifactSource,
                                      final String path ) {
        try {
            final DbArtifactSource dbBundle = (DbArtifactSource)artifactSource;

            final Connection conn = this.getConnectionFromSource(dbBundle);
            synchronized (conn) {
                final StringTokenizer tok = new StringTokenizer(path, "/"); //$NON-NLS-1$
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
                final DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE.getScript(databaseType,
                                                                                                                  scriptType);
                if (scriptDescription == null) {
                    return null;
                }

                final Class<? extends DatabaseStreamHandler> streamHandlerType = scriptDescription.getStreamHandlerClass();
                final DatabaseStreamHandler streamHandler;
                if (streamHandlerType != null) {
                    streamHandler = streamHandlerType.newInstance();
                } else {
                    streamHandler = null;
                }
                byte[] content;
                switch (scriptDescription.getPreferedType()) {
                    case SQL:
                        content = loadFromSql(catalog, schema, name, scriptDescription, streamHandler, conn);
                        break;
                    case TEMPLATE:
                        content = loadFromTemplate(catalog, schema, name, scriptDescription, streamHandler, conn);
                        break;
                    default:
                        content = null;
                        logAndReturn(new ConfigurationException("Invalid prefered type"));
                }
                if (content == null) {
                    if (scriptDescription.isTryAgainIfNoResult()) {
                        switch (scriptDescription.getPreferedType()) {
                            case SQL:
                                content = this.loadFromTemplate(catalog, schema, name, scriptDescription, streamHandler, conn);

                                break;
                            case TEMPLATE:
                                content = this.loadFromSql(catalog, schema, name, scriptDescription, streamHandler, conn);
                                break;

                        }
                    }
                }
                if (content == null) {
                    return null;
                }

                if (streamHandler != null) {
                    content = streamHandler.afterStreamProcessing(schema, scriptType, catalog, name, content, conn);
                }
                final String contentAsString = new String(content);
                final StreamArtifact sa = Artifact.createArtifact(StreamArtifact.class, path, ChangeType.INCLUDED);
                sa.setContent(contentAsString);

                return sa;

            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

}
