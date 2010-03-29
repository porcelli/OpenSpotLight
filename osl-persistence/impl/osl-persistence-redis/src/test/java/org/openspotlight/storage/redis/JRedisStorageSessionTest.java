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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jredis.JRedis;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
public class JRedisStorageSessionTest {

    private enum ExamplePartition implements STStorageSession.STPartition {

        DEFAULT("default");

        private final String partitionName;

        public String getPartitionName() {
            return partitionName;
        }

        ExamplePartition(String partitionName) {
            this.partitionName = partitionName;
        }
    }

    final Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO, ExamplePartition.DEFAULT));

    @Before
    public void cleanPreviousData() throws Exception {
        injector.getInstance(JRedis.class).flushall();
    }

    @Test
    public void shouldInstantiateOneSessionPerThread() throws Exception {
        STStorageSession session1 = injector.getInstance(STStorageSession.class);
        STStorageSession session2 = injector.getInstance(STStorageSession.class);
        assertThat(session1, is(session2));

        final List<STStorageSession> sessions = new CopyOnWriteArrayList<STStorageSession>();
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread() {
            @Override
            public void run() {
                try {
                    sessions.add(injector.getInstance(STStorageSession.class));
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
    public void shouldInsertNewNodeEntryAndFindUniqueWithAutoFlush() {
        STStorageSession session1 = injector.getInstance(STStorageSession.class);
        STNodeEntry foundNewNode1 = session1.createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session1);
        assertThat(foundNewNode1, is(nullValue()));

        STNodeEntry newNode1 = session1.createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        foundNewNode1 = session1.createCriteria().withNodeEntry("newNode1").withProperty("sequence")
                .equals(Integer.class, 1).withProperty("name").equals(String.class, "name")
                .buildCriteria().andFindUnique(session1);
        assertThat(foundNewNode1, is(notNullValue()));
        assertThat(foundNewNode1, is(newNode1));
    }

    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithExplicitFlush() {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldCreateHierarchyAndLoadParentNode() {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldCreateHierarchyAndLoadChildrenNodes() {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithPartitions() {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldSaveSubPartitionsOnExplicitFlush() {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSimplePropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSimplePropertiesOnAutoFlush() throws Exception {

        Date newDate = new Date();
        STStorageSession session = injector.getInstance(STStorageSession.class);
        STNodeEntry newNode = session.createWithName("newNode1").withKey("sequence", Integer.class, 1)
                .withKey("name", String.class, "name").andCreate();
        newNode.getVerifiedOperations().setSimpleProperty(session, "stringProperty", String.class, "value");
        newNode.getVerifiedOperations().setSimpleProperty(session, "dateProperty", Date.class, newDate);
        newNode.getVerifiedOperations().setSimpleProperty(session, "integerProperty", Integer.class, 2);
        newNode.getVerifiedOperations().setSimpleProperty(session, "floatProperty", Float.class, 2.1f);
        newNode.getVerifiedOperations().setSimpleProperty(session, "doubleProperty", Double.class, 2.1d);

        STNodeEntry loadedNode = session.createCriteria().withNodeEntry("newNode1").withProperty("sequence").equals(Integer.class, 1)
                .withProperty("name").equals(String.class, "name").buildCriteria().andFindUnique(session);


        assertThat(newNode.getProperty(session, "stringProperty").<String>getTransientValue(),
                is("value"));

        assertThat(newNode.getProperty(session, "dateProperty").<Date>getTransientValue(),
                is(newDate));


        assertThat(newNode.getProperty(session, "integerProperty").<Integer>getTransientValue(),
                is(2));

        assertThat(newNode.getProperty(session, "floatProperty").<Float>getTransientValue(),
                is(2.1f));

        assertThat(newNode.getProperty(session, "doubleProperty").<Double>getTransientValue(),
                is(2.1d));

        assertThat(newNode.<String>getPropertyValue(session, "stringProperty"),
                is("value"));

        assertThat(newNode.<Date>getPropertyValue(session, "dateProperty"),
                is(newDate));


        assertThat(newNode.<Integer>getPropertyValue(session, "integerProperty"),
                is(2));

        assertThat(newNode.<Float>getPropertyValue(session, "floatProperty"),
                is(2.1f));

        assertThat(newNode.<Double>getPropertyValue(session, "doubleProperty"),
                is(2.1d));


        assertThat(newNode.<String>getPropertyValue(session, "stringProperty"),
                is(loadedNode.<String>getPropertyValue(session, "stringProperty")));

        assertThat(newNode.<Date>getPropertyValue(session, "dateProperty"),
                is(loadedNode.<Date>getPropertyValue(session, "dateProperty")));


        assertThat(newNode.<Integer>getPropertyValue(session, "integerProperty"),
                is(loadedNode.<Integer>getPropertyValue(session, "integerProperty")));

        assertThat(newNode.<Float>getPropertyValue(session, "floatProperty"),
                is(loadedNode.<Float>getPropertyValue(session, "floatProperty")));

        assertThat(newNode.<Double>getPropertyValue(session, "doubleProperty"),
                is(loadedNode.<Double>getPropertyValue(session, "doubleProperty")));


        STNodeEntry anotherLoadedNode = session.createCriteria().withNodeEntry("newNode1")
                .withProperty("stringProperty").equals(String.class, "value").buildCriteria().andFindUnique(session);

        assertThat(anotherLoadedNode, is(loadedNode));

        STNodeEntry noLoadedNode = session.createCriteria().withNodeEntry("newNode1")
                .withProperty("stringProperty").equals(String.class, "invalid").buildCriteria().andFindUnique(session);

        assertThat(noLoadedNode, is(nullValue()));
    }

    @Test
    public void shouldWorkWithListPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithListPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSetPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSetPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithMapPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithMapPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedPojoPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedPojoPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedListPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedListPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedSetPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedSetPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedMapPropertiesOnExplicitFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithSerializedMapPropertiesOnAutoFlush() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldWorkWithWeakReferences() throws Exception {

        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldFindMultipleResults() throws Exception {

    }

}
