/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
