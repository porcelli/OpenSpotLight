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

import org.openspotlight.graph.query.info.OrderByStatementInfo;
import org.openspotlight.graph.query.info.OrderByTypeInfo;
import org.openspotlight.graph.query.info.SelectStatementInfo;
import org.openspotlight.graph.query.info.OrderByTypeInfo.OrderType;

/**
 * The Class SLOrderByStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class OrderByStatementImpl implements SLOrderByStatement {

    /** The select facade. */
    private SLSelectFacade         selectFacade;

    /** The order by statement info. */
    private OrderByStatementInfo orderByStatementInfo;

    /**
     * Instantiates a new sL order by statement impl.
     * 
     * @param selectFacade the select facade
     * @param orderByStatementInfo the order by statement info
     */
    public OrderByStatementImpl(
                                   SLSelectFacade selectFacade, OrderByStatementInfo orderByStatementInfo ) {
        this.selectFacade = selectFacade;
        this.orderByStatementInfo = orderByStatementInfo;
    }

    /**
     * {@inheritDoc}
     */
    public Type type( String typeName ) {
        OrderByTypeInfo typeInfo = new OrderByTypeInfo();
        typeInfo.setOrderByStatementInfo(orderByStatementInfo);
        typeInfo.setTypeName(typeName);
        orderByStatementInfo.getOrderByTypeInfoList().add(typeInfo);
        return new TypeImpl(this, selectFacade, typeInfo);
    }

    /**
     * {@inheritDoc}
     */
    public End orderByEnd() {
        return new EndImpl(selectFacade, orderByStatementInfo.getSelectStatementInfo());
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /** The order by statement. */
        private SLOrderByStatement orderByStatement;

        /** The select facade. */
        private SLSelectFacade     selectFacade;

        /** The type info. */
        private OrderByTypeInfo  typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param orderByStatement the order by statement
         * @param selectFacade the select facade
         * @param typeInfo the type info
         */
        public TypeImpl(
                         SLOrderByStatement orderByStatement, SLSelectFacade selectFacade, OrderByTypeInfo typeInfo ) {
            this.orderByStatement = orderByStatement;
            this.selectFacade = selectFacade;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        public Type subTypes() {
            typeInfo.setSubTypes(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public Property property( String name ) {
            typeInfo.setPropertyName(name);
            return new PropertyImpl(orderByStatement, selectFacade, typeInfo);
        }

        /**
         * The Class PropertyImpl.
         * 
         * @author Vitor Hugo Chagas
         */
        public static class PropertyImpl implements Property {

            /** The order by statement. */
            SLOrderByStatement        orderByStatement;

            /** The select facade. */
            private SLSelectFacade    selectFacade;

            /** The type info. */
            private OrderByTypeInfo typeInfo;

            /**
             * Instantiates a new property impl.
             * 
             * @param orderByStatement the order by statement
             * @param selectFacade the select facade
             * @param typeInfo the type info
             */
            public PropertyImpl(
                                 SLOrderByStatement orderByStatement, SLSelectFacade selectFacade, OrderByTypeInfo typeInfo ) {
                this.selectFacade = selectFacade;
                this.typeInfo = typeInfo;
                this.orderByStatement = orderByStatement;
            }

            /**
             * {@inheritDoc}
             */
            public SLOrderByStatement ascending() {
                typeInfo.setOrderType(OrderType.ASCENDING);
                return orderByStatement;
            }

            /**
             * {@inheritDoc}
             */
            public SLOrderByStatement descending() {
                typeInfo.setOrderType(OrderType.DESCENDING);
                return orderByStatement;
            }

            /**
             * {@inheritDoc}
             */
            public End orderByEnd() {
                return new EndImpl(selectFacade, typeInfo.getOrderByStatementInfo().getSelectStatementInfo());
            }
        }
    }

    /**
     * The Class EndImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class EndImpl implements End {

        /** The select facade. */
        private SLSelectFacade        selectFacade;

        /** The select statement info. */
        private SelectStatementInfo selectStatementInfo;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param selectStatementInfo the select statement info
         */
        public EndImpl(
                        SLSelectFacade selectFacade, SelectStatementInfo selectStatementInfo ) {
            this.selectFacade = selectFacade;
            this.selectStatementInfo = selectStatementInfo;
        }

        /**
         * {@inheritDoc}
         */
        public End collator( int strength ) {
            selectStatementInfo.setCollatorStrength(strength);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End executeXTimes() {
            selectStatementInfo.setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End executeXTimes( int x ) {
            selectStatementInfo.setXTimes(x);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public End keepResult() {
            selectStatementInfo.setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit ) {
            selectStatementInfo.setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit,
                          Integer offset ) {
            selectStatementInfo.setLimit(limit);
            selectStatementInfo.setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public SLSelectStatement select() {
            return selectFacade.select();
        }

        /**
         * {@inheritDoc}
         */
        public SLSelectByLinkCount selectByLinkCount() {
            return selectFacade.selectByLinkCount();
        }

        /**
         * {@inheritDoc}
         */
        public SLSelectByLinkType selectByLinkType() {
            return selectFacade.selectByLinkType();
        }

        /**
         * {@inheritDoc}
         */
        public SLSelectByNodeType selectByNodeType() {
            return selectFacade.selectByNodeType();
        }
    }
}
