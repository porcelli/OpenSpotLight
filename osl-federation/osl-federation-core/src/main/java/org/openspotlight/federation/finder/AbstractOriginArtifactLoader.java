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
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.PathElement;
import org.openspotlight.task.ExecutorInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOriginArtifactLoader implements OriginArtifactLoader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract boolean isMultithreaded();

    private final LoaderInternalMethods internalMethods = new LoaderInternalMethodsImpl();

    private final class LoaderInternalMethodsImpl implements LoaderInternalMethods {

        public final Set<Class<? extends Artifact>> getAvailableTypes() {
            try {
                return internalGetAvailableTypes();
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public final <A extends Artifact> boolean isMaybeChanged( ArtifactSource source,
                                                                  String artifactName,
                                                                  A oldOne ) {
            try {
                return internalIsMaybeChanged(source, artifactName, oldOne);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public final boolean isTypeSupported( Class<? extends Artifact> type ) {
            try {
                return internalIsTypeSupported(type);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public final <A extends Artifact> Set<String> retrieveOriginalNames( Class<A> type,
                                                                             ArtifactSource source,
                                                                             String initialPath ) {
            try {
                Set<String> result = internalRetrieveOriginalNames(type, source, initialPath);
                if (logger.isDebugEnabled()) {
                    logger.debug("returned " + Strings.bigCollectionsToString(result) + " for (" + type.getSimpleName() + ", "
                                 + source.getName() + ", " + initialPath + ")");
                }
                return result;
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public <A extends Artifact> boolean accept( ArtifactSource source,
                                                    Class<A> type ) {
            try {
                return internalAccept(source, type);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    public final <A extends Artifact> A findByPath( Class<A> type,
                                                    ArtifactSource source,
                                                    String path ) {
        try {
            A result = fillSomeData(type, source, internalFindByPath(type, source, path));
            if (logger.isDebugEnabled()) {
                logger.debug("returned " + result + " for (" + type.getSimpleName() + ", " + source.getName() + ", " + path + ")");
            }
            return result;
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public final <A extends Artifact> A findByRelativePath( Class<A> type,
                                                            ArtifactSource source,
                                                            A relativeTo,
                                                            String path ) {
        try {
            return fillSomeData(type, source, internalFindByRelativePath(type, source, relativeTo, path));
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public final LoaderInternalMethods getInternalMethods() {
        return internalMethods;
    }

    public final <A extends Artifact> Set<A> listByPath( Class<A> type,
                                                         ArtifactSource source,
                                                         String path ) {
        try {
            return fillSomeData(type, source, internalListByPath(type, source, path));
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public final void closeResources() {
        try {
            internalCloseResources();
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    protected <A extends Artifact> A internalFindByRelativePath( Class<A> type,
                                                                 ArtifactSource source,
                                                                 A relativeTo,
                                                                 String path ) throws Exception {
        final String newPath = PathElement.createRelativePath(relativeTo.getParent(), path).getCompletePath();
        return internalFindByPath(type, source, newPath);

    }

    protected <A extends Artifact> Set<A> internalListByPath( final Class<A> type,
                                                              final ArtifactSource source,
                                                              final String initialPath ) throws Exception {
        Set<String> paths = getInternalMethods().retrieveOriginalNames(type, source, initialPath);
        Set<A> result = new HashSet<A>();
        if (isMultithreaded()) {
            List<Callable<A>> tasks = new ArrayList<Callable<A>>();
            for (final String path : paths) {
                Callable<A> callable = new Callable<A>() {
                    public A call() throws Exception {
                        return internalFindByPath(type, source, path);
                    }
                };
                tasks.add(callable);
            }
            List<Future<A>> futures = ExecutorInstance.INSTANCE.invokeAll(tasks);
            for (Future<A> f : futures){
                A a = f.get();
                if(a!=null){
                    result.add(a);
                }
            }


        } else {
            for (final String path : paths) {
                A a = internalFindByPath(type, source, path);
                if(a!=null){
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

    private <A extends Artifact> A fillSomeData( Class<A> type,
                                                 ArtifactSource source,
                                                 A artifact ) {
        if (artifact != null) artifact.setRepositoryName(source.getRepository().getName());
        return artifact;
    }

    private <A extends Artifact> Set<A> fillSomeData( Class<A> type,
                                                      ArtifactSource source,
                                                      Set<A> artifacts ) {
        if (artifacts == null) return Collections.emptySet();
        for (A artifact : artifacts)
            artifact.setRepositoryName(source.getRepository().getName());
        return artifacts;
    }

    protected abstract <A extends Artifact> boolean internalIsMaybeChanged( ArtifactSource source,
                                                                            String artifactName,
                                                                            A oldOne ) throws Exception;

    protected abstract Set<Class<? extends Artifact>> internalGetAvailableTypes() throws Exception;

    protected abstract boolean internalIsTypeSupported( Class<? extends Artifact> type ) throws Exception;

    protected abstract <A extends Artifact> Set<String> internalRetrieveOriginalNames( Class<A> type,
                                                                                       ArtifactSource source,
                                                                                       String initialPath ) throws Exception;

    protected abstract <A extends Artifact> A internalFindByPath( Class<A> type,
                                                                  ArtifactSource source,
                                                                  String path ) throws Exception;

    protected abstract void internalCloseResources() throws Exception;

    protected abstract <A extends Artifact> boolean internalAccept( ArtifactSource source,
                                                                    Class<A> type ) throws Exception;

}
