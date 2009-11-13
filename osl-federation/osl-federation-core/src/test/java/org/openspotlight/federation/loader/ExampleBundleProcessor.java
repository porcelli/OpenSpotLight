package org.openspotlight.federation.loader;

import org.openspotlight.federation.data.processing.BundleProcessingFatalException;
import org.openspotlight.federation.data.processing.BundleProcessingNonFatalException;
import org.openspotlight.federation.data.processing.BundleProcessor;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.StreamArtifact;

public class ExampleBundleProcessor implements BundleProcessor<StreamArtifact> {

    public void globalProcessingFinalized( final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup<? extends Artifact> bundleProcessingGroup,
                                           final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingContext graphContext ) {
        // TODO Auto-generated method stub

    }

    public org.openspotlight.federation.data.processing.BundleProcessor.ProcessingStartAction globalProcessingStarted( final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup<StreamArtifact> bundleProcessingGroup,
                                                                                                                       final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingContext graphContext )
        throws BundleProcessingFatalException {
        // TODO Auto-generated method stub
        return null;
    }

    public org.openspotlight.federation.data.processing.BundleProcessor.ProcessingAction processArtifact( final StreamArtifact targetArtifact,
                                                                                                          final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup<StreamArtifact> bundleProcessingGroup,
                                                                                                          final org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingContext graphContext )
        throws BundleProcessingNonFatalException, BundleProcessingFatalException {
        // TODO Auto-generated method stub
        return null;
    }

}
