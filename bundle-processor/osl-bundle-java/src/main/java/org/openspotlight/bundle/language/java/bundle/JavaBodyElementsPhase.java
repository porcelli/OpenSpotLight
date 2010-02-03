package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.openspotlight.bundle.language.java.parser.JavaBodyElements;
import org.openspotlight.bundle.language.java.parser.executor.JavaBodyElementsExecutor;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaBodyElementsPhase implements
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
		final JavaTransientDto dto = (JavaTransientDto) artifact
				.getTransientMap().get("DTO-PublicElementsTree");
		final CommonTreeNodeStream stream = dto.treeNodes;
		final JavaBodyElements elements = new JavaBodyElements(stream);
		elements.setExecutor(new JavaBodyElementsExecutor());
		elements.compilationUnit();
		return LastProcessStatus.PROCESSED;
	}

}
