package org.openspotlight.federation.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

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
    private static class ImmutableXmlConfigurationManager implements ConfigurationManager {

        /** The global settings. */
        protected GlobalSettings          globalSettings;

        /** The all repositories. */
        protected Map<String, Repository> allRepositories = new HashMap<String, Repository>();

        /** The configuration. */
        protected XmlConfiguration        configuration;

        /**
         * Instantiates a new immutable xml configuration manager.
         * 
         * @param newConfiguration the new configuration
         */
        public ImmutableXmlConfigurationManager(
                                                 final XmlConfiguration newConfiguration ) {
            this.configuration = newConfiguration;
            this.refreshConfiguration();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#closeResources()
         */
        public void closeResources() {
            //
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#getGlobalSettings()
         */
        public GlobalSettings getGlobalSettings() {
            return this.globalSettings;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#getRepositoryByName(java.lang.String)
         */
        public Repository getRepositoryByName( final String name ) throws ConfigurationException {
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

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#saveGlobalSettings(org.openspotlight.federation.domain.GlobalSettings)
         */
        public void saveGlobalSettings( final GlobalSettings globalSettings ) throws ConfigurationException {
            throw new UnsupportedOperationException();

        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#saveRepository(org.openspotlight.federation.domain.Repository)
         */
        public void saveRepository( final Repository configuration ) throws ConfigurationException {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * The Class MuttableXmlConfigurationManager.
     */
    private static class MuttableXmlConfigurationManager extends ImmutableXmlConfigurationManager {

        /** The x stream. */
        private final XStream xStream;

        /** The xml file location. */
        private final String  xmlFileLocation;

        /**
         * Instantiates a new muttable xml configuration manager.
         * 
         * @param xStream the x stream
         * @param xmlFileLocation the xml file location
         * @throws Exception the exception
         */
        public MuttableXmlConfigurationManager(
                                                final XStream xStream, final String xmlFileLocation ) throws Exception {
            super(
                  new File(xmlFileLocation).exists() ? (XmlConfiguration)xStream.fromXML(new FileInputStream(xmlFileLocation)) : new XmlConfiguration());
            this.xStream = xStream;
            this.xmlFileLocation = xmlFileLocation;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.XmlConfigurationManagerFactory.ImmutableXmlConfigurationManager#saveGlobalSettings(org.openspotlight.federation.domain.GlobalSettings)
         */
        @Override
        public void saveGlobalSettings( final GlobalSettings globalSettings ) throws ConfigurationException {
            this.configuration.setSettings(globalSettings);
            this.refreshConfiguration();
            this.saveXml();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.XmlConfigurationManagerFactory.ImmutableXmlConfigurationManager#saveRepository(org.openspotlight.federation.domain.Repository)
         */
        @Override
        public void saveRepository( final Repository configuration ) throws ConfigurationException {
            this.configuration.getRepositories().add(configuration);
            this.refreshConfiguration();
            this.saveXml();
        }

        /**
         * Save xml.
         * 
         * @throws ConfigurationException the configuration exception
         */
        private void saveXml() throws ConfigurationException {
            try {
                final String content = this.xStream.toXML(this.configuration);
                final String dir = this.xmlFileLocation.substring(0, this.xmlFileLocation.lastIndexOf('/'));
                new File(dir).mkdirs();
                final FileOutputStream fos = new FileOutputStream(this.xmlFileLocation);
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
    public static ConfigurationManager loadImmutableFromXmlContent( final String xmlContent ) {
        final XStream xStream = setupXStream();
        final XmlConfiguration configuration = (XmlConfiguration)xStream.fromXML(xmlContent);
        return new ImmutableXmlConfigurationManager(configuration);
    }

    /**
     * Load mutable from file.
     * 
     * @param fileLocation the file location
     * @return the configuration manager
     */
    public static ConfigurationManager loadMutableFromFile( final String fileLocation ) {
        try {
            final XStream xStream = setupXStream();
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
        xStream.alias("configuration", XmlConfiguration.class);
        return xStream;
    }

}
