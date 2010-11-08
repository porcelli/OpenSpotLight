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

import static org.openspotlight.graph.query.SideType.ANY_SIDE;
import static org.openspotlight.graph.query.SideType.A_SIDE;
import static org.openspotlight.graph.query.SideType.BOTH_SIDES;
import static org.openspotlight.graph.query.SideType.B_SIDE;

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.graph.query.info.SelectByLinkTypeInfo;
import org.openspotlight.graph.query.info.SelectByLinkTypeInfo.SLSelectByLinkInfo;
import org.openspotlight.graph.query.info.SelectByLinkTypeInfo.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.WhereByLinkTypeInfo;

/**
 * The Class SLSelectByLinkTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectByLinkTypeImpl implements SelectByLinkType, SelectInfoGetter {

    /** The select info. */
    private final SelectByLinkTypeInfo selectInfo;

    /** The types. */
    private final List<Type>           types;

    /** The by links. */
    private final List<ByLink>         byLinks;

    /** The select end. */
    private final End                  selectEnd;

    /**
     * Instantiates a new sL select statement impl.
     * 
     * @param selectFacade the select facade
     */
    public SelectByLinkTypeImpl(
                                   final SelectFacade selectFacade) {
        selectInfo = new SelectByLinkTypeInfo();
        types = new ArrayList<Type>();
        byLinks = new ArrayList<ByLink>();
        selectEnd = new EndImpl(selectFacade, selectInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectByLinkTypeInfo getSelectInfo() {
        return selectInfo;
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
    public ByLink byLink(final String typeName) {
        final SLSelectByLinkInfo byLinkInfo = selectInfo.addByLink(typeName);
        final ByLink byLink = new ByLinkImpl(this, byLinkInfo);
        byLinks.add(byLink);
        return byLink;
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
     * Gets the select by node type info.
     * 
     * @return the select by node type info
     */
    public SelectByLinkTypeInfo getSelectByNodeTypeInfo() {
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
        for (final SLSelectByLinkInfo byLinkInfo: selectInfo.getByLinkInfoList()) {
            commaCount += byLinkInfo.isComma() ? 1 : 0;
        }
        if (commaCount == selectInfo.getTypeInfoList().size() + selectInfo.getByLinkInfoList().size()) { throw new InvalidQuerySyntaxRuntimeException(
            "last SELECT clause item must not preceed comma."); }
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /** The select by link type. */
        private final SelectByLinkType selectByLinkType;

        /** The type info. */
        private final SLSelectTypeInfo typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param selectByNodeType the select by node type
         * @param typeInfo the type info
         */
        TypeImpl(
                  final SelectByLinkType selectByNodeType, final SLSelectTypeInfo typeInfo) {
            selectByLinkType = selectByNodeType;
            this.typeInfo = typeInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectByLinkType comma() {
            typeInfo.setComma(true);
            return selectByLinkType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End selectEnd() {
            return selectByLinkType.end();
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

        /** The select info. */
        private final SelectByLinkTypeInfo selectInfo;

        /** The where. */
        private WhereByLinkType            where;

        /** The order by. */
        private OrderByStatement           orderBy;

        /** The select facade. */
        private final SelectFacade         selectFacade;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param selectInfo the select info
         */
        EndImpl(
                 final SelectFacade selectFacade, final SelectByLinkTypeInfo selectInfo) {
            this.selectFacade = selectFacade;
            this.selectInfo = selectInfo;
            // this.orderBy = new SLOrderByStatementImpl();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WhereByLinkType where() {
            if (where == null) {
                final WhereByLinkTypeInfo whereByLinkType = new WhereByLinkTypeInfo(selectInfo);
                selectInfo.setWhereByLinkTypeInfo(whereByLinkType);
                where = new WhereByLinkTypeImpl(orderBy, whereByLinkType);
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

    /**
     * The Class ByLinkImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class ByLinkImpl implements ByLink {

        /** The select. */
        private final SelectByLinkType   select;

        /** The by link info. */
        private final SLSelectByLinkInfo byLinkInfo;

        /**
         * Instantiates a new by link impl.
         * 
         * @param select the select
         * @param byLinkInfo the by link info
         */
        public ByLinkImpl(
                           final SelectByLinkType select, final SLSelectByLinkInfo byLinkInfo) {
            this.select = select;
            this.byLinkInfo = byLinkInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ByLink a() {
            byLinkInfo.setSide(A_SIDE);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ByLink b() {
            byLinkInfo.setSide(B_SIDE);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ByLink any() {
            byLinkInfo.setSide(ANY_SIDE);
            return this;
        }

        /**
         * Both.
         * 
         * @return the by link
         */
        public ByLink both() {
            byLinkInfo.setSide(BOTH_SIDES);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectByLinkType comma() {
            byLinkInfo.setComma(true);
            return select;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End selectEnd() {
            return select.end();
        }
    }
}
