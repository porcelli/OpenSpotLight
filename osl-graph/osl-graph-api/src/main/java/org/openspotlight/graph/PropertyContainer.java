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
package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.common.Pair;

/**
 * Defines a common API for handling properties on {@link Node}, {@link Link} and {@link Context}.
 * <p>
 * Properties are key-value pairs where keys are always strings and values are any serializable object.
 * 
 * @author porcelli
 * @author feuteston
 */
public interface PropertyContainer {

    /**
     * Checks if this element is modified.
     * 
     * @return true is modified, false otherwise
     */
    boolean isDirty();

    /**
     * Sets the property value for the given key. Null is not an accepted property value.
     * 
     * @param <V> any serializable type
     * @param key the property key
     * @param value the property value
     * @throws IllegalArgumentException if any input param is null
     */
    <V extends Serializable> void setProperty(String key,
                                               V value)
        throws IllegalArgumentException;

    /**
     * Returns a list of properties pairs (key-value).
     * 
     * @return the key-value properties pairs
     */
    Set<Pair<String, Serializable>> getProperties();

    /**
     * Returns <code>true</code> if this property container has a property accessible through the given key, <code>false</code>
     * otherwise. Null is not an accepted property value.
     * 
     * @param key the property key
     * @return <code>true</code> if this element has a property accessible through the given key, <code>false</code> otherwise
     * @throws IllegalArgumentException if the input param is null
     */
    boolean hasProperty(String key)
        throws IllegalArgumentException;

    /**
     * Returns all existing property keys, or an empty iterable if this element has no properties.
     * 
     * @return all property keys on this element
     */
    Iterable<String> getPropertyKeys();

    /**
     * Returns the property value associated with the given key or null if property not found.
     * 
     * @param <V> any serializable type
     * @param key the property key
     * @return the property value or null if property not found
     * @throws IllegalArgumentException if the input param is null
     */
    <V extends Serializable> V getPropertyValue(String key)
        throws IllegalArgumentException;

    /**
     * Returns the property value associated with the given key, or a default value.
     * 
     * @param <V> any serializable type
     * @param key the property key
     * @param defaultValue the default value that will be returned if no property value was associated with the given key
     * @return the property value associated with the given key or the default value.
     * @throws IllegalArgumentException if any input param is null
     */
    <V extends Serializable> V getPropertyValue(String key,
                                                 V defaultValue)
        throws IllegalArgumentException;

    /**
     * Returns the property value as string (this is just a sugar method) or null if property not found.
     * 
     * @param key the property key
     * @return the property value as string or null if property not found
     * @throws IllegalArgumentException if the input param is null
     */
    String getPropertyValueAsString(String key)
        throws IllegalArgumentException;

    /**
     * Removes the property associated with the given key if exists. If there's no property associated with the key, nothing will
     * happen.
     * 
     * @param key the property key
     * @throws IllegalArgumentException if the input param is null
     */
    void removeProperty(String key)
        throws IllegalArgumentException;

}
