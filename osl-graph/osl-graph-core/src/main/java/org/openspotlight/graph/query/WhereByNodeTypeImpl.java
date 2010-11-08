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

import org.openspotlight.graph.query.info.WhereByNodeTypeInfo;
import org.openspotlight.graph.query.info.WhereByNodeTypeInfo.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.WhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;

/**
 * The Class SLWhereByNodeTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class WhereByNodeTypeImpl implements WhereByNodeType, WhereByNodeTypeInfoGetter {

    /**
     * private void verifyConditionalOperator() { if (statementInfo.getConditionInfoList().isEmpty()) { throw new
     * SLInvalidQuerySyntaxRuntimeException( "the first condition of a statement must not start with AND or OR operators" ); } }
     */

    public static class EndImpl implements End {

        /** The order by statement. */
        private final OrderByStatement    orderByStatement;

        /** The select facade. */
        private final SelectFacade        selectFacade;

        /** The where by node type info. */
        private final WhereByNodeTypeInfo whereByNodeTypeInfo;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param whereByNodeTypeInfo the where by node type info
         * @param orderByStatement the order by statement
         */
        public EndImpl(
                        final SelectFacade selectFacade, final WhereByNodeTypeInfo whereByNodeTypeInfo,
                        final OrderByStatement orderByStatement) {
            this.selectFacade = selectFacade;
            this.whereByNodeTypeInfo = whereByNodeTypeInfo;
            this.orderByStatement = orderByStatement;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes() {
            whereByNodeTypeInfo.getSelectByNodeTypeInfo().setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes(final int x) {
            whereByNodeTypeInfo.getSelectByNodeTypeInfo().setXTimes(x);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End keepResult() {
            whereByNodeTypeInfo.getSelectByNodeTypeInfo().setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit) {
            whereByNodeTypeInfo.getSelectByNodeTypeInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit,
                          final Integer offset) {
            whereByNodeTypeInfo.getSelectByNodeTypeInfo().setLimit(limit);
            whereByNodeTypeInfo.getSelectByNodeTypeInfo().setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OrderByStatement orderBy() {
            return orderByStatement;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectStatement select() {
            return selectFacade.select();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectByLinkCount selectByLinkCount() {
            return selectFacade.selectByLinkCount();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectByLinkType selectByLinkType() {
            return selectFacade.selectByLinkType();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectByNodeType selectByNodeType() {
            return selectFacade.selectByNodeType();
        }
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each {

            /**
             * The Class LinkImpl.
             * 
             * @author Vitor Hugo Chagas
             */
            public static class LinkImpl implements Link {

                /**
                 * The Class SideImpl.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static class SideImpl implements Side {

                    /**
                     * The Class CountImpl.
                     * 
                     * @author Vitor Hugo Chagas
                     */
                    public static class CountImpl implements Count {

                        /**
                         * The Class OperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class OperatorImpl implements Operator {

                            /**
                             * The Class ValueImpl.
                             * 
                             * @author Vitor Hugo Chagas
                             */
                            public static class ValueImpl implements Value {

                                /**
                                 * The Class CloseBracketImpl.
                                 * 
                                 * @author Vitor Hugo Chagas
                                 */
                                public static class CloseBracketImpl implements CloseBracket {

                                    /** The condition info. */
                                    private final SLConditionInfo conditionInfo;

                                    /** The outer each. */
                                    private final Each            outerEach;

                                    /** The where statement. */
                                    private final WhereByNodeType whereStatement;

                                    /**
                                     * Instantiates a new close bracket impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param outerEach the outer each
                                     * @param conditionInfo the condition info
                                     */
                                    public CloseBracketImpl(
                                                             final WhereByNodeType whereStatement, final Each outerEach,
                                                             final SLConditionInfo conditionInfo) {
                                        this.whereStatement = whereStatement;
                                        this.outerEach = outerEach;
                                        this.conditionInfo = conditionInfo;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public RelationalOperator and() {
                                        final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                        outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                        final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                        return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public RelationalOperator or() {
                                        final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                        outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                        final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                        return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public WhereByNodeType typeEnd() {
                                        return whereStatement;
                                    }
                                }

                                /**
                                 * The Class RelationalOperatorImpl.
                                 * 
                                 * @author Vitor Hugo Chagas
                                 */
                                public static class RelationalOperatorImpl implements RelationalOperator {

                                    /**
                                     * The Class OpenBracketImpl.
                                     * 
                                     * @author Vitor Hugo Chagas
                                     */
                                    public static class OpenBracketImpl implements OpenBracket {

                                        /** The each. */
                                        private final Each each;

                                        /**
                                         * Instantiates a new open bracket impl.
                                         * 
                                         * @param each the each
                                         */
                                        public OpenBracketImpl(
                                                                final Each each) {
                                            this.each = each;
                                        }

                                        /**
                                         * {@inheritDoc}
                                         */
                                        @Override
                                        public Each each() {
                                            return each;
                                        }
                                    }

                                    /** The condition info. */
                                    private final SLConditionInfo conditionInfo;

                                    /** The each. */
                                    private final Each            each;

                                    /** The where statement. */
                                    private final WhereByNodeType whereStatement;

                                    /**
                                     * Instantiates a new relational operator impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param each the each
                                     * @param conditionInfo the condition info
                                     */
                                    public RelationalOperatorImpl(
                                                                   final WhereByNodeType whereStatement, final Each each,
                                                                   final SLConditionInfo conditionInfo) {
                                        this.each = each;
                                        this.whereStatement = whereStatement;
                                        this.conditionInfo = conditionInfo;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public WhereByNodeType comma() {
                                        return whereStatement;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public Each each() {
                                        return each;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public RelationalOperator not() {
                                        conditionInfo.setConditionalNotOperator(true);
                                        return this;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public OpenBracket openBracket() {
                                        final SLTypeStatementInfo newStatementInfo =
                                            new SLTypeStatementInfo(
                                                                                                       conditionInfo
                                                                                                           .getTypeInfo());
                                        conditionInfo.setInnerStatementInfo(newStatementInfo);
                                        final SLConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                        final Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                        return new OpenBracketImpl(each);
                                    }
                                }

                                /** The condition info. */
                                private final SLConditionInfo conditionInfo;

                                /** The each. */
                                private final Each            each;

                                /** The outer each. */
                                private final Each            outerEach;

                                /** The where statement. */
                                private final WhereByNodeType whereStatement;

                                /**
                                 * Instantiates a new value impl.
                                 * 
                                 * @param whereStatement the where statement
                                 * @param each the each
                                 * @param outerEach the outer each
                                 * @param conditionInfo the condition info
                                 */
                                public ValueImpl(
                                                  final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                                  final SLConditionInfo conditionInfo) {
                                    this.each = each;
                                    this.outerEach = outerEach;
                                    this.whereStatement = whereStatement;
                                    this.conditionInfo = conditionInfo;
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public RelationalOperator and() {
                                    final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                    final SLConditionInfo newConditionInfo =
                                        outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                    final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                                    return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public CloseBracket closeBracket() {
                                    return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public RelationalOperator or() {
                                    final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                    final SLConditionInfo newConditionInfo =
                                        outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                    final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                                    return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public WhereByNodeType typeEnd() {
                                    return whereStatement;
                                }
                            }

                            /** The condition info. */
                            private final SLConditionInfo conditionInfo;

                            /** The each. */
                            private final Each            each;

                            /** The outer each. */
                            private final Each            outerEach;

                            /** The where statement. */
                            private final WhereByNodeType whereStatement;

                            /**
                             * Instantiates a new operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public OperatorImpl(
                                                 final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                                 final SLConditionInfo conditionInfo) {
                                this.each = each;
                                this.outerEach = outerEach;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public Value value(final Integer value) {
                                conditionInfo.setValue(value);
                                return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                            }
                        }

                        /** The condition info. */
                        private final SLConditionInfo conditionInfo;

                        /** The each. */
                        private final Each            each;

                        /** The outer each. */
                        private final Each            outerEach;

                        /** The where statement. */
                        private final WhereByNodeType whereStatement;

                        /**
                         * Instantiates a new count impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public CountImpl(
                                          final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                          final SLConditionInfo conditionInfo) {
                            this.each = each;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                            this.outerEach = outerEach;
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByNodeType.Type .Each.Property#contains()
                         */
                        /**
                         * Contains.
                         * 
                         * @return the operator
                         */
                        public Operator contains() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.CONTAINS);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByNodeType.Type .Each.Property#endsWith()
                         */
                        /**
                         * Ends with.
                         * 
                         * @return the operator
                         */
                        public Operator endsWith() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.ENDS_WITH);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public Operator equalsTo() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.EQUAL);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public Operator greaterOrEqualThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_OR_EQUAL_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public Operator greaterThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public Operator lesserOrEqualThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public Operator lesserThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public Count not() {
                            conditionInfo.setRelationalNotOperator(true);
                            return this;
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByNodeType.Type .Each.Property#startsWith()
                         */
                        /**
                         * Starts with.
                         * 
                         * @return the operator
                         */
                        public Operator startsWith() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.STARTS_WITH);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }
                    }

                    /** The condition info. */
                    private final SLConditionInfo conditionInfo;

                    /** The each. */
                    private final Each            each;

                    /** The outer each. */
                    private final Each            outerEach;

                    /** The where statement. */
                    private final WhereByNodeType whereStatement;

                    /**
                     * Instantiates a new side impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public SideImpl(
                                     final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                    final SLConditionInfo conditionInfo) {
                        this.whereStatement = whereStatement;
                        this.each = each;
                        this.outerEach = outerEach;
                        this.conditionInfo = conditionInfo;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Count count() {
                        return new CountImpl(whereStatement, each, outerEach, conditionInfo);
                    }
                }

                /** The condition info. */
                private final SLConditionInfo conditionInfo;

                /** The each. */
                private final Each            each;

                /** The outer each. */
                private final Each            outerEach;

                /** The where statement. */
                private final WhereByNodeType whereStatement;

                /**
                 * Instantiates a new link impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public LinkImpl(
                                 final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                final SLConditionInfo conditionInfo) {
                    this.whereStatement = whereStatement;
                    this.each = each;
                    this.outerEach = outerEach;
                    this.conditionInfo = conditionInfo;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Side a() {
                    conditionInfo.setSide(SideType.A_SIDE);
                    return new SideImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Side b() {
                    conditionInfo.setSide(SideType.B_SIDE);
                    return new SideImpl(whereStatement, each, outerEach, conditionInfo);
                }
            }

            /**
             * The Class PropertyImpl.
             * 
             * @author Vitor Hugo Chagas
             */
            public static class PropertyImpl implements Property {

                /**
                 * The Class OperatorImpl.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static class OperatorImpl implements Operator {

                    /**
                     * The Class ValueImpl.
                     * 
                     * @author Vitor Hugo Chagas
                     */
                    public static class ValueImpl implements Value {

                        /**
                         * The Class CloseBracketImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class CloseBracketImpl implements CloseBracket {

                            /** The condition info. */
                            private final SLConditionInfo conditionInfo;

                            /** The outer each. */
                            private final Each            outerEach;

                            /** The where statement. */
                            private final WhereByNodeType whereStatement;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     final WhereByNodeType whereStatement, final Each outerEach,
                                                     final SLConditionInfo conditionInfo) {
                                this.whereStatement = whereStatement;
                                this.outerEach = outerEach;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public RelationalOperator and() {
                                final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public RelationalOperator or() {
                                final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public WhereByNodeType typeEnd() {
                                return whereStatement;
                            }
                        }

                        /**
                         * The Class RelationalOperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class RelationalOperatorImpl implements RelationalOperator {

                            /**
                             * The Class OpenBracketImpl.
                             * 
                             * @author Vitor Hugo Chagas
                             */
                            public static class OpenBracketImpl implements OpenBracket {

                                /** The each. */
                                private final Each each;

                                /**
                                 * Instantiates a new open bracket impl.
                                 * 
                                 * @param each the each
                                 */
                                public OpenBracketImpl(
                                                        final Each each) {
                                    this.each = each;
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public Each each() {
                                    return each;
                                }
                            }

                            /** The condition info. */
                            private final SLConditionInfo conditionInfo;

                            /** The each. */
                            private final Each            each;

                            /** The where statement. */
                            private final WhereByNodeType whereStatement;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           final WhereByNodeType whereStatement, final Each each,
                                                           final SLConditionInfo conditionInfo) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public WhereByNodeType comma() {
                                return whereStatement;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public Each each() {
                                return each;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public RelationalOperator not() {
                                conditionInfo.setConditionalNotOperator(true);
                                return this;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public OpenBracket openBracket() {
                                final SLTypeStatementInfo newStatementInfo = new SLTypeStatementInfo(conditionInfo.getTypeInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                final SLConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                final Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                return new OpenBracketImpl(each);
                            }
                        }

                        /** The condition info. */
                        private final SLConditionInfo conditionInfo;

                        /** The each. */
                        private final Each            each;

                        /** The outer each. */
                        private final Each            outerEach;

                        /** The where statement. */
                        private final WhereByNodeType whereStatement;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                          final SLConditionInfo conditionInfo) {
                            this.each = each;
                            this.outerEach = outerEach;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public RelationalOperator and() {
                            final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public CloseBracket closeBracket() {
                            return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public RelationalOperator or() {
                            final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public WhereByNodeType typeEnd() {
                            return whereStatement;
                        }
                    }

                    /** The condition info. */
                    private final SLConditionInfo conditionInfo;

                    /** The each. */
                    private final Each            each;

                    /** The outer each. */
                    private final Each            outerEach;

                    /** The where statement. */
                    private final WhereByNodeType whereStatement;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                         final SLConditionInfo conditionInfo) {
                        this.each = each;
                        this.outerEach = outerEach;
                        this.whereStatement = whereStatement;
                        this.conditionInfo = conditionInfo;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Value value(final Boolean value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Value value(final Double value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Value value(final Float value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Value value(final Integer value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Value value(final Long value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public Value value(final String value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }
                }

                /** The condition info. */
                private final SLConditionInfo conditionInfo;

                /** The each. */
                private final Each            each;

                /** The outer each. */
                private final Each            outerEach;

                /** The where statement. */
                private final WhereByNodeType whereStatement;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     final WhereByNodeType whereStatement, final Each each, final Each outerEach,
                                    final SLConditionInfo conditionInfo) {
                    this.each = each;
                    this.whereStatement = whereStatement;
                    this.conditionInfo = conditionInfo;
                    this.outerEach = outerEach;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator contains() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.CONTAINS);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator endsWith() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.ENDS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator equalsTo() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.EQUAL);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator greaterOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator greaterThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator lesserOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator lesserThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Property not() {
                    conditionInfo.setRelationalNotOperator(true);
                    return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Operator startsWith() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.STARTS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }
            }

            /** The condition info. */
            private final SLConditionInfo conditionInfo;

            /** The outer each. */
            private final Each            outerEach;

            /** The where statement. */
            private final WhereByNodeType whereStatement;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             final WhereByNodeType whereStatement, final SLConditionInfo conditionInfo) {
                this(whereStatement, conditionInfo, null);
            }

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             * @param outerEach the outer each
             */
            public EachImpl(
                             final WhereByNodeType whereStatement, final SLConditionInfo conditionInfo, final Each outerEach) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Link link(final String name) {
                conditionInfo.setLinkTypeName(name);
                return new LinkImpl(whereStatement, this, outerEach, conditionInfo);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Property property(final String name) {
                conditionInfo.setPropertyName(name);
                return new PropertyImpl(whereStatement, this, outerEach, conditionInfo);
            }
        }

        /**
         * The Class SubTypesImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class SubTypesImpl implements SubTypes {

            /** The type info. */
            private final SLWhereTypeInfo typeInfo;

            /** The where statement. */
            private final WhereByNodeType whereStatement;

            /**
             * Instantiates a new sub types impl.
             * 
             * @param whereStatement the where statement
             * @param typeInfo the type info
             */
            public SubTypesImpl(
                                 final WhereByNodeType whereStatement, final SLWhereTypeInfo typeInfo) {
                this.whereStatement = whereStatement;
                this.typeInfo = typeInfo;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Each each() {
                final SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo);
                typeInfo.setTypeStatementInfo(whereStatementInfo);
                final SLConditionInfo conditionInfo = whereStatementInfo.addCondition();
                return new EachImpl(whereStatement, conditionInfo);
            }
        }

        /** The type info. */
        private final SLWhereTypeInfo typeInfo;

        /** The where statement. */
        private final WhereByNodeType whereStatement;

        /**
         * Instantiates a new type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public TypeImpl(
                         final WhereByNodeType whereStatement, final SLWhereTypeInfo typeInfo) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Each each() {
            final SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo);
            typeInfo.setTypeStatementInfo(whereStatementInfo);
            final SLConditionInfo conditionInfo = whereStatementInfo.addCondition();
            return new EachImpl(whereStatement, conditionInfo);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SubTypes subTypes() {
            typeInfo.setSubTypes(true);
            return new SubTypesImpl(whereStatement, typeInfo);
        }
    }

    /** The end. */
    private final End                 end;

    /** The where by node type info. */
    private final WhereByNodeTypeInfo whereByNodeTypeInfo;

    /**
     * Instantiates a new sL where by node type impl.
     * 
     * @param end the end
     * @param whereByNodeTypeInfo the where by node type info
     */
    public WhereByNodeTypeImpl(
                                  final End end, final WhereByNodeTypeInfo whereByNodeTypeInfo) {
        this.end = end;
        this.whereByNodeTypeInfo = whereByNodeTypeInfo;
    }

    /**
     * Instantiates a new sL where by node type impl.
     * 
     * @param selectFacade the select facade
     * @param orderBy the order by
     * @param whereByNodeTypeInfo the where by node type info
     */
    public WhereByNodeTypeImpl(
                                  final SelectFacade selectFacade, final OrderByStatement orderBy,
                                  final WhereByNodeTypeInfo whereByNodeTypeInfo) {
        this(new EndImpl(selectFacade, whereByNodeTypeInfo, orderBy), whereByNodeTypeInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WhereByNodeTypeInfo getWhereStatementInfo() {
        return whereByNodeTypeInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type type(final String typeName) {
        final SLWhereTypeInfo typeInfo = new SLWhereTypeInfo(typeName);
        whereByNodeTypeInfo.getWhereTypeInfoList().add(typeInfo);
        return new TypeImpl(this, typeInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public End whereEnd() {
        return end;
    }
}
