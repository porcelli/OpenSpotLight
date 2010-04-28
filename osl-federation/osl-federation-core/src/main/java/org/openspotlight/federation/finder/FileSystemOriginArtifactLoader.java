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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Files;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;

public class FileSystemOriginArtifactLoader extends AbstractOriginArtifactLoader {

    @Override
    protected void internalCloseResources() {

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected <A extends Artifact> A internalFindByPath( Class<A> type,
                                                         ArtifactSource source,
                                                         String rawPath ) {
        Assertions.checkNotEmpty("rawPath", rawPath);
        Assertions.checkCondition("validTypeAndConfig", (type.equals(StringArtifact.class) && !source.isBinary())
                                                        || (type.equals(StreamArtifact.class) && source.isBinary()));
        try {

            String[] pathInfo = fixPathInformation(source, rawPath);
            final String path = pathInfo[0];
            final String location = pathInfo[1];

            final File file = new File(location);
            if (!file.exists()) {
                return null;
            }
            if (!file.isFile()) {
                return null;
            }
            if (StringArtifact.class.equals(type)) {
                final FileInputStream resource = new FileInputStream(file);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
                final StringBuilder buffer = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }
                final String content = buffer.toString();
                final StringArtifact artifact = Artifact.createArtifact(StringArtifact.class, "/" + path, ChangeType.INCLUDED);
                artifact.getContent().setTransient(content);
                return (A)artifact;
            } else {// StreamArtifact
                final FileInputStream resource = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(resource, baos);
                final StreamArtifact artifact = Artifact.createArtifact(StreamArtifact.class, "/" + path, ChangeType.INCLUDED);
                artifact.getContent().setTransient(new ByteArrayInputStream(baos.toByteArray()));
                return (A)artifact;
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @SuppressWarnings( "unchecked" )
    private static final Set<Class<? extends Artifact>> availableTypes = SLCollections.<Class<? extends Artifact>>setOf(
                                                                                                                        StringArtifact.class,
                                                                                                                        StreamArtifact.class);

    @Override
    protected Set<Class<? extends Artifact>> internalGetAvailableTypes() {
        return availableTypes;
    }

    @Override
    protected <A extends Artifact> boolean internalIsMaybeChanged( ArtifactSource source,
                                                                   String artifactName,
                                                                   A oldOne ) {
        String[] pathInfo = fixPathInformation(source, artifactName);
        final String location = pathInfo[1];

        final File file = new File(location);
        if (!file.exists()) {
            return true;
        }
        if (!file.isFile()) {
            return true;
        }
        if (file.lastModified() != oldOne.getLastChange()) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean internalIsTypeSupported( Class<? extends Artifact> type ) {
        return availableTypes.contains(type);
    }

    @Override
    protected <A extends Artifact> Set<String> internalRetrieveOriginalNames( Class<A> type,
                                                                              ArtifactSource artifactSource,
                                                                              String initialPath ) {
        try {
            final String rawPath = initialPath == null ? "." : initialPath;

            String initialLookup = artifactSource.getInitialLookup();
            if (initialLookup.endsWith("/")) {
                initialLookup = initialLookup.substring(0, initialLookup.length() - 1);
            }
            String newPath = rawPath;
            if (newPath.startsWith("/")) {
                newPath = newPath.substring(1);
            }
            final String location = MessageFormat.format("{0}/{1}", artifactSource.getInitialLookup(), newPath);

            final String pathToRemove = Files.getNormalizedFileName(new File(artifactSource.getInitialLookup()));

            final Set<String> pathList = Files.listFileNamesFrom(location, false);

            final Set<String> result = new HashSet<String>();
            for (final String p : pathList) {
                if (new File(p).isFile()) {
                    final String correctRelativePath = Strings.removeBegginingFrom(pathToRemove, p);
                    result.add(correctRelativePath);
                }
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    private String[] fixPathInformation( ArtifactSource source,
                                         String rawPath ) {
        final String path = rawPath.startsWith("/") ? Strings.removeBegginingFrom("/", rawPath) : rawPath;
        final String location = MessageFormat.format("{0}/{1}", source.getInitialLookup(), path);

        return new String[] {path, location};
    }

    @Override
    protected <A extends Artifact> boolean internalAccept( ArtifactSource source,
                                                           Class<A> type ) {
        if (!availableTypes.contains(type)) return false;
        File f = new File(source.getInitialLookup());
        if (!f.exists() || !f.isDirectory()) return false;
        if (type.equals(StringArtifact.class) && !source.isBinary()) return true;
        if (type.equals(StreamArtifact.class) && source.isBinary()) return true;
        return false;
    }

    @Override
    protected boolean isMultithreaded() {
        return true;
    }

}
