package org.openspotlight.federation.loader;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.processing.BundleProcessor;

public class ExampleBundleProcessor implements BundleProcessor<StreamArtifact> {

    public <A extends Artifact> boolean acceptKindOfArtifact( final Class<A> kindOfArtifact ) {
        // TODO Auto-generated method stub
        return false;
    }

    public void didFinishToProcessArtifact( final StreamArtifact artifact,
                                      final LastProcessStatus status ) {
        // TODO Auto-generated method stub

    }

    public void beforeProcessArtifact( final StreamArtifact artifact ) {
        // TODO Auto-generated method stub

    }

    public void didFinishiProcessing( final org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges<StreamArtifact> changes ) {
        // TODO Auto-generated method stub

    }

    public Class<StreamArtifact> getArtifactType() {
        // TODO Auto-generated method stub
        return null;
    }

    public org.openspotlight.federation.processing.BundleProcessor.SaveBehavior getSaveBehavior() {
        // TODO Auto-generated method stub
        return null;
    }

    public void didiFinishGlobalProcessing( final org.openspotlight.federation.processing.BundleProcessor.ArtifactProcessingResults<StreamArtifact> results ) {
        // TODO Auto-generated method stub

    }

    public LastProcessStatus processArtifact( final StreamArtifact artifact,
                                              final org.openspotlight.federation.processing.BundleProcessor.CurrentProcessorContext currentContext,
                                              final org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext context )
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public void selectArtifactsToBeProcessed( final org.openspotlight.federation.processing.BundleProcessor.CurrentProcessorContext currentContext,
                                              final org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext context,
                                              final org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges<StreamArtifact> changes,
                                              final org.openspotlight.federation.processing.BundleProcessor.ArtifactsToBeProcessed<StreamArtifact> toBeReturned ) {
        // TODO Auto-generated method stub

    }

}
