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
package org.openspotlight.security.idm.store.test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jboss.identity.idm.impl.configuration.IdentityConfigurationImpl;
import org.jboss.identity.idm.impl.configuration.IdentityStoreConfigurationContextImpl;
import org.jboss.identity.idm.impl.configuration.jaxb2.JAXB2IdentityConfiguration;
import org.jboss.identity.idm.impl.store.CommonIdentityStoreTest;
import org.jboss.identity.idm.impl.store.IdentityStoreTestContext;
import org.jboss.identity.idm.spi.configuration.IdentityConfigurationContextRegistry;
import org.jboss.identity.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityConfigurationMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityStoreConfigurationMetaData;
import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.persist.support.SimplePersistFactoryImpl;
import org.openspotlight.security.idm.store.SLIdentityStoreImpl;
import org.openspotlight.security.idm.store.SLIdentityStoreSessionImpl;
import org.openspotlight.security.idm.store.StaticInjector;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class SLIdentityStoreImplTest {

    private static class SLIdStoreTestContext implements IdentityStoreTestContext {

        private SLIdentityStoreImpl        store;

        private SLIdentityStoreSessionImpl session;

        public void begin() throws Exception {

            final IdentityConfigurationMetaData configurationMD = JAXB2IdentityConfiguration.createConfigurationMetaData("slstore.xml");

            final IdentityConfigurationContextRegistry registry = (IdentityConfigurationContextRegistry)new IdentityConfigurationImpl().configure(configurationMD);

            IdentityStoreConfigurationMetaData storeMD = null;

            for (final IdentityStoreConfigurationMetaData metaData : configurationMD.getIdentityStores()) {
                if (metaData.getId().equals("SLStore")) {
                    storeMD = metaData;
                    break;
                }
            }

            final IdentityStoreConfigurationContext context = new IdentityStoreConfigurationContextImpl(configurationMD,
                                                                                                        registry, storeMD);

            this.store = new SLIdentityStoreImpl();
            this.store.bootstrap(context);
            this.session = (SLIdentityStoreSessionImpl)this.store.createIdentityStoreSession();
        }

        public void commit() throws Exception {
            this.session.commitTransaction();

        }

        public void flush() throws Exception {
            this.session.save();

        }

        public IdentityStoreInvocationContext getCtx() {
            return new IdentityStoreInvocationContext() {

                public IdentityStoreSession getIdentityStoreSession() {
                    return SLIdStoreTestContext.this.session;
                }

                public String getRealmId() {
                    return "abc";
                }

                public String getSessionId() {
                    return "abc";
                }
            };
        }

        public IdentityStore getStore() {
            return this.store;
        }

    }

    private final CommonIdentityStoreTest test = new CommonIdentityStoreTest(new SLIdStoreTestContext());

    @Before
    public void clearAllData() throws Exception {

        Injector autoFlushInjector = Guice.createInjector(new JRedisStorageModule(
                                                                                  STStorageSession.STFlushMode.AUTO,
                                                                                  ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                                                                                  repositoryPath("repositoryPath")) {

            @Override
            protected void configure() {
                super.configure();
                bind(SimplePersistFactory.class).to(SimplePersistFactoryImpl.class);
            }
        });
        autoFlushInjector.getInstance(JRedisFactory.class).getFrom(SLPartition.SECURITY).flushall();
        StaticInjector.INSTANCE.setInjector(autoFlushInjector);
    }

    @Test
    public void testAttributes() throws Exception {
        this.test.testAttributes();
    }

    @Test
    @Ignore
    // actually isn't possible to persist "large" binary values on simple
    // persist, since it needs array support and also input stream support.
    public void testBinaryCredential() throws Exception {
        this.test.testBinaryCredential();
    }

    @Test
    public void testCriteria() throws Exception {
        this.test.testCriteria();
    }

    @Test
    public void testFindMethods() throws Exception {
        this.test.testFindMethods();
    }

    @Test
    public void testPasswordCredential() throws Exception {
        this.test.testPasswordCredential();
    }

    @Test
    public void testRelationships() throws Exception {
        this.test.testRelationships();
    }

    @Test
    public void testStorePersistence() throws Exception {
        this.test.testStorePersistence();
    }
}
