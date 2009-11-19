/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.common.Disposable;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.finder.ArtifactFinderByRepositoryProvider;
import org.openspotlight.federation.finder.ArtifactFinderByRepositoryProviderFactory;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLoggerFactory;
import org.openspotlight.security.idm.AuthenticatedUser;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorSupport.
 */
public class BundleProcessorSupport implements Disposable {

    /** The graph. */
    private final SLGraph                                   graph;

    /** The user. */
    private final AuthenticatedUser                         user;

    /** The descriptor. */
    private final JcrConnectionDescriptor                   descriptor;

    /** The artifact finder factory. */
    private final ArtifactFinderByRepositoryProviderFactory artifactFinderFactory;

    /** The logger factory. */
    private final DetailedLoggerFactory                     loggerFactory;

    /** The oppened graph sessions. */
    private final CopyOnWriteArrayList<SLGraphSession>      oppenedGraphSessions = new CopyOnWriteArrayList<SLGraphSession>();

    /**
     * Instantiates a new bundle processor support.
     * 
     * @param user the user
     * @param descriptor the descriptor
     * @param artifactFinderFactory the artifact finder factory
     * @param loggerFactory the logger factory
     * @throws Exception the exception
     */
    public BundleProcessorSupport(
                                   final AuthenticatedUser user, final JcrConnectionDescriptor descriptor,
                                   final ArtifactFinderByRepositoryProviderFactory artifactFinderFactory,
                                   final DetailedLoggerFactory loggerFactory ) throws Exception {
        super();
        this.user = user;
        this.descriptor = descriptor;
        this.artifactFinderFactory = artifactFinderFactory;
        this.loggerFactory = loggerFactory;
        this.graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class).createGraph(descriptor);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.common.Disposable#closeResources()
     */
    public void closeResources() {
        try {
            this.loggerFactory.closeResources();
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
        try {
            this.artifactFinderFactory.closeResources();
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
        for (final SLGraphSession session : this.oppenedGraphSessions) {
            try {
                session.save();
                session.close();
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
            }
        }

    }

    /**
     * Creates the bundle context.
     * 
     * @return the bundle processor context impl
     * @throws Exception the exception
     */
    public <A extends Artifact> BundleProcessorContextImpl createBundleContext() throws Exception {

        final SLGraphSession graphSession = this.graph.openSession(this.user);
        this.oppenedGraphSessions.add(graphSession);
        final DetailedLogger logger = this.loggerFactory.createNewLogger();
        final ArtifactFinderByRepositoryProvider artifactFinderProvider = this.artifactFinderFactory.createNew();
        final BundleProcessorContextImpl ctx = new BundleProcessorContextImpl(artifactFinderProvider, this.user, graphSession,
                                                                              logger);
        return ctx;
    }

    /**
     * Gets the or create group node for.
     * 
     * @param context the context
     * @param group the group
     * @return the or create group node for
     */
    public SLNode getOrCreateGroupNodeFor( final SLContext context,
                                           final Group group ) {
        throw new UnsupportedOperationException();//FIXME implement
    }
}
