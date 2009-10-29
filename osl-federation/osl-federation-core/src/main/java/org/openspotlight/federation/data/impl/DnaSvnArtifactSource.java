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

package org.openspotlight.federation.data.impl;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * This bundle class is used to load the file system artifacts using the DNA
 * FileSystem Connector.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
@StaticMetadata(propertyNames = { "active", "initialLookup", "user", "password" }, propertyTypes = {
		Boolean.class, String.class, String.class, String.class }, keyPropertyName = "name", keyPropertyType = String.class, validParentTypes = { Group.class }, validChildrenTypes = {
		BundleProcessorType.class, Group.class, StreamArtifactAboutToChange.class,
		CustomArtifact.class, ArtifactMapping.class })
public class DnaSvnArtifactSource extends ArtifactSource {

	private static final String PASSWORD = "password"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final long serialVersionUID = 6055285487792875923L;

	private static final String USER = "user"; //$NON-NLS-1$

	private final InstanceMetadata instanceMetadata;

	/**
	 * creates a bundle inside this project.
	 * 
	 * @param project
	 * @param name
	 */
	public DnaSvnArtifactSource(final Group project, final String name) {
		super(project, name);
		this.instanceMetadata = super.getInstanceMetadata();
	}

	/**
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return this.instanceMetadata.getProperty(PASSWORD);
	}

	/**
	 * 
	 * @return user name
	 */
	public String getUser() {
		return this.instanceMetadata.getProperty(USER);
	}

	/**
	 * Sets the password
	 * 
	 * @param password
	 */
	public void setPassword(final String password) {
		this.instanceMetadata.setProperty(PASSWORD, password);
	}

	/**
	 * Sets username
	 * 
	 * @param user
	 */
	public void setUser(final String user) {
		this.instanceMetadata.setProperty(USER, user);
	}

}
