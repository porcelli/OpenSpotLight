package org.openspotlight.federation.domain;

import java.util.Set;

import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;

public interface ArtifactFinderRegistry {

	public Set<ArtifactFinderBySourceProvider> getRegisteredArtifactFinderProviders();

}
