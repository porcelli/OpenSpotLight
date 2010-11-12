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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.key.NodeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl.SimpleKeyImpl;

/**
 * Internal (default) implementation of {@link NodeKeyBuilder}.
 * 
 * @author feuteston
 * @author porcelli
 */
public class NodeKeyBuilderImpl implements NodeKeyBuilder {

    private final NodeKeyBuilderImpl child;
    private final Set<SimpleKey>     localEntries = newHashSet();

    private String                   parentKey;

    private final Partition          partition;

    private final String             type;

    private NodeKeyBuilderImpl(final String type, final NodeKeyBuilderImpl child, final Partition partition) {
        this.type = type;
        this.child = child;
        this.partition = partition;
    }

    public NodeKeyBuilderImpl(final String type, final Partition partition) {
        this.type = type;
        this.partition = partition;
        this.child = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKey andCreate() {
        NodeKey currentKey = null;
        NodeKeyBuilderImpl currentBuilder = this;
        if (parentKey == null) {
            do {
                final CompositeKey localKey = new CompositeKeyImpl(currentBuilder.localEntries, currentBuilder.type);
                currentKey =
                    new NodeKeyImpl(localKey, currentKey != null ? currentKey.getKeyAsString() : null,
                        currentBuilder.partition);
                currentBuilder = currentBuilder.child;
            } while (currentBuilder != null);
        } else {
            final CompositeKey localKey = new CompositeKeyImpl(currentBuilder.localEntries, currentBuilder.type);
            currentKey = new NodeKeyImpl(localKey, parentKey, partition);
        }
        return currentKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKeyBuilder withParent(final Partition newPartition, final String nodeType) {
        return new NodeKeyBuilderImpl(nodeType, this, newPartition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKeyBuilder withParent(final String parentId) {
        this.parentKey = parentId;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKeyBuilder withSimpleKey(final String propertyName, final String value) {
        this.localEntries.add(new SimpleKeyImpl(propertyName, value));
        return this;
    }
}
