package org.openspotlight.federation.processing.internal.domain;

import java.util.Collections;
import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.processing.BundleProcessor.ArtifactProcessingResults;

/**
 * The Class ArtifactProcessingResultsImpl.
 */
public class ArtifactProcessingResultsImpl<T extends Artifact> implements ArtifactProcessingResults<T> {

    /** The artifacts with error. */
    private Set<T> artifactsWithError;

    /** The ignored artifacts. */
    private Set<T> ignoredArtifacts;

    /** The processed arifacts. */
    private Set<T> processedArifacts;

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactProcessingResults#getArtifactsWithError()
     */
    public Set<T> getArtifactsWithError() {
        return this.artifactsWithError;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactProcessingResults#getIgnoredArtifacts()
     */
    public Set<T> getIgnoredArtifacts() {
        return this.ignoredArtifacts;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactProcessingResults#getProcessedArifacts()
     */
    public Set<T> getProcessedArifacts() {
        return this.processedArifacts;
    }

    /**
     * Sets the artifacts with error.
     * 
     * @param artifactsWithError the new artifacts with error
     */
    public void setArtifactsWithError( final Set<T> artifactsWithError ) {
        this.artifactsWithError = Collections.unmodifiableSet(artifactsWithError);
    }

    /**
     * Sets the ignored artifacts.
     * 
     * @param ignoredArtifacts the new ignored artifacts
     */
    public void setIgnoredArtifacts( final Set<T> ignoredArtifacts ) {
        this.ignoredArtifacts = Collections.unmodifiableSet(ignoredArtifacts);
    }

    /**
     * Sets the processed arifacts.
     * 
     * @param processedArifacts the new processed arifacts
     */
    public void setProcessedArifacts( final Set<T> processedArifacts ) {
        this.processedArifacts = Collections.unmodifiableSet(processedArifacts);
    }

}
