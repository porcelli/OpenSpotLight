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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.task.ExecutorInstance;

public abstract class AbstractPersistentArtifactManager implements
        PersistentArtifactManager {

    protected abstract boolean isMultithreaded();

    private final PersistentArtifactInternalMethods internalMethods = new PersistentArtifactInternalMethodsImpl();

    private final class PersistentArtifactInternalMethodsImpl implements
            PersistentArtifactInternalMethods {

        public <A extends Artifact> A findByOriginalName( ArtifactSource source,
                                                          Class<A> type,
                                                          String originName ) {
            try {
                return internalFindByOriginalName(source, type, originName);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public <A extends Artifact> boolean isTypeSupported( Class<A> type ) {
            try {
                return internalIsTypeSupported(type);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public <A extends Artifact> Set<A> listByOriginalNames(
                                                                ArtifactSource source,
                                                                Class<A> type,
                                                                String originName ) {
            try {
                return internalListByOriginalNames(source, type, originName);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public <A extends Artifact> Set<String> retrieveOriginalNames(
                                                                       ArtifactSource source,
                                                                       Class<A> type,
                                                                       String initialPath ) {
            try {
                return internalRetrieveOriginalNames(source, type, initialPath);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        public <A extends Artifact> Set<String> retrieveNames( Class<A> type,
                                                               String initialPath ) {
            try {
                return internalRetrieveNames(type, initialPath);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    public <A extends Artifact> void addTransient( A artifact ) {
        try {
            internalAddTransient(artifact);
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public <A extends Artifact> A findByPath( Class<A> type,
                                              String path ) {
        try {
            return internalFindByPath(type, path);
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public PersistentArtifactInternalMethods getInternalMethods() {
        return internalMethods;
    }

    public <A extends Artifact> Set<A> listByPath( Class<A> type,
                                                   String path ) {
        try {
            return internalListByPath(type, path);
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public <A extends Artifact> void markAsRemoved( A artifact ) {
        try {
            internalMarkAsRemoved(artifact);
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public void saveTransientData() {
        try {
            internalSaveTransientData();
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public void closeResources() {
        try {
            internalCloseResources();
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    protected abstract <A extends Artifact> A internalFindByOriginalName(
                                                                          ArtifactSource source,
                                                                          Class<A> type,
                                                                          String originName )
            throws Exception;

    protected abstract <A extends Artifact> boolean internalIsTypeSupported(
                                                                             Class<A> type ) throws Exception;

    protected abstract <A extends Artifact> Set<String> internalRetrieveOriginalNames(
                                                                                       ArtifactSource source,
                                                                                       Class<A> type,
                                                                                       String initialPath )
            throws Exception;

    protected abstract <A extends Artifact> Set<String> internalRetrieveNames(
                                                                               Class<A> type,
                                                                               String initialPath ) throws Exception;

    protected abstract <A extends Artifact> void internalAddTransient( A artifact )
            throws Exception;

    protected abstract <A extends Artifact> A internalFindByPath( Class<A> type,
                                                                  String path ) throws Exception;

    protected abstract <A extends Artifact> void internalMarkAsRemoved(
                                                                        A artifact ) throws Exception;

    protected abstract void internalSaveTransientData() throws Exception;

    protected abstract void internalCloseResources() throws Exception;

    protected final <A extends Artifact> Set<A> internalListByOriginalNames(
                                                                             final ArtifactSource source,
                                                                             final Class<A> type,
                                                                             String initialPath )
            throws Exception {
        Set<String> paths = getInternalMethods().retrieveOriginalNames(source,
                                                                       type, initialPath);
        Set<A> result = new HashSet<A>();
        if (isMultithreaded()) {
            List<Callable<A>> tasks = new ArrayList<Callable<A>>();
            for (final String path : paths) {
                Callable<A> callable = new Callable<A>() {
                    public A call() throws Exception {
                        return internalFindByOriginalName(source, type, path);
                    }
                };
                tasks.add(callable);
            }
            List<Future<A>> futures = ExecutorInstance.INSTANCE
                                                               .invokeAll(tasks);
            for (Future<A> f : futures)
                result.add(f.get());
        } else {
            for (final String path : paths) {
                result.add(internalFindByOriginalName(source, type, path));
            }
        }
        return result;
    }

    protected final <A extends Artifact> Set<A> internalListByPath(
                                                                    final Class<A> type,
                                                                    String initialPath ) throws Exception {
        Set<String> paths = getInternalMethods().retrieveNames(type,
                                                               initialPath);
        Set<A> result = new HashSet<A>();
        if (isMultithreaded()) {
            List<Callable<A>> tasks = new ArrayList<Callable<A>>();
            for (final String path : paths) {
                Callable<A> callable = new Callable<A>() {
                    public A call() throws Exception {
                        return internalFindByPath(type, path);
                    }
                };
                tasks.add(callable);
            }
            List<Future<A>> futures = ExecutorInstance.INSTANCE
                                                               .invokeAll(tasks);
            for (Future<A> f : futures)
                result.add(f.get());
        } else {
            for (final String path : paths) {
                result.add(internalFindByPath(type, path));
            }
        }
        return result;
    }

}
