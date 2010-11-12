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

package org.openspotlight.storage.engine;

import java.util.Set;

import org.openspotlight.common.Disposable;
import org.openspotlight.storage.NodeCriteria;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;

public interface StorageEngineBind<R> extends Disposable {

    R createLinkReference(StorageLink link);

    R createNodeReference(StorageNode node);

    void flushNewItem(R reference, StorageNode node)
        throws Exception;

    void flushRemovedItem(StorageNode node)
        throws Exception;

    void flushRemovedLink(StorageLink link)
        throws Exception;

    void handleNewLink(StorageNode source, StorageLink link)
        throws Exception;

    Iterable<StorageNode> search(NodeCriteria criteria)
        throws Exception;

    Iterable<StorageNode> getNodes(Partition partition, String type)
        throws Exception;

    Iterable<StorageLink> getLinks(StorageNode source, StorageNode target, String type)
        throws Exception;

    void flushSimpleProperty(R reference, Partition partition, Property property)
        throws Exception;

    Iterable<String> getAllNodeTypes(Partition partition)
        throws Exception;

    StorageNode getNode(String key)
        throws Exception;

    Iterable<StorageNode> getChildren(StorageNode node)
        throws Exception;

    Iterable<StorageNode> getChildren(StorageNode node, String type)
        throws Exception;

    StorageNode getParent(StorageNode node)
        throws Exception;

    Set<Property> loadProperties(PropertyContainer element)
        throws Exception;

    byte[] getPropertyValue(Partition partition, Property property)
        throws Exception;

    void savePartitions(Partition... partitions)
        throws Exception;

}
