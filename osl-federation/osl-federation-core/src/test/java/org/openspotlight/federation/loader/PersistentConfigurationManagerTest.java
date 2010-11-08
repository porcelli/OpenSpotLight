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
package org.openspotlight.federation.loader;

import java.util.Set;

import org.hamcrest.core.Is;
import org.jredis.JRedis;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.util.GroupDifferences;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The Class PersistentConfigurationManagerTest.
 */
public class PersistentConfigurationManagerTest extends
        AbstractConfigurationManagerTest {

    private static JRedis                                            jredis;

    private static SimplePersistCapable<StorageNode, StorageSession> simplePersist;

    @BeforeClass
    public static void setupJcrRepo()
        throws Exception {
        final Injector injector = Guice.createInjector(
                new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                        ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                new SimplePersistModule());
        simplePersist = injector.getInstance(SimplePersistFactory.class)
                .createSimplePersist(RegularPartitions.FEDERATION);
        jredis = injector.getInstance(JRedisFactory.class).getFrom(
                RegularPartitions.FEDERATION);
    }

    @Override
    protected MutableConfigurationManager createNewConfigurationManager() {
        return PersistentConfigurationManagerFactoryImpl
                .createMutableUsingSession(simplePersist);
    }

    @Before
    public void clean()
        throws Exception {
        jredis.flushall();
    }

    @After
    public void closeSession()
        throws Exception {
        // TODO
    }

    @Before
    public void setupSession()
        throws Exception {
        // TODO
    }

    @Test
    public void shouldFindGroupDeltas()
        throws Exception {

        final Repository repository = new Repository();
        repository.setName("newRepository");
        final Group group = new Group();
        group.setName("willBeRemoved");
        group.setRepository(repository);
        repository.getGroups().add(group);
        final MutableConfigurationManager manager1 = createNewConfigurationManager();
        manager1.saveRepository(repository);
        final Group group2 = new Group();
        group2.setName("new");
        group2.setRepository(repository);
        repository.getGroups().add(group2);
        repository.getGroups().remove(group);
        manager1.saveRepository(repository);

        final GroupDifferences differences = GroupSupport.getDifferences(
                simplePersist, repository.getName());
        final Set<String> added = differences.getAddedGroups();

        Assert.assertThat(added.contains("newRepository/new"), Is.is(true));

    }

}
