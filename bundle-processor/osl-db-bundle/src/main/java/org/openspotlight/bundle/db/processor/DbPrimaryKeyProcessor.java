package org.openspotlight.bundle.db.processor;

import static org.openspotlight.bundle.db.processor.DbProcessorHelper.createPrimaryKey;

import org.openspotlight.bundle.db.processor.wrapped.WrappedTypeFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.db.PrimaryKeyConstraintArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class DbPrimaryKeyProcessor implements
		BundleProcessorArtifactPhase<PrimaryKeyConstraintArtifact> {

	public void beforeProcessArtifact(
			final PrimaryKeyConstraintArtifact artifact) {

	}

	public void didFinishToProcessArtifact(
			final PrimaryKeyConstraintArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<PrimaryKeyConstraintArtifact> getArtifactType() {
		return PrimaryKeyConstraintArtifact.class;
	}

	public LastProcessStatus processArtifact(
			final PrimaryKeyConstraintArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		final DbWrappedType wrappedType = WrappedTypeFactory.INSTANCE
				.createByType(artifact.getDatabaseType());
		createPrimaryKey(wrappedType, context, currentContext, artifact);
		return LastProcessStatus.PROCESSED;
	}

}
