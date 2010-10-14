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
import org.openspotlight.graph.query.info.WhereStatementInfo;
import org.openspotlight.graph.query.info.WhereTypeInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo;
import org.openspotlight.graph.query.info.WhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereTypeInfo.SLTypeStatementInfo.SLTypeConditionInfo;

/**
 * The Class SLWhereStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class WhereStatementImpl implements WhereStatement {

    /** The end. */
    private End                  end;

    /** The where statement info. */
    private WhereStatementInfo whereStatementInfo;

    /**
     * Instantiates a new sL where statement impl.
     * 
     * @param selectFacade the select facade
     * @param orderBy the order by
     * @param whereStatementInfo the where statement info
     */
    public WhereStatementImpl(
                                 SelectFacade selectFacade, OrderByStatement orderBy, WhereStatementInfo whereStatementInfo ) {
        this(new EndImpl(selectFacade, whereStatementInfo, orderBy), whereStatementInfo);
    }

    /**
     * Instantiates a new sL where statement impl.
     * 
     * @param end the end
     * @param whereStatementInfo the where statement info
     */
    public WhereStatementImpl(
                                 End end, WhereStatementInfo whereStatementInfo ) {
        this.end = end;
        this.whereStatementInfo = whereStatementInfo;
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
    public Type type( String typeName ) {
        WhereTypeInfo typeInfo = new WhereTypeInfo(typeName);
        whereStatementInfo.getWhereTypeInfoList().add(typeInfo);
        return new TypeImpl(this, typeInfo);
    }

    /**
     * {@inheritDoc}
     */
    public LinkType linkType( String typeName ) {
        WhereLinkTypeInfo linkTypeInfo = new WhereLinkTypeInfo(typeName);
        whereStatementInfo.getWhereLinkTypeInfoList().add(linkTypeInfo);
        return new LinkTypeImpl(this, linkTypeInfo);
    }

    /**
     * {@inheritDoc}
     */
    public End whereEnd() {
        return end;
    }

    /**
     * The Class EndImpl.
     * 
     * @author Vitor Hugo Chagas
     */

    public static class EndImpl implements End {

        /** The where statement info. */
        private WhereStatementInfo whereStatementInfo;

        /** The order by statement. */
        private OrderByStatement   orderByStatement;

        /** The select facade. */
        private SelectFacade       selectFacade;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param whereStatementInfo the where statement info
         * @param orderByStatement the order by statement
         */
        public EndImpl(
                        SelectFacade selectFacade, WhereStatementInfo whereStatementInfo, OrderByStatement orderByStatement ) {
            this.selectFacade = selectFacade;
            this.whereStatementInfo = whereStatementInfo;
            this.orderByStatement = orderByStatement;
        }

        /**
         * {@inheritDoc}
         */
        public OrderByStatement orderBy() {
            if (orderByStatement == null) {
                SelectStatementInfo selectInfo = whereStatementInfo.getSelectStatementInfo();
                OrderByStatementInfo orderByStatementInfo = new OrderByStatementInfo(selectInfo);
                selectInfo.setOrderByStatementInfo(orderByStatementInfo);
                orderByStatement = new OrderByStatementImpl(selectFacade, orderByStatementInfo);
            }
            return orderByStatement;
        }

        /**
         * {@inheritDoc}
         */
        public End keepResult() {
            whereStatementInfo.getSelectStatementInfo().setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit ) {
            whereStatementInfo.getSelectStatementInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit,
                          Integer offset ) {
            whereStatementInfo.getSelectStatementInfo().setLimit(limit);
            whereStatementInfo.getSelectStatementInfo().setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End executeXTimes() {
            whereStatementInfo.getSelectStatementInfo().setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End executeXTimes( int x ) {
            whereStatementInfo.getSelectStatementInfo().setXTimes(x);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End collator( int strength ) {
            whereStatementInfo.getSelectStatementInfo().setCollatorStrength(strength);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public SelectByLinkType selectByLinkType() {
            return selectFacade.selectByLinkType();
        }

        /**
         * {@inheritDoc}
         */
        public SelectByNodeType selectByNodeType() {
            return selectFacade.selectByNodeType();
        }

        /**
         * {@inheritDoc}
         */
        public SelectByLinkCount selectByLinkCount() {
            return selectFacade.selectByLinkCount();
        }

        /**
         * {@inheritDoc}
         */
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
        private WhereStatement whereStatement;

        /** The type info. */
        private WhereTypeInfo  typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public TypeImpl(
                         WhereStatement whereStatement, WhereTypeInfo typeInfo ) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        public SubTypes subTypes() {
            typeInfo.setSubTypes(true);
            return new SubTypesImpl(whereStatement, typeInfo);
        }

        /**
         * {@inheritDoc}
         */
        public Each each() {
            SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo, null);
            typeInfo.setTypeStatementInfo(whereStatementInfo);
            SLTypeConditionInfo conditionInfo = whereStatementInfo.addCondition();
            return new EachImpl(whereStatement, conditionInfo);
        }

        /**
         * The Class SubTypesImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class SubTypesImpl implements SubTypes {

            /** The where statement. */
            private WhereStatement whereStatement;

            /** The type info. */
            private WhereTypeInfo  typeInfo;

            /**
             * Instantiates a new sub types impl.
             * 
             * @param whereStatement the where statement
             * @param typeInfo the type info
             */
            public SubTypesImpl(
                                 WhereStatement whereStatement, WhereTypeInfo typeInfo ) {
                this.whereStatement = whereStatement;
                this.typeInfo = typeInfo;
            }

            /**
             * {@inheritDoc}
             */
            public Each each() {
                SLTypeStatementInfo whereStatementInfo = new SLTypeStatementInfo(typeInfo, null);
                typeInfo.setTypeStatementInfo(whereStatementInfo);
                SLTypeConditionInfo conditionInfo = whereStatementInfo.addCondition();
                return new EachImpl(whereStatement, conditionInfo);
            }
        }

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each, OuterEachGetter<Each> {

            /** The where statement. */
            private WhereStatement    whereStatement;

            /** The condition info. */
            private SLTypeConditionInfo conditionInfo;

            /** The outer each. */
            private Each                outerEach;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             WhereStatement whereStatement, SLTypeConditionInfo conditionInfo ) {
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
                             WhereStatement whereStatement, SLTypeConditionInfo conditionInfo, Each outerEach ) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /**
             * {@inheritDoc}
             */
            public Each getOuterEach() {
                return outerEach;
            }

            /**
             * {@inheritDoc}
             */
            public Property property( String name ) {
                conditionInfo.setPropertyName(name);
                return new PropertyImpl(whereStatement, this, outerEach, conditionInfo);
            }

            /**
             * {@inheritDoc}
             */
            public Link link( String name ) {
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
                private WhereStatement    whereStatement;

                /** The each. */
                private Each                each;

                /** The outer each. */
                private Each                outerEach;

                /** The condition info. */
                private SLTypeConditionInfo conditionInfo;

                /**
                 * Instantiates a new link impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public LinkImpl(
                                 WhereStatement whereStatement, Each each, Each outerEach, SLTypeConditionInfo conditionInfo ) {
                    this.whereStatement = whereStatement;
                    this.each = each;
                    this.outerEach = outerEach;
                    this.conditionInfo = conditionInfo;
                }

                /**
                 * {@inheritDoc}
                 */
                public Side a() {
                    conditionInfo.setSide(SideType.A_SIDE);
                    return new SideImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
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
                    private WhereStatement    whereStatement;

                    /** The each. */
                    private Each                each;

                    /** The outer each. */
                    private Each                outerEach;

                    /** The condition info. */
                    private SLTypeConditionInfo conditionInfo;

                    /**
                     * Instantiates a new side impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public SideImpl(
                                     WhereStatement whereStatement, Each each, Each outerEach, SLTypeConditionInfo conditionInfo ) {
                        this.whereStatement = whereStatement;
                        this.each = each;
                        this.outerEach = outerEach;
                        this.conditionInfo = conditionInfo;
                    }

                    /**
                     * {@inheritDoc}
                     */
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
                        private Each                each;

                        /** The outer each. */
                        private Each                outerEach;

                        /** The where statement. */
                        private WhereStatement    whereStatement;

                        /** The condition info. */
                        private SLTypeConditionInfo conditionInfo;

                        /**
                         * Instantiates a new count impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public CountImpl(
                                          WhereStatement whereStatement, Each each, Each outerEach,
                                          SLTypeConditionInfo conditionInfo ) {
                            this.each = each;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                            this.outerEach = outerEach;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public Count not() {
                            conditionInfo.setRelationalNotOperator(true);
                            return this;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public Operator lesserThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public Operator greaterThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public Operator equalsTo() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.EQUAL);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public Operator lesserOrEqualThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public Operator greaterOrEqualThan() {
                            conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_OR_EQUAL_THAN);
                            return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                        }

                        /*
                         * (non-Javadoc)
                         * 
                         * @see
                         * org.openspotlight.graph.query.SLWhereStatement.Type
                         * .Each.Property#contains()
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
                         * 
                         * @see
                         * org.openspotlight.graph.query.SLWhereStatement.Type
                         * .Each.Property#startsWith()
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

                        /*
                         * (non-Javadoc)
                         * 
                         * @see
                         * org.openspotlight.graph.query.SLWhereStatement.Type
                         * .Each.Property#endsWith()
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
                         * The Class OperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class OperatorImpl implements Operator {

                            /** The each. */
                            private Each                each;

                            /** The outer each. */
                            private Each                outerEach;

                            /** The where statement. */
                            private WhereStatement    whereStatement;

                            /** The condition info. */
                            private SLTypeConditionInfo conditionInfo;

                            /**
                             * Instantiates a new operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public OperatorImpl(
                                                 WhereStatement whereStatement, Each each, Each outerEach,
                                                 SLTypeConditionInfo conditionInfo ) {
                                this.each = each;
                                this.outerEach = outerEach;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public Value value( Integer value ) {
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
                                private Each                each;

                                /** The outer each. */
                                private Each                outerEach;

                                /** The where statement. */
                                private WhereStatement    whereStatement;

                                /** The condition info. */
                                private SLTypeConditionInfo conditionInfo;

                                /**
                                 * Instantiates a new value impl.
                                 * 
                                 * @param whereStatement the where statement
                                 * @param each the each
                                 * @param outerEach the outer each
                                 * @param conditionInfo the condition info
                                 */
                                public ValueImpl(
                                                  WhereStatement whereStatement, Each each, Each outerEach,
                                                  SLTypeConditionInfo conditionInfo ) {
                                    this.each = each;
                                    this.outerEach = outerEach;
                                    this.whereStatement = whereStatement;
                                    this.conditionInfo = conditionInfo;
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                public WhereStatement typeEnd() {
                                    conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
                                    return whereStatement;
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                public RelationalOperator or() {
                                    SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                    SLTypeConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                    Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                                    return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                public RelationalOperator and() {
                                    SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                    SLTypeConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                    Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                                    return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                                }

                                /**
                                 * {@inheritDoc}
                                 */
                                public CloseBracket closeBracket() {
                                    SLTypeStatementInfo statementInfo = conditionInfo.getOuterStatementInfo();
                                    statementInfo.setClosed(true);
                                    return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                                }

                                /**
                                 * The Class RelationalOperatorImpl.
                                 * 
                                 * @author Vitor Hugo Chagas
                                 */
                                public static class RelationalOperatorImpl implements RelationalOperator {

                                    /** The each. */
                                    private Each                each;

                                    /** The where statement. */
                                    private WhereStatement    whereStatement;

                                    /** The condition info. */
                                    private SLTypeConditionInfo conditionInfo;

                                    /**
                                     * Instantiates a new relational operator impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param each the each
                                     * @param conditionInfo the condition info
                                     */
                                    public RelationalOperatorImpl(
                                                                   WhereStatement whereStatement, Each each,
                                                                   SLTypeConditionInfo conditionInfo ) {
                                        this.each = each;
                                        this.whereStatement = whereStatement;
                                        this.conditionInfo = conditionInfo;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public RelationalOperator not() {
                                        conditionInfo.setConditionalNotOperator(true);
                                        return this;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public WhereStatement comma() {
                                        return this.whereStatement;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public Each each() {
                                        return this.each;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public OpenBracket openBracket() {
                                        SLTypeStatementInfo newStatementInfo = new SLTypeStatementInfo(
                                                                                                       conditionInfo.getTypeInfo(),
                                                                                                       conditionInfo.getOuterStatementInfo());
                                        conditionInfo.setInnerStatementInfo(newStatementInfo);
                                        SLTypeConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                        Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                        return new OpenBracketImpl(each);
                                    }

                                    /**
                                     * The Class OpenBracketImpl.
                                     * 
                                     * @author Vitor Hugo Chagas
                                     */
                                    public static class OpenBracketImpl implements OpenBracket {

                                        /** The each. */
                                        private Each each;

                                        /**
                                         * Instantiates a new open bracket impl.
                                         * 
                                         * @param each the each
                                         */
                                        public OpenBracketImpl(
                                                                Each each ) {
                                            this.each = each;
                                        }

                                        /**
                                         * {@inheritDoc}
                                         */
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
                                    private WhereStatement    whereStatement;

                                    /** The outer each. */
                                    private Each                outerEach;

                                    /** The condition info. */
                                    private SLTypeConditionInfo conditionInfo;

                                    /**
                                     * Instantiates a new close bracket impl.
                                     * 
                                     * @param whereStatement the where statement
                                     * @param outerEach the outer each
                                     * @param conditionInfo the condition info
                                     */
                                    public CloseBracketImpl(
                                                             WhereStatement whereStatement, Each outerEach,
                                                             SLTypeConditionInfo conditionInfo ) {
                                        this.whereStatement = whereStatement;
                                        this.outerEach = outerEach;
                                        this.conditionInfo = conditionInfo;
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    @SuppressWarnings( "unchecked" )
                                    public CloseBracket closeBracket() {
                                        int size = conditionInfo.getOuterStatementInfo().getConditionInfoList().size();
                                        SLTypeConditionInfo outerConditionInfo = conditionInfo.getOuterStatementInfo().getConditionInfoList().get(
                                                                                                                                                  size - 1);
                                        SLTypeStatementInfo outerStatementInfo = outerConditionInfo.getOuterStatementInfo().getOuterStatementInfo();
                                        outerStatementInfo.setClosed(true);
                                        OuterEachGetter<Each> getter = OuterEachGetter.class.cast(outerEach);
                                        return new CloseBracketImpl(whereStatement, getter.getOuterEach(), outerConditionInfo);
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public RelationalOperator or() {
                                        SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                        outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                        Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                        return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public RelationalOperator and() {
                                        SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                        outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                        Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                        return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                                    }

                                    /**
                                     * {@inheritDoc}
                                     */
                                    public WhereStatement typeEnd() {
                                        conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
                                        return whereStatement;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /**
             * The Class PropertyImpl.
             * 
             * @author Vitor Hugo Chagas
             */
            public static class PropertyImpl implements Property {

                /** The each. */
                private Each                each;

                /** The outer each. */
                private Each                outerEach;

                /** The where statement. */
                private WhereStatement    whereStatement;

                /** The condition info. */
                private SLTypeConditionInfo conditionInfo;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     WhereStatement whereStatement, Each each, Each outerEach, SLTypeConditionInfo conditionInfo ) {
                    this.each = each;
                    this.whereStatement = whereStatement;
                    this.conditionInfo = conditionInfo;
                    this.outerEach = outerEach;
                }

                /**
                 * {@inheritDoc}
                 */
                public Property not() {
                    conditionInfo.setRelationalNotOperator(true);
                    return this;
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator lesserThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator greaterThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator equalsTo() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.EQUAL);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator lesserOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator greaterOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator contains() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.CONTAINS);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator startsWith() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.STARTS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
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
                    private Each                each;

                    /** The outer each. */
                    private Each                outerEach;

                    /** The where statement. */
                    private WhereStatement    whereStatement;

                    /** The condition info. */
                    private SLTypeConditionInfo conditionInfo;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         WhereStatement whereStatement, Each each, Each outerEach,
                                         SLTypeConditionInfo conditionInfo ) {
                        this.each = each;
                        this.outerEach = outerEach;
                        this.whereStatement = whereStatement;
                        this.conditionInfo = conditionInfo;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( String value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Integer value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Long value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Float value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Double value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Boolean value ) {
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
                        private Each                each;

                        /** The outer each. */
                        private Each                outerEach;

                        /** The where statement. */
                        private WhereStatement    whereStatement;

                        /** The condition info. */
                        private SLTypeConditionInfo conditionInfo;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          WhereStatement whereStatement, Each each, Each outerEach,
                                          SLTypeConditionInfo conditionInfo ) {
                            this.each = each;
                            this.outerEach = outerEach;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public WhereStatement typeEnd() {
                            conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
                            return whereStatement;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public RelationalOperator or() {
                            SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            SLTypeConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                            Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public RelationalOperator and() {
                            SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            SLTypeConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                            Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public CloseBracket closeBracket() {
                            SLTypeStatementInfo statementInfo = conditionInfo.getOuterStatementInfo();
                            statementInfo.setClosed(true);
                            return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                        }

                        /**
                         * The Class RelationalOperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class RelationalOperatorImpl implements RelationalOperator {

                            /** The each. */
                            private Each                each;

                            /** The where statement. */
                            private WhereStatement    whereStatement;

                            /** The condition info. */
                            private SLTypeConditionInfo conditionInfo;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           WhereStatement whereStatement, Each each,
                                                           SLTypeConditionInfo conditionInfo ) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public RelationalOperator not() {
                                conditionInfo.setConditionalNotOperator(true);
                                return this;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public WhereStatement comma() {
                                return this.whereStatement;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public Each each() {
                                return this.each;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public OpenBracket openBracket() {
                                SLTypeStatementInfo newStatementInfo = new SLTypeStatementInfo(
                                                                                               conditionInfo.getTypeInfo(),
                                                                                               conditionInfo.getOuterStatementInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                SLTypeConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                return new OpenBracketImpl(each);
                            }

                            /**
                             * The Class OpenBracketImpl.
                             * 
                             * @author Vitor Hugo Chagas
                             */
                            public static class OpenBracketImpl implements OpenBracket {

                                /** The each. */
                                private Each each;

                                /**
                                 * Instantiates a new open bracket impl.
                                 * 
                                 * @param each the each
                                 */
                                public OpenBracketImpl(
                                                        Each each ) {
                                    this.each = each;
                                }

                                /**
                                 * {@inheritDoc}
                                 */
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
                            private WhereStatement    whereStatement;

                            /** The outer each. */
                            private Each                outerEach;

                            /** The condition info. */
                            private SLTypeConditionInfo conditionInfo;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     WhereStatement whereStatement, Each outerEach,
                                                     SLTypeConditionInfo conditionInfo ) {
                                this.whereStatement = whereStatement;
                                this.outerEach = outerEach;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            @SuppressWarnings( "unchecked" )
                            public CloseBracket closeBracket() {
                                int size = conditionInfo.getOuterStatementInfo().getConditionInfoList().size();
                                SLTypeConditionInfo outerConditionInfo = conditionInfo.getOuterStatementInfo().getConditionInfoList().get(
                                                                                                                                          size - 1);
                                SLTypeStatementInfo outerStatementInfo = outerConditionInfo.getOuterStatementInfo().getOuterStatementInfo();
                                outerStatementInfo.setClosed(true);
                                OuterEachGetter<Each> getter = OuterEachGetter.class.cast(outerEach);
                                return new CloseBracketImpl(whereStatement, getter.getOuterEach(), outerConditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public RelationalOperator or() {
                                SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public RelationalOperator and() {
                                SLTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public WhereStatement typeEnd() {
                                conditionInfo.getTypeInfo().getTypeStatementInfo().setClosed(true);
                                return whereStatement;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * The Class LinkTypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class LinkTypeImpl implements LinkType {

        /** The where statement. */
        private WhereStatement    whereStatement;

        /** The type info. */
        private WhereLinkTypeInfo typeInfo;

        /**
         * Instantiates a new link type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public LinkTypeImpl(
                             WhereStatement whereStatement, WhereLinkTypeInfo typeInfo ) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        public Each each() {
            SLLinkTypeStatementInfo whereStatementInfo = new SLLinkTypeStatementInfo(typeInfo);
            typeInfo.setLinkTypeStatementInfo(whereStatementInfo);
            SLLinkTypeConditionInfo conditionInfo = whereStatementInfo.addCondition();
            return new EachImpl(whereStatement, conditionInfo);
        }

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each {

            /** The where statement. */
            private WhereStatement        whereStatement;

            /** The condition info. */
            private SLLinkTypeConditionInfo conditionInfo;

            /** The outer each. */
            private Each                    outerEach;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             WhereStatement whereStatement, SLLinkTypeConditionInfo conditionInfo ) {
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
                             WhereStatement whereStatement,
                             org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo,
                             Each outerEach ) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /**
             * {@inheritDoc}
             */
            public Property property( String name ) {
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
                private Each                                                                                                   each;

                /** The outer each. */
                private Each                                                                                                   outerEach;

                /** The where statement. */
                private WhereStatement                                                                                       whereStatement;

                /** The condition info. */
                private org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     WhereStatement whereStatement,
                                     Each each,
                                     Each outerEach,
                                     org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo ) {
                    this.each = each;
                    this.whereStatement = whereStatement;
                    this.conditionInfo = conditionInfo;
                    this.outerEach = outerEach;
                }

                /**
                 * {@inheritDoc}
                 */
                public Property not() {
                    conditionInfo.setRelationalNotOperator(true);
                    return this;
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator lesserThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator greaterThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator equalsTo() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.EQUAL);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator lesserOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.LESSER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator greaterOrEqualThan() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.GREATER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator contains() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.CONTAINS);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
                 */
                public Operator startsWith() {
                    conditionInfo.setRelationalOperator(RelationalOperatorType.STARTS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * {@inheritDoc}
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
                    private Each                                                                                                   each;

                    /** The outer each. */
                    private Each                                                                                                   outerEach;

                    /** The where statement. */
                    private WhereStatement                                                                                       whereStatement;

                    /** The condition info. */
                    private org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         WhereStatement whereStatement,
                                         Each each,
                                         Each outerEach,
                                         org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo conditionInfo ) {
                        this.each = each;
                        this.outerEach = outerEach;
                        this.whereStatement = whereStatement;
                        this.conditionInfo = conditionInfo;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( String value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Integer value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Long value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Float value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Double value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Value value( Boolean value ) {
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
                        private Each                    each;

                        /** The outer each. */
                        private Each                    outerEach;

                        /** The where statement. */
                        private WhereStatement        whereStatement;

                        /** The condition info. */
                        private SLLinkTypeConditionInfo conditionInfo;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          WhereStatement whereStatement, Each each, Each outerEach,
                                          SLLinkTypeConditionInfo conditionInfo ) {
                            this.each = each;
                            this.outerEach = outerEach;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public WhereStatement linkTypeEnd() {
                            conditionInfo.getLinkTypeInfo().getLinkTypeStatementInfo().setClosed(true);
                            return whereStatement;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public RelationalOperator or() {
                            SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            SLLinkTypeConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                            Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public RelationalOperator and() {
                            SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            SLLinkTypeConditionInfo newConditionInfo = outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                            Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public CloseBracket closeBracket() {
                            conditionInfo.getOuterStatementInfo().setClosed(true);
                            return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                        }

                        /**
                         * The Class RelationalOperatorImpl.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static class RelationalOperatorImpl implements RelationalOperator {

                            /** The each. */
                            private Each                    each;

                            /** The where statement. */
                            private WhereStatement        whereStatement;

                            /** The condition info. */
                            private SLLinkTypeConditionInfo conditionInfo;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           WhereStatement whereStatement, Each each,
                                                           SLLinkTypeConditionInfo conditionInfo ) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public WhereStatement comma() {
                                return this.whereStatement;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public Each each() {
                                return this.each;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public OpenBracket openBracket() {
                                SLLinkTypeStatementInfo newStatementInfo = new SLLinkTypeStatementInfo(
                                                                                                       conditionInfo.getLinkTypeInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                SLLinkTypeConditionInfo newConditionInfo = newStatementInfo.addCondition();
                                Each each = new EachImpl(whereStatement, newConditionInfo, this.each);
                                return new OpenBracketImpl(each);
                            }

                            /**
                             * The Class OpenBracketImpl.
                             * 
                             * @author Vitor Hugo Chagas
                             */
                            public static class OpenBracketImpl implements OpenBracket {

                                /** The each. */
                                private Each each;

                                /**
                                 * Instantiates a new open bracket impl.
                                 * 
                                 * @param each the each
                                 */
                                public OpenBracketImpl(
                                                        Each each ) {
                                    this.each = each;
                                }

                                /**
                                 * {@inheritDoc}
                                 */
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
                            private WhereStatement        whereStatement;

                            /** The outer each. */
                            private Each                    outerEach;

                            /** The condition info. */
                            private SLLinkTypeConditionInfo conditionInfo;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     WhereStatement whereStatement, Each outerEach,
                                                     SLLinkTypeConditionInfo conditionInfo ) {
                                this.whereStatement = whereStatement;
                                this.outerEach = outerEach;
                                this.conditionInfo = conditionInfo;
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public CloseBracket closeBracket() {
                                conditionInfo.getOuterStatementInfo().setClosed(true);
                                return new CloseBracketImpl(whereStatement, outerEach, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public RelationalOperator or() {
                                SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.OR);
                                Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public RelationalOperator and() {
                                SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(ConditionalOperatorType.AND);
                                Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /**
                             * {@inheritDoc}
                             */
                            public WhereStatement linkTypeEnd() {
                                conditionInfo.getLinkTypeInfo().getLinkTypeStatementInfo().setClosed(true);
                                return whereStatement;
                            }
                        }
                    }
                }
            }
        }
    }

}

interface OuterEachGetter<E> {

    public E getOuterEach();
}
