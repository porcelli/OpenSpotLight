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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.log.LogableObject;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class ArtifactSource.
 */
@Name("artifact_source")
public class ArtifactSource implements SimpleNodeType, Serializable,
		LogableObject, Schedulable {

	private static final long serialVersionUID = -2430120111043500137L;

	private List<String> cronInformation = new ArrayList<String>();

	/** The repository. */
	private Repository repository;

	/** The active. */
	private boolean active;

	/** The initial lookup. */
	private String initialLookup;

	/** The name. */
	private String name;

	/** The mappings. */
	private Set<ArtifactSourceMapping> mappings = new HashSet<ArtifactSourceMapping>();

	private volatile int hashCode;

	public boolean equals(final Object o) {
		if (!(o instanceof ArtifactSource)) {
			return false;
		}
		final ArtifactSource that = (ArtifactSource) o;
		final boolean result = Equals.eachEquality(Arrays.of(this.getClass(),
				name, repository), Arrays.andOf(that.getClass(), that.name,
				that.repository));
		return result;
	}

	public List<String> getCronInformation() {
		return cronInformation;
	}

	/**
	 * Gets the initial lookup.
	 * 
	 * @return the initial lookup
	 */
	public String getInitialLookup() {
		return initialLookup;
	}

	/**
	 * Gets the mappings.
	 * 
	 * @return the mappings
	 */
	public Set<ArtifactSourceMapping> getMappings() {
		return mappings;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@KeyProperty
	public String getName() {
		return name;
	}

	/**
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	@ParentProperty
	public Repository getRepository() {
		return repository;
	}

	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HashCodes.hashOf(this.getClass(), name, repository);
			hashCode = result;
		}
		return result;
	}

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 * 
	 * @param active
	 *            the new active
	 */
	public void setActive(final boolean active) {
		this.active = active;
	}

	public void setCronInformation(final List<String> cronInformation) {
		this.cronInformation = cronInformation;
	}

	/**
	 * Sets the initial lookup.
	 * 
	 * @param initialLookup
	 *            the new initial lookup
	 */
	public void setInitialLookup(final String initialLookup) {
		this.initialLookup = initialLookup;
	}

	/**
	 * Sets the mappings.
	 * 
	 * @param mappings
	 *            the new mappings
	 */
	public void setMappings(final Set<ArtifactSourceMapping> mappings) {
		this.mappings = mappings;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the repository.
	 * 
	 * @param repository
	 *            the new repository
	 */
	public void setRepository(final Repository repository) {
		this.repository = repository;
	}

	public String toUniqueJobString() {
		return getRepository().getName() + ":" + getName() + ":"
				+ getInitialLookup();
	}

}
