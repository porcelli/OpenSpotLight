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
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.PathElement;
import org.openspotlight.federation.domain.StreamArtifact;

public class LocalSourceStreamArtifactFinder implements ArtifactFinder<StreamArtifact> {

    public StreamArtifact findByPath( final String initialPath ) {
        Assertions.checkNotEmpty("initialPath", initialPath);
        Assertions.checkCondition("correctStarting", initialPath.startsWith("classpath:"));
        final String path = Strings.removeBegginingFrom("classpath:", initialPath);
        for (final ChangeType t : ChangeType.values()) {
            try {

                final String location = MessageFormat.format("./src/test/resources/artifacts/{0}/{1}",
                                                             t.toString().toLowerCase(), path);

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
                final StreamArtifact streamArtifact = StreamArtifact.createNewStreamArtifact("classpath:/" + path, t, content);
                return streamArtifact;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        return null;
    }

    public StreamArtifact findByPath( final String artifactSourceReference,
                                      final String path ) {
        Assertions.checkNotEmpty("artifactSourceReference", artifactSourceReference);
        Assertions.checkNotEmpty("path", path);
        Assertions.checkCondition("correctArtifactSourceRef", "classpath:".equals(artifactSourceReference));
        return this.findByPath(artifactSourceReference + path);
    }

    public StreamArtifact findByRelativePath( final StreamArtifact relativeTo,
                                              final String path ) {
        final String newPath = PathElement.createRelativePath(relativeTo.getParent(), path).getCompletePath();
        return this.findByPath(newPath);
    }

    public Set<StreamArtifact> listByPath( final String rawInitialPath ) {
        Assertions.checkNotEmpty("rawInitialPath", rawInitialPath);
        Assertions.checkCondition("correctStarting", rawInitialPath.startsWith("classpath:"));
        final String initialPath = Strings.removeBegginingFrom("classpath:", rawInitialPath);
        try {
            final Set<StreamArtifact> result = new HashSet<StreamArtifact>();
            for (final ChangeType t : ChangeType.values()) {

                final String path = MessageFormat.format("./src/test/resources/artifacts/{0}/{1}", t.toString().toLowerCase(),
                                                         initialPath);
                final File initialDir = new File(path);
                final String pathToRemove = initialDir.getCanonicalPath().substring(
                                                                                    0,
                                                                                    initialDir.getCanonicalPath().length()
                                                                                    - initialPath.length() - 1);
                final Set<String> pathList = Files.listFileNamesFrom(path);

                for (final String p : pathList) {
                    final String correctRelativePath = Strings.removeBegginingFrom(pathToRemove, p);
                    final StreamArtifact sa = this.findByPath("classpath:", correctRelativePath);
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

    public Set<StreamArtifact> listByPath( final String artifactSourceReference,
                                           final String path ) {
        Assertions.checkNotEmpty("artifactSourceReference", artifactSourceReference);
        Assertions.checkNotEmpty("path", path);
        Assertions.checkCondition("correctArtifactSourceRef", "classpath:".equals(artifactSourceReference));
        return this.listByPath(artifactSourceReference + path);
    }

}
