package org.openspotlight.federation.finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Files;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.PathElement;
import org.openspotlight.federation.domain.StreamArtifact;

public class LocalSourceStreamArtifactFinder implements ArtifactFinder<StreamArtifact> {

    public boolean canAcceptArtifactSource( final ArtifactSource artifactSource ) {
        for (final ChangeType t : ChangeType.values()) {
            if (new File(artifactSource.getInitialLookup() + "/" + t.toString().toLowerCase()).exists()) {
                return true;
            }
        }
        return false;
    }

    public void closeResources() {
        // TODO Auto-generated method stub

    }

    public StreamArtifact findByPath( final ArtifactSource artifactSource,
                                      final String rawPath ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkNotEmpty("rawPath", rawPath);
        for (final ChangeType t : ChangeType.values()) {
            try {

                final String location = MessageFormat.format("{0}/{1}/{2}", artifactSource.getInitialLookup(),
                                                             t.toString().toLowerCase(), rawPath);

                final File file = new File(location);
                if (!file.exists()) {
                    continue;
                }

                final FileInputStream resource = new FileInputStream(file);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
                final StringBuilder buffer = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }
                final String content = buffer.toString();
                final StreamArtifact streamArtifact = Artifact.createArtifact(StreamArtifact.class, rawPath, t);
                streamArtifact.setContent(content);
                return streamArtifact;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        return null;

    }

    public StreamArtifact findByRelativePath( final ArtifactSource artifactSource,
                                              final StreamArtifact relativeTo,
                                              final String path ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkNotNull("relativeTo", relativeTo);
        Assertions.checkNotEmpty("path", path);
        final String newPath = PathElement.createRelativePath(relativeTo.getParent(), path).getCompletePath();

        return this.findByPath(artifactSource, newPath);
    }

    public Set<StreamArtifact> listByPath( final ArtifactSource artifactSource,
                                           final String rawPath ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        try {
            final Set<StreamArtifact> result = new HashSet<StreamArtifact>();
            final Set<String> allFilePaths = this.retrieveAllArtifactNames(artifactSource, rawPath);
            for (final String path : allFilePaths) {
                final StreamArtifact sa = this.findByPath(artifactSource, path);
                result.add(sa);
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public Set<String> retrieveAllArtifactNames( final ArtifactSource artifactSource,
                                                 final String initialPath ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        final String rawPath = initialPath == null ? "." : initialPath;
        try {
            final Set<String> result = new HashSet<String>();
            for (final ChangeType t : ChangeType.values()) {

                final String location = MessageFormat.format("{0}/{1}/{2}", artifactSource.getInitialLookup(),
                                                             t.toString().toLowerCase(), rawPath);

                final String pathToRemove = new File(artifactSource.getInitialLookup()).getCanonicalPath() + "/"
                                            + t.toString().toLowerCase() + "/";
                final Set<String> pathList = Files.listFileNamesFrom(location);

                for (final String p : pathList) {
                    final String correctRelativePath = Strings.removeBegginingFrom(pathToRemove, p);
                    result.add(correctRelativePath);
                }
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }
}
