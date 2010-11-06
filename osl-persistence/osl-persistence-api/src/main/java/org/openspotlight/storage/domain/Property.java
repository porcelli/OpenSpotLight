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

import org.openspotlight.storage.StorageSession;

/**
 * A Property represents the smallest granularity of content. Properties holds primarily string values, but it can handle any
 * other data thru a stream or byte array. <br>
 * Any {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey} can be also represented as a property, but as
 * keys are immutable, they cannot be modified.
 * 
 * @author feuteston
 * @author porcelli
 */
public interface Property {

    /**
     * Returns the property name.
     * 
     * @return the property name
     */
    String getPropertyName();

    /**
     * Checks if this property is indexed. Only indexed properties are searchable.
     * 
     * @return true is indexed, false otherwise
     */
    boolean isIndexed();

    /**
     * Checks if this property is a key.
     * 
     * @return true is indexed, false otherwise
     */
    //TODO: needs more info about what is this key!
    boolean isKey();

    /**
     * Returns the {@link PropertyContainer} ({@link StorageNode} or {@link StorageLink}) that owns this object.
     * 
     * @return the property owner
     */
    PropertyContainer getParent();

    /**
     * Sets the property value in String format. Null is an accepted value. <br>
     * 
     * @param session the storage session
     * @param value the property value
     * @throws IllegalArgumentException if input param session is null
     * @throws IllegalStateException if try to set a property that is also a
     *         {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey}
     */
    void setStringValue(StorageSession session, String value)
        throws IllegalArgumentException;

    /**
     * Sets the property value in byte array format. Null is an accepted value. <br>
     * 
     * @param session the storage session
     * @param value the property value
     * @throws IllegalArgumentException if input param session is null
     * @throws IllegalStateException if try to set a property that is also a
     *         {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey}
     */
    void setBytesValue(StorageSession session, byte[] value)
        throws IllegalArgumentException;

    /**
     * Sets the property value using {@link InputStream}. Null is an accepted value.<br>
     * The {@link InputStream} format is usefull when you have to store big values. Behind the scenes the {@link InputStream}
     * content is converted to byte array.
     * 
     * @param session the storage session
     * @param value the property value
     * @throws IllegalArgumentException if input param session is null
     * @throws IllegalStateException if try to set a property that is also a
     *         {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey}
     */
    void setStreamValue(StorageSession session, InputStream value)
        throws IllegalArgumentException;

    /**
     * Returns the property value as String.
     * 
     * @param session the storage session
     * @return the value as string
     * @throws IllegalArgumentException if input param is null
     */
    String getValueAsString(StorageSession session)
        throws IllegalArgumentException;

    /**
     * Returns the property value as byte array.
     * 
     * @param session the storage session
     * @return the value as byte array
     * @throws IllegalArgumentException if input param is null
     */
    byte[] getValueAsBytes(StorageSession session)
        throws IllegalArgumentException;

    /**
     * Returns the property value as {@link InputStream} - usefull for large data value.
     * 
     * @param session the storage session
     * @return the value as {@link InputStream}
     * @throws IllegalArgumentException if input param is null
     */
    InputStream getValueAsStream(StorageSession session)
        throws IllegalArgumentException;

}
