package org.openspotlight.federation.finder;

import org.openspotlight.common.Disposable;

/**
 * A factory for creating ArtifactFinderByRepositoryProvider objects. This will be used on BundleProcessing.
 */
public interface ArtifactFinderByRepositoryProviderFactory extends Disposable {

    /**
     * Creates a new ArtifactFinderByRepositoryProvider object.
     * 
     * @return the artifact finder by repository provider
     */
    public ArtifactFinderByRepositoryProvider createNew();
}
