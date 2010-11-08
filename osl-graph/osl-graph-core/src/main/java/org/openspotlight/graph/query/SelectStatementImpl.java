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

import org.openspotlight.graph.query.info.AllTypesInfo;
import org.openspotlight.graph.query.info.OrderByStatementInfo;
import org.openspotlight.graph.query.info.SelectByLinkInfo;
import org.openspotlight.graph.query.info.SelectInfo;
import org.openspotlight.graph.query.info.SelectStatementInfo;
import org.openspotlight.graph.query.info.SelectTypeInfo;
import org.openspotlight.graph.query.info.WhereStatementInfo;

/**
 * The Class SLSelectStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectStatementImpl implements SelectStatement, SelectInfoGetter, SelectStatementInfoGetter {

    /**
     * The Class AllTypesImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class AllTypesImpl implements AllTypes {

        /** The all types info. */
        private final AllTypesInfo    allTypesInfo;

        /** The by links. */
        private final List<ByLink>    byLinks;

        /** The select statement. */
        private final SelectStatement selectStatement;

        /**
         * Instantiates a new all types impl.
         * 
         * @param selectStatement the select statement
         * @param allTypesInfo the all types info
         * @param byLinks the by links
         */
        public AllTypesImpl(
                             final SelectStatement selectStatement, final AllTypesInfo allTypesInfo, final List<ByLink> byLinks) {
            this.selectStatement = selectStatement;
            this.allTypesInfo = allTypesInfo;
            this.byLinks = byLinks;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ByLink byLink(final String typeName) {
            final SelectByLinkInfo byLinkInfo = allTypesInfo.getSelectStatementInfo().addByLink(typeName);
            final ByLink byLink = new ByLinkImpl(selectStatement, byLinkInfo);
            byLinks.add(byLink);
            return byLink;
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
            return selectStatement.end();
        }
    }

    /**
     * The Class ByLinkImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class ByLinkImpl implements ByLink {

        /** The by link info. */
        private final SelectByLinkInfo byLinkInfo;

        /** The select. */
        private final SelectStatement  select;

        /**
         * Instantiates a new by link impl.
         * 
         * @param select the select
         * @param byLinkInfo the by link info
         */
        public ByLinkImpl(
                           final SelectStatement select, final SelectByLinkInfo byLinkInfo) {
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
        public ByLink any() {
            byLinkInfo.setSide(ANY_SIDE);
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
        public SelectStatement comma() {
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

    /**
     * The Class EndImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class EndImpl implements End {

        /** The order by. */
        private OrderByStatement          orderBy;

        /** The select facade. */
        private final SelectFacade        selectFacade;

        /** The select info. */
        private final SelectStatementInfo selectInfo;

        /** The where. */
        private WhereStatement            where;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param selectInfo the select info
         */
        EndImpl(
                 final SelectFacade selectFacade, final SelectStatementInfo selectInfo) {
            this.selectFacade = selectFacade;
            this.selectInfo = selectInfo;

            // this.orderBy = new SLOrderByStatementImpl();
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
        public End executeXTimes(final Integer x) {
            selectInfo.setXTimes(x);
            return this;
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
        public OrderByStatement orderBy() {
            if (orderBy == null) {
                final OrderByStatementInfo orderByStatementInfo = new OrderByStatementInfo(selectInfo);
                selectInfo.setOrderByStatementInfo(orderByStatementInfo);
                orderBy = new OrderByStatementImpl(selectFacade, orderByStatementInfo);
            }
            return orderBy;
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

        /**
         * {@inheritDoc}
         */
        @Override
        public WhereStatement where() {
            if (where == null) {
                final WhereStatementInfo whereStatementInfo = new WhereStatementInfo(selectInfo);
                selectInfo.setWhereStatementInfo(whereStatementInfo);
                where = new WhereStatementImpl(selectFacade, orderBy, whereStatementInfo);
            }
            return where;
        }
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /** The by links. */
        private final List<ByLink>    byLinks;

        /** The select statement. */
        private final SelectStatement selectStatement;

        /** The type info. */
        private final SelectTypeInfo  typeInfo;

        /**
         * Instantiates a new type impl.
         * 
         * @param selectStatement the select statement
         * @param typeInfo the type info
         */
        TypeImpl(
                  final SelectStatement selectStatement, final SelectTypeInfo typeInfo, final List<ByLink> byLinks) {
            this.selectStatement = selectStatement;
            this.typeInfo = typeInfo;
            this.byLinks = byLinks;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ByLink byLink(final String typeName) {
            final SelectByLinkInfo byLinkInfo = typeInfo.getSelectStatementInfo().addByLink(typeName);
            final ByLink byLink = new ByLinkImpl(selectStatement, byLinkInfo);
            byLinks.add(byLink);
            return byLink;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SelectStatement comma() {
            typeInfo.setComma(true);
            return selectStatement;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public End selectEnd() {
            return selectStatement.end();
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

    /** The by links. */
    private final List<ByLink>        byLinks;

    /** The select end. */
    private final End                 selectEnd;

    /** The select info. */
    private final SelectStatementInfo selectInfo;

    /** The types. */
    private final List<Type>          types;

    /**
     * Instantiates a new sL select statement impl.
     * 
     * @param selectFacade the select facade
     */
    public SelectStatementImpl(
                                  final SelectFacade selectFacade) {
        selectInfo = new SelectStatementInfo();
        types = new ArrayList<Type>();
        byLinks = new ArrayList<ByLink>();
        selectEnd = new EndImpl(selectFacade, selectInfo);
    }

    /**
     * Verify if last item terminated with comma.
     */
    private void verifyIfLastItemTerminatedWithComma() {
        int commaCount = 0;
        for (final SelectTypeInfo typeInfo: selectInfo.getTypeInfoList()) {
            commaCount += typeInfo.isComma() ? 1 : 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllTypes allTypes() {
        final AllTypesInfo allTypesInfo = selectInfo.addAllTypes();
        return new AllTypesImpl(this, allTypesInfo, byLinks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByLink byLink(final String typeName) {
        final SelectByLinkInfo byLinkInfo = selectInfo.addByLink(typeName);
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
     * {@inheritDoc}
     */
    @Override
    public SelectInfo getSelectInfo() {
        return selectInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectStatementInfo getSelectStatementInfo() {
        return selectInfo;
    }

    @Override
    public String toString() {
        return selectInfo.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type type(final String typeName) {
        final SelectTypeInfo typeInfo = selectInfo.addType(typeName);
        final Type type = new TypeImpl(this, typeInfo, byLinks);
        types.add(type);
        return type;
    }
}
