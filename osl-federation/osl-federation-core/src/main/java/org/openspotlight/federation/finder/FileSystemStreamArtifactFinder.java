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
import org.openspotlight.federation.domain.StreamArtifact;

public class FileSystemStreamArtifactFinder extends AbstractArtifactFinder<StreamArtifact> {

    private final ArtifactSource artifactSource;

    public FileSystemStreamArtifactFinder(
                                           final ArtifactSource artifactSource ) {
        Assertions.checkNotNull("artifactSource", artifactSource);
        Assertions.checkCondition("sourceExists", new File(artifactSource.getInitialLookup()).exists());
        this.artifactSource = artifactSource;
    }

    public StreamArtifact findByPath( final String rawPath ) {
        Assertions.checkNotEmpty("rawPath", rawPath);
        final String path = rawPath.startsWith("/") ? Strings.removeBegginingFrom("/", rawPath) : rawPath;
        try {

            final String location = MessageFormat.format("{0}/{1}", this.artifactSource.getInitialLookup(), path);

            final File file = new File(location);
            if (!file.exists()) {
                return null;
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
            final StreamArtifact streamArtifact = Artifact.createArtifact(StreamArtifact.class, "/" + path, ChangeType.INCLUDED);
            streamArtifact.setContent(content);
            return streamArtifact;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public Class<StreamArtifact> getArtifactType() {
        return StreamArtifact.class;
    }

    public Class<? extends ArtifactSource> getSourceType() {
        return ArtifactSource.class;
    }

    public Set<String> retrieveAllArtifactNames( final String initialPath ) {
        final String rawPath = initialPath == null ? "." : initialPath;
        try {
            final Set<String> result = new HashSet<String>();
            String initialLookup = this.artifactSource.getInitialLookup();
            if (initialLookup.endsWith("/")) {
                initialLookup = initialLookup.substring(0, initialLookup.length() - 1);
            }
            String newPath = rawPath;
            if (newPath.startsWith("/")) {
                newPath = newPath.substring(1);
            }
            final String location = MessageFormat.format("{0}/{1}", this.artifactSource.getInitialLookup(), newPath);

            final String pathToRemove = new File(this.artifactSource.getInitialLookup()).getCanonicalPath();

            final Set<String> pathList = Files.listFileNamesFrom(location);

            for (final String p : pathList) {
                final String correctRelativePath = Strings.removeBegginingFrom(pathToRemove, p);
                result.add(correctRelativePath);
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

}
