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
package org.openspotlight.federation.context;

import org.openspotlight.common.DisposingListener;
import org.openspotlight.common.concurrent.AtomicLazyResource;
import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.finder.PersistentArtifactManagerImpl;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.loader.ConfigurationManagerFactoryImpl;
import org.openspotlight.federation.log.DetailedLoggerProvider;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * This class is an {@link ExecutionContext} which initialize all resources in a lazy way, and also close it in a lazy way also.
 *
 * @author feu
 */
public class DefaultExecutionContext implements ExecutionContext, LockContainer {

    private final SimplePersistFactory simplePersistFactory;

    private final STRepositoryPath repositoryPath;

    private final DetailedLoggerProvider detailedLoggerProvider;

    private final class LazyConfigurationManagerProvider extends
            AtomicLazyResource<ConfigurationManager> {
        private LazyConfigurationManagerProvider(
                final LockContainer lockContainer) {
            super(lockContainer);
        }

        @Override
        protected ConfigurationManager createReference() {
            return ConfigurationManagerFactoryImpl
                    .createMutableUsingSession(simplePersistFactory.createSimplePersist(SLPartition.FEDERATION));
        }
    }

    private final class LazyDetailedLoggerProvider extends
            AtomicLazyResource<DetailedLogger> {
        private LazyDetailedLoggerProvider(
                final LockContainer lockContainer) {
            super(lockContainer);
        }

        @Override
        protected DetailedLogger createReference() {
            return detailedLoggerProvider.get();
        }
    }

    private final class LazyGraphSessionProvider extends
            AtomicLazyResource<SLGraphSession> {
        private LazyGraphSessionProvider(
                final LockContainer lockContainer) {
            super(lockContainer);
        }

        @Override
        protected SLGraphSession createReference() throws Exception {
            final SLGraph graph = AbstractFactory.getDefaultInstance(
                    SLGraphFactory.class).createGraph(descriptor);
            return graph.openSession(getUser(), repositoryName);
        }
    }

    private final class LazyJcrConnectionProvider extends
            AtomicLazyResource<JcrConnectionProvider> {
        private LazyJcrConnectionProvider(
                final LockContainer lockContainer) {
            super(lockContainer);
        }

        @Override
        protected JcrConnectionProvider createReference() {
            return JcrConnectionProvider.createFromData(descriptor);
        }
    }

    private final class LazyJcrPersistentArtifactManager extends
            AtomicLazyResource<PersistentArtifactManager> {
        private LazyJcrPersistentArtifactManager(
                final LockContainer lockContainer) {
            super(lockContainer);
        }

        @Override
        protected PersistentArtifactManager createReference() {
            return new PersistentArtifactManagerImpl(repository, simplePersistFactory);
        }
    }

    private final String username;
    private final String password;
    private final JcrConnectionDescriptor descriptor;
    private final String repositoryName;
    private final Repository repository;
    private final DisposingListener<DefaultExecutionContext> listener;
    private final Lock lock = new Lock();

    private final AtomicLazyResource<AuthenticatedUser> lazyAuthenticatedUserReference = new AtomicLazyResource<AuthenticatedUser>() {

        @Override
        protected AuthenticatedUser createReference()
                throws Exception {
            final SecurityFactory securityFactory = AbstractFactory
                    .getDefaultInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser(username);
            final AuthenticatedUser user = securityFactory
                    .createIdentityManager(descriptor).authenticate(simpleUser,
                            password);
            return user;
        }
    };

    private final AtomicLazyResource<JcrConnectionProvider> lazyConnectionProviderReference = new LazyJcrConnectionProvider(
            this);

    private final AtomicLazyResource<PersistentArtifactManager> lazyJcrPersistentArtifactManagerProvider = new LazyJcrPersistentArtifactManager(
            this);

    private final AtomicLazyResource<ConfigurationManager> lazyConfigurationManagerReference = new LazyConfigurationManagerProvider(
            this);

    private final AtomicLazyResource<SLGraphSession> lazyGraphSessionReference = new LazyGraphSessionProvider(
            this);

    private final AtomicLazyResource<DetailedLogger> lazyDetailedLoggerReference = new LazyDetailedLoggerProvider(
            this);


    DefaultExecutionContext(
            final String username, final String password,
            final JcrConnectionDescriptor descriptor,
            final DisposingListener<DefaultExecutionContext> listener,
            Repository repository, SimplePersistFactory simplePersistFactory, DetailedLoggerProvider detailedLoggerProvider) {
        this.username = username;
        this.password = password;
        this.descriptor = descriptor;
        this.simplePersistFactory = simplePersistFactory;
        this.detailedLoggerProvider = detailedLoggerProvider;
        this.repositoryName = repository.getName();
        this.repository = repository;
        this.listener = listener;
        this.repositoryPath = STRepositoryPath.repositoryPath(repository.getName());
    }

    public boolean artifactFinderSupportsThisType(
            final Class<? extends Artifact> type) {
        return true;
    }

    public void closeResources() {
        synchronized (lock) {

            lazyConfigurationManagerReference.closeResources();
            lazyDetailedLoggerReference.closeResources();
            lazyGraphSessionReference.closeResources();
            lazyConnectionProviderReference.closeResources();
            listener.didCloseResource(this);
        }
    }

    public ConfigurationManager getDefaultConfigurationManager() {
        return lazyConfigurationManagerReference.get();
    }

    public JcrConnectionProvider getDefaultConnectionProvider() {
        return lazyConnectionProviderReference.get();
    }

    public SLGraphSession getGraphSession() {
        return lazyGraphSessionReference.get();
    }

    public Lock getLockObject() {
        return lock;
    }

    public DetailedLogger getLogger() {
        return lazyDetailedLoggerReference.get();
    }

    public String getPassword() {
        return password;
    }

    public String getRepository() {
        return repositoryName;
    }

    public AuthenticatedUser getUser() {
        return lazyAuthenticatedUserReference.get();
    }

    public String getUserName() {
        return username;
    }

    private Map<STPartition,SimplePersistCapable<STNodeEntry, STStorageSession>> openedSimplePersists = newHashMap();

    public SimplePersistCapable<STNodeEntry, STStorageSession> getSimplePersist(STPartition partition) {
        SimplePersistCapable<STNodeEntry, STStorageSession> result = openedSimplePersists.get(partition);
        if(result==null){
            result = simplePersistFactory.createSimplePersist(partition);
            openedSimplePersists.put(partition,result);
        }
        return result;
    }

    public STRepositoryPath getRepositoryPath() {
        return repositoryPath;
    }

    public SimplePersistFactory getSimplePersistFactory() {
        return simplePersistFactory;
    }

    public PersistentArtifactManager getPersistentArtifactManager() {
        return lazyJcrPersistentArtifactManagerProvider.get();
    }

}
