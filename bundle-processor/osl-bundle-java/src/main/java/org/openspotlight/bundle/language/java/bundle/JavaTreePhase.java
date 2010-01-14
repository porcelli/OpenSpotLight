package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.openspotlight.bundle.language.java.parser.JavaTree;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaTreePhase implements
		BundleProcessorArtifactPhase<StringArtifact> {

	public void beforeProcessArtifact(final StringArtifact artifact) {

	}

	public void didFinishToProcessArtifact(final StringArtifact artifact,
			final LastProcessStatus status) {

	}

	public Class<StringArtifact> getArtifactType() {
		return StringArtifact.class;
	}

	public LastProcessStatus processArtifact(final StringArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		JavaTransientDto dto = (JavaTransientDto) artifact.getTransientMap()
				.get("DTO");
		final CommonTreeNodeStream treeNodes = new CommonTreeNodeStream(
				dto.tree);
		final JavaTree walker = new JavaTree(treeNodes);
		walker.compilationUnit();
		dto = (JavaTransientDto) artifact.getTransientMap().get("DTO");
		dto = JavaTransientDto.fromTree(dto).withTreeNodeStream(treeNodes)
				.withWalker(walker).create();
		artifact.getTransientMap().put("DTO", dto);
		return LastProcessStatus.PROCESSED;
	}

}
