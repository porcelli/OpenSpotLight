package org.openspotlight.bundle.language.java.bundle.jar;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaBinaryProcessor implements
		BundleProcessorArtifactPhase<StreamArtifact> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void beforeProcessArtifact(final StreamArtifact artifact) {

	}

	public void didFinishToProcessArtifact(final StreamArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<StreamArtifact> getArtifactType() {
		return StreamArtifact.class;
	}

	public LastProcessStatus processArtifact(final StreamArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		logger.info("processing " + artifact.getArtifactCompleteName());
		return LastProcessStatus.PROCESSED;
	}

}
