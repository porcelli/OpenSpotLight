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

package org.openspotlight.federation.data;

import static java.util.Arrays.asList;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Collections.createImmutableMap;
import static org.openspotlight.common.util.Collections.createImmutableSet;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to store static meta data for {@link ConfigurationNode
 * configuration nodes}. Use the {@link Factory} static methods to create this
 * metadata.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface StaticMetadata {
    
    /**
     * Static methods to create {@link StaticMetadata the static metadata } for
     * {@link ConfigurationNode configuration nodes}
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    static class Factory {
        
        /**
         * This inner class was made to store class behavior data for nodes. It
         * should be created and frozen on a static way.
         * 
         * @author Luiz Fernando Teston - feu.teston@caravelatech.com
         * 
         */
        private static class ImmutableClassMetadata implements StaticMetadata {
            
            private final Class<? extends ConfigurationNode> type;
            private final Set<Class<? extends ConfigurationNode>> parentNodeValidTypes;
            private final Set<Class<? extends ConfigurationNode>> childrenNodeValidTypes;
            private final Class<? extends Serializable> keyPropertyType;
            private final Set<Class<? extends ConfigurationNode>> singleChildrenNodeValidTypes;
            
            private final String keyProperty;
            
            private final Map<String, Class<?>> propertyTypes;
            
            private final int hashCode;
            
            /**
             * copy constructor
             * 
             * @param base
             */
            public ImmutableClassMetadata(final StaticMetadata base) {
                this.type = base.getType();
                this.keyProperty = base.getKeyProperty();
                
                this.parentNodeValidTypes = createImmutableSet(base
                        .getParentNodeValidTypes());
                this.childrenNodeValidTypes = createImmutableSet(base
                        .getChildrenValidNodeTypes());
                this.propertyTypes = createImmutableMap(base.getPropertyTypes());
                
                this.keyPropertyType = base.getKeyPropertyType();
                this.singleChildrenNodeValidTypes = createImmutableSet(base
                        .getSingleChildrenNodeValidTypes());
                
                this.hashCode = hashOf(this.type, this.keyProperty,
                        this.parentNodeValidTypes, this.childrenNodeValidTypes,
                        this.propertyTypes, this.keyPropertyType,
                        this.singleChildrenNodeValidTypes);
            }
            
            /**
             * {@inheritDoc}
             * 
             */
            public void addPropertyTypes(
                    final Map<String, Class<?>> newPropertyTypes) {
                throw logAndReturn(new UnsupportedOperationException());
            }
            
            @Override
            public boolean equals(final Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof StaticMetadata)) {
                    return false;
                }
                final StaticMetadata that = (StaticMetadata) o;
                return eachEquality(of(this.type, this.keyProperty,
                        this.parentNodeValidTypes, this.childrenNodeValidTypes,
                        this.propertyTypes, this.keyPropertyType,
                        this.singleChildrenNodeValidTypes), andOf(that
                        .getType(), that.getKeyProperty(), that
                        .getParentNodeValidTypes(), that
                        .getChildrenValidNodeTypes(), that.getPropertyTypes(),
                        that.getKeyPropertyType(), that
                                .getSingleChildrenNodeValidTypes()));
            }
            
            /**
             * {@inheritDoc}
             */
            public Set<Class<? extends ConfigurationNode>> getChildrenValidNodeTypes() {
                return this.childrenNodeValidTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public String getKeyProperty() {
                return this.keyProperty;
            }
            
            /**
             * {@inheritDoc}
             */
            public Class<? extends Serializable> getKeyPropertyType() {
                return this.keyPropertyType;
            }
            
            /**
             * {@inheritDoc}
             */
            public Set<Class<? extends ConfigurationNode>> getParentNodeValidTypes() {
                return this.parentNodeValidTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public Map<String, Class<?>> getPropertyTypes() {
                return this.propertyTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public Set<Class<? extends ConfigurationNode>> getSingleChildrenNodeValidTypes() {
                return this.singleChildrenNodeValidTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public Class<? extends ConfigurationNode> getType() {
                return this.type;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public int hashCode() {
                return this.hashCode;
            }
            
            /**
             * {@inheritDoc}
             * 
             * @throws UnsupportedOperationException
             *             since it is a non mutable {@link StaticMetadata}.
             */
            public void setChildrenNodeValidTypes(
                    final Class<? extends ConfigurationNode>... childrenNodeValidTypes) {
                throw logAndReturn(new UnsupportedOperationException());
            }
            
            /**
             * {@inheritDoc}
             * 
             * @throws UnsupportedOperationException
             *             since it is a non mutable {@link StaticMetadata}.
             */
            public void setKeyProperty(final String keyProperty) {
                throw logAndReturn(new UnsupportedOperationException());
            }
            
            /**
             * {@inheritDoc}
             * 
             * @throws UnsupportedOperationException
             *             since it is a non mutable {@link StaticMetadata}.
             */
            public void setKeyPropertyType(
                    final Class<? extends Serializable> keyPropertyType) {
                throw logAndReturn(new UnsupportedOperationException());
            }
            
            /**
             * {@inheritDoc}
             * 
             * @throws UnsupportedOperationException
             *             since it is a non mutable {@link StaticMetadata}.
             */
            public void setParentNodeValidTypes(
                    final Class<? extends ConfigurationNode>... parentNodeValidTypes) {
                throw logAndReturn(new UnsupportedOperationException());
            }
            
            /**
             * {@inheritDoc}
             * 
             * @throws UnsupportedOperationException
             *             since it is a non mutable {@link StaticMetadata}.
             */
            public void setSingleChildrenNodeValidTypes(
                    final Class<? extends ConfigurationNode>... singleChildrenNodeValidTypes) {
                throw logAndReturn(new UnsupportedOperationException());
                
            }
            
            /**
             * {@inheritDoc}
             * 
             * @throws UnsupportedOperationException
             *             since it is a non mutable {@link StaticMetadata}.
             */
            public void setType(final Class<? extends ConfigurationNode> type) {
                throw logAndReturn(new UnsupportedOperationException());
            }
        }
        
        /**
         * This inner class was made to store class behavior data for nodes. It
         * should be created and frozen on a static way.
         * 
         * @author Luiz Fernando Teston - feu.teston@caravelatech.com
         * 
         */
        private static class MutableClassMetadata implements StaticMetadata {
            
            private Class<? extends ConfigurationNode> type;
            private Set<Class<? extends ConfigurationNode>> parentNodeValidTypes;
            private Set<Class<? extends ConfigurationNode>> childrenNodeValidTypes;
            private String keyProperty;
            private Class<? extends Serializable> keyPropertyType;
            private Set<Class<? extends ConfigurationNode>> singleChildrenNodeValidTypes;
            
            private final Map<String, Class<?>> propertyTypes;
            
            /**
             * default constructor
             */
            public MutableClassMetadata() {
                this.propertyTypes = new HashMap<String, Class<?>>();
            }
            
            /**
             * copy constructor
             * 
             * @param base
             */
            public MutableClassMetadata(final StaticMetadata base) {
                this.type = base.getType();
                this.keyProperty = base.getKeyProperty();
                this.parentNodeValidTypes = base.getParentNodeValidTypes();
                this.childrenNodeValidTypes = base.getChildrenValidNodeTypes();
                this.propertyTypes = base.getPropertyTypes() == null ? new HashMap<String, Class<?>>()
                        : base.getPropertyTypes();
                this.keyPropertyType = base.getKeyPropertyType();
                this.singleChildrenNodeValidTypes = base
                        .getSingleChildrenNodeValidTypes();
            }
            
            /**
             * {@inheritDoc}
             */
            public void addPropertyTypes(
                    final Map<String, Class<?>> newPropertyTypes) {
                this.propertyTypes.putAll(newPropertyTypes);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean equals(final Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof StaticMetadata)) {
                    return false;
                }
                final StaticMetadata that = (StaticMetadata) o;
                return eachEquality(of(this.type, this.keyProperty,
                        this.parentNodeValidTypes, this.childrenNodeValidTypes,
                        this.propertyTypes, this.keyPropertyType,
                        this.singleChildrenNodeValidTypes), andOf(that
                        .getType(), that.getKeyProperty(), that
                        .getParentNodeValidTypes(), that
                        .getChildrenValidNodeTypes(), that.getPropertyTypes(),
                        that.getKeyPropertyType(), that
                                .getSingleChildrenNodeValidTypes()));
                
            }
            
            /**
             * {@inheritDoc}
             */
            public Set<Class<? extends ConfigurationNode>> getChildrenValidNodeTypes() {
                return this.childrenNodeValidTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public String getKeyProperty() {
                return this.keyProperty;
            }
            
            /**
             * {@inheritDoc}
             */
            public Class<? extends Serializable> getKeyPropertyType() {
                return this.keyPropertyType;
            }
            
            /**
             * {@inheritDoc}
             */
            public Set<Class<? extends ConfigurationNode>> getParentNodeValidTypes() {
                return this.parentNodeValidTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public Map<String, Class<?>> getPropertyTypes() {
                return this.propertyTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public Set<Class<? extends ConfigurationNode>> getSingleChildrenNodeValidTypes() {
                return this.singleChildrenNodeValidTypes;
            }
            
            /**
             * {@inheritDoc}
             */
            public Class<? extends ConfigurationNode> getType() {
                return this.type;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public int hashCode() {
                return hashOf(this.type, this.keyProperty,
                        this.parentNodeValidTypes, this.childrenNodeValidTypes,
                        this.propertyTypes, this.keyPropertyType,
                        this.singleChildrenNodeValidTypes);
                
            }
            
            /**
             * {@inheritDoc}
             */
            public void setChildrenNodeValidTypes(
                    final Class<? extends ConfigurationNode>... childrenNodeValidTypes) {
                this.childrenNodeValidTypes = new HashSet<Class<? extends ConfigurationNode>>(
                        asList(childrenNodeValidTypes));
            }
            
            /**
             * {@inheritDoc}
             */
            public void setKeyProperty(final String keyProperty) {
                this.keyProperty = keyProperty;
            }
            
            /**
             * {@inheritDoc}
             */
            public void setKeyPropertyType(
                    final Class<? extends Serializable> keyPropertyType) {
                this.keyPropertyType = keyPropertyType;
            }
            
            /**
             * {@inheritDoc}
             */
            public void setParentNodeValidTypes(
                    final Class<? extends ConfigurationNode>... parentNodeValidTypes) {
                this.parentNodeValidTypes = new HashSet<Class<? extends ConfigurationNode>>(
                        asList(parentNodeValidTypes));
            }
            
            /**
             * {@inheritDoc}
             */
            public void setSingleChildrenNodeValidTypes(
                    final Class<? extends ConfigurationNode>... singleChildrenNodeValidTypes) {
                this.singleChildrenNodeValidTypes = new HashSet<Class<? extends ConfigurationNode>>(
                        asList(singleChildrenNodeValidTypes));
            }
            
            /**
             * {@inheritDoc}
             */
            public void setType(final Class<? extends ConfigurationNode> type) {
                this.type = type;
            }
            
        }
        
        /**
         * Creates an immutable and thread safe {@link StaticMetadata}
         * 
         * @param origin
         * @return a static metadata
         */
        public static StaticMetadata createImmutable(final StaticMetadata origin) {
            return new ImmutableClassMetadata(origin);
        }
        
        /**
         * Creates an mutable {@link StaticMetadata}. An immutable one can be
         * created using the {@link #createImmutable(StaticMetadata)} after this
         * {@link StaticMetadata} is setted correctly.
         * 
         * @return a static metadata
         */
        public static StaticMetadata createMutable() {
            return new MutableClassMetadata();
        }
        
        /**
         * Creates an mutable {@link StaticMetadata} using a existing one as a
         * base. An immutable one can be created using the
         * {@link #createImmutable(StaticMetadata)} after this
         * {@link StaticMetadata} is setted correctly.
         * 
         * @param origin
         *            the base {@link StaticMetadata}
         * 
         * @return a new {@link StaticMetadata}
         */
        public static StaticMetadata createMutable(final StaticMetadata origin) {
            return new MutableClassMetadata(origin);
        }
    }
    
    /**
     * Sets the propertyTypes. See {@link #getPropertyTypes()}.
     * 
     * 
     * @param propertyTypes
     */
    public abstract void addPropertyTypes(Map<String, Class<?>> propertyTypes);
    
    /**
     * The types that can be used as a children for this kind of node.
     * 
     * @return all the valid types to be used as a children types
     */
    public abstract Set<Class<? extends ConfigurationNode>> getChildrenValidNodeTypes();
    
    /**
     * It's keyProperty name should change depending on the node type. For
     * example, for a file, the keyProperty should be location, and for a
     * project the keyProperty should be name.
     * 
     * @return the key property name
     */
    public abstract String getKeyProperty();
    
    /**
     * See the {@link #getKeyPropertyType()}.
     * 
     * @return the key property type.
     */
    public Class<? extends Serializable> getKeyPropertyType();
    
    /**
     * The parent type should be null in case of a root node, or all the valid
     * parent types. As it is, a node can have multiple parent types and should
     * be valid when placed in all that types.
     * 
     * @return all valid parent types
     */
    public abstract Set<Class<? extends ConfigurationNode>> getParentNodeValidTypes();
    
    /**
     * All valid properties on a {@link ConfigurationNode} needs to be maped
     * with its name and type. So, with that its posible to validate its data
     * and also to recover the metadata without using reflection.
     * 
     * @return the property types
     */
    public abstract Map<String, Class<?>> getPropertyTypes();
    
    /**
     * Returns all valid single children node types. This is the node types that
     * don't need to have any key property, and because of that, this nodes can
     * be stored only by a single property, but not by many of them
     * (collection).
     * 
     * @return a set of children types
     */
    public Set<Class<? extends ConfigurationNode>> getSingleChildrenNodeValidTypes();
    
    /**
     * It is the {@link ConfigurationNode} type itself with the node associated
     * with this {@link StaticMetadata static metadata}.
     * 
     * @return the node type
     */
    public abstract Class<? extends ConfigurationNode> getType();
    
    /**
     * Sets the childrenNodeValidTypes. See {@link #getChildrenValidNodeTypes()}
     * 
     * @param childrenNodeValidTypes
     */
    public abstract void setChildrenNodeValidTypes(
            Class<? extends ConfigurationNode>... childrenNodeValidTypes);
    
    /**
     * Sets the keyProperty. See {@link #getKeyProperty()}.
     * 
     * @param keyProperty
     */
    public abstract void setKeyProperty(String keyProperty);
    
    /**
     * Sets the key property type. Should be any single java class, but not a
     * new pojo class for example.
     * 
     * @param keyPropertyType
     */
    public void setKeyPropertyType(Class<? extends Serializable> keyPropertyType);
    
    /**
     * Sets the parentNodeValidTypes. See {@link #getParentNodeValidTypes()}.
     * 
     * @param parentNodeValidTypes
     */
    public abstract void setParentNodeValidTypes(
            Class<? extends ConfigurationNode>... parentNodeValidTypes);
    
    /**
     * Sets the single children node valid types.
     * 
     * @param singleChildrenNodeValidTypes
     */
    public void setSingleChildrenNodeValidTypes(
            Class<? extends ConfigurationNode>... singleChildrenNodeValidTypes);
    
    /**
     * Sets the type. See {@link #getType()}.
     * 
     * @param type
     */
    public abstract void setType(Class<? extends ConfigurationNode> type);
}