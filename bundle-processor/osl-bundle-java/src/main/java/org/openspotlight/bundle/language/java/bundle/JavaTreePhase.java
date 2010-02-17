package org.openspotlight.bundle.language.java.bundle;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaTreePhase implements
		BundleProcessorArtifactPhase<StringArtifact> {

	public void beforeProcessArtifact(final StringArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) {

	}

	public void didFinishToProcessArtifact(final StringArtifact artifact,
			final LastProcessStatus status,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) {

	}

	public Class<StringArtifact> getArtifactType() {
		return StringArtifact.class;
	}

	public LastProcessStatus processArtifact(final StringArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		// JavaTransientDto dto = (JavaTransientDto) artifact.getTransientMap()
		// .get("DTO-Parser");
		// final CommonTreeNodeStream treeNodes = new CommonTreeNodeStream(
		// dto.tree);
		// final JavaTree walker = new JavaTree(treeNodes);
		// walker.compilationUnit();
		// dto = JavaTransientDto.fromTree(dto).withTreeNodeStream(treeNodes)
		// .withWalker(walker).create();
		// artifact.getTransientMap().put("DTO-Tree", dto);
		return LastProcessStatus.PROCESSED;
	}

}
