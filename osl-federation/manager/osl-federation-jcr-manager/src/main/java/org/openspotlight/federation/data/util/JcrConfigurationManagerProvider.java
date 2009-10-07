package org.openspotlight.federation.data.util;

import javax.jcr.Session;

import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.ConfigurationManagerProvider;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class JcrConfigurationManagerProvider implements ConfigurationManagerProvider {
    private final JcrConnectionProvider connectionProvider;

    public JcrConfigurationManagerProvider(
                                            final JcrConnectionProvider connectionProvider ) {
        this.connectionProvider = connectionProvider;
    }

    public ConfigurationManager getNewInstance() {
        final Session session = this.connectionProvider.openSession();
        final JcrSessionConfigurationManager manager = new JcrSessionConfigurationManager(session);
        return manager;
    }

}
