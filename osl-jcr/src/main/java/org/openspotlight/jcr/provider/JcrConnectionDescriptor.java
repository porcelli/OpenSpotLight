package org.openspotlight.jcr.provider;

import javax.jcr.Credentials;

public interface JcrConnectionDescriptor {

    public static enum JcrType {
        JACKRABBIT
    }

    public String getConfigurationDirectory();

    public Credentials getCredentials();

    public JcrType getJcrType();

    public String getXmlClasspathLocation();

}
