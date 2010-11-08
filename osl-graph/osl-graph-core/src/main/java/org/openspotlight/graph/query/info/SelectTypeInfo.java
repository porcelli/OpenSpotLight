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

import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;

/**
 * The Class SLSelectTypeInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectTypeInfo {

    /** The comma. */
    private boolean             comma;

    /** The name. */
    private String              name;

    /** The select statement info. */
    private SelectStatementInfo selectStatementInfo;

    /** The sub types. */
    private boolean             subTypes;

    /**
     * Instantiates a new sL select type info.
     * 
     * @param selectStatementInfo the select statement info
     * @param name the name
     */
    public SelectTypeInfo(
                             final SelectStatementInfo selectStatementInfo, final String name) {
        this.selectStatementInfo = selectStatementInfo;
        setName(name);
    }

    /**
     * Instantiates a new sL select type info.
     * 
     * @param name the name
     */
    public SelectTypeInfo(
                             final String name) {
        setName(name);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return Equals.eachEquality(SelectTypeInfo.class, this, obj, "name");
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the select statement info.
     * 
     * @return the select statement info
     */
    public SelectStatementInfo getSelectStatementInfo() {
        return selectStatementInfo;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
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
