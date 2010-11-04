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

/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store any kind of
 * connection state. This implementation must not be shared between threads.
 */
public interface StorageSession extends Disposable {

    PartitionMethods withPartition(Partition partition);

    interface PartitionMethods extends NodeFactory {

        Iterable<String> getAllNodeTypes();

        NodeKeyBuilder createKey(String nodeType);

        Iterable<StorageNode> findByCriteria(Criteria criteria);

        Iterable<StorageNode> findByType(String nodeType);

        StorageNode findUniqueByCriteria(Criteria criteria);

        CriteriaBuilder createCriteria();

        NodeBuilder createWithType(String nodeType);

        NodeKey createNewSimpleKey(String... nodePaths);

        StorageNode createNewSimpleNode(String... nodePaths);

    }

    StorageNode findNodeByStringId(String idAsString);

    void removeNode(StorageNode StorageNode);

    static enum FlushMode {
        AUTO,
        EXPLICIT
    }

    FlushMode getFlushMode();

    interface NodeKeyBuilder {

        NodeKeyBuilder withSimpleKey(String keyName,
                                      String value);

        NodeKeyBuilder withParent(Partition partition,
                                       String nodeType);

        NodeKeyBuilder withParent(String parentId);

        NodeKey andCreate();
    }

    void discardTransient();

    void flushTransient();

    StorageLink addLink(StorageNode origin,
                         StorageNode destiny,
                         String name);

    void removeLink(StorageNode origin,
                     StorageNode destiny,
                     String name);

    void removeLink(StorageLink link);

    Iterable<StorageLink> findLinks(StorageNode origin);

    Iterable<StorageLink> findLinks(StorageNode origin,
                                     String type);

    StorageLink getLink(StorageNode origin,
                         StorageNode destiny,
                         String type);

    Iterable<StorageLink> findLinks(StorageNode origin,
                                     StorageNode destiny);

}
