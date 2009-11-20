package org.openspotlight.federation.processing.internal.domain;

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

    /** The artifact by source provider. */
    private final ArtifactFinderByRepositoryProvider artifactBySourceProvider;

    /** The authenticated user. */
    private final AuthenticatedUser                  authenticatedUser;

    /** The graph session. */
    private final SLGraphSession                     graphSession;

    /** The logger. */
    private final DetailedLogger                     logger;

    /**
     * Instantiates a new bundle processor context impl.
     * 
     * @param artifactBySourceProvider the artifact by source provider
     * @param authenticatedUser the authenticated user
     * @param graphSession the graph session
     * @param logger the logger
     */
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

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getArtifactFinder(java.lang.Class, org.openspotlight.federation.domain.Repository)
     */
    public <A extends Artifact> ArtifactFinder<A> getArtifactFinder( final Class<A> artifactType,
                                                                     final Repository repository ) {
        return this.artifactBySourceProvider.getByRepository(artifactType, repository);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getAuthenticatedUser()
     */
    public AuthenticatedUser getAuthenticatedUser() {
        return this.authenticatedUser;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getGraphSession()
     */
    public SLGraphSession getGraphSession() {
        return this.graphSession;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getLogger()
     */
    public DetailedLogger getLogger() {
        return this.logger;
    }

}
