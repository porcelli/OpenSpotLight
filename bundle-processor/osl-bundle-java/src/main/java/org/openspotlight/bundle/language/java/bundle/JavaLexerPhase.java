package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.CommonTokenStream;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLArtifactStream;
import org.openspotlight.bundle.common.parser.SLArtifactStreamBasicImpl;
import org.openspotlight.bundle.language.java.parser.JavaLexer;
import org.openspotlight.bundle.language.java.parser.executor.JavaLexerExecutor;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;

public class JavaLexerPhase implements
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
		final SourceLineInfoAggregator sourceLine = new SourceLineInfoAggregator();
		final JavaLexerExecutor lexerExecutor = new JavaLexerExecutor(artifact,
				sourceLine);
		lexer.setLexerExecutor(lexerExecutor);
		final CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		commonTokenStream.getTokens();
		final JavaTransientDto dto = JavaTransientDto.fromLexer().withStream(
				stream).withLexer(lexer).withSourceline(sourceLine)
				.withLexerExecutor(lexerExecutor).withCommonTokenStream(
						commonTokenStream).create();
		artifact.getTransientMap().put("DTO-Lexer", dto);

		return LastProcessStatus.PROCESSED;
	}

}
