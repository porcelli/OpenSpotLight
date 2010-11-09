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

package org.openspotlight.storage;

import org.openspotlight.common.Disposable;
import org.openspotlight.storage.Criteria.CriteriaBuilder;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;

/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store any kind of
 * connection state. This implementation must not be shared between threads.
 * 
 * @author feuteston
 * @author porcelli
 */
public interface StorageSession extends Disposable {

    /**
     * Defines the {@link StorageSession} flush behavior.
     * 
     * @author feuteston
     * @author porcelli
     */
    static enum FlushMode {
        /**
         * Data are automatically flushed into storage, wich means that its not necessary execute
         * {@link StorageSession#flushTransient()} method.
         */
        AUTO,
        /**
         * Its mandatory execute the {@link StorageSession#flushTransient()} method to flush data into storage.
         */
        EXPLICIT
    }

    /**
     * Builder pattern that creates {@link NodeKey} instances.
     * 
     * @author feuteston
     * @author porcelli
     */
    interface NodeKeyBuilder {

        /**
         * Creates the {@link NodeKey} instance based on builder data.
         * 
         * @return the node key instance
         */
        NodeKey andCreate();

        /**
         * Adds a new {@link SimpleKey} into stack.
         * 
         * @param keyName the key name
         * @param value the key value
         * @return self builder
         */
        NodeKeyBuilder withSimpleKey(String keyName, String value);

        /**
         * Sets the parent's key of the current {@link NodeKey}.
         * 
         * @param parentKey the parent key
         * @return self builder
         */
        NodeKeyBuilder withParent(String parentKey);

        /**
         * Pushes a new {@link NodeKey} into builder stack
         * 
         * @param partition the parent partition
         * @param nodeType the parent node type
         * @return the pushed
         */
        NodeKeyBuilder withParent(Partition partition, String nodeType);
    }

    /**
     * Interface that defines a set of operations available to execute for a {@link Partition}.
     * 
     * @author feuteston
     * @author porcelli
     */
    interface PartitionMethods extends NodeFactory {

        CriteriaBuilder createCriteria();

        NodeKeyBuilder createKey(String nodeType);

        NodeKey createNewSimpleKey(String... nodePaths);

        StorageNode createNewSimpleNode(String... nodePaths);

        NodeBuilder createWithType(String nodeType);

        Iterable<StorageNode> findByCriteria(Criteria criteria);

        Iterable<StorageNode> findByType(String nodeType);

        StorageNode findUniqueByCriteria(Criteria criteria);

        Iterable<String> getAllNodeTypes();
    }

    /**
     * Defines the partition wich will be executed
     * 
     * @param partition the chosen partition
     * @return partition manipulation methods
     */
    PartitionMethods withPartition(Partition partition);

    /**
     * Returns the session's flush mode behavior.
     * 
     * @return the flush mode behavior
     * @see FlushMode
     */
    FlushMode getFlushMode();

    /**
     * Flush into storage the transient (not yet stored) data.<br>
     * <b>Note</b> that this method just makes sense if session is running with {@link FlushMode#EXPLICIT} mode.
     */
    void flushTransient();

    /**
     * This method discard all transiente (not yet stored) data.<br>
     * <b>Important Notes:</b><br>
     * <ul>
     * <li>this method has no undo, so be carefull
     * <li>this method just makes sense if session is running with {@link FlushMode#EXPLICIT} mode.
     * </ul>
     */
    void discardTransient();

    StorageLink addLink(StorageNode origin, StorageNode destiny, String name);

    void removeLink(StorageLink link);

    void removeLink(StorageNode origin, StorageNode destiny, String name);

    StorageLink getLink(StorageNode origin, StorageNode destiny, String type);

    Iterable<StorageLink> findLinks(StorageNode origin);

    Iterable<StorageLink> findLinks(StorageNode origin, StorageNode destiny);

    Iterable<StorageLink> findLinks(StorageNode origin, String type);

    StorageNode getNode(String key);

    void removeNode(StorageNode StorageNode);

}
