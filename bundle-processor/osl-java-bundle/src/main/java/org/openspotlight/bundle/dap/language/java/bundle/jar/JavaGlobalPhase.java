package org.openspotlight.bundle.dap.language.java.bundle.jar;

import java.util.Set;

import org.openspotlight.common.util.Collections;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.ArtifactChanges;
import org.openspotlight.federation.processing.ArtifactsToBeProcessed;
import org.openspotlight.federation.processing.BundleProcessorGlobalPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.openspotlight.federation.processing.SaveBehavior;

public class JavaGlobalPhase implements BundleProcessorGlobalPhase<Artifact> {

	public void didFinishProcessing(final ArtifactChanges<Artifact> changes) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public Set<Class<? extends Artifact>> getArtifactTypes() {
		return Collections.<Class<? extends Artifact>> setOf(
				StreamArtifact.class, StringArtifact.class);
	}

	public SaveBehavior getSaveBehavior() {
		return SaveBehavior.PER_PROCESSING;
	}

	public void selectArtifactsToBeProcessed(
			final CurrentProcessorContext currentContext,
			final ExecutionContext context,
			final ArtifactChanges<Artifact> changes,
			final ArtifactsToBeProcessed<Artifact> toBeReturned)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
