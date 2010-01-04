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
package org.openspotlight.federation.finder;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.PathElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractArtifactFinder.
 */
/**
 * @author feu
 * @param <A>
 */
public abstract class AbstractArtifactFinder<A extends Artifact> implements
		ArtifactFinder<A> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String currentRepository;

	protected AbstractArtifactFinder(final String currentRepository) {
		this.currentRepository = currentRepository;
	}

	private void didLoadArtifact(final A artifact) {
		if (artifact != null) {
			artifact.setRepositoryName(currentRepository);
			logger.info("loaded " + artifact.getArtifactCompleteName());
		}
	}

	private void didLoadArtifacts(final Iterable<A> artifacts) {
		if (artifacts != null) {
			for (final A artifact : artifacts) {
				if (artifact != null) {
					artifact.setRepositoryName(currentRepository);
					logger.info("loaded " + artifact.getArtifactCompleteName());
				}
			}
		}
	}

	public final A findByPath(final String path) {
		final A result = internalFindByPath(path);
		didLoadArtifact(result);
		return result;
	}

	public final A findByRelativePath(final A relativeTo, final String path) {
		final A result = internalFindByRelativePath(relativeTo, path);
		didLoadArtifact(result);
		return result;
	}

	public final String getCurrentRepository() {
		return currentRepository;
	}

	protected abstract A internalFindByPath(String path);

	protected A internalFindByRelativePath(final A relativeTo, final String path) {
		final String newPath = PathElement.createRelativePath(
				relativeTo.getParent(), path).getCompletePath();

		return findByPath(newPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.federation.finder.ArtifactFinder#listByPath(org.
	 * openspotlight.federation.domain.ArtifactSource, java.lang.String)
	 */
	protected Set<A> internalListByPath(final String rawPath) {
		try {
			final Set<A> result = new HashSet<A>();
			final Set<String> allFilePaths = retrieveAllArtifactNames(rawPath);
			for (final String path : allFilePaths) {
				final A sa = findByPath(path);
				result.add(sa);
			}
			return result;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	protected abstract Set<String> internalRetrieveAllArtifactNames(
			String initialPath);

	public final Set<A> listByPath(final String path) {
		final Set<A> result = internalListByPath(path);
		didLoadArtifacts(result);
		return result;
	}

	public final Set<String> retrieveAllArtifactNames(final String initialPath) {
		final Set<String> result = new TreeSet<String>(
				internalRetrieveAllArtifactNames(initialPath));
		logger.info("retrieved names for path " + initialPath + ": " + result);

		return result;
	}

}
