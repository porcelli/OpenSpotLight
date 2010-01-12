package org.openspotlight.bundle.common.metrics;

public class SimpleSourceLineInfo {
	private int physicalLines;
	private int efectiveLines;
	private int logicalLines;
	private int logicalLinesOfCode;
	private int logicalLinesOfWhitespace;
	private int commentedLines;
	private int fullCommentLines;
	private int meaningfulCommentLines;

	public void add(final SimpleSourceLineInfo otherSource) {
		physicalLines += otherSource.getPhysicalLines();
		efectiveLines += otherSource.getEfectiveLines();
		logicalLines += otherSource.getLogicalLines();
		logicalLinesOfCode += otherSource.getLogicalLinesOfCode();
		logicalLinesOfWhitespace += otherSource.getLogicalLinesOfWhitespace();
		commentedLines += otherSource.getCommentedLines();
		fullCommentLines += otherSource.getFullCommentLines();
		meaningfulCommentLines += otherSource.getMeaningfulCommentLines();
	}

	public Integer getCommentedLines() {
		return commentedLines;
	}

	public Integer getEfectiveLines() {
		return efectiveLines;
	}

	public Integer getFullCommentLines() {
		return fullCommentLines;
	}

	public Integer getLogicalLines() {
		return logicalLines;
	}

	public Integer getLogicalLinesOfCode() {
		return logicalLinesOfCode;
	}

	public Integer getLogicalLinesOfWhitespace() {
		return logicalLinesOfWhitespace;
	}

	public Integer getMeaningfulCommentLines() {
		return meaningfulCommentLines;
	}

	public Integer getPhysicalLines() {
		return physicalLines;
	}

	public void setCommentedLines(final int commentedLines) {
		this.commentedLines = commentedLines;
	}

	public void setEfectiveLines(final int efectiveLines) {
		this.efectiveLines = efectiveLines;
	}

	public void setFullCommentLines(final int fullCommentLines) {
		this.fullCommentLines = fullCommentLines;
	}

	public void setLogicalLines(final int logicalLines) {
		this.logicalLines = logicalLines;
	}

	public void setLogicalLinesOfCode(final int logicalLinesOfCode) {
		this.logicalLinesOfCode = logicalLinesOfCode;
	}

	public void setLogicalLinesOfWhitespace(final int logicalLinesOfWhitespace) {
		this.logicalLinesOfWhitespace = logicalLinesOfWhitespace;
	}

	public void setMeaningfulCommentLines(final int meaningfulCommentLines) {
		this.meaningfulCommentLines = meaningfulCommentLines;
	}

	public void setPhysicalLines(final int physicalLines) {
		this.physicalLines = physicalLines;
	}
}