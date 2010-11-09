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
package org.openspotlight.federation.loader;

import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.common.util.Strings;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.PathElement;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.task.ExecutorInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible to load artifacts. It has a public method read its javadoc.
 * 
 * @author feu
 */
public enum ArtifactLoaderManager {

    INSTANCE;

    /**
     * It groups the name and type just to perform the artifact cleanup (mark as excluded) after the load task creation.
     * 
     * @author feu
     */
    private static class ArtifactTypeCleanupResources {

        public final Set<String>               names;
        public final Class<? extends Artifact> type;

        public ArtifactTypeCleanupResources(final Class<? extends Artifact> type,
                                            final Set<String> names) {
            this.type = type;
            this.names = names;
        }
    }

    /**
     * It maps the necessary data to perform the artifact loading.
     * 
     * @author feu
     */
    private static class ArtifactTypeResources {
        public final ArtifactSourceMapping        acceptedMapping;
        public final ArtifactTypeCleanupResources cleanupResources;
        public final OriginArtifactLoader         loader;
        public final String                       name;
        public final Class<? extends Artifact>    type;

        public ArtifactTypeResources(final String name, final OriginArtifactLoader loader,
                                     final Class<? extends Artifact> type,
                                     final ArtifactSourceMapping acceptedMapping,
                                     final ArtifactTypeCleanupResources cleanupResources) {
            this.name = name;
            this.loader = loader;
            this.type = type;
            this.acceptedMapping = acceptedMapping;
            this.cleanupResources = cleanupResources;
        }
    }

    /**
     * Static class responsible to find the change type of each artifact, to reload or load its contents if this is necessary, and
     * also to map its new name, and preserve the old one as another property.
     * 
     * @author feu
     */
    private static class LoadAndMapTask implements Callable<Void> {
        final PersistentArtifactManagerProvider provider;
        final ArtifactTypeResources             r;

        final ArtifactSource                    source;

        public LoadAndMapTask(final PersistentArtifactManagerProvider provider,
                              final ArtifactSource source, final ArtifactTypeResources r) {
            super();
            this.provider = provider;
            this.source = source;
            this.r = r;
        }

        @Override
        public Void call()
            throws Exception {
            try {
                final PersistentArtifactManager persistentArtifactManager = provider
                        .get();
                final Artifact persisted = persistentArtifactManager
                        .getInternalMethods().findByOriginalName(source,
                                r.type, r.name);
                final Artifact newOne = r.loader.findByPath(r.type, source,
                        r.name, source.getEncodingForFileContent());
                if (newOne == null) { throw logAndReturn(new IllegalStateException(
                            "This exclusion should be handled in another task")); }
                ChangeType change = null;
                if (persisted == null) {
                    change = ChangeType.INCLUDED;

                } else {
                    if (!r.loader.getInternalMethods().isMaybeChanged(source,
                            r.name, persisted)) {
                        change = ChangeType.NOT_CHANGED;
                    } else {
                        if (newOne.contentEquals(persisted)) {
                            change = ChangeType.NOT_CHANGED;
                        } else {
                            change = ChangeType.CHANGED;
                        }
                    }
                }
                if (change.equals(ChangeType.NOT_CHANGED)
                        && persisted.getChangeType().equals(
                            ChangeType.NOT_CHANGED)) { return null; }
                newOne.setMappedFrom(r.acceptedMapping.getFrom());
                newOne.setMappedTo(r.acceptedMapping.getTo());
                newOne.setChangeType(change);
                newOne.updateOriginalName(source, r.name);
                mapNewName(r, newOne);
                persistentArtifactManager.addTransient(newOne);
                persistentArtifactManager.saveTransientData();
                return null;
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
                return null;
            }
        }

    }

    /**
     * Static class responsible to set the new change type as excluded.
     * 
     * @author feu
     */
    private static class SetChangeTypeAsExcludedTask implements Callable<Void> {

        private final String                            name;

        private final PersistentArtifactManagerProvider provider;

        private final ArtifactTypeCleanupResources      resources;

        private final ArtifactSource                    source;

        public SetChangeTypeAsExcludedTask(final String name,
                                           final ArtifactTypeCleanupResources resources,
                                           final PersistentArtifactManagerProvider provider,
                                           final ArtifactSource source) {
            super();
            this.name = name;
            this.resources = resources;
            this.provider = provider;
            this.source = source;
        }

        @Override
        public Void call()
            throws Exception {
            try {
                final PersistentArtifactManager manager = provider.get();
                Artifact loaded = manager.getInternalMethods()
                        .findByOriginalName(source, resources.type, name);
                if (loaded == null) {
                    loaded = manager.findByPath(resources.type, name);
                }
                if (loaded != null) {
                    loaded.setChangeType(ChangeType.EXCLUDED);
                    manager.addTransient(loaded);
                    manager.saveTransientData();
                }
                return null;
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
                return null;
            }
        }

    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Map the old name to a new name
     * 
     * @param r
     * @param newOne
     */
    static void mapNewName(final ArtifactTypeResources r, final Artifact newOne) {
        String currentPathString = newOne.getParent().getCompletePath();
        if (!currentPathString.startsWith("/")) {
            currentPathString = "/" + currentPathString;
        }
        String toRemove = r.acceptedMapping.getFrom();
        if (!toRemove.startsWith("/")) {
            toRemove = "/" + toRemove;
        }
        if (currentPathString.startsWith(toRemove)) {
            currentPathString = Strings.removeBegginingFrom(toRemove,
                    currentPathString);
        }
        String newPathString = null;
        if (!currentPathString.startsWith(r.acceptedMapping.getTo())) {
            newPathString = r.acceptedMapping.getTo() + currentPathString;
        } else {
            newPathString = currentPathString;
        }
        newOne.setMappedFrom(r.acceptedMapping.getFrom());
        newOne.setMappedTo(r.acceptedMapping.getTo());
        final PathElement newPath = PathElement
                .createFromPathString(newPathString);
        newOne.setParent(newPath);
        newOne.getArtifactCompleteName();
    }

    /**
     * Loads in single thread only the data needed to process all artifacts: the names itself
     * 
     * @param source
     * @return
     * @throws Exception
     */
    private Pair<Set<ArtifactTypeResources>, Set<ArtifactTypeCleanupResources>>
        loadBaseData(final ArtifactSource source,
                     final PersistentArtifactManagerProvider provider,
                     final Iterable<Class<? extends OriginArtifactLoader>> registry)
            throws Exception {
        final Set<ArtifactTypeResources> resourcesToLoad = new CopyOnWriteArraySet<ArtifactTypeResources>();
        final Set<ArtifactTypeCleanupResources> resourcesToClean = new CopyOnWriteArraySet<ArtifactTypeCleanupResources>();

        for (final Class<? extends OriginArtifactLoader> loaderClass: registry) {
            final OriginArtifactLoader loader = loaderClass.newInstance();
            final Set<Class<? extends Artifact>> types = loader.getInternalMethods()
                    .getAvailableTypes();
            for (final Class<? extends Artifact> type: types) {
                if (loader.getInternalMethods().accept(source, type)) {
                    for (final ArtifactSourceMapping mapping: source.getMappings()) {
                        final Set<String> namesFromOrigin = new HashSet<String>(
                                loader.getInternalMethods()
                                        .retrieveOriginalNames(type, source,
                                            mapping.getFrom()));
                        if (logger.isDebugEnabled()) {
                            logger.debug("for type "
                                    + type.getSimpleName()
                                    + " on artifact source "
                                    + source.getUrl()
                                    + " was loaded "
                                    + Strings
                                        .bigCollectionsToString(namesFromOrigin));
                        }

                        final FilterResult result = filterNamesByPattern(
                                Strings.rootPath(mapping.getFrom()),
                                namesFromOrigin, mapping.getIncludeds(),
                                mapping.getExcludeds(), false);
                        final HashSet<String> namesToExclude = new HashSet<String>();
                        final List<String> rawNamesToExclude = SLCollections
                                .iterableToList(provider
                                        .get()
                                        .getInternalMethods()
                                        .retrieveOriginalNames(source, type,
                                            mapping.getFrom()));
                        for (final String raw: rawNamesToExclude) {
                            namesToExclude.add(removeBegginingFrom(
                                    source.getInitialLookup(), raw));
                        }

                        namesToExclude.removeAll(namesFromOrigin);

                        final ArtifactTypeCleanupResources cleanupResources = new ArtifactTypeCleanupResources(
                                type, namesToExclude);
                        resourcesToClean.add(cleanupResources);
                        for (final String s: result.getIncludedNames()) {
                            final ArtifactTypeResources resourcesByType = new ArtifactTypeResources(
                                    s, loader, type, mapping, cleanupResources);
                            resourcesToLoad.add(resourcesByType);
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("for type "
                                    + type.getSimpleName()
                                    + " on artifact source "
                                    + source.getUrl()
                                    + " was included "
                                    + Strings.bigCollectionsToString(result
                                        .getIncludedNames()));
                            logger.debug("for type "
                                    + type.getSimpleName()
                                    + " on artifact source "
                                    + source.getUrl()
                                    + " was ignored "
                                    + Strings.bigCollectionsToString(result
                                        .getIgnoredNames()));
                            logger.debug("for type "
                                    + type.getSimpleName()
                                    + " on artifact source "
                                    + source.getUrl()
                                    + " was excluded "
                                    + Strings.bigCollectionsToString(result
                                        .getExcludedNames()));
                        }
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ignoring " + type.getSimpleName()
                                + " on artifact source " + source.getUrl());
                    }
                }
            }
        }

        return Pair.newPair(resourcesToLoad, resourcesToClean);
    }

    /**
     * It will reload all needed artifacts on a multithreaded environment, if this is possible. The {@link OriginArtifactLoader
     * loaders} passed as argument must have default constructors, since it was projected to receive the necessary state classes
     * on load methods. It should work on a multi threaded environment. It should be very easy to do, since there's no need to
     * store any state on this class.
     * 
     * @param source
     */
    public void refreshResources(final ArtifactSource source,
                                 final PersistentArtifactManagerProvider provider,
                                 final Iterable<Class<? extends OriginArtifactLoader>> registry) {
        try {
            final Pair<Set<ArtifactTypeResources>, Set<ArtifactTypeCleanupResources>> result =
                loadBaseData(source, provider, registry);

            final List<Callable<Void>> tasks = new LinkedList<Callable<Void>>();
            for (final ArtifactTypeResources r: result.getK1()) {
                r.cleanupResources.names.remove(r.name);
                tasks.add(new LoadAndMapTask(provider, source, r));
            }
            for (final ArtifactTypeCleanupResources cleanup: result.getK2()) {
                for (final String toRemove: cleanup.names) {
                    tasks.add(new SetChangeTypeAsExcludedTask(toRemove,
                            cleanup, provider, source));
                }
            }

            if (provider.useOnePerThread()) {
                for (final Callable<Void> c: tasks) {
                    c.call();
                }
            } else {
                final List<Future<Void>> results = ExecutorInstance.INSTANCE
                        .invokeAll(tasks);
                for (final Future<Void> f: results) {
                    f.get();
                }
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

}
