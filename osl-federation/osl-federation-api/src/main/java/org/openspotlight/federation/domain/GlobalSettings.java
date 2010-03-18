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
package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
@Name("configuration")
public class GlobalSettings implements SimpleNodeType, Serializable {

	private static final long serialVersionUID = 3443359462450366393L;

	private Set<Class<? extends OriginArtifactLoader>> loaderRegistry = new HashSet<Class<? extends OriginArtifactLoader>>();

	public Set<Class<? extends OriginArtifactLoader>> getLoaderRegistry() {
		return loaderRegistry;
	}

	public void setLoaderRegistry(
			Set<Class<? extends OriginArtifactLoader>> loaderRegistry) {
		this.loaderRegistry = loaderRegistry;
	}

	@SuppressWarnings("unchecked")
	private Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> schedulableCommandMap = new HashMap<Class<? extends Schedulable>, Class<? extends SchedulableCommand>>();

	private long defaultSleepingIntervalInMilliseconds;

	/** The max result list size. */
	private int maxResultListSize;

	private String systemUser;

	private String systemPassword;

	public GlobalSettings() {
	}

	public long getDefaultSleepingIntervalInMilliseconds() {
		return defaultSleepingIntervalInMilliseconds;
	}

	/**
	 * Gets the max result list size.
	 * 
	 * @return the max result list size
	 */
	public int getMaxResultListSize() {
		return maxResultListSize;
	}

	@SuppressWarnings("unchecked")
	public Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> getSchedulableCommandMap() {
		return schedulableCommandMap;
	}

	public String getSystemPassword() {
		return systemPassword;
	}

	public String getSystemUser() {
		return systemUser;
	}

	public void setDefaultSleepingIntervalInMilliseconds(
			final long defaultSleepingIntervalInMilliseconds) {
		this.defaultSleepingIntervalInMilliseconds = defaultSleepingIntervalInMilliseconds;
	}

	/**
	 * Sets the max result list size.
	 * 
	 * @param maxResultListSize
	 *            the new max result list size
	 */
	public void setMaxResultListSize(final int maxResultListSize) {
		this.maxResultListSize = maxResultListSize;
	}

	@SuppressWarnings("unchecked")
	public void setSchedulableCommandMap(
			final Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> schedulableCommandMap) {
		this.schedulableCommandMap = schedulableCommandMap;
	}

	public void setSystemPassword(final String systemPassword) {
		this.systemPassword = systemPassword;
	}

	public void setSystemUser(final String systemUser) {
		this.systemUser = systemUser;
	}

}
