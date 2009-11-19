/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.finder.JcrSessionArtifactBySourceProvider;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.idm.AuthenticatedUser;

public class BundleProcessorSupport {

    private SLGraph graph;
    
    private AuthenticatedUser user;
    
    private JcrConnectionProvider provider;
    
    private Jcr
    
    public BundleProcessorSupport(
                                   final JcrConnectionDescriptor descriptor,
                                   final AuthenticatedUser user, final JcrSessionArtifactBySourceProvider provider ) {

    }

    public <A extends Artifact> BundleProcessorContextImpl createBundleContext() {

        final BundleProcessorContextImpl context = new BundleProcessorContextImpl<A>();
        context.se
    }

    public SLNode getOrCreateGroupNodeFor( final SLContext context,
                                           final Group group ) {
        throw new UnsupportedOperationException();//FIXME implement
    }
}
