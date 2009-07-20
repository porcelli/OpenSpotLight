/*
 * Copyright (c) 2008, Alexandre Porcelli or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Alexandre Porcelli.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.openspotlight.structure.elements;

/**
 * Base class that should be used to define SpotLight LinkTypes.
 * 
 * LinkType defines a relationship between two instances of SLSimpleType.
 * 
 * @author Vinicius Carvalho
 * 
 * @see SLSimpleType
 */
public abstract class SLLinkType extends SLElement {
    private static final long serialVersionUID = 806513545381938129L;

    //@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (int)(prime * result + handle);
        return result;
    }

    //@Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SLLinkType other = (SLLinkType)obj;
        if (handle != other.handle) return false;
        return true;
    }
}
