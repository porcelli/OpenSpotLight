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
    private final SelectByNodeTypeInfo selectInfo;

    /** The types. */
    private final List<Type>           types;

    /** The select end. */
    private final End                  selectEnd;

    /**
     * Instantiates a new sL select by node type impl.
     * 
     * @param selectFacade the select facade
     */
    public SelectByNodeTypeImpl(
                                   final SelectFacade selectFacade) {
        selectInfo = new SelectByNodeTypeInfo();
        types = new ArrayList<Type>();
        selectEnd = new EndImpl(selectFacade, selectInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllTypes allTypes() {
        final AllTypesInfo allTypesInfo = selectInfo.addAllTypes();
        return new AllTypesImpl(this, allTypesInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type type(final String typeName) {
        final SLSelectTypeInfo typeInfo = selectInfo.addType(typeName);
        final Type type = new TypeImpl(this, typeInfo);
        types.add(type);
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public End end() {
        verifyIfLastItemTerminatedWithComma();
        return selectEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        for (final SLSelectTypeInfo typeInfo: selectInfo.getTypeInfoList()) {
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
        private final SelectByNodeType selectByNodeType;

        /** The all types info. */
        private final AllTypesInfo     allTypesInfo;

        /**
         * Instantiates a new all types impl.
         * 
         * @param selectByNodeType the select by node type
         * @param allTypesInfo the all types info
         */
        public AllTypesImpl(
                             final SelectByNodeType selectByNodeType, final AllTypesInfo allTypesInfo) {
            this.selectByNodeType = selectByNodeType;
            this.allTypesInfo = allTypesInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AllTypes onWhere() {
            allTypesInfo.setOnWhere(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
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
        private final SelectByNodeType selectByNodeType;

        /** The type info. */
        private final SLSelectTypeInfo typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param selectByNodeType the select by node type
         * @param typeInfo the type info
         */
        TypeImpl(
                  final SelectByNodeType selectByNodeType, final SLSelectTypeInfo typeInfo) {
            this.selectByNodeType = selectByNodeType;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectByNodeType comma() {
            typeInfo.setComma(true);
            return selectByNodeType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End selectEnd() {
            return selectByNodeType.end();
        }

        /**
         * {@inheritDoc}
         */
        @Override
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
        private final SelectFacade         selectFacade;

        /** The select info. */
        private final SelectByNodeTypeInfo selectInfo;

        /** The where. */
        private WhereByNodeType            where;

        /** The order by. */
        private OrderByStatement           orderBy;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param selectInfo the select info
         */
        EndImpl(
                 final SelectFacade selectFacade, final SelectByNodeTypeInfo selectInfo) {
            this.selectFacade = selectFacade;
            this.selectInfo = selectInfo;
            // this.orderBy = new SLOrderByStatementImpl();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WhereByNodeType where() {
            if (where == null) {
                final WhereByNodeTypeInfo whereStatementInfo = new WhereByNodeTypeInfo(selectInfo);
                selectInfo.setWhereStatementInfo(whereStatementInfo);
                where = new WhereByNodeTypeImpl(selectFacade, orderBy, whereStatementInfo);
            }
            return where;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OrderByStatement orderBy() {
            return orderBy;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End keepResult() {
            selectInfo.setKeepResult(true);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit) {
            selectInfo.setLimit(limit);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End limit(final Integer limit,
                          final Integer offset) {
            selectInfo.setLimit(limit);
            selectInfo.setOffset(offset);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes() {
            selectInfo.setXTimes(0);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End executeXTimes(final int x) {
            selectInfo.setXTimes(x);
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

}
