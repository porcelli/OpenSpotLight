/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderProvider;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext;

public class StartingRunnable<T extends Artifact> implements Runnable {

    private final Queue<Pair<BundleProcessorType, Class<T>>> startingQueue;
    private final Repository                                 repository;
    private final Pair<BundleProcessorType, Class<T>>        thisEntry;
    private final ArtifactFinderProvider                     artifactFinderProvider;
    private final Class<T>                                   artifactType;
    private final ArtifactChangesImpl<T>                     changes;
    private final BundleProcessorContext<T>                  context;
    private final ArtifactsToBeProcessedImpl<T>              toBeReturned;
    private final BundleProcessorType                        bundleProcessorType;

    public StartingRunnable(
                             final Queue<Pair<BundleProcessorType, Class<T>>> startingQueue,
                             final Pair<BundleProcessorType, Class<T>> thisEntry,
                             final ArtifactFinderProvider artifactFinderProvider, final BundleProcessorContext<T> context,
                             final Repository repository ) {
        this.startingQueue = startingQueue;
        this.thisEntry = thisEntry;
        this.artifactFinderProvider = artifactFinderProvider;
        this.artifactType = thisEntry.getK2();
        this.context = context;
        this.repository = repository;
        this.bundleProcessorType = thisEntry.getK1();
        this.toBeReturned = new ArtifactsToBeProcessedImpl<T>();
        this.changes = new ArtifactChangesImpl<T>();
    }

    public void run() {
        try {
            final BundleProcessor<?> rawBundleProcessor = this.bundleProcessorType.getType().newInstance();
            if (!rawBundleProcessor.acceptKindOfArtifact(this.artifactType)) {
                return;
            }
            final Set<T> changedArtifacts = new HashSet<T>();
            final Set<T> excludedArtifacts = new HashSet<T>();
            final Set<T> includedArtifacts = new HashSet<T>();
            final Set<T> notChangedArtifacts = new HashSet<T>();
            final BundleProcessor<T> bundleProcessor = (BundleProcessor<T>)rawBundleProcessor;
            final ArtifactFinder<T> finder = this.artifactFinderProvider.getFinderForType(this.repository, this.artifactType);
            for (final BundleSource src : this.bundleProcessorType.getSources()) {

                final Set<String> rawNames = finder.retrieveAllArtifactNames(src.getRelative());
                final FilterResult newNames = filterNamesByPattern(rawNames, src.getIncludeds(), src.getExcludeds(), false);
                for (final String name : newNames.getIncludedNames()) {
                    final T savedArtifact = finder.findByPath(name);
                    switch (savedArtifact.getChangeType()) {
                        case CHANGED:
                            changedArtifacts.add(savedArtifact);
                            break;
                        case EXCLUDED:
                            excludedArtifacts.add(savedArtifact);
                            break;
                        case INCLUDED:
                            includedArtifacts.add(savedArtifact);
                            break;
                        case NOT_CHANGED:
                            notChangedArtifacts.add(savedArtifact);
                            break;
                    }
                }

            }

            this.changes.setChangedArtifacts(changedArtifacts);
            this.changes.setExcludedArtifacts(excludedArtifacts);
            this.changes.setIncludedArtifacts(includedArtifacts);
            this.changes.setNotChangedArtifacts(notChangedArtifacts);

            final Set<T> artifactsAlreadyProcessed = new HashSet<T>();
            this.toBeReturned.setArtifactsAlreadyProcessed(artifactsAlreadyProcessed);
            final Set<T> artifactsToBeProcessed = new HashSet<T>();
            artifactsAlreadyProcessed.addAll(changedArtifacts);
            artifactsAlreadyProcessed.addAll(includedArtifacts);
            this.toBeReturned.setArtifactsToBeProcessed(artifactsToBeProcessed);
            bundleProcessor.selectArtifactsToBeProcessed(this.changes, this.context, this.toBeReturned);
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        } finally {
            this.startingQueue.remove(this.thisEntry);
        }
    }
}
