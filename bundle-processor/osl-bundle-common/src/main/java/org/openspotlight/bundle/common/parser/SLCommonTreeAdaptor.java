package org.openspotlight.bundle.common.parser;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

public class SLCommonTreeAdaptor extends CommonTreeAdaptor {

    @Override
    public void addChild( final Object t,
                          final Object child ) {
        if (t != null && child != null) {
            if (t instanceof SLCommonTree && child instanceof SLCommonTree) {
                final SLCommonTree tParent = (SLCommonTree)t;
                final SLCommonTree tChild = (SLCommonTree)child;

                if (0 >= tParent.getStartCharOffset()) {
                    tParent.setStartCharOffset(tChild.getStartCharOffset());
                    tParent.setEndCharOffset(tChild.getEndCharOffset());
                }
                if (0 < tParent.getChildCount()) {
                    tParent.setEndCharOffset(tChild.getEndCharOffset());
                }
            }
            final Tree typedT = (Tree)t;
            final Tree typedChild = (Tree)child;
            typedT.addChild(typedChild);
        }
    }

    @Override
    public Object create( final int tokenType,
                          final Token fromToken,
                          final String text ) {
        if (fromToken instanceof SLCommonToken) {
            final SLCommonToken result = (SLCommonToken)super.create(
                                                                     tokenType, fromToken, text);
            fillData(tokenType, fromToken, text, result);
            return result;
        }
        return super.create(tokenType, fromToken, text);
    }

    @Override
    public Object create( final Token payload ) {
        final SLCommonTree tree = new SLCommonTree(payload);
        if (payload instanceof SLCommonToken) {
            final CommonToken typed = (CommonToken)payload;
            tree.setStartCharOffset(typed.getStartIndex());
            tree.setEndCharOffset(typed.getStopIndex());
        }
        return tree;
    }

    @Override
    public Token createToken( final int tokenType,
                              final String text ) {
        return new SLCommonToken(tokenType, text);
    }

    @Override
    public Token createToken( final Token fromToken ) {
        return new SLCommonToken(fromToken);
    }

    private void fillData( final int tokenType,
                           final Token fromToken,
                           final String text,
                           final SLCommonToken result ) {
        final SLCommonToken typed = (SLCommonToken)fromToken;
        result.setStartIndex(typed.getStartIndex());
        if (text == null) {
            result.setStopIndex(typed.getStopIndex());
        } else {
            result.setEndCharPositionInLine(result.getStartIndex()
                                            + text.length());
        }
        result.setLine(typed.getLine());
        result.setEndLine(typed.getLine());
        result.setCharPositionInLine(typed.getStartCharPositionInLine());
        result.setEndCharPositionInLine(typed.getEndCharPositionInLine());
    }

}
