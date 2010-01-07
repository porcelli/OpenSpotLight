package org.openspotlight.bundle.db.processor;

import static org.openspotlight.bundle.db.processor.DbProcessorHelper.createColumns;
import static org.openspotlight.bundle.db.processor.DbProcessorHelper.createTableData;

import org.openspotlight.bundle.db.processor.DbProcessorHelper.TableVo;
import org.openspotlight.bundle.db.processor.wrapped.WrappedTypeFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.db.TableArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class DbTableArtifactProcessor implements
		BundleProcessorArtifactPhase<TableArtifact> {

	public void beforeProcessArtifact(final TableArtifact artifact) {

	}

	public void didFinishToProcessArtifact(final TableArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<TableArtifact> getArtifactType() {
		return TableArtifact.class;
	}

	public LastProcessStatus processArtifact(final TableArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		final DbWrappedType wrappedType = WrappedTypeFactory.INSTANCE
				.createByType(artifact.getDatabaseType());
		final TableVo data = createTableData(wrappedType, artifact,
				currentContext, context);
		createColumns(wrappedType, artifact, context, data.databaseContextNode,
				data.database, data.table, data.abstractTable);
		return LastProcessStatus.PROCESSED;
	}

}
