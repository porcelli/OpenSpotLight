package org.openspotlight.graph.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphImpl;
import org.openspotlight.graph.persistence.SLPersistentTreeFactory;
import org.openspotlight.graph.persistence.SLPersistentTreeFactoryImpl;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.SecurityFactoryImpl;

/**
 * Created by User: feu - Date: Apr 28, 2010 - Time: 5:17:05 PM
 */
public class SLGraphModule extends AbstractModule{

    private final JcrConnectionDescriptor descriptor;

    public SLGraphModule(JcrConnectionDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    protected void configure() {
        bind(SecurityFactory.class).to(SecurityFactoryImpl.class);
        bind(SLPersistentTreeFactory.class).to(SLPersistentTreeFactoryImpl.class);
        bind(SLGraph.class).toProvider(SLGraphProvider.class);
        bind(JcrConnectionDescriptor.class).toInstance(descriptor);
    }
}
