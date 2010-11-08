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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.domain.GlobalSettings;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.loader.xml.XmlConfiguration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;

/**
 * A factory for creating XmlConfigurationManager objects.
 */
@Singleton
public class XmlConfigurationManagerFactory implements ConfigurationManagerFactory {

    private final String xmlFileLocation;

    @Inject
    public XmlConfigurationManagerFactory(@XmlConfigFileLocation final String xmlFileLocation) {
        this.xmlFileLocation = xmlFileLocation;
    }

    @Override
    public ImmutableConfigurationManager createImmutable() {
        return loadMutableFromFile(xmlFileLocation);
    }

    @Override
    public MutableConfigurationManager createMutable() {
        return loadMutableFromFile(xmlFileLocation);
    }

    /**
     * The Class ImmutableXmlConfigurationManager.
     */
    private static class ImmutableXmlConfigurationManager implements ImmutableConfigurationManager {

        /**
         * The global settings.
         */
        protected GlobalSettings          globalSettings;

        /**
         * The all repositories.
         */
        protected Map<String, Repository> allRepositories = new HashMap<String, Repository>();

        /**
         * The configuration.
         */
        protected XmlConfiguration        configuration;

        /**
         * Instantiates a new immutable xml configuration manager.
         * 
         * @param newConfiguration the new configuration
         */
        public ImmutableXmlConfigurationManager(
                                                final XmlConfiguration newConfiguration) {
            configuration = newConfiguration;
            refreshConfiguration();
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.federation.loader.MutableConfigurationManager#closeResources ()
         */

        @Override
        public void closeResources() {
            //
        }

        @Override
        public Set<Repository> getAllRepositories()
            throws ConfigurationException {
            final Set<Repository> allNames = new HashSet<Repository>(allRepositories.values());
            return allNames;
        }

        @Override
        public Set<String> getAllRepositoryNames()
            throws ConfigurationException {
            final Set<String> allNames = new HashSet<String>(allRepositories.keySet());
            return allNames;
        }

        /*
         * (non-Javadoc)
         * @seeorg.openspotlight.federation.loader.MutableConfigurationManager# getGlobalSettings()
         */

        @Override
        public GlobalSettings getGlobalSettings() {
            return globalSettings;
        }

        /*
         * (non-Javadoc)
         * @seeorg.openspotlight.federation.loader.MutableConfigurationManager# getRepositoryByName(java.lang.String)
         */

        @Override
        public Repository getRepositoryByName(final String name)
            throws ConfigurationException {
            return allRepositories.get(name);
        }

        /**
         * Refresh configuration.
         */
        protected void refreshConfiguration() {
            globalSettings = configuration.getSettings();
            allRepositories.clear();
            for (final Repository repo: configuration.getRepositories()) {
                allRepositories.put(repo.getName(), repo);
            }
        }

    }

    /**
     * The Class MuttableXmlConfigurationManager.
     */
    private static class MuttableXmlConfigurationManager extends ImmutableXmlConfigurationManager implements
        MutableConfigurationManager {

        /**
         * The x stream.
         */
        private final XStream xStream;

        /**
         * The xml file location.
         */
        private final String  xmlFileLocation;

        /**
         * Instantiates a new muttable xml configuration manager.
         * 
         * @param xStream the x stream
         * @param xmlFileLocation the xml file location
         * @throws Exception the exception
         */
        public MuttableXmlConfigurationManager(
                                               final XStream xStream, final String xmlFileLocation)
            throws Exception {
            super(
                    new File(xmlFileLocation).exists() ? (XmlConfiguration) xStream.fromXML(new FileInputStream(xmlFileLocation))
                        : new XmlConfiguration());
            this.xStream = xStream;
            this.xmlFileLocation = xmlFileLocation;
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.federation.loader.XmlConfigurationManagerFactory .ImmutableXmlConfigurationManager
         * #saveGlobalSettings(org.openspotlight .federation.domain.GlobalSettings)
         */

        @Override
        public void saveGlobalSettings(final GlobalSettings globalSettings)
            throws ConfigurationException {
            configuration.setSettings(globalSettings);
            refreshConfiguration();
            saveXml();
        }

        /*
         * (non-Javadoc)
         * @see org.openspotlight.federation.loader.XmlConfigurationManagerFactory .ImmutableXmlConfigurationManager
         * #saveRepository(org.openspotlight.federation.domain.Repository)
         */

        @Override
        public void saveRepository(final Repository configuration)
            throws ConfigurationException {
            this.configuration.getRepositories().add(configuration);
            refreshConfiguration();
            saveXml();
        }

        /**
         * Save xml.
         * 
         * @throws ConfigurationException the configuration exception
         */
        private void saveXml()
            throws ConfigurationException {
            try {
                final String content = xStream.toXML(configuration);
                final String dir = xmlFileLocation.substring(0, xmlFileLocation.lastIndexOf('/'));
                new File(dir).mkdirs();
                final FileOutputStream fos = new FileOutputStream(xmlFileLocation);
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

    }

    /**
     * Load immutable from xml content.
     * 
     * @param xmlContent the xml content
     * @return the configuration manager
     */
    public static ImmutableConfigurationManager loadImmutableFromXmlContent(final String xmlContent) {
        final XStream xStream = XmlConfigurationManagerFactory.setupXStream();
        final XmlConfiguration configuration = (XmlConfiguration) xStream.fromXML(xmlContent);
        return new ImmutableXmlConfigurationManager(configuration);
    }

    /**
     * Load mutable from file.
     * 
     * @param fileLocation the file location
     * @return the configuration manager
     */
    public static MutableConfigurationManager loadMutableFromFile(final String fileLocation) {
        try {
            final XStream xStream = XmlConfigurationManagerFactory.setupXStream();
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
