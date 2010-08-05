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

package org.openspotlight.storage;

import java.util.Set;

import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STLinkEntry;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryFactory;
import org.openspotlight.storage.domain.node.STProperty;

/**
 * This class is an abstraction of a current state of storage session. The
 * implementation classes must not store any kind of connection state. This
 * implementation must not be shared between threads.
 */
public interface STStorageSession {

	public STRepositoryPath getRepositoryPath();

	STPartitionMethods withPartition(STPartition partition);

	interface STPartitionMethods extends STNodeEntryFactory {

		Iterable<String> getAllNodeNames();

		STUniqueKeyBuilder createKey(String nodeEntryName);

		Iterable<STNodeEntry> findByCriteria(STCriteria criteria);

		Iterable<STNodeEntry> findNamed(String nodeEntryName);

		STNodeEntry findUniqueByCriteria(STCriteria criteria);

		public STCriteriaBuilder createCriteria();

		STNodeEntryBuilder createWithName(String name);

		STStorageSessionInternalMethods getInternalMethods();

		STUniqueKey createNewSimpleKey(String... nodePaths);

		STNodeEntry createNewSimpleNode(String... nodePaths);

	}

	void removeNode(
			org.openspotlight.storage.domain.node.STNodeEntry stNodeEntry);

	interface STCriteriaBuilder {

		STCriteriaBuilder withProperty(String propertyName);

		STCriteriaBuilder withNodeEntry(String nodeName);

		STCriteriaBuilder equalsTo(String value);

		STCriteriaBuilder containsString(String value);

		STCriteriaBuilder startsWithString(String value);

		STCriteriaBuilder endsWithString(String value);

		STCriteriaBuilder and();

		STCriteria buildCriteria();

		STCriteriaBuilder withLocalKey(STLocalKey localKey);

		STCriteriaBuilder withUniqueKey(STUniqueKey uniqueKey);

		STCriteriaBuilder withUniqueKeyAsString(String uniqueKeyAsString);
	}

	interface STPropertyCriteriaItem extends STCriteriaItem {

		String getValue();

		String getPropertyName();

	}

	interface STPropertyContainsString extends STCriteriaItem {
		String getValue();

		String getPropertyName();
	}

	interface STPropertyStartsWithString extends STCriteriaItem {
		String getValue();

		String getPropertyName();
	}

	interface STPropertyEndsWithString extends STCriteriaItem {
		String getValue();

		String getPropertyName();
	}

	interface STUniqueKeyCriteriaItem extends STCriteriaItem {
		STUniqueKey getValue();

	}

	interface STUniqueKeyAsStringCriteriaItem extends STCriteriaItem {
		String getKeyAsString();

	}

	interface STLocalKeyCriteriaItem extends STCriteriaItem {
		STLocalKey getValue();
	}

	interface STCriteriaItem {

		String getNodeEntryName();

	}

	interface STCriteria {

		STPartition getPartition();

		String getNodeName();

		Set<STCriteriaItem> getCriteriaItems();

		Iterable<STNodeEntry> andFind(STStorageSession session);

		STNodeEntry andFindUnique(STStorageSession session);

	}

	static enum STFlushMode {
		AUTO, EXPLICIT
	}

	STFlushMode getFlushMode();

	interface STStorageSessionInternalMethods {

		STNodeEntryFactory.STNodeEntryBuilder nodeEntryCreateWithName(
				STNodeEntry stNodeEntry, String name);

		void propertySetProperty(
				org.openspotlight.storage.domain.node.STProperty stProperty,
				byte[] value);

		Set<STProperty> nodeEntryLoadProperties(
				org.openspotlight.storage.domain.node.STNodeEntry stNodeEntry);

		STNodeEntry nodeEntryGetParent(
				org.openspotlight.storage.domain.node.STNodeEntry stNodeEntry);

		Iterable<STNodeEntry> nodeEntryGetChildren(STPartition partition,
				STNodeEntry stNodeEntry);

		byte[] propertyGetValue(STProperty stProperty);

		Iterable<STNodeEntry> nodeEntryGetNamedChildren(STPartition partition,
				STNodeEntry stNodeEntry, String name);
	}

	interface STUniqueKeyBuilder {

		STUniqueKeyBuilder withEntry(String propertyName, String value);

		STUniqueKeyBuilder withParent(STPartition partition,
				String nodeEntryName);

		STUniqueKeyBuilder withParent(String parentId);

		STUniqueKey andCreate();

	}

	void discardTransient();

	void flushTransient();

	STLinkEntry addLink(STNodeEntry origin, STNodeEntry destiny, String name);

	void removeLink(STNodeEntry origin, STNodeEntry destiny, String name);
	
	void removeLink(STLinkEntry link);

	Iterable<STLinkEntry> findLinks(STNodeEntry origin);

	Iterable<STLinkEntry> findLinks(STNodeEntry origin, String name);

	STLinkEntry getLink(STNodeEntry origin, STNodeEntry destiny, String name);

	Iterable<STLinkEntry> findLinks(STNodeEntry origin, STNodeEntry destiny);

}
