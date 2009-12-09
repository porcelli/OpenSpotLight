package org.openspotlight.federation.data.load.db.test;

import java.util.Set;

import org.openspotlight.common.util.Collections;
import org.openspotlight.federation.domain.ArtifactFinderRegistry;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinderBySourceProvider;

public class SampleDatabaseCustomArtifactRegistry implements
		ArtifactFinderRegistry {

	public Set<ArtifactFinderBySourceProvider> getRegisteredArtifactFinderProviders() {
		return Collections
				.<ArtifactFinderBySourceProvider> setOf(new DatabaseCustomArtifactFinderBySourceProvider());
	}

}
