package org.openspotlight.federation.finder;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.StreamArtifact;

public class LocalSourceArtifactFinderByRepositoryProvider implements ArtifactFinderByRepositoryProvider {
    private final ArtifactSource artifactSource;
    private final boolean        useChangeFolders;

    public LocalSourceArtifactFinderByRepositoryProvider(
                                                          final ArtifactSource artifactSource, final boolean useChangeFolders ) {
        this.artifactSource = artifactSource;
        this.useChangeFolders = useChangeFolders;
    }

    public synchronized <A extends Artifact> ArtifactFinder<A> getByRepository( final Class<A> artifactType,
                                                                                final Repository repository ) {
        if (StreamArtifact.class.equals(artifactType)) {
            if (this.useChangeFolders) {
                return (ArtifactFinder<A>)new LocalSourceStreamArtifactFinder(this.artifactSource);
            } else {
                return (ArtifactFinder<A>)new FileSystemStreamArtifactFinder(this.artifactSource);
            }
        } else {
            throw Exceptions.logAndReturn(new IllegalArgumentException(
                                                                       "Only stream artifacts are allowed on this artifact finder provider"));
        }
    }
}
