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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.PathElement;
import org.openspotlight.task.ExecutorInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOriginArtifactLoader implements OriginArtifactLoader {

    private final class LoaderInternalMethodsImpl implements LoaderInternalMethods {

        @Override
        public <A extends Artifact> boolean accept(final ArtifactSource source,
                                                    final Class<A> type) {
            try {
                return internalAccept(source, type);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public final Set<Class<? extends Artifact>> getAvailableTypes() {
            try {
                return internalGetAvailableTypes();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public final <A extends Artifact> boolean isMaybeChanged(final ArtifactSource source,
                                                                  final String artifactName,
                                                                  final A oldOne) {
            try {
                return internalIsMaybeChanged(source, artifactName, oldOne);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public final boolean isTypeSupported(final Class<? extends Artifact> type) {
            try {
                return internalIsTypeSupported(type);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        @Override
        public final <A extends Artifact> Set<String> retrieveOriginalNames(final Class<A> type,
                                                                             final ArtifactSource source,
                                                                             final String initialPath) {
            try {
                final Set<String> result = internalRetrieveOriginalNames(type, source, initialPath);
                if (logger.isDebugEnabled()) {
                    logger.debug("returned " + Strings.bigCollectionsToString(result) + " for (" + type.getSimpleName() + ", "
                                 + source.getName() + ", " + initialPath + ")");
                }
                return result;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    private final LoaderInternalMethods internalMethods = new LoaderInternalMethodsImpl();

    private final Logger                logger          = LoggerFactory.getLogger(getClass());

    private <A extends Artifact> A fillSomeData(final Class<A> type,
                                                 final ArtifactSource source,
                                                 final A artifact) {
        if (artifact != null) {
            artifact.setRepositoryName(source.getRepository().getName());
        }
        return artifact;
    }

    private <A extends Artifact> Set<A> fillSomeData(final Class<A> type,
                                                      final ArtifactSource source,
                                                      final Set<A> artifacts) {
        if (artifacts == null) { return Collections.emptySet(); }
        for (final A artifact: artifacts) {
            artifact.setRepositoryName(source.getRepository().getName());
        }
        return artifacts;
    }

    protected abstract <A extends Artifact> boolean internalAccept(ArtifactSource source,
                                                                    Class<A> type)
        throws Exception;

    protected abstract void internalCloseResources()
        throws Exception;

    protected abstract <A extends Artifact> A internalFindByPath(Class<A> type,
                                                                  ArtifactSource source,
                                                                  String path, String encodingToRead)
        throws Exception;

    protected <A extends Artifact> A internalFindByRelativePath(final Class<A> type,
                                                                 final ArtifactSource source,
                                                                 final A relativeTo,
                                                                 final String path, final String encoding)
        throws Exception {
        final String newPath = PathElement.createRelativePath(relativeTo.getParent(), path).getCompletePath();
        return internalFindByPath(type, source, newPath, encoding);

    }

    protected abstract Set<Class<? extends Artifact>> internalGetAvailableTypes()
        throws Exception;

    protected abstract <A extends Artifact> boolean internalIsMaybeChanged(ArtifactSource source,
                                                                            String artifactName,
                                                                            A oldOne)
        throws Exception;

    protected abstract boolean internalIsTypeSupported(Class<? extends Artifact> type)
        throws Exception;

    protected <A extends Artifact> Set<A> internalListByPath(final Class<A> type,
                                                              final ArtifactSource source,
                                                              final String initialPath, final String encoding)
        throws Exception {
        final Set<String> paths = getInternalMethods().retrieveOriginalNames(type, source, initialPath);
        final Set<A> result = new HashSet<A>();
        if (isMultithreaded()) {
            final List<Callable<A>> tasks = new ArrayList<Callable<A>>();
            for (final String path: paths) {
                final Callable<A> callable = new Callable<A>() {
                    @Override
                    public A call()
                        throws Exception {
                        return internalFindByPath(type, source, path, encoding);
                    }
                };
                tasks.add(callable);
            }
            final List<Future<A>> futures = ExecutorInstance.INSTANCE.invokeAll(tasks);
            for (final Future<A> f: futures) {
                final A a = f.get();
                if (a != null) {
                    result.add(a);
                }
            }

        } else {
            for (final String path: paths) {
                final A a = internalFindByPath(type, source, path, encoding);
                if (a != null) {
                    result.add(a);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("returned " + Strings.bigCollectionsToString(result) + " for (" + type.getSimpleName() + ", "
                         + source.getName() + ", " + initialPath + ")");
        }

        return result;
    }

    protected abstract <A extends Artifact> Set<String> internalRetrieveOriginalNames(Class<A> type,
                                                                                       ArtifactSource source,
                                                                                       String initialPath)
        throws Exception;

    protected abstract boolean isMultithreaded();

    @Override
    public final void closeResources() {
        try {
            internalCloseResources();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public final <A extends Artifact> A findByPath(final Class<A> type,
                                                    final ArtifactSource source,
                                                    final String path, final String encoding) {
        try {
            final A result = fillSomeData(type, source, internalFindByPath(type, source, path, encoding));
            if (logger.isDebugEnabled()) {
                logger
                    .debug("returned " + result + " for (" + type.getSimpleName() + ", " + source.getName() + ", " + path + ")");
            }
            return result;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public final <A extends Artifact> A findByRelativePath(final Class<A> type,
                                                            final ArtifactSource source,
                                                            final A relativeTo,
                                                            final String path, final String encoding) {
        try {
            return fillSomeData(type, source, internalFindByRelativePath(type, source, relativeTo, path, encoding));
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public final LoaderInternalMethods getInternalMethods() {
        return internalMethods;
    }

    @Override
    public final <A extends Artifact> Set<A> listByPath(final Class<A> type,
                                                         final ArtifactSource source,
                                                         final String path, final String encoding) {
        try {
            return fillSomeData(type, source, internalListByPath(type, source, path, encoding));
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

}
