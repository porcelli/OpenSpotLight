package org.openspotlight.federation.finder;

import org.openspotlight.federation.domain.Artifact;

// TODO: Auto-generated Javadoc
/**
 * The Interface ArtifactFinderWithSaveCapabilitie.
 */
public interface ArtifactFinderWithSaveCapabilitie<A extends Artifact> extends ArtifactFinder<A> {

    /**
     * Save.
     * 
     * @param artifactToSave the artifact to save
     */
    public void save( A artifactToSave );
}
