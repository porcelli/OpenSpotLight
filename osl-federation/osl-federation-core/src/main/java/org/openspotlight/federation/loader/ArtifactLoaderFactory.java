/**
 * 
 */
package org.openspotlight.federation.loader;

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspotlight.common.Pair;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.PathElement;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.ArtifactTypeRegistry;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ArtifactLoader objects.
 */
public class ArtifactLoaderFactory {

    /**
     * The Class ArtifactLoaderImpl.
     */
    private static class ArtifactLoaderImpl implements ArtifactLoader {

        /**
         * The Class SourcesToProcessItems.
         */
        private static class SourcesToProcessItems {

            /** The artifact finder. */
            final ArtifactFinder<?>     artifactFinder;

            /** The artifact source. */
            final ArtifactSource        artifactSource;

            /** The artifact name. */
            final String                artifactName;

            /** The mapping. */
            final ArtifactSourceMapping mapping;

            /**
             * Instantiates a new sources to process items.
             * 
             * @param artifactFinder the artifact finder
             * @param artifactSource the artifact source
             * @param artifactName the artifact name
             * @param mapping the mapping
             */
            public SourcesToProcessItems(
                                          final ArtifactFinder<?> artifactFinder, final ArtifactSource artifactSource,
                                          final String artifactName, final ArtifactSourceMapping mapping ) {
                this.artifactFinder = artifactFinder;
                this.artifactSource = artifactSource;
                this.artifactName = artifactName;
                this.mapping = mapping;
            }

        }

        /** The configuration. */
        private final GlobalSettings                   configuration;

        /** The artifact providers. */
        private final ArtifactFinderBySourceProvider[] artifactProviders;

        /** The artifact types. */
        private Set<Class<? extends Artifact>>         artifactTypes;

        /** The sleep time. */
        private final long                             sleepTime;

        /** The executor. */
        private ExecutorService                        executor;

        /**
         * Instantiates a new artifact loader impl.
         * 
         * @param configuration the configuration
         * @param artifactProviders the artifact providers
         */
        public ArtifactLoaderImpl(
                                   final GlobalSettings configuration, final ArtifactFinderBySourceProvider... artifactProviders ) {
            this.configuration = configuration;
            this.artifactProviders = artifactProviders;
            this.sleepTime = configuration.getDefaultSleepingIntervalInMilliseconds();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ArtifactLoader#closeResources()
         */
        public void closeResources() {
            try {
                this.executor.shutdown();
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
            }
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ArtifactLoader#loadArtifactsFromSource(org.openspotlight.federation.domain.ArtifactSource[])
         */
        public Iterable<Artifact> loadArtifactsFromSource( final ArtifactSource... sources ) {
            final Queue<Pair<ArtifactFinder<?>, ArtifactSource>> sourcesToLoad = new ConcurrentLinkedQueue<Pair<ArtifactFinder<?>, ArtifactSource>>();
            final Queue<ArtifactLoaderImpl.SourcesToProcessItems> sourcesToProcess = new ConcurrentLinkedQueue<ArtifactLoaderImpl.SourcesToProcessItems>();
            final Queue<Artifact> loadedArtifacts = new ConcurrentLinkedQueue<Artifact>();

            addingSources: for (final ArtifactSource source : sources) {
                if (!source.isActive()) {
                    continue addingSources;
                }
                for (final ArtifactFinderBySourceProvider provider : this.artifactProviders) {
                    for (final Class<? extends Artifact> type : this.artifactTypes) {
                        final Set<ArtifactFinder<? extends Artifact>> artifactFinders = provider.getForType(type, source);
                        for (final ArtifactFinder<?> finder : artifactFinders) {
                            sourcesToLoad.add(new Pair<ArtifactFinder<?>, ArtifactSource>(finder, source));

                        }
                    }
                }
            }
            if (sourcesToLoad.size() == 0) {
                return Collections.emptySet();
            }
            for (final Pair<ArtifactFinder<?>, ArtifactSource> pair : new ArrayList<Pair<ArtifactFinder<?>, ArtifactSource>>(
                                                                                                                             sourcesToLoad)) {
                this.executor.execute(new Runnable() {

                    public void run() {
                        try {
                            for (final ArtifactSourceMapping mapping : pair.getK2().getMappings()) {
                                final Set<String> rawNames = pair.getK1().retrieveAllArtifactNames(mapping.getFrom());
                                final FilterResult newNames = filterNamesByPattern(rawNames, mapping.getIncludeds(),
                                                                                   mapping.getExcludeds(), false);

                                for (final String name : newNames.getIncludedNames()) {

                                    sourcesToProcess.add(new SourcesToProcessItems(pair.getK1(), pair.getK2(), name, mapping));
                                }
                            }
                        } catch (final Exception e) {
                            Exceptions.catchAndLog(e);
                        } finally {
                            sourcesToLoad.remove(pair);
                        }
                    }
                });
            }
            while (sourcesToLoad.size() != 0) {
                try {
                    Thread.sleep(this.sleepTime);
                } catch (final InterruptedException e) {

                }
            }
            if (sourcesToProcess.size() == 0) {
                return Collections.emptySet();
            }
            for (final ArtifactLoaderImpl.SourcesToProcessItems sourceToProcess : new ArrayList<ArtifactLoaderImpl.SourcesToProcessItems>(
                                                                                                                                          sourcesToProcess)) {
                this.executor.execute(new Runnable() {

                    public void run() {
                        try {

                            final Artifact loaded = sourceToProcess.artifactFinder.findByPath(sourceToProcess.artifactName);
                            String currentPathString = loaded.getParent().getCompletePath();
                            if (!currentPathString.startsWith("/")) {
                                currentPathString = "/" + currentPathString;
                            }
                            String toRemove = sourceToProcess.mapping.getFrom();
                            if (!toRemove.startsWith("/")) {
                                toRemove = "/" + toRemove;
                            }
                            if (currentPathString.startsWith(toRemove)) {
                                currentPathString = Strings.removeBegginingFrom(toRemove, currentPathString);
                            }
                            final String newPathString = sourceToProcess.mapping.getTo() + currentPathString;
                            final PathElement newPath = PathElement.createFromPathString(newPathString);
                            loaded.setParent(newPath);
                            loadedArtifacts.add(loaded);

                        } catch (final Exception e) {
                            Exceptions.catchAndLog(e);
                        } finally {
                            sourcesToProcess.remove(sourceToProcess);
                        }

                    }
                });
            }
            while (sourcesToProcess.size() != 0) {
                try {
                    Thread.sleep(this.sleepTime);
                } catch (final InterruptedException e) {

                }
            }
            return new ArrayList<Artifact>(loadedArtifacts);
        }

        /**
         * Setup.
         */
        public synchronized void setup() {
            this.executor = Executors.newFixedThreadPool(this.configuration.getNumberOfParallelThreads());
            this.artifactTypes = ArtifactTypeRegistry.INSTANCE.getRegisteredArtifactTypes();
        }
    }

    /**
     * Creates a new ArtifactLoader object.
     * 
     * @param configuration the configuration
     * @param artifactSourceProviders the artifact source providers
     * @return the artifact loader
     */
    public static ArtifactLoader createNewLoader( final GlobalSettings configuration,
                                                  final ArtifactFinderBySourceProvider... artifactSourceProviders ) {
        final ArtifactLoaderFactory.ArtifactLoaderImpl loader = new ArtifactLoaderImpl(configuration, artifactSourceProviders);
        loader.setup();
        return loader;
    }

    /**
     * Instantiates a new artifact loader factory.
     */
    private ArtifactLoaderFactory() {
    }

}
