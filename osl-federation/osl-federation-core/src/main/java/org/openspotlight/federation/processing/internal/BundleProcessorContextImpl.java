package org.openspotlight.federation.processing.internal;

import java.util.HashMap;
import java.util.Map;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.JcrSessionArtifactBySourceProvider;
import org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.security.idm.AuthenticatedUser;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorContextImpl.
 */
public class BundleProcessorContextImpl implements BundleProcessorContext {

    private final Map<Repository, JcrSessionArtifactBySourceProvider> providerMap = new HashMap<Repository, JcrSessionArtifactBySourceProvider>();

    /** The authenticated user. */
    private AuthenticatedUser                                         authenticatedUser;

    /** The current group. */
    private Group                                                     currentGroup;

    /** The current node group. */
    private SLNode                                                    currentNodeGroup;

    /** The graph session. */
    private SLGraphSession                                            graphSession;

    /** The logger. */
    private DetailedLogger                                            logger;

    /** The group context. */
    private SLContext                                                 groupContext;

    public <A extends Artifact> ArtifactFinder<A> getArtifactFinder( final Class<A> artifactType ) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getAuthenticatedUser()
     */
    public AuthenticatedUser getAuthenticatedUser() {
        return this.authenticatedUser;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getCurrentGroup()
     */
    public Group getCurrentGroup() {
        return this.currentGroup;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getCurrentNodeGroup()
     */
    public SLNode getCurrentNodeGroup() {
        return this.currentNodeGroup;
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

    /* (non-Javadoc)
     * @see org.openspotlight.federation.processing.BundleProcessor.BundleProcessorContext#getNodeForGroup(org.openspotlight.federation.domain.Group)
     */
    public SLNode getNodeForGroup( final Group group ) {
        return BundleProcessorSupport.getOrCreateGroupNodeFor(this.groupContext, group);
    }

    /**
     * Sets the authenticated user.
     * 
     * @param authenticatedUser the new authenticated user
     */
    public void setAuthenticatedUser( final AuthenticatedUser authenticatedUser ) {
        this.authenticatedUser = authenticatedUser;
    }

    /**
     * Sets the current group.
     * 
     * @param currentGroup the new current group
     */
    public void setCurrentGroup( final Group currentGroup ) {
        this.currentGroup = currentGroup;
    }

    /**
     * Sets the current node group.
     * 
     * @param currentNodeGroup the new current node group
     */
    public void setCurrentNodeGroup( final SLNode currentNodeGroup ) {
        this.currentNodeGroup = currentNodeGroup;
    }

    /**
     * Sets the graph session.
     * 
     * @param graphSession the new graph session
     */
    public void setGraphSession( final SLGraphSession graphSession ) {
        this.graphSession = graphSession;
    }

    /**
     * Sets the logger.
     * 
     * @param logger the new logger
     */
    public void setLogger( final DetailedLogger logger ) {
        this.logger = logger;
    }
}
