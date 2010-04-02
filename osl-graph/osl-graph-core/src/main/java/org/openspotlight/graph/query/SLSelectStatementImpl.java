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

import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.query.info.*;

import java.util.ArrayList;
import java.util.List;

import static org.openspotlight.graph.query.SLSideType.*;

/**
 * The Class SLSelectStatementImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLSelectStatementImpl implements SLSelectStatement, SLSelectInfoGetter, SLSelectStatementInfoGetter {

    /** The select info. */
    private SLSelectStatementInfo selectInfo;

    /** The types. */
    private List<Type>            types;

    /** The by links. */
    private List<ByLink>          byLinks;

    /** The select end. */
    private End                   selectEnd;

    /**
     * Instantiates a new sL select statement impl.
     * 
     * @param selectFacade the select facade
     */
    public SLSelectStatementImpl(
                                  SLSelectFacade selectFacade ) {
        this.selectInfo = new SLSelectStatementInfo();
        this.types = new ArrayList<Type>();
        this.byLinks = new ArrayList<ByLink>();
        this.selectEnd = new EndImpl(selectFacade, selectInfo);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLSelectStatement#allTypes()
     */
    public AllTypes allTypes() {
        SLAllTypesInfo allTypesInfo = selectInfo.addAllTypes();
        return new AllTypesImpl(this, allTypesInfo, byLinks);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLSelectStatement#type(java.lang.String)
     */
    public Type type( String typeName ) {
        SLSelectTypeInfo typeInfo = selectInfo.addType(typeName);
        Type type = new TypeImpl(this, typeInfo, byLinks);
        types.add(type);
        return type;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLSelectStatement#byLink(java.lang.String)
     */
    public ByLink byLink( String typeName ) {
        SLSelectByLinkInfo byLinkInfo = selectInfo.addByLink(typeName);
        ByLink byLink = new ByLinkImpl(this, byLinkInfo);
        byLinks.add(byLink);
        return byLink;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLSelectStatement#end()
     */
    public End end() {
        verifyIfLastItemTerminatedWithComma();
        return selectEnd;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLSelectStatementInfoGetter#getSelectInfo()
     */
    /**
     * Gets the select info.
     * 
     * @return the select info
     */
    public SLSelectInfo getSelectInfo() {
        return selectInfo;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLSelectStatementInfoGetter#getSelectStatementInfo()
     */
    public SLSelectStatementInfo getSelectStatementInfo() {
        return selectInfo;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

        /** The select statement. */
        private SLSelectStatement selectStatement;

        /** The all types info. */
        private SLAllTypesInfo    allTypesInfo;

        /** The by links. */
        private List<ByLink>      byLinks;

        /**
         * Instantiates a new all types impl.
         * 
         * @param selectStatement the select statement
         * @param allTypesInfo the all types info
         * @param byLinks the by links
         */
        public AllTypesImpl(
                             SLSelectStatement selectStatement, SLAllTypesInfo allTypesInfo, List<ByLink> byLinks ) {
            this.selectStatement = selectStatement;
            this.allTypesInfo = allTypesInfo;
            this.byLinks = byLinks;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.AllTypes#onWhere()
         */
        public AllTypes onWhere() {
            allTypesInfo.setOnWhere(true);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.AllTypes#byLink(java.lang.String)
         */
        public ByLink byLink( String typeName ) {
            SLSelectByLinkInfo byLinkInfo = allTypesInfo.getSelectStatementInfo().addByLink(typeName);
            ByLink byLink = new ByLinkImpl(selectStatement, byLinkInfo);
            byLinks.add(byLink);
            return byLink;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.AllTypes#selectEnd()
         */
        public End selectEnd() {
            return selectStatement.end();
        }
    }

    /**
     * The Class TypeImpl.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class TypeImpl implements Type {

        /** The select statement. */
        private SLSelectStatement selectStatement;

        /** The type info. */
        private SLSelectTypeInfo  typeInfo;

        /** The by links. */
        private List<ByLink>      byLinks;

        /**
         * Instantiates a new type impl.
         * 
         * @param selectStatement the select statement
         * @param typeInfo the type info
         */
        TypeImpl(
                  SLSelectStatement selectStatement, SLSelectTypeInfo typeInfo, List<ByLink> byLinks ) {
            this.selectStatement = selectStatement;
            this.typeInfo = typeInfo;
            this.byLinks = byLinks;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.Type#comma()
         */
        public SLSelectStatement comma() {
            typeInfo.setComma(true);
            return selectStatement;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.Type#selectEnd()
         */
        public End selectEnd() {
            return selectStatement.end();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.Type#subTypes()
         */
        public Type subTypes() {
            typeInfo.setSubTypes(true);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.Type#byLink(java.lang.String)
         */
        public ByLink byLink( String typeName ) {
            SLSelectByLinkInfo byLinkInfo = typeInfo.getSelectStatementInfo().addByLink(typeName);
            ByLink byLink = new ByLinkImpl(selectStatement, byLinkInfo);
            byLinks.add(byLink);
            return byLink;
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

        /** The select info. */
        private SLSelectStatementInfo selectInfo;

        /** The where. */
        private SLWhereStatement      where;

        /** The order by. */
        private SLOrderByStatement    orderBy;

        /**
         * Instantiates a new end impl.
         * 
         * @param selectFacade the select facade
         * @param selectInfo the select info
         */
        EndImpl(
                 SLSelectFacade selectFacade, SLSelectStatementInfo selectInfo ) {
            this.selectFacade = selectFacade;
            this.selectInfo = selectInfo;

            //this.orderBy = new SLOrderByStatementImpl();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.End#where()
         */
        public SLWhereStatement where() {
            if (this.where == null) {
                SLWhereStatementInfo whereStatementInfo = new SLWhereStatementInfo(selectInfo);
                selectInfo.setWhereStatementInfo(whereStatementInfo);
                this.where = new SLWhereStatementImpl(selectFacade, orderBy, whereStatementInfo);
            }
            return where;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.End#orderBy()
         */
        public SLOrderByStatement orderBy() {
            if (this.orderBy == null) {
                SLOrderByStatementInfo orderByStatementInfo = new SLOrderByStatementInfo(selectInfo);
                selectInfo.setOrderByStatementInfo(orderByStatementInfo);
                this.orderBy = new SLOrderByStatementImpl(selectFacade, orderByStatementInfo);
            }
            return orderBy;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.End#keepResult()
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

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.End#executeXTimes()
         */
        public End executeXTimes() {
            selectInfo.setXTimes(0);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectStatement.End#executeXTimes(int)
         */
        public End executeXTimes( Integer x ) {
            selectInfo.setXTimes(x);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkType()
         */
        public SLSelectByLinkType selectByLinkType() throws SLGraphSessionException {
            return selectFacade.selectByLinkType();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectFacade#selectStatement()
         */
        public SLSelectByNodeType selectByNodeType() throws SLGraphSessionException {
            return selectFacade.selectByNodeType();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkCount()
         */
        public SLSelectByLinkCount selectByLinkCount() throws SLGraphSessionException {
            return selectFacade.selectByLinkCount();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectFacade#select()
         */
        public SLSelectStatement select() throws SLGraphSessionException {
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
        private SLSelectStatement  select;

        /** The by link info. */
        private SLSelectByLinkInfo byLinkInfo;

        /**
         * Instantiates a new by link impl.
         * 
         * @param select the select
         * @param byLinkInfo the by link info
         */
        public ByLinkImpl(
                           SLSelectStatement select, SLSelectByLinkInfo byLinkInfo ) {
            this.select = select;
            this.byLinkInfo = byLinkInfo;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#a()
         */
        public ByLink a() {
            byLinkInfo.setSide(A_SIDE);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#b()
         */
        public ByLink b() {
            byLinkInfo.setSide(B_SIDE);
            return this;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#any()
         */
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

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#comma()
         */
        public SLSelectStatement comma() {
            byLinkInfo.setComma(true);
            return select;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.graph.query.SLSelectByLinkType.ByLink#selectEnd()
         */
        public End selectEnd() {
            return select.end();
        }
    }

}
