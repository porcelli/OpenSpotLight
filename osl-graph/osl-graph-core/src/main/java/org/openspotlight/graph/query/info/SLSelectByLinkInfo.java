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

import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.graph.query.SLSideType;

/**
 * The Class SLSelectByLinkInfo.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLSelectByLinkInfo {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    private String            name;

    /** The side. */
    private SLSideType        side;

    /** The comma. */
    private boolean           comma;

    /**
     * Instantiates a new sL select by link info.
     * 
     * @param name the name
     */
    public SLSelectByLinkInfo(
                               String name ) {
        setName(name);
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
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName( String name ) {
        this.name = name;
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
     * Sets the comma.
     * 
     * @param comma the new comma
     */
    public void setComma( boolean comma ) {
        this.comma = comma;
    }

    /**
     * Gets the side.
     * 
     * @return the side
     */
    public SLSideType getSide() {
        return side;
    }

    /**
     * Sets the side.
     * 
     * @param side the new side
     */
    public void setSide( SLSideType side ) {
        this.side = side;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        return Equals.eachEquality(SLSelectByLinkInfo.class, this, obj, "name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodes.hashOf(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('"').append(name).append('"').append(' ');
        buffer.append('(').append(side).append(')');
        return buffer.toString();
    }
}
