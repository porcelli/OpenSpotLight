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
package org.openspotlight.graph.query.info;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SLSelectStatementInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectStatementInfo extends SelectInfo {

    /** The Constant serialVersionUID. */
    private static final long        serialVersionUID = 1L;

    /** The type info list. */
    private List<SelectTypeInfo>   typeInfoList;

    /** The by link info list. */
    private List<SelectByLinkInfo> byLinkInfoList;

    /** The where statement info. */
    private WhereStatementInfo     whereStatementInfo;

    /** The order by statement info. */
    private OrderByStatementInfo   orderByStatementInfo;

    /** The all types info. */
    private AllTypesInfo           allTypesInfo;

    /** The collator strength. */
    private Integer                  collatorStrength;

    /**
     * Instantiates a new sL select statement info.
     */
    public SelectStatementInfo() {
        typeInfoList = new ArrayList<SelectTypeInfo>();
        byLinkInfoList = new ArrayList<SelectByLinkInfo>();
    }

    /**
     * Gets the all types.
     * 
     * @return the all types
     */
    public AllTypesInfo getAllTypes() {
        return allTypesInfo;
    }

    /**
     * Adds the all types.
     * 
     * @return the sL all types info
     */
    public AllTypesInfo addAllTypes() {
        if (allTypesInfo == null) {
            allTypesInfo = new AllTypesInfo(this);
        }
        return allTypesInfo;
    }

    /**
     * Adds the type.
     * 
     * @param name the name
     * @return the sL select type info
     */
    public SelectTypeInfo addType( String name ) {
        SelectTypeInfo typeInfo = new SelectTypeInfo(this, name);
        typeInfoList.add(typeInfo);
        return typeInfo;
    }

    /**
     * Adds the by link.
     * 
     * @param name the name
     * @return the sL select by link info
     */
    public SelectByLinkInfo addByLink( String name ) {
        SelectByLinkInfo byLinkInfo = new SelectByLinkInfo(name);
        byLinkInfoList.add(byLinkInfo);
        return byLinkInfo;
    }

    /**
     * Gets the by link info list.
     * 
     * @return the by link info list
     */
    public List<SelectByLinkInfo> getByLinkInfoList() {
        return byLinkInfoList;
    }

    /**
     * Gets the type info list.
     * 
     * @return the type info list
     */
    public List<SelectTypeInfo> getTypeInfoList() {
        return typeInfoList;
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
     * Sets the where statement info.
     * 
     * @param whereStatementInfo the new where statement info
     */
    public void setWhereStatementInfo( WhereStatementInfo whereStatementInfo ) {
        this.whereStatementInfo = whereStatementInfo;
    }

    /**
     * Gets the collator strength.
     * 
     * @return the collator strength
     */
    public Integer getCollatorStrength() {
        return collatorStrength;
    }

    /**
     * Sets the collator strength.
     * 
     * @param collatorStrength the new collator strength
     */
    public void setCollatorStrength( Integer collatorStrength ) {
        this.collatorStrength = collatorStrength;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.query.info.SLSelectInfo#toString()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        if (allTypesInfo == null) {
            buffer.append("\nSELECT\n");
        } else {
            if (allTypesInfo.isOnWhere()) {
                buffer.append("\nSELECT **\n");
            } else {
                buffer.append("\nSELECT *\n");
            }
        }

        // types ...
        for (int i = 0; i < typeInfoList.size(); i++) {
            SelectTypeInfo typeInfo = typeInfoList.get(i);
            if (i > 0) buffer.append(",\n");
            buffer.append('\t').append('"').append(typeInfo.getName());
            if (typeInfo.isSubTypes()) buffer.append(".*");
            buffer.append('"');
        }

        // bylink ...
        for (int i = 0; i < byLinkInfoList.size(); i++) {
            if (i == 0) buffer.append("BY LINK \n");
            if (i > 0) buffer.append(",\n");
            SelectByLinkInfo byLinkTypeInfo = byLinkInfoList.get(i);
            buffer.append('\t').append(byLinkTypeInfo.toString());
        }

        // where ...
        if (whereStatementInfo != null) {
            buffer.append(whereStatementInfo);
        }

        // order by...
        if (orderByStatementInfo != null) {
            buffer.append(orderByStatementInfo);
        }

        buffer.append("USE COLLATOR LEVEL ");
        buffer.append(collatorStrength);
        buffer.append('\n');

        buffer.append(super.toString());

        return buffer.toString();
    }

    public OrderByStatementInfo getOrderByStatementInfo() {
        return orderByStatementInfo;
    }

    public void setOrderByStatementInfo( OrderByStatementInfo orderByStatementInfo ) {
        this.orderByStatementInfo = orderByStatementInfo;
    }
}
