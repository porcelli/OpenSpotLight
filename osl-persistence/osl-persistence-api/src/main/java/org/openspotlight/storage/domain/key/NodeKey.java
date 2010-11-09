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

package org.openspotlight.storage.domain.key;

import java.util.Set;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.domain.StorageDataMarker;
import org.openspotlight.storage.domain.StorageNode;

/**
 * Identifies uniquely a {@link StorageNode}. The node uniquiness is achieved by: <br>
 * <ul>
 * <li>its {@link Partition} (where its stored)
 * <li>its parent node (that can be null)
 * <li>and its {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey} (that aggregates simple keys and node type)
 * </ul>
 * NodeKeys shouldn't be instantiated direclty, to create new instances of NodeKeys you should use the
 * {@link org.openspotlight.storage.StorageSession.NodeKeyBuilder} interface.<br>
 * 
 * @author feuteston
 * @author porcelli
 */
public interface NodeKey extends StorageDataMarker, Comparable<NodeKey> {

    /**
     * The composite key aggregates all {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey}s and the node
     * type.
     * 
     * @author feuteston
     * @author porcelli
     */
    public interface CompositeKey extends StorageDataMarker, Comparable<CompositeKey> {

        /**
         * The simplest key (pair of name and value) that composes a node key.
         * 
         * @author feuteston
         * @author porcelli
         */
        public interface SimpleKey extends StorageDataMarker, Comparable<SimpleKey> {

            /**
             * Returns the key name
             * 
             * @return the key name
             */
            String getKeyName();

            /**
             * Returns the key value
             * 
             * @return the key value
             */
            String getValue();
        }

        /**
         * Returns this key encoded into a string format
         * 
         * @return the node key encoded into string format
         */
        String getKeyAsString();

        /**
         * Returns all existing {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey} names, or an empty
         * {@link Set} if there is no SimpleKeys. <br>
         * 
         * @return all simple key names of this composite key
         */
        Set<String> getKeyNames();

        /**
         * Returns all existing {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey}, or an empty
         * {@link Set} if there is no SimpleKeys. <br>
         * 
         * @return all simple key of this composite key
         */
        Set<SimpleKey> getKeys();

        /**
         * Returns the node type
         * 
         * @return the node type
         */
        String getNodeType();
    }

    /**
     * Returns the {@link org.openspotlight.storage.domain.key.NodeKey.CompositeKey}
     * 
     * @return the composite key
     */
    CompositeKey getCompositeKey();

    /**
     * Returns this key encoded into a string format
     * 
     * @return the node key encoded into string format
     */
    String getKeyAsString();

    /**
     * Returns the parent key (using {@link #getKeyAsString()}), or null if there is no parent.
     * 
     * @return the parent key, or null if there is no parent
     */
    String getParentKeyAsString();

    /**
     * Returns the {@link Partition} where the {@link StorageNode} is stored.
     * 
     * @return the partition where node is stored
     */
    Partition getPartition();

}
