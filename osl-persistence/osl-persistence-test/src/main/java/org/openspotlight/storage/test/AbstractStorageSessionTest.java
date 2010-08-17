/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto * Direitos Autorais Reservados (c) 2009, CARAVELATECH
 * CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
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
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Link;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.node.PropertyImpl;

import com.google.inject.Injector;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 5:08:39 PM
 */
public abstract class AbstractStorageSessionTest {

    protected abstract Injector createsAutoFlushInjector();

    protected abstract Injector createsExplicitFlushInjector();

    private final boolean didRunOnce = false;

    public void setupInjectors()
        throws Exception {
        if (supportsAutoFlushInjector()) {
            autoFlushInjector = createsAutoFlushInjector();
        }
        if (supportsExplicitFlushInjector()) {
            explicitFlushInjector = createsExplicitFlushInjector();
        }

    }

    protected enum ExamplePartition implements Partition {

        DEFAULT("DEFAULT"),
        FIRST("FIRST"),
        SECOND("SECOND");

        public static final PartitionFactory FACTORY = new PartitionFactory() {

                                                         @Override
                                                         public Partition getPartitionByName(final String name) {
                                                             return ExamplePartition.valueOf(name.toUpperCase());
                                                         }

                                                         @Override
                                                         public Partition[] getValues() {
                                                             return ExamplePartition.values();
                                                         }
                                                     };

        private final String                 partitionName;

        public String getPartitionName() {
            return partitionName;
        }

        private ExamplePartition(final String partitionName) {
            this.partitionName = partitionName;
        }
    }

    protected abstract boolean supportsAutoFlushInjector();

    protected abstract boolean supportsExplicitFlushInjector();

    protected abstract boolean supportsAdvancedQueries();

    protected abstract void internalCleanPreviousData()
        throws Exception;

    protected Injector autoFlushInjector;

    protected Injector explicitFlushInjector;

    @Before
    public void cleanPreviousData()
        throws Exception {
        setupInjectors();
        internalCleanPreviousData();
    }

    @Test
    public void shouldFindNodeTypesOnDifferentPartitionsOnAutoFlush()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
                                                                            "a1", "b1", "c1");
        session.withPartition(ExamplePartition.FIRST).createNewSimpleNode("a2",
                                                                          "b2", "c2");
        final List<String> nodeTypes1 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getAllNodeTypes());
        final List<String> nodeTypes2 = iterableToList(session.withPartition(
                                                                       ExamplePartition.FIRST).getAllNodeTypes());
        assertThat(nodeTypes1.contains("a1"), is(true));
        assertThat(nodeTypes1.contains("b1"), is(true));
        assertThat(nodeTypes1.contains("c1"), is(true));
        assertThat(nodeTypes2.contains("a2"), is(true));
        assertThat(nodeTypes2.contains("b2"), is(true));
        assertThat(nodeTypes2.contains("c2"), is(true));
        assertThat(nodeTypes2.contains("a1"), is(false));
        assertThat(nodeTypes2.contains("b1"), is(false));
        assertThat(nodeTypes2.contains("c1"), is(false));
        assertThat(nodeTypes1.contains("a2"), is(false));
        assertThat(nodeTypes1.contains("b2"), is(false));
        assertThat(nodeTypes1.contains("c2"), is(false));
    }

    @Test
    public void shouldExcludeParentAndChildrenOnExplicitFlush()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node c1 = session.withPartition(ExamplePartition.DEFAULT)
                                .createNewSimpleNode("a1", "b1", "c1");
        session.flushTransient();
        final Node b1 = c1.getParent(session);
        final Node a1 = b1.getParent(session);
        a1.removeNode(session);
        session.flushTransient();
        final Iterable<Node> foundA1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).findByType("a1");
        final Iterable<Node> foundB1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).findByType("b1");
        final Iterable<Node> foundC1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).findByType("c1");
        assertThat(foundA1.iterator().hasNext(), is(false));
        assertThat(foundB1.iterator().hasNext(), is(false));
        assertThat(foundC1.iterator().hasNext(), is(false));

    }

    @Test
    public void shouldExcludeParentAndChildrenOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node c1 = session.withPartition(ExamplePartition.DEFAULT)
                                .createNewSimpleNode("a1", "b1", "c1");
        final Node b1 = c1.getParent(session);
        final Node a1 = b1.getParent(session);
        a1.removeNode(session);
        final Iterable<Node> foundA1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).findByType("a1");
        final Iterable<Node> foundB1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).findByType("b1");
        final Iterable<Node> foundC1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).findByType("c1");
        assertThat(foundA1.iterator().hasNext(), is(false));
        assertThat(foundB1.iterator().hasNext(), is(false));
        assertThat(foundC1.iterator().hasNext(), is(false));

    }

    @Test
    public void shouldFindNodeTypesOnDifferentPartitionsOnExplicitFlush()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
                                                                            "a1", "b1", "c1");
        session.withPartition(ExamplePartition.FIRST).createNewSimpleNode("a2",
                                                                          "b2", "c2");
        session.flushTransient();
        final List<String> nodeTypes1 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getAllNodeTypes());
        final List<String> nodeTypes2 = iterableToList(session.withPartition(
                                                                       ExamplePartition.FIRST).getAllNodeTypes());
        assertThat(nodeTypes1.contains("a1"), is(true));
        assertThat(nodeTypes1.contains("b1"), is(true));
        assertThat(nodeTypes1.contains("c1"), is(true));
        assertThat(nodeTypes2.contains("a2"), is(true));
        assertThat(nodeTypes2.contains("b2"), is(true));
        assertThat(nodeTypes2.contains("c2"), is(true));
        assertThat(nodeTypes2.contains("a1"), is(false));
        assertThat(nodeTypes2.contains("b1"), is(false));
        assertThat(nodeTypes2.contains("c1"), is(false));
        assertThat(nodeTypes1.contains("a2"), is(false));
        assertThat(nodeTypes1.contains("b2"), is(false));
        assertThat(nodeTypes1.contains("c2"), is(false));
    }

    @Test
    public void shouldSaveSimpleNodesOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
                                                                            "a", "b", "c");
        final Iterable<Node> result = session.withPartition(
                                                             ExamplePartition.DEFAULT).findByType("c");
        assertThat(result.iterator().hasNext(), is(true));

    }

    @Test
    public void shouldFindSimpleNodeWithStringIdOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNewSimpleNode("a", "b", "c");
        final String nodeIdAsString = newNode.getKey().getKeyAsString();
        final Node result = session.withPartition(ExamplePartition.DEFAULT)
                                    .createCriteria().withUniqueKeyAsString(nodeIdAsString)
                                    .buildCriteria().andFindUnique(session);
        assertThat(result, is(newNode));

    }

    @Test
    public void shouldFindSimpleNodeWithStringIdOnExplicitFlush()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNewSimpleNode("a", "b", "c");
        session.flushTransient();
        final String nodeIdAsString = newNode.getKey().getKeyAsString();
        final Node result = session.withPartition(ExamplePartition.DEFAULT)
                                    .createCriteria().withUniqueKeyAsString(nodeIdAsString)
                                    .buildCriteria().andFindUnique(session);
        assertThat(result, is(newNode));

    }

    @Test
    public void shouldInstantiateOneSessionPerThread()
        throws Exception {
        final StorageSession session1 = autoFlushInjector
                                                   .getInstance(StorageSession.class);
        final StorageSession session2 = autoFlushInjector
                                                   .getInstance(StorageSession.class);
        assertThat(session1, is(session2));

        final List<StorageSession> sessions = new CopyOnWriteArrayList<StorageSession>();
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread() {
            @Override
            public void run() {
                try {
                    sessions.add(autoFlushInjector
                                                  .getInstance(StorageSession.class));
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
    public void shouldCreateTheSameKey()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node aNode = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("newNode1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node sameNode = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final String aKeyAsString = aNode.getKey().getKeyAsString();
        final String sameKeyAsString = sameNode.getKey().getKeyAsString();
        assertThat(aKeyAsString, is(sameKeyAsString));

    }

    @Test
    public void shouldFindByUniqueKey()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node aNode = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("newNode1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node theSameNode =
            session
                .withPartition(
                                                        ExamplePartition.DEFAULT)
                .createCriteria()
                .withUniqueKey(
                                                                                                                 aNode
                                                                                                                     .getKey())
                .buildCriteria().andFindUnique(session);
        assertThat(aNode, is(theSameNode));
        assertThat(theSameNode.getProperty(session, "name").getValueAsString(
                                                                             session), is("name"));
        final Node nullNode = session.withPartition(ExamplePartition.DEFAULT)
                                      .createCriteria().withUniqueKey(
                                                                      session.withPartition(ExamplePartition.DEFAULT)
                                                                             .createKey("invalid").andCreate())
                                      .buildCriteria().andFindUnique(session);
        assertThat(nullNode, is(nullValue()));

    }

    @Test
    public void shouldFindByLocalKey()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final Node aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();

        final List<Node> theSameNodes =
            iterableToList(session
                .withPartition(
                                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withLocalKey(
                                                                                                                                      aNode1
                                                                                                                                          .getKey()
                                                                                                                                          .getCompositeKey())
                .buildCriteria()
                .andFind(
                                                                                                                                                                                                   session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
    }

    @Test
    public void shouldFindByProperties()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final Node aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        final List<Node> theSameNodes =
            iterableToList(session.withPartition(
                                                                              ExamplePartition.DEFAULT).createCriteria()
                                                               .withNodeEntry("node").withProperty("parameter").equalsTo(
                                                                                                                         "value")
                .buildCriteria().andFind(session));
        assertThat(theSameNodes.size(), is(2));

        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        final List<Node> onlyOneNode =
            iterableToList(session
                .withPartition(
                                                                             ExamplePartition.DEFAULT)
                .createCriteria()
                                                              .withNodeEntry("node")
                .withProperty("parameter1")
                .equalsTo(
                                                                                                                         "value1")
                .buildCriteria().andFind(session));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindByPropertiesContainingString()
        throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final Node aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name1").withParent(root1).andCreate();
            final Node aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "aeiou");
            root1.setIndexedProperty(session, "parameter", "foo");
            root2.setIndexedProperty(session, "parameter", "bar");
            final List<Node> theSameNodes = iterableToList(session
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
    public void shouldFindByPropertiesWithNullValue()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("node").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "a").andCreate();
        final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("node").withSimpleKey("sequence", "2")
                                   .withSimpleKey("name", "b").andCreate();
        final Node aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name1").withParent(root1).andCreate();
        final Node aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name2").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "io");
        aNode2.setIndexedProperty(session, "parameter", "aeiou");
        root1.setIndexedProperty(session, "parameter", null);
        root2.setIndexedProperty(session, "parameter", null);
        final List<Node> theSameNodes = iterableToList(session.withPartition(
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
    public void shouldFindByPropertiesStartingWithString()
        throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final Node aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name1").withParent(root1).andCreate();
            final Node aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "iou");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<Node> theSameNodes = iterableToList(session
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
    public void shouldFindByPropertiesEndingWithString()
        throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final Node aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name1").withParent(root1).andCreate();
            final Node aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "uio");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<Node> theSameNodes = iterableToList(session
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
    public void shouldFindByLocalKeyAndProperties()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final Node aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        final List<Node> theSameNodes =
            iterableToList(session
                .withPartition(
                                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withLocalKey(
                                                                                                                                      aNode1
                                                                                                                                          .getKey()
                                                                                                                                          .getCompositeKey())
                .withProperty("parameter")
                                                               .equalsTo("value").buildCriteria().andFind(session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        final List<Node> onlyOneNode =
            iterableToList(session
                .withPartition(
                                                                             ExamplePartition.DEFAULT)
                .createCriteria()
                .withLocalKey(
                                                                                                                                     aNode1
                                                                                                                                         .getKey()
                                                                                                                                         .getCompositeKey())
                .withProperty("parameter1")
                                                              .equalsTo("value1").buildCriteria().andFind(session));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindNodesByType()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final Node aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();

        final List<Node> onlyOneNode = iterableToList(session.withPartition(
                                                                             ExamplePartition.DEFAULT).findByType("root1"));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(false));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(true));
        assertThat(onlyOneNode.contains(root2), is(false));
        final List<Node> twoNodes = iterableToList(session.withPartition(
                                                                          ExamplePartition.DEFAULT).findByType("node"));
        assertThat(twoNodes.size(), is(2));
        assertThat(twoNodes.contains(aNode1), is(true));
        assertThat(twoNodes.contains(aNode2), is(true));
        assertThat(twoNodes.contains(root1), is(false));
        assertThat(twoNodes.contains(root2), is(false));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFindingWithUniqueAndOtherAttributes()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
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
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        Node foundNewNode1 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                   "newNode1")
                .withProperty("sequence").equalsTo("1")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        foundNewNode1 =
            session.withPartition(ExamplePartition.DEFAULT)
                               .createCriteria().withNodeEntry("newNode1").withProperty(
                                                                                        "sequence").equalsTo("1")
                .withProperty("name")
                               .equalsTo("name").buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(notNullValue()));
        assertThat(foundNewNode1, is(newNode1));
    }

    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithExplicitFlush() {

        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        Node foundNewNode1 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                   "newNode1")
                .withProperty("sequence").equalsTo("1")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        foundNewNode1 =
            session.withPartition(ExamplePartition.DEFAULT)
                               .createCriteria().withNodeEntry("newNode1").withProperty(
                                                                                        "sequence").equalsTo("1")
                .withProperty("name")
                               .equalsTo("name").buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));
        session.flushTransient();
        final Node foundNewNode2 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                   "newNode1")
                .withProperty("sequence").equalsTo("1")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andFindUnique(session);
        assertThat(foundNewNode2, is(notNullValue()));
        assertThat(foundNewNode2, is(newNode1));
    }

    @Test
    public void shouldCreateHierarchyAndLoadParentNode() {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("sameName").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final Node newNode2 =
            session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("sameName").withParent(newNode1).withSimpleKey(
                                                                                                    "sequence", "1")
                .withSimpleKey("name", "name")
                                      .andCreate();
        final Node newNode3 =
            session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("sameName").withParent(newNode2).withSimpleKey(
                                                                                                    "sequence", "3")
                .withSimpleKey("name", "name")
                                      .andCreate();

        final Node foundNewNode3 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                   "sameName")
                .withProperty("sequence").equalsTo("3")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andFindUnique(session);
        assertThat(foundNewNode3, is(notNullValue()));
        final Node foundNewNode2 = foundNewNode3.getParent(session);
        final Node foundNewNode1 = foundNewNode2.getParent(session);
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
        assertThat(foundNewNode1.getType(), is("sameName"));
        assertThat(foundNewNode2.getType(), is("sameName"));
        assertThat(foundNewNode3.getType(), is("sameName"));

    }

    @Test
    public void shouldCreateHierarchyAndLoadChildrenNodes() {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final Node root = session.withPartition(ExamplePartition.DEFAULT)
                                  .createWithType("root").withSimpleKey("sequence", "1")
                                  .withSimpleKey("name", "name").andCreate();
        final Node child1 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "1")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final Node child2 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "2")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final Node child3 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "3")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final Node child4 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "4")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final Node childAnotherType1 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "1").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();
        final Node childAnotherType2 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "2").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();
        final Node childAnotherType3 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "3").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();
        final Node childAnotherType4 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "4").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();

        final List<Node> allChildren = iterableToList(root.getChildren(
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

        final List<Node> childrenType2 =
            iterableToList(root.getChildrenByType(
                                                                               ExamplePartition.DEFAULT, session,
                "childAnotherType"));

        assertThat(childrenType2.size(), is(4));
        assertThat(childrenType2.contains(childAnotherType1), is(true));
        assertThat(childrenType2.contains(childAnotherType2), is(true));
        assertThat(childrenType2.contains(childAnotherType3), is(true));
        assertThat(childrenType2.contains(childAnotherType4), is(true));

        final List<Node> childrenType1 = iterableToList(root.getChildrenByType(
                                                                               ExamplePartition.DEFAULT, session, "child"));

        assertThat(childrenType1.size(), is(4));
        assertThat(childrenType1.contains(child1), is(true));
        assertThat(childrenType1.contains(child2), is(true));
        assertThat(childrenType1.contains(child3), is(true));
        assertThat(childrenType1.contains(child4), is(true));

    }

    @Test
    public void shouldWorkWithPartitions() {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        session.withPartition(ExamplePartition.DEFAULT).createWithType("root")
                .withSimpleKey("sequence", "1").withSimpleKey("name", "name")
                .andCreate();
        session.withPartition(ExamplePartition.FIRST).createWithType("root")
                .withSimpleKey("sequence", "1").withSimpleKey("name", "name")
                .andCreate();
        session.withPartition(ExamplePartition.SECOND).createWithType("root")
                .withSimpleKey("sequence", "1").withSimpleKey("name", "name")
                .andCreate();
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createCriteria().withUniqueKey(
                                                                   session.withPartition(ExamplePartition.DEFAULT)
                                                                          .createKey("root").withSimpleKey("sequence", "1")
                                                                          .withSimpleKey("name", "name").andCreate())
                                   .buildCriteria().andFindUnique(session);

        final Node root2 = session.withPartition(ExamplePartition.FIRST)
                                   .createCriteria().withUniqueKey(
                                                                   session.withPartition(ExamplePartition.FIRST)
                                                                          .createKey("root").withSimpleKey("sequence", "1")
                                                                          .withSimpleKey("name", "name").andCreate())
                                   .buildCriteria().andFindUnique(session);

        final Node root3 = session.withPartition(ExamplePartition.SECOND)
                                   .createCriteria().withUniqueKey(
                                                                   session.withPartition(ExamplePartition.SECOND)
                                                                          .createKey("root").withSimpleKey("sequence", "1")
                                                                          .withSimpleKey("name", "name").andCreate())
                                   .buildCriteria().andFindUnique(session);

        assertThat(root1, is(notNullValue()));

        assertThat(root2, is(notNullValue()));

        assertThat(root3, is(notNullValue()));

        assertThat(root1, is(not(root2)));

        assertThat(root2, is(not(root3)));

        final List<Node> list1 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).findByType("root"));
        final List<Node> list2 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).findByType("root"));
        final List<Node> list3 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).findByType("root"));

        assertThat(list1.size(), is(1));
        assertThat(list2.size(), is(1));
        assertThat(list3.size(), is(1));
    }

    @Test
    public void shouldWorkWithSimplePropertiesOnExplicitFlush()
            throws Exception {

        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();

        final Node loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeEntry("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andFindUnique(session);

        assertThat(loadedNode, is(nullValue()));

        session.flushTransient();

        final Node loadedNode1 =
            session.withPartition(
                                                        ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                 "newNode1")
                .withProperty("sequence").equalsTo("1")
                                         .withProperty("name").equalsTo("name").buildCriteria()
                                         .andFindUnique(session);

        assertThat(loadedNode1, is(notNullValue()));

        newNode.setSimpleProperty(session, "stringProperty", "value");

        assertThat(((PropertyImpl) newNode.getProperty(session, "stringProperty"))
                          .getTransientValueAsString(session),
                   is("value"));

        assertThat(loadedNode1.getPropertyAsString(session, "stringProperty"),
                   is(nullValue()));

        session.flushTransient();
        final Node loadedNode2 =
            session.withPartition(
                                                        ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                 "newNode1")
                .withProperty("sequence").equalsTo("1")
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
    public void shouldWorkWithSimplePropertiesOnAutoFlush()
        throws Exception {

        final Date newDate = new Date();
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();
        newNode.setIndexedProperty(session, "stringProperty", "value");

        final Node loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeEntry("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andFindUnique(session);

        assertThat(((PropertyImpl) newNode.getProperty(session, "stringProperty"))
                                                                                 .getTransientValueAsString(session),
                   is("value"));

        assertThat(newNode.getPropertyAsString(session, "stringProperty"),
                   is("value"));

        assertThat(loadedNode.getPropertyAsString(session, "stringProperty"),
                   is("value"));

        final Node anotherLoadedNode =
            session
                .withPartition(
                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeEntry(
                                                                                                                       "newNode1")
                .withProperty("stringProperty").equalsTo("value")
                                               .buildCriteria().andFindUnique(session);

        assertThat(anotherLoadedNode, is(loadedNode));

        final Node noLoadedNode =
            session.withPartition(
                                                         ExamplePartition.DEFAULT).createCriteria().withNodeEntry(
                                                                                                                  "newNode1")
                .withProperty("stringProperty").equalsTo("invalid")
                                          .buildCriteria().andFindUnique(session);

        assertThat(noLoadedNode, is(nullValue()));
    }

    public static class PojoClass implements Serializable {

        private String aString;

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PojoClass pojoClass = (PojoClass) o;

            if (anInt != pojoClass.anInt) { return false; }
            if (aString != null ? !aString.equals(pojoClass.aString)
                    : pojoClass.aString != null) { return false; }

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

        public void setaString(final String aString) {
            this.aString = aString;
        }

        public int getAnInt() {
            return anInt;
        }

        public void setAnInt(final int anInt) {
            this.anInt = anInt;
        }

        private int anInt;

    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnExplicitFlush()
            throws Exception {

        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();

        final InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.setSimpleProperty(session, "streamProperty", stream);

        final Node nullNode =
            session.withPartition(ExamplePartition.DEFAULT)
                                      .createCriteria().withNodeEntry("newNode1").withProperty(
                                                                                               "sequence").equalsTo("1")
                .withProperty("name")
                                      .equalsTo("name").buildCriteria().andFindUnique(session);

        assertThat(nullNode, is(nullValue()));
        session.flushTransient();
        final Node loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeEntry("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andFindUnique(session);

        stream.reset();
        assertThat(IOUtils.contentEquals(newNode.getPropertyAsStream(session,
                                                                     "streamProperty"), stream), is(true));

        final InputStream loaded1 = loadedNode.getPropertyAsStream(session,
                                                             "streamProperty");

        final ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        final String asString1 = new String(temporary1.toByteArray());
        final ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        final InputStream loaded2 = loadedNode.getPropertyAsStream(session,
                                                             "streamProperty");

        IOUtils.copy(loaded2, temporary2);
        final String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1, is("streamValue"));
        assertThat(asString2, is("streamValue"));
    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnAutoFlush()
            throws Exception {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();

        final InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.setSimpleProperty(session, "streamProperty", stream);

        final Node loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeEntry("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andFindUnique(session);

        stream.reset();
        assertThat(IOUtils.contentEquals(newNode.getPropertyAsStream(session,
                                                                     "streamProperty"), stream), is(true));

        final InputStream loaded1 = loadedNode.getPropertyAsStream(session,
                                                             "streamProperty");

        final ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        final String asString1 = new String(temporary1.toByteArray());
        final ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        final InputStream loaded2 = loadedNode.getPropertyAsStream(session,
                                                             "streamProperty");

        IOUtils.copy(loaded2, temporary2);
        final String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1, is("streamValue"));
        assertThat(asString2, is("streamValue"));

    }

    @Test
    public void shouldFindMultipleResults()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final Node newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        final Node newNode3 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "another name").andCreate();
        final Node newNode4 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("anotherName").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        final List<Node> result =
            iterableToList(session
                .withPartition(
                                                                        ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeEntry(
                                                                                                                                 "newNode1")
                .withProperty("name").equalsTo("name")
                                                         .buildCriteria().andFind(session));

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));
        assertThat(result.contains(newNode3), is(false));
        assertThat(result.contains(newNode4), is(false));

    }

    @Test
    public void shouldRemoveNodesOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final Node newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();

        final List<Node> result = iterableToList(session.withPartition(
                                                                        ExamplePartition.DEFAULT).findByType("newNode1"));

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        List<Node> newResult = iterableToList(session.withPartition(
                                                                           ExamplePartition.DEFAULT).findByType("newNode1"));
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));

        newNode2.removeNode(session);
        newResult = iterableToList(session.withPartition(
                                                         ExamplePartition.DEFAULT).findByType("newNode1"));
        assertThat(newResult.size(), is(0));

    }

    @Test
    public void shouldRemoveNodesOnExplicitFlush()
        throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final Node newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        session.flushTransient();
        final List<Node> result = iterableToList(session.withPartition(
                                                                        ExamplePartition.DEFAULT).findByType("newNode1"));

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        final List<Node> resultNotChanged =
            iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT)
                .findByType("newNode1"));

        assertThat(resultNotChanged.size(), is(2));
        assertThat(resultNotChanged.contains(newNode1), is(true));
        assertThat(resultNotChanged.contains(newNode2), is(true));

        session.flushTransient();

        List<Node> newResult = iterableToList(session.withPartition(
                                                                           ExamplePartition.DEFAULT).findByType("newNode1"));
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));

        newNode2.removeNode(session);
        session.flushTransient();
        newResult = iterableToList(session.withPartition(
                                                         ExamplePartition.DEFAULT).findByType("newNode1"));
        assertThat(newResult.size(), is(0));

    }

    @Test
    public void shouldDiscardTransientNodesOnExplicitFlush()
        throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final Node newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        session.flushTransient();
        final List<Node> result = iterableToList(session.withPartition(
                                                                        ExamplePartition.DEFAULT).findByType("newNode1"));

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        final List<Node> resultNotChanged =
            iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT)
                .findByType("newNode1"));

        assertThat(resultNotChanged.size(), is(2));
        assertThat(resultNotChanged.contains(newNode1), is(true));
        assertThat(resultNotChanged.contains(newNode2), is(true));

        session.discardTransient();

        final List<Node> resultStillNotChanged =
            iterableToList(session
                                                                        .withPartition(ExamplePartition.DEFAULT).findByType(
                                                                            "newNode1"));

        assertThat(resultStillNotChanged.size(), is(2));
        assertThat(resultStillNotChanged.contains(newNode1), is(true));
        assertThat(resultStillNotChanged.contains(newNode2), is(true));

        session.flushTransient();
        final List<Node> resultNotChangedAgain =
            iterableToList(session
                                                                        .withPartition(ExamplePartition.DEFAULT).findByType(
                                                                            "newNode1"));

        assertThat(resultNotChangedAgain.size(), is(2));
        assertThat(resultNotChangedAgain.contains(newNode1), is(true));
        assertThat(resultNotChangedAgain.contains(newNode2), is(true));

    }

    @Test
    public void shouldUpdatePropertyAndFindWithUpdatedValue()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        newNode1.setIndexedProperty(session, "parameter", "firstValue");
        final List<Node> found =
            iterableToList(session
                .withPartition(
                                                                       ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeEntry(
                                                                                                                                "newNode1")
                .withProperty("parameter").equalsTo("firstValue")
                                                        .buildCriteria().andFind(session));
        assertThat(found.size(), is(1));
        assertThat(found.contains(newNode1), is(true));
        newNode1.getProperty(session, "parameter").setStringValue(session,
                                                                  "secondValue");

        final List<Node> notFound =
            iterableToList(session
                .withPartition(
                                                                          ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeEntry(
                                                                                                                                   "newNode1")
                .withProperty("parameter").equalsTo("firstValue")
                                                           .buildCriteria().andFind(session));
        assertThat(notFound.size(), is(0));

        final List<Node> foundAgain =
            iterableToList(session
                .withPartition(
                                                                            ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeEntry(
                                                                                                                                     "newNode1")
                .withProperty("parameter").equalsTo("secondValue")
                                                             .buildCriteria().andFind(session));
        assertThat(foundAgain.size(), is(1));
        assertThat(foundAgain.contains(newNode1), is(true));

    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotSetKeyProperty()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        newNode1.setSimpleProperty(session, "sequence", "3");

    }

    @Test
    public void shouldFindByPropertiesWithoutNodeType()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("abc").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createWithType("def").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final Node aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("ghi").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final Node aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createWithType("jkl").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        final List<Node> theSameNodes =
            iterableToList(session
                .withPartition(
                                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withProperty(
                                                                                                                                      "parameter")
                .equalsTo("value").buildCriteria().andFind(session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        final List<Node> onlyOneNode =
            iterableToList(session
                .withPartition(
                                                                             ExamplePartition.DEFAULT)
                .createCriteria()
                .withProperty(
                                                                                                                                     "parameter1")
                .equalsTo("value1")
                .buildCriteria()
                .andFind(
                                                                                                                                                                                              session));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindByPropertiesContainingStringWithoutNodeType()
            throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("abc").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("def").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final Node aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "ghi")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name1").withParent(root1).andCreate();
            final Node aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "jkl")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "aeiou");
            root1.setIndexedProperty(session, "parameter", "foo");
            root2.setIndexedProperty(session, "parameter", "bar");
            final List<Node> theSameNodes = iterableToList(session
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
    public void shouldFindByPropertiesStartingWithStringWithoutNodeType()
            throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("abc").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("def").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final Node aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "ghi")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name1").withParent(root1).andCreate();
            final Node aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "jkl")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "iou");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<Node> theSameNodes = iterableToList(session
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
    public void shouldFindByPropertiesEndingWithStringWithoutNodeType()
            throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final Node root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("abc").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final Node root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createWithType("def").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final Node aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "ghi")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name1").withParent(root1).andCreate();
            final Node aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createWithType(
                                                                                                "jkl")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "uio");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<Node> theSameNodes = iterableToList(session
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
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a", "b", "c");
        final Node b = c.getParent(session);
        final Node a = b.getParent(session);

        final Link aToCLink = session.addLink(a, c, "AtoC");
        final Link aToBLink = session.addLink(a, b, "AtoB");
        final Link cToALink = session.addLink(c, a, "CtoA");

        assertThat(aToCLink.getOrigin(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToBLink.getOrigin(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getOrigin(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final Link foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));
        final List<Link> foundALinks = iterableToList(session.findLinks(a));
        assertThat(foundALinks.size(), is(2));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<Link> foundBLinks = iterableToList(session.findLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<Link> foundAToCLinks = iterableToList(session.findLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<Link> foundAToBLinks = iterableToList(session.findLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));

    }

    @Test
    public void shouldAddAndRetriveLinksOnDifferentPartitionsWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final Node c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("c");
        final Node b = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("c");
        final Node a = session.withPartition(ExamplePartition.SECOND)
                               .createNewSimpleNode("c");

        final Link aToCLink = session.addLink(a, c, "AtoC");
        final Link aToBLink = session.addLink(a, b, "AtoB");
        final Link cToALink = session.addLink(c, a, "CtoA");

        assertThat(aToCLink.getOrigin(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToBLink.getOrigin(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getOrigin(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final Link foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));
        final List<Link> foundALinks = iterableToList(session.findLinks(a));
        assertThat(foundALinks.size(), is(2));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<Link> foundBLinks = iterableToList(session.findLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<Link> foundAToCLinks = iterableToList(session.findLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<Link> foundAToBLinks = iterableToList(session.findLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));

    }

    @Test
    public void shouldAddAndRetriveLinksOnSamePartitionWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a", "b", "c");
        session.flushTransient();
        final Node b = c.getParent(session);
        final Node a = b.getParent(session);

        final Link aToCLink = session.addLink(a, c, "AtoC");
        final Link aToBLink = session.addLink(a, b, "AtoB");
        final Link cToALink = session.addLink(c, a, "CtoA");
        session.flushTransient();
        assertThat(aToCLink.getOrigin(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToBLink.getOrigin(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getOrigin(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final Link foundCtoALink = session.getLink(c, a, "CtoA");
        final List<Link> foundALinks = iterableToList(session.findLinks(a));
        assertThat(cToALink, is(foundCtoALink));
        assertThat(foundALinks.size(), is(2));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<Link> foundBLinks = iterableToList(session.findLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<Link> foundAToCLinks = iterableToList(session.findLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<Link> foundAToBLinks = iterableToList(session.findLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));

    }

    @Test
    public void shouldAddAndRetriveLinksOnDifferentPartitionsWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final Node c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("c");
        final Node b = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("c");
        final Node a = session.withPartition(ExamplePartition.SECOND)
                               .createNewSimpleNode("c");

        final Link aToCLink = session.addLink(a, c, "AtoC");
        final Link aToBLink = session.addLink(a, b, "AtoB");
        final Link cToALink = session.addLink(c, a, "CtoA");
        session.flushTransient();
        assertThat(aToCLink.getOrigin(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToBLink.getOrigin(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getOrigin(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final Link foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));
        final List<Link> foundALinks = iterableToList(session.findLinks(a));
        assertThat(foundALinks.size(), is(2));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<Link> foundBLinks = iterableToList(session.findLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<Link> foundAToCLinks = iterableToList(session.findLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<Link> foundAToBLinks = iterableToList(session.findLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));

    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnSamePartitionWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final Node b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final Node a = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a");

        final Link link = session.addLink(a, b, "AtoB");
        final Link link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");

        final Link foundLink = session.getLink(a, b, "AtoB");
        final Link foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
                                                                            .getPropertyAsString(session, "sample")));
        assertThat(foundLink.getPropertyAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);

        final Link notFoundLink = session.getLink(a, b, "AtoB");
        final Link notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnSamePartitionWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);

        final Node b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final Node a = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a");

        final Link link = session.addLink(a, b, "AtoB");
        final Link link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");
        session.flushTransient();
        final Link foundLink = session.getLink(a, b, "AtoB");
        final Link foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
                                                                            .getPropertyAsString(session, "sample")));
        assertThat(foundLink.getPropertyAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);
        session.flushTransient();
        final Link notFoundLink = session.getLink(a, b, "AtoB");
        final Link notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnDifferentPartitionsWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final Node b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final Node a = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("a");

        final Link link = session.addLink(a, b, "AtoB");
        final Link link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");

        final Link foundLink = session.getLink(a, b, "AtoB");
        final Link foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
                                                                            .getPropertyAsString(session, "sample")));
        assertThat(foundLink.getPropertyAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);

        final Link notFoundLink = session.getLink(a, b, "AtoB");
        final Link notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnDifferentPartitionsWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);

        final Node b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final Node a = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("a");

        final Link link = session.addLink(a, b, "AtoB");
        final Link link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");
        session.flushTransient();
        final Link foundLink = session.getLink(a, b, "AtoB");
        final Link foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyAsString(session, "sample"), is(link
                                                                            .getPropertyAsString(session, "sample")));
        assertThat(foundLink.getPropertyAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);
        session.flushTransient();
        final Link notFoundLink = session.getLink(a, b, "AtoB");
        final Link notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

    }

}
