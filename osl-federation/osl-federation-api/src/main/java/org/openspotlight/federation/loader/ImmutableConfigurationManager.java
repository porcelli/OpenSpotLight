package org.openspotlight.federation.loader;

import java.util.Set;

import org.openspotlight.common.Disposable;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.domain.GlobalSettings;
import org.openspotlight.domain.Repository;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 6, 2010 Time: 10:26:59 AM To change this template use File | Settings | File
 * Templates.
 */
public interface ImmutableConfigurationManager extends Disposable {

    Iterable<Repository> getAllRepositories()
        throws ConfigurationException;

    Set<String> getAllRepositoryNames()
        throws ConfigurationException;

    /**
     * Gets the global settings.
     * 
     * @return the global settings
     */
    GlobalSettings getGlobalSettings();

    /**
     * Loads the current group from configuration, marking the configuration as saved.
     * 
     * @param name the name
     * @return a fresh configuration
     * @throws ConfigurationException the configuration exception
     */
    Repository getRepositoryByName(String name)
        throws ConfigurationException;
}
