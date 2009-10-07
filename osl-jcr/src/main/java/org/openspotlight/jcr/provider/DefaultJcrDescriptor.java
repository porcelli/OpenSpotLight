package org.openspotlight.jcr.provider;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

public enum DefaultJcrDescriptor implements JcrConnectionDescriptor {

    TEMP_DESCRIPTOR("/tmp/osl-temp-repository", new SimpleCredentials("username", "password".toCharArray()), JcrType.JACKRABBIT,
                    "temp-repository.xml"),
    DEFAULT_DESCRIPTOR("/osl/repository", new SimpleCredentials("username", "password".toCharArray()), JcrType.JACKRABBIT,
                       "postgres-repository.xml");

    private final String      configurationDirectory;

    private final Credentials credentials;
    private final JcrType     jcrType;

    private final String      xmlClasspathLocation;

    private DefaultJcrDescriptor(
                                  final String configurationDirectory, final Credentials credentials, final JcrType jcrType,
                                  final String xmlClasspathLocation ) {
        this.configurationDirectory = configurationDirectory;
        this.credentials = credentials;
        this.jcrType = jcrType;
        this.xmlClasspathLocation = xmlClasspathLocation;
    }

    public String getConfigurationDirectory() {
        return this.configurationDirectory;
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public JcrType getJcrType() {
        return this.jcrType;
    }

    public String getXmlClasspathLocation() {
        return this.xmlClasspathLocation;
    }

}
