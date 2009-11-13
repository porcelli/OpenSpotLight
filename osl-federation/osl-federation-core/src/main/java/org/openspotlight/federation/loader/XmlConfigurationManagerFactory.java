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

public class XmlConfigurationManagerFactory {

    private static class ImmutableXmlConfigurationManager implements ConfigurationManager {
        protected GlobalSettings          globalSettings;

        protected Map<String, Repository> allRepositories = new HashMap<String, Repository>();

        protected XmlConfiguration        configuration;

        public ImmutableXmlConfigurationManager(
                                                 final XmlConfiguration newConfiguration ) {
            this.configuration = newConfiguration;
            this.refreshConfiguration();
        }

        public void closeResources() {
            //
        }

        public GlobalSettings getGlobalSettings() {
            return this.globalSettings;
        }

        public Repository getRepositoryByName( final String name ) throws ConfigurationException {
            return this.allRepositories.get(name);
        }

        protected void refreshConfiguration() {
            this.globalSettings = this.configuration.getSettings();
            this.allRepositories.clear();
            for (final Repository repo : this.configuration.getRepositories()) {
                this.allRepositories.put(repo.getName(), repo);
            }
        }

        public void saveGlobalSettings( final GlobalSettings globalSettings ) throws ConfigurationException {
            throw new UnsupportedOperationException();

        }

        public void saveRepository( final Repository configuration ) throws ConfigurationException {
            throw new UnsupportedOperationException();
        }

    }

    private static class MuttableXmlConfigurationManager extends ImmutableXmlConfigurationManager {
        private final XStream xStream;
        private final String  xmlFileLocation;

        public MuttableXmlConfigurationManager(
                                                final XStream xStream, final String xmlFileLocation ) throws Exception {
            super(
                  new File(xmlFileLocation).exists() ? (XmlConfiguration)xStream.fromXML(new FileInputStream(xmlFileLocation)) : new XmlConfiguration());
            this.xStream = xStream;
            this.xmlFileLocation = xmlFileLocation;
        }

        @Override
        public void saveGlobalSettings( final GlobalSettings globalSettings ) throws ConfigurationException {
            this.configuration.setSettings(globalSettings);
            this.refreshConfiguration();
            this.saveXml();
        }

        @Override
        public void saveRepository( final Repository configuration ) throws ConfigurationException {
            this.configuration.getRepositories().add(configuration);
            this.refreshConfiguration();
            this.saveXml();
        }

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

    public static ConfigurationManager loadImmutableFromXmlContent( final String xmlContent ) {
        final XStream xStream = setupXStream();
        final XmlConfiguration configuration = (XmlConfiguration)xStream.fromXML(xmlContent);
        return new ImmutableXmlConfigurationManager(configuration);
    }

    public static ConfigurationManager loadMutableFromFile( final String fileLocation ) {
        try {
            final XStream xStream = setupXStream();
            return new MuttableXmlConfigurationManager(xStream, fileLocation);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
        }

    }

    private static XStream setupXStream() {
        final XStream xStream = new XStream();
        xStream.aliasPackage("osl", "org.openspotlight.federation.domain");
        xStream.alias("configuration", XmlConfiguration.class);
        return xStream;
    }

}
