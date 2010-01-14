package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLArtifactStream;
import org.openspotlight.bundle.language.java.parser.JavaLexer;
import org.openspotlight.bundle.language.java.parser.JavaParser;
import org.openspotlight.bundle.language.java.parser.JavaTree;
import org.openspotlight.bundle.language.java.parser.executor.JavaLexerExecutor;
import org.openspotlight.bundle.language.java.parser.executor.JavaParserExecutor;
import org.openspotlight.common.util.Assertions;

/**
 * This is just a dto. This bunch of builders are used to ensure that the dto
 * will have only final fields, since it will be shared between threads.
 * 
 * @author feu
 * 
 */
public class JavaTransientDto {

	private abstract static class BuilderParameters {

		protected SLArtifactStream stream;

		protected JavaLexer lexer;

		protected SourceLineInfoAggregator sourceLine;
		protected JavaLexerExecutor lexerExecutor;
		protected CommonTokenStream commonTokenStream;
		protected JavaParserExecutor parserExecutor;
		protected JavaParser parser;
		protected Tree tree;
		protected CommonTreeNodeStream treeNodes;
		protected JavaTree walker;

		BuilderParameters() {

		}

		BuilderParameters(final JavaTransientDto dto) {
			stream = dto.stream;
			lexer = dto.lexer;
			sourceLine = dto.sourceLine;
			lexerExecutor = dto.lexerExecutor;
			commonTokenStream = dto.commonTokenStream;
			parserExecutor = dto.parserExecutor;
			parser = dto.parser;
			tree = dto.tree;
			treeNodes = dto.treeNodes;
			walker = dto.walker;
		}

		public final JavaTransientDto create() {
			validate();
			return new JavaTransientDto(stream, lexer, sourceLine,
					lexerExecutor, commonTokenStream, parser, parserExecutor,
					tree, treeNodes, walker);
		}

		protected abstract void validate();

	}

	public static class LexerDtoBuilder extends BuilderParameters {

		private LexerDtoBuilder() {
		}

		@Override
		protected void validate() {
			Assertions.checkNotNull("stream", stream);
			Assertions.checkNotNull("lexer", lexer);
			Assertions.checkNotNull("sourceLine", sourceLine);
			Assertions.checkNotNull("lexerExecutor", lexerExecutor);
			Assertions.checkNotNull("commonTokenStream", commonTokenStream);
		}

		public LexerDtoBuilder withCommonTokenStream(
				final CommonTokenStream commonTokenStream) {
			this.commonTokenStream = commonTokenStream;
			return this;
		}

		public LexerDtoBuilder withLexer(final JavaLexer lexer) {
			this.lexer = lexer;
			return this;
		}

		public LexerDtoBuilder withLexerExecutor(
				final JavaLexerExecutor lexerExecutor) {
			this.lexerExecutor = lexerExecutor;
			return this;
		}

		public LexerDtoBuilder withSourceline(
				final SourceLineInfoAggregator sourceLine) {
			this.sourceLine = sourceLine;
			return this;
		}

		public LexerDtoBuilder withStream(final SLArtifactStream stream) {
			this.stream = stream;
			return this;
		}
	}

	public static class ParserDtoBuilder extends BuilderParameters {

		private ParserDtoBuilder(final JavaTransientDto dto) {
			super(dto);
		}

		@Override
		protected void validate() {
			Assertions.checkNotNull("tree", tree);
			Assertions.checkNotNull("parserExecutor", parserExecutor);
			Assertions.checkNotNull("parser", parser);
			Assertions.checkNotNull("stream", stream);
			Assertions.checkNotNull("lexer", lexer);
			Assertions.checkNotNull("sourceLine", sourceLine);
			Assertions.checkNotNull("lexerExecutor", lexerExecutor);
			Assertions.checkNotNull("commonTokenStream", commonTokenStream);

		}

		public ParserDtoBuilder withParser(final JavaParser parser) {
			this.parser = parser;
			return this;
		}

		public ParserDtoBuilder withParserExecutor(
				final JavaParserExecutor parserExecutor) {
			this.parserExecutor = parserExecutor;
			return this;
		}

		public ParserDtoBuilder withTree(final Tree tree) {
			this.tree = tree;
			return this;
		}

	}

	public static class TreeDtoBuilder extends BuilderParameters {

		TreeDtoBuilder(final JavaTransientDto dto) {
			super(dto);
		}

		@Override
		protected void validate() {
			Assertions.checkNotNull("treeNodes", treeNodes);
			Assertions.checkNotNull("walker", walker);
			Assertions.checkNotNull("tree", tree);
			Assertions.checkNotNull("parserExecutor", parserExecutor);
			Assertions.checkNotNull("parser", parser);
			Assertions.checkNotNull("stream", stream);
			Assertions.checkNotNull("lexer", lexer);
			Assertions.checkNotNull("sourceLine", sourceLine);
			Assertions.checkNotNull("lexerExecutor", lexerExecutor);
			Assertions.checkNotNull("commonTokenStream", commonTokenStream);

		}

		public TreeDtoBuilder withTreeNodeStream(
				final CommonTreeNodeStream treeNodes) {
			this.treeNodes = treeNodes;
			return this;
		}

		public TreeDtoBuilder withWalker(final JavaTree walker) {
			this.walker = walker;
			return this;
		}

	}

	public static LexerDtoBuilder fromLexer() {
		return new LexerDtoBuilder();
	}

	public static ParserDtoBuilder fromParser(final JavaTransientDto dto) {
		return new ParserDtoBuilder(dto);
	}

	public static TreeDtoBuilder fromTree(final JavaTransientDto dto) {
		return new TreeDtoBuilder(dto);
	}

	public final SLArtifactStream stream;
	public final JavaLexer lexer;
	public final SourceLineInfoAggregator sourceLine;
	public final JavaLexerExecutor lexerExecutor;
	public final CommonTokenStream commonTokenStream;
	public final JavaParser parser;
	public final JavaParserExecutor parserExecutor;
	public final Tree tree;
	public final CommonTreeNodeStream treeNodes;
	public final JavaTree walker;

	private JavaTransientDto(final SLArtifactStream stream,
			final JavaLexer lexer, final SourceLineInfoAggregator sourceLine,
			final JavaLexerExecutor lexerExecutor,
			final CommonTokenStream commonTokenStream, final JavaParser parser,
			final JavaParserExecutor parserExecutor, final Tree tree,
			final CommonTreeNodeStream treeNodes, final JavaTree walker) {
		super();
		this.stream = stream;
		this.lexer = lexer;
		this.sourceLine = sourceLine;
		this.lexerExecutor = lexerExecutor;
		this.commonTokenStream = commonTokenStream;
		this.parser = parser;
		this.parserExecutor = parserExecutor;
		this.tree = tree;
		this.treeNodes = treeNodes;
		this.walker = walker;
	}

}
