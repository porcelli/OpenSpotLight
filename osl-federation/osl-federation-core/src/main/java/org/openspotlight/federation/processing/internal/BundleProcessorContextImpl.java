package org.openspotlight.federation.processing.internal;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderByRepositoryProvider;
import org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.security.idm.AuthenticatedUser;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorContextImpl.
 */
public class BundleProcessorContextImpl implements BundleProcessorContext {

    private final ArtifactFinderByRepositoryProvider artifactBySourceProviderFactory;

    private final AuthenticatedUser                  authenticatedUser;
    private final SLGraphSession                     graphSession;

    private final DetailedLogger                     logger;

    public <A extends Artifact> ArtifactFinder<A> getArtifactFinder( final Class<A> artifactType,
                                                                     final Repository repository ) {
        return this.artifactBySourceProviderFactory.getByRepository(repository).getForType(artifactType, source);
    }

    public AuthenticatedUser getAuthenticatedUser() {
        // TODO Auto-generated method stub
        return null;
    }

    public SLGraphSession getGraphSession() {
        // TODO Auto-generated method stub
        return null;
    }

    public DetailedLogger getLogger() {
        // TODO Auto-generated method stub
        return null;
    }

}
