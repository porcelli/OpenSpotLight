package org.openspotlight.bundle.common.parser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;

public class SLCommonTree extends CommonTree {

	private int startCharOffset = -1;

	private int endCharOffset = -1;

	private SLNode node;

	private SLLink principalLink;

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

	public SLNode getNode() {
		return node;
	}

	public SLLink getPrincipalLink() {
		return principalLink;
	}

	public int getStartCharOffset() {
		return startCharOffset;
	}

	public void setEndCharOffset(final int endCharOffset) {
		this.endCharOffset = endCharOffset;
	}

	public void setNode(final SLNode node) {
		this.node = node;
	}

	public void setPrincipalLink(final SLLink principalLink) {
		this.principalLink = principalLink;
	}

	public void setStartCharOffset(final int startCharOffset) {
		this.startCharOffset = startCharOffset;
	}

}
