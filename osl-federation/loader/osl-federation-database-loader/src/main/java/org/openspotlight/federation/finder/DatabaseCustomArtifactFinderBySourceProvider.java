package org.openspotlight.federation.finder;

import java.util.Collections;
import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.DatabaseCustomArtifact;
import org.openspotlight.federation.domain.DbArtifactSource;

public class DatabaseCustomArtifactFinderBySourceProvider implements
		ArtifactFinderBySourceProvider {

	@SuppressWarnings("unchecked")
	public <S extends ArtifactSource> Set<ArtifactFinder<? extends Artifact>> getForType(
			final Class<? extends Artifact> artifactType, final S source) {
		if (DatabaseCustomArtifact.class.isAssignableFrom(artifactType)
				&& source instanceof DbArtifactSource) {
			return org.openspotlight.common.util.Collections
					.<ArtifactFinder<? extends Artifact>> setOf(new DatabaseCustomArtifactFinder(
							(DbArtifactSource) source));

		}
		return Collections.emptySet();
	}

}
