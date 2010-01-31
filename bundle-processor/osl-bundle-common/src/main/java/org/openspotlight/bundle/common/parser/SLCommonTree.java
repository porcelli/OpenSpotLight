package org.openspotlight.bundle.common.parser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

public class SLCommonTree extends CommonTree {

	private int startCharOffset = -1;
	private int lineStart = -1;
	private int lineEnd = -1;
	private int endCharOffset = -1;

	public SLCommonTree() {
		super();
	}

	public SLCommonTree(final CommonTree node) {
		super(node);
		token = node.token;
	}

	public SLCommonTree(final Token t) {
		super(t);
	}

	@Override
	public Tree dupNode() {
		return new SLCommonTree(this);
	}

	public int getEndCharOffset() {
		return endCharOffset;
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public int getLineStart() {
		return lineStart;
	}

	public int getStartCharOffset() {
		return startCharOffset;
	}

	public void setEndCharOffset(final int endCharOffset) {
		this.endCharOffset = endCharOffset;
	}

	public void setLineEnd(final int lineEnd) {
		this.lineEnd = lineEnd;
	}

	public void setLineStart(final int lineStart) {
		this.lineStart = lineStart;
	}

	public void setStartCharOffset(final int startCharOffset) {
		this.startCharOffset = startCharOffset;
	}

}
