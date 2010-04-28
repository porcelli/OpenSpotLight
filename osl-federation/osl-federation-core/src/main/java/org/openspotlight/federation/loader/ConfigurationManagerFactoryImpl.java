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
package org.openspotlight.federation.loader;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.util.GroupDifferences;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A factory for creating JcrSessionConfigurationManager objects.
 */
public class ConfigurationManagerFactoryImpl {

    /**
     * The Class MutableConfigurationManager.
     */
    private static class MutableConfigurationManager implements ConfigurationManager {

        /**
         * The session.
         */
        private final SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist;

        /**
         * The Constant globalSettingsRootNode.
         */
        private final STNodeEntry                                         globalSettingsRootNode;
        /**
         * The Constant repositoriesRootNode.
         */
        private final STNodeEntry                                         repositoriesRootNode;

        /**
         * Instantiates a new mutable jcr session configuration manager.
         */
        public MutableConfigurationManager(
                                            final SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist ) {
            this.simplePersist = simplePersist;
            this.globalSettingsRootNode = simplePersist.getPartitionMethods().createNewSimpleNode("configuration",
                                                                                                  "global-settings");
            this.repositoriesRootNode = simplePersist.getPartitionMethods().createNewSimpleNode("configuration", "repositories");
        }

        private void applyGroupDeltas( final Repository configuration ) {
            GroupDifferences existentDeltas = GroupSupport.getDifferences(simplePersist, configuration.getName());
            if (existentDeltas == null) {
                existentDeltas = new GroupDifferences();
                existentDeltas.setRepositoryName(configuration.getName());
            }
            final Iterable<Repository> existentRepository = simplePersist.findByProperties(repositoriesRootNode,
                                                                                           Repository.class,
                                                                                           new String[] {"name"},
                                                                                           new Object[] {configuration.getName()});
            Iterator<Repository> it = existentRepository.iterator();
            Repository old = null;
            if (it.hasNext()) {
                old = it.next();
            }

            GroupSupport.findDifferencesOnAllRepositories(existentDeltas, old, configuration);
            GroupSupport.saveDifferences(simplePersist, existentDeltas);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.openspotlight.federation.loader.ConfigurationManager#closeResources
         * ()
         */

        public void closeResources() {

        }

        public Iterable<Repository> getAllRepositories() throws ConfigurationException {
            final Iterable<Repository> repositories = simplePersist.findByProperties(repositoriesRootNode, Repository.class,
                                                                                     new String[] {}, new Object[] {});
            return repositories;
        }

        public Set<String> getAllRepositoryNames() throws ConfigurationException {
            final Iterable<Repository> repositories = simplePersist.findByProperties(repositoriesRootNode, Repository.class,
                                                                                     new String[] {}, new Object[] {});
            final Set<String> repositoryNames = new HashSet<String>();
            for (final Repository repo : repositories) {
                repositoryNames.add(repo.getName());
            }
            return repositoryNames;
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.openspotlight.federation.loader.ConfigurationManager#
         * getGlobalSettings()
         */

        public GlobalSettings getGlobalSettings() {
            try {
                GlobalSettings settings = simplePersist.findUnique(GlobalSettings.class);
                return settings;

            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.openspotlight.federation.loader.ConfigurationManager#
         * getRepositoryByName(java.lang.String)
         */

        public Repository getRepositoryByName( final String name ) throws ConfigurationException {
            try {
                final Repository repository = simplePersist.findUniqueByProperties(repositoriesRootNode, Repository.class,
                                                                                   new String[] {"name"}, new Object[] {name});

                return repository;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.openspotlight.federation.loader.ConfigurationManager#
         * saveGlobalSettings
         * (org.openspotlight.federation.domain.GlobalSettings)
         */

        public void saveGlobalSettings( final GlobalSettings globalSettings ) throws ConfigurationException {
            try {
                simplePersist.convertBeanToNode(globalSettingsRootNode, globalSettings);
                simplePersist.getCurrentSession().flushTransient();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.openspotlight.federation.loader.ConfigurationManager#saveRepository
         * (org.openspotlight.federation.domain.Repository)
         */

        public void saveRepository( final Repository configuration ) throws ConfigurationException {
            try {
                applyGroupDeltas(configuration);
                simplePersist.convertBeanToNode(repositoriesRootNode, configuration);
                simplePersist.getCurrentSession().flushTransient();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

    }

    /**
     * Creates a new JcrSessionConfigurationManager object.
     * 
     * @param simplePersist the simplePersist
     * @return the configuration manager
     */
    public static ConfigurationManager createMutableUsingSession( final SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist ) {
        return new MutableConfigurationManager(simplePersist);
    }

}
