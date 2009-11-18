package org.openspotlight.federation.finder;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Repository;

/**
 * The Interface ArtifactFinderProvider.
 */
public interface ArtifactFinderProvider {

    /**
     * Gets the finder for type.
     * 
     * @param typeOfArtifact the type of artifact
     * @param repositoryName the repository name
     * @return the finder for type
     */
    public <A extends Artifact> ArtifactFinder<A> getFinderForType( Repository repositoryName,
                                                                    Class<A> typeOfArtifact );

}
