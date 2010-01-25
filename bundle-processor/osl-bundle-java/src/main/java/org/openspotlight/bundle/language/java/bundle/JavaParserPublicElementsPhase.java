package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.openspotlight.bundle.language.java.parser.JavaPublicElementsTree;
import org.openspotlight.bundle.language.java.parser.executor.JavaPublicElemetsTreeExecutor;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaParserPublicElementsPhase implements
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
				.get("DTO-Parser");
		final CommonTreeNodeStream treeNodes = new CommonTreeNodeStream(
				dto.tree);
		final JavaPublicElementsTree walker = new JavaPublicElementsTree(
				treeNodes);
		treeNodes.setTokenStream(dto.commonTokenStream);
		walker.setExecutor(new JavaPublicElemetsTreeExecutor(currentContext
				.getCurrentNodeGroup(), context.getGraphSession(), artifact
				.getArtifactCompleteName()));
		walker.compilationUnit();
		dto = JavaTransientDto.fromTree(dto).withTreeNodeStream(treeNodes)
				.withWalker(walker).create();
		artifact.getTransientMap().put("DTO-PublicElementsTree", dto);

		return LastProcessStatus.PROCESSED;
	}
}
