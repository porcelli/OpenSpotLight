package org.openspotlight.bundle.db.processor;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.processing.BundleProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbTableArtifactBundleProcessor implements
		BundleProcessor<TableArtifact> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public <A extends Artifact> boolean acceptKindOfArtifact(
			final Class<A> kindOfArtifact) {
		return TableArtifact.class.isAssignableFrom(kindOfArtifact);
	}

	public void beforeProcessArtifact(final TableArtifact artifact) {

	}

	public void didFinishProcessing(final ArtifactChanges<TableArtifact> changes) {

	}

	public void didFinishToProcessArtifact(final TableArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<TableArtifact> getArtifactType() {
		return TableArtifact.class;
	}

	public SaveBehavior getSaveBehavior() {
		return SaveBehavior.PER_ARTIFACT;
	}

	public LastProcessStatus processArtifact(final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		logger.info("adding node " + artifact.getTableName() + " into slnode "
				+ currentContext.getCurrentNodeGroup().getName()
				+ " on repository "
				+ currentContext.getCurrentRepository().getName());
		currentContext.getCurrentNodeGroup().addNode(artifact.getTableName());
		return LastProcessStatus.PROCESSED;
	}

	public void selectArtifactsToBeProcessed(
			final CurrentProcessorContext currentContext,
			final ExecutionContext context,
			final ArtifactChanges<TableArtifact> changes,
			final ArtifactsToBeProcessed<TableArtifact> toBeReturned) {

	}

}
