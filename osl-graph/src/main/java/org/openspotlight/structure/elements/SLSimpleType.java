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
import java.text.Collator;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Base class that should be used to define SpotLight ObjectTypes. This class should be extened and these extensions will define
 * the object types.
 * 
 * Object Type means to SpotLight a description (metamodel definition) of any data that will be stored in its repository.
 * 
 * @author Vinicius Carvalho
 */
@SuppressWarnings( "unchecked" )
public class SLSimpleType extends SLElement {
    protected String key;
    protected Long contextHandle;
    private int collatorLevel = Collator.IDENTICAL;
    private String caption;
    private SLSimpleType parent;
    private List<SLSimpleType> children = new LinkedList<SLSimpleType>();
    private HashMap<String, SLProperty> properties;
    private Set<SLLinkType> deleteLinkedObjects;
    private Set<SLLinkType> deleteLinks;
    private static final long serialVersionUID = 7236344563400926380L;

    public SLSimpleType(
                         String key, Long contextHandle ) {
        this(key, contextHandle, Collator.IDENTICAL);
    }

    public SLSimpleType() {
        this.properties = new HashMap<String, SLProperty>();
        this.deleteLinkedObjects = new HashSet<SLLinkType>();
        this.deleteLinks = new HashSet<SLLinkType>();
    }

    public SLSimpleType(
                         String key, Long contextHandle, int collatorLevel ) {
        this.key = key;
        this.contextHandle = contextHandle;
        this.properties = new HashMap<String, SLProperty>();
        this.deleteLinkedObjects = new HashSet<SLLinkType>();
        this.deleteLinks = new HashSet<SLLinkType>();
        setCollatorLevel(collatorLevel);
    }

    /**
     * Getter of DeleLinkedObject set
     * 
     * @return set of DeleLinkedObjects
     */
    @XmlElementWrapper( name = "XDeleteLinkedObjectList", namespace = "http://www.devexp.com.br/XOBV1" )
    @XmlElement( name = "XProperty", namespace = "http://www.devexp.com.br/XOBV1" )
    public Set<SLLinkType> getDeleteLinkedObjects() {
        return deleteLinkedObjects;
    }

    /**
     * Getter of DeleteLinks set
     * 
     * @return set of DelteLinks
     */
    @XmlElementWrapper( name = "XDeleteLinkList", namespace = "http://www.devexp.com.br/XOBV1" )
    @XmlElement( name = "XProperty", namespace = "http://www.devexp.com.br/XOBV1" )
    public Set<SLLinkType> getDeleteLinks() {
        return deleteLinks;
    }

    /**
     * Getter of Key.
     * 
     * @return key
     */
    @XmlAttribute( name = "Key" )
    public String getKey() {
        return key;
    }

    /**
     * Getter of ContextHandle
     * 
     * @return context handle
     */
    @XmlAttribute( name = "ContextHandle" )
    public Long getContextHandle() {
        return contextHandle;
    }

    /**
     * Getter of Properties
     * 
     * @return collection of properties
     */
    @XmlElementWrapper( name = "XPropertyList", namespace = "http://www.devexp.com.br/XOBV1" )
    @XmlElement( name = "XProperty", namespace = "http://www.devexp.com.br/XOBV1" )
    public Collection<SLProperty> getProperties() {
        return properties.values();
    }

    /**
     * Getter of Caption
     * 
     * @return caption
     */
    @XmlAttribute( name = "Caption" )
    public String getCaption() {
        return caption;
    }

    /**
     * Getter of Parent
     * 
     * @return parent
     */
    @XmlTransient
    public SLSimpleType getParent() {
        return parent;
    }

    /**
     * Getter of Path. Path represents all object context.
     * 
     * @return path
     */
    @XmlAttribute( name = "Path" )
    public String getPath() {
        if (handle <= 0) {
            return null;
        }
        String fullPath;
        if (parent == null) {
            fullPath = "/" + handle;
        } else {
            fullPath = parent.getPath() + "/" + handle;
        }
        return fullPath;
    }

    /**
     * Getter of Property by its name
     * 
     * @param propertyName property name
     * @return property
     * @see SLProperty
     */
    public SLProperty<Serializable> getProperty( String propertyName ) {
        return this.properties.get(propertyName);
    }

    /**
     * Getter of Children
     * 
     * @return list of children
     */
    @XmlTransient
    public List<SLSimpleType> getChildren() {
        return children;
    }

    /**
     * Getter of Collator level.
     * 
     * @return collator level
     * @see Collator
     */
    @XmlTransient
    public int getCollatorLevel() {
        return collatorLevel;
    }

    /**
     * Setter of CollatorLevel. Collator is used to compare the object key.
     * 
     * @param collatorLevel collator level
     */
    public void setCollatorLevel( int collatorLevel ) {
        if (collatorLevel > Collator.IDENTICAL && collatorLevel < Collator.PRIMARY) {
            this.collatorLevel = Collator.IDENTICAL;
        } else {
            this.collatorLevel = collatorLevel;
        }
    }

    /**
     * Setter of Parent
     * 
     * @param parent value
     */
    public void setParent( SLSimpleType parent ) {
        this.parent = parent;
    }

    /**
     * Setter of Children
     * 
     * @param children value
     */
    public void setChildren( List<SLSimpleType> children ) {
        this.children = children;
    }

    /**
     * Setter of Key. This string should represent this object uniquely in its context ({@link #setContextHandle()}
     * 
     * @param key value
     */
    public void setKey( String key ) {
        this.key = key;
    }

    /**
     * Setter of ContextHandle. ContextHandle is the handle of its context. This property enables SLSimpleType creates a Tree
     * structure.
     * 
     * @param contextHandle value
     */
    public void setContextHandle( Long contextHandle ) {
        this.contextHandle = contextHandle;
    }

    /**
     * Add a Property
     * 
     * @param property value
     */
    public void addProperty( SLProperty property ) {
        this.properties.put(property.getName(), property);
    }

    /**
     * Add a SLLinkType to DeleteLink list
     * 
     * @param link link type
     * @see SLLinkType
     */
    public void addDeleteLinks( SLLinkType link ) {
        this.deleteLinks.add(link);
    }

    /**
     * Add a SLLinkType to DeleteLinkedObject list
     * 
     * @param link link type
     * @see SLLinkType
     */
    public void addDeleteLinkedObject( SLLinkType link ) {
        this.deleteLinkedObjects.add(link);
    }

    /**
     * Setter of Caption. Caption is a string that enables easily identify the object.
     * 
     * @param caption value
     */
    public void setCaption( String caption ) {
        this.caption = caption;
    }

    /**
     * Setter of DeleteLinkedObjects. This set is composed by SLLinkTypes. DeleteLinkedObjects information is used during
     * repository feeding process. The feed process will delete all objects linked to the object (using this set). This
     * funcionallity helps to keep the repository up to date.
     * 
     * @param deleteLinkedObjects set of SLLinkType
     * @see SLLinkType
     */
    public void setDeleteLinkedObjects( Set<SLLinkType> deleteLinkedObjects ) {
        this.deleteLinkedObjects = deleteLinkedObjects;
    }

    /**
     * Setter of DeleteLinks. This set is composed by SLLinkTypes. DeleteLink information is used during repository feeding
     * process. The feed process will delete all the links of the object listed here. This funcionallity helps to keep the
     * repository up to date.
     * 
     * @param deleteLinks set of SLLinkType
     * @see SLLinkType
     */
    public void setDeleteLinks( Set<SLLinkType> deleteLinks ) {
        this.deleteLinks = deleteLinks;
    }

    //@Override
    public String toString() {
        return (this.getClass().getName() + ":" + this.getHandle() + ":" + this.getKey() + ":" + this.getContextHandle());
    }

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
        SLSimpleType other = (SLSimpleType)obj;
        if (handle != other.handle) return false;
        return true;
    }

    //@Override
    public String getLabel() {
        return null;
    }
}
