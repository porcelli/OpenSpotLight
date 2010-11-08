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

import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;

/**
 * The Class SLSelectByNodeTypeInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectByNodeTypeInfo extends SelectInfo {

    /**
     * The Class SLSelectTypeInfo.
     * 
     * @author Vitor Hugo Chagas
     */
    public static class SLSelectTypeInfo {

        /** The comma. */
        private boolean comma;

        /** The name. */
        private String  name;

        /** The sub types. */
        private boolean subTypes;

        /**
         * Instantiates a new sL select type info.
         * 
         * @param name the name
         */
        public SLSelectTypeInfo(
                                 final String name) {
            setName(name);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equalsTo(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            return Equals.eachEquality(SLSelectTypeInfo.class, this, obj, "name");
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return HashCodes.hashOf(name);
        }

        /**
         * Checks if is comma.
         * 
         * @return true, if is comma
         */
        public boolean isComma() {
            return comma;
        }

        /**
         * Checks if is sub types.
         * 
         * @return true, if is sub types
         */
        public boolean isSubTypes() {
            return subTypes;
        }

        /**
         * Sets the comma.
         * 
         * @param comma the new comma
         */
        public void setComma(final boolean comma) {
            this.comma = comma;
        }

        /**
         * Sets the name.
         * 
         * @param name the new name
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Sets the sub types.
         * 
         * @param subTypes the new sub types
         */
        public void setSubTypes(final boolean subTypes) {
            this.subTypes = subTypes;
        }
    }

    /** The Constant serialVersionUID. */
    private static final long            serialVersionUID = 1L;

    /** The all types info. */
    private AllTypesInfo                 allTypesInfo;

    /** The type info list. */
    private final List<SLSelectTypeInfo> typeInfoList;

    /** The where statement info. */
    private WhereByNodeTypeInfo          whereStatementInfo;

    /**
     * Instantiates a new sL select by node type info.
     */
    public SelectByNodeTypeInfo() {
        typeInfoList = new ArrayList<SLSelectTypeInfo>();
    }

    /**
     * Adds the all types.
     * 
     * @return the sL all types info
     */
    public AllTypesInfo addAllTypes() {
        if (allTypesInfo == null) {
            allTypesInfo = new AllTypesInfo();
        }
        return allTypesInfo;
    }

    /**
     * Adds the type.
     * 
     * @param name the name
     * @return the sL select type info
     */
    public SLSelectTypeInfo addType(final String name) {
        final SLSelectTypeInfo typeInfo = new SLSelectTypeInfo(name);
        typeInfoList.add(typeInfo);
        return typeInfo;
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
     * Gets the type info list.
     * 
     * @return the type info list
     */
    public List<SLSelectTypeInfo> getTypeInfoList() {
        return typeInfoList;
    }

    /**
     * Gets the where statement info.
     * 
     * @return the where statement info
     */
    public WhereByNodeTypeInfo getWhereStatementInfo() {
        return whereStatementInfo;
    }

    /**
     * Sets the where statement info.
     * 
     * @param whereStatementInfo the new where statement info
     */
    public void setWhereStatementInfo(final WhereByNodeTypeInfo whereStatementInfo) {
        this.whereStatementInfo = whereStatementInfo;
    }

    /*
     * (non-Javadoc)
     * @see org.openspotlight.graph.query.info.SLSelectInfo#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

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
            final SLSelectTypeInfo typeInfo = typeInfoList.get(i);
            if (i > 0) {
                buffer.append(",\n");
            }
            buffer.append('\t').append('"').append(typeInfo.getName());
            if (typeInfo.isSubTypes()) {
                buffer.append(".*");
            }
            buffer.append('"');
        }

        // where ...
        if (whereStatementInfo != null) {
            buffer.append(whereStatementInfo);
        }

        buffer.append(super.toString());

        return buffer.toString();
    }

}
