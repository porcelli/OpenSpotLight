package org.openspotlight.federation.data;

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
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.PathElement;
import org.openspotlight.federation.domain.StreamArtifact;

public class LocalSourceStreamArtifactFinder implements ArtifactFinder<StreamArtifact> {

    public boolean canAcceptArtifactSource( final ArtifactSource artifactSource ) {
        return true;
    }

    public StreamArtifact findByPath( final ArtifactSource artifactSource,
                                      final String rawPath ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkNotEmpty("rawPath", rawPath);
        for (final ChangeType t : ChangeType.values()) {
            try {

                final String location = MessageFormat.format("./{0}/{1}/{2}", artifactSource.getInitialLookup(),
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
                final StreamArtifact streamArtifact = StreamArtifact.createNewStreamArtifact(artifactSource.getUniqueReference()
                                                                                             + "/" + rawPath, t, content);
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
        String newPath = PathElement.createRelativePath(relativeTo.getParent(), path).getCompletePath();
        newPath = Strings.removeBegginingFrom(artifactSource.getUniqueReference() + "/", newPath);

        return this.findByPath(artifactSource, newPath);
    }

    public Set<StreamArtifact> listByPath( final ArtifactSource artifactSource,
                                           final String rawPath ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkNotEmpty("rawPath", rawPath);
        try {
            final Set<StreamArtifact> result = new HashSet<StreamArtifact>();
            for (final ChangeType t : ChangeType.values()) {

                final String location = MessageFormat.format("./{0}/{1}/{2}", artifactSource.getInitialLookup(),
                                                             t.toString().toLowerCase(), rawPath);

                final File initialDir = new File(location);
                final String pathToRemove = initialDir.getCanonicalPath().substring(
                                                                                    0,
                                                                                    initialDir.getCanonicalPath().length()
                                                                                    - rawPath.length() - 1);
                final Set<String> pathList = Files.listFileNamesFrom(location);

                for (final String p : pathList) {
                    final String correctRelativePath = Strings.removeBegginingFrom(pathToRemove, p);
                    final StreamArtifact sa = this.findByPath(artifactSource, correctRelativePath);
                    if (sa != null) {
                        result.add(sa);
                    }
                }
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public Set<String> retrieveAllArtifactNames( final ArtifactSource artifactSource ) {
        try {
            final Set<String> pathList = Files.listFileNamesFrom(artifactSource.getInitialLookup());

            return pathList;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

}
