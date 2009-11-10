package org.openspotlight.federation.loader;

/**
 * This interface describes a {@link ConfigurationManager} factory. It should create a new {@link ConfigurationManager} each time
 * the method {@link #getNewInstance()} is called.
 * 
 * @author feu
 */
public interface ConfigurationManagerProvider {

    /**
     * Gets the new instance.
     * 
     * @return the new instance
     */
    public ConfigurationManager getNewInstance();

}
