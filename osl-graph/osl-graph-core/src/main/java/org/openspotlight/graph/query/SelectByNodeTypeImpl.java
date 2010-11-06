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

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.graph.query.info.AllTypesInfo;
import org.openspotlight.graph.query.info.SelectByNodeTypeInfo;
import org.openspotlight.graph.query.info.SelectByNodeTypeInfo.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SelectInfo;
import org.openspotlight.graph.query.info.WhereByNodeTypeInfo;

/**
 * The Class SLSelectStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectByNodeTypeImpl implements SelectByNodeType, SelectInfoGetter {

    /** The select info. */
    private SelectByNodeTypeInfo selectInfo;

    /** The types. */
    private List<Type>             types;

    /** The select end. */
    private End                    selectEnd;

    /**
     * Instantiates a new sL select by node type impl.
     * 
     * @param selectFacade the select facade
     */
    public SelectByNodeTypeImpl(
                                   SelectFacade selectFacade ) {
        this.selectInfo = new SelectByNodeTypeInfo();
        this.types = new ArrayList<Type>();
        this.selectEnd = new EndImpl(selectFacade, selectInfo);
    }

    /**
     * {@inheritDoc}
     */
    public AllTypes allTypes() {
        AllTypesInfo allTypesInfo = selectInfo.addAllTypes();
        return new AllTypesImpl(this, allTypesInfo);
    }

    /**
     * {@inheritDoc}
     */
    public Type type( String typeName ) {
        SLSelectTypeInfo typeInfo = selectInfo.addType(typeName);
        Type type = new TypeImpl(this, typeInfo);
        types.add(type);
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public End end() {
        verifyIfLastItemTerminatedWithComma();
        return selectEnd;
    }

    /**
     * {@inheritDoc}
     */
    public SelectInfo getSelectInfo() {
        return selectInfo;
    }

    @Override
    public String toString() {
        return selectInfo.toString();
    }

    /**
     * Verify if last item terminated with comma.
     */
    private void verifyIfLastItemTerminatedWithComma() {
        int commaCount = 0;
        for (SLSelectTypeInfo typeInfo : selectInfo.getTypeInfoList()) {
            commaCount += typeInfo.isComma() ? 1 : 0;
        }
    }

    /**
     * The Class AllTypesImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class AllTypesImpl implements AllTypes {

        /** The select by node type. */
        private SelectByNodeType selectByNodeType;

        /** The all types info. */
        private AllTypesInfo     allTypesInfo;

        /**
         * Instantiates a new all types impl.
         * 
         * @param selectByNodeType the select by node type
         * @param allTypesInfo the all types info
         */
        public AllTypesImpl(
                             SelectByNodeType selectByNodeType, AllTypesInfo allTypesInfo ) {
            this.selectByNodeType = selectByNodeType;
            this.allTypesInfo = allTypesInfo;
        }

        /**
         * {@inheritDoc}
         */
        public AllTypes onWhere() {
            allTypesInfo.setOnWhere(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End selectEnd() {
            return selectByNodeType.end();
        }
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /** The select by node type. */
        private SelectByNodeType selectByNodeType;

        /** The type info. */
        private SLSelectTypeInfo   typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param selectByNodeType the select by node type
         * @param typeInfo the type info
         */
        TypeImpl(
                  SelectByNodeType selectByNodeType, SLSelectTypeInfo typeInfo ) {
            this.selectByNodeType = selectByNodeType;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        public SelectByNodeType comma() {
            typeInfo.setComma(true);
            return selectByNodeType;
        }

        /**
         * {@inheritDoc}
         */
        public End selectEnd() {
            return selectByNodeType.end();
        }

        /**
         * {@inheritDoc}
         */
        public Type subTypes() {
            typeInfo.setSubTypes(true);
            return this;
        }

    }

    /**
     * The Class EndImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class EndImpl implements End {

        /** The select facade. */
        private SelectFacade         selectFacade;

        /** The select info. */
        private SelectByNodeTypeInfo selectInfo;

        /** The where. */
        private WhereByNodeType      where;

        /** The order by. */
        private OrderByStatement     orderBy;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param selectInfo the select info
         */
        EndImpl(
                 SelectFacade selectFacade, SelectByNodeTypeInfo selectInfo ) {
            this.selectFacade = selectFacade;
            this.selectInfo = selectInfo;
            // this.orderBy = new SLOrderByStatementImpl();
        }

        /**
         * {@inheritDoc}
         */
        public WhereByNodeType where() {
            if (this.where == null) {
                WhereByNodeTypeInfo whereStatementInfo = new WhereByNodeTypeInfo(selectInfo);
                selectInfo.setWhereStatementInfo(whereStatementInfo);
                this.where = new WhereByNodeTypeImpl(selectFacade, orderBy, whereStatementInfo);
            }
            return where;
        }

        /**
         * {@inheritDoc}
         */
        public OrderByStatement orderBy() {
            return orderBy;
        }

        /**
         * {@inheritDoc}
         */
        public End keepResult() {
            selectInfo.setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit ) {
            selectInfo.setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End limit( Integer limit,
                          Integer offset ) {
            selectInfo.setLimit(limit);
            selectInfo.setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End executeXTimes() {
            selectInfo.setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public End executeXTimes( int x ) {
            selectInfo.setXTimes(x);
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

}
