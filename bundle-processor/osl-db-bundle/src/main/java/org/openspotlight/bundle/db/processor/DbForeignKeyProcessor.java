package org.openspotlight.bundle.db.processor;

import org.openspotlight.bundle.db.metamodel.link.ConstraintDatabaseColumn;
import org.openspotlight.bundle.db.metamodel.node.DatabaseConstraintPrimaryKey;
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

		if (c.getPks() != null) {
			for (final String pkName : c.getPks()) {
				final DatabaseConstraintPrimaryKey pk = column.addNode(
						wrappedType.getDatabaseConstraintPrimaryKeyType(),
						pkName);
				context.getGraphSession().addLink(
						ConstraintDatabaseColumn.class, column, pk, false);
			}
		}

		return LastProcessStatus.PROCESSED;
	}

}
