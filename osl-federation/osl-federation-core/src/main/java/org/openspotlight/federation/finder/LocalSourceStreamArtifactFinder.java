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

    private final ArtifactSource artifactSource;

    public LocalSourceStreamArtifactFinder(
                                            final ArtifactSource artifactSource ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkCondition("fileExists", new File(artifactSource.getInitialLookup() + "/").exists());
        this.artifactSource = artifactSource;
    }

    public void closeResources() {
        // TODO Auto-generated method stub

    }

    public StreamArtifact findByPath( final String rawPath ) {
        Assertions.checkNotEmpty("rawPath", rawPath);
        for (final ChangeType t : ChangeType.values()) {
            try {

                final String location = MessageFormat.format("{0}/{1}/{2}", this.artifactSource.getInitialLookup(),
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

    public StreamArtifact findByRelativePath( final StreamArtifact relativeTo,
                                              final String path ) {
        Assertions.checkNotNull("artifactSource", this.artifactSource);
        Assertions.checkNotNull("relativeTo", relativeTo);
        Assertions.checkNotEmpty("path", path);
        final String newPath = PathElement.createRelativePath(relativeTo.getParent(), path).getCompletePath();

        return this.findByPath(newPath);
    }

    public Class<StreamArtifact> getArtifactType() {
        return StreamArtifact.class;
    }

    public Class<? extends ArtifactSource> getSourceType() {
        return null;
    }

    public Set<StreamArtifact> listByPath( final String rawPath ) {
        try {
            final Set<StreamArtifact> result = new HashSet<StreamArtifact>();
            final Set<String> allFilePaths = this.retrieveAllArtifactNames(rawPath);
            for (final String path : allFilePaths) {
                final StreamArtifact sa = this.findByPath(path);
                result.add(sa);
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public Set<String> retrieveAllArtifactNames( final String initialPath ) {
        final String rawPath = initialPath == null ? "." : initialPath;
        try {
            final Set<String> result = new HashSet<String>();
            for (final ChangeType t : ChangeType.values()) {

                final String location = MessageFormat.format("{0}/{1}/{2}", this.artifactSource.getInitialLookup(),
                                                             t.toString().toLowerCase(), rawPath);

                final String pathToRemove = new File(this.artifactSource.getInitialLookup()).getCanonicalPath() + "/"
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
