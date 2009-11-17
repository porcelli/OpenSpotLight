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
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;

public class ArtifactLoaderFactory {

    private static class ArtifactLoaderImpl implements ArtifactLoader {

        private static class SourcesToProcessItems {
            final ArtifactFinder<?>     artifactFinder;
            final ArtifactSource        artifactSource;
            final String                artifactName;
            final ArtifactSourceMapping mapping;

            public SourcesToProcessItems(
                                          final ArtifactFinder<?> artifactFinder, final ArtifactSource artifactSource,
                                          final String artifactName, final ArtifactSourceMapping mapping ) {
                this.artifactFinder = artifactFinder;
                this.artifactSource = artifactSource;
                this.artifactName = artifactName;
                this.mapping = mapping;
            }

        }

        private final GlobalSettings         configuration;
        private final ArtifactLoaderBehavior behavior;
        private final ArtifactFinder<?>[]    artifactFinders;

        private final long                   sleepTime;

        private ExecutorService              executor;

        public ArtifactLoaderImpl(
                                   final GlobalSettings configuration, final ArtifactLoaderBehavior behavior,
                                   final ArtifactFinder<?>... artifactFinders ) {
            this.configuration = configuration;
            this.behavior = behavior;
            this.artifactFinders = artifactFinders;
            this.sleepTime = configuration.getDefaultSleepingIntervalInMilliseconds();
        }

        public void closeResources() {
            try {
                this.executor.shutdown();
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
            }
            for (final ArtifactFinder<?> finder : this.artifactFinders) {
                try {
                    finder.closeResources();
                } catch (final Exception e) {
                    Exceptions.catchAndLog(e);
                }
            }
        }

        public Iterable<Artifact> loadArtifactsFromSource( final ArtifactSource... sources ) {
            final Queue<Pair<ArtifactFinder<?>, ArtifactSource>> sourcesToLoad = new ConcurrentLinkedQueue<Pair<ArtifactFinder<?>, ArtifactSource>>();
            final Queue<ArtifactLoaderImpl.SourcesToProcessItems> sourcesToProcess = new ConcurrentLinkedQueue<ArtifactLoaderImpl.SourcesToProcessItems>();
            final Queue<Artifact> loadedArtifacts = new ConcurrentLinkedQueue<Artifact>();

            addingSources: for (final ArtifactSource source : sources) {
                if (!source.isActive()) {
                    continue addingSources;
                }
                boolean hasAnyFinder = false;
                for (final ArtifactFinder<?> finder : this.artifactFinders) {
                    if (finder.canAcceptArtifactSource(source)) {
                        sourcesToLoad.add(new Pair<ArtifactFinder<?>, ArtifactSource>(finder, source));
                        hasAnyFinder = true;
                        if (ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE.equals(this.behavior)) {
                            continue addingSources;
                        }
                    }
                }
                if (hasAnyFinder) {
                    Exceptions.logAndThrow(new IllegalStateException("No configured artifact finder to process source " + source));
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
                                final Set<String> rawNames = pair.getK1().retrieveAllArtifactNames(pair.getK2(),
                                                                                                   mapping.getFrom());
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

                            final Artifact loaded = sourceToProcess.artifactFinder.findByPath(sourceToProcess.artifactSource,
                                                                                              sourceToProcess.artifactName);
                            String currentPathString = loaded.getParent().getCompletePath();
                            if (!currentPathString.startsWith("/")) {
                                currentPathString = "/" + currentPathString;
                            }
                            String toRemove = sourceToProcess.mapping.getFrom();
                            if (!toRemove.startsWith("/")) {
                                toRemove = "/" + toRemove;
                            }
                            final String newPathString = sourceToProcess.mapping.getTo()
                                                         + Strings.removeBegginingFrom(toRemove, currentPathString);
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

        public synchronized void setup() {
            this.executor = Executors.newFixedThreadPool(this.configuration.getNumberOfParallelThreads());
        }
    }

    public static ArtifactLoader createNewLoader( final GlobalSettings configuration,
                                                  final ArtifactLoaderBehavior behavior,
                                                  final ArtifactFinder<?>... artifactFinders ) {
        final ArtifactLoaderFactory.ArtifactLoaderImpl loader = new ArtifactLoaderImpl(configuration, behavior, artifactFinders);
        loader.setup();
        return loader;
    }

    private ArtifactLoaderFactory() {
    }

}
