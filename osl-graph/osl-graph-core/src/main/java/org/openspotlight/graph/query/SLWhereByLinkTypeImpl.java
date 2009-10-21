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

import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo.SLConditionInfo;

/**
 * The Class SLWhereByLinkTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLWhereByLinkTypeImpl implements SLWhereByLinkType {

    /** The end. */
    private End                   end;

    /** The where statement info. */
    private SLWhereByLinkTypeInfo whereStatementInfo;

    /**
     * Instantiates a new sL where by link type impl.
     * 
     * @param orderBy the order by
     * @param whereStatementInfo the where statement info
     */
    public SLWhereByLinkTypeImpl(
                                  SLOrderByStatement orderBy, SLWhereByLinkTypeInfo whereStatementInfo ) {
        this(new EndImpl(whereStatementInfo, orderBy), whereStatementInfo);
    }

    /**
     * Instantiates a new sL where by link type impl.
     * 
     * @param end the end
     * @param whereStatementInfo the where statement info
     */
    public SLWhereByLinkTypeImpl(
                                  End end, SLWhereByLinkTypeInfo whereStatementInfo ) {
        this.end = end;
        this.whereStatementInfo = whereStatementInfo;
    }

    /**
     * Gets the where statement info.
     * 
     * @return the where statement info
     */
    public SLWhereByLinkTypeInfo getWhereStatementInfo() {
        return whereStatementInfo;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLWhereByLinkType#linkType(java.lang.String)
     */
    public LinkType linkType( String typeName ) {
        SLWhereLinkTypeInfo typeInfo = new SLWhereLinkTypeInfo(typeName);
        whereStatementInfo.getWhereLinkTypeInfoList().add(typeInfo);
        return new LinkTypeImpl(this, typeInfo);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLWhereByLinkType#whereEnd()
     */
    public End whereEnd() {
        return end;
    }

    /**
     * private void verifyConditionalOperator() { if (statementInfo.getConditionInfoList().isEmpty()) { throw new
     * SLInvalidQuerySyntaxRuntimeException("the first condition of a statement must not start with AND or OR operators"); } }
     */

    public static class EndImpl implements End {

        /** The where by link type info. */
        private SLWhereByLinkTypeInfo whereByLinkTypeInfo;

        /** The order by statement. */
        private SLOrderByStatement    orderByStatement;

        /**
         * Instantiates a new end impl.
         * 
         * @param whereByLinkTypeInfo the where by link type info
         * @param orderByStatement the order by statement
         */
        public EndImpl(
                        SLWhereByLinkTypeInfo whereByLinkTypeInfo, SLOrderByStatement orderByStatement ) {
            this.whereByLinkTypeInfo = whereByLinkTypeInfo;
            this.orderByStatement = orderByStatement;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#orderBy()
         */
        public SLOrderByStatement orderBy() {
            return orderByStatement;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#keepResult()
         */
        public End keepResult() {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit ) {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit,
                          Integer offset ) {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setLimit(limit);
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setOffset(offset);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#executeXTimes()
         */
        public End executeXTimes() {
            whereByLinkTypeInfo.getSelectByLinkTypeInfo().setXTimes(0);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.End#executeXTimes(int)
         */
        public End executeXTimes( int x ) {
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
        private SLWhereByLinkType   whereStatement;

        /** The type info. */
        private SLWhereLinkTypeInfo typeInfo;

        /**
         * Instantiates a new link type impl.
         * 
         * @param whereStatement the where statement
         * @param typeInfo the type info
         */
        public LinkTypeImpl(
                             SLWhereByLinkType whereStatement, SLWhereLinkTypeInfo typeInfo ) {
            this.whereStatement = whereStatement;
            this.typeInfo = typeInfo;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType#each()
         */
        public Each each() {
            SLLinkTypeStatementInfo whereStatementInfo = new SLLinkTypeStatementInfo(typeInfo);
            typeInfo.setLinkTypeStatementInfo(whereStatementInfo);
            SLConditionInfo conditionInfo = whereStatementInfo.addCondition();
            return new EachImpl(whereStatement, conditionInfo);
        }

        /**
         * The Class EachImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class EachImpl implements Each {

            /** The where statement. */
            private SLWhereByLinkType whereStatement;

            /** The condition info. */
            private SLConditionInfo   conditionInfo;

            /** The outer each. */
            private Each              outerEach;

            /**
             * Instantiates a new each impl.
             * 
             * @param whereStatement the where statement
             * @param conditionInfo the condition info
             */
            public EachImpl(
                             SLWhereByLinkType whereStatement, SLConditionInfo conditionInfo ) {
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
                             SLWhereByLinkType whereStatement, SLConditionInfo conditionInfo, Each outerEach ) {
                this.whereStatement = whereStatement;
                this.conditionInfo = conditionInfo;
                this.outerEach = outerEach;
            }

            /* (non-Javadoc)
             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each#property(java.lang.String)
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
                private Each              each;

                /** The outer each. */
                private Each              outerEach;

                /** The where statement. */
                private SLWhereByLinkType whereStatement;

                /** The condition info. */
                private SLConditionInfo   conditionInfo;

                /**
                 * Instantiates a new property impl.
                 * 
                 * @param whereStatement the where statement
                 * @param each the each
                 * @param outerEach the outer each
                 * @param conditionInfo the condition info
                 */
                public PropertyImpl(
                                     SLWhereByLinkType whereStatement, Each each, Each outerEach, SLConditionInfo conditionInfo ) {
                    this.each = each;
                    this.whereStatement = whereStatement;
                    this.conditionInfo = conditionInfo;
                    this.outerEach = outerEach;
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#not()
                 */
                public Property not() {
                    conditionInfo.setRelationalNotOperator(true);
                    return this;
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#lesserThan()
                 */
                public Operator lesserThan() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#greaterThan()
                 */
                public Operator greaterThan() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#equalsTo()
                 */
                public Operator equalsTo() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.EQUAL);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#lesserOrEqualThan()
                 */
                public Operator lesserOrEqualThan() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#greaterOrEqualThan()
                 */
                public Operator greaterOrEqualThan() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_OR_EQUAL_THAN);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#contains()
                 */
                public Operator contains() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.CONTAINS);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#startsWith()
                 */
                public Operator startsWith() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.STARTS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /* (non-Javadoc)
                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property#endsWith()
                 */
                public Operator endsWith() {
                    conditionInfo.setRelationalOperator(SLRelationalOperatorType.ENDS_WITH);
                    return new OperatorImpl(whereStatement, each, outerEach, conditionInfo);
                }

                /**
                 * The Class OperatorImpl.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static class OperatorImpl implements Operator {

                    /** The each. */
                    private Each              each;

                    /** The outer each. */
                    private Each              outerEach;

                    /** The where statement. */
                    private SLWhereByLinkType whereStatement;

                    /** The condition info. */
                    private SLConditionInfo   conditionInfo;

                    /**
                     * Instantiates a new operator impl.
                     * 
                     * @param whereStatement the where statement
                     * @param each the each
                     * @param outerEach the outer each
                     * @param conditionInfo the condition info
                     */
                    public OperatorImpl(
                                         SLWhereByLinkType whereStatement, Each each, Each outerEach,
                                         SLConditionInfo conditionInfo ) {
                        this.each = each;
                        this.outerEach = outerEach;
                        this.whereStatement = whereStatement;
                        this.conditionInfo = conditionInfo;
                    }

                    /* (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator#value(java.lang.String)
                     */
                    public Value value( String value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /* (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator#value(java.lang.Integer)
                     */
                    public Value value( Integer value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /* (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator#value(java.lang.Long)
                     */
                    public Value value( Long value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /* (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator#value(java.lang.Float)
                     */
                    public Value value( Float value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /* (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator#value(java.lang.Double)
                     */
                    public Value value( Double value ) {
                        conditionInfo.setValue(value);
                        return new ValueImpl(whereStatement, each, outerEach, conditionInfo);
                    }

                    /* (non-Javadoc)
                     * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator#value(java.lang.Boolean)
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
                        private Each              each;

                        /** The outer each. */
                        private Each              outerEach;

                        /** The where statement. */
                        private SLWhereByLinkType whereStatement;

                        /** The condition info. */
                        private SLConditionInfo   conditionInfo;

                        /**
                         * Instantiates a new value impl.
                         * 
                         * @param whereStatement the where statement
                         * @param each the each
                         * @param outerEach the outer each
                         * @param conditionInfo the condition info
                         */
                        public ValueImpl(
                                          SLWhereByLinkType whereStatement, Each each, Each outerEach,
                                          SLConditionInfo conditionInfo ) {
                            this.each = each;
                            this.outerEach = outerEach;
                            this.whereStatement = whereStatement;
                            this.conditionInfo = conditionInfo;
                        }

                        /* (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value#linkTypeEnd()
                         */
                        public SLWhereByLinkType linkTypeEnd() {
                            return whereStatement;
                        }

                        /* (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value#or()
                         */
                        public RelationalOperator or() {
                            SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(SLConditionalOperatorType.OR);
                            Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /* (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value#and()
                         */
                        public RelationalOperator and() {
                            SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                            SLConditionInfo newConditionInfo = outerStatementInfo.addCondition(SLConditionalOperatorType.AND);
                            Each newEach = new EachImpl(whereStatement, newConditionInfo, this.each);
                            return new RelationalOperatorImpl(whereStatement, newEach, newConditionInfo);
                        }

                        /* (non-Javadoc)
                         * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value#closeBracket()
                         */
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
                            private Each              each;

                            /** The where statement. */
                            private SLWhereByLinkType whereStatement;

                            /** The condition info. */
                            private SLConditionInfo   conditionInfo;

                            /**
                             * Instantiates a new relational operator impl.
                             * 
                             * @param whereStatement the where statement
                             * @param each the each
                             * @param conditionInfo the condition info
                             */
                            public RelationalOperatorImpl(
                                                           SLWhereByLinkType whereStatement, Each each,
                                                           SLConditionInfo conditionInfo ) {
                                this.each = each;
                                this.whereStatement = whereStatement;
                                this.conditionInfo = conditionInfo;
                            }

                            /* (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.RelationalOperator#comma()
                             */
                            public SLWhereByLinkType comma() {
                                return this.whereStatement;
                            }

                            /* (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.RelationalOperator#each()
                             */
                            public Each each() {
                                return this.each;
                            }

                            /* (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.RelationalOperator#openBracket()
                             */
                            public OpenBracket openBracket() {
                                SLLinkTypeStatementInfo newStatementInfo = new SLLinkTypeStatementInfo(conditionInfo.getTypeInfo());
                                conditionInfo.setInnerStatementInfo(newStatementInfo);
                                SLConditionInfo newConditionInfo = newStatementInfo.addCondition();
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

                                /* (non-Javadoc)
                                 * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.RelationalOperator.OpenBracket#each()
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
                            private SLWhereByLinkType whereStatement;

                            /** The outer each. */
                            private Each              outerEach;

                            /** The condition info. */
                            private SLConditionInfo   conditionInfo;

                            /**
                             * Instantiates a new close bracket impl.
                             * 
                             * @param whereStatement the where statement
                             * @param outerEach the outer each
                             * @param conditionInfo the condition info
                             */
                            public CloseBracketImpl(
                                                     SLWhereByLinkType whereStatement, Each outerEach,
                                                     SLConditionInfo conditionInfo ) {
                                this.whereStatement = whereStatement;
                                this.outerEach = outerEach;
                                this.conditionInfo = conditionInfo;
                            }

                            /* (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.CloseBracket#or()
                             */
                            public RelationalOperator or() {
                                SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(SLConditionalOperatorType.OR);
                                Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /* (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.CloseBracket#and()
                             */
                            public RelationalOperator and() {
                                SLLinkTypeStatementInfo outerStatementInfo = conditionInfo.getOuterStatementInfo();
                                outerStatementInfo.addCondition(SLConditionalOperatorType.AND);
                                Each each = new EachImpl(whereStatement, conditionInfo, outerEach);
                                return new RelationalOperatorImpl(whereStatement, each, conditionInfo);
                            }

                            /* (non-Javadoc)
                             * @see org.openspotlight.graph.query.SLWhereByLinkType.LinkType.Each.Property.Operator.Value.CloseBracket#typeEnd()
                             */
                            public SLWhereByLinkType typeEnd() {
                                return whereStatement;
                            }
                        }
                    }
                }
            }
        }
    }

}
