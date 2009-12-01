package org.openspotlight.jcr.provider;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

/**
 * The Enum DefaultJcrDescriptor contains the defaults
 * {@link JcrConnectionDescriptor descriptors} used inside this project.
 */
public enum DefaultJcrDescriptor implements JcrConnectionDescriptor {

	/** The TEM p_ descriptor. */
	TEMP_DESCRIPTOR("TEMP_DIR/osl-temp-repository", new SimpleCredentials(
			"username", "password".toCharArray()), JcrType.JACKRABBIT,
			"temp-repository.xml", true),

	/** The DEFAUL t_ descriptor. */
	DEFAULT_DESCRIPTOR("/osl/repository", new SimpleCredentials("username",
			"password".toCharArray()), JcrType.JACKRABBIT,
			"postgres-repository.xml", false);

	/** The configuration directory. */
	private final String configurationDirectory;

	/** The credentials. */
	private final Credentials credentials;

	/** The jcr type. */
	private final JcrType jcrType;

	/** The xml classpath location. */
	private final String xmlClasspathLocation;

	private final boolean temporary;

	/**
	 * Instantiates a new default jcr descriptor.
	 * 
	 * @param configurationDirectory
	 *            the configuration directory
	 * @param credentials
	 *            the credentials
	 * @param jcrType
	 *            the jcr type
	 * @param xmlClasspathLocation
	 *            the xml classpath location
	 */
	private DefaultJcrDescriptor(final String configurationDirectory,
			final Credentials credentials, final JcrType jcrType,
			final String xmlClasspathLocation, final boolean temporary) {
		String newConfigurationDir = configurationDirectory;
		if (configurationDirectory.contains("TEMP_DIR")) {
			String tempDir = System.getProperty("java.io.tmpdir");
			tempDir = tempDir.replaceAll("[\\\\]", "/");
			if (tempDir.endsWith("/")) {
				tempDir = tempDir.substring(0, tempDir.length() - 1);
			}
			newConfigurationDir = configurationDirectory.replaceAll("TEMP_DIR",
					tempDir);
		}

		this.configurationDirectory = newConfigurationDir;
		this.credentials = credentials;
		this.jcrType = jcrType;
		this.xmlClasspathLocation = xmlClasspathLocation;
		this.temporary = temporary;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.jcr.provider.JcrConnectionDescriptor#
	 * getConfigurationDirectory()
	 */
	public String getConfigurationDirectory() {
		return this.configurationDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.jcr.provider.JcrConnectionDescriptor#getCredentials()
	 */
	public Credentials getCredentials() {
		return this.credentials;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.jcr.provider.JcrConnectionDescriptor#getJcrType()
	 */
	public JcrType getJcrType() {
		return this.jcrType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.jcr.provider.JcrConnectionDescriptor#
	 * getXmlClasspathLocation()
	 */
	public String getXmlClasspathLocation() {
		return this.xmlClasspathLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.jcr.provider.JcrConnectionDescriptor#isTemporary()
	 */
	public boolean isTemporary() {
		return this.temporary;
	}

}
