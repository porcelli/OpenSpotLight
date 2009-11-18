/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import java.util.Queue;

import org.openspotlight.common.Pair;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderProvider;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges;
import org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed;
import org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext;

public class StartingRunnable<T extends Artifact> implements Runnable {

    private final Queue<Pair<Class<T>, BundleProcessor<T>>> startingQueue;
    private final Repository                                repository;
    private final Pair<Class<T>, BundleProcessor<T>>        thisEntry;
    private final ArtifactFinderProvider                    artifactFinderProvider;
    private final Class<T>                                  artifactType;
    ArtifactChanges<T>                                      changes;
    private final BundleProcessorContext<T>                 context;
    ArtifactsToBeProcessed<T>                               toBeReturned;

    public StartingRunnable(
                             final Queue<Pair<Class<T>, BundleProcessor<T>>> startingQueue,
                             final Pair<Class<T>, BundleProcessor<T>> thisEntry,
                             final ArtifactFinderProvider artifactFinderProvider, final Class<T> artifactType,
                             final BundleProcessorContext<T> context, final Repository repository ) {
        this.startingQueue = startingQueue;
        this.thisEntry = thisEntry;
        this.artifactFinderProvider = artifactFinderProvider;
        this.artifactType = artifactType;
        this.context = context;
        this.repository = repository;
    }

    public void run() {
        if (this.thisEntry.getK2().acceptKindOfArtifact(this.artifactType)) {
            final ArtifactFinder<T> finder = this.artifactFinderProvider.getFinderForType(this.repository, this.artifactType);
            finder.listByPath(artifactSource, null);
            this.thisEntry.getK2().selectArtifactsToBeProcessed(this.changes, this.context, this.toBeReturned);
        }
        this.startingQueue.remove(this.thisEntry);
    }

}
