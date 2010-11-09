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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.domain.GlobalSettings;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.util.GroupDifferences;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.PartitionFactory.RegularPartitions;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;

import com.google.inject.Inject;

/**
 * A factory for creating JcrSessionConfigurationManager objects.
 */
public class PersistentConfigurationManagerFactoryImpl implements ConfigurationManagerFactory {

    private static class ImmutablePersistentConfigurationManager implements
            org.openspotlight.federation.loader.ImmutableConfigurationManager {

        /**
         * The Constant globalSettingsRootNode.
         */
        protected final StorageNode                                       globalSettingsRootNode;

        /**
         * The Constant repositoriesRootNode.
         */
        protected final StorageNode                                       repositoriesRootNode;
        /**
         * The session.
         */
        protected final SimplePersistCapable<StorageNode, StorageSession> simplePersist;

        /**
         * Instantiates a new mutable jcr session configuration manager.
         */
        public ImmutablePersistentConfigurationManager(
                                                       final SimplePersistCapable<StorageNode, StorageSession> simplePersist) {
            this.simplePersist = simplePersist;
            globalSettingsRootNode = simplePersist.getPartitionMethods()
                    .createNewSimpleNode("configuration", "global-settings");
            repositoriesRootNode = simplePersist.getPartitionMethods()
                    .createNewSimpleNode("configuration", "repositories");
        }

        protected void applyGroupDeltas(final Repository configuration) {
            GroupDifferences existentDeltas = GroupSupport.getDifferences(
                    simplePersist, configuration.getName());
            if (existentDeltas == null) {
                existentDeltas = new GroupDifferences();
                existentDeltas.setRepositoryName(configuration.getName());
            }
            final Iterable<Repository> existentRepository = simplePersist
                    .findByProperties(repositoriesRootNode, Repository.class,
                            new String[] {"name"},
                            new Object[] {configuration.getName()});
            final Iterator<Repository> it = existentRepository.iterator();
            Repository old = null;
            if (it.hasNext()) {
                old = it.next();
            }

            GroupSupport.findDifferencesOnAllRepositories(existentDeltas, old,
                    configuration);
            GroupSupport.saveDifferences(simplePersist, existentDeltas);
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.federation.loader.MutableConfigurationManager#closeResources ()
         */

        @Override
        public void closeResources() {
            simplePersist.closeResources();
        }

        @Override
        public Iterable<Repository> getAllRepositories()
                throws ConfigurationException {
            final Iterable<Repository> repositories = simplePersist
                    .findByProperties(repositoriesRootNode, Repository.class,
                            new String[] {}, new Object[] {});
            return repositories;
        }

        @Override
        public Set<String> getAllRepositoryNames()
                throws ConfigurationException {
            final Iterable<Repository> repositories = simplePersist
                    .findByProperties(repositoriesRootNode, Repository.class,
                            new String[] {}, new Object[] {});
            final Set<String> repositoryNames = new HashSet<String>();
            for (final Repository repo: repositories) {
                repositoryNames.add(repo.getName());
            }
            return repositoryNames;
        }

        /*
         * (non-Javadoc)
         * @seeorg.openspotlight.federation.loader.MutableConfigurationManager# getGlobalSettings()
         */

        @Override
        public GlobalSettings getGlobalSettings() {
            try {
                final GlobalSettings settings = simplePersist
                        .findUnique(GlobalSettings.class);
                return settings;

            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e,
                        ConfigurationException.class);
            }

        }

        /*
         * (non-Javadoc)
         * @seeorg.openspotlight.federation.loader.MutableConfigurationManager# getRepositoryByName(java.lang.String)
         */

        @Override
        public Repository getRepositoryByName(final String name)
                throws ConfigurationException {
            try {
                final Repository repository = simplePersist
                        .findUniqueByProperties(repositoriesRootNode,
                                Repository.class, new String[] {"name"},
                                new Object[] {name});

                return repository;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e,
                        ConfigurationException.class);
            }
        }

    }

    /**
     * The Class MutableConfigurationManager.
     */
    private static class MutablePersistentConfigurationManager extends ImmutablePersistentConfigurationManager implements
            org.openspotlight.federation.loader.MutableConfigurationManager {

        public MutablePersistentConfigurationManager(
                                                     final SimplePersistCapable<StorageNode, StorageSession> simplePersist) {
            super(simplePersist);
        }

        /*
         * (non-Javadoc)
         * @seeorg.openspotlight.federation.loader.MutableConfigurationManager# saveGlobalSettings
         * (org.openspotlight.federation.domain.GlobalSettings)
         */

        @Override
        public void saveGlobalSettings(final GlobalSettings globalSettings)
                throws ConfigurationException {
            try {
                simplePersist.convertBeanToNode(globalSettingsRootNode,
                        globalSettings);
                simplePersist.getCurrentSession().flushTransient();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e,
                        ConfigurationException.class);
            }
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.federation.loader.MutableConfigurationManager#saveRepository
         * (org.openspotlight.federation.domain.Repository)
         */

        @Override
        public void saveRepository(final Repository configuration)
                throws ConfigurationException {
            try {
                applyGroupDeltas(configuration);
                simplePersist.convertBeanToNode(repositoriesRootNode,
                        configuration);
                simplePersist.getCurrentSession().flushTransient();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e,
                        ConfigurationException.class);
            }
        }

    }

    private final SimplePersistFactory factory;

    @Inject
    public PersistentConfigurationManagerFactoryImpl(final SimplePersistFactory factory) {
        this.factory = factory;
    }

    public static org.openspotlight.federation.loader.MutableConfigurationManager
        createMutableUsingSession(
                                  final SimplePersistCapable<StorageNode, StorageSession> simplePersist) {
        return new MutablePersistentConfigurationManager(simplePersist);
    }

    @Override
    public ImmutableConfigurationManager createImmutable() {
        return createMutableUsingSession(factory.createSimplePersist(RegularPartitions.FEDERATION));
    }

    @Override
    public org.openspotlight.federation.loader.MutableConfigurationManager createMutable() {
        return createMutableUsingSession(factory.createSimplePersist(RegularPartitions.FEDERATION));
    }

}
