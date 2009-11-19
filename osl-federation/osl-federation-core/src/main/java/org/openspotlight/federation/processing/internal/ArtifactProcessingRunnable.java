package org.openspotlight.federation.processing.internal;

import java.util.Date;
import java.util.Queue;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.log.DetailedLogger.LogEventType;

public class ArtifactProcessingRunnable<T extends Artifact> implements Runnable {

    private BundleProcessorContextImpl<T>                               bundleProcessorContext;

    private final Class<T>                                              artifactType;

    private final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue;

    private final T                                                     artifact;

    private final BundleProcessor<T>                                    bundleProcessor;

    public ArtifactProcessingRunnable(
                                       final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue,

                                       final T artifact, final BundleProcessor<T> bundleProcessor, final Class<T> artifactType ) {
        this.artifactType = artifactType;
        this.artifact = artifact;
        this.bundleProcessor = bundleProcessor;
        this.artifactQueue = artifactQueue;
    }

    public BundleProcessorContextImpl<T> getBundleProcessorContext() {
        return this.bundleProcessorContext;
    }

    public void run() {
        this.bundleProcessor.beforeProcessArtifact(this.artifact);
        LastProcessStatus result;
        try {
            result = this.bundleProcessor.processArtifact(this.artifact, this.bundleProcessorContext);

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

    public void setBundleProcessorContext( final BundleProcessorContextImpl<T> bundleProcessorContext ) {
        this.bundleProcessorContext = bundleProcessorContext;

    }

}
