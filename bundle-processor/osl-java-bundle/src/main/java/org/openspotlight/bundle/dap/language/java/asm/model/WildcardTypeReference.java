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
package org.openspotlight.bundle.dap.language.java.asm.model;

/**
 * Represents a wildcard type, this type is associated to generics. A wildcard type can be bounded to another type (upper or
 * lower).
 * 
 * @author porcelli
 */
public class WildcardTypeReference implements TypeReference {

    /** The is upper bound. */
    private boolean       isUpperBound = false;

    /** The bound type. */
    private TypeReference boundType    = null;

    /** The alias. */
    private String        alias        = null;

    /**
     * Instantiates a new wildcard type reference.
     */
    public WildcardTypeReference() {
    }

    /**
     * Instantiates a new wildcard type reference.
     * 
     * @param alias the alias
     */
    public WildcardTypeReference(
                                  String alias ) {
        this.alias = alias;
    }

    /**
     * Instantiates a new wildcard type reference.
     * 
     * @param isUpperBound the is upper bound
     * @param boundType the bound type
     */
    public WildcardTypeReference(
                                  boolean isUpperBound, TypeReference boundType ) {
        this.isUpperBound = isUpperBound;
        this.boundType = boundType;
    }

    /**
     * Checks if is upper bound.
     * 
     * @return true, if is upper bound
     */
    public boolean isUpperBound() {
        return isUpperBound;
    }

    /**
     * Sets the upper bound.
     * 
     * @param isUpperBound the new upper bound
     */
    public void setUpperBound( boolean isUpperBound ) {
        this.isUpperBound = isUpperBound;
    }

    /**
     * Gets the bound type.
     * 
     * @return the bound type
     */
    public TypeReference getBoundType() {
        return boundType;
    }

    /**
     * Sets the bound type.
     * 
     * @param boundType the new bound type
     */
    public void setBoundType( TypeReference boundType ) {
        this.boundType = boundType;
    }

    /**
     * Gets the alias.
     * 
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     * 
     * @param alias the new alias
     */
    public void setAlias( String alias ) {
        this.alias = alias;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.tool.dap.language.java.asm.model.TypeReference#getFullName()
     */
    public String getFullName() {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.openspotlight.tool.dap.language.java.asm.model.TypeReference#getName()
     */
    public String getName() {
        if (boundType != null && isUpperBound) {
            return getInternalName() + " extends " + boundType.getFullName();
        } else if (boundType != null && !isUpperBound) {
            return getInternalName() + " instanceOf " + boundType.getFullName();
        }
        return getInternalName();
    }

    /**
     * Gets the internal name.
     * 
     * @return the internal name
     */
    private String getInternalName() {
        if ((alias == null) || (alias.trim().length() == 0)) {
            return "?";
        }
        return alias;
    }
}
