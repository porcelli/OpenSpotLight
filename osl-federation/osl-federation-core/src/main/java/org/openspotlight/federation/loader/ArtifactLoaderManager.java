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

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.PathElement;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.task.ExecutorInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import static org.openspotlight.common.util.SLCollections.iterableToSet;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

/**
 * Class responsible to load artifacts. It has a public method read its javadoc.
 *
 * @author feu
 */
public enum ArtifactLoaderManager {

    INSTANCE;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * It groups the name and type just to perform the artifact cleanup (mark as excluded) after the load task creation.
     *
     * @author feu
     */
    private static class ArtifactTypeCleanupResources {

        public final Class<? extends Artifact> type;
        public final Set<String> names;

        public ArtifactTypeCleanupResources(
                Class<? extends Artifact> type, Set<String> names) {
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
        public final String name;
        public final OriginArtifactLoader loader;
        public final Class<? extends Artifact> type;
        public final ArtifactSourceMapping acceptedMapping;
        public final ArtifactTypeCleanupResources cleanupResources;

        public ArtifactTypeResources(
                String name, OriginArtifactLoader loader, Class<? extends Artifact> type,
                ArtifactSourceMapping acceptedMapping, ArtifactTypeCleanupResources cleanupResources) {
            this.name = name;
            this.loader = loader;
            this.type = type;
            this.acceptedMapping = acceptedMapping;
            this.cleanupResources = cleanupResources;
        }
    }

    /**
     * Static class responsible to set the new change type as excluded.
     *
     * @author feu
     */
    private static class SetChangeTypeAsExcludedTask implements Callable<Void> {

        public SetChangeTypeAsExcludedTask(
                String name, ArtifactTypeCleanupResources resources,
                PersistentArtifactManagerProvider provider, ArtifactSource source) {
            super();
            this.name = name;
            this.resources = resources;
            this.provider = provider;
            this.source = source;
        }

        private final String name;

        private final ArtifactTypeCleanupResources resources;

        private final PersistentArtifactManagerProvider provider;

        private final ArtifactSource source;

        public Void call() throws Exception {
            try {
                PersistentArtifactManager manager = provider.get();
                Artifact loaded = manager.getInternalMethods().findByOriginalName(source, resources.type, name);
                if (loaded == null) {
                    loaded = manager.findByPath(resources.type, name);
                }
                if (loaded != null) {
                    loaded.setChangeType(ChangeType.EXCLUDED);
                    manager.addTransient(loaded);
                    manager.saveTransientData();
                }
                return null;
            } catch (Exception e) {
                Exceptions.catchAndLog(e);
                return null;
            }
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
        final ArtifactSource source;

        public LoadAndMapTask(
                PersistentArtifactManagerProvider provider, ArtifactSource source, ArtifactTypeResources r) {
            super();
            this.provider = provider;
            this.source = source;
            this.r = r;
        }

        final ArtifactTypeResources r;

        public Void call() throws Exception {
            try {
                PersistentArtifactManager persistentArtifactManager = provider.get();
                final Artifact persisted = persistentArtifactManager.getInternalMethods().findByOriginalName(source, r.type, r.name);
                final Artifact newOne = r.loader.findByPath(r.type, source, r.name, source.getEncodingForFileContent());
                if (newOne == null)
                    throw logAndReturn(new IllegalStateException("This exclusion should be handled in another task"));
                ChangeType change = null;
                if (persisted == null) {
                    change = ChangeType.INCLUDED;

                } else {
                    if (!r.loader.getInternalMethods().isMaybeChanged(source, r.name, persisted)) {
                        change = ChangeType.NOT_CHANGED;
                    } else {
                        if (newOne.contentEquals(persisted)) {
                            change = ChangeType.NOT_CHANGED;
                        } else {
                            change = ChangeType.CHANGED;
                        }
                    }
                }
                if (change.equals(ChangeType.NOT_CHANGED) && persisted.getChangeType().equals(ChangeType.NOT_CHANGED))
                    return null;
                newOne.setMappedFrom(r.acceptedMapping.getFrom());
                newOne.setMappedTo(r.acceptedMapping.getTo());
                newOne.setChangeType(change);
                newOne.updateOriginalName(source, r.name);
                mapNewName(r, newOne);
                persistentArtifactManager.addTransient(newOne);
                persistentArtifactManager.saveTransientData();
                return null;
            } catch (Exception e) {
                Exceptions.catchAndLog(e);
                return null;
            }
        }

    }

    /**
     * It will reload all needed artifacts on a multithreaded environment, if this is possible. There's one mandatory thing here:
     * {@link GlobalSettings#getLoaderRegistry()} must be filled with valid types to be used as {@link OriginArtifactLoader
     * loaders}. This loaders must have default constructors, since it was projected to receive the necessary state classes on
     * load methods. It should work on a multi threaded environment. It should be very easy to do, since there's no need to store
     * any state on this class.
     *
     * @param settings
     * @param source
     */
    public void refreshResources(final GlobalSettings settings,
                                 final ArtifactSource source,
                                 final PersistentArtifactManagerProvider provider) {
        try {
            Pair<Set<ArtifactTypeResources>, Set<ArtifactTypeCleanupResources>> result = loadBaseData(settings, source, provider);

            List<Callable<Void>> tasks = new LinkedList<Callable<Void>>();
            for (final ArtifactTypeResources r : result.getK1()) {
                r.cleanupResources.names.remove(r.name);
                tasks.add(new LoadAndMapTask(provider, source, r));
            }
            for (final ArtifactTypeCleanupResources cleanup : result.getK2()) {
                for (String toRemove : cleanup.names) {
                    tasks.add(new SetChangeTypeAsExcludedTask(toRemove, cleanup, provider, source));
                }
            }

            if (provider.useOnePerThread()) {
                for (Callable<Void> c : tasks) {
                    c.call();
                }
            } else {
                List<Future<Void>> results = ExecutorInstance.INSTANCE.invokeAll(tasks);
                for (Future<Void> f : results) {
                    f.get();
                }
            }
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    /**
     * Loads in single thread only the data needed to process all artifacts: the names itself
     *
     * @param settings
     * @param source
     * @return
     * @throws Exception
     */
    private Pair<Set<ArtifactTypeResources>, Set<ArtifactTypeCleanupResources>> loadBaseData(GlobalSettings settings,
                                                                                             ArtifactSource source,
                                                                                             PersistentArtifactManagerProvider provider)
            throws Exception {
        Set<ArtifactTypeResources> resourcesToLoad = new CopyOnWriteArraySet<ArtifactTypeResources>();
        Set<ArtifactTypeCleanupResources> resourcesToClean = new CopyOnWriteArraySet<ArtifactTypeCleanupResources>();

        for (Class<? extends OriginArtifactLoader> loaderClass : settings.getLoaderRegistry()) {
            OriginArtifactLoader loader = loaderClass.newInstance();
            Set<Class<? extends Artifact>> types = loader.getInternalMethods().getAvailableTypes();
            for (Class<? extends Artifact> type : types) {
                if (loader.getInternalMethods().accept(source, type)) {
                    for (ArtifactSourceMapping mapping : source.getMappings()) {
                        Set<String> namesFromOrigin = new HashSet<String>(loader.getInternalMethods().retrieveOriginalNames(type, source,
                                mapping.getFrom()));
                        if (logger.isDebugEnabled())
                            logger.debug("for type " + type.getSimpleName() + " on artifact source "
                                    + source.getName() + " was loaded "
                                    + Strings.bigCollectionsToString(namesFromOrigin));


                        FilterResult result = filterNamesByPattern(Strings.rootPath(mapping.getFrom()), namesFromOrigin,
                                mapping.getIncludeds(), mapping.getExcludeds(), false);
                        HashSet<String> namesToExclude = new HashSet<String>();
                        Set<String> rawNamesToExclude = iterableToSet(provider.get().getInternalMethods().retrieveOriginalNames(source, type, mapping.getFrom()));
                        for (String raw : rawNamesToExclude) {
                            namesToExclude.add(removeBegginingFrom(source.getInitialLookup(), raw));
                        }

                        namesToExclude.removeAll(namesFromOrigin);

                        ArtifactTypeCleanupResources cleanupResources = new ArtifactTypeCleanupResources(type, namesToExclude);
                        resourcesToClean.add(cleanupResources);
                        for (String s : result.getIncludedNames()) {
                            ArtifactTypeResources resourcesByType = new ArtifactTypeResources(s, loader, type, mapping,
                                    cleanupResources);
                            resourcesToLoad.add(resourcesByType);
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("for type " + type.getSimpleName() + " on artifact source " + source.getName()
                                    + " was included " + Strings.bigCollectionsToString(result.getIncludedNames()));
                            logger.debug("for type " + type.getSimpleName() + " on artifact source " + source.getName()
                                    + " was ignored " + Strings.bigCollectionsToString(result.getIgnoredNames()));
                            logger.debug("for type " + type.getSimpleName() + " on artifact source " + source.getName()
                                    + " was excluded " + Strings.bigCollectionsToString(result.getExcludedNames()));
                        }
                    }
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("ignoring " + type.getSimpleName() + " on artifact source "
                                + source.getName());
                }
            }
        }

        return Pair.newPair(resourcesToLoad, resourcesToClean);
    }

    /**
     * Map the old name to a new name
     *
     * @param r
     * @param newOne
     */
    static void mapNewName(final ArtifactTypeResources r,
                           Artifact newOne) {
        String currentPathString = newOne.getParent().getCompletePath();
        if (!currentPathString.startsWith("/")) {
            currentPathString = "/" + currentPathString;
        }
        String toRemove = r.acceptedMapping.getFrom();
        if (!toRemove.startsWith("/")) {
            toRemove = "/" + toRemove;
        }
        if (currentPathString.startsWith(toRemove)) {
            currentPathString = Strings.removeBegginingFrom(toRemove, currentPathString);
        }
        String newPathString = null;
        if (!currentPathString.startsWith(r.acceptedMapping.getTo())) {
            newPathString = r.acceptedMapping.getTo() + currentPathString;
        } else {
            newPathString = currentPathString;
        }
        newOne.setMappedFrom(r.acceptedMapping.getFrom());
        newOne.setMappedTo(r.acceptedMapping.getTo());
        final PathElement newPath = PathElement.createFromPathString(newPathString);
        newOne.setParent(newPath);
        newOne.getArtifactCompleteName();
    }

}
