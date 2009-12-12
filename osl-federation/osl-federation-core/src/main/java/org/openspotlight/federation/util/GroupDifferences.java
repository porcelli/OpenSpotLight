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
package org.openspotlight.federation.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class GroupDifferences implements SimpleNodeType, Serializable {

	private String repositoryName;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6595595697385787637L;

	private Set<String> addedGroups = new HashSet<String>();

	private Set<String> removedGroups = new HashSet<String>();

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof GroupDifferences)) {
			return false;
		}
		final GroupDifferences that = (GroupDifferences) obj;
		return Equals.eachEquality(Arrays.of(getRepositoryName()), Arrays
				.andOf(that.getRepositoryName()));
	}

	public Set<String> getAddedGroups() {
		return addedGroups;
	}

	public Set<String> getRemovedGroups() {
		return removedGroups;
	}

	@KeyProperty
	public String getRepositoryName() {
		return repositoryName;
	}

	@Override
	public int hashCode() {
		return HashCodes.hashOf(getClass(), repositoryName);
	}

	public void setAddedGroups(final Set<String> addedGroups) {
		this.addedGroups = addedGroups;
	}

	public void setRemovedGroups(final Set<String> removedGroups) {
		this.removedGroups = removedGroups;
	}

	public void setRepositoryName(final String repositoryName) {
		this.repositoryName = repositoryName;
	}

}