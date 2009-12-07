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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.loader.xml.XmlConfiguration;

import com.thoughtworks.xstream.XStream;

/**
 * A factory for creating XmlConfigurationManager objects.
 */
public class XmlConfigurationManagerFactory {

	/**
	 * The Class ImmutableXmlConfigurationManager.
	 */
	private static class ImmutableXmlConfigurationManager implements
			ConfigurationManager {

		/** The global settings. */
		protected GlobalSettings globalSettings;

		/** The all repositories. */
		protected Map<String, Repository> allRepositories = new HashMap<String, Repository>();

		/** The configuration. */
		protected XmlConfiguration configuration;

		/**
		 * Instantiates a new immutable xml configuration manager.
		 * 
		 * @param newConfiguration
		 *            the new configuration
		 */
		public ImmutableXmlConfigurationManager(
				final XmlConfiguration newConfiguration) {
			this.configuration = newConfiguration;
			this.refreshConfiguration();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.federation.loader.ConfigurationManager#closeResources
		 * ()
		 */
		public void closeResources() {
			//
		}

		public Set<Repository> getAllRepositories()
				throws ConfigurationException {
			final Set<Repository> allNames = new HashSet<Repository>(
					this.allRepositories.values());
			return allNames;
		}

		public Set<String> getAllRepositoryNames()
				throws ConfigurationException {
			final Set<String> allNames = new HashSet<String>(
					this.allRepositories.keySet());
			return allNames;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.openspotlight.federation.loader.ConfigurationManager#
		 * getGlobalSettings()
		 */
		public GlobalSettings getGlobalSettings() {
			return this.globalSettings;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.openspotlight.federation.loader.ConfigurationManager#
		 * getRepositoryByName(java.lang.String)
		 */
		public Repository getRepositoryByName(final String name)
				throws ConfigurationException {
			return this.allRepositories.get(name);
		}

		/**
		 * Refresh configuration.
		 */
		protected void refreshConfiguration() {
			this.globalSettings = this.configuration.getSettings();
			this.allRepositories.clear();
			for (final Repository repo : this.configuration.getRepositories()) {
				this.allRepositories.put(repo.getName(), repo);
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
			throw new UnsupportedOperationException();

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
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * The Class MuttableXmlConfigurationManager.
	 */
	private static class MuttableXmlConfigurationManager extends
			ImmutableXmlConfigurationManager {

		/** The x stream. */
		private final XStream xStream;

		/** The xml file location. */
		private final String xmlFileLocation;

		/**
		 * Instantiates a new muttable xml configuration manager.
		 * 
		 * @param xStream
		 *            the x stream
		 * @param xmlFileLocation
		 *            the xml file location
		 * @throws Exception
		 *             the exception
		 */
		public MuttableXmlConfigurationManager(final XStream xStream,
				final String xmlFileLocation) throws Exception {
			super(
					new File(xmlFileLocation).exists() ? (XmlConfiguration) xStream
							.fromXML(new FileInputStream(xmlFileLocation))
							: new XmlConfiguration());
			this.xStream = xStream;
			this.xmlFileLocation = xmlFileLocation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.federation.loader.XmlConfigurationManagerFactory
		 * .ImmutableXmlConfigurationManager
		 * #saveGlobalSettings(org.openspotlight
		 * .federation.domain.GlobalSettings)
		 */
		@Override
		public void saveGlobalSettings(final GlobalSettings globalSettings)
				throws ConfigurationException {
			this.configuration.setSettings(globalSettings);
			this.refreshConfiguration();
			this.saveXml();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.federation.loader.XmlConfigurationManagerFactory
		 * .ImmutableXmlConfigurationManager
		 * #saveRepository(org.openspotlight.federation.domain.Repository)
		 */
		@Override
		public void saveRepository(final Repository configuration)
				throws ConfigurationException {
			this.configuration.getRepositories().add(configuration);
			this.refreshConfiguration();
			this.saveXml();
		}

		/**
		 * Save xml.
		 * 
		 * @throws ConfigurationException
		 *             the configuration exception
		 */
		private void saveXml() throws ConfigurationException {
			try {
				final String content = this.xStream.toXML(this.configuration);
				final String dir = this.xmlFileLocation.substring(0,
						this.xmlFileLocation.lastIndexOf('/'));
				new File(dir).mkdirs();
				final FileOutputStream fos = new FileOutputStream(
						this.xmlFileLocation);
				fos.write(content.getBytes());
				fos.flush();
				fos.close();
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}
		}

	}

	/**
	 * Load immutable from xml content.
	 * 
	 * @param xmlContent
	 *            the xml content
	 * @return the configuration manager
	 */
	public static ConfigurationManager loadImmutableFromXmlContent(
			final String xmlContent) {
		final XStream xStream = XmlConfigurationManagerFactory.setupXStream();
		final XmlConfiguration configuration = (XmlConfiguration) xStream
				.fromXML(xmlContent);
		return new ImmutableXmlConfigurationManager(configuration);
	}

	/**
	 * Load mutable from file.
	 * 
	 * @param fileLocation
	 *            the file location
	 * @return the configuration manager
	 */
	public static ConfigurationManager loadMutableFromFile(
			final String fileLocation) {
		try {
			final XStream xStream = XmlConfigurationManagerFactory
					.setupXStream();
			return new MuttableXmlConfigurationManager(xStream, fileLocation);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
		}

	}

	/**
	 * Setup x stream.
	 * 
	 * @return the x stream
	 */
	private static XStream setupXStream() {
		final XStream xStream = new XStream();
		xStream.aliasPackage("osl", "org.openspotlight.federation.domain");
		xStream.alias("Configuration", XmlConfiguration.class);
		return xStream;
	}

}
