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

package org.openspotlight.storage.redis;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;


/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 5:08:39 PM
 */
public class JRedisStorageSessionTest {
    public JRedisStorageSessionTest() {
        autoFlushInjector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO, mappedServerConfig, repositoryPath("repositoryPath")));
    }


    private enum JRedisServerConfigExample implements JRedisServerDetail {
        DEFAULT("localhost", 6379, 0),
        FIRST("localhost", 6379, 1),
        SECOND("localhost", 6379, 2);

        private JRedisServerConfigExample(String serverName, int serverPort, int db) {
            this.serverName = serverName;
            this.serverPort = serverPort;
            this.db = db;
        }

        private final String serverName;

        private final int db;

        public int getDb() {
            return db;
        }

        public String getPassword() {
            return null;
        }

        private final int serverPort;

        public String getServerName() {
            return serverName;
        }

        public int getServerPort() {
            return serverPort;
        }
    }

    private enum ExamplePartition implements STPartition {

        DEFAULT("default"), FIRST("first"), SECOND("second");

        private final String partitionName;

        public String getPartitionName() {
            return partitionName;
        }

        ExamplePartition(String partitionName) {
            this.partitionName = partitionName;
        }
    }

    final Map<STPartition, JRedisServerDetail> mappedServerConfig;

    {
        mappedServerConfig = ImmutableMap.<STPartition, JRedisServerDetail>builder()
                .put(ExamplePartition.DEFAULT, JRedisServerConfigExample.DEFAULT)
                .put(ExamplePartition.FIRST, JRedisServerConfigExample.FIRST)
                .put(ExamplePartition.SECOND, JRedisServerConfigExample.SECOND).build();
    }

    final Injector autoFlushInjector;

    final Injector explicitFlushInjector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.EXPLICIT, mappedServerConfig, repositoryPath("repositoryPath")));

    @Before
    public void cleanPreviousData() throws Exception {
        JRedisFactory autoFlushFactory = autoFlushInjector.getInstance(JRedisFactory.class);
        autoFlushFactory.getFrom(ExamplePartition.DEFAULT).flushall();
    }
    @Test
    public void shouldSaveSimpleNodesOnAutoFlush() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT)
                .createNewSimpleNode("a","b","c");
        Iterable<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).findNamed("c");
        assertThat(result.iterator().hasNext(),is(true));

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
        STNodeEntry aNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry sameNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        String aKeyAsString = session.getSupportMethods().getUniqueKeyAsSimpleString(aNode.getUniqueKey());
        String sameKeyAsString = session.getSupportMethods().getUniqueKeyAsSimpleString(sameNode.getUniqueKey());
        assertThat(aKeyAsString, is(sameKeyAsString));

    }


    @Test
    public void shouldFindByUniqueKey() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry aNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry theSameNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withUniqueKey(aNode.getUniqueKey()).buildCriteria().andFindUnique(session);
        assertThat(aNode, is(theSameNode));
        assertThat(theSameNode.getProperty(session, "name").getValueAs(session, String.class), is("name"));
        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withUniqueKey(session.withPartition(ExamplePartition.DEFAULT).createKey("invalid").andCreate()).buildCriteria().andFindUnique(session);
        assertThat(nullNode, is(nullValue()));


    }

    @Test
    public void shouldFindByLocalKey() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root2).andCreate();

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
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "value");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "value");
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter1", String.class, "value1");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter1", String.class, "value2");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").equals(String.class, "value").buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter1").equals(String.class, "value1").buildCriteria().andFind(session);
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }



    @Test
    public void shouldFindByPropertiesContainingString() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "aeiou");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "foo");
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "bar");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").containsString("io").buildCriteria().andFind(session);
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        assertThat(theSameNodes.size(), is(2));
    }



    @Test
    public void shouldFindByPropertiesWithNullValue() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "a").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "b").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "aeiou");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, null);
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, null);
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").equals(String.class,null).buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(root1), is(true));
        assertThat(theSameNodes.contains(root2), is(true));
        assertThat(theSameNodes.contains(aNode1), is(false));
        assertThat(theSameNodes.contains(aNode2), is(false));
    }
    @Test
    public void shouldFindByPropertiesStartingWithString() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "iou");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "fooiou");
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "baior");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").startsWithString("io").buildCriteria().andFind(session);
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        assertThat(theSameNodes.size(), is(2));
    }

    @Test
    public void shouldFindByPropertiesEndingWithString() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "uio");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "fooiou");
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "baior");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("node")
                .withProperty("parameter").endsWithString("io").buildCriteria().andFind(session);
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        assertThat(theSameNodes.size(), is(2));
    }


    @Test
    public void shouldFindByLocalKeyAndProperties() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "value");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "value");
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter1", String.class, "value1");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter1", String.class, "value2");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withLocalKey(aNode1.getUniqueKey().getLocalKey()).withProperty("parameter").equals(String.class, "value").buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withLocalKey(aNode1.getUniqueKey().getLocalKey()).withProperty("parameter1").equals(String.class, "value1").buildCriteria().andFind(session);
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindNamedNodes() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("root2").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("node").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root2).andCreate();

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
                .withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").withUniqueKey(session
                .withPartition(ExamplePartition.DEFAULT).createKey("sample").andCreate())
                .buildCriteria().andFindUnique(session);
    }


    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithAutoFlush() {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equals(Integer.class, 1).withProperty("name").equals(String.class, "name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(notNullValue()));
        assertThat(foundNewNode1, is(newNode1));
    }

    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithExplicitFlush() {

        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        foundNewNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equals(Integer.class, 1).withProperty("name").equals(String.class, "name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode1, is(nullValue()));
        session.flushTransient();
        STNodeEntry foundNewNode2 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equals(Integer.class, 1).withProperty("name").equals(String.class, "name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode2, is(notNullValue()));
        assertThat(foundNewNode2, is(newNode1));
    }

    @Test
    public void shouldCreateHierarchyAndLoadParentNode() {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);

        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("sameName").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("sameName").withParent(newNode1).withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("sameName").withParent(newNode2).withKey("sequence", Integer.class, 3)
                .withKey("name", String.class, "name").andCreate();


        STNodeEntry foundNewNode3 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("sameName").withProperty("sequence")
                .equals(Integer.class, 3).withProperty("name").equals(String.class, "name")
                .buildCriteria().andFindUnique(session);
        assertThat(foundNewNode3, is(notNullValue()));
        STNodeEntry foundNewNode2 = foundNewNode3.getParent(session);
        STNodeEntry foundNewNode1 = foundNewNode2.getParent(session);
        assertThat(foundNewNode3, is(newNode3));
        assertThat(foundNewNode2, is(newNode2));
        assertThat(foundNewNode1, is(newNode1));
        assertThat(foundNewNode1.<String>getPropertyValue(session, "name"), is("name"));
        assertThat(foundNewNode2.<String>getPropertyValue(session, "name"), is("name"));
        assertThat(foundNewNode3.<String>getPropertyValue(session, "name"), is("name"));
        assertThat(foundNewNode1.<Integer>getPropertyValue(session, "sequence"), is(1));
        assertThat(foundNewNode2.<Integer>getPropertyValue(session, "sequence"), is(1));
        assertThat(foundNewNode3.<Integer>getPropertyValue(session, "sequence"), is(3));
        assertThat(foundNewNode1.getNodeEntryName(), is("sameName"));
        assertThat(foundNewNode2.getNodeEntryName(), is("sameName"));
        assertThat(foundNewNode3.getNodeEntryName(), is("sameName"));


    }

    @Test
    public void shouldCreateHierarchyAndLoadChildrenNodes() {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);

        STNodeEntry root = session.withPartition(ExamplePartition.DEFAULT).createWithName("root")
                .withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry child1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child")
                .withParent(root)
                .withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry child2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child")
                .withParent(root)
                .withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry child3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child")
                .withParent(root)
                .withKey("sequence", Integer.class, 3)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry child4 = session.withPartition(ExamplePartition.DEFAULT).createWithName("child")
                .withParent(root)
                .withKey("sequence", Integer.class, 4)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry childAnotherType1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType")
                .withParent(root)
                .withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry childAnotherType2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType")
                .withParent(root)
                .withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry childAnotherType3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType")
                .withParent(root)
                .withKey("sequence", Integer.class, 3)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry childAnotherType4 = session.withPartition(ExamplePartition.DEFAULT).createWithName("childAnotherType")
                .withParent(root)
                .withKey("sequence", Integer.class, 4)
                .withKey("name", String.class, "name").andCreate();

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

        session.withPartition(ExamplePartition.DEFAULT).createWithName("root")
                .withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        session.withPartition(ExamplePartition.FIRST).createWithName("root")
                .withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        session.withPartition(ExamplePartition.SECOND).createWithName("root")
                .withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withUniqueKey(
                session.withPartition(ExamplePartition.DEFAULT).createKey("root").withEntry("sequence", Integer.class, 1)
                        .withEntry("name", String.class, "name").andCreate()).buildCriteria().andFindUnique(session);

        STNodeEntry root2 = session.withPartition(ExamplePartition.FIRST).createCriteria().withUniqueKey(
                session.withPartition(ExamplePartition.FIRST).createKey("root").withEntry("sequence", Integer.class, 1)
                        .withEntry("name", String.class, "name").andCreate()).buildCriteria().andFindUnique(session);

        STNodeEntry root3 = session.withPartition(ExamplePartition.SECOND).createCriteria().withUniqueKey(
                session.withPartition(ExamplePartition.SECOND).createKey("root").withEntry("sequence", Integer.class, 1)
                        .withEntry("name", String.class, "name").andCreate()).buildCriteria().andFindUnique(session);

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
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);

        assertThat(loadedNode, is(nullValue()));

        session.flushTransient();

        STNodeEntry loadedNode1 = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);

        assertThat(loadedNode1, is(notNullValue()));

        newNode.getVerifiedOperations().setSimpleProperty(session, "classProperty", Class.class, String.class);
        newNode.getVerifiedOperations().setSimpleProperty(session, "enumProperty", ExampleEnum.class, ExampleEnum.FIRST);
        newNode.getVerifiedOperations().setSimpleProperty(session, "stringProperty", String.class, "value");
        newNode.getVerifiedOperations().setSimpleProperty(session, "dateProperty", Date.class, newDate);
        newNode.getVerifiedOperations().setSimpleProperty(session, "integerProperty", Integer.class, 2);
        newNode.getVerifiedOperations().setSimpleProperty(session, "floatProperty", Float.class, 2.1f);
        newNode.getVerifiedOperations().setSimpleProperty(session, "doubleProperty", Double.class, 2.1d);


        assertThat(newNode.getProperty(session, "enumProperty").getInternalMethods().<ExampleEnum>getTransientValue(),
                is(ExampleEnum.FIRST));
        assertThat(newNode.getProperty(session, "classProperty").getInternalMethods().<Class>getTransientValue().getName(),
                is(String.class.getName()));
        assertThat(newNode.getProperty(session, "stringProperty").getInternalMethods().<String>getTransientValue(),
                is("value"));

        assertThat(newNode.getProperty(session, "dateProperty").getInternalMethods().<Date>getTransientValue(),
                is(newDate));


        assertThat(newNode.getProperty(session, "integerProperty").getInternalMethods().<Integer>getTransientValue(),
                is(2));

        assertThat(newNode.getProperty(session, "floatProperty").getInternalMethods().<Float>getTransientValue(),
                is(2.1f));

        assertThat(newNode.getProperty(session, "doubleProperty").getInternalMethods().<Double>getTransientValue(),
                is(2.1d));

        assertThat(newNode.<String>getPropertyValue(session, "stringProperty"),
                is("value"));

        assertThat(newNode.<ExampleEnum>getPropertyValue(session, "enumProperty"),
                is(ExampleEnum.FIRST));

        assertThat(newNode.<Class>getPropertyValue(session, "classProperty").getName(),
                is(String.class.getName()));

        assertThat(newNode.<Date>getPropertyValue(session, "dateProperty"),
                is(newDate));


        assertThat(newNode.<Integer>getPropertyValue(session, "integerProperty"),
                is(2));

        assertThat(newNode.<Float>getPropertyValue(session, "floatProperty"),
                is(2.1f));

        assertThat(newNode.<Double>getPropertyValue(session, "doubleProperty"),
                is(2.1d));


        assertThat(loadedNode1.<String>getPropertyValue(session, "stringProperty"),
                is(nullValue()));

        assertThat(loadedNode1.<ExampleEnum>getPropertyValue(session, "enumProperty"),
                is(nullValue()));

        assertThat(loadedNode1.<Class>getPropertyValue(session, "classProperty"),
                is(nullValue()));


        assertThat(loadedNode1.<Date>getPropertyValue(session, "dateProperty"),
                is(nullValue()));


        assertThat(loadedNode1.<Integer>getPropertyValue(session, "integerProperty"),
                is(nullValue()));

        assertThat(loadedNode1.<Float>getPropertyValue(session, "floatProperty"),
                is(nullValue()));

        assertThat(loadedNode1.<Double>getPropertyValue(session, "doubleProperty"),
                is(nullValue()));

        session.flushTransient();


        assertThat(loadedNode1.<String>getPropertyValue(session, "stringProperty"),
                is("value"));

        assertThat(loadedNode1.<ExampleEnum>getPropertyValue(session, "enumProperty"),
                is(ExampleEnum.FIRST));

        assertThat(loadedNode1.<Class>getPropertyValue(session, "classProperty").getName(),
                is(String.class.getName()));


        assertThat(loadedNode1.<Date>getPropertyValue(session, "dateProperty").toString(),
                is(newDate.toString()));


        assertThat(loadedNode1.<Integer>getPropertyValue(session, "integerProperty"),
                is(2));

        assertThat(loadedNode1.<Float>getPropertyValue(session, "floatProperty"),
                is(2.1f));

        assertThat(loadedNode1.<Double>getPropertyValue(session, "doubleProperty"),
                is(2.1d));


    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotChangePropertyTypeOnList() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        List<Integer> correctList = newArrayList();
        List<String> wrongList = newArrayList();

        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode.getVerifiedOperations().setSerializedListProperty(session, "listProperty", Integer.class, correctList);
        newNode.getVerifiedOperations().setSerializedListProperty(session, "listProperty", String.class, wrongList);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotChangePropertyTypeOnMap() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        Map<String, Integer> correctMap = newHashMap();
        Map<String, String> wrongMap = newHashMap();

        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode.getVerifiedOperations().setSerializedMapProperty(session, "mapProperty", String.class, Integer.class, correctMap);
        newNode.getVerifiedOperations().setSerializedMapProperty(session, "mapProperty", String.class, String.class, wrongMap);

    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotChangePropertyTypeOnStream() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode.getVerifiedOperations().setInputStreamProperty(session, "streamProperty", new ByteArrayInputStream("test".getBytes()));
        newNode.getVerifiedOperations().setSimpleProperty(session, "streamProperty", String.class, "invalidValue");
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotChangePropertyTypeOnSimple() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode.getVerifiedOperations().setSimpleProperty(session, "classProperty", Class.class, String.class);
        newNode.getVerifiedOperations().setSimpleProperty(session, "classProperty", String.class, "invalidValue");
    }

    public enum ExampleEnum {
        FIRST
    }

    @Test
    public void shouldWorkWithSimplePropertiesOnAutoFlush() throws Exception {

        Date newDate = new Date();
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode.getVerifiedOperations().setSimpleProperty(session, "classProperty", Class.class, String.class);
        newNode.getVerifiedOperations().setSimpleProperty(session, "enumProperty", ExampleEnum.class, ExampleEnum.FIRST);
        newNode.getVerifiedOperations().setSimpleProperty(session, "stringProperty", String.class, "value");
        newNode.getVerifiedOperations().setSimpleProperty(session, "dateProperty", Date.class, newDate);
        newNode.getVerifiedOperations().setSimpleProperty(session, "integerProperty", Integer.class, 2);
        newNode.getVerifiedOperations().setSimpleProperty(session, "floatProperty", Float.class, 2.1f);
        newNode.getVerifiedOperations().setSimpleProperty(session, "doubleProperty", Double.class, 2.1d);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat(newNode.getProperty(session, "enumProperty").getInternalMethods().<ExampleEnum>getTransientValue(),
                is(ExampleEnum.FIRST));
        assertThat(newNode.getProperty(session, "classProperty").getInternalMethods().<Class>getTransientValue().getName(),
                is(String.class.getName()));
        assertThat(newNode.getProperty(session, "stringProperty").getInternalMethods().<String>getTransientValue(),
                is("value"));

        assertThat(newNode.getProperty(session, "dateProperty").getInternalMethods().<Date>getTransientValue().toString(),
                is(newDate.toString()));


        assertThat(newNode.getProperty(session, "integerProperty").getInternalMethods().<Integer>getTransientValue(),
                is(2));

        assertThat(newNode.getProperty(session, "floatProperty").getInternalMethods().<Float>getTransientValue(),
                is(2.1f));

        assertThat(newNode.getProperty(session, "doubleProperty").getInternalMethods().<Double>getTransientValue(),
                is(2.1d));

        assertThat(newNode.<String>getPropertyValue(session, "stringProperty"),
                is("value"));

        assertThat(newNode.<ExampleEnum>getPropertyValue(session, "enumProperty"),
                is(ExampleEnum.FIRST));

        assertThat(newNode.<Class>getPropertyValue(session, "classProperty").getName(),
                is(String.class.getName()));

        assertThat(newNode.<Date>getPropertyValue(session, "dateProperty").toString(),
                is(newDate.toString()));


        assertThat(newNode.<Integer>getPropertyValue(session, "integerProperty"),
                is(2));

        assertThat(newNode.<Float>getPropertyValue(session, "floatProperty"),
                is(2.1f));

        assertThat(newNode.<Double>getPropertyValue(session, "doubleProperty"),
                is(2.1d));


        assertThat(loadedNode.<String>getPropertyValue(session, "stringProperty"),
                is("value"));

        assertThat(loadedNode.<ExampleEnum>getPropertyValue(session, "enumProperty"),
                is(ExampleEnum.FIRST));

        assertThat(loadedNode.<Class>getPropertyValue(session, "classProperty").getName(),
                is(String.class.getName()));


        assertThat(loadedNode.<Date>getPropertyValue(session, "dateProperty").toString(),
                is(newDate.toString()));


        assertThat(loadedNode.<Integer>getPropertyValue(session, "integerProperty"),
                is(2));

        assertThat(loadedNode.<Float>getPropertyValue(session, "floatProperty"),
                is(2.1f));

        assertThat(loadedNode.<Double>getPropertyValue(session, "doubleProperty"),
                is(2.1d));


        STNodeEntry anotherLoadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1")
                .withProperty("stringProperty").equals(String.class, "value").buildCriteria().andFindUnique(session);

        assertThat(anotherLoadedNode, is(loadedNode));

        STNodeEntry noLoadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1")
                .withProperty("stringProperty").equals(String.class, "invalid").buildCriteria().andFindUnique(session);

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
    public void shouldWorkWithSerializedPojoPropertiesOnAutoFlush() throws Exception {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        PojoClass pojo1 = new PojoClass();
        pojo1.setAnInt(3);
        pojo1.setaString("a string");

        newNode.getVerifiedOperations().setSerializedPojoProperty(session, "pojoProperty", PojoClass.class, pojo1);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat(newNode.getProperty(session, "pojoProperty").getInternalMethods().<PojoClass>getTransientValue(),
                is(pojo1));

        PojoClass loaded1 = loadedNode.<PojoClass>getPropertyValue(session, "pojoProperty");

        PojoClass loaded2 = loadedNode.getProperty(session, "pojoProperty").<PojoClass>getValueAs(session, PojoClass.class);

        assertThat(loaded1, is(pojo1));
        assertThat(loaded2, is(pojo1));

    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnExplicitFlush() throws Exception {


        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.getVerifiedOperations().setInputStreamProperty(session, "streamProperty", stream);


        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);

        assertThat(nullNode, is(nullValue()));
        session.flushTransient();
        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat(IOUtils.contentEquals(newNode.getProperty(session, "streamProperty").getInternalMethods().<InputStream>getTransientValue(), stream),
                is(true));

        InputStream loaded1 = loadedNode.<InputStream>getPropertyValue(session, "streamProperty");

        ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        String asString1 = new String(temporary1.toByteArray());
        ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        InputStream loaded2 = loadedNode.getProperty(session, "streamProperty").<InputStream>getValueAs(session, InputStream.class);

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
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.getVerifiedOperations().setInputStreamProperty(session, "streamProperty", stream);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat(IOUtils.contentEquals(newNode.getProperty(session, "streamProperty").getInternalMethods().<InputStream>getTransientValue(), stream),
                is(true));

        InputStream loaded1 = loadedNode.<InputStream>getPropertyValue(session, "streamProperty");

        ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        String asString1 = new String(temporary1.toByteArray());
        ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        InputStream loaded2 = loadedNode.getProperty(session, "streamProperty").<InputStream>getValueAs(session, InputStream.class);

        IOUtils.copy(loaded2, temporary2);
        String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1,
                is("streamValue"));
        assertThat(asString2,
                is("streamValue"));


    }

    @Test
    public void shouldWorkWithSerializedListPropertiesOnExplicitFlush() throws Exception {

        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        List<String> aList = asList("1", "2", "3");
        newNode.getVerifiedOperations().setSerializedListProperty(session, "listProperty", String.class, aList);

        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);
        assertThat(nullNode, is(nullValue()));
        session.flushTransient();
        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat((Object) newNode.getProperty(session, "listProperty").getInternalMethods().<List>getTransientValue(),
                is((Object) aList));

        List<String> loaded1 = loadedNode.<List>getPropertyValue(session, "listProperty");

        List<String> loaded2 = loadedNode.getProperty(session, "listProperty").<List>getValueAs(session, List.class);

        assertThat((Object) loaded1, is((Object) aList));
        assertThat((Object) loaded2, is((Object) aList));
    }

    @Test
    public void shouldWorkWithSerializedListPropertiesOnAutoFlush() throws Exception {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        List<String> aList = asList("1", "2", "3");
        newNode.getVerifiedOperations().setSerializedListProperty(session, "listProperty", String.class, aList);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat((Object) newNode.getProperty(session, "listProperty").getInternalMethods().<List>getTransientValue(),
                is((Object) aList));

        List<String> loaded1 = loadedNode.<List>getPropertyValue(session, "listProperty");

        List<String> loaded2 = loadedNode.getProperty(session, "listProperty").<List>getValueAs(session, List.class);

        assertThat((Object) loaded1, is((Object) aList));
        assertThat((Object) loaded2, is((Object) aList));
    }

    @Test
    public void shouldWorkWithSerializedSetPropertiesOnExplicitFlush() throws Exception {

        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        Set<String> aSet = ImmutableSet.of("1", "2", "3");
        newNode.getVerifiedOperations().setSerializedSetProperty(session, "setProperty", String.class, aSet);


        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);

        assertThat(nullNode, is(nullValue()));
        session.flushTransient();

        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat((Object) newNode.getProperty(session, "setProperty").getInternalMethods().<Set>getTransientValue(),
                is((Object) aSet));

        Set<String> loaded1 = loadedNode.<Set>getPropertyValue(session, "setProperty");

        Set<String> loaded2 = loadedNode.getProperty(session, "setProperty").<Set>getValueAs(session, Set.class);

        assertThat((Object) loaded1, is((Object) aSet));
        assertThat((Object) loaded2, is((Object) aSet));
    }

    @Test
    public void shouldWorkWithSerializedSetPropertiesOnAutoFlush() throws Exception {

        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        Set<String> aSet = ImmutableSet.of("1", "2", "3");
        newNode.getVerifiedOperations().setSerializedSetProperty(session, "setProperty", String.class, aSet);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat((Object) newNode.getProperty(session, "setProperty").getInternalMethods().<Set>getTransientValue(),
                is((Object) aSet));

        Set<String> loaded1 = loadedNode.<Set>getPropertyValue(session, "setProperty");

        Set<String> loaded2 = loadedNode.getProperty(session, "setProperty").<Set>getValueAs(session, Set.class);

        assertThat((Object) loaded1, is((Object) aSet));
        assertThat((Object) loaded2, is((Object) aSet));
    }

    @Test
    public void shouldWorkWithSerializedMapPropertiesOnExplicitFlush() throws Exception {

        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        Map<String, Integer> aMap = ImmutableMap.<String, Integer>builder().put("1", 1).put("2", 2).build();
        newNode.getVerifiedOperations().setSerializedMapProperty(session, "mapProperty", String.class, Integer.class, aMap);


        STNodeEntry nullNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);

        assertThat(nullNode, is(nullValue()));
        session.flushTransient();
        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat((Object) newNode.getProperty(session, "mapProperty").getInternalMethods().<Map>getTransientValue(),
                is((Object) aMap));

        Map<String, Integer> loaded1 = loadedNode.<Map>getPropertyValue(session, "mapProperty");

        Map<String, Integer> loaded2 = loadedNode.getProperty(session, "mapProperty").<Map>getValueAs(session, Map.class);

        assertThat((Object) loaded1, is((Object) aMap));
        assertThat((Object) loaded2, is((Object) aMap));
    }

    @Test
    public void shouldWorkWithSerializedMapPropertiesOnAutoFlush() throws Exception {


        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();

        Map<String, Integer> aMap = ImmutableMap.<String, Integer>builder().put("1", 1).put("2", 2).build();
        newNode.getVerifiedOperations().setSerializedMapProperty(session, "mapProperty", String.class, Integer.class, aMap);


        STNodeEntry loadedNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat((Object) newNode.getProperty(session, "mapProperty").getInternalMethods().<Map>getTransientValue(),
                is((Object) aMap));

        Map<String, Integer> loaded1 = loadedNode.<Map>getPropertyValue(session, "mapProperty");

        Map<String, Integer> loaded2 = loadedNode.getProperty(session, "mapProperty").<Map>getValueAs(session, Map.class);

        assertThat((Object) loaded1, is((Object) aMap));
        assertThat((Object) loaded2, is((Object) aMap));
    }


    @Test
    public void shouldFindMultipleResults() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode3 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "another name").andCreate();
        STNodeEntry newNode4 = session.withPartition(ExamplePartition.DEFAULT).createWithName("anotherName").withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();

        Set<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeEntry("newNode1").withProperty("name")
                .equals(String.class, "name").buildCriteria().andFind(session);

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
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();

        Set<STNodeEntry> result = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.removeNode(session);
        Set<STNodeEntry> newResult = session.withPartition(ExamplePartition.DEFAULT).findNamed("newNode1");
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));


    }

    @Test
    public void shouldRemoveNodesOnExplicitFlush() throws Exception {
        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();
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
    }

    @Test
    public void shouldDiscardTransientNodesOnExplicitFlush() throws Exception {
        STStorageSession session = explicitFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry newNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 2)
                .withKey("name", String.class, "name").andCreate();
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
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "firstValue");
        Set<STNodeEntry> found = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeEntry("newNode1").withProperty("parameter").equals(String.class, "firstValue")
                .buildCriteria().andFind(session);
        assertThat(found.size(), is(1));
        assertThat(found.contains(newNode1), is(true));
        newNode1.getProperty(session, "parameter").setValue(session, "secondValue");

        Set<STNodeEntry> notFound = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeEntry("newNode1").withProperty("parameter").equals(String.class, "firstValue")
                .buildCriteria().andFind(session);
        assertThat(notFound.size(), is(0));

        Set<STNodeEntry> foundAgain = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeEntry("newNode1").withProperty("parameter").equals(String.class, "secondValue")
                .buildCriteria().andFind(session);
        assertThat(foundAgain.size(), is(1));
        assertThat(foundAgain.contains(newNode1), is(true));


    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotSetKeyProperty() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode1.getUnverifiedOperations().setSimpleProperty(session, "sequence", Integer.class, 3);

    }

    @Test
    public void shouldChangePropertyTypesOnUnverifiedOperations() throws Exception {

        PojoClass pojo = new PojoClass();
        List<String> stringList = newArrayList();
        List<Integer> integerList = newArrayList();
        Map<String, String> stringMap = newHashMap();
        Map<Integer, Integer> integerMap = newHashMap();
        Set<String> stringSet = newHashSet();
        Set<Integer> integerSet = newHashSet();
        String streamData = "exampleData";
        InputStream stream = new ByteArrayInputStream(streamData.getBytes());


        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry newNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("newNode1").withKey("key", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode1.getUnverifiedOperations().setSimpleProperty(session, "sequence", Integer.class, 3);
        assertThat(newNode1.<Integer>getPropertyValue(session, "sequence"), is(3));
        newNode1.getUnverifiedOperations().setSimpleProperty(session, "sequence", String.class, "4");
        assertThat(newNode1.<String>getPropertyValue(session, "sequence"), is("4"));
        newNode1.getUnverifiedOperations().setSerializedPojoProperty(session, "sequence", PojoClass.class, pojo);
        assertThat(newNode1.<PojoClass>getPropertyValue(session, "sequence"), is(pojo));
        newNode1.getUnverifiedOperations().setSerializedPojoProperty(session, "sequence", String.class, "I am serializable! That's ok!");
        assertThat(newNode1.<String>getPropertyValue(session, "sequence"), is("I am serializable! That's ok!"));
        newNode1.getUnverifiedOperations().setSerializedListProperty(session, "sequence", String.class, stringList);
        assertThat(newNode1.<List>getPropertyValue(session, "sequence"), is((Object) stringList));
        newNode1.getUnverifiedOperations().setSerializedListProperty(session, "sequence", Integer.class, integerList);
        assertThat(newNode1.<List>getPropertyValue(session, "sequence"), is((Object) integerList));
        newNode1.getUnverifiedOperations().setSerializedSetProperty(session, "sequence", String.class, stringSet);
        assertThat(newNode1.<Set>getPropertyValue(session, "sequence"), is((Object) stringSet));
        newNode1.getUnverifiedOperations().setSerializedSetProperty(session, "sequence", Integer.class, integerSet);
        assertThat(newNode1.<Set>getPropertyValue(session, "sequence"), is((Object) integerSet));
        newNode1.getUnverifiedOperations().setSerializedMapProperty(session, "sequence", String.class, String.class, stringMap);
        assertThat(newNode1.<Map>getPropertyValue(session, "sequence"), is((Object) stringMap));
        newNode1.getUnverifiedOperations().setSerializedMapProperty(session, "sequence", Integer.class, Integer.class, integerMap);
        assertThat(newNode1.<Map>getPropertyValue(session, "sequence"), is((Object) integerMap));
        newNode1.getUnverifiedOperations().setInputStreamProperty(session, "sequence", stream);
        InputStream loaded2 = newNode1.getProperty(session, "sequence").<InputStream>getValueAs(session, InputStream.class);
        ByteArrayOutputStream temporary = new ByteArrayOutputStream();
        IOUtils.copy(loaded2, temporary);
        String contentAsString = new String(temporary.toByteArray());
        assertThat(contentAsString, is(streamData));
        newNode1.getUnverifiedOperations().setSimpleProperty(session, "sequence", Integer.class, 3);
        assertThat(newNode1.<Integer>getPropertyValue(session, "sequence"), is(3));

    }


    @Test
    public void shouldFindByPropertiesWithoutNodeName() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "value");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "value");
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter1", String.class, "value1");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter1", String.class, "value2");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter").equals(String.class, "value").buildCriteria().andFind(session);
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        Set<STNodeEntry> onlyOneNode = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter1").equals(String.class, "value1").buildCriteria().andFind(session);
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }



    @Test
    public void shouldFindByPropertiesContainingStringWithoutNodeName() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "aeiou");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "foo");
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "bar");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter").containsString("io").buildCriteria().andFind(session);
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        assertThat(theSameNodes.size(), is(2));
    }

    @Test
    public void shouldFindByPropertiesStartingWithStringWithoutNodeName() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "iou");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "fooiou");
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "baior");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter").startsWithString("io").buildCriteria().andFind(session);
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        assertThat(theSameNodes.size(), is(2));
    }

    @Test
    public void shouldFindByPropertiesEndingWithStringWithoutNodeName() throws Exception {
        STStorageSession session = autoFlushInjector.getInstance(STStorageSession.class);
        STNodeEntry root1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("abc").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").andCreate();
        STNodeEntry root2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("def").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").andCreate();
        STNodeEntry aNode1 = session.withPartition(ExamplePartition.DEFAULT).createWithName("ghi").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name1").withParent(root1).andCreate();
        STNodeEntry aNode2 = session.withPartition(ExamplePartition.DEFAULT).createWithName("jkl").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name2").withParent(root2).andCreate();
        aNode1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "io");
        aNode2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "uio");
        root1.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "fooiou");
        root2.getVerifiedOperations().setSimpleProperty(session, "parameter", String.class, "baior");
        Set<STNodeEntry> theSameNodes = session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withProperty("parameter").endsWithString("io").buildCriteria().andFind(session);
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        assertThat(theSameNodes.size(), is(2));
    }



}
