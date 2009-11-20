package org.openspotlight.federation.processing.internal.task;

import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.finder.ArtifactFinderWithSaveCapabilitie;
import org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;

public class _3_SaveEachArtifactStatusTask<T extends Artifact> implements ArtifactTask {

    private final T                                    artifact;
    private final ArtifactFinderWithSaveCapabilitie<T> finder;

    public _3_SaveEachArtifactStatusTask(
                                          final T artifact, final ArtifactFinderWithSaveCapabilitie<T> finder ) {
        this.artifact = artifact;
        this.finder = finder;
    }

    public void doTask() {
        try {
            this.finder.save(this.artifact);
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
    }

    public CurrentProcessorContextImpl getCurrentContext() {
        return null;
    }

    public int getPriority() {
        return 3;
    }

    public void setBundleContext( final BundleProcessorContextImpl context ) {

    }

    public void setQueue( final PriorityBlockingQueue<ArtifactTask> queue ) {

    }
}
