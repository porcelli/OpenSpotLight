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
package org.openspotlight.storage.domain;

import java.io.InputStream;
import java.util.Set;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;

/**
 * Defines a common API for handling properties on {@link StorageNode} and {@link StorageLink} as well exposes its unique string
 * identifier and wich partition it is stored.
 * 
 * @see Property
 * @author feuteston
 * @author porcelli
 */
public interface PropertyContainer {

    /**
     * Returns the unique identifier of the element.
     * 
     * @return the unique identifier
     */
    String getKeyAsString();

    /**
     * Returns the partition of the element.
     * 
     * @return the partition
     */
    Partition getPartition();

    /**
     * Invalidates the internal cache and, as the properties load is a lazy operation, will force reload its state during next
     * access.
     */
    void forceReload();

    /**
     * Returns all existing properties, or an empty {@link Set} if this element has no properties. <br>
     * In {@link StorageNode} case, this method returns also the {@link SimpleKey} entries.<br>
     * 
     * @param session the storage session
     * @return all properties of this element
     * @throws IllegalArgumentException if input param is null
     * @see Property
     */
    Set<Property> getProperties(StorageSession session)
        throws IllegalArgumentException;

    /**
     * Returns the property associated with the given name or null if property not found. <br>
     * In {@link StorageNode} case, you can also use this method to get any {@link SimpleKey} entry.<br>
     * 
     * @param session the storage session
     * @param name the property name
     * @return the property associated with the given name or null if property not found.
     * @throws IllegalArgumentException if any input param is null or empty
     * @see Property
     */
    Property getProperty(StorageSession session, String name)
        throws IllegalArgumentException;

    /**
     * Returns all existing property names, or an empty {@link Set} if this element has no properties. <br>
     * In {@link StorageNode} case, this method returns also the {@link SimpleKey} names.<br>
     * 
     * @param session the storage session
     * @return all property keys of this element
     * @throws IllegalArgumentException if input param is null
     */
    Set<String> getPropertyNames(StorageSession session)
        throws IllegalArgumentException;

    /**
     * Returns the property value as an array of bytes or null if property not found. <br>
     * In {@link StorageNode} case, you can also use this method to get any {@link SimpleKey} value. <b>Important</b>: you should
     * prefer use the {@link #getPropertyValueAsString(StorageSession, String)} method, once that {@link SimpleKey} are always
     * {@link String} values. <br>
     * 
     * @param session the storage session
     * @param name the property name
     * @return the property value as an array of bytes or null if property not found
     * @throws IllegalArgumentException if any input param is null or empty
     */
    byte[] getPropertyValueAsBytes(StorageSession session, String name)
        throws IllegalArgumentException;

    /**
     * Returns the property value as {@link InputStream} or null if property not found. <br>
     * In {@link StorageNode} case, you can also use this method to get any {@link SimpleKey} value. <b>Important</b>: you should
     * prefer use the {@link #getPropertyValueAsString(StorageSession, String)} method, once that {@link SimpleKey} are always
     * {@link String} values. <br>
     * 
     * @param session the storage session
     * @param name the property name
     * @return the property value as {@link InputStream} or null if property not found
     * @throws IllegalArgumentException if any input param is null or empty
     * @see InputStream
     */
    InputStream getPropertyValueAsStream(StorageSession session, String name)
        throws IllegalArgumentException;

    /**
     * Returns the property value as string or null if property not found. <br>
     * In {@link StorageNode} case, you can also use this method to get any {@link SimpleKey} value.<br>
     * 
     * @param session the storage session
     * @param name the property name
     * @return the property value as string or null if property not found
     * @throws IllegalArgumentException if any input param is null or empty
     */
    String getPropertyValueAsString(StorageSession session, String name)
        throws IllegalArgumentException;

    /**
     * Sets (or creates if does not exists) an <b>indexed</b> property value for the given name. <br>
     * Null is an accepted property value.<br>
     * <b>Important Notes:</b><br>
     * &nbsp;1. Only indexed properties are searchable.<br>
     * &nbsp;2. Indexed properties consumes more disk space.
     * 
     * @param session the storage session
     * @param name the property name
     * @param value the property value
     * @return the setted (or created) indexed property
     * @throws IllegalArgumentException if input params session and name are null or empty
     * @throws IllegalStateException if try to set a property that is also a {@link SimpleKey}
     * @see Property
     */
    Property setIndexedProperty(StorageSession session, String name, String value)
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Sets (or creates if does not exists) the property value for the given name. <br>
     * Null is an accepted property value. <br>
     * <b>Important Note:</b><br>
     * Simple properties are not indexed and due this aren't searchable.
     * 
     * @param session the storage session
     * @param name the property name
     * @param value the property value
     * @return the setted (or created) property
     * @throws IllegalArgumentException if input params session and name are null or empty
     * @throws IllegalStateException if try to set a property that is also a {@link SimpleKey}
     * @see Property
     */
    Property setSimpleProperty(StorageSession session, String name, byte[] value)
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Sets (or creates if does not exists) the property value for the given name. <br>
     * Null is an accepted property value. <br>
     * <br>
     * <b>Important Note:</b><br>
     * Simple properties are not indexed and due this aren't searchable.
     * 
     * @param session the storage session
     * @param name the property name
     * @param value the property value
     * @return the setted (or created) property
     * @throws IllegalArgumentException if input params session and name are null or empty
     * @throws IllegalStateException if try to set a property that is also a {@link SimpleKey}
     * @see Property
     */
    Property setSimpleProperty(StorageSession session, String name, InputStream value)
        throws IllegalArgumentException, IllegalStateException;

    /**
     * Sets (or creates if does not exists) the property value for the given name. <br>
     * Null is an accepted property value. <br>
     * <b>Important Note:</b><br>
     * Simple properties are not indexed and due this aren't searchable.
     * 
     * @param session the storage session
     * @param name the property name
     * @param value the property value
     * @return the setted (or created) property
     * @throws IllegalArgumentException if input params session and name are null or empty
     * @throws IllegalStateException if try to set a property that is also a {@link SimpleKey}
     * @see Property
     */
    Property setSimpleProperty(StorageSession session, String name, String value)
        throws IllegalArgumentException, IllegalStateException;

}
