package org.openspotlight.federation.processing.internal.task;

import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.processing.BundleProcessor;
import org.openspotlight.federation.processing.internal.domain.ArtifactChangesImpl;
import org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class _6_EndingToProcessArtifactsTask.
 */
public class _4_EndingToProcessArtifactsTask<T extends Artifact> implements ArtifactTask {

    /** The changes. */
    private final ArtifactChangesImpl<T> changes;

    /** The processor. */
    private final BundleProcessor<T>     processor;

    /**
     * Instantiates a new _6_ ending to process artifacts task.
     * 
     * @param changes the changes
     * @param processor the processor
     */
    public _4_EndingToProcessArtifactsTask(
                                            final ArtifactChangesImpl<T> changes, final BundleProcessor<T> processor ) {
        super();
        this.changes = changes;
        this.processor = processor;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.internal.task.ArtifactTask#doTask()
     */
    public void doTask() {
        this.processor.didFinishiProcessing(this.changes);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.internal.task.ArtifactTask#getCurrentContext()
     */
    public CurrentProcessorContextImpl getCurrentContext() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.internal.task.ArtifactTask#setBundleContext(org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl)
     */
    public void setBundleContext( final BundleProcessorContextImpl context ) {

    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.internal.task.ArtifactTask#setQueue(java.util.concurrent.PriorityBlockingQueue)
     */
    public void setQueue( final PriorityBlockingQueue<ArtifactTask> queue ) {

    }
}
