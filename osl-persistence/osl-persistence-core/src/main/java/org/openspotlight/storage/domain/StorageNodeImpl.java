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

import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.openspotlight.storage.StorageSessionImpl;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;

/**
 * Internal (default) implementation of {@link StorageNode}.
 * 
 * @author feuteston
 * @author porcelli
 */
public class StorageNodeImpl extends PropertyContainerImpl implements StorageNode {

    private static final long                                                        serialVersionUID           =
                                                                                                                    -4545520206784316277L;

    private WeakHashMap<Partition, WeakReference<Iterable<StorageNode>>>             childrenWeakReference      =
                                                                                                                    new WeakHashMap<Partition, WeakReference<Iterable<StorageNode>>>();

    private final WeakHashMap<Partition, WeakHashMap<Iterable<StorageNode>, String>> typedChildrenWeakReference =
                                                                                                                    new WeakHashMap<Partition, WeakHashMap<Iterable<StorageNode>, String>>();

    private final NodeKey                                                            nodeKey;

    private final String                                                             nodeType;

    private WeakReference<StorageNode>                                               parentWeakReference        = null;

    private final Partition                                                          partition;

    public StorageNodeImpl(final NodeKey uniqueKey, final boolean resetTimeout)
        throws IllegalArgumentException {
        this(uniqueKey, null, resetTimeout);
    }

    public StorageNodeImpl(final NodeKey uniqueKey, final Set<Property> properties, final boolean resetTimeout)
        throws IllegalArgumentException {
        super(resetTimeout);
        nodeType = uniqueKey.getCompositeKey().getNodeType();
        if (nodeType == null) { throw new IllegalArgumentException(); }
        nodeKey = uniqueKey;
        if (properties != null) {
            for (final Property property: properties) {
                propertiesByName.put(property.getPropertyName(), property);
            }
        }
        partition = uniqueKey.getPartition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void verifyBeforeSet(final String propertyName) {
        if (nodeKey.getCompositeKey().getKeyNames().contains(propertyName)) { throw new IllegalStateException(); }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeBuilder createWithType(final StorageSession session, final String type)
        throws IllegalArgumentException {
        checkNotNull("session", session);
        checkNotEmpty("type", type);

        return ((StorageSessionImpl<?, ?>) session).createNode(partition, this, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final StorageNodeImpl that = (StorageNodeImpl) o;

        if (nodeType != null ? !nodeType.equals(that.nodeType) : that.nodeType != null) { return false; }
        return !(nodeKey != null ? !nodeKey.equals(that.nodeKey) : that.nodeKey != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageNode> getChildren(final Partition partition, final StorageSession session) {
        Iterable<StorageNode> children =
            childrenWeakReference.get(partition) != null ? childrenWeakReference.get(partition).get() : null;
        if (children == null) {
            children = getChildrenForcingReload(partition, session);
        }
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageNode> getChildren(final Partition partition, final StorageSession session, final String type) {
        Iterable<StorageNode> thisChildren = null;
        if (typedChildrenWeakReference.containsKey(partition) && typedChildrenWeakReference.get(partition).containsValue(type)) {
            for (final Map.Entry<Iterable<StorageNode>, String> entry: typedChildrenWeakReference.get(partition).entrySet()) {
                if (type.equals(entry.getValue())) {
                    thisChildren = entry.getKey();
                    break;
                }
            }
        }

        if (thisChildren == null) {
            thisChildren = getChildrenForcingReload(partition, session, type);
        }
        return thisChildren;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageNode> getChildrenForcingReload(final Partition partition, final StorageSession session,
                                                          final String type) {
        final Iterable<StorageNode> children =
            ((StorageSessionImpl<?, ?>) session).getChildren(partition, this, type);
        WeakHashMap<Iterable<StorageNode>, String> newTypedChildrenWeakReference =
            new WeakHashMap<Iterable<StorageNode>, String>();
        newTypedChildrenWeakReference.put(children, type);
        typedChildrenWeakReference.put(partition, newTypedChildrenWeakReference);
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageNode> getChildrenForcingReload(final Partition partition, final StorageSession session) {
        final Iterable<StorageNode> children = ((StorageSessionImpl<?, ?>) session).getChildren(partition, this);
        childrenWeakReference.put(partition, new WeakReference<Iterable<StorageNode>>(children));
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKey getKey() {
        return nodeKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKeyAsString() {
        return getKey().getKeyAsString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageNode getParent(final StorageSession session) {
        StorageNode parent = parentWeakReference != null ? parentWeakReference.get() : null;
        if (parent == null) {
            parent = ((StorageSessionImpl<?, ?>) session).getParent(this);
            parentWeakReference = new WeakReference<StorageNode>(parent);
        }
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Partition getPartition() {
        return getKey().getPartition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return nodeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = nodeType != null ? nodeType.hashCode() : 0;
        result = 31 * result + (nodeKey != null ? nodeKey.hashCode() : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final StorageSession session) {
        session.removeNode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StorageNodeImpl{partition=" + partition + ", nodeEntryName='" + nodeType + "', uniqueKey=" + nodeKey
            + '}';
    }
}
