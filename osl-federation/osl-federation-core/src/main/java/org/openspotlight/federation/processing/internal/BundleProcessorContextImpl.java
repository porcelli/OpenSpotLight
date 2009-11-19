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

    private final ArtifactFinderByRepositoryProvider artifactBySourceProvider;

    private final AuthenticatedUser                  authenticatedUser;

    private final SLGraphSession                     graphSession;
    private final DetailedLogger                     logger;

    public BundleProcessorContextImpl(
                                       final ArtifactFinderByRepositoryProvider artifactBySourceProvider,
                                       final AuthenticatedUser authenticatedUser, final SLGraphSession graphSession,
                                       final DetailedLogger logger ) {
        super();
        this.artifactBySourceProvider = artifactBySourceProvider;
        this.authenticatedUser = authenticatedUser;
        this.graphSession = graphSession;
        this.logger = logger;
    }

    public <A extends Artifact> ArtifactFinder<A> getArtifactFinder( final Class<A> artifactType,
                                                                     final Repository repository ) {
        return this.artifactBySourceProvider.getByRepository(artifactType, repository);
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return this.authenticatedUser;
    }

    public SLGraphSession getGraphSession() {
        return this.graphSession;
    }

    public DetailedLogger getLogger() {
        return this.logger;
    }

}
