package org.openspotlight.federation.processing.internal;

import java.util.Date;
import java.util.Queue;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.BundleProcessor.SaveBehavior;
import org.openspotlight.log.DetailedLogger.LogEventType;

// TODO: Auto-generated Javadoc
/**
 * The Class ArtifactProcessingRunnable.
 */
public class ArtifactProcessingRunnable<T extends Artifact> implements RunnableWithBundleContext {

    /** The bundle processor context. */
    private BundleProcessorContextImpl                                  bundleProcessorContext;

    /** The artifact type. */
    private final Class<T>                                              artifactType;

    /** The artifact queue. */
    private final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue;

    /** The artifact. */
    private final T                                                     artifact;

    /** The bundle processor. */
    private final BundleProcessor<T>                                    bundleProcessor;

    /** The current context impl. */
    private final CurrentProcessorContextImpl                           currentContextImpl;

    /**
     * Instantiates a new artifact processing runnable.
     * 
     * @param currentCtx the current ctx
     * @param artifactQueue the artifact queue
     * @param artifact the artifact
     * @param bundleProcessor the bundle processor
     * @param artifactType the artifact type
     */
    public ArtifactProcessingRunnable(
                                       final CurrentProcessorContextImpl currentCtx,
                                       final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue,

                                       final T artifact, final BundleProcessor<T> bundleProcessor, final Class<T> artifactType ) {
        this.artifactType = artifactType;
        this.artifact = artifact;
        this.bundleProcessor = bundleProcessor;
        this.artifactQueue = artifactQueue;
        this.currentContextImpl = currentCtx;
    }

    /**
     * Gets the bundle processor context.
     * 
     * @return the bundle processor context
     */
    public BundleProcessorContextImpl getBundleProcessorContext() {
        return this.bundleProcessorContext;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.internal.RunnableWithBundleContext#getCurrentContext()
     */
    public CurrentProcessorContextImpl getCurrentContext() {
        return this.currentContextImpl;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        this.bundleProcessor.beforeProcessArtifact(this.artifact);
        LastProcessStatus result;
        try {
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
        this.bundleProcessor.afterProcessArtifact(this.artifact, result);
        this.artifactQueue.remove(this);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.internal.RunnableWithBundleContext#setBundleContext(org.openspotlight.federation.processing.internal.BundleProcessorContextImpl)
     */
    public void setBundleContext( final BundleProcessorContextImpl context ) {
        this.bundleProcessorContext = context;

    }

}
