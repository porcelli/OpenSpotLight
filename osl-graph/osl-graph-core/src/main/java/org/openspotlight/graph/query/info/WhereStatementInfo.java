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
package org.openspotlight.graph.query.info;

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.common.util.StringBuilderUtil;

/**
 * The Class SLWhereStatementInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class WhereStatementInfo {

    /** The select statement info. */
    private SelectStatementInfo     selectStatementInfo;

    /** The where link type info list. */
    private List<WhereLinkTypeInfo> whereLinkTypeInfoList = new ArrayList<WhereLinkTypeInfo>();

    /** The where type info list. */
    private List<WhereTypeInfo>     whereTypeInfoList     = new ArrayList<WhereTypeInfo>();

    /**
     * Instantiates a new sL where statement info.
     * 
     * @param selectStatementInfo the select statement info
     */
    public WhereStatementInfo(
                                 final SelectStatementInfo selectStatementInfo) {
        this.selectStatementInfo = selectStatementInfo;
    }

    /**
     * Gets the select statement info.
     * 
     * @return the select statement info
     */
    public SelectStatementInfo getSelectStatementInfo() {
        return selectStatementInfo;
    }

    /**
     * Gets the where link type info list.
     * 
     * @return the where link type info list
     */
    public List<WhereLinkTypeInfo> getWhereLinkTypeInfoList() {
        return whereLinkTypeInfoList;
    }

    /**
     * Gets the where type info list.
     * 
     * @return the where type info list
     */
    public List<WhereTypeInfo> getWhereTypeInfoList() {
        return whereTypeInfoList;
    }

    /**
     * Sets the select statement.
     * 
     * @param selectStatementInfo the new select statement
     */
    public void setSelectStatement(final SelectStatementInfo selectStatementInfo) {
        this.selectStatementInfo = selectStatementInfo;
    }

    /**
     * Sets the where link type info list.
     * 
     * @param whereLinkTypeInfoList the new where link type info list
     */
    public void setWhereLinkTypeInfoList(final List<WhereLinkTypeInfo> whereLinkTypeInfoList) {
        this.whereLinkTypeInfoList = whereLinkTypeInfoList;
    }

    /**
     * Sets the where type info list.
     * 
     * @param whereTypeInfoList the new where type info list
     */
    public void setWhereTypeInfoList(final List<WhereTypeInfo> whereTypeInfoList) {
        this.whereTypeInfoList = whereTypeInfoList;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("\nWHERE\n");
        for (final WhereTypeInfo typeInfo: whereTypeInfoList) {
            StringBuilderUtil.append(buffer, typeInfo.getTypeStatementInfo());
        }
        for (final WhereLinkTypeInfo typeInfo: whereLinkTypeInfoList) {
            StringBuilderUtil.append(buffer, typeInfo.getLinkTypeStatementInfo());
        }
        return buffer.toString();
    }
}
