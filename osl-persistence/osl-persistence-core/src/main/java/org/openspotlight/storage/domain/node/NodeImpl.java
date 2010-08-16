/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */

package org.openspotlight.storage.domain.node;

import static com.google.common.collect.Maps.newHashMap;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.openspotlight.storage.AbstractStorageSession;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.key.LocalKey;
import org.openspotlight.storage.domain.key.UniqueKey;

public class NodeImpl extends PropertyContainerImpl implements
        Node {

    private static final long serialVersionUID = -4545520206784316277L;

    @Override
    protected void verifyBeforeSet(final String propertyName) {
        if (localKey.getEntryNames().contains(propertyName)) { throw new IllegalStateException(); }
    }

    public NodeImpl(final UniqueKey uniqueKey, final Set<Property> properties,
                            final boolean resetTimeout)
        throws IllegalArgumentException {
        super(resetTimeout);
        nodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
        if (nodeEntryName == null) { throw new IllegalArgumentException(); }
        localKey = uniqueKey.getLocalKey();
        this.uniqueKey = uniqueKey;
        propertiesByName = newHashMap();
        if (properties != null) {
            for (final Property property: properties) {
                propertiesByName.put(property.getPropertyName(), property);
            }
        }
        partition = uniqueKey.getPartition();
        namedChildrenWeakReference = new WeakHashMap<Iterable<Node>, String>();
    }

    public NodeImpl(final UniqueKey uniqueKey, final boolean resetTimeout)
        throws IllegalArgumentException {
        this(uniqueKey, null, resetTimeout);
    }

    private final Partition                           partition;

    private WeakReference<Node>                       parentWeakReference   = null;

    private WeakReference<Iterable<Node>>             childrenWeakReference = null;

    private final WeakHashMap<Iterable<Node>, String> namedChildrenWeakReference;

    private final Map<String, Property>               propertiesByName;

    private final String                              nodeEntryName;

    private final LocalKey                            localKey;

    private final UniqueKey                           uniqueKey;

    @Override
    public String getNodeEntryName() {
        return nodeEntryName;
    }

    @Override
    public LocalKey getLocalKey() {
        return localKey;
    }

    @Override
    public UniqueKey getUniqueKey() {
        return uniqueKey;
    }

    @Override
    public Iterable<Node> getChildren(final Partition partition,
                                              final StorageSession session) {
        Iterable<Node> children = childrenWeakReference != null ? childrenWeakReference
                                                                                              .get()
                : null;
        if (children == null) {
            children = getChildrenForcingReload(partition, session);
        }
        return children;
    }

    @Override
    public Iterable<Node> getChildrenNamed(final Partition partition,
                                                   final StorageSession session,
                                                   final String name) {

        Iterable<Node> thisChildren = null;
        if (namedChildrenWeakReference.containsValue(name)) {
            for (final Map.Entry<Iterable<Node>, String> entry: namedChildrenWeakReference
                                                                                            .entrySet()) {
                if (name.equals(entry.getValue())) {
                    thisChildren = entry.getKey();
                    break;
                }
            }
        }

        if (thisChildren == null) {
            thisChildren = getChildrenNamedForcingReload(partition, session,
                                                         name);
        }
        return thisChildren;
    }

    @Override
    public Iterable<Node> getChildrenForcingReload(
                                                           final Partition partition,
                                                           final StorageSession session) {
        final Iterable<Node> children =
            ((AbstractStorageSession<?>) session.withPartition(partition)).nodeEntryGetChildren(partition, this);
        childrenWeakReference = new WeakReference<Iterable<Node>>(
                                                                         children);
        return children;
    }

    @Override
    public Iterable<Node> getChildrenNamedForcingReload(
                                                                final Partition partition,
                                                                final StorageSession session,
                                                                final String name) {
        final Iterable<Node> children =
            ((AbstractStorageSession<?>) session.withPartition(partition))
                                                                                                   .nodeEntryGetNamedChildren(
                                                                                                       partition, this, name);
        namedChildrenWeakReference.put(children, name);
        return children;
    }

    @Override
    public Node getParent(final StorageSession session) {
        Node parent = parentWeakReference != null ? parentWeakReference
                                                                              .get() : null;
        if (parent == null) {
            parent = ((AbstractStorageSession<?>) session.withPartition(partition)).nodeEntryGetParent(this);
            parentWeakReference = new WeakReference<Node>(parent);
        }
        return parent;
    }

    @Override
    public void removeNode(final StorageSession session) {
        session.removeNode(this);
    }

    @Override
    public NodeBuilder createWithName(final StorageSession session,
                                              final String name) {
        return ((AbstractStorageSession<?>) session.withPartition(partition)).nodeEntryCreateWithName(this, name);

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final NodeImpl that = (NodeImpl) o;

        if (localKey != null ? !localKey.equals(that.localKey)
                : that.localKey != null) { return false; }
        if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName)
                : that.nodeEntryName != null) { return false; }
        return !(uniqueKey != null ? !uniqueKey.equals(that.uniqueKey)
                : that.uniqueKey != null);
    }

    @Override
    public int hashCode() {
        int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (uniqueKey != null ? uniqueKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "STNodeEntryImpl{" + "partition=" + partition
                + ", nodeEntryName='" + nodeEntryName + '\'' + ", uniqueKey="
                + uniqueKey + '}';
    }

    @Override
    public String getKeyAsString() {
        return getUniqueKey().getKeyAsString();
    }

    @Override
    public Partition getPartition() {
        return getUniqueKey().getPartition();
    }
}
