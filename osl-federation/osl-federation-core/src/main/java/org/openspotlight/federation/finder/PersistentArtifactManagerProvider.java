package org.openspotlight.federation.finder;

import org.openspotlight.common.concurrent.MultipleProvider;

public class PersistentArtifactManagerProvider extends
		MultipleProvider<PersistentArtifactManager> {

	public PersistentArtifactManagerProvider(
			ItemFactory<PersistentArtifactManager> factory) {
		super(factory);
	}

}
