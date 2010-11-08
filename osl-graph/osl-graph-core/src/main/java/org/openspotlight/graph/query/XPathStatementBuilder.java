/**
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

import static org.openspotlight.common.util.StringBuilderUtil.append;
import static org.openspotlight.common.util.StringBuilderUtil.appendIfNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.query.XPathStatementBuilder.Statement.Condition;

/**
 * The Class SLXPathStatementBuilder.
 * 
 * @author Vitor Hugo Chagas
 */
public class XPathStatementBuilder {

    /**
     * The Class Statement.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class Statement {

        /**
         * The Class Condition.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class Condition {

            /**
             * The Class LeftOperand.
             * 
             * @author Vitor Hugo Chagas
             */
            public static class LeftOperand {

                /**
                 * The Class RelationalOperator.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static class RelationalOperator {

                    /** The condition. */
                    private final Condition condition;

                    /**
                     * Instantiates a new relational operator.
                     * 
                     * @param condition the condition
                     */
                    private RelationalOperator(
                                                final Condition condition) {
                        this.condition = condition;
                    }

                    /**
                     * Right operand.
                     * 
                     * @param value the value
                     * @return the statement
                     */
                    public Statement rightOperand(final Object value) {
                        condition.rightOperandValue = value;
                        condition.closed = true;
                        return condition.outerStatement;
                    }
                }

                /** The condition. */
                private final Condition condition;

                /**
                 * Instantiates a new left operand.
                 * 
                 * @param condition the condition
                 */
                private LeftOperand(
                                     final Condition condition) {
                    this.condition = condition;
                }

                /**
                 * Inexistent.
                 * 
                 * @return the statement
                 */
                public Statement inexistent() {
                    condition.closed = true;
                    condition.inexistent = true;
                    return condition.outerStatement;
                }

                /**
                 * Operator.
                 * 
                 * @param relationalOperator the relational operator
                 * @return the relational operator
                 */
                public RelationalOperator operator(final RelationalOperatorType relationalOperator) {
                    return operator(relationalOperator, false);
                }

                /**
                 * Operator.
                 * 
                 * @param relationalOperator the relational operator
                 * @param applyNot the apply not
                 * @return the relational operator
                 */
                public RelationalOperator operator(final RelationalOperatorType relationalOperator,
                                                    final boolean applyNot) {
                    condition.relationalOperator = relationalOperator;
                    condition.relationalOperatorApplyNot = applyNot;
                    return new RelationalOperator(condition);
                }
            }

            /** The closed. */
            private boolean                   closed     = false;

            /** The conditional operator. */
            private ConditionalOperatorType   conditionalOperator;

            /** The conditional operator apply not. */
            private boolean                   conditionalOperatorApplyNot;

            /** The inexistent. */
            private boolean                   inexistent = false;

            /** The inner statement. */
            private Statement                 innerStatement;

            /** The left operand value. */
            private Object                    leftOperandValue;

            /** The outer statement. */
            private Statement                 outerStatement;

            /** The relational operator. */
            private RelationalOperatorType    relationalOperator;

            /** The relational operator apply not. */
            private boolean                   relationalOperatorApplyNot;

            /** The right operand value. */
            private Object                    rightOperandValue;

            /** The stack trace. */
            private final StackTraceElement[] stackTrace;

            /**
             * Instantiates a new condition.
             * 
             * @param outerStatement the outer statement
             */
            private Condition(
                               final Statement outerStatement) {
                stackTrace = Thread.currentThread().getStackTrace();
                this.outerStatement = outerStatement;
            }

            /**
             * Left operand.
             * 
             * @param value the value
             * @return the left operand
             */
            public LeftOperand leftOperand(final Object value) {
                leftOperandValue = value;
                return new LeftOperand(this);
            }
        }

        /**
         * The Class ConditionalOperator.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class ConditionalOperator {

            /** The apply not. */
            private final boolean                 applyNot;

            /** The conditional operator. */
            private final ConditionalOperatorType conditionalOperator;

            /** The statement. */
            private final Statement               statement;

            /**
             * Instantiates a new conditional operator.
             * 
             * @param statement the statement
             * @param conditionalOperator the conditional operator
             * @param applyNot the apply not
             */
            private ConditionalOperator(
                                         final Statement statement, final ConditionalOperatorType conditionalOperator,
                                        final boolean applyNot) {
                this.statement = statement;
                this.conditionalOperator = conditionalOperator;
                this.applyNot = applyNot;
            }

            /**
             * Condition.
             * 
             * @return the condition
             */
            public Condition condition() {
                final Condition condition = new Condition(statement);
                condition.conditionalOperator = conditionalOperator;
                condition.conditionalOperatorApplyNot = applyNot;
                condition.outerStatement = statement;
                statement.conditions.add(condition);
                return condition;
            }

            /**
             * Open bracket.
             * 
             * @return the statement
             */
            public Statement openBracket() {
                final Condition condition = new Condition(statement);
                condition.conditionalOperator = conditionalOperator;
                condition.conditionalOperatorApplyNot = applyNot;
                statement.conditions.add(condition);
                final Statement innerStatement = new Statement(condition);
                condition.innerStatement = innerStatement;
                return innerStatement;
            }
        }

        /** The conditions. */
        private final List<Condition> conditions = new ArrayList<Condition>();

        /** The parent. */
        private final Condition       parent;

        /**
         * Instantiates a new statement.
         * 
         * @param parent the parent
         */
        private Statement(
                           final Condition parent) {
            this.parent = parent;
        }

        /**
         * Close bracket.
         * 
         * @return the statement
         */
        public Statement closeBracket() {
            parent.closed = true;
            return parent.outerStatement;
        }

        /**
         * Condition.
         * 
         * @return the condition
         */
        public Condition condition() {
            final Condition condition = new Condition(this);
            conditions.add(condition);
            return condition;
        }

        /**
         * Gets the condition count.
         * 
         * @return the condition count
         */
        public int getConditionCount() {
            return conditions.size();
        }

        /**
         * Open bracket.
         * 
         * @return the statement
         */
        public Statement openBracket() {
            final Condition condition = new Condition(this);
            conditions.add(condition);
            final Statement innerStatement = new Statement(condition);
            condition.innerStatement = innerStatement;
            return innerStatement;
        }

        /**
         * Operator.
         * 
         * @param conditionalOperator the conditional operator
         * @return the conditional operator
         */
        public ConditionalOperator operator(final ConditionalOperatorType conditionalOperator) {
            return operator(conditionalOperator, false);
        }

        /**
         * Operator.
         * 
         * @param conditionalOperator the conditional operator
         * @param applyNot the apply not
         * @return the conditional operator
         */
        public ConditionalOperator operator(final ConditionalOperatorType conditionalOperator,
                                             final boolean applyNot) {
            return new ConditionalOperator(this, conditionalOperator, applyNot);
        }
    }

    /** The order by. */
    private String    orderBy;

    /** The path. */
    private String    path;

    /** The root statement. */
    private Statement rootStatement;

    /**
     * Instantiates a new sLX path statement builder.
     */
    public XPathStatementBuilder() {}

    /**
     * Instantiates a new sLX path statement builder.
     * 
     * @param path the path
     */
    public XPathStatementBuilder(
                                    final String path) {
        this.path = path;
    }

    /**
     * Prints the statement.
     * 
     * @param buffer the buffer
     * @param statement the statement
     * @param identLevel the ident level
     */
    private void printStatement(final StringBuilder buffer,
                                 final Statement statement,
                                 final int identLevel) {
        validate(rootStatement);
        final StringBuilder statementBuffer = new StringBuilder();
        final String tabs0 = StringUtils.repeat("\t", identLevel);
        final String tabs1 = StringUtils.repeat("\t", identLevel + 1);
        for (final Condition condition: statement.conditions) {
            append(statementBuffer, tabs0);
            if (condition.innerStatement == null) {
                if (condition.conditionalOperator != null) {
                    if (condition.conditionalOperator != null) {
                        append(statementBuffer, condition.conditionalOperator.symbol().toLowerCase(), ' ');
                    }
                    if (condition.conditionalOperatorApplyNot) {
                        append(statementBuffer, "not(\n", tabs1);
                    }
                }
                if (condition.inexistent) {
                    append(statementBuffer, "not(@", condition.leftOperandValue, ")\n");
                } else {
                    final String expression = condition.relationalOperator.xPathExpression(condition.leftOperandValue,
                                                                                     condition.rightOperandValue,
                                                                                     condition.relationalOperatorApplyNot);
                    append(statementBuffer, expression, '\n');
                    if (condition.conditionalOperator != null && condition.conditionalOperatorApplyNot) {
                        append(statementBuffer, tabs0, ")\n");
                    }
                }
            } else {
                if (condition.conditionalOperator != null) {
                    append(statementBuffer, condition.conditionalOperator.symbol().toLowerCase(), ' ');
                }
                if (condition.conditionalOperatorApplyNot) {
                    append(statementBuffer, "not(\n");
                } else {
                    append(statementBuffer, "(\n");
                }
                printStatement(statementBuffer, condition.innerStatement, identLevel + 1);
                append(statementBuffer, tabs0, ")\n");
            }
        }
        append(buffer, statementBuffer);
    }

    /**
     * Validate.
     * 
     * @param statement the statement
     */
    private void validate(final Statement statement) {
        if (statement != null) {
            for (final Condition condition: statement.conditions) {
                if (!condition.closed) {
                    final RuntimeException e = new SLRuntimeException("All conditions must be closed.");
                    e.setStackTrace(condition.stackTrace);
                    throw e;
                }
                validate(condition.innerStatement);
            }
        }
    }

    /**
     * Gets the root statement.
     * 
     * @return the root statement
     */
    public Statement getRootStatement() {
        if (rootStatement == null) {
            rootStatement = new Statement(null);
        }
        return rootStatement;
    }

    /**
     * Gets the x path.
     * 
     * @return the x path
     */
    public String getXPath() {
        final StringBuilder buffer = new StringBuilder();
        final StringBuilder statementBuffer = new StringBuilder();
        printStatement(statementBuffer, rootStatement, 0);
        append(buffer, path, "\n[\n", statementBuffer, "]");
        appendIfNotNull(buffer, orderBy, "\norder by @", orderBy);
        return buffer.toString();
    }

    /**
     * Gets the x path string.
     * 
     * @return the x path string
     */
    public String getXPathString() {
        String xpath = getXPath();
        xpath = StringUtils.replace(xpath, "\t", "");
        xpath = StringUtils.replace(xpath, "\n", " ");
        return xpath;
    }

    /**
     * Sets the order by.
     * 
     * @param orderBy the new order by
     */
    public void setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Sets the path.
     * 
     * @param path the new path
     */
    public void setPath(final String path) {
        this.path = path;
    }
}
