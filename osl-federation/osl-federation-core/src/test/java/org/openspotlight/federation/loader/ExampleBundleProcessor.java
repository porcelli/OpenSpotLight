package org.openspotlight.federation.loader;

import org.openspotlight.federation.data.processing.BundleProcessor;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.StreamArtifact;

public class ExampleBundleProcessor implements BundleProcessor<StreamArtifact> {

    public <A extends Artifact> boolean acceptKindOfArtifact( final Class<A> kindOfArtifact ) {
        // TODO Auto-generated method stub
        return false;
    }

    public void afterProcessArtifact( final StreamArtifact artifact,
                                      final LastProcessStatus status ) {
        // TODO Auto-generated method stub

    }

    public void beforeProcessArtifact( final StreamArtifact artifact ) {
        // TODO Auto-generated method stub

    }

    public void globalProcessingDone( final org.openspotlight.federation.data.processing.BundleProcessor.ArtifactProcessingResults<StreamArtifact> results ) {
        // TODO Auto-generated method stub

    }

    public LastProcessStatus processArtifact( final StreamArtifact artifact,
                                              final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessorContext<StreamArtifact> context )
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public org.openspotlight.federation.data.processing.BundleProcessor.ArtifactsToBeProcessed<StreamArtifact> returnArtifactsToBeProcessed( final org.openspotlight.federation.data.processing.BundleProcessor.ArtifactChanges<StreamArtifact> changes,
                                                                                                                                             final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessorContext<StreamArtifact> context,
                                                                                                                                             final org.openspotlight.federation.data.processing.BundleProcessor.ArtifactsToBeProcessed<StreamArtifact> toBeReturned ) {
        // TODO Auto-generated method stub
        return null;
    }

}
