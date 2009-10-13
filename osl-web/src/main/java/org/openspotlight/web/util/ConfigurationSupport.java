package org.openspotlight.web.util;

import java.io.InputStream;

import javax.jcr.Session;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.federation.data.NoConfigurationYetException;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.load.XmlConfigurationManager;
import org.openspotlight.federation.util.MarkAllAsDirtyVisitor;

public class ConfigurationSupport {
    public static boolean initializeConfiguration( final boolean forceReload,
                                                   final Session jcrSession ) throws SLException {
        final ConfigurationManager manager = new JcrSessionConfigurationManager(jcrSession);
        boolean firstTime = false;
        try {
            manager.load(LazyType.LAZY);
        } catch (final NoConfigurationYetException e) {
            firstTime = true;
        }
        boolean reloaded = false;
        if (firstTime || forceReload) {
            saveXmlOnJcr(manager);
            reloaded = true;
        }
        return reloaded;
    }

    private static Configuration saveXmlOnJcr( final ConfigurationManager manager ) throws SLException {
        Configuration configuration;
        final InputStream is = ClassPathResource.getResourceFromClassPath("osl-configuration.xml");
        final XmlConfigurationManager xmlManager = new XmlConfigurationManager(is);
        configuration = xmlManager.load(LazyType.EAGER);
        configuration.getInstanceMetadata().accept(new MarkAllAsDirtyVisitor());
        manager.save(configuration);
        return configuration;
    }
}
