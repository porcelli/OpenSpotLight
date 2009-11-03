package org.openspotlight.federation.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Set;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.impl.StreamArtifact.ChangeType;

public class LocalSourceStreamArtifactFinder implements StreamArtifactFinder {

    public StreamArtifact findByPath( final String path ) {
        Assertions.checkNotEmpty("path", path);
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
        return this.findByPath(path);
    }

    public StreamArtifact findByRelativePath( final StreamArtifact relativeTo,
                                              final String path ) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<StreamArtifact> listByPath( final String path ) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<StreamArtifact> listByPath( final String artifactSourceReference,
                                           final String path ) {
        // TODO Auto-generated method stub
        return null;
    }

}
