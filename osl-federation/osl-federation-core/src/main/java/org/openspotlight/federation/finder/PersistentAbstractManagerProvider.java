package org.openspotlight.federation.finder;

import org.openspotlight.common.concurrent.MultipleProvider;

public class PersistentAbstractManagerProvider extends
		MultipleProvider<PersistentArtifactManager> {

	public PersistentAbstractManagerProvider(
			ItemFactory<PersistentArtifactManager> factory) {
		super(factory);
	}

}
