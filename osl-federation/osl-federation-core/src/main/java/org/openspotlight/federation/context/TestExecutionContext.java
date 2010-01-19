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
package org.openspotlight.federation.context;

import org.openspotlight.common.DisposingListener;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.TestExecutionContextFactory.ArtifactFinderType;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.FileSystemStringArtifactFinder;
import org.openspotlight.federation.finder.LocalSourceStreamArtifactFinder;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.security.idm.AuthenticatedUser;

/**
 * This class is an {@link ExecutionContext} which initialize all resources in a
 * lazy way, and also close it in a lazy way also.
 * 
 * @author feu
 * 
 */
public class TestExecutionContext extends SingleGraphSessionExecutionContext {
	private final ArtifactFinderType type;

	private final ArtifactSource source;

	TestExecutionContext(final String username, final String password,
			final JcrConnectionDescriptor descriptor,
			final String repositoryName,
			final DisposingListener<DefaultExecutionContext> listener,
			final AuthenticatedUser user,
			final SLGraphSession uniqueGraphSession,
			final ArtifactSource source, final ArtifactFinderType type) {
		super(username, password, descriptor, repositoryName, listener, user,
				uniqueGraphSession);
		this.source = source;
		this.type = type;
	}

	public boolean artifactFinderSupportsThisType(
			final Class<? extends Artifact> type) {
		return type.equals(StringArtifact.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <A extends Artifact> ArtifactFinder<A> internalCreateFinder(
			final Class<A> artifactType, final Repository typedRepository) {
		Assertions.checkCondition("artifactTypeAsStream", StringArtifact.class
				.equals(artifactType));
		ArtifactFinder<A> finder;
		switch (type) {
		case LOCAL_SOURCE:
			finder = (ArtifactFinder<A>) new LocalSourceStreamArtifactFinder(
					source);

			break;
		case FILESYSTEM:
			finder = (ArtifactFinder<A>) new FileSystemStringArtifactFinder(
					source);

			break;
		default:
			throw Exceptions.logAndReturn(new IllegalStateException(
					"invalid enum type"));
		}
		return finder;
	}

}
