package org.openspotlight.federation.processing.internal;

import java.util.Collections;
import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges;

/**
 * The Class ArtifactChangesImpl.
 */
public class ArtifactChangesImpl<T extends Artifact> implements ArtifactChanges<T> {

    /** The changed artifacts. */
    private Set<T> changedArtifacts;

    /** The excluded artifacts. */
    private Set<T> excludedArtifacts;

    /** The included artifacts. */
    private Set<T> includedArtifacts;

    /** The not changed artifacts. */
    private Set<T> notChangedArtifacts;

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges#getChangedArtifacts()
     */
    public Set<T> getChangedArtifacts() {
        return this.changedArtifacts;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges#getExcludedArtifacts()
     */
    public Set<T> getExcludedArtifacts() {
        return this.excludedArtifacts;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges#getIncludedArtifacts()
     */
    public Set<T> getIncludedArtifacts() {
        return this.includedArtifacts;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges#getNotChangedArtifacts()
     */
    public Set<T> getNotChangedArtifacts() {
        return this.notChangedArtifacts;
    }

    /**
     * Sets the changed artifacts.
     * 
     * @param changedArtifacts the new changed artifacts
     */
    public void setChangedArtifacts( final Set<T> changedArtifacts ) {
        this.changedArtifacts = Collections.unmodifiableSet(changedArtifacts);
    }

    /**
     * Sets the excluded artifacts.
     * 
     * @param excludedArtifacts the new excluded artifacts
     */
    public void setExcludedArtifacts( final Set<T> excludedArtifacts ) {
        this.excludedArtifacts = Collections.unmodifiableSet(excludedArtifacts);
    }

    /**
     * Sets the included artifacts.
     * 
     * @param includedArtifacts the new included artifacts
     */
    public void setIncludedArtifacts( final Set<T> includedArtifacts ) {
        this.includedArtifacts = Collections.unmodifiableSet(includedArtifacts);
    }

    /**
     * Sets the not changed artifacts.
     * 
     * @param notChangedArtifacts the new not changed artifacts
     */
    public void setNotChangedArtifacts( final Set<T> notChangedArtifacts ) {
        this.notChangedArtifacts = Collections.unmodifiableSet(notChangedArtifacts);
    }
}
