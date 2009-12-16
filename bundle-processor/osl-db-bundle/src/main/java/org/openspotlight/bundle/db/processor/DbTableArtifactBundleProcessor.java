package org.openspotlight.bundle.db.processor;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.processing.BundleProcessor;

public class DbTableArtifactBundleProcessor implements
		BundleProcessor<TableArtifact> {

	public <A extends Artifact> boolean acceptKindOfArtifact(
			final Class<A> kindOfArtifact) {
		// TODO Auto-generated method stub
		return false;
	}

	public void beforeProcessArtifact(final TableArtifact artifact) {
		// TODO Auto-generated method stub

	}

	public void didFinishProcessing(
			final org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges<TableArtifact> changes) {
		// TODO Auto-generated method stub

	}

	public void didFinishToProcessArtifact(final TableArtifact artifact,
			final LastProcessStatus status) {
		// TODO Auto-generated method stub

	}

	public Class<TableArtifact> getArtifactType() {
		// TODO Auto-generated method stub
		return null;
	}

	public org.openspotlight.federation.processing.BundleProcessor.SaveBehavior getSaveBehavior() {
		// TODO Auto-generated method stub
		return null;
	}

	public LastProcessStatus processArtifact(
			final TableArtifact artifact,
			final org.openspotlight.federation.processing.BundleProcessor.CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void selectArtifactsToBeProcessed(
			final org.openspotlight.federation.processing.BundleProcessor.CurrentProcessorContext currentContext,
			final ExecutionContext context,
			final org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges<TableArtifact> changes,
			final org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed<TableArtifact> toBeReturned) {
		// TODO Auto-generated method stub

	}

}
