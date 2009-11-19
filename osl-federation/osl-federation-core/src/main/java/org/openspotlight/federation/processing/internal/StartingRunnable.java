/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderProvider;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.security.idm.AuthenticatedUser;

// TODO: Auto-generated Javadoc
/**
 * The Class StartingRunnable.
 */
public class StartingRunnable<T extends Artifact> implements Runnable {

    private final AuthenticatedUser                                     user;

    /** The starting queue. */
    private final Queue<Pair<BundleProcessorType, Class<T>>>            startingQueue;

    /** The artifact queue. */
    private final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue;

    /** The repository. */
    private final Repository                                            repository;

    /** The this entry. */
    private final Pair<BundleProcessorType, Class<T>>                   thisEntry;

    /** The artifact finder provider. */
    private final ArtifactFinderProvider                                artifactFinderProvider;

    /** The artifact type. */
    private final Class<T>                                              artifactType;

    /** The changes. */
    private final ArtifactChangesImpl<T>                                changes;

    /** The context. */
    private final BundleProcessorContext<T>                             context;

    /** The to be returned. */
    private final ArtifactsToBeProcessedImpl<T>                         toBeReturned;

    /** The bundle processor type. */
    private final BundleProcessorType                                   bundleProcessorType;

    /**
     * Instantiates a new starting runnable.
     * 
     * @param startingQueue the starting queue
     * @param thisEntry the this entry
     * @param artifactFinderProvider the artifact finder provider
     * @param context the context
     * @param repository the repository
     * @param artifactQueue the artifact queue
     */
    public StartingRunnable(
                             final Queue<Pair<BundleProcessorType, Class<T>>> startingQueue,
                             final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue,
                             final Pair<BundleProcessorType, Class<T>> thisEntry,
                             final ArtifactFinderProvider artifactFinderProvider, final BundleProcessorContext<T> context,
                             final Repository repository, final AuthenticatedUser user ) {
        this.startingQueue = startingQueue;
        this.artifactQueue = artifactQueue;
        this.thisEntry = thisEntry;
        this.artifactFinderProvider = artifactFinderProvider;
        this.artifactType = thisEntry.getK2();
        this.context = context;
        this.repository = repository;
        this.bundleProcessorType = thisEntry.getK1();
        this.toBeReturned = new ArtifactsToBeProcessedImpl<T>();
        this.changes = new ArtifactChangesImpl<T>();
        this.user = user;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
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
            final Date lastProcessedDate = new Date();
            try {
                bundleProcessor.selectArtifactsToBeProcessed(this.changes, this.context, this.toBeReturned);
                for (final T artifactAlreadyProcessed : this.toBeReturned.getArtifactsAlreadyProcessed()) {
                    artifactAlreadyProcessed.setLastProcessedDate(lastProcessedDate);
                    artifactAlreadyProcessed.setLastProcessStatus(LastProcessStatus.PROCESSED);
                    this.context.getLogger().log(
                                                 this.user,
                                                 LogEventType.TRACE,
                                                 "Artifact processed on starting for bundle Processor "
                                                 + bundleProcessor.getClass().getName(), artifactAlreadyProcessed);
                }
                for (final T artifactToProcess : this.toBeReturned.getArtifactsToBeProcessed()) {
                    this.artifactQueue.add(new ArtifactProcessingRunnable<T>(this.artifactQueue, artifactToProcess,
                                                                             bundleProcessor, this.artifactType));
                }
            } catch (final Exception e) {
                for (final T artifactWithError : this.toBeReturned.getArtifactsToBeProcessed()) {
                    artifactWithError.setLastProcessedDate(lastProcessedDate);
                    artifactWithError.setLastProcessStatus(LastProcessStatus.EXCEPTION_DURRING_PROCESS);
                    this.context.getLogger().log(
                                                 this.user,
                                                 LogEventType.ERROR,
                                                 "Error on trying to process artifact on starting for bundle Processor "
                                                 + bundleProcessor.getClass().getName(), artifactWithError);
                }
                throw e;
            }

        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        } finally {
            this.startingQueue.remove(this.thisEntry);
        }
    }
}
