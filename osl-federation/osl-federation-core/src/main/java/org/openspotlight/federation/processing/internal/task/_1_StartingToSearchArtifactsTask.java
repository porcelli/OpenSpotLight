package org.openspotlight.federation.processing.internal.task;

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext;
import org.openspotlight.federation.processing.BundleProcessor.SaveBehavior;
import org.openspotlight.federation.processing.internal.domain.ArtifactChangesImpl;
import org.openspotlight.federation.processing.internal.domain.ArtifactsToBeProcessedImpl;
import org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.security.idm.AuthenticatedUser;

public class _1_StartingToSearchArtifactsTask<T extends Artifact> implements ArtifactTask {
    private final AuthenticatedUser             user;

    /** The repository. */
    private final Repository                    repository;

    /** The artifact type. */
    private final Class<T>                      artifactType;

    /** The changes. */
    private final ArtifactChangesImpl<T>        changes;

    /** The context. */
    private BundleProcessorContext              context;

    /** The to be returned. */
    private final ArtifactsToBeProcessedImpl<T> toBeReturned;

    private final CurrentProcessorContextImpl   currentContext;

    /** The bundle processor type. */
    private final BundleProcessorType           bundleProcessorType;

    private PriorityBlockingQueue<ArtifactTask> queue;

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
    public _1_StartingToSearchArtifactsTask(
                                             final CurrentProcessorContextImpl currentContext,

                                             final Repository repository, final AuthenticatedUser user,
                                             final Class<? extends Artifact> artifactType,
                                             final BundleProcessorType bundleProcessorType ) {
        this.currentContext = currentContext;
        this.artifactType = (Class<T>)artifactType;
        this.repository = repository;
        this.bundleProcessorType = bundleProcessorType;
        this.toBeReturned = new ArtifactsToBeProcessedImpl<T>();
        this.changes = new ArtifactChangesImpl<T>();
        this.user = user;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void doTask() {
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

            final ArtifactFinder<T> finder = this.context.getArtifactFinder(this.artifactType, this.repository);
            for (final BundleSource src : this.bundleProcessorType.getSources()) {

                final Set<String> rawNames = finder.retrieveAllArtifactNames(src.getRelative());
                System.err.println("loaded " + rawNames);//FIXME REMOVE THIS!
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
                bundleProcessor.selectArtifactsToBeProcessed(this.currentContext, this.context, this.changes, this.toBeReturned);
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
                    final CurrentProcessorContextImpl taskCtx = new CurrentProcessorContextImpl();
                    taskCtx.setCurrentGroup(this.currentContext.getCurrentGroup());
                    taskCtx.setCurrentRepository(this.currentContext.getCurrentRepository());
                    this.queue.add(new _2_EachArtifactTask<T>(taskCtx, artifactToProcess, bundleProcessor, this.artifactType));
                    if (bundleProcessor.getSaveBehavior().equals(SaveBehavior.PER_PROCESSING)) {

                    }
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
        }
    }

    public CurrentProcessorContextImpl getCurrentContext() {
        return this.currentContext;
    }

    public int getPriority() {
        return 1;
    }

    public void setBundleContext( final BundleProcessorContextImpl context ) {
        this.context = context;

    }

    public void setQueue( final PriorityBlockingQueue<ArtifactTask> queue ) {
        this.queue = queue;

    }
}
