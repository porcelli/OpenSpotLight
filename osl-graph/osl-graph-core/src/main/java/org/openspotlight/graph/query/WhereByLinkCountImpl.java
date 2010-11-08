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

import org.openspotlight.graph.query.info.WhereByLinkCountInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;

/**
 * The Class SLWhereByLinkCountImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class WhereByLinkCountImpl implements WhereByLinkCount {

    /** The end. */
    private final End                  end;

    /** The where by link count info. */
    private final WhereByLinkCountInfo whereByLinkCountInfo;

    /**
     * Instantiates a new sL where by link count impl.
     * 
     * @param selectFacade the select facade
     * @param orderBy the order by
     * @param whereByLinkCountInfo the where by link count info
     */
    public WhereByLinkCountImpl(
                                   final SelectFacade selectFacade, final OrderByStatement orderBy,
                                   final WhereByLinkCountInfo whereByLinkCountInfo) {
        this(new EndImpl(selectFacade, whereByLinkCountInfo, orderBy), whereByLinkCountInfo);
    }

    /**
     * Instantiates a new sL where by link count impl.
     * 
     * @param end the end
     * @param whereByLinkCountInfo the where by link count info
     */
    public WhereByLinkCountImpl(
                                   final End end, final WhereByLinkCountInfo whereByLinkCountInfo) {
        this.end = end;
        this.whereByLinkCountInfo = whereByLinkCountInfo;
    }

    /*
     * (non-Javadoc)
     * @seeorg.openspotlight.graph.query.SLWhereByLinkCountInfoGetter# getWhereStatementInfo()
     */
    /**
     * Gets the where statement info.
     * 
     * @return the where statement info
     */
    public WhereByLinkCountInfo getWhereStatementInfo() {
        return whereByLinkCountInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type type(final String typeName) {
        final SLWhereTypeInfo typeInfo = new SLWhereTypeInfo(typeName);
        whereByLinkCountInfo.getWhereTypeInfoList().add(typeInfo);
        return new TypeImpl(this, typeInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public End whereEnd() {
        return end;
    }

    /**
     * The Class EndImpl.
     * 
     * @author Vitor Hugo Chagas
     */

    public static class EndImpl implements End {

        /** The where by link count info. */
        private final WhereByLinkCountInfo whereByLinkCountInfo;

        /** The order by statement. */
        private final OrderByStatement     orderByStatement;

        /** The select facade. */
        private final SelectFacade         selectFacade;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param whereByLinkCountInfo the where by link count info
         * @param orderByStatement the order by statement
         */
        public EndImpl(
                        final SelectFacade selectFacade, final WhereByLinkCountInfo whereByLinkCountInfo,
                        final OrderByStatement orderByStatement) {
            this.selectFacade = selectFacade;
            this.whereByLinkCountInfo = whereByLinkCountInfo;
            this.orderByStatement = orderByStatement;
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
        public End keepResult() {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit) {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit,
                          final Integer offset) {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setLimit(limit);
            whereByLinkCountInfo.getSelectByLinkCountInfo().setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes() {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes(final int x) {
            whereByLinkCountInfo.getSelectByLinkCountInfo().setXTimes(x);
            return this;
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
        public SelectStatement select() {
            return selectFacade.select();
        }
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /** The where statement. */
        private final WhereByLinkCount whereStatement;

        /** The type info. */
        private final SLWhereTypeInfo  typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public TypeImpl(
                         final WhereByLinkCount whereStatement, final SLWhereTypeInfo typeInfo) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SubTypes subTypes() {
            typeInfo.setSubTypes(true);
            return new SubTypesImpl(whereStatement, typeInfo);
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
         * The Class SubTypesImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class SubTypesImpl implements SubTypes {

            /** The where statement. */
            private final WhereByLinkCount whereStatement;

            /** The type info. */
            private final SLWhereTypeInfo  typeInfo;

            /**
             * Instantiates a new sub types impl.
             * 
             * @param whereStatement the where statement
             * @param typeInfo the type info
             */
            public SubTypesImpl(
                                 final WhereByLinkCount whereStatement, final SLWhereTypeInfo typeInfo) {
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

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each {

            /** The where statement. */
            private final WhereByLinkCount whereStatement;

            /** The condition info. */
            private final SLConditionInfo  conditionInfo;

            /** The outer each. */
            private final Each             outerEach;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             final WhereByLinkCount whereStatement, final SLConditionInfo conditionInfo) {
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
                             final WhereByLinkCount whereStatement, final SLConditionInfo conditionInfo, final Each outerEach) {
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
             * The Class LinkImpl.
             * 
             * @author Vitor Hugo Chagas
             */
            public static class LinkImpl implements Link {

                /** The where statement. */
                private final WhereByLinkCount whereStatement;

                /** The each. */
                private final Each             each;

                /** The outer each. */
                private final Each             outerEach;

                /** The condition info. */
                private final SLConditionInfo  conditionInfo;

                /**
                 * Instantiates a new link impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public LinkImpl(
                                 final WhereByLinkCount whereStatement, final Each each, final Each outerEach,
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

                /**
                 * The Class SideImpl.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static class SideImpl implements Side {

                    /** The where statement. */
                    private final WhereByLinkCount whereStatement;

                    /** The each. */
                    private final Each             each;

                    /** The outer each. */
                    private final Each             outerEach;

                    /** The condition info. */
                    private final SLConditionInfo  conditionInfo;

                    /**
                     * Instantiates a new side impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public SideImpl(
                                     final WhereByLinkCount whereStatement, final Each each, final Each outerEach,
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

                    /**
                     * The Class CountImpl.
                     * 
                     * @author Vitor Hugo Chagas
                     */
                    public static class CountImpl implements Count {

                        /** The each. */
                        private final Each             each;

                        /** The outer each. */
                        private final Each             outerEach;

                        /** The where statement. */
                        private final WhereByLinkCount whereStatement;

                        /** The condition info. */
                        private final SLConditionInfo  conditionInfo;

                        /**
                         * Instantiates a new count impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public CountImpl(
                                          final WhereByLinkCount whereStatement, final Each each, final Each outerEach,
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
                        public Count not() {
                            conditionInfo.setRelationalNotOperator(true);
                            return this;
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
                        public Operator greaterThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
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
                        public Operator lesserOrEqualThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
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
                         * Contains.
                         * 
                         * @return the operator
                         */
                        public Operator contains() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.CONTAINS);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * Starts with.
                         * 
                         * @return the operator
                         */
                        public Operator startsWith() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.STARTS_WITH);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

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
                         * The Class OperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class OperatorImpl implements Operator {

                            /** The each. */
                            private final Each             each;

                            /** The outer each. */
                            private final Each             outerEach;

                            /** The where statement. */
                            private final WhereByLinkCount whereStatement;

                            /** The condition info. */
                            private final SLConditionInfo  conditionInfo;

                            /**
                             * Instantiates a new operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public OperatorImpl(
                                                 final WhereByLinkCount whereStatement, final Each each, final Each outerEach,
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

                            /**
                             * The Class ValueImpl.
                             * 
                             * @author Vitor Hugo Chagas
                             */
                            public static class ValueImpl implements Value {

                                /** The each. */
                                private final Each             each;

                                /** The outer each. */
                                private final Each             outerEach;

                                /** The where statement. */
                                private final WhereByLinkCount whereStatement;

                                /** The condition info. */
                                private final SLConditionInfo  conditionInfo;

                                /**
                                 * Instantiates a new value impl.
                                 * 
                                 * @param whereStatement the where statement
                                 * @param each the each
                                 * @param outerEach the outer each
                                 * @param conditionInfo the condition info
                                 */
                                public ValueImpl(
                                                  final WhereByLinkCount whereStatement, final Each each, final Each outerEach,
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
                                public WhereByLinkCount typeEnd() {
                                    return whereStatement;
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
                                 * The Class RelationalOperatorImpl.
                                 * 
                                 * @author Vitor Hugo Chagas
                                 */
                                public static class RelationalOperatorImpl implements RelationalOperator {

                                    /** The each. */
                                    private final Each             each;

                                    /** The where statement. */
                                    private final WhereByLinkCount whereStatement;

                                    /** The condition info. */
                                    private final SLConditionInfo  conditionInfo;

                                    /**
                                     * Instantiates a new relational operator impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param each the each
                                     * @param conditionInfo the condition info
                                     */
                                    public RelationalOperatorImpl(
                                                                   final WhereByLinkCount whereStatement, final Each each,
                                                                   final SLConditionInfo conditionInfo) {
                                        this.each = each;
                                        this.whereStatement = whereStatement;
                                        this.conditionInfo = conditionInfo;
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
                                    public WhereByLinkCount comma() {
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
                                        final SLTypeStatementInfo newStatementInfo =
                                            new SLTypeStatementInfo(
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

                                        /**
                                         * {@inheritDoc}
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
                                    private final WhereByLinkCount whereStatement;

                                    /** The outer each. */
                                    private final Each             outerEach;

                                    /** The condition info. */
                                    private final SLConditionInfo  conditionInfo;

                                    /**
                                     * Instantiates a new close bracket impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param outerEach the outer each
                                     * @param conditionInfo the condition info
                                     */
                                    public CloseBracketImpl(
                                                             final WhereByLinkCount whereStatement, final Each outerEach,
                                                             final SLConditionInfo conditionInfo) {
                                        this.whereStatement = whereStatement;
                                        this.outerEach = outerEach;
                                        this.conditionInfo = conditionInfo;
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
                                    public WhereByLinkCount typeEnd() {
                                        return whereStatement;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
