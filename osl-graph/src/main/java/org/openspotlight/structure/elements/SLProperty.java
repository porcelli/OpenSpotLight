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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Representation of a property consisting of a name and value.
 * 
 * @author Vinicius Carvalho
 * @param <V>
 */
public class SLProperty<V extends Serializable> implements Serializable {
    private V value;
    private String name;
    private static final long serialVersionUID = -9008567228410375396L;

    public SLProperty() {
    }

    public SLProperty(
                       String name, V value ) {
        this.value = value;
        this.name = name;
    }

    /**
     * Getter of value
     * 
     * @return value
     */
    @XmlTransient
    public V getValue() {
        return value;
    }

    /**
     * Setter of value
     * 
     * @param value
     */
    public void setValue( V value ) {
        this.value = value;
    }

    /**
     * Getter of name
     * 
     * @return name
     */
    @XmlAttribute( name = "Name" )
    public String getName() {
        return name;
    }

    /**
     * Setter of property name
     * 
     * @param name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Getter of value in string format
     * 
     * @return value in string
     */
    @XmlAttribute( name = "Value" )
    public String getStringValue() {
        return this.value.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SLProperty other = (SLProperty)obj;
        return other.getName().equals(this.getName());
    }
}
