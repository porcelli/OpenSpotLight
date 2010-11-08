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

import org.openspotlight.graph.query.info.WhereByLinkTypeInfo;
import org.openspotlight.graph.query.info.WhereByLinkTypeInfo.SLWhereLinkTypeInfo;
import org.openspotlight.graph.query.info.WhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo.SLConditionInfo;

/**
 * The Class SLWhereByLinkTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class WhereByLinkTypeImpl implements WhereByLinkType {

    /** The end. */
    private final End                 end;

    /** The where statement info. */
    private final WhereByLinkTypeInfo whereStatementInfo;

    /**
     * Instantiates a new sL where by link type impl.
     * 
     * @param orderBy the order by
     * @param whereStatementInfo the where statement info
     */
    public WhereByLinkTypeImpl(
                                  final OrderByStatement orderBy, final WhereByLinkTypeInfo whereStatementInfo) {
        this(new EndImpl(whereStatementInfo, orderBy), whereStatementInfo);
    }

    /**
     * Instantiates a new sL where by link type impl.
     * 
     * @param end the end
     * @param whereStatementInfo the where statement info
     */
    public WhereByLinkTypeImpl(
                                  final End end, final WhereByLinkTypeInfo whereStatementInfo) {
        this.end = end;
        this.whereStatementInfo = whereStatementInfo;
    }

    /**
     * Gets the where statement info.
     * 
     * @return the where statement info
     */
    public WhereByLinkTypeInfo getWhereStatementInfo() {
        return whereStatementInfo;
    }

    /*
     * (non-Javadoc)
     * @see org.openspotlight.graph.query.SLWhereByLinkType#linkType(java.lang.String )
     */
    @Override
    public LinkType linkType(final String typeName) {
        final SLWhereLinkTypeInfo typeInfo = new SLWhereLinkTypeInfo(typeName);
        whereStatementInfo.getWhereLinkTypeInfoList().add(typeInfo);
        return new LinkTypeImpl(this, typeInfo);
    }

    /*
     * (non-Javadoc)
     * @see org.openspotlight.graph.query.SLWhereByLinkType#whereEnd()
     */
    @Override
    public End whereEnd() {
        return end;
    }

    /**
     * private void verifyConditionalOperator() { if (statementInfo.getConditionInfoList().isEmpty()) { throw new
     * SLInvalidQuerySyntaxRuntimeException( "the first condition of a statement must not start with AND or OR operators" ); } }
     */

    public static class EndImpl implements End {

        /** The where by link type info. */
        private final WhereByLinkTypeInfo whereByLinkTypeInfo;

        /** The order by statement. */
        private final OrderByStatement    orderByStatement;

        /**
         * Instantiates a new end impl.
         * 
         * @param whereByLinkTypeInfo the where by link type info
         * @param orderByStatement the order by statement
         */
        public EndImpl(
                        final WhereByLinkTypeInfo whereByLinkTypeInfo, final OrderByStatement orderByStatement) {
            this.whereByLinkTypeInfo = whereByLinkTypeInfo;
            this.orderByStatement = orderByStatement;
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#orderBy()
         */
        @Override
        public OrderByStatement orderBy() {
            return orderByStatement;
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#keepResult()
         */
        @Override
        public End keepResult() {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit) {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit,
                          final Integer offset) {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setLimit(limit);
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setOffset(offset);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#executeXTimes()
         */
        @Override
        public End executeXTimes() {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setXTimes(0);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#executeXTimes (int)
         */
        @Override
        public End executeXTimes(final int x) {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setXTimes(x);
            return this;
        }
    }

    /**
     * The Class LinkTypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class LinkTypeImpl implements LinkType {

        /** The where statement. */
        private final WhereByLinkType     whereStatement;

        /** The type info. */
        private final SLWhereLinkTypeInfo typeInfo;

        /**
         * Instantiates a new link type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public LinkTypeImpl(
                             final WhereByLinkType whereStatement, final SLWhereLinkTypeInfo typeInfo) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType#each()
         */
        @Override
        public Each each() {
            final SLLinkTypeStatementInfo whereStatementInfo = new SLLinkTypeStatementInfo(typeInfo);
            typeInfo.setLinkTypeStatementInfo(whereStatementInfo);
            final SLConditionInfo conditionInfo = whereStatementInfo.addCondition();
            return new EachImpl(whereStatement, conditionInfo);
        }

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each {

            /** The where statement. */
            private final WhereByLinkType whereStatement;

            /** The condition info. */
            private final SLConditionInfo conditionInfo;

            /** The outer each. */
            private final Each            outerEach;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             final WhereByLinkType whereStatement, final SLConditionInfo conditionInfo) {
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
                             final WhereByLinkType whereStatement, final SLConditionInfo conditionInfo, final Each outerEach) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /*
             * (non-Javadoc)
             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each #property(java.lang.String)
             */
            @Override
            public Property property(final String name) {
                conditionInfo.setPropertyName(name);
                return new PropertyImpl(whereStatement, this, outerEach, conditionInfo);
            }

            /**
             * The Class PropertyImpl.
             * 
             * @author Vitor Hugo Chagas
             */
            public static class PropertyImpl implements Property {

                /** The each. */
                private final Each            each;

                /** The outer each. */
                private final Each            outerEach;

                /** The where statement. */
                private final WhereByLinkType whereStatement;

                /** The condition info. */
                private final SLConditionInfo conditionInfo;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     final WhereByLinkType whereStatement, final Each each, final Each outerEach,
                                    final SLConditionInfo conditionInfo) {
                    this.each = each;
                    this.whereStatement = whereStatement;
                    this.conditionInfo = conditionInfo;
                    this.outerEach = outerEach;
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#not()
                 */
                @Override
                public Property not() {
                    conditionInfo.setRelationalNotOperator(true);
                    return this;
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#lesserThan()
                 */
                @Override
                public Operator lesserThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#greaterThan()
                 */
                @Override
                public Operator greaterThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#equalsTo()
                 */
                @Override
                public Operator equalsTo() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.EQUAL);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#lesserOrEqualThan()
                 */
                @Override
                public Operator lesserOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#greaterOrEqualThan()
                 */
                @Override
                public Operator greaterOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#contains()
                 */
                @Override
                public Operator contains() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.CONTAINS);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#startsWith()
                 */
                @Override
                public Operator startsWith() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.STARTS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /*
                 * (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType. Each.Property#endsWith()
                 */
                @Override
                public Operator endsWith() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.ENDS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * The Class OperatorImpl.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static class OperatorImpl implements Operator {

                    /** The each. */
                    private final Each            each;

                    /** The outer each. */
                    private final Each            outerEach;

                    /** The where statement. */
                    private final WhereByLinkType whereStatement;

                    /** The condition info. */
                    private final SLConditionInfo conditionInfo;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         final WhereByLinkType whereStatement, final Each each, final Each outerEach,
                                         final SLConditionInfo conditionInfo) {
                        this.each = each;
                        this.outerEach = outerEach;
                        this.whereStatement = whereStatement;
                        this.conditionInfo = conditionInfo;
                    }

                    /*
                     * (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                     * .Each.Property.Operator#value(java.lang.String)
                     */
                    @Override
                    public Value value(final String value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /*
                     * (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                     * .Each.Property.Operator#value(java.lang.Integer)
                     */
                    @Override
                    public Value value(final Integer value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /*
                     * (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType .Each.Property.Operator#value(java.lang.Long)
                     */
                    @Override
                    public Value value(final Long value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /*
                     * (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                     * .Each.Property.Operator#value(java.lang.Float)
                     */
                    @Override
                    public Value value(final Float value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /*
                     * (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                     * .Each.Property.Operator#value(java.lang.Double)
                     */
                    @Override
                    public Value value(final Double value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /*
                     * (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                     * .Each.Property.Operator#value(java.lang.Boolean)
                     */
                    @Override
                    public Value value(final Boolean value) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * The Class ValueImpl.
                     * 
                     * @author Vitor Hugo Chagas
                     */
                    public static class ValueImpl implements Value {

                        /** The each. */
                        private final Each            each;

                        /** The outer each. */
                        private final Each            outerEach;

                        /** The where statement. */
                        private final WhereByLinkType whereStatement;

                        /** The condition info. */
                        private final SLConditionInfo conditionInfo;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          final WhereByLinkType whereStatement, final Each each, final Each outerEach,
                                          final SLConditionInfo conditionInfo) {
                            this.each = each;
                            this.outerEach = outerEach;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                         * .Each.Property.Operator.Value#linkTypeEnd()
                         */
                        @Override
                        public WhereByLinkType linkTypeEnd() {
                            return whereStatement;
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType .Each.Property.Operator.Value#or()
                         */
                        @Override
                        public RelationalOperator or() {
                            final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType .Each.Property.Operator.Value#and()
                         */
                        @Override
                        public RelationalOperator and() {
                            final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            final SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                            final Each newEach = new EachImpl(whereStatement, newConditionInfo, each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /*
                         * (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType
                         * .Each.Property.Operator.Value#closeBracket()
                         */
                        @Override
                        public CloseBracket closeBracket() {
                            return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                        }

                        /**
                         * The Class RelationalOperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class RelationalOperatorImpl implements RelationalOperator {

                            /** The each. */
                            private final Each            each;

                            /** The where statement. */
                            private final WhereByLinkType whereStatement;

                            /** The condition info. */
                            private final SLConditionInfo conditionInfo;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           final WhereByLinkType whereStatement, final Each each,
                                                           final SLConditionInfo conditionInfo) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /*
                             * (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType
                             * .Each.Property.Operator.Value.RelationalOperator #comma()
                             */
                            @Override
                            public WhereByLinkType comma() {
                                return whereStatement;
                            }

                            /*
                             * (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType
                             * .Each.Property.Operator.Value.RelationalOperator #each()
                             */
                            @Override
                            public Each each() {
                                return each;
                            }

                            /*
                             * (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType
                             * .Each.Property.Operator.Value.RelationalOperator #openBracket()
                             */
                            @Override
                            public OpenBracket openBracket() {
                                final SLLinkTypeStatementInfo newStatementInfo =
                                    new SLLinkTypeStatementInfo(
                                                                                                       conditionInfo
                                                                                                           .getTypeInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                final SLConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                final Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                return new OpenBracketImpl(each);
                            }

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

                                /*
                                 * (non-Javadoc)
                                 * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType.Each.Property.Operator.Value.
                                 * RelationalOperator.OpenBracket#each()
                                 */
                                @Override
                                public Each each() {
                                    return each;
                                }
                            }
                        }

                        /**
                         * The Class CloseBracketImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class CloseBracketImpl implements CloseBracket {

                            /** The where statement. */
                            private final WhereByLinkType whereStatement;

                            /** The outer each. */
                            private final Each            outerEach;

                            /** The condition info. */
                            private final SLConditionInfo conditionInfo;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     final WhereByLinkType whereStatement, final Each outerEach,
                                                     final SLConditionInfo conditionInfo) {
                                this.whereStatement = whereStatement;
                                this.outerEach = outerEach;
                                this.conditionInfo = conditionInfo;
                            }

                            /*
                             * (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType
                             * .Each.Property.Operator.Value.CloseBracket#or()
                             */
                            @Override
                            public RelationalOperator or() {
                                final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /*
                             * (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType
                             * .Each.Property.Operator.Value.CloseBracket#and()
                             */
                            @Override
                            public RelationalOperator and() {
                                final SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                final Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /*
                             * (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType .LinkType
                             * .Each.Property.Operator.Value.CloseBracket #typeEnd()
                             */
                            @Override
                            public WhereByLinkType typeEnd() {
                                return whereStatement;
                            }
                        }
                    }
                }
            }
        }
    }

}
