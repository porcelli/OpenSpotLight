/**
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

import static com.google.common.collect.Lists.newLinkedList;
import static org.openspotlight.common.util.Exceptions.logAndReturn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.DbArtifactSource;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.domain.artifact.db.DatabaseType;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript;
import org.openspotlight.federation.finder.db.DatabaseMetadataScript.DatabaseStreamHandler;
import org.openspotlight.federation.finder.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.finder.db.ScriptType;

public class DatabaseStreamArtifactFinder extends AbstractDatabaseArtifactFinder {

    @Override
    protected <A extends Artifact> boolean internalAccept(final ArtifactSource source,
                                                           final Class<A> type)
        throws Exception {
        return source instanceof DbArtifactSource && availableTypes.contains(type);
    }

    @Override
    protected <A extends Artifact> A internalFindByPath(final Class<A> type,
                                                         final ArtifactSource source,
                                                         final String path, final String encoding)
        throws Exception {
        final DbArtifactSource artifactSource = (DbArtifactSource) source;

        final Connection conn = getConnectionFromSource(artifactSource);
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
            final DatabaseType databaseType = artifactSource.getType();
            final DatabaseMetadataScript scriptDescription = DatabaseMetadataScriptManager.INSTANCE.getScript(databaseType,
                                                                                                              scriptType);
            if (scriptDescription == null) { return null; }

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
                            content = loadFromTemplate(catalog, schema, name, scriptDescription, streamHandler, conn);

                            break;
                        case TEMPLATE:
                            content = loadFromSql(catalog, schema, name, scriptDescription, streamHandler, conn);
                            break;

                    }
                }
            }
            if (content == null) { return null; }

            if (streamHandler != null) {
                content = streamHandler.afterStreamProcessing(schema, scriptType, catalog, name, content, conn);
            }
            final List<String> contentAsStringList = asStringList(content, encoding);
            final StringArtifact sa = Artifact.createArtifact(StringArtifact.class, path, ChangeType.INCLUDED);
            sa.getContent().setTransient(contentAsStringList);
            @SuppressWarnings("unchecked")
            final A a = (A) sa;
            return a;
        }
    }

    private List<String> asStringList(final byte[] content, final String encoding)
        throws IOException {
        final List<String> lines = newLinkedList();
        final BufferedReader reader =
            new BufferedReader(new StringReader(encoding != null ? new String(content, encoding) : new String(content)));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    @SuppressWarnings("unchecked")
    private final Set<Class<? extends Artifact>> availableTypes = SLCollections
                                                                    .<Class<? extends Artifact>>setOf(StringArtifact.class);

    @Override
    protected Set<Class<? extends Artifact>> internalGetAvailableTypes()
        throws Exception {
        return availableTypes;
    }

    @Override
    protected boolean internalIsTypeSupported(final Class<? extends Artifact> type)
        throws Exception {
        return StringArtifact.class.equals(type);
    }

}
