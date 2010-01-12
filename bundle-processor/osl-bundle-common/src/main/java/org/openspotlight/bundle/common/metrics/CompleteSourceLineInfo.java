package org.openspotlight.bundle.common.metrics;

public class CompleteSourceLineInfo extends SimpleSourceLineInfo {
	// meaningfulCommentLines / logicalLinesOfCode
	private double commentDensity;
	// logicalLinesOfCode / logicalLines
	private double codePercentage;
	// logicalLinesOfWhitespace / logicalLines
	private double whitespacePercentage;

	public void calculate() {
		final double commentDensity = getMeaningfulCommentLines().doubleValue()
				/ getLogicalLinesOfCode().doubleValue() * 100;
		setCommentDensity(commentDensity);
		final double codePercentage = getLogicalLinesOfCode().doubleValue()
				/ getLogicalLines().doubleValue() * 100;
		setCodePercentage(codePercentage);
		final double whitespacePercentage = getLogicalLinesOfWhitespace()
				.doubleValue()
				/ getLogicalLines().doubleValue() * 100;
		setWhitespacePercentage(whitespacePercentage);
	}

	public Double getCodePercentage() {
		return codePercentage;
	}

	public Double getCommentDensity() {
		return commentDensity;
	}

	public Double getWhitespacePercentage() {
		return whitespacePercentage;
	}

	public void setCodePercentage(final double codePercentage) {
		this.codePercentage = codePercentage;
	}

	public void setCommentDensity(final double commentDensity) {
		this.commentDensity = commentDensity;
	}

	public void setWhitespacePercentage(final double whitespacePercentage) {
		this.whitespacePercentage = whitespacePercentage;
	}
}