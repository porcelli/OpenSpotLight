package org.openspotlight.federation.processing;

import org.openspotlight.federation.domain.artifact.Artifact;

public interface BundleProcessorSinglePhase<T extends Artifact> extends
		BundleProcessorGlobalPhase<T>, BundleProcessorArtifactPhase<T> {

}
