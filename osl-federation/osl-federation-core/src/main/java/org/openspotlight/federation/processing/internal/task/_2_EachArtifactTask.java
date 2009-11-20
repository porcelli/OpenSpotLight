package org.openspotlight.federation.processing.internal.task;

import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderWithSaveCapabilitie;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.BundleProcessor.SaveBehavior;
import org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.log.DetailedLogger.LogEventType;

public class _2_EachArtifactTask<T extends Artifact> implements ArtifactTask {
    /** The bundle processor context. */
    private BundleProcessorContextImpl          bundleProcessorContext;

    /** The artifact type. */
    private final Class<T>                      artifactType;

    private PriorityBlockingQueue<ArtifactTask> queue;

    /** The artifact. */
    private final T                             artifact;

    /** The bundle processor. */
    private final BundleProcessor<T>            bundleProcessor;

    /** The current context impl. */
    private final CurrentProcessorContextImpl   currentContextImpl;

    /**
     * Instantiates a new artifact processing runnable.
     * 
     * @param currentCtx the current ctx
     * @param artifactQueue the artifact queue
     * @param artifact the artifact
     * @param bundleProcessor the bundle processor
     * @param artifactType the artifact type
     */
    public _2_EachArtifactTask(
                                final CurrentProcessorContextImpl currentCtx, final T artifact,
                                final BundleProcessor<T> bundleProcessor, final Class<T> artifactType ) {
        this.artifactType = artifactType;
        this.artifact = artifact;
        this.bundleProcessor = bundleProcessor;
        this.currentContextImpl = currentCtx;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void doTask() {
        this.bundleProcessor.beforeProcessArtifact(this.artifact);
        LastProcessStatus result;
        try {
            if (this.artifact instanceof ArtifactWithSyntaxInformation) {
                final ArtifactWithSyntaxInformation artifactWithInfo = (ArtifactWithSyntaxInformation)this.artifact;
                artifactWithInfo.clearSyntaxInformationSet();
            }
            result = this.bundleProcessor.processArtifact(this.artifact, this.currentContextImpl, this.bundleProcessorContext);
            if (SaveBehavior.PER_ARTIFACT.equals(this.bundleProcessor.getSaveBehavior())) {
                this.bundleProcessorContext.getGraphSession().save();
            }
        } catch (final Exception e) {
            result = LastProcessStatus.EXCEPTION_DURRING_PROCESS;
            Exceptions.catchAndLog(e);
            this.bundleProcessorContext.getLogger().log(
                                                        this.bundleProcessorContext.getAuthenticatedUser(),
                                                        LogEventType.ERROR,
                                                        "Error during artifact processing on bundle processor "
                                                        + this.bundleProcessor.getClass().getName(), this.artifact);
        }
        this.artifact.setLastProcessStatus(result);
        this.artifact.setLastProcessedDate(new Date());
        final ArtifactFinder<T> finder = this.bundleProcessorContext.getArtifactFinder(
                                                                                       this.artifactType,
                                                                                       this.currentContextImpl.getCurrentRepository());
        if (finder instanceof ArtifactFinderWithSaveCapabilitie) {
            final ArtifactFinderWithSaveCapabilitie<T> finderWithSaveCapabilitie = (ArtifactFinderWithSaveCapabilitie<T>)finder;
            this.queue.offer(new _3_SaveEachArtifactStatusTask(this.artifact, finderWithSaveCapabilitie));
        }
        this.bundleProcessor.didFinishToProcessArtifact(this.artifact, result);
    }

    public CurrentProcessorContextImpl getCurrentContext() {
        return this.currentContextImpl;
    }

    public int getPriority() {
        return 2;
    }

    public void setBundleContext( final BundleProcessorContextImpl context ) {
        this.bundleProcessorContext = context;

    }

    public void setQueue( final PriorityBlockingQueue<ArtifactTask> queue ) {
        this.queue = queue;
    }
}
