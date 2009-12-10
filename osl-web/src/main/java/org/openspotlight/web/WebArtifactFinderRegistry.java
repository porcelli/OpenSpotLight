package org.openspotlight.web;

import java.util.Set;

import org.openspotlight.common.util.Collections;
import org.openspotlight.federation.domain.ArtifactFinderRegistry;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.DatabaseStreamArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.FileSystemArtifactBySourceProvider;

public class WebArtifactFinderRegistry implements ArtifactFinderRegistry {

	public Set<ArtifactFinderBySourceProvider> getRegisteredArtifactFinderProviders() {
		return Collections.<ArtifactFinderBySourceProvider> setOf(
				new DatabaseCustomArtifactFinderBySourceProvider(),
				new DatabaseStreamArtifactFinderBySourceProvider(),
				new FileSystemArtifactBySourceProvider());
	}

}
