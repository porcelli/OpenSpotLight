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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.PathElement;
import org.openspotlight.task.ExecutorInstance;

public abstract class AbstractOriginArtifactLoader implements
		OriginArtifactLoader {

	private final LoaderInternalMethods internalMethods = new LoaderInternalMethodsImpl();

	private final class LoaderInternalMethodsImpl implements
			LoaderInternalMethods {

		public final Set<Class<? extends Artifact>> getAvailableTypes() {
			return internalGetAvailableTypes();
		}

		public final <A extends Artifact> boolean isMaybeChanged(
				ArtifactSource source, String artifactName, A oldOne) {
			return internalIsMaybeChanged(source, artifactName, oldOne);
		}

		public final boolean isTypeSupported(Class<? extends Artifact> type) {
			return internalIsTypeSupported(type);
		}

		public final <A extends Artifact> Set<String> retrieveOriginalNames(
				Class<A> type, ArtifactSource source, String initialPath) {
			return internalRetrieveOriginalNames(type, source, initialPath);
		}

		public <A extends Artifact> boolean accept(ArtifactSource source,
				Class<A> type) {
			return internalAccept(source, type);
		}

	}

	public final <A extends Artifact> A findByPath(Class<A> type,
			ArtifactSource source, String path) {
		return fillSomeData(type, source,
				internalFindByPath(type, source, path));
	}

	public final <A extends Artifact> A findByRelativePath(Class<A> type,
			ArtifactSource source, A relativeTo, String path) {
		return fillSomeData(type, source, internalFindByRelativePath(type,
				source, relativeTo, path));
	}

	public final LoaderInternalMethods getInternalMethods() {
		return internalMethods;
	}

	public final <A extends Artifact> Set<A> listByPath(Class<A> type,
			ArtifactSource source, String path) {
		return fillSomeData(type, source,
				internalListByPath(type, source, path));
	}

	public final void closeResources() {
		internalCloseResources();

	}

	protected abstract <A extends Artifact> boolean internalIsMaybeChanged(
			ArtifactSource source, String artifactName, A oldOne);

	protected abstract Set<Class<? extends Artifact>> internalGetAvailableTypes();

	protected abstract boolean internalIsTypeSupported(
			Class<? extends Artifact> type);

	protected abstract <A extends Artifact> Set<String> internalRetrieveOriginalNames(
			Class<A> type, ArtifactSource source, String initialPath);

	protected abstract <A extends Artifact> A internalFindByPath(Class<A> type,
			ArtifactSource source, String path);

	protected <A extends Artifact> A internalFindByRelativePath(Class<A> type,
			ArtifactSource source, A relativeTo, String path) {
		final String newPath = PathElement.createRelativePath(
				relativeTo.getParent(), path).getCompletePath();
		return internalFindByPath(type, source, newPath);

	}

	protected <A extends Artifact> Set<A> internalListByPath(
			final Class<A> type, final ArtifactSource source,
			final String initialPath) {
		try {
			Set<String> paths = getInternalMethods().retrieveOriginalNames(
					type, source, initialPath);
			List<Callable<A>> tasks = new ArrayList<Callable<A>>();
			for (final String path : paths) {
				Callable<A> callable = new Callable<A>() {
					public A call() throws Exception {
						return internalFindByPath(type, source, path);
					}
				};
				tasks.add(callable);
			}
			List<Future<A>> futures = ExecutorInstance.INSTANCE
					.getExecutorInstance().invokeAll(tasks);
			Set<A> result = new HashSet<A>();
			for (Future<A> f : futures)
				result.add(f.get());
			return result;
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	private <A extends Artifact> A fillSomeData(Class<A> type,
			ArtifactSource source, A artifact) {
		if (artifact != null)
			artifact.setRepositoryName(source.getRepository().getName());
		return artifact;
	}

	private <A extends Artifact> Set<A> fillSomeData(Class<A> type,
			ArtifactSource source, Set<A> artifacts) {
		if (artifacts == null)
			return Collections.emptySet();
		for (A artifact : artifacts)
			artifact.setRepositoryName(source.getRepository().getName());
		return artifacts;
	}

	protected abstract void internalCloseResources();

	protected abstract <A extends Artifact> boolean internalAccept(ArtifactSource source,
			Class<A> type);

	
	
}
