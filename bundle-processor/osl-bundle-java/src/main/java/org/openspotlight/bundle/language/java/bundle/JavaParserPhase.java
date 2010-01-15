package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.language.java.parser.JavaParser;
import org.openspotlight.bundle.language.java.parser.executor.JavaParserExecutor;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaParserPhase implements
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
				.get("DTO-Lexer");
		final JavaParser parser = new JavaParser(dto.commonTokenStream);
		final JavaParserExecutor parserExecutor = new JavaParserExecutor(
				context, artifact.getContent(), artifact
						.getArtifactCompleteName(), artifact.getVersion(),
				artifact);
		parser.setParserExecutor(parserExecutor);
		final Tree tree = (Tree) parser.compilationUnit().getTree();
		dto = JavaTransientDto.fromParser(dto).withParser(parser)
				.withParserExecutor(parserExecutor).withTree(tree).create();
		artifact.getTransientMap().put("DTO-Parser", dto);
		return LastProcessStatus.PROCESSED;
	}

}
