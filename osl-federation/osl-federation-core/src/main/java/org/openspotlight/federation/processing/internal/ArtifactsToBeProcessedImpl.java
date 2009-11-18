package org.openspotlight.federation.processing.internal;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed;

/**
 * The Class ArtifactsToBeProcessedImpl.
 */
public class ArtifactsToBeProcessedImpl<T extends Artifact> implements ArtifactsToBeProcessed<T> {

    /** The artifacts already processed. */
    private Set<T> artifactsAlreadyProcessed;

    /** The artifacts to be processed. */
    private Set<T> artifactsToBeProcessed;

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed#getArtifactsAlreadyProcessed()
     */
    public Set<T> getArtifactsAlreadyProcessed() {
        return this.artifactsAlreadyProcessed;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed#getArtifactsToBeProcessed()
     */
    public Set<T> getArtifactsToBeProcessed() {
        return this.artifactsToBeProcessed;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed#setArtifactsAlreadyProcessed(java.util.Set)
     */
    public void setArtifactsAlreadyProcessed( final Set<T> artifactsAlreadyProcessed ) {
        this.artifactsAlreadyProcessed = artifactsAlreadyProcessed;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed#setArtifactsToBeProcessed(java.util.Set)
     */
    public void setArtifactsToBeProcessed( final Set<T> artifactsToBeProcessed ) {
        this.artifactsToBeProcessed = artifactsToBeProcessed;
    }

}
