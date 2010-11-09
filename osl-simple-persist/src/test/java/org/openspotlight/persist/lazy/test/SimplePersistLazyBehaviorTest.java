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

package org.openspotlight.persist.lazy.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistImpl;
import org.openspotlight.storage.PartitionFactory.RegularPartitions;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SimplePersistLazyBehaviorTest {

    final Injector                                    autoFlushInjector =
                                                                            Guice
                                                                                .createInjector(new JRedisStorageModule(
                                                                                                                         StorageSession.FlushMode.AUTO,
                                                                                                                         ExampleRedisConfig.EXAMPLE
                                                                                                                             .getMappedServerConfig()));

    StorageSession                                    session;
    SimplePersistCapable<StorageNode, StorageSession> simplePersist;

    @Before
    public void cleanPreviousData()
        throws Exception {
        final JRedisFactory autoFlushFactory = autoFlushInjector.getInstance(JRedisFactory.class);
        autoFlushFactory.getFrom(RegularPartitions.FEDERATION).flushall();
        session = autoFlushInjector.getInstance(StorageSession.class);
        simplePersist = new SimplePersistImpl(session, RegularPartitions.FEDERATION);
    }

    @Test
    public void shouldLoadSavedValue()
        throws Exception {

        ClassWithLazyProperty bean = new ClassWithLazyProperty();
        bean.setTest("test");

        bean.getBigPojoProperty().setTransient(new SerializablePojoProperty());
        bean.getBigPojoProperty().get(simplePersist).setAnotherProperty("test");
        assertThat(bean.getBigPojoProperty().getMetadata().getTransient(), is(notNullValue()));
        final StorageNode node = simplePersist.convertBeanToNode(bean);
        bean = simplePersist.convertNodeToBean(node);
        assertThat(bean.getBigPojoProperty().get(simplePersist), is(notNullValue()));

    }

    @Test
    public void shouldLooseWeakValue()
        throws Exception {
        final ClassWithLazyProperty bean = new ClassWithLazyProperty();
        bean.setTest("test");
        bean.getBigPojoProperty().getMetadata().setCached(new SerializablePojoProperty());
        bean.getBigPojoProperty().get(null).setAnotherProperty("test");
        assertThat(bean.getBigPojoProperty().get(null), is(notNullValue()));
        System.gc();
        assertThat(bean.getBigPojoProperty().getMetadata().getTransient(), is(nullValue()));

    }

    @Test
    public void shouldSaveTransientValue()
        throws Exception {
        ClassWithLazyProperty bean = new ClassWithLazyProperty();
        bean.setTest("test");

        bean.getBigPojoProperty().setTransient(new SerializablePojoProperty());
        bean.getBigPojoProperty().get(simplePersist).setAnotherProperty("test");
        assertThat(bean.getBigPojoProperty().getMetadata().getTransient(), is(notNullValue()));
        final StorageNode node = simplePersist.convertBeanToNode(bean);

        bean = simplePersist.convertNodeToBean(node);
        assertThat(bean.getBigPojoProperty().getMetadata().getCached(simplePersist), is(notNullValue()));
        assertThat(bean.getBigPojoProperty().getMetadata().getTransient(), is(nullValue()));
    }

}
