package org.openspotlight.federation.finder;

import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Collections.setOf;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.DatabaseCustomArtifact;
import org.openspotlight.federation.domain.StreamArtifact;

public enum ArtifactTypeRegistry {

	INSTANCE;

	@SuppressWarnings("unchecked")
	private final Set<Class<? extends Artifact>> artifactTypes = unmodifiableSet(setOf(
			StreamArtifact.class, DatabaseCustomArtifact.class));

	public Set<Class<? extends Artifact>> getRegisteredArtifactTypes() {
		return artifactTypes;
	}

}
