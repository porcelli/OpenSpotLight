/*
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

package org.openspotlight.storage.domain.node;

import static com.google.common.collect.Maps.newHashMap;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import com.google.inject.internal.ImmutableSet;

public class STNodeEntryImpl extends STPropertyContainerImpl implements
		STNodeEntry {

	@Override
	protected void verifyBeforeSet(String propertyName) {
		if (localKey.getEntryNames().contains(propertyName))
			throw new IllegalStateException();
	}

	public STNodeEntryImpl(STUniqueKey uniqueKey, Set<STProperty> properties,
			boolean resetTimeout) {
		super(resetTimeout);
		this.nodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
		if (nodeEntryName == null)
			throw new IllegalStateException();
		this.localKey = uniqueKey.getLocalKey();
		this.uniqueKey = uniqueKey;
		propertiesByName = newHashMap();
		if (properties != null) {
			for (STProperty property : properties) {
				this.propertiesByName.put(property.getPropertyName(), property);
			}
		}
		partition = uniqueKey.getPartition();
		namedChildrenWeakReference = new WeakHashMap<Iterable<STNodeEntry>, String>();
	}

	public STNodeEntryImpl(STUniqueKey uniqueKey, boolean resetTimeout) {
		this(uniqueKey, null, resetTimeout);
	}

	private final STPartition partition;

	private WeakReference<STNodeEntry> parentWeakReference = null;

	private WeakReference<Iterable<STNodeEntry>> childrenWeakReference = null;

	private WeakHashMap<Iterable<STNodeEntry>, String> namedChildrenWeakReference;

	private final Map<String, STProperty> propertiesByName;

	private final String nodeEntryName;

	private final STLocalKey localKey;

	private final STUniqueKey uniqueKey;


	public String getNodeEntryName() {
		return nodeEntryName;
	}

	public STLocalKey getLocalKey() {
		return localKey;
	}

	public STUniqueKey getUniqueKey() {
		return uniqueKey;
	}

	public Iterable<STNodeEntry> getChildren(STPartition partition,
			STStorageSession session) {
		Iterable<STNodeEntry> children = childrenWeakReference != null ? childrenWeakReference
				.get()
				: null;
		if (children == null) {
			children = getChildrenForcingReload(partition, session);
		}
		return children;
	}

	public Iterable<STNodeEntry> getChildrenNamed(STPartition partition,
			STStorageSession session, String name) {

		Iterable<STNodeEntry> thisChildren = null;
		if (namedChildrenWeakReference.containsValue(name)) {
			for (Map.Entry<Iterable<STNodeEntry>, String> entry : namedChildrenWeakReference
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

	public Iterable<STNodeEntry> getChildrenForcingReload(
			STPartition partition, STStorageSession session) {
		Iterable<STNodeEntry> children = session.withPartition(partition)
				.getInternalMethods().nodeEntryGetChildren(partition, this);
		childrenWeakReference = new WeakReference<Iterable<STNodeEntry>>(
				children);
		return children;
	}

	public Iterable<STNodeEntry> getChildrenNamedForcingReload(
			STPartition partition, STStorageSession session, String name) {
		Iterable<STNodeEntry> children = session.withPartition(partition)
				.getInternalMethods().nodeEntryGetNamedChildren(partition,
						this, name);
		namedChildrenWeakReference.put(children, name);
		return children;
	}

	public STNodeEntry getParent(STStorageSession session) {
		STNodeEntry parent = parentWeakReference != null ? parentWeakReference
				.get() : null;
		if (parent == null) {
			parent = session.withPartition(partition).getInternalMethods()
					.nodeEntryGetParent(this);
			parentWeakReference = new WeakReference<STNodeEntry>(parent);
		}
		return parent;
	}

	public void removeNode(STStorageSession session) {
		session.removeNode(this);
	}

	public STNodeEntryBuilder createWithName(final STStorageSession session,
			final String name) {
		return session.withPartition(partition).getInternalMethods()
				.nodeEntryCreateWithName(this, name);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		STNodeEntryImpl that = (STNodeEntryImpl) o;

		if (localKey != null ? !localKey.equals(that.localKey)
				: that.localKey != null)
			return false;
		if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName)
				: that.nodeEntryName != null)
			return false;
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
	public STPartition getPartition() {
		return getUniqueKey().getPartition();
	}
}
