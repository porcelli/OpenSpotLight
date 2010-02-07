package org.openspotlight.bundle.language.java.bundle;

import java.util.Set;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.parser.JavaBodyElements;
import org.openspotlight.bundle.language.java.parser.executor.JavaBodyElementsExecutor;
import org.openspotlight.bundle.language.java.parser.executor.JavaExecutorSupport;
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
		final JavaExecutorSupport support = dto.support;
		final CommonTreeNodeStream stream = dto.treeNodes;
		@SuppressWarnings("unchecked")
		final Set<String> contexts = (Set<String>) currentContext
				.getTransientProperties().get(JavaConstants.USING_CONTEXTS);
		stream.reset();
		final JavaBodyElements elements = new JavaBodyElements(stream);
		elements.setExecutor(new JavaBodyElementsExecutor(support, contexts));
		elements.compilationUnit();
		return LastProcessStatus.PROCESSED;
	}

}
