package org.openspotlight.federation.data.util;

import javax.jcr.Session;

import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.ConfigurationManagerProvider;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

/**
 * This configuration manager provider creates new {@link JcrSessionConfigurationManager} when the {@link #getNewInstance()}
 * method is called.
 * 
 * @author feu
 */
public class JcrConfigurationManagerProvider implements ConfigurationManagerProvider {

    /** The connection provider. */
    private final JcrConnectionProvider connectionProvider;

    /**
     * Instantiates a new jcr configuration manager provider.
     * 
     * @param connectionProvider the connection provider
     */
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
