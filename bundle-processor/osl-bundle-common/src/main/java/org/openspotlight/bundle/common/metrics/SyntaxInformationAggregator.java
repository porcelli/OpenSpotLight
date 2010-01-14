package org.openspotlight.bundle.common.metrics;

import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.SyntaxInformationType;

public class SyntaxInformationAggregator {
	private final ArtifactWithSyntaxInformation artifact;

	public SyntaxInformationAggregator(
			final ArtifactWithSyntaxInformation artifact) {
		this.artifact = artifact;
	}

	public void addHidden(final int tokenLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenLine, tokenLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.HIDDEN);
	}

	public void addIdentifier(final int tokenLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenLine, tokenLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.IDENTIFIER);
	}

	public void addMultiLineComment(final int tokenStartLine,
			final int tokenStartCharPositionInLine, final int tokenEndLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenStartLine, tokenEndLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.COMMENT);
	}

	public void addNumberLiteral(final int tokenLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenLine, tokenLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.NUMBER_LITERAL);
	}

	public void addReserved(final int tokenStartLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenStartLine, tokenStartLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.RESERVED);
	}

	public void addSingleLineComment(final int tokenLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenLine, tokenLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.COMMENT);
	}

	public void addStringLiteral(final int tokenLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenLine, tokenLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.STRING_LITERAL);
	}

	public void addSymbol(final int tokenLine,
			final int tokenStartCharPositionInLine,
			final int tokenEndCharPositionInLine) {
		artifact.addSyntaxInformation(tokenLine, tokenLine,
				tokenStartCharPositionInLine, tokenEndCharPositionInLine,
				SyntaxInformationType.SYMBOL);
	}

}
