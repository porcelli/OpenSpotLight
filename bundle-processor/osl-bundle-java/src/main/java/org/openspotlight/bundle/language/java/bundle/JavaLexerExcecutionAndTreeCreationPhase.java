package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.CommonTokenStream;
import org.openspotlight.bundle.common.parser.SLArtifactStream;
import org.openspotlight.bundle.common.parser.SLArtifactStreamBasicImpl;
import org.openspotlight.bundle.language.java.parser.JavaLexer;
import org.openspotlight.bundle.language.java.parser.executor.JavaLexerExecutor;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaLexerExcecutionAndTreeCreationPhase implements
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
		final SLArtifactStream stream = new SLArtifactStreamBasicImpl(artifact
				.getArtifactCompleteName(), artifact.getContent());
		final JavaLexer lexer = new JavaLexer(stream);
		final JavaLexerExecutor lexerExecutor = new JavaLexerExecutor(artifact);
		lexer.setLexerExecutor(lexerExecutor);
		final CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		commonTokenStream.getTokens();
		// final JavaParser parser = new JavaParser(commonTokenStream);
		// final JavaParserExecutor parserExecutor = new JavaParserExecutor(
		// context, artifact.getContent(), artifact
		// .getArtifactCompleteName(), artifact.getVersion(),
		// artifact);
		// parser.setParserExecutor(parserExecutor);
		// final Tree tree = (Tree) parser.compilationUnit().getTree();
		// artifact.getTransientMap().put("tree", tree);

		// //not needed now
		// final CommonTreeNodeStream treeNodes = new
		// CommonTreeNodeStream(tree);
		// final JavaTree walker = new JavaTree(treeNodes);
		// walker.compilationUnit();

		return LastProcessStatus.PROCESSED;
	}

}
