/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA **********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.graph.query;

/**
 * The Interface SLWhereByLinkType.
 * 
 * @author Vitor Hugo Chagas
 */
public interface WhereByLinkType {

    /**
     * The Interface End.
     * 
     * @author Vitor Hugo Chagas
     */
    public static interface End {

        /**
         * Execute x times.
         * 
         * @return the end
         */
        public End executeXTimes();

        /**
         * Execute x times.
         * 
         * @param x the x
         * @return the end
         */
        public End executeXTimes(int x);

        /**
         * Keep result.
         * 
         * @return the end
         */
        public End keepResult();

        /**
         * Limit.
         * 
         * @param size the size
         * @return the end
         */
        public End limit(Integer size);

        /**
         * Limit.
         * 
         * @param size the size
         * @param offset the offset
         * @return the end
         */
        public End limit(Integer size,
                          Integer offset);

        /**
         * Order by.
         * 
         * @return the sL order by statement
         */
        public OrderByStatement orderBy();
    }

    /**
     * The Interface LinkType.
     * 
     * @author Vitor Hugo Chagas
     */
    public static interface LinkType {

        /**
         * The Interface Each.
         * 
         * @author Vitor Hugo Chagas
         */
        public static interface Each {

            /**
             * The Interface Property.
             * 
             * @author Vitor Hugo Chagas
             */
            public static interface Property {

                /**
                 * The Interface Operator.
                 * 
                 * @author Vitor Hugo Chagas
                 */
                public static interface Operator {

                    /**
                     * The Interface Value.
                     * 
                     * @author Vitor Hugo Chagas
                     */
                    public static interface Value {

                        /**
                         * The Interface CloseBracket.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static interface CloseBracket {

                            /**
                             * And.
                             * 
                             * @return the relational operator
                             */
                            public RelationalOperator and();

                            /**
                             * Or.
                             * 
                             * @return the relational operator
                             */
                            public RelationalOperator or();

                            /**
                             * Type end.
                             * 
                             * @return the sL where by link type
                             */
                            public WhereByLinkType typeEnd();
                        }

                        /**
                         * The Interface OpenBracket.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static interface OpenBracket {

                            /**
                             * Close bracket.
                             * 
                             * @return the close bracket
                             */
                            public CloseBracket closeBracket();

                            /**
                             * Each.
                             * 
                             * @return the each
                             */
                            public Each each();
                        }

                        /**
                         * The Interface RelationalOperator.
                         * 
                         * @author Vitor Hugo Chagas
                         */
                        public static interface RelationalOperator {

                            /**
                             * The Interface OpenBracket.
                             * 
                             * @author Vitor Hugo Chagas
                             */
                            public static interface OpenBracket {

                                /**
                                 * Each.
                                 * 
                                 * @return the each
                                 */
                                public Each each();
                            }

                            /**
                             * Comma.
                             * 
                             * @return the sL where by link type
                             */
                            public WhereByLinkType comma();

                            /**
                             * Each.
                             * 
                             * @return the each
                             */
                            public Each each();

                            /**
                             * Open bracket.
                             * 
                             * @return the open bracket
                             */
                            public OpenBracket openBracket();
                        }

                        /**
                         * And.
                         * 
                         * @return the relational operator
                         */
                        public RelationalOperator and();

                        /**
                         * Close bracket.
                         * 
                         * @return the close bracket
                         */
                        public CloseBracket closeBracket();

                        /**
                         * Link type end.
                         * 
                         * @return the sL where by link type
                         */
                        public WhereByLinkType linkTypeEnd();

                        /**
                         * Or.
                         * 
                         * @return the relational operator
                         */
                        public RelationalOperator or();
                    }

                    /**
                     * Value.
                     * 
                     * @param value the value
                     * @return the value
                     */
                    public Value value(Boolean value);

                    /**
                     * Value.
                     * 
                     * @param value the value
                     * @return the value
                     */
                    public Value value(Double value);

                    /**
                     * Value.
                     * 
                     * @param value the value
                     * @return the value
                     */
                    public Value value(Float value);

                    /**
                     * Value.
                     * 
                     * @param value the value
                     * @return the value
                     */
                    public Value value(Integer value);

                    /**
                     * Value.
                     * 
                     * @param value the value
                     * @return the value
                     */
                    public Value value(Long value);

                    /**
                     * Value.
                     * 
                     * @param value the value
                     * @return the value
                     */
                    public Value value(String value);
                }

                /**
                 * Contains.
                 * 
                 * @return the operator
                 */
                public Operator contains();

                /**
                 * Ends with.
                 * 
                 * @return the operator
                 */
                public Operator endsWith();

                /**
                 * Equals to.
                 * 
                 * @return the operator
                 */
                public Operator equalsTo();

                /**
                 * Greater or equal than.
                 * 
                 * @return the operator
                 */
                public Operator greaterOrEqualThan();

                /**
                 * Greater than.
                 * 
                 * @return the operator
                 */
                public Operator greaterThan();

                /**
                 * Lesser or equal than.
                 * 
                 * @return the operator
                 */
                public Operator lesserOrEqualThan();

                /**
                 * Lesser than.
                 * 
                 * @return the operator
                 */
                public Operator lesserThan();

                /**
                 * Not.
                 * 
                 * @return the property
                 */
                public Property not();

                /**
                 * Starts with.
                 * 
                 * @return the operator
                 */
                public Operator startsWith();
            }

            /**
             * Property.
             * 
             * @param name the name
             * @return the property
             */
            public Property property(String name);
        }

        /**
         * Each.
         * 
         * @return the each
         */
        public Each each();
    }

    /**
     * Link type.
     * 
     * @param typeName the type name
     * @return the link type
     */
    public LinkType linkType(String typeName);

    /**
     * Where end.
     * 
     * @return the end
     */
    public End whereEnd();
}
