package org.openspotlight.bundle.common.parser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openspotlight.graph.SLNode;

public class SLCommonTree extends CommonTree implements SLLineInfo {

    private int    startCharOffset       = -1;
    private int    endCharOffset         = -1;
    private int    endCharPositionInLine = -1;
    private int    endLine               = -1;

    private SLNode node;

    public SLCommonTree() {
        super();
    }

    public SLCommonTree(
                         final CommonTree node ) {
        super(node);
        token = node.token;
        if (node instanceof SLLineInfo) {
            SLLineInfo typed = (SLLineInfo)node;
            setEndCharPositionInLine(typed.getEndCharPositionInLine());
            setEndLine(typed.getEndLine());
        }
    }

    public SLCommonTree(
                         final Token t ) {
        super(t);
        if (t instanceof SLLineInfo) {
            SLLineInfo typed = (SLLineInfo)t;
            setEndCharPositionInLine(typed.getEndCharPositionInLine());
            setEndLine(typed.getEndLine());
        }
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

    public int getStartCharOffset() {
        return startCharOffset;
    }

    public void setEndCharOffset( final int endCharOffset ) {
        this.endCharOffset = endCharOffset;
    }

    public void setNode( final SLNode node ) {
        this.node = node;
    }

    public void setStartCharOffset( final int startCharOffset ) {
        this.startCharOffset = startCharOffset;
    }

    public int getEndCharPositionInLine() {
        if ((this.token == null) || (this.token.getCharPositionInLine() == -1)) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(getChildCount())).getEndCharPositionInLine();
            }
            return 0;
        }
        return this.endCharPositionInLine;
    }

    public void setEndCharPositionInLine( int charPositionInLine ) {
        this.endCharPositionInLine = charPositionInLine;
    }

    public int getEndLine() {
        if ((this.token == null) || (this.token.getCharPositionInLine() == -1)) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(getChildCount())).getEndLine();
            }
            return 0;
        }
        return this.endLine;
    }

    public void setEndLine( int endLine ) {
        this.endLine = endLine;
    }

    public int getStartCharPositionInLine() {
        return getCharPositionInLine();
    }

    public int getStartLine() {
        return getLine();
    }

    public SLArtifactStream getArtifact() {
        if ((this.token == null)) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(0)).getArtifact();
            }
            return null;
        }
        return ((SLCommonToken)this.token).getArtifact();
    }
}
