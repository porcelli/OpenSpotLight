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

import java.util.Set;

import javax.jcr.Session;

import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.artifact.Artifact;

// TODO: Auto-generated Javadoc
/**
 * The Interface StreamArtifactFinder.
 */
public interface ArtifactFinder<A extends Artifact> extends Disposable {

	/**
	 * This method verifies if the artifact with given name is changed comparing
	 * with the old one. It may return false yes, but never false no. It is used
	 * to get few optimizations on artifact loading process.
	 * 
	 * @param artifactName
	 * @param oldOne
	 * @return
	 */
	public boolean isMaybeChanged(String artifactName, A oldOne);

	/**
	 * Find by path.
	 * 
	 * @param path
	 *            the path
	 * @param artifactSource
	 *            the artifact source
	 * @return the stream artifact
	 */
	public A findByPath(String path);

	/**
	 * Find by relative path.
	 * 
	 * @param relativeTo
	 *            the relative to
	 * @param path
	 *            the path
	 * @param artifactSource
	 *            the artifact source
	 * @return the stream artifact
	 */
	public A findByRelativePath(A relativeTo, String path);

	public Session finderSession();

	public String getCurrentRepository();

	/**
	 * List by path.
	 * 
	 * @param path
	 *            the path
	 * @param artifactSource
	 *            the artifact source
	 * @return the set< stream artifact>
	 */
	public Set<A> listByPath(String path);

	/**
	 * Retrieve all artifact names.
	 * 
	 * @param artifactSource
	 *            the artifact source
	 * @return the set< string>
	 */
	public Set<String> retrieveAllArtifactNames(String initialPath);

}
