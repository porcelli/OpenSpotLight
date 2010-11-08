/**
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.task.ExecutorInstance;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractPersistentArtifactManager implements PersistentArtifactManager {
    private final class PersistentArtifactInternalMethodsImpl implements PersistentArtifactInternalMethods {

        @Override
        public <A extends Artifact> A findByOriginalName(final ArtifactSource source,
                                                          final Class<A> type,
                                                          final String originName) {
            try {
                return internalFindByOriginalName(source, type, originName);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public <A extends Artifact> boolean isTypeSupported(final Class<A> type) {
            try {
                return internalIsTypeSupported(type);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public <A extends Artifact> Iterable<A> listByOriginalNames(final ArtifactSource source,
                                                                    final Class<A> type,
                                                                    final String originName) {
            try {
                return internalListByOriginalNames(source, type, originName);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public <A extends Artifact> Iterable<String> retrieveNames(final Class<A> type,
                                                                   final String initialPath) {
            try {
                return internalRetrieveNames(type, initialPath);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public <A extends Artifact> Iterable<String> retrieveOriginalNames(final ArtifactSource source,
                                                                           final Class<A> type,
                                                                           final String initialPath) {
            try {
                return internalRetrieveOriginalNames(source, type, initialPath);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    private final PersistentArtifactInternalMethods internalMethods = new PersistentArtifactInternalMethodsImpl();

    protected abstract <A extends Artifact> void internalAddTransient(A artifact)
        throws Exception;

    protected abstract void internalCloseResources()
        throws Exception;

    protected abstract <A extends Artifact> A internalFindByOriginalName(ArtifactSource source,
                                                                          Class<A> type,
                                                                          String originName)
        throws Exception;

    protected abstract <A extends Artifact> A internalFindByPath(Class<A> type,
                                                                  String path)
        throws Exception;

    protected abstract <A extends Artifact> boolean internalIsTypeSupported(Class<A> type)
        throws Exception;

    protected final <A extends Artifact> Iterable<A> internalListByOriginalNames(final ArtifactSource source,
                                                                                 final Class<A> type,
                                                                                 final String initialPath)
        throws Exception {
        final Iterable<String> paths = getInternalMethods().retrieveOriginalNames(source, type, initialPath);
        final HashSet<A> result = new HashSet<A>();
        if (isMultithreaded()) {
            final List<Callable<A>> tasks = new ArrayList<Callable<A>>();
            for (final String path: paths) {
                final Callable<A> callable = new Callable<A>() {
                    @Override
                    public A call()
                        throws Exception {
                        return internalFindByOriginalName(source, type, path);
                    }
                };
                tasks.add(callable);
            }
            final List<Future<A>> futures = ExecutorInstance.INSTANCE.invokeAll(tasks);
            for (final Future<A> f: futures) {
                result.add(f.get());
            }
        } else {
            for (final String path: paths) {
                result.add(internalFindByOriginalName(source, type, path));
            }
        }
        return result;
    }

    protected final <A extends Artifact> Iterable<A> internalListByPath(final Class<A> type,
                                                                        final String initialPath)
        throws Exception {
        final Iterable<String> paths = getInternalMethods().retrieveNames(type, initialPath);
        final Set<A> result = new HashSet<A>();
        if (isMultithreaded()) {
            final List<Callable<A>> tasks = new ArrayList<Callable<A>>();
            for (final String path: paths) {
                final Callable<A> callable = new Callable<A>() {
                    @Override
                    public A call()
                        throws Exception {
                        return internalFindByPath(type, path);
                    }
                };
                tasks.add(callable);
            }
            final List<Future<A>> futures = ExecutorInstance.INSTANCE.invokeAll(tasks);
            for (final Future<A> f: futures) {
                result.add(f.get());
            }
        } else {
            for (final String path: paths) {
                result.add(internalFindByPath(type, path));
            }
        }
        return ImmutableSet.copyOf(result);
    }

    protected abstract <A extends Artifact> void internalMarkAsRemoved(A artifact)
        throws Exception;

    protected abstract <A extends Artifact> Iterable<String> internalRetrieveNames(Class<A> type,
                                                                                   String initialPath)
        throws Exception;

    protected abstract <A extends Artifact> Iterable<String> internalRetrieveOriginalNames(ArtifactSource source,
                                                                                           Class<A> type,
                                                                                           String initialPath)
        throws Exception;

    protected abstract void internalSaveTransientData()
        throws Exception;

    protected abstract boolean isMultithreaded();

    @Override
    public <A extends Artifact> void addTransient(final A artifact) {
        try {
            internalAddTransient(artifact);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public void closeResources() {
        try {
            internalCloseResources();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public <A extends Artifact> A findByPath(final Class<A> type,
                                              final String path) {
        try {
            return internalFindByPath(type, path);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public PersistentArtifactInternalMethods getInternalMethods() {
        return internalMethods;
    }

    @Override
    public <A extends Artifact> Iterable<A> listByInitialPath(final Class<A> type,
                                                              final String path) {
        try {
            return internalListByPath(type, path);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public <A extends Artifact> void markAsRemoved(final A artifact) {
        try {
            internalMarkAsRemoved(artifact);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public void saveTransientData() {
        try {
            internalSaveTransientData();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

}
