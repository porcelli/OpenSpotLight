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

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.federation.util.GroupSupport.GroupDifferences;
import org.openspotlight.persist.support.SimplePersistSupport;

/**
 * A factory for creating JcrSessionConfigurationManager objects.
 */
public class JcrSessionConfigurationManagerFactory {

	/**
	 * The Class MutableJcrSessionConfigurationManager.
	 */
	private static class MutableJcrSessionConfigurationManager implements
			ConfigurationManager {

		/** The session. */
		private final Session session;

		/** The Constant GLOBAL_SETTINGS_LOCATION. */
		private static final String GLOBAL_SETTINGS_LOCATION = SharedConstants.DEFAULT_JCR_ROOT_NAME
				+ "/config/global";

		/** The Constant REPOSITORIES_LOCATION. */
		private static final String REPOSITORIES_LOCATION = SharedConstants.DEFAULT_JCR_ROOT_NAME
				+ "/config/repositories";

		/**
		 * Instantiates a new mutable jcr session configuration manager.
		 * 
		 * @param session
		 *            the session
		 */
		public MutableJcrSessionConfigurationManager(final Session session) {
			this.session = session;
		}

		private void applyGroupDeltas(final Repository configuration) {
			GroupDifferences existentDeltas = GroupSupport.getDifferences(
					session, configuration.getName());
			if (existentDeltas == null) {
				existentDeltas = new GroupDifferences();
				existentDeltas.setRepositoryName(configuration.getName());
			}
			final Set<Repository> existentRepository = SimplePersistSupport
					.findNodesByProperties(
							MutableJcrSessionConfigurationManager.REPOSITORIES_LOCATION,
							session, Repository.class, LazyType.EAGER,
							new String[] { "name" },
							new Object[] { configuration.getName() });
			Repository old = null;
			if (existentRepository.size() > 1) {
				throw Exceptions
						.logAndReturn(new IllegalStateException(
								"unexpected number of repositories with the same name found"));
			} else if (existentRepository.size() == 1) {
				old = existentRepository.iterator().next();
			}

			GroupSupport.findDifferencesOnAllRepositories(existentDeltas, old,
					configuration);
			GroupSupport.saveDifferences(session, existentDeltas);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.federation.loader.ConfigurationManager#closeResources
		 * ()
		 */
		public void closeResources() {
			if (session.isLive()) {
				session.logout();
			}

		}

		public Set<Repository> getAllRepositories()
				throws ConfigurationException {
			final Set<Repository> repositories = SimplePersistSupport
					.findNodesByProperties(
							MutableJcrSessionConfigurationManager.REPOSITORIES_LOCATION,
							session, Repository.class, LazyType.EAGER,
							new String[] {}, new Object[] {});
			return repositories;
		}

		public Set<String> getAllRepositoryNames()
				throws ConfigurationException {
			final Set<Repository> repositories = SimplePersistSupport
					.findNodesByProperties(
							MutableJcrSessionConfigurationManager.REPOSITORIES_LOCATION,
							session, Repository.class, LazyType.EAGER,
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
				final String nodeName = SimplePersistSupport
						.getJcrNodeName(GlobalSettings.class);
				final String xpath = MutableJcrSessionConfigurationManager.GLOBAL_SETTINGS_LOCATION
						+ "/" + nodeName;
				final Query query = session.getWorkspace().getQueryManager()
						.createQuery(xpath, Query.XPATH);
				final NodeIterator nodeIterator = query.execute().getNodes();
				if (nodeIterator.hasNext()) {
					final Node node = nodeIterator.nextNode();
					final GlobalSettings settings = SimplePersistSupport
							.convertJcrToBean(session, node, LazyType.EAGER);
					return settings;
				}
				return null;
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.openspotlight.federation.loader.ConfigurationManager#
		 * getRepositoryByName(java.lang.String)
		 */
		public Repository getRepositoryByName(final String name)
				throws ConfigurationException {
			try {
				final Set<Repository> repository = SimplePersistSupport
						.findNodesByProperties(
								MutableJcrSessionConfigurationManager.REPOSITORIES_LOCATION,
								session, Repository.class, LazyType.EAGER,
								new String[] { "name" }, new Object[] { name });
				return repository.iterator().next();
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.openspotlight.federation.loader.ConfigurationManager#
		 * saveGlobalSettings
		 * (org.openspotlight.federation.domain.GlobalSettings)
		 */
		public void saveGlobalSettings(final GlobalSettings globalSettings)
				throws ConfigurationException {
			try {
				SimplePersistSupport
						.convertBeanToJcr(
								MutableJcrSessionConfigurationManager.GLOBAL_SETTINGS_LOCATION,
								session, globalSettings);
				session.save();
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.federation.loader.ConfigurationManager#saveRepository
		 * (org.openspotlight.federation.domain.Repository)
		 */
		public void saveRepository(final Repository configuration)
				throws ConfigurationException {
			try {
				applyGroupDeltas(configuration);
				SimplePersistSupport
						.convertBeanToJcr(
								MutableJcrSessionConfigurationManager.REPOSITORIES_LOCATION,
								session, configuration);
				session.save();
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}
		}

	}

	/**
	 * Creates a new JcrSessionConfigurationManager object.
	 * 
	 * @param session
	 *            the session
	 * @return the configuration manager
	 */
	public static ConfigurationManager createMutableUsingSession(
			final Session session) {
		return new MutableJcrSessionConfigurationManager(session);
	}

}
