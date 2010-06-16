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

import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;


/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 5:08:39 PM
 */
public abstract class AbstractSTStorageSessionTest {

    protected abstract Injector createsAutoFlushInjector();

    protected abstract Injector createsExplicitFlushInjector();

    private boolean didRunOnce = false;

    public void setupInjectors() throws Exception {
        if (supportsAutoFlushInjector()) this.autoFlushInjector = createsAutoFlushInjector();
        if (supportsExplicitFlushInjector()) this.explicitFlushInjector = createsExplicitFlushInjector();

    }


    protected enum ExamplePartition implements STPartition {

        DEFAULT("DEFAULT"), FIRST("FIRST"), SECOND("SECOND");

        public static final STPartitionFactory FACTORY = new STPartitionFactory() {

            @Override
            public STPartition getPartitionByName(String name) {
                return ExamplePartition.valueOf(name.toUpperCase());
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
    public void shouldSaveSimpleNodesOnAutoFlush() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT)
                .createNewSimpleNode("a", "b", "c");
        Iterable<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).findNamed("c");
        assertThat(result.iterator().hasNext(), is(true));

    }

    @Test
    public void shouldInstantiateOneSessionPerThread() throws Exception {
        STStorageSession session1 = autoFlushInjector.getInstance(STStorageSession.class);
        STStorageSession session2 = autoFlushInjector.getInstance(STStorageSession.class);
        assertThat(session1, is(session2));

        final List<STStorageSession> sessions = new CopyOnWriteArrayList<STStorageSession>();
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread() {
            @Override
            public void run() {
                try {
                    sessions.add(autoFlushInjector.getInstance(STStorageSession.class));
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
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry aNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry sameNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        String aKeyAsString = aNode.getUniqueKey().getKeyAsString();
        String sameKeyAsString = sameNode.getUniqueKey().getKeyAsString();
        assertThat(aKeyAsString, is(sameKeyAsString));

    }


    @Test
    public void shouldFindByUniqueKey() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry aNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry theSameNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withUniqueKey(aNode.getUniqueKey()).buildCriteria().andFindUnique(session);
        assertThat(aNode, is(theSameNode));
        assertThat(theSameNode.getProperty(session, "name").getValueAsString(session), is("name"));
        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withUniqueKey(session.withPartition(ExamplePartition.DEFAULT).createKey("invalid", false).andCreate()).buildCriteria().andFindUnique(session);
        assertThat(nullNode, is(nullValue()));


    }

    @Test
    public void shouldFindByLocalKey() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root2).andCreate();

        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withLocalKey(aNode1.getUniqueKey().getLocalKey()).buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
    }


    @Test
    public void shouldFindByProperties() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").equalsTo("value").buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));


        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter1").equalsTo("value1").buildCriteria().andFind(session);
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }


    public void shouldFindByPropertiesContainingString() throws Exception {
        if (supportsAdvancedQueries()) {
            STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
            STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name1").andCreate();
            STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name2").andCreate();
            STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name1").withParent(root1).andCreate();
            STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name2").withParent(root2).andCreate();
            aNode1.setSimpleProperty(session, "parameter", "io");
            aNode2.setSimpleProperty(session, "parameter", "aeiou");
            root1.setSimpleProperty(session, "parameter", "foo");
            root2.setSimpleProperty(session, "parameter", "bar");
            Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                    .withProperty("parameter").containsString("io").buildCriteria().andFind(session);
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }


    @Test
    public void shouldFindByPropertiesWithNullValue() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "a").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "2")
                .withKey("name", "b").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name2").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "io");
        aNode2.setIndexedProperty(session, "parameter", "aeiou");
        root1.setIndexedProperty(session, "parameter", null);
        root2.setIndexedProperty(session, "parameter", null);
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").equalsTo(null).buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(root1), is(true));
        assertThat(theSameNodes.contains(root2), is(true));
        assertThat(theSameNodes.contains(aNode1), is(false));
        assertThat(theSameNodes.contains(aNode2), is(false));
    }

    public void shouldFindByPropertiesStartingWithString() throws Exception {
        if (supportsAdvancedQueries()) {
            STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
            STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name1").andCreate();
            STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name2").andCreate();
            STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name1").withParent(root1).andCreate();
            STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name2").withParent(root2).andCreate();
            aNode1.setSimpleProperty(session, "parameter", "io");
            aNode2.setSimpleProperty(session, "parameter", "iou");
            root1.setSimpleProperty(session, "parameter", "fooiou");
            root2.setSimpleProperty(session, "parameter", "baior");
            Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                    .withProperty("parameter").startsWithString("io").buildCriteria().andFind(session);
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    public void shouldFindByPropertiesEndingWithString() throws Exception {
        if (supportsAdvancedQueries()) {
            STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
            STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name1").andCreate();
            STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name2").andCreate();
            STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name1").withParent(root1).andCreate();
            STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                    .withKey("name", "name2").withParent(root2).andCreate();
            aNode1.setSimpleProperty(session, "parameter", "io");
            aNode2.setSimpleProperty(session, "parameter", "uio");
            root1.setSimpleProperty(session, "parameter", "fooiou");
            root2.setSimpleProperty(session, "parameter", "baior");
            Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                    .withProperty("parameter").endsWithString("io").buildCriteria().andFind(session);
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }


    @Test
    public void shouldFindByLocalKeyAndProperties() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withLocalKey(aNode1.getUniqueKey().getLocalKey()).withProperty("parameter").equalsTo("value").buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withLocalKey(aNode1.getUniqueKey().getLocalKey()).withProperty("parameter1").equalsTo("value1").buildCriteria().andFind(session);
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindNamedNodes() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root2).andCreate();

        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).findNamed("root1");
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(false));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(true));
        assertThat(onlyOneNode.contains(root2), is(false));
        Set<STNodeEntry> twoNodes = session.withPartition(ExamplePartition.DEFAULT).findNamed("node");
        assertThat(twoNodes.size(), is(2));
        assertThat(twoNodes.contains(aNode1), is(true));
        assertThat(twoNodes.contains(aNode2), is(true));
        assertThat(twoNodes.contains(root1), is(false));
        assertThat(twoNodes.contains(root2), is(false));


    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFindingWithUniqueAndOtherAttributes() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1")
                .withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").withUniqueKey(session
                .withPartition(ExamplePartition.DEFAULT).createKey("sample", false).andCreate())
                .buildCriteria().andFindUnique(session);
    }


    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithAutoFlush() {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equalsTo("1").withProperty("name").equalsTo("name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(notNullValue()));
        assertThat(foundNewNode1, is(newNode1));
    }

    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithExplicitFlush() {

        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equalsTo("1").withProperty("name").equalsTo("name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));
        session.flushTransient();
        STNodeEntry foundNewNode2 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equalsTo("1").withProperty("name").equalsTo("name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode2, is(notNullValue()));
        assertThat(foundNewNode2, is(newNode1));
    }

    @Test
    public void shouldCreateHierarchyAndLoadParentNode() {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);

        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("sameName", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("sameName", false).withParent(newNode1).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("sameName", false).withParent(newNode2).withKey("sequence", "3")
                .withKey("name", "name").andCreate();


        STNodeEntry foundNewNode3 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("sameName").withProperty("sequence")
                .equalsTo("3").withProperty("name").equalsTo("name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode3, is(notNullValue()));
        STNodeEntry foundNewNode2 = foundNewNode3.getParent(session);
        STNodeEntry foundNewNode1 = foundNewNode2.getParent(session);
        assertThat(foundNewNode3, is(newNode3));
        assertThat(foundNewNode2, is(newNode2));
        assertThat(foundNewNode1, is(newNode1));
        assertThat(foundNewNode1.getPropertyAsString(session, "name"), is("name"));
        assertThat(foundNewNode2.getPropertyAsString(session, "name"), is("name"));
        assertThat(foundNewNode3.getPropertyAsString(session, "name"), is("name"));
        assertThat(foundNewNode1.getPropertyAsString(session, "sequence"), is("1"));
        assertThat(foundNewNode2.getPropertyAsString(session, "sequence"), is("1"));
        assertThat(foundNewNode3.getPropertyAsString(session, "sequence"), is("3"));
        assertThat(foundNewNode1.getNodeEntryName(), is("sameName"));
        assertThat(foundNewNode2.getNodeEntryName(), is("sameName"));
        assertThat(foundNewNode3.getNodeEntryName(), is("sameName"));


    }

    @Test
    public void shouldCreateHierarchyAndLoadChildrenNodes() {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);

        STNodeEntry root = session.withPartition(ExamplePartition.DEFAULT).createWithName("root", false)
                .withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry child1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child", false)
                .withParent(root)
                .withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry child2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child", false)
                .withParent(root)
                .withKey("sequence", "2")
                .withKey("name", "name").andCreate();
        STNodeEntry child3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child", false)
                .withParent(root)
                .withKey("sequence", "3")
                .withKey("name", "name").andCreate();
        STNodeEntry child4 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child", false)
                .withParent(root)
                .withKey("sequence", "4")
                .withKey("name", "name").andCreate();
        STNodeEntry childAnotherType1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType", false)
                .withParent(root)
                .withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry childAnotherType2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType", false)
                .withParent(root)
                .withKey("sequence", "2")
                .withKey("name", "name").andCreate();
        STNodeEntry childAnotherType3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType", false)
                .withParent(root)
                .withKey("sequence", "3")
                .withKey("name", "name").andCreate();
        STNodeEntry childAnotherType4 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType", false)
                .withParent(root)
                .withKey("sequence", "4")
                .withKey("name", "name").andCreate();

        Set<STNodeEntry> allChildren = root.getChildren(session);
        assertThat(allChildren.size(), is(8));
        assertThat(allChildren.contains(child1), is(true));
        assertThat(allChildren.contains(child2), is(true));
        assertThat(allChildren.contains(child3), is(true));
        assertThat(allChildren.contains(child4), is(true));
        assertThat(allChildren.contains(childAnotherType1), is(true));
        assertThat(allChildren.contains(childAnotherType2), is(true));
        assertThat(allChildren.contains(childAnotherType3), is(true));
        assertThat(allChildren.contains(childAnotherType4), is(true));


        Set<STNodeEntry> childrenType2 = root.getChildrenNamed(session, "childAnotherType");

        assertThat(childrenType2.size(), is(4));
        assertThat(childrenType2.contains(childAnotherType1), is(true));
        assertThat(childrenType2.contains(childAnotherType2), is(true));
        assertThat(childrenType2.contains(childAnotherType3), is(true));
        assertThat(childrenType2.contains(childAnotherType4), is(true));

        Set<STNodeEntry> childrenType1 = root.getChildrenNamed(session, "child");

        assertThat(childrenType1.size(), is(4));
        assertThat(childrenType1.contains(child1), is(true));
        assertThat(childrenType1.contains(child2), is(true));
        assertThat(childrenType1.contains(child3), is(true));
        assertThat(childrenType1.contains(child4), is(true));


    }

    @Test
    public void shouldWorkWithPartitions() {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);

        session.withPartition(ExamplePartition.DEFAULT).createWithName("root", false)
                .withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        session.withPartition(ExamplePartition.FIRST).createWithName("root", false)
                .withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        session.withPartition(ExamplePartition.SECOND).createWithName("root", false)
                .withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withUniqueKey(
                session.withPartition(ExamplePartition.DEFAULT).createKey("root", false).withEntry("sequence", "1")
                        .withEntry("name", "name").andCreate()).buildCriteria().andFindUnique(session);

        STNodeEntry root2 = session.withPartition(ExamplePartition.FIRST).createCriteria().withUniqueKey(
                session.withPartition(ExamplePartition.FIRST).createKey("root", false).withEntry("sequence", "1")
                        .withEntry("name", "name").andCreate()).buildCriteria().andFindUnique(session);

        STNodeEntry root3 = session.withPartition(ExamplePartition.SECOND).createCriteria().withUniqueKey(
                session.withPartition(ExamplePartition.SECOND).createKey("root", false).withEntry("sequence", "1")
                        .withEntry("name", "name").andCreate()).buildCriteria().andFindUnique(session);

        assertThat(root1, is(notNullValue()));

        assertThat(root2, is(notNullValue()));

        assertThat(root3, is(notNullValue()));

        assertThat(root1, is(not(root2)));

        assertThat(root2, is(not(root3)));

        Set<STNodeEntry> list1 = session.withPartition(ExamplePartition.DEFAULT).findNamed("root");
        Set<STNodeEntry> list2 = session.withPartition(ExamplePartition.DEFAULT).findNamed("root");
        Set<STNodeEntry> list3 = session.withPartition(ExamplePartition.DEFAULT).findNamed("root");

        assertThat(list1.size(), is(1));
        assertThat(list2.size(), is(1));
        assertThat(list3.size(), is(1));
    }


    @Test
    public void shouldWorkWithSimplePropertiesOnExplicitFlush() throws Exception {

        Date newDate = new Date();
        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();

        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);

        assertThat(loadedNode, is(nullValue()));

        session.flushTransient();

        STNodeEntry loadedNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);

        assertThat(loadedNode1, is(notNullValue()));

        newNode.setIndexedProperty(session, "stringProperty", "value");


        assertThat(newNode.getProperty(session, "stringProperty").getInternalMethods().getTransientValueAsString(session),
                is("value"));


        assertThat(loadedNode1.getPropertyAsString(session, "stringProperty"),
                is(nullValue()));


        session.flushTransient();
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
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        newNode.setIndexedProperty(session, "stringProperty", "value");

        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);


        assertThat(newNode.getProperty(session, "stringProperty").getInternalMethods().getTransientValueAsString(session),
                is("value"));

        assertThat(newNode.getPropertyAsString(session, "stringProperty"),
                is("value"));


        assertThat(loadedNode.getPropertyAsString(session, "stringProperty"),
                is("value"));


        STNodeEntry anotherLoadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1")
                .withProperty("stringProperty").equalsTo("value").buildCriteria().andFindUnique(session);

        assertThat(anotherLoadedNode, is(loadedNode));

        STNodeEntry noLoadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1")
                .withProperty("stringProperty").equalsTo("invalid").buildCriteria().andFindUnique(session);

        assertThat(noLoadedNode, is(nullValue()));
    }

    public static class PojoClass implements Serializable {

        private String aString;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PojoClass pojoClass = (PojoClass) o;

            if (anInt != pojoClass.anInt) return false;
            if (aString != null ? !aString.equals(pojoClass.aString) : pojoClass.aString != null) return false;

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
    public void shouldWorkWithInputStreamPropertiesOnExplicitFlush() throws Exception {


        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();

        InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.setSimpleProperty(session, "streamProperty", stream);


        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);

        assertThat(nullNode, is(nullValue()));
        session.flushTransient();
        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);


        stream.reset();
        assertThat(IOUtils.contentEquals(newNode.getPropertyAsStream(session, "streamProperty"), stream),
                is(true));

        InputStream loaded1 = loadedNode.getPropertyAsStream(session, "streamProperty");

        ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        String asString1 = new String(temporary1.toByteArray());
        ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        InputStream loaded2 = loadedNode.getPropertyAsStream(session, "streamProperty");

        IOUtils.copy(loaded2, temporary2);
        String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1,
                is("streamValue"));
        assertThat(asString2,
                is("streamValue"));
    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnAutoFlush() throws Exception {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();


        InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.setSimpleProperty(session, "streamProperty", stream);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equalsTo("1")
                .withProperty("name").equalsTo("name").buildCriteria().andFindUnique(session);


        stream.reset();
        assertThat(IOUtils.contentEquals(newNode.getPropertyAsStream(session, "streamProperty"), stream),
                is(true));

        InputStream loaded1 = loadedNode.getPropertyAsStream(session, "streamProperty");

        ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        String asString1 = new String(temporary1.toByteArray());
        ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        InputStream loaded2 = loadedNode.getPropertyAsStream(session, "streamProperty");

        IOUtils.copy(loaded2, temporary2);
        String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1,
                is("streamValue"));
        assertThat(asString2,
                is("streamValue"));


    }


    @Test
    public void shouldFindMultipleResults() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "2")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "another name").andCreate();
        STNodeEntry newNode4 = session.withPartition(ExamplePartition.DEFAULT).createWithName("anotherName", false).withKey("sequence", "2")
                .withKey("name", "name").andCreate();
        Set<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("name")
                .equalsTo("name").buildCriteria().andFind(session);

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));
        assertThat(result.contains(newNode3), is(false));
        assertThat(result.contains(newNode4), is(false));


    }

    @Test
    public void shouldRemoveNodesOnAutoFlush() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "2")
                .withKey("name", "name").andCreate();

        Set<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        Set<STNodeEntry> newResult = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));

        newNode2.removeNode(session);
        newResult = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");
        assertThat(newResult.size(), is(0));

    }

    @Test
    public void shouldRemoveNodesOnExplicitFlush() throws Exception {
        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "2")
                .withKey("name", "name").andCreate();
        session.flushTransient();
        Set<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        Set<STNodeEntry> resultNotChanged = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(resultNotChanged.size(), is(2));
        assertThat(resultNotChanged.contains(newNode1), is(true));
        assertThat(resultNotChanged.contains(newNode2), is(true));

        session.flushTransient();

        Set<STNodeEntry> newResult = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));


        newNode2.removeNode(session);
        session.flushTransient();
        newResult = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");
        assertThat(newResult.size(), is(0));

    }

    @Test
    public void shouldDiscardTransientNodesOnExplicitFlush() throws Exception {
        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "2")
                .withKey("name", "name").andCreate();
        session.flushTransient();
        Set<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        Set<STNodeEntry> resultNotChanged = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(resultNotChanged.size(), is(2));
        assertThat(resultNotChanged.contains(newNode1), is(true));
        assertThat(resultNotChanged.contains(newNode2), is(true));

        session.discardTransient();

        Set<STNodeEntry> resultStillNotChanged = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(resultStillNotChanged.size(), is(2));
        assertThat(resultStillNotChanged.contains(newNode1), is(true));
        assertThat(resultStillNotChanged.contains(newNode2), is(true));

        session.flushTransient();
        Set<STNodeEntry> resultNotChangedAgain = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(resultNotChangedAgain.size(), is(2));
        assertThat(resultNotChangedAgain.contains(newNode1), is(true));
        assertThat(resultNotChangedAgain.contains(newNode2), is(true));

    }


    @Test
    public void shouldUpdatePropertyAndFindWithUpdatedValue() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        newNode1.setIndexedProperty(session, "parameter", "firstValue");
        Set<STNodeEntry> found = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeEntry("newNode1").withProperty("parameter").equalsTo("firstValue")
                .buildCriteria().andFind(session);
        assertThat(found.size(), is(1));
        assertThat(found.contains(newNode1), is(true));
        newNode1.getProperty(session, "parameter").setStringValue(session, "secondValue");

        Set<STNodeEntry> notFound = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeEntry("newNode1").withProperty("parameter").equalsTo("firstValue")
                .buildCriteria().andFind(session);
        assertThat(notFound.size(), is(0));

        Set<STNodeEntry> foundAgain = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeEntry("newNode1").withProperty("parameter").equalsTo("secondValue")
                .buildCriteria().andFind(session);
        assertThat(foundAgain.size(), is(1));
        assertThat(foundAgain.contains(newNode1), is(true));


    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotSetKeyProperty() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        newNode1.setSimpleProperty(session, "sequence", "3");

    }


    @Test
    public void shouldFindByPropertiesWithoutNodeName() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def", false).withKey("sequence", "1")
                .withKey("name", "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl", false).withKey("sequence", "1")
                .withKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter").equalsTo("value").buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter1").equalsTo("value1").buildCriteria().andFind(session);
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }


    public void shouldFindByPropertiesContainingStringWithoutNodeName() throws Exception {
        if (supportsAdvancedQueries()) {
            STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
            STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc", false).withKey("sequence", "1")
                    .withKey("name", "name1").andCreate();
            STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def", false).withKey("sequence", "1")
                    .withKey("name", "name2").andCreate();
            STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi", false).withKey("sequence", "1")
                    .withKey("name", "name1").withParent(root1).andCreate();
            STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl", false).withKey("sequence", "1")
                    .withKey("name", "name2").withParent(root2).andCreate();
            aNode1.setSimpleProperty(session, "parameter", "io");
            aNode2.setSimpleProperty(session, "parameter", "aeiou");
            root1.setSimpleProperty(session, "parameter", "foo");
            root2.setSimpleProperty(session, "parameter", "bar");
            Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                    .withProperty("parameter").containsString("io").buildCriteria().andFind(session);
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    public void shouldFindByPropertiesStartingWithStringWithoutNodeName() throws Exception {
        if (supportsAdvancedQueries()) {
            STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
            STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc", false).withKey("sequence", "1")
                    .withKey("name", "name1").andCreate();
            STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def", false).withKey("sequence", "1")
                    .withKey("name", "name2").andCreate();
            STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi", false).withKey("sequence", "1")
                    .withKey("name", "name1").withParent(root1).andCreate();
            STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl", false).withKey("sequence", "1")
                    .withKey("name", "name2").withParent(root2).andCreate();
            aNode1.setSimpleProperty(session, "parameter", "io");
            aNode2.setSimpleProperty(session, "parameter", "iou");
            root1.setSimpleProperty(session, "parameter", "fooiou");
            root2.setSimpleProperty(session, "parameter", "baior");
            Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                    .withProperty("parameter").startsWithString("io").buildCriteria().andFind(session);
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    public void shouldFindByPropertiesEndingWithStringWithoutNodeName() throws Exception {
        if (supportsAdvancedQueries()) {
            STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
            STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc", false).withKey("sequence", "1")
                    .withKey("name", "name1").andCreate();
            STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def", false).withKey("sequence", "1")
                    .withKey("name", "name2").andCreate();
            STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi", false).withKey("sequence", "1")
                    .withKey("name", "name1").withParent(root1).andCreate();
            STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl", false).withKey("sequence", "1")
                    .withKey("name", "name2").withParent(root2).andCreate();
            aNode1.setSimpleProperty(session, "parameter", "io");
            aNode2.setSimpleProperty(session, "parameter", "uio");
            root1.setSimpleProperty(session, "parameter", "fooiou");
            root2.setSimpleProperty(session, "parameter", "baior");
            Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                    .withProperty("parameter").endsWithString("io").buildCriteria().andFind(session);
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }


}