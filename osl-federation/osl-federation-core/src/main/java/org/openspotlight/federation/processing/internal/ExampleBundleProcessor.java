package org.openspotlight.federation.processing.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.domain.SyntaxInformationType;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.graph.SLNode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleBundleProcessor implements BundleProcessor<StreamArtifact> {

	public static List<LastProcessStatus> allStatus = new CopyOnWriteArrayList<LastProcessStatus>();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public <A extends Artifact> boolean acceptKindOfArtifact(
			final Class<A> kindOfArtifact) {
		return StreamArtifact.class.equals(kindOfArtifact);
	}

	public void beforeProcessArtifact(final StreamArtifact artifact) {
		logger.info("starting to process " + artifact);
	}

	public void didFinishProcessing(
			final ArtifactChanges<StreamArtifact> changes) {

	}

	public void didFinishToProcessArtifact(final StreamArtifact artifact,
			final LastProcessStatus status) {
		ExampleBundleProcessor.allStatus.add(status);

		logger.info("processed " + artifact);
	}

	public Class<StreamArtifact> getArtifactType() {
		return StreamArtifact.class;
	}

	public SaveBehavior getSaveBehavior() {
		return SaveBehavior.PER_PROCESSING;
	}

	public LastProcessStatus processArtifact(final StreamArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		context.getLogger().log(context.getUser(), LogEventType.DEBUG,
				"another test", artifact);
		for (int i = 0; i < 100; i++) {
			final SLNode node = currentContext.getCurrentNodeGroup().addNode(
					artifact.getArtifactCompleteName() + "i");
			node.addNode(artifact.getArtifactName() + "i");
		}
		artifact
				.addSyntaxInformation(2, 4, 5, 6, SyntaxInformationType.COMMENT);

		return LastProcessStatus.PROCESSED;
	}

	public void selectArtifactsToBeProcessed(
			final CurrentProcessorContext currentContext,
			final ExecutionContext context,
			final ArtifactChanges<StreamArtifact> changes,
			final ArtifactsToBeProcessed<StreamArtifact> toBeReturned) {

	}

}
