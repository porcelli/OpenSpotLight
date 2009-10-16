package org.openspotlight.graph.client;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public interface SLRemoteGraphSessionFactory {

    public SLGraphSession createRemoteSession( JcrConnectionDescriptor descriptor );

}
