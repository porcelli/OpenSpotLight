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

import org.openspotlight.graph.query.info.OrderByStatementInfo;
import org.openspotlight.graph.query.info.SelectStatementInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo;
import org.openspotlight.graph.query.info.WhereStatementInfo;
import org.openspotlight.graph.query.info.WhereTypeInfo;
import org.openspotlight.graph.query.info.WhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereTypeInfo.SLTypeStatementInfo.SLTypeConditionInfo;

interface OuterEachGetter<E> {

    public E getOuterEach();
}

/**
 * The Class SLWhereStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class WhereStatementImpl implements WhereStatement {

    /**
     * The Class EndImpl.
     * 
     * @author Vitor Hugo Chagas
     */

    public static class EndImpl implements End {

        /** The order by statement. */
        private OrderByStatement         orderByStatement;

        /** The select facade. */
        private final SelectFacade       selectFacade;

        /** The where statement info. */
        private final WhereStatementInfo whereStatementInfo;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param whereStatementInfo the where statement info
         * @param orderByStatement the order by statement
         */
        public EndImpl(
                        final SelectFacade selectFacade, final WhereStatementInfo whereStatementInfo,
                       final OrderByStatement orderByStatement) {
            this.selectFacade = selectFacade;
            this.whereStatementInfo = whereStatementInfo;
            this.orderByStatement = orderByStatement;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End collator(final int strength) {
            whereStatementInfo.getSelectStatementInfo().setCollatorStrength(strength);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes() {
            whereStatementInfo.getSelectStatementInfo().setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes(final int x) {
            whereStatementInfo.getSelectStatementInfo().setXTimes(x);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End keepResult() {
            whereStatementInfo.getSelectStatementInfo().setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit) {
            whereStatementInfo.getSelectStatementInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit,
                          final Integer offset) {
            whereStatementInfo.getSelectStatementInfo().setLimit(limit);
            whereStatementInfo.getSelectStatementInfo().setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OrderByStatement orderBy() {
            if (orderByStatement == null) {
                final SelectStatementInfo selectInfo = whereStatementInfo.getSelectStatementInfo();
                final OrderByStatementInfo orderByStatementInfo = new OrderByStatementInfo(selectInfo);
                selectInfo.setOrderByStatementInfo(orderByStatementInfo);
                orderByStatement = new OrderByStatementImpl(selectFacade, orderByStatementInfo);
            }
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
     * The Class LinkTypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class LinkTypeImpl implements LinkType {

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each {

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
                            private final SLLinkTypeConditionInfo conditionInfo;

                            /** The outer each. */
                            private final Each                    outerEach;

                            /** The where statement. */
                            private final WhereStatement          whereStatement;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     final WhereStatement whereStatement, final Each outerEach,
                                                     final SLLinkTypeConditionInfo conditionInfo) {
                                this.whereStatement = whereStatement;
                                this.outerEach = outerEach;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public RelationalOperator and() {
                                final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public CloseBracket closeBracket() {
                                conditionInfo.getOuterStatementInfo().setClosed(true);
                                return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public WhereStatement linkTypeEnd() {
                                conditionInfo.getLinkTypeInfo().getLinkTypeStatementInfo().setClosed(true);
                                return whereStatement;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public RelationalOperator or() {
                                final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
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
                            private final SLLinkTypeConditionInfo conditionInfo;

                            /** The each. */
                            private final Each                    each;

                            /** The where statement. */
                            private final WhereStatement          whereStatement;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           final WhereStatement whereStatement, final Each each,
                                                           final SLLinkTypeConditionInfo conditionInfo) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public WhereStatement comma() {
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
                            public OpenBracket openBracket() {
                                final SLLinkTypeStatementInfo newStatementInfo =
                                    new SLLinkTypeStatementInfo(
                                                                                                       conditionInfo
                                                                                                           .getLinkTypeInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                final SLLinkTypeConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                final Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                return new OpenBracketImpl(each);
                            }
                        }

                        /** The condition info. */
                        private final SLLinkTypeConditionInfo conditionInfo;

                        /** The each. */
                        private final Each                    each;

                        /** The outer each. */
                        private final Each                    outerEach;

                        /** The where statement. */
                        private final WhereStatement          whereStatement;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          final WhereStatement whereStatement, final Each each, final Each outerEach,
                                          final SLLinkTypeConditionInfo conditionInfo) {
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
                            final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLLinkTypeConditionInfo newConditionInfo =
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public CloseBracket closeBracket() {
                            conditionInfo.getOuterStatementInfo().setClosed(true);
                            return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public WhereStatement linkTypeEnd() {
                            conditionInfo.getLinkTypeInfo().getLinkTypeStatementInfo().setClosed(true);
                            return whereStatement;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public RelationalOperator or() {
                            final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLLinkTypeConditionInfo newConditionInfo =
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }
                    }

                    /** The condition info. */
                    private final org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo;

                    /** The each. */
                    private final Each                                                                                                 each;

                    /** The outer each. */
                    private final Each                                                                                                 outerEach;

                    /** The where statement. */
                    private final WhereStatement                                                                                       whereStatement;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         final WhereStatement whereStatement,
                                         final Each each,
                                         final Each outerEach,
                                         final org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo) {
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
                private final org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo;

                /** The each. */
                private final Each                                                                                                 each;

                /** The outer each. */
                private final Each                                                                                                 outerEach;

                /** The where statement. */
                private final WhereStatement                                                                                       whereStatement;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     final WhereStatement whereStatement,
                                     final Each each,
                                     final Each outerEach,
                                     final org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo) {
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
            private final SLLinkTypeConditionInfo conditionInfo;

            /** The outer each. */
            private final Each                    outerEach;

            /** The where statement. */
            private final WhereStatement          whereStatement;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             * @param outerEach the outer each
             */
            public EachImpl(
                             final WhereStatement whereStatement,
                             final org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo,
                             final Each outerEach) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             final WhereStatement whereStatement, final SLLinkTypeConditionInfo conditionInfo) {
                this(whereStatement, conditionInfo, null);
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

        /** The type info. */
        private final WhereLinkTypeInfo typeInfo;

        /** The where statement. */
        private final WhereStatement    whereStatement;

        /**
         * Instantiates a new link type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public LinkTypeImpl(
                             final WhereStatement whereStatement, final WhereLinkTypeInfo typeInfo) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Each each() {
            final SLLinkTypeStatementInfo whereStatementInfo = new SLLinkTypeStatementInfo(typeInfo);
            typeInfo.setLinkTypeStatementInfo(whereStatementInfo);
            final SLLinkTypeConditionInfo conditionInfo = whereStatementInfo.addCondition();
            return new EachImpl(whereStatement, conditionInfo);
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
        public static class EachImpl implements Each, OuterEachGetter<Each> {

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
                                    private final SLTypeConditionInfo conditionInfo;

                                    /** The outer each. */
                                    private final Each                outerEach;

                                    /** The where statement. */
                                    private final WhereStatement      whereStatement;

                                    /**
                                     * Instantiates a new close bracket impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param outerEach the outer each
                                     * @param conditionInfo the condition info
                                     */
                                    public CloseBracketImpl(
                                                             final WhereStatement whereStatement, final Each outerEach,
                                                             final SLTypeConditionInfo conditionInfo) {
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
                                    @SuppressWarnings("unchecked")
                                    public CloseBracket closeBracket() {
                                        final int size = conditionInfo.getOuterStatementInfo().getConditionInfoList().size();
                                        final SLTypeConditionInfo outerConditionInfo =
                                            conditionInfo
                                                .getOuterStatementInfo()
                                                .getConditionInfoList()
                                                .get(
                                                                                                                                                  size - 1);
                                        final SLTypeStatementInfo outerStatementInfo =
                                            outerConditionInfo.getOuterStatementInfo().getOuterStatementInfo();
                                        outerStatementInfo.setClosed(true);
                                        final OuterEachGetter<Each> getter = OuterEachGetter.class.cast(outerEach);
                                        return new CloseBracketImpl(whereStatement, getter.getOuterEach(), outerConditionInfo);
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
                                    public WhereStatement typeEnd() {
                                        conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
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
                                    private final SLTypeConditionInfo conditionInfo;

                                    /** The each. */
                                    private final Each                each;

                                    /** The where statement. */
                                    private final WhereStatement      whereStatement;

                                    /**
                                     * Instantiates a new relational operator impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param each the each
                                     * @param conditionInfo the condition info
                                     */
                                    public RelationalOperatorImpl(
                                                                   final WhereStatement whereStatement, final Each each,
                                                                   final SLTypeConditionInfo conditionInfo) {
                                        this.each = each;
                                        this.whereStatement = whereStatement;
                                        this.conditionInfo = conditionInfo;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @Override
                                    public WhereStatement comma() {
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
                                                                                                           .getTypeInfo(),
                                                                                                       conditionInfo
                                                                                                           .getOuterStatementInfo());
                                        conditionInfo.setInnerStatementInfo(newStatementInfo);
                                        final SLTypeConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                        final Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                        return new OpenBracketImpl(each);
                                    }
                                }

                                /** The condition info. */
                                private final SLTypeConditionInfo conditionInfo;

                                /** The each. */
                                private final Each                each;

                                /** The outer each. */
                                private final Each                outerEach;

                                /** The where statement. */
                                private final WhereStatement      whereStatement;

                                /**
                                 * Instantiates a new value impl.
                                 * 
                                 * @param whereStatement the where statement
                                 * @param each the each
                                 * @param outerEach the outer each
                                 * @param conditionInfo the condition info
                                 */
                                public ValueImpl(
                                                  final WhereStatement whereStatement, final Each each, final Each outerEach,
                                                  final SLTypeConditionInfo conditionInfo) {
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
                                    final SLTypeConditionInfo newConditionInfo =
                                        outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                    final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                                    return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public CloseBracket closeBracket() {
                                    final SLTypeStatementInfo statementInfo = conditionInfo.getOuterStatementInfo();
                                    statementInfo.setClosed(true);
                                    return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public RelationalOperator or() {
                                    final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                    final SLTypeConditionInfo newConditionInfo =
                                        outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                    final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                                    return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                @Override
                                public WhereStatement typeEnd() {
                                    conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
                                    return whereStatement;
                                }
                            }

                            /** The condition info. */
                            private final SLTypeConditionInfo conditionInfo;

                            /** The each. */
                            private final Each                each;

                            /** The outer each. */
                            private final Each                outerEach;

                            /** The where statement. */
                            private final WhereStatement      whereStatement;

                            /**
                             * Instantiates a new operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public OperatorImpl(
                                                 final WhereStatement whereStatement, final Each each, final Each outerEach,
                                                 final SLTypeConditionInfo conditionInfo) {
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
                        private final SLTypeConditionInfo conditionInfo;

                        /** The each. */
                        private final Each                each;

                        /** The outer each. */
                        private final Each                outerEach;

                        /** The where statement. */
                        private final WhereStatement      whereStatement;

                        /**
                         * Instantiates a new count impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public CountImpl(
                                          final WhereStatement whereStatement, final Each each, final Each outerEach,
                                          final SLTypeConditionInfo conditionInfo) {
                            this.each = each;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                            this.outerEach = outerEach;
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereStatement.Type .Each.Property#contains()
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
                         * @see org.openspotlight.graph.query.SLWhereStatement.Type .Each.Property#endsWith()
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
                         * @see org.openspotlight.graph.query.SLWhereStatement.Type .Each.Property#startsWith()
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
                    private final SLTypeConditionInfo conditionInfo;

                    /** The each. */
                    private final Each                each;

                    /** The outer each. */
                    private final Each                outerEach;

                    /** The where statement. */
                    private final WhereStatement      whereStatement;

                    /**
                     * Instantiates a new side impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public SideImpl(
                                     final WhereStatement whereStatement, final Each each, final Each outerEach,
                                    final SLTypeConditionInfo conditionInfo) {
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
                private final SLTypeConditionInfo conditionInfo;

                /** The each. */
                private final Each                each;

                /** The outer each. */
                private final Each                outerEach;

                /** The where statement. */
                private final WhereStatement      whereStatement;

                /**
                 * Instantiates a new link impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public LinkImpl(
                                 final WhereStatement whereStatement, final Each each, final Each outerEach,
                                final SLTypeConditionInfo conditionInfo) {
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
                            private final SLTypeConditionInfo conditionInfo;

                            /** The outer each. */
                            private final Each                outerEach;

                            /** The where statement. */
                            private final WhereStatement      whereStatement;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     final WhereStatement whereStatement, final Each outerEach,
                                                     final SLTypeConditionInfo conditionInfo) {
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
                            @SuppressWarnings("unchecked")
                            public CloseBracket closeBracket() {
                                final int size = conditionInfo.getOuterStatementInfo().getConditionInfoList().size();
                                final SLTypeConditionInfo outerConditionInfo =
                                    conditionInfo
                                        .getOuterStatementInfo()
                                        .getConditionInfoList()
                                        .get(
                                                                                                                                          size - 1);
                                final SLTypeStatementInfo outerStatementInfo =
                                    outerConditionInfo.getOuterStatementInfo().getOuterStatementInfo();
                                outerStatementInfo.setClosed(true);
                                final OuterEachGetter<Each> getter = OuterEachGetter.class.cast(outerEach);
                                return new CloseBracketImpl(whereStatement, getter.getOuterEach(), outerConditionInfo);
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
                            public WhereStatement typeEnd() {
                                conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
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
                            private final SLTypeConditionInfo conditionInfo;

                            /** The each. */
                            private final Each                each;

                            /** The where statement. */
                            private final WhereStatement      whereStatement;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           final WhereStatement whereStatement, final Each each,
                                                           final SLTypeConditionInfo conditionInfo) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public WhereStatement comma() {
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
                                                                                               conditionInfo.getTypeInfo(),
                                                                                               conditionInfo
                                                                                                   .getOuterStatementInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                final SLTypeConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                final Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                return new OpenBracketImpl(each);
                            }
                        }

                        /** The condition info. */
                        private final SLTypeConditionInfo conditionInfo;

                        /** The each. */
                        private final Each                each;

                        /** The outer each. */
                        private final Each                outerEach;

                        /** The where statement. */
                        private final WhereStatement      whereStatement;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          final WhereStatement whereStatement, final Each each, final Each outerEach,
                                          final SLTypeConditionInfo conditionInfo) {
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
                            final SLTypeConditionInfo newConditionInfo =
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public CloseBracket closeBracket() {
                            final SLTypeStatementInfo statementInfo = conditionInfo.getOuterStatementInfo();
                            statementInfo.setClosed(true);
                            return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public RelationalOperator or() {
                            final SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLTypeConditionInfo newConditionInfo =
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public WhereStatement typeEnd() {
                            conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
                            return whereStatement;
                        }
                    }

                    /** The condition info. */
                    private final SLTypeConditionInfo conditionInfo;

                    /** The each. */
                    private final Each                each;

                    /** The outer each. */
                    private final Each                outerEach;

                    /** The where statement. */
                    private final WhereStatement      whereStatement;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         final WhereStatement whereStatement, final Each each, final Each outerEach,
                                         final SLTypeConditionInfo conditionInfo) {
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
                private final SLTypeConditionInfo conditionInfo;

                /** The each. */
                private final Each                each;

                /** The outer each. */
                private final Each                outerEach;

                /** The where statement. */
                private final WhereStatement      whereStatement;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     final WhereStatement whereStatement, final Each each, final Each outerEach,
                                    final SLTypeConditionInfo conditionInfo) {
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
            private final SLTypeConditionInfo conditionInfo;

            /** The outer each. */
            private final Each                outerEach;

            /** The where statement. */
            private final WhereStatement      whereStatement;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             final WhereStatement whereStatement, final SLTypeConditionInfo conditionInfo) {
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
                             final WhereStatement whereStatement, final SLTypeConditionInfo conditionInfo, final Each outerEach) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Each getOuterEach() {
                return outerEach;
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
            private final WhereTypeInfo  typeInfo;

            /** The where statement. */
            private final WhereStatement whereStatement;

            /**
             * Instantiates a new sub types impl.
             * 
             * @param whereStatement the where statement
             * @param typeInfo the type info
             */
            public SubTypesImpl(
                                 final WhereStatement whereStatement, final WhereTypeInfo typeInfo) {
                this.whereStatement = whereStatement;
                this.typeInfo = typeInfo;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Each each() {
                final SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo, null);
                typeInfo.setTypeStatementInfo(whereStatementInfo);
                final SLTypeConditionInfo conditionInfo = whereStatementInfo.addCondition();
                return new EachImpl(whereStatement, conditionInfo);
            }
        }

        /** The type info. */
        private final WhereTypeInfo  typeInfo;

        /** The where statement. */
        private final WhereStatement whereStatement;

        /**
         * Instantiates a new type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public TypeImpl(
                         final WhereStatement whereStatement, final WhereTypeInfo typeInfo) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Each each() {
            final SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo, null);
            typeInfo.setTypeStatementInfo(whereStatementInfo);
            final SLTypeConditionInfo conditionInfo = whereStatementInfo.addCondition();
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
    private final End                end;

    /** The where statement info. */
    private final WhereStatementInfo whereStatementInfo;

    /**
     * Instantiates a new sL where statement impl.
     * 
     * @param end the end
     * @param whereStatementInfo the where statement info
     */
    public WhereStatementImpl(
                                 final End end, final WhereStatementInfo whereStatementInfo) {
        this.end = end;
        this.whereStatementInfo = whereStatementInfo;
    }

    /**
     * Instantiates a new sL where statement impl.
     * 
     * @param selectFacade the select facade
     * @param orderBy the order by
     * @param whereStatementInfo the where statement info
     */
    public WhereStatementImpl(
                                 final SelectFacade selectFacade, final OrderByStatement orderBy,
                              final WhereStatementInfo whereStatementInfo) {
        this(new EndImpl(selectFacade, whereStatementInfo, orderBy), whereStatementInfo);
    }

    /**
     * Gets the where statement info.
     * 
     * @return the where statement info
     */
    public WhereStatementInfo getWhereStatementInfo() {
        return whereStatementInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkType linkType(final String typeName) {
        final WhereLinkTypeInfo linkTypeInfo = new WhereLinkTypeInfo(typeName);
        whereStatementInfo.getWhereLinkTypeInfoList().add(linkTypeInfo);
        return new LinkTypeImpl(this, linkTypeInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type type(final String typeName) {
        final WhereTypeInfo typeInfo = new WhereTypeInfo(typeName);
        whereStatementInfo.getWhereTypeInfoList().add(typeInfo);
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
