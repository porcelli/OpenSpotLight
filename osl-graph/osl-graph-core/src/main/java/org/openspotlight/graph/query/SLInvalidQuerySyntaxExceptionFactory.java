/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.query;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.MismatchedNotSetException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.MismatchedTreeNodeException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.openspotlight.graph.query.parser.SLQLLexer;

/**
 * Helper class that generates SLQueryLanguageParserException with user friendly error messages.
 * 
 * @author porcelli
 * @see SLInvalidQuerySyntaxException
 */
public class SLInvalidQuerySyntaxExceptionFactory {

    /** The Constant MISMATCHED_TOKEN_MESSAGE_COMPLETE. */
    private final static String MISMATCHED_TOKEN_MESSAGE_COMPLETE     = "Line %1$d:%2$d mismatched input '%3$s' expecting '%4$s'";

    /** The Constant MISMATCHED_TOKEN_MESSAGE_PART. */
    private final static String MISMATCHED_TOKEN_MESSAGE_PART         = "Line %1$d:%2$d mismatched input '%3$s'";

    /** The Constant MISMATCHED_TREE_NODE_MESSAGE_COMPLETE. */
    private final static String MISMATCHED_TREE_NODE_MESSAGE_COMPLETE = "Line %1$d:%2$d mismatched tree node '%3$s' expecting '%4$s'";

    /** The Constant MISMATCHED_TREE_NODE_MESSAGE_PART. */
    private final static String MISMATCHED_TREE_NODE_MESSAGE_PART     = "Line %1$d:%2$d mismatched tree node '%3$s'";

    /** The Constant NO_VIABLE_ALT_MESSAGE. */
    private final static String NO_VIABLE_ALT_MESSAGE                 = "Line %1$d:%2$d no viable alternative at input '%3$s'";

    /** The Constant EARLY_EXIT_MESSAGE. */
    private final static String EARLY_EXIT_MESSAGE                    = "Line %1$d:%2$d required (...)+ loop did not match anything at input '%3$s'";

    /** The Constant MISMATCHED_SET_MESSAGE. */
    private final static String MISMATCHED_SET_MESSAGE                = "Line %1$d:%2$d mismatched input '%3$' expecting set '%4$s'.";

    /** The Constant MISMATCHED_NOT_SET_MESSAGE. */
    private final static String MISMATCHED_NOT_SET_MESSAGE            = "Line %1$d:%2$d mismatched input '%3$' expecting set '%4$s'";

    /** The Constant FAILED_PREDICATE_MESSAGE. */
    private final static String FAILED_PREDICATE_MESSAGE              = "Line %1$d:%2$d rule '%3$s' failed predicate: {%4$s}?";

    /** The token names. */
    private String[]            tokenNames                            = null;

    /**
     * SLInvalidQuerySyntaxExceptionFactory constructor.
     * 
     * @param tokenNames tokenNames generated by ANTLR
     */
    public SLInvalidQuerySyntaxExceptionFactory(
                                                 String[] tokenNames ) {
        this.tokenNames = tokenNames;
    }

    /**
     * This method creates a SLInvalidQuerySyntaxException full of information.
     * 
     * @param e original exception
     * @return SLInvalidQuerySyntaxException filled.
     */
    public SLInvalidQuerySyntaxException createSLQueryLanguageException(
                                                                         RecognitionException e ) {
        List<String> codeAndMessage = createErrorMessage(e);
        return new SLInvalidQuerySyntaxException(codeAndMessage.get(1),
                                                 codeAndMessage.get(0), e.line, e.charPositionInLine, e.index, e);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createSLQueryLanguageException() {
        return new SLInvalidQuerySyntaxException("");
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createInvalidExecutingException() {
        return new SLInvalidQuerySyntaxException("ERR 108",
                                                 "invalid use of executing times", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createInvalidDoubleStarException() {
        return new SLInvalidQuerySyntaxException("ERR 109",
                                                 "invalid use of double star (**)", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createDuplicatedDefineMessageException() {
        return new SLInvalidQuerySyntaxException("ERR 110",
                                                 "invalid use of double star (**)", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createDuplicatedDefineDomainException() {
        return new SLInvalidQuerySyntaxException("ERR 111",
                                                 "invalid use of double star (**)", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createDefineTargetWithoutByLinkException() {
        return new SLInvalidQuerySyntaxException("ERR 112",
                                                 "invalid use of double star (**)", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createByLinkWithoutDefineTargetException() {
        return new SLInvalidQuerySyntaxException("ERR 113",
                                                 "invalid use of double star (**)", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createCannotUseWhereWithByLInkException() {
        return new SLInvalidQuerySyntaxException("ERR 114",
                                                 "invalid use of double star (**)", -1, -1, -1, null);
    }

    /**
     * Creates a new SLInvalidQuerySyntaxException object.
     * 
     * @return the SL invalid query syntax exception
     */
    public SLInvalidQuerySyntaxException createDefineTargetWithByLinkException() {
        return new SLInvalidQuerySyntaxException("ERR 115",
                                                 "invalid use of by link in define target", -1, -1, -1, null);
    }

    /**
     * This will take a RecognitionException, and create a sensible error message out of it.
     * 
     * @param e the e
     * @return the list< string>
     */
    private List<String> createErrorMessage( RecognitionException e ) {
        List<String> codeAndMessage = new ArrayList<String>(2);
        String message = "";
        if (e instanceof MismatchedTokenException) {
            MismatchedTokenException mte = (MismatchedTokenException)e;
            if (tokenNames != null && mte.expecting >= 0
                && mte.expecting < tokenNames.length) {
                message = String
                                .format(
                                        SLInvalidQuerySyntaxExceptionFactory.MISMATCHED_TOKEN_MESSAGE_COMPLETE,
                                        e.line, e.charPositionInLine,
                                        getBetterToken(e.token),
                                        getBetterToken(mte.expecting));
                codeAndMessage.add(message);
                codeAndMessage.add("ERR 101");
            } else {
                message = String
                                .format(
                                        SLInvalidQuerySyntaxExceptionFactory.MISMATCHED_TOKEN_MESSAGE_PART,
                                        e.line, e.charPositionInLine,
                                        getBetterToken(e.token));
                codeAndMessage.add(message);
                codeAndMessage.add("ERR 101");
            }
        } else if (e instanceof MismatchedTreeNodeException) {
            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
            if (mtne.expecting >= 0 && mtne.expecting < tokenNames.length) {
                message = String
                                .format(
                                        SLInvalidQuerySyntaxExceptionFactory.MISMATCHED_TREE_NODE_MESSAGE_COMPLETE,
                                        e.line, e.charPositionInLine,
                                        getBetterToken(e.token),
                                        getBetterToken(mtne.expecting));
                codeAndMessage.add(message);
                codeAndMessage.add("ERR 102");
            } else {
                message = String
                                .format(
                                        SLInvalidQuerySyntaxExceptionFactory.MISMATCHED_TREE_NODE_MESSAGE_PART,
                                        e.line, e.charPositionInLine,
                                        getBetterToken(e.token));
                codeAndMessage.add(message);
                codeAndMessage.add("ERR 102");
            }
        } else if (e instanceof NoViableAltException) {
            // NoViableAltException nvae = (NoViableAltException) e;
            message = String.format(
                                    SLInvalidQuerySyntaxExceptionFactory.NO_VIABLE_ALT_MESSAGE,
                                    e.line, e.charPositionInLine, getBetterToken(e.token));
            codeAndMessage.add(message);
            codeAndMessage.add("ERR 103");
        } else if (e instanceof EarlyExitException) {
            // EarlyExitException eee = (EarlyExitException) e;
            message = String.format(
                                    SLInvalidQuerySyntaxExceptionFactory.EARLY_EXIT_MESSAGE, e.line,
                                    e.charPositionInLine, getBetterToken(e.token));
            codeAndMessage.add(message);
            codeAndMessage.add("ERR 104");
        } else if (e instanceof MismatchedSetException) {
            MismatchedSetException mse = (MismatchedSetException)e;
            message = String.format(
                                    SLInvalidQuerySyntaxExceptionFactory.MISMATCHED_SET_MESSAGE,
                                    e.line, e.charPositionInLine, getBetterToken(e.token),
                                    mse.expecting);
            codeAndMessage.add(message);
            codeAndMessage.add("ERR 105");
        } else if (e instanceof MismatchedNotSetException) {
            MismatchedNotSetException mse = (MismatchedNotSetException)e;
            message = String.format(
                                    SLInvalidQuerySyntaxExceptionFactory.MISMATCHED_NOT_SET_MESSAGE,
                                    e.line, e.charPositionInLine, getBetterToken(e.token),
                                    mse.expecting);
            codeAndMessage.add(message);
            codeAndMessage.add("ERR 106");
        } else if (e instanceof FailedPredicateException) {
            FailedPredicateException fpe = (FailedPredicateException)e;
            message = String.format(
                                    SLInvalidQuerySyntaxExceptionFactory.FAILED_PREDICATE_MESSAGE,
                                    e.line, e.charPositionInLine, fpe.ruleName,
                                    fpe.predicateText);
            codeAndMessage.add(message);
            codeAndMessage.add("ERR 107");
        }
        if (codeAndMessage.get(0).length() == 0) {
            codeAndMessage.add("?????");
        }
        return codeAndMessage;
    }

    /**
     * Helper method that creates a user friendly token definition.
     * 
     * @param token token
     * @return user friendly token definition
     */
    private String getBetterToken( Token token ) {
        if (token == null) {
            return "";
        }
        return getBetterToken(token.getType(), token.getText());
    }

    /**
     * Helper method that creates a user friendly token definition.
     * 
     * @param tokenType token type
     * @return user friendly token definition
     */
    private String getBetterToken( int tokenType ) {
        return getBetterToken(tokenType, null);
    }

    /**
     * Helper method that creates a user friendly token definition.
     * 
     * @param tokenType token type
     * @param defaultValue default value for identifier token, may be null
     * @return user friendly token definition
     */
    private String getBetterToken( int tokenType,
                                   String defaultValue ) {
        switch (tokenType) {
            case SLQLLexer.INT:
                return defaultValue == null ? "int" : defaultValue;
            case SLQLLexer.DEC:
                return defaultValue == null ? "decimal" : defaultValue;
            case SLQLLexer.STRING:
                return defaultValue == null ? "string" : defaultValue;
            case SLQLLexer.SEMICOLON:
                return ";";
            case SLQLLexer.STAR:
                return "*";
            case SLQLLexer.DOUBLE_STAR:
                return "**";
            case SLQLLexer.EQUALS:
                return "=";
            case SLQLLexer.NOT_EQUALS:
                return "!=";
            case SLQLLexer.GREATER:
                return ">";
            case SLQLLexer.GREATER_OR_EQUALS:
                return ">=";
            case SLQLLexer.LESSER:
                return "<";
            case SLQLLexer.LESSER_OR_EQUALS:
                return "<=";
            case SLQLLexer.ID:
                return defaultValue == null ? "identifier" : defaultValue;
            case SLQLLexer.LEFT_PAREN:
                return "(";
            case SLQLLexer.RIGHT_PAREN:
                return ")";
            case SLQLLexer.LEFT_SQUARE:
                return "[";
            case SLQLLexer.RIGHT_SQUARE:
                return "]";
            case SLQLLexer.COMMA:
                return ",";
            case SLQLLexer.DOT:
                return ".";
            case SLQLLexer.OR_OPERATOR:
                return "||";
            case SLQLLexer.AND_OPERATOR:
                return "&&";
            case SLQLLexer.EOF:
                return "<eof>";
            default:
                return tokenType > tokenNames.length ? "unknown"
                    : tokenNames[tokenType];
        }
    }
}
