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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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

package org.openspotlight.storage.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.SLCollections.iterableToList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STLinkEntry;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.inject.Injector;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 5:08:39 PM
 */
public abstract class AbstractSTStorageSessionTest {

	protected abstract Injector createsAutoFlushInjector();

	protected abstract Injector createsExplicitFlushInjector();

	private boolean didRunOnce = false;

	public void setupInjectors() throws Exception {
		if (supportsAutoFlushInjector())
			this.autoFlushInjector = createsAutoFlushInjector();
		if (supportsExplicitFlushInjector())
			this.explicitFlushInjector = createsExplicitFlushInjector();

	}

	protected enum ExamplePartition implements STPartition {

		DEFAULT("DEFAULT"), FIRST("FIRST"), SECOND("SECOND");

		public static final STPartitionFactory FACTORY = new STPartitionFactory() {

			@Override
			public STPartition getPartitionByName(String name) {
				return ExamplePartition.valueOf(name.toUpperCase());
			}

			public STPartition[] getValues() {
				return ExamplePartition.values();
			}
		};

		private final String partitionName;

		public String getPartitionName() {
			return partitionName;
		}

		private ExamplePartition(String partitionName) {
			this.partitionName = partitionName;
		}
	}

	protected abstract boolean supportsAutoFlushInjector();

	protected abstract boolean supportsExplicitFlushInjector();

	protected abstract boolean supportsAdvancedQueries();

	protected abstract void internalCleanPreviousData() throws Exception;

	protected Injector autoFlushInjector;

	protected Injector explicitFlushInjector;

	@Before
	public void cleanPreviousData() throws Exception {
		setupInjectors();
		internalCleanPreviousData();
	}

	@Test
	public void shouldFindNodeNamesOnDifferentPartitionsOnAutoFlush()
			throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
				"a1", "b1", "c1");
		session.withPartition(ExamplePartition.FIRST).createNewSimpleNode("a2",
				"b2", "c2");
		List<String> nodeNames1 = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).getAllNodeNames());
		List<String> nodeNames2 = iterableToList(session.withPartition(
				ExamplePartition.FIRST).getAllNodeNames());
		assertThat(nodeNames1.contains("a1"), is(true));
		assertThat(nodeNames1.contains("b1"), is(true));
		assertThat(nodeNames1.contains("c1"), is(true));
		assertThat(nodeNames2.contains("a2"), is(true));
		assertThat(nodeNames2.contains("b2"), is(true));
		assertThat(nodeNames2.contains("c2"), is(true));
		assertThat(nodeNames2.contains("a1"), is(false));
		assertThat(nodeNames2.contains("b1"), is(false));
		assertThat(nodeNames2.contains("c1"), is(false));
		assertThat(nodeNames1.contains("a2"), is(false));
		assertThat(nodeNames1.contains("b2"), is(false));
		assertThat(nodeNames1.contains("c2"), is(false));
	}

	@Test
	public void shouldExcludeParentAndChildrenOnExplicitFlush()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry c1 = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a1", "b1", "c1");
		session.flushTransient();
		STNodeEntry b1 = c1.getParent(session);
		STNodeEntry a1 = b1.getParent(session);
		a1.removeNode(session);
		session.flushTransient();
		Iterable<STNodeEntry> foundA1 = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("a1");
		Iterable<STNodeEntry> foundB1 = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("b1");
		Iterable<STNodeEntry> foundC1 = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("c1");
		assertThat(foundA1.iterator().hasNext(), is(false));
		assertThat(foundB1.iterator().hasNext(), is(false));
		assertThat(foundC1.iterator().hasNext(), is(false));

	}

	@Test
	public void shouldExcludeParentAndChildrenOnAutoFlush() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry c1 = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a1", "b1", "c1");
		STNodeEntry b1 = c1.getParent(session);
		STNodeEntry a1 = b1.getParent(session);
		a1.removeNode(session);
		Iterable<STNodeEntry> foundA1 = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("a1");
		Iterable<STNodeEntry> foundB1 = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("b1");
		Iterable<STNodeEntry> foundC1 = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("c1");
		assertThat(foundA1.iterator().hasNext(), is(false));
		assertThat(foundB1.iterator().hasNext(), is(false));
		assertThat(foundC1.iterator().hasNext(), is(false));

	}

	@Test
	public void shouldFindNodeNamesOnDifferentPartitionsOnExplicitFlush()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
				"a1", "b1", "c1");
		session.withPartition(ExamplePartition.FIRST).createNewSimpleNode("a2",
				"b2", "c2");
		session.flushTransient();
		List<String> nodeNames1 = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).getAllNodeNames());
		List<String> nodeNames2 = iterableToList(session.withPartition(
				ExamplePartition.FIRST).getAllNodeNames());
		assertThat(nodeNames1.contains("a1"), is(true));
		assertThat(nodeNames1.contains("b1"), is(true));
		assertThat(nodeNames1.contains("c1"), is(true));
		assertThat(nodeNames2.contains("a2"), is(true));
		assertThat(nodeNames2.contains("b2"), is(true));
		assertThat(nodeNames2.contains("c2"), is(true));
		assertThat(nodeNames2.contains("a1"), is(false));
		assertThat(nodeNames2.contains("b1"), is(false));
		assertThat(nodeNames2.contains("c1"), is(false));
		assertThat(nodeNames1.contains("a2"), is(false));
		assertThat(nodeNames1.contains("b2"), is(false));
		assertThat(nodeNames1.contains("c2"), is(false));
	}

	@Test
	public void shouldSaveSimpleNodesOnAutoFlush() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
				"a", "b", "c");
		Iterable<STNodeEntry> result = session.withPartition(
				ExamplePartition.DEFAULT).findNamed("c");
		assertThat(result.iterator().hasNext(), is(true));

	}

	@Test
	public void shouldFindSimpleNodeWithStringIdOnAutoFlush() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a", "b", "c");
		String nodeIdAsString = newNode.getUniqueKey().getKeyAsString();
		STNodeEntry result = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withUniqueKeyAsString(nodeIdAsString)
				.buildCriteria().andFindUnique(session);
		assertThat(result, is(newNode));

	}

	@Test
	public void shouldFindSimpleNodeWithStringIdOnExplicitFlush()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a", "b", "c");
		session.flushTransient();
		String nodeIdAsString = newNode.getUniqueKey().getKeyAsString();
		STNodeEntry result = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withUniqueKeyAsString(nodeIdAsString)
				.buildCriteria().andFindUnique(session);
		assertThat(result, is(newNode));

	}

	@Test
	public void shouldInstantiateOneSessionPerThread() throws Exception {
		STStorageSession session1 = autoFlushInjector
				.getInstance(STStorageSession.class);
		STStorageSession session2 = autoFlushInjector
				.getInstance(STStorageSession.class);
		assertThat(session1, is(session2));

		final List<STStorageSession> sessions = new CopyOnWriteArrayList<STStorageSession>();
		final CountDownLatch latch = new CountDownLatch(1);

		new Thread() {
			@Override
			public void run() {
				try {
					sessions.add(autoFlushInjector
							.getInstance(STStorageSession.class));
				} finally {
					latch.countDown();
				}
			}
		}.start();
		latch.await(5, TimeUnit.SECONDS);
		assertThat(sessions.size(), is(1));
		assertThat(session1, is(not(sessions.get(0))));
	}

	@Test
	public void shouldCreateTheSameKey() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry aNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry sameNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		String aKeyAsString = aNode.getUniqueKey().getKeyAsString();
		String sameKeyAsString = sameNode.getUniqueKey().getKeyAsString();
		assertThat(aKeyAsString, is(sameKeyAsString));

	}

	@Test
	public void shouldFindByUniqueKey() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry aNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry theSameNode = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withUniqueKey(
				aNode.getUniqueKey()).buildCriteria().andFindUnique(session);
		assertThat(aNode, is(theSameNode));
		assertThat(theSameNode.getProperty(session, "name").getValueAsString(
				session), is("name"));
		STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withUniqueKey(
						session.withPartition(ExamplePartition.DEFAULT)
								.createKey("invalid").andCreate())
				.buildCriteria().andFindUnique(session);
		assertThat(nullNode, is(nullValue()));

	}

	@Test
	public void shouldFindByLocalKey() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root2").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root1).andCreate();
		STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root2).andCreate();

		List<STNodeEntry> theSameNodes = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withLocalKey(
				aNode1.getUniqueKey().getLocalKey()).buildCriteria().andFind(
				session));
		assertThat(theSameNodes.size(), is(2));
		assertThat(theSameNodes.contains(aNode1), is(true));
		assertThat(theSameNodes.contains(aNode2), is(true));
		assertThat(theSameNodes.contains(root1), is(false));
		assertThat(theSameNodes.contains(root2), is(false));
	}

	@Test
	public void shouldFindByProperties() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root2").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root1).andCreate();
		STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root2).andCreate();
		aNode1.setIndexedProperty(session, "parameter", "value");
		aNode2.setIndexedProperty(session, "parameter", "value");
		aNode1.setIndexedProperty(session, "parameter1", "value1");
		aNode2.setIndexedProperty(session, "parameter1", "value2");
		List<STNodeEntry> theSameNodes = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("node").withProperty("parameter").equalsTo(
						"value").buildCriteria().andFind(session));
		assertThat(theSameNodes.size(), is(2));

		assertThat(theSameNodes.contains(aNode1), is(true));
		assertThat(theSameNodes.contains(aNode2), is(true));
		assertThat(theSameNodes.contains(root1), is(false));
		assertThat(theSameNodes.contains(root2), is(false));
		List<STNodeEntry> onlyOneNode = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("node").withProperty("parameter1").equalsTo(
						"value1").buildCriteria().andFind(session));
		assertThat(onlyOneNode.size(), is(1));
		assertThat(onlyOneNode.contains(aNode1), is(true));
		assertThat(onlyOneNode.contains(aNode2), is(false));
		assertThat(onlyOneNode.contains(root1), is(false));
		assertThat(onlyOneNode.contains(root2), is(false));
	}

	@Test
	public void shouldFindByPropertiesContainingString() throws Exception {
		if (supportsAdvancedQueries()) {
			STStorageSession session = autoFlushInjector
					.getInstance(STStorageSession.class);
			STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("node").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name1").andCreate();
			STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("node").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name2").andCreate();
			STNodeEntry aNode1 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"node").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name1").withParent(root1).andCreate();
			STNodeEntry aNode2 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"node").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name2").withParent(root2).andCreate();
			aNode1.setIndexedProperty(session, "parameter", "io");
			aNode2.setIndexedProperty(session, "parameter", "aeiou");
			root1.setIndexedProperty(session, "parameter", "foo");
			root2.setIndexedProperty(session, "parameter", "bar");
			List<STNodeEntry> theSameNodes = iterableToList(session
					.withPartition(ExamplePartition.DEFAULT).createCriteria()
					.withNodeEntry("node").withProperty("parameter")
					.containsString("io").buildCriteria().andFind(session));
			assertThat(theSameNodes.size(), is(2));
			assertThat(theSameNodes.contains(aNode1), is(true));
			assertThat(theSameNodes.contains(aNode2), is(true));
			assertThat(theSameNodes.contains(root1), is(false));
			assertThat(theSameNodes.contains(root2), is(false));
		}
	}

	@Test
	public void shouldFindByPropertiesWithNullValue() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "a").andCreate();
		STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "2")
				.withKeyEntry("name", "b").andCreate();
		STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name1").withParent(root1).andCreate();
		STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name2").withParent(root2).andCreate();
		aNode1.setIndexedProperty(session, "parameter", "io");
		aNode2.setIndexedProperty(session, "parameter", "aeiou");
		root1.setIndexedProperty(session, "parameter", null);
		root2.setIndexedProperty(session, "parameter", null);
		List<STNodeEntry> theSameNodes = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("node").withProperty("parameter").equalsTo(null)
				.buildCriteria().andFind(session));
		assertThat(theSameNodes.size(), is(2));
		assertThat(theSameNodes.contains(root1), is(true));
		assertThat(theSameNodes.contains(root2), is(true));
		assertThat(theSameNodes.contains(aNode1), is(false));
		assertThat(theSameNodes.contains(aNode2), is(false));
	}

	@Test
	public void shouldFindByPropertiesStartingWithString() throws Exception {
		if (supportsAdvancedQueries()) {
			STStorageSession session = autoFlushInjector
					.getInstance(STStorageSession.class);
			STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("node").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name1").andCreate();
			STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("node").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name2").andCreate();
			STNodeEntry aNode1 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"node").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name1").withParent(root1).andCreate();
			STNodeEntry aNode2 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"node").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name2").withParent(root2).andCreate();
			aNode1.setIndexedProperty(session, "parameter", "io");
			aNode2.setIndexedProperty(session, "parameter", "iou");
			root1.setIndexedProperty(session, "parameter", "fooiou");
			root2.setIndexedProperty(session, "parameter", "baior");
			List<STNodeEntry> theSameNodes = iterableToList(session
					.withPartition(ExamplePartition.DEFAULT).createCriteria()
					.withNodeEntry("node").withProperty("parameter")
					.startsWithString("io").buildCriteria().andFind(session));
			assertThat(theSameNodes.contains(aNode1), is(true));
			assertThat(theSameNodes.contains(aNode2), is(true));
			assertThat(theSameNodes.contains(root1), is(false));
			assertThat(theSameNodes.contains(root2), is(false));
			assertThat(theSameNodes.size(), is(2));
		}
	}

	@Test
	public void shouldFindByPropertiesEndingWithString() throws Exception {
		if (supportsAdvancedQueries()) {
			STStorageSession session = autoFlushInjector
					.getInstance(STStorageSession.class);
			STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("node").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name1").andCreate();
			STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("node").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name2").andCreate();
			STNodeEntry aNode1 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"node").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name1").withParent(root1).andCreate();
			STNodeEntry aNode2 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"node").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name2").withParent(root2).andCreate();
			aNode1.setIndexedProperty(session, "parameter", "io");
			aNode2.setIndexedProperty(session, "parameter", "uio");
			root1.setIndexedProperty(session, "parameter", "fooiou");
			root2.setIndexedProperty(session, "parameter", "baior");
			List<STNodeEntry> theSameNodes = iterableToList(session
					.withPartition(ExamplePartition.DEFAULT).createCriteria()
					.withNodeEntry("node").withProperty("parameter")
					.endsWithString("io").buildCriteria().andFind(session));
			assertThat(theSameNodes.contains(aNode1), is(true));
			assertThat(theSameNodes.contains(aNode2), is(true));
			assertThat(theSameNodes.contains(root1), is(false));
			assertThat(theSameNodes.contains(root2), is(false));
			assertThat(theSameNodes.size(), is(2));
		}
	}

	@Test
	public void shouldFindByLocalKeyAndProperties() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root2").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root1).andCreate();
		STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root2).andCreate();
		aNode1.setIndexedProperty(session, "parameter", "value");
		aNode2.setIndexedProperty(session, "parameter", "value");
		aNode1.setIndexedProperty(session, "parameter1", "value1");
		aNode2.setIndexedProperty(session, "parameter1", "value2");
		List<STNodeEntry> theSameNodes = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withLocalKey(
				aNode1.getUniqueKey().getLocalKey()).withProperty("parameter")
				.equalsTo("value").buildCriteria().andFind(session));
		assertThat(theSameNodes.size(), is(2));
		assertThat(theSameNodes.contains(aNode1), is(true));
		assertThat(theSameNodes.contains(aNode2), is(true));
		assertThat(theSameNodes.contains(root1), is(false));
		assertThat(theSameNodes.contains(root2), is(false));
		List<STNodeEntry> onlyOneNode = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withLocalKey(
				aNode1.getUniqueKey().getLocalKey()).withProperty("parameter1")
				.equalsTo("value1").buildCriteria().andFind(session));
		assertThat(onlyOneNode.size(), is(1));
		assertThat(onlyOneNode.contains(aNode1), is(true));
		assertThat(onlyOneNode.contains(aNode2), is(false));
		assertThat(onlyOneNode.contains(root1), is(false));
		assertThat(onlyOneNode.contains(root2), is(false));
	}

	@Test
	public void shouldFindNamedNodes() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root2").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root1).andCreate();
		STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("node").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root2).andCreate();

		List<STNodeEntry> onlyOneNode = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("root1"));
		assertThat(onlyOneNode.size(), is(1));
		assertThat(onlyOneNode.contains(aNode1), is(false));
		assertThat(onlyOneNode.contains(aNode2), is(false));
		assertThat(onlyOneNode.contains(root1), is(true));
		assertThat(onlyOneNode.contains(root2), is(false));
		List<STNodeEntry> twoNodes = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("node"));
		assertThat(twoNodes.size(), is(2));
		assertThat(twoNodes.contains(aNode1), is(true));
		assertThat(twoNodes.contains(aNode2), is(true));
		assertThat(twoNodes.contains(root1), is(false));
		assertThat(twoNodes.contains(root2), is(false));

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenFindingWithUniqueAndOtherAttributes()
			throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		session.withPartition(ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("newNode1").withProperty("sequence").equalsTo(
						"1").withProperty("name").equalsTo("name")
				.withUniqueKey(
						session.withPartition(ExamplePartition.DEFAULT)
								.createKey("sample").andCreate())
				.buildCriteria().andFindUnique(session);
	}

	@Test
	public void shouldInsertNewNodeEntryAndFindUniqueWithAutoFlush() {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry foundNewNode1 = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("sequence").equalsTo("1")
				.withProperty("name").equalsTo("name").buildCriteria()
				.andFindUnique(session);
		assertThat(foundNewNode1, is(nullValue()));

		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withNodeEntry("newNode1").withProperty(
						"sequence").equalsTo("1").withProperty("name")
				.equalsTo("name").buildCriteria().andFindUnique(session);
		assertThat(foundNewNode1, is(notNullValue()));
		assertThat(foundNewNode1, is(newNode1));
	}

	@Test
	public void shouldInsertNewNodeEntryAndFindUniqueWithExplicitFlush() {

		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry foundNewNode1 = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("sequence").equalsTo("1")
				.withProperty("name").equalsTo("name").buildCriteria()
				.andFindUnique(session);
		assertThat(foundNewNode1, is(nullValue()));

		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withNodeEntry("newNode1").withProperty(
						"sequence").equalsTo("1").withProperty("name")
				.equalsTo("name").buildCriteria().andFindUnique(session);
		assertThat(foundNewNode1, is(nullValue()));
		session.flushTransient();
		STNodeEntry foundNewNode2 = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("sequence").equalsTo("1")
				.withProperty("name").equalsTo("name").buildCriteria()
				.andFindUnique(session);
		assertThat(foundNewNode2, is(notNullValue()));
		assertThat(foundNewNode2, is(newNode1));
	}

	@Test
	public void shouldCreateHierarchyAndLoadParentNode() {

		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);

		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("sameName").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("sameName").withParent(newNode1).withKeyEntry(
						"sequence", "1").withKeyEntry("name", "name")
				.andCreate();
		STNodeEntry newNode3 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("sameName").withParent(newNode2).withKeyEntry(
						"sequence", "3").withKeyEntry("name", "name")
				.andCreate();

		STNodeEntry foundNewNode3 = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"sameName").withProperty("sequence").equalsTo("3")
				.withProperty("name").equalsTo("name").buildCriteria()
				.andFindUnique(session);
		assertThat(foundNewNode3, is(notNullValue()));
		STNodeEntry foundNewNode2 = foundNewNode3.getParent(session);
		STNodeEntry foundNewNode1 = foundNewNode2.getParent(session);
		assertThat(foundNewNode3, is(newNode3));
		assertThat(foundNewNode2, is(newNode2));
		assertThat(foundNewNode1, is(newNode1));
		assertThat(foundNewNode1.getPropertyAsString(session, "name"),
				is("name"));
		assertThat(foundNewNode2.getPropertyAsString(session, "name"),
				is("name"));
		assertThat(foundNewNode3.getPropertyAsString(session, "name"),
				is("name"));
		assertThat(foundNewNode1.getPropertyAsString(session, "sequence"),
				is("1"));
		assertThat(foundNewNode2.getPropertyAsString(session, "sequence"),
				is("1"));
		assertThat(foundNewNode3.getPropertyAsString(session, "sequence"),
				is("3"));
		assertThat(foundNewNode1.getNodeEntryName(), is("sameName"));
		assertThat(foundNewNode2.getNodeEntryName(), is("sameName"));
		assertThat(foundNewNode3.getNodeEntryName(), is("sameName"));

	}

	@Test
	public void shouldCreateHierarchyAndLoadChildrenNodes() {

		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);

		STNodeEntry root = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("root").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry child1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("child").withParent(root).withKeyEntry(
						"sequence", "1").withKeyEntry("name", "name")
				.andCreate();
		STNodeEntry child2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("child").withParent(root).withKeyEntry(
						"sequence", "2").withKeyEntry("name", "name")
				.andCreate();
		STNodeEntry child3 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("child").withParent(root).withKeyEntry(
						"sequence", "3").withKeyEntry("name", "name")
				.andCreate();
		STNodeEntry child4 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("child").withParent(root).withKeyEntry(
						"sequence", "4").withKeyEntry("name", "name")
				.andCreate();
		STNodeEntry childAnotherType1 = session.withPartition(
				ExamplePartition.DEFAULT).createWithName("childAnotherType")
				.withParent(root).withKeyEntry("sequence", "1").withKeyEntry(
						"name", "name").andCreate();
		STNodeEntry childAnotherType2 = session.withPartition(
				ExamplePartition.DEFAULT).createWithName("childAnotherType")
				.withParent(root).withKeyEntry("sequence", "2").withKeyEntry(
						"name", "name").andCreate();
		STNodeEntry childAnotherType3 = session.withPartition(
				ExamplePartition.DEFAULT).createWithName("childAnotherType")
				.withParent(root).withKeyEntry("sequence", "3").withKeyEntry(
						"name", "name").andCreate();
		STNodeEntry childAnotherType4 = session.withPartition(
				ExamplePartition.DEFAULT).createWithName("childAnotherType")
				.withParent(root).withKeyEntry("sequence", "4").withKeyEntry(
						"name", "name").andCreate();

		List<STNodeEntry> allChildren = iterableToList(root.getChildren(
				ExamplePartition.DEFAULT, session));
		assertThat(allChildren.size(), is(8));
		assertThat(allChildren.contains(child1), is(true));
		assertThat(allChildren.contains(child2), is(true));
		assertThat(allChildren.contains(child3), is(true));
		assertThat(allChildren.contains(child4), is(true));
		assertThat(allChildren.contains(childAnotherType1), is(true));
		assertThat(allChildren.contains(childAnotherType2), is(true));
		assertThat(allChildren.contains(childAnotherType3), is(true));
		assertThat(allChildren.contains(childAnotherType4), is(true));

		List<STNodeEntry> childrenType2 = iterableToList(root.getChildrenNamed(
				ExamplePartition.DEFAULT, session, "childAnotherType"));

		assertThat(childrenType2.size(), is(4));
		assertThat(childrenType2.contains(childAnotherType1), is(true));
		assertThat(childrenType2.contains(childAnotherType2), is(true));
		assertThat(childrenType2.contains(childAnotherType3), is(true));
		assertThat(childrenType2.contains(childAnotherType4), is(true));

		List<STNodeEntry> childrenType1 = iterableToList(root.getChildrenNamed(
				ExamplePartition.DEFAULT, session, "child"));

		assertThat(childrenType1.size(), is(4));
		assertThat(childrenType1.contains(child1), is(true));
		assertThat(childrenType1.contains(child2), is(true));
		assertThat(childrenType1.contains(child3), is(true));
		assertThat(childrenType1.contains(child4), is(true));

	}

	@Test
	public void shouldWorkWithPartitions() {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);

		session.withPartition(ExamplePartition.DEFAULT).createWithName("root")
				.withKeyEntry("sequence", "1").withKeyEntry("name", "name")
				.andCreate();
		session.withPartition(ExamplePartition.FIRST).createWithName("root")
				.withKeyEntry("sequence", "1").withKeyEntry("name", "name")
				.andCreate();
		session.withPartition(ExamplePartition.SECOND).createWithName("root")
				.withKeyEntry("sequence", "1").withKeyEntry("name", "name")
				.andCreate();
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withUniqueKey(
						session.withPartition(ExamplePartition.DEFAULT)
								.createKey("root").withEntry("sequence", "1")
								.withEntry("name", "name").andCreate())
				.buildCriteria().andFindUnique(session);

		STNodeEntry root2 = session.withPartition(ExamplePartition.FIRST)
				.createCriteria().withUniqueKey(
						session.withPartition(ExamplePartition.FIRST)
								.createKey("root").withEntry("sequence", "1")
								.withEntry("name", "name").andCreate())
				.buildCriteria().andFindUnique(session);

		STNodeEntry root3 = session.withPartition(ExamplePartition.SECOND)
				.createCriteria().withUniqueKey(
						session.withPartition(ExamplePartition.SECOND)
								.createKey("root").withEntry("sequence", "1")
								.withEntry("name", "name").andCreate())
				.buildCriteria().andFindUnique(session);

		assertThat(root1, is(notNullValue()));

		assertThat(root2, is(notNullValue()));

		assertThat(root3, is(notNullValue()));

		assertThat(root1, is(not(root2)));

		assertThat(root2, is(not(root3)));

		List<STNodeEntry> list1 = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("root"));
		List<STNodeEntry> list2 = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("root"));
		List<STNodeEntry> list3 = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("root"));

		assertThat(list1.size(), is(1));
		assertThat(list2.size(), is(1));
		assertThat(list3.size(), is(1));
	}

	@Test
	public void shouldWorkWithSimplePropertiesOnExplicitFlush()
			throws Exception {

		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();

		STNodeEntry loadedNode = session
				.withPartition(ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("newNode1").withProperty("sequence").equalsTo(
						"1").withProperty("name").equalsTo("name")
				.buildCriteria().andFindUnique(session);

		assertThat(loadedNode, is(nullValue()));

		session.flushTransient();

		STNodeEntry loadedNode1 = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("sequence").equalsTo("1")
				.withProperty("name").equalsTo("name").buildCriteria()
				.andFindUnique(session);

		assertThat(loadedNode1, is(notNullValue()));

		newNode.setSimpleProperty(session, "stringProperty", "value");

		assertThat(newNode.getProperty(session, "stringProperty")
				.getInternalMethods().getTransientValueAsString(session),
				is("value"));

		assertThat(loadedNode1.getPropertyAsString(session, "stringProperty"),
				is(nullValue()));

		session.flushTransient();
		STNodeEntry loadedNode2 = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("sequence").equalsTo("1")
				.withProperty("name").equalsTo("name").buildCriteria()
				.andFindUnique(session);

		assertThat(loadedNode1.getPropertyAsString(session, "stringProperty"),
				is(nullValue()));

		assertThat(loadedNode2.getPropertyAsString(session, "stringProperty"),
				is("value"));

		loadedNode1.forceReload();

		assertThat(loadedNode1.getPropertyAsString(session, "stringProperty"),
				is("value"));

	}

	public enum ExampleEnum {
		FIRST
	}

	@Test
	public void shouldWorkWithSimplePropertiesOnAutoFlush() throws Exception {

		Date newDate = new Date();
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		newNode.setIndexedProperty(session, "stringProperty", "value");

		STNodeEntry loadedNode = session
				.withPartition(ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("newNode1").withProperty("sequence").equalsTo(
						"1").withProperty("name").equalsTo("name")
				.buildCriteria().andFindUnique(session);

		assertThat(newNode.getProperty(session, "stringProperty")
				.getInternalMethods().getTransientValueAsString(session),
				is("value"));

		assertThat(newNode.getPropertyAsString(session, "stringProperty"),
				is("value"));

		assertThat(loadedNode.getPropertyAsString(session, "stringProperty"),
				is("value"));

		STNodeEntry anotherLoadedNode = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("stringProperty").equalsTo("value")
				.buildCriteria().andFindUnique(session);

		assertThat(anotherLoadedNode, is(loadedNode));

		STNodeEntry noLoadedNode = session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("stringProperty").equalsTo("invalid")
				.buildCriteria().andFindUnique(session);

		assertThat(noLoadedNode, is(nullValue()));
	}

	public static class PojoClass implements Serializable {

		private String aString;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			PojoClass pojoClass = (PojoClass) o;

			if (anInt != pojoClass.anInt)
				return false;
			if (aString != null ? !aString.equals(pojoClass.aString)
					: pojoClass.aString != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = aString != null ? aString.hashCode() : 0;
			result = 31 * result + anInt;
			return result;
		}

		public String getaString() {
			return aString;
		}

		public void setaString(String aString) {
			this.aString = aString;
		}

		public int getAnInt() {
			return anInt;
		}

		public void setAnInt(int anInt) {
			this.anInt = anInt;
		}

		private int anInt;

	}

	@Test
	public void shouldWorkWithInputStreamPropertiesOnExplicitFlush()
			throws Exception {

		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();

		InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

		newNode.setSimpleProperty(session, "streamProperty", stream);

		STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT)
				.createCriteria().withNodeEntry("newNode1").withProperty(
						"sequence").equalsTo("1").withProperty("name")
				.equalsTo("name").buildCriteria().andFindUnique(session);

		assertThat(nullNode, is(nullValue()));
		session.flushTransient();
		STNodeEntry loadedNode = session
				.withPartition(ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("newNode1").withProperty("sequence").equalsTo(
						"1").withProperty("name").equalsTo("name")
				.buildCriteria().andFindUnique(session);

		stream.reset();
		assertThat(IOUtils.contentEquals(newNode.getPropertyAsStream(session,
				"streamProperty"), stream), is(true));

		InputStream loaded1 = loadedNode.getPropertyAsStream(session,
				"streamProperty");

		ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
		IOUtils.copy(loaded1, temporary1);
		String asString1 = new String(temporary1.toByteArray());
		ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
		InputStream loaded2 = loadedNode.getPropertyAsStream(session,
				"streamProperty");

		IOUtils.copy(loaded2, temporary2);
		String asString2 = new String(temporary2.toByteArray());
		assertThat(asString1, is("streamValue"));
		assertThat(asString2, is("streamValue"));
	}

	@Test
	public void shouldWorkWithInputStreamPropertiesOnAutoFlush()
			throws Exception {

		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();

		InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

		newNode.setSimpleProperty(session, "streamProperty", stream);

		STNodeEntry loadedNode = session
				.withPartition(ExamplePartition.DEFAULT).createCriteria()
				.withNodeEntry("newNode1").withProperty("sequence").equalsTo(
						"1").withProperty("name").equalsTo("name")
				.buildCriteria().andFindUnique(session);

		stream.reset();
		assertThat(IOUtils.contentEquals(newNode.getPropertyAsStream(session,
				"streamProperty"), stream), is(true));

		InputStream loaded1 = loadedNode.getPropertyAsStream(session,
				"streamProperty");

		ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
		IOUtils.copy(loaded1, temporary1);
		String asString1 = new String(temporary1.toByteArray());
		ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
		InputStream loaded2 = loadedNode.getPropertyAsStream(session,
				"streamProperty");

		IOUtils.copy(loaded2, temporary2);
		String asString2 = new String(temporary2.toByteArray());
		assertThat(asString1, is("streamValue"));
		assertThat(asString2, is("streamValue"));

	}

	@Test
	public void shouldFindMultipleResults() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "2")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry newNode3 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "another name").andCreate();
		STNodeEntry newNode4 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("anotherName").withKeyEntry("sequence", "2")
				.withKeyEntry("name", "name").andCreate();
		List<STNodeEntry> result = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("name").equalsTo("name")
				.buildCriteria().andFind(session));

		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(2));
		assertThat(result.contains(newNode1), is(true));
		assertThat(result.contains(newNode2), is(true));
		assertThat(result.contains(newNode3), is(false));
		assertThat(result.contains(newNode4), is(false));

	}

	@Test
	public void shouldRemoveNodesOnAutoFlush() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "2")
				.withKeyEntry("name", "name").andCreate();

		List<STNodeEntry> result = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(result.size(), is(2));
		assertThat(result.contains(newNode1), is(true));
		assertThat(result.contains(newNode2), is(true));

		newNode1.removeNode(session);
		List<STNodeEntry> newResult = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));
		assertThat(newResult.size(), is(1));
		assertThat(newResult.contains(newNode1), is(false));
		assertThat(newResult.contains(newNode2), is(true));

		newNode2.removeNode(session);
		newResult = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));
		assertThat(newResult.size(), is(0));

	}

	@Test
	public void shouldRemoveNodesOnExplicitFlush() throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "2")
				.withKeyEntry("name", "name").andCreate();
		session.flushTransient();
		List<STNodeEntry> result = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(result.size(), is(2));
		assertThat(result.contains(newNode1), is(true));
		assertThat(result.contains(newNode2), is(true));

		newNode1.removeNode(session);
		List<STNodeEntry> resultNotChanged = iterableToList(session
				.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(resultNotChanged.size(), is(2));
		assertThat(resultNotChanged.contains(newNode1), is(true));
		assertThat(resultNotChanged.contains(newNode2), is(true));

		session.flushTransient();

		List<STNodeEntry> newResult = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));
		assertThat(newResult.size(), is(1));
		assertThat(newResult.contains(newNode1), is(false));
		assertThat(newResult.contains(newNode2), is(true));

		newNode2.removeNode(session);
		session.flushTransient();
		newResult = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));
		assertThat(newResult.size(), is(0));

	}

	@Test
	public void shouldDiscardTransientNodesOnExplicitFlush() throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "2")
				.withKeyEntry("name", "name").andCreate();
		session.flushTransient();
		List<STNodeEntry> result = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(result.size(), is(2));
		assertThat(result.contains(newNode1), is(true));
		assertThat(result.contains(newNode2), is(true));

		newNode1.removeNode(session);
		List<STNodeEntry> resultNotChanged = iterableToList(session
				.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(resultNotChanged.size(), is(2));
		assertThat(resultNotChanged.contains(newNode1), is(true));
		assertThat(resultNotChanged.contains(newNode2), is(true));

		session.discardTransient();

		List<STNodeEntry> resultStillNotChanged = iterableToList(session
				.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(resultStillNotChanged.size(), is(2));
		assertThat(resultStillNotChanged.contains(newNode1), is(true));
		assertThat(resultStillNotChanged.contains(newNode2), is(true));

		session.flushTransient();
		List<STNodeEntry> resultNotChangedAgain = iterableToList(session
				.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1"));

		assertThat(resultNotChangedAgain.size(), is(2));
		assertThat(resultNotChangedAgain.contains(newNode1), is(true));
		assertThat(resultNotChangedAgain.contains(newNode2), is(true));

	}

	@Test
	public void shouldUpdatePropertyAndFindWithUpdatedValue() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		newNode1.setIndexedProperty(session, "parameter", "firstValue");
		List<STNodeEntry> found = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("parameter").equalsTo("firstValue")
				.buildCriteria().andFind(session));
		assertThat(found.size(), is(1));
		assertThat(found.contains(newNode1), is(true));
		newNode1.getProperty(session, "parameter").setStringValue(session,
				"secondValue");

		List<STNodeEntry> notFound = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("parameter").equalsTo("firstValue")
				.buildCriteria().andFind(session));
		assertThat(notFound.size(), is(0));

		List<STNodeEntry> foundAgain = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
				"newNode1").withProperty("parameter").equalsTo("secondValue")
				.buildCriteria().andFind(session));
		assertThat(foundAgain.size(), is(1));
		assertThat(foundAgain.contains(newNode1), is(true));

	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotSetKeyProperty() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("newNode1").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		newNode1.setSimpleProperty(session, "sequence", "3");

	}

	@Test
	public void shouldFindByPropertiesWithoutNodeName() throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("abc").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("def").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").andCreate();
		STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("ghi").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root1).andCreate();
		STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT)
				.createWithName("jkl").withKeyEntry("sequence", "1")
				.withKeyEntry("name", "name").withParent(root2).andCreate();
		aNode1.setIndexedProperty(session, "parameter", "value");
		aNode2.setIndexedProperty(session, "parameter", "value");
		aNode1.setIndexedProperty(session, "parameter1", "value1");
		aNode2.setIndexedProperty(session, "parameter1", "value2");
		List<STNodeEntry> theSameNodes = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withProperty(
				"parameter").equalsTo("value").buildCriteria().andFind(session));
		assertThat(theSameNodes.size(), is(2));
		assertThat(theSameNodes.contains(aNode1), is(true));
		assertThat(theSameNodes.contains(aNode2), is(true));
		assertThat(theSameNodes.contains(root1), is(false));
		assertThat(theSameNodes.contains(root2), is(false));
		List<STNodeEntry> onlyOneNode = iterableToList(session.withPartition(
				ExamplePartition.DEFAULT).createCriteria().withProperty(
				"parameter1").equalsTo("value1").buildCriteria().andFind(
				session));
		assertThat(onlyOneNode.size(), is(1));
		assertThat(onlyOneNode.contains(aNode1), is(true));
		assertThat(onlyOneNode.contains(aNode2), is(false));
		assertThat(onlyOneNode.contains(root1), is(false));
		assertThat(onlyOneNode.contains(root2), is(false));
	}

	@Test
	public void shouldFindByPropertiesContainingStringWithoutNodeName()
			throws Exception {
		if (supportsAdvancedQueries()) {
			STStorageSession session = autoFlushInjector
					.getInstance(STStorageSession.class);
			STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("abc").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name1").andCreate();
			STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("def").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name2").andCreate();
			STNodeEntry aNode1 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"ghi").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name1").withParent(root1).andCreate();
			STNodeEntry aNode2 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"jkl").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name2").withParent(root2).andCreate();
			aNode1.setIndexedProperty(session, "parameter", "io");
			aNode2.setIndexedProperty(session, "parameter", "aeiou");
			root1.setIndexedProperty(session, "parameter", "foo");
			root2.setIndexedProperty(session, "parameter", "bar");
			List<STNodeEntry> theSameNodes = iterableToList(session
					.withPartition(ExamplePartition.DEFAULT).createCriteria()
					.withProperty("parameter").containsString("io")
					.buildCriteria().andFind(session));
			assertThat(theSameNodes.contains(aNode1), is(true));
			assertThat(theSameNodes.contains(aNode2), is(true));
			assertThat(theSameNodes.contains(root1), is(false));
			assertThat(theSameNodes.contains(root2), is(false));
			assertThat(theSameNodes.size(), is(2));
		}
	}

	@Test
	public void shouldFindByPropertiesStartingWithStringWithoutNodeName()
			throws Exception {
		if (supportsAdvancedQueries()) {
			STStorageSession session = autoFlushInjector
					.getInstance(STStorageSession.class);
			STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("abc").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name1").andCreate();
			STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("def").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name2").andCreate();
			STNodeEntry aNode1 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"ghi").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name1").withParent(root1).andCreate();
			STNodeEntry aNode2 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"jkl").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name2").withParent(root2).andCreate();
			aNode1.setIndexedProperty(session, "parameter", "io");
			aNode2.setIndexedProperty(session, "parameter", "iou");
			root1.setIndexedProperty(session, "parameter", "fooiou");
			root2.setIndexedProperty(session, "parameter", "baior");
			List<STNodeEntry> theSameNodes = iterableToList(session
					.withPartition(ExamplePartition.DEFAULT).createCriteria()
					.withProperty("parameter").startsWithString("io")
					.buildCriteria().andFind(session));
			assertThat(theSameNodes.contains(aNode1), is(true));
			assertThat(theSameNodes.contains(aNode2), is(true));
			assertThat(theSameNodes.contains(root1), is(false));
			assertThat(theSameNodes.contains(root2), is(false));
			assertThat(theSameNodes.size(), is(2));
		}
	}

	@Test
	public void shouldFindByPropertiesEndingWithStringWithoutNodeName()
			throws Exception {
		if (supportsAdvancedQueries()) {
			STStorageSession session = autoFlushInjector
					.getInstance(STStorageSession.class);
			STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("abc").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name1").andCreate();
			STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT)
					.createWithName("def").withKeyEntry("sequence", "1")
					.withKeyEntry("name", "name2").andCreate();
			STNodeEntry aNode1 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"ghi").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name1").withParent(root1).andCreate();
			STNodeEntry aNode2 = session
					.withPartition(ExamplePartition.DEFAULT).createWithName(
							"jkl").withKeyEntry("sequence", "1").withKeyEntry(
							"name", "name2").withParent(root2).andCreate();
			aNode1.setIndexedProperty(session, "parameter", "io");
			aNode2.setIndexedProperty(session, "parameter", "uio");
			root1.setIndexedProperty(session, "parameter", "fooiou");
			root2.setIndexedProperty(session, "parameter", "baior");
			List<STNodeEntry> theSameNodes = iterableToList(session
					.withPartition(ExamplePartition.DEFAULT).createCriteria()
					.withProperty("parameter").endsWithString("io")
					.buildCriteria().andFind(session));
			assertThat(theSameNodes.contains(aNode1), is(true));
			assertThat(theSameNodes.contains(aNode2), is(true));
			assertThat(theSameNodes.contains(root1), is(false));
			assertThat(theSameNodes.contains(root2), is(false));
			assertThat(theSameNodes.size(), is(2));
		}
	}

	@Test
	public void shouldAddAndRetriveLinksOnSamePartitionWithAutoFlushInjector()
			throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry c = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a", "b", "c");
		STNodeEntry b = c.getParent(session);
		STNodeEntry a = b.getParent(session);

		STLinkEntry aToCLink = session.addLink(a, c, "AtoC");
		STLinkEntry aToBLink = session.addLink(a, b, "AtoB");
		STLinkEntry cToALink = session.addLink(c, a, "CtoA");

		assertThat(aToCLink.getOrigin(), is(a));
		assertThat(aToCLink.getTarget(), is(c));

		assertThat(aToBLink.getOrigin(), is(a));
		assertThat(aToBLink.getTarget(), is(b));

		assertThat(cToALink.getOrigin(), is(c));
		assertThat(cToALink.getTarget(), is(a));

		STLinkEntry foundCtoALink = session.getLink(c, a, "CtoA");
		assertThat(cToALink, is(foundCtoALink));
		List<STLinkEntry> foundALinks = iterableToList(session.findLinks(a));
		assertThat(foundALinks.size(), is(2));
		assertThat(foundALinks.contains(aToCLink), is(true));
		assertThat(foundALinks.contains(aToBLink), is(true));

		List<STLinkEntry> foundBLinks = iterableToList(session.findLinks(b));
		assertThat(foundBLinks.size(), is(0));

		List<STLinkEntry> foundAToCLinks = iterableToList(session.findLinks(a,
				"AtoC"));

		assertThat(foundAToCLinks.size(), is(1));
		assertThat(foundAToCLinks.contains(aToCLink), is(true));

		List<STLinkEntry> foundAToBLinks = iterableToList(session.findLinks(a,
				b));
		assertThat(foundAToBLinks.size(), is(1));
		assertThat(foundAToBLinks.contains(aToBLink), is(true));

	}

	@Test
	public void shouldAddAndRetriveLinksOnDifferentPartitionsWithAutoFlushInjector()
			throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry c = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("c");
		STNodeEntry b = session.withPartition(ExamplePartition.FIRST)
				.createNewSimpleNode("c");
		STNodeEntry a = session.withPartition(ExamplePartition.SECOND)
				.createNewSimpleNode("c");

		STLinkEntry aToCLink = session.addLink(a, c, "AtoC");
		STLinkEntry aToBLink = session.addLink(a, b, "AtoB");
		STLinkEntry cToALink = session.addLink(c, a, "CtoA");

		assertThat(aToCLink.getOrigin(), is(a));
		assertThat(aToCLink.getTarget(), is(c));

		assertThat(aToBLink.getOrigin(), is(a));
		assertThat(aToBLink.getTarget(), is(b));

		assertThat(cToALink.getOrigin(), is(c));
		assertThat(cToALink.getTarget(), is(a));

		STLinkEntry foundCtoALink = session.getLink(c, a, "CtoA");
		assertThat(cToALink, is(foundCtoALink));
		List<STLinkEntry> foundALinks = iterableToList(session.findLinks(a));
		assertThat(foundALinks.size(), is(2));
		assertThat(foundALinks.contains(aToCLink), is(true));
		assertThat(foundALinks.contains(aToBLink), is(true));

		List<STLinkEntry> foundBLinks = iterableToList(session.findLinks(b));
		assertThat(foundBLinks.size(), is(0));

		List<STLinkEntry> foundAToCLinks = iterableToList(session.findLinks(a,
				"AtoC"));

		assertThat(foundAToCLinks.size(), is(1));
		assertThat(foundAToCLinks.contains(aToCLink), is(true));

		List<STLinkEntry> foundAToBLinks = iterableToList(session.findLinks(a,
				b));
		assertThat(foundAToBLinks.size(), is(1));
		assertThat(foundAToBLinks.contains(aToBLink), is(true));

	}

	@Test
	public void shouldAddAndRetriveLinksOnSamePartitionWithExplicitFlushInjector()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry c = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a", "b", "c");
		STNodeEntry b = c.getParent(session);
		STNodeEntry a = b.getParent(session);

		STLinkEntry aToCLink = session.addLink(a, c, "AtoC");
		STLinkEntry aToBLink = session.addLink(a, b, "AtoB");
		STLinkEntry cToALink = session.addLink(c, a, "CtoA");
		session.flushTransient();
		assertThat(aToCLink.getOrigin(), is(a));
		assertThat(aToCLink.getTarget(), is(c));

		assertThat(aToBLink.getOrigin(), is(a));
		assertThat(aToBLink.getTarget(), is(b));

		assertThat(cToALink.getOrigin(), is(c));
		assertThat(cToALink.getTarget(), is(a));

		STLinkEntry foundCtoALink = session.getLink(c, a, "CtoA");
		assertThat(cToALink, is(foundCtoALink));
		List<STLinkEntry> foundALinks = iterableToList(session.findLinks(a));
		assertThat(foundALinks.size(), is(2));
		assertThat(foundALinks.contains(aToCLink), is(true));
		assertThat(foundALinks.contains(aToBLink), is(true));

		List<STLinkEntry> foundBLinks = iterableToList(session.findLinks(b));
		assertThat(foundBLinks.size(), is(0));

		List<STLinkEntry> foundAToCLinks = iterableToList(session.findLinks(a,
				"AtoC"));

		assertThat(foundAToCLinks.size(), is(1));
		assertThat(foundAToCLinks.contains(aToCLink), is(true));

		List<STLinkEntry> foundAToBLinks = iterableToList(session.findLinks(a,
				b));
		assertThat(foundAToBLinks.size(), is(1));
		assertThat(foundAToBLinks.contains(aToBLink), is(true));

	}

	@Test
	public void shouldAddAndRetriveLinksOnDifferentPartitionsWithExplicitFlushInjector()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);
		STNodeEntry c = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("c");
		STNodeEntry b = session.withPartition(ExamplePartition.FIRST)
				.createNewSimpleNode("c");
		STNodeEntry a = session.withPartition(ExamplePartition.SECOND)
				.createNewSimpleNode("c");

		STLinkEntry aToCLink = session.addLink(a, c, "AtoC");
		STLinkEntry aToBLink = session.addLink(a, b, "AtoB");
		STLinkEntry cToALink = session.addLink(c, a, "CtoA");
		session.flushTransient();
		assertThat(aToCLink.getOrigin(), is(a));
		assertThat(aToCLink.getTarget(), is(c));

		assertThat(aToBLink.getOrigin(), is(a));
		assertThat(aToBLink.getTarget(), is(b));

		assertThat(cToALink.getOrigin(), is(c));
		assertThat(cToALink.getTarget(), is(a));

		STLinkEntry foundCtoALink = session.getLink(c, a, "CtoA");
		assertThat(cToALink, is(foundCtoALink));
		List<STLinkEntry> foundALinks = iterableToList(session.findLinks(a));
		assertThat(foundALinks.size(), is(2));
		assertThat(foundALinks.contains(aToCLink), is(true));
		assertThat(foundALinks.contains(aToBLink), is(true));

		List<STLinkEntry> foundBLinks = iterableToList(session.findLinks(b));
		assertThat(foundBLinks.size(), is(0));

		List<STLinkEntry> foundAToCLinks = iterableToList(session.findLinks(a,
				"AtoC"));

		assertThat(foundAToCLinks.size(), is(1));
		assertThat(foundAToCLinks.contains(aToCLink), is(true));

		List<STLinkEntry> foundAToBLinks = iterableToList(session.findLinks(a,
				b));
		assertThat(foundAToBLinks.size(), is(1));
		assertThat(foundAToBLinks.contains(aToBLink), is(true));

	}

	@Test
	public void shouldCreateAndRemoveLinksWithPropertiesOnSamePartitionWithAutoFlushInjector()
			throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);

		STNodeEntry b = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("b");
		STNodeEntry a = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a");

		STLinkEntry link = session.addLink(a, b, "AtoB");
		STLinkEntry link2 = session.addLink(a, b, "AtoB2");
		link.setIndexedProperty(session, "sample", "value");

		STLinkEntry foundLink = session.getLink(a, b, "AtoB");
		STLinkEntry foundLink2 = session.getLink(a, b, "AtoB2");
		assertThat(foundLink, is(link));
		assertThat(foundLink2, is(link2));
		assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
				.getPropertyAsString(session, "sample")));
		assertThat(foundLink.getPropertyAsString(session, "sample"),
				is("value"));

		session.removeLink(a, b, "AtoB");
		session.removeLink(link2);

		STLinkEntry notFoundLink = session.getLink(a, b, "AtoB");
		STLinkEntry notFoundLink2 = session.getLink(a, b, "AtoB2");

		assertThat(notFoundLink, is(nullValue()));
		assertThat(notFoundLink2, is(nullValue()));

	}

	@Test
	public void shouldCreateAndRemoveLinksWithPropertiesOnSamePartitionWithExplicitFlushInjector()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);

		STNodeEntry b = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("b");
		STNodeEntry a = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("a");

		STLinkEntry link = session.addLink(a, b, "AtoB");
		STLinkEntry link2 = session.addLink(a, b, "AtoB2");
		link.setIndexedProperty(session, "sample", "value");
		session.flushTransient();
		STLinkEntry foundLink = session.getLink(a, b, "AtoB");
		STLinkEntry foundLink2 = session.getLink(a, b, "AtoB2");
		assertThat(foundLink, is(link));
		assertThat(foundLink2, is(link2));
		assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
				.getPropertyAsString(session, "sample")));
		assertThat(foundLink.getPropertyAsString(session, "sample"),
				is("value"));

		session.removeLink(a, b, "AtoB");
		session.removeLink(link2);
		session.flushTransient();
		STLinkEntry notFoundLink = session.getLink(a, b, "AtoB");
		STLinkEntry notFoundLink2 = session.getLink(a, b, "AtoB2");

		assertThat(notFoundLink, is(nullValue()));
		assertThat(notFoundLink2, is(nullValue()));

	}

	@Test
	public void shouldCreateAndRemoveLinksWithPropertiesOnDifferentPartitionsWithAutoFlushInjector()
			throws Exception {
		STStorageSession session = autoFlushInjector
				.getInstance(STStorageSession.class);

		STNodeEntry b = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("b");
		STNodeEntry a = session.withPartition(ExamplePartition.FIRST)
				.createNewSimpleNode("a");

		STLinkEntry link = session.addLink(a, b, "AtoB");
		STLinkEntry link2 = session.addLink(a, b, "AtoB2");
		link.setIndexedProperty(session, "sample", "value");

		STLinkEntry foundLink = session.getLink(a, b, "AtoB");
		STLinkEntry foundLink2 = session.getLink(a, b, "AtoB2");
		assertThat(foundLink, is(link));
		assertThat(foundLink2, is(link2));
		assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
				.getPropertyAsString(session, "sample")));
		assertThat(foundLink.getPropertyAsString(session, "sample"),
				is("value"));

		session.removeLink(a, b, "AtoB");
		session.removeLink(link2);

		STLinkEntry notFoundLink = session.getLink(a, b, "AtoB");
		STLinkEntry notFoundLink2 = session.getLink(a, b, "AtoB2");

		assertThat(notFoundLink, is(nullValue()));
		assertThat(notFoundLink2, is(nullValue()));

	}

	@Test
	public void shouldCreateAndRemoveLinksWithPropertiesOnDifferentPartitionsWithExplicitFlushInjector()
			throws Exception {
		STStorageSession session = explicitFlushInjector
				.getInstance(STStorageSession.class);

		STNodeEntry b = session.withPartition(ExamplePartition.DEFAULT)
				.createNewSimpleNode("b");
		STNodeEntry a = session.withPartition(ExamplePartition.FIRST)
				.createNewSimpleNode("a");

		STLinkEntry link = session.addLink(a, b, "AtoB");
		STLinkEntry link2 = session.addLink(a, b, "AtoB2");
		link.setIndexedProperty(session, "sample", "value");
		session.flushTransient();
		STLinkEntry foundLink = session.getLink(a, b, "AtoB");
		STLinkEntry foundLink2 = session.getLink(a, b, "AtoB2");
		assertThat(foundLink, is(link));
		assertThat(foundLink2, is(link2));
		assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
				.getPropertyAsString(session, "sample")));
		assertThat(foundLink.getPropertyAsString(session, "sample"),
				is("value"));

		session.removeLink(a, b, "AtoB");
		session.removeLink(link2);
		session.flushTransient();
		STLinkEntry notFoundLink = session.getLink(a, b, "AtoB");
		STLinkEntry notFoundLink2 = session.getLink(a, b, "AtoB2");

		assertThat(notFoundLink, is(nullValue()));
		assertThat(notFoundLink2, is(nullValue()));

	}

}
