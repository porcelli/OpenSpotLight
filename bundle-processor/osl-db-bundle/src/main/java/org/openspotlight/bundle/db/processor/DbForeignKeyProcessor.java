package org.openspotlight.bundle.db.processor;

import static org.openspotlight.bundle.db.processor.DbProcessorHelper.createForeignKey;

import org.openspotlight.bundle.db.processor.wrapped.WrappedTypeFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.db.ForeignKeyConstraintArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class DbForeignKeyProcessor implements
		BundleProcessorArtifactPhase<ForeignKeyConstraintArtifact> {

	public void beforeProcessArtifact(
			final ForeignKeyConstraintArtifact artifact) {

	}

	public void didFinishToProcessArtifact(
			final ForeignKeyConstraintArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<ForeignKeyConstraintArtifact> getArtifactType() {
		return ForeignKeyConstraintArtifact.class;
	}

	public LastProcessStatus processArtifact(
			final ForeignKeyConstraintArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		final DbWrappedType wrappedType = WrappedTypeFactory.INSTANCE
				.createByType(artifact.getDatabaseType());
		createForeignKey(wrappedType, context, currentContext, artifact);
		return LastProcessStatus.PROCESSED;
	}

}
