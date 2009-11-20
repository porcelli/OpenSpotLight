package org.openspotlight.federation.finder;

import org.openspotlight.federation.domain.ArtifactSource;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating JcrArtifactFinderByRepositoryProvider objects.
 */
public class LocalSourceArtifactFinderByRepositoryProviderFactory implements ArtifactFinderByRepositoryProviderFactory {
    private final ArtifactSource artifactSource;
    private final boolean        useChangeFolders;

    public LocalSourceArtifactFinderByRepositoryProviderFactory(
                                                                 final ArtifactSource artifactSource,
                                                                 final boolean useChangeFolders ) {
        this.artifactSource = artifactSource;
        this.useChangeFolders = useChangeFolders;
    }

    public synchronized void closeResources() {
    }

    public ArtifactFinderByRepositoryProvider createNew() {
        return new LocalSourceArtifactFinderByRepositoryProvider(this.artifactSource, this.useChangeFolders);
    }

}
