package org.openspotlight.jcr.provider;

import javax.jcr.Credentials;

/**
 * The Interface JcrConnectionDescriptor is used to describe an Jcr Connection.
 */
public interface JcrConnectionDescriptor {

    /**
     * The Enum JcrType.
     */
    public static enum JcrType {

        /** The JACKRABBIT. */
        JACKRABBIT
    }

    /**
     * Gets the configuration directory.
     * 
     * @return the configuration directory
     */
    public String getConfigurationDirectory();

    /**
     * Gets the credentials.
     * 
     * @return the credentials
     */
    public Credentials getCredentials();

    /**
     * Gets the jcr type.
     * 
     * @return the jcr type
     */
    public JcrType getJcrType();

    /**
     * Gets the xml classpath location.
     * 
     * @return the xml classpath location
     */
    public String getXmlClasspathLocation();

    /**
     * Checks if is temporary.
     * 
     * @return true, if is temporary
     */
    public boolean isTemporary();

}
