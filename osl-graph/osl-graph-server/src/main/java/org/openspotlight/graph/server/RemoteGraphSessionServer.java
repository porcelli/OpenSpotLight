package org.openspotlight.graph.server;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.remote.server.RemoteObjectServer;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory;

/**
 * The Class RemoteGraphSessionServer.
 */
public class RemoteGraphSessionServer {

    /**
     * A factory for creating InternalGraphSession objects.
     */
    private static class InternalGraphSessionFactory implements InternalObjectFactory<SLGraphSession> {

        /** The current graph map. */
        private final Map<JcrConnectionDescriptor, SLGraph> currentGraphMap = new ConcurrentHashMap<JcrConnectionDescriptor, SLGraph>();

        /** The graph factory. */
        private final SLGraphFactory                        graphFactory;

        /**
         * Instantiates a new internal graph session factory.
         */
        public InternalGraphSessionFactory() {
            try {
                this.graphFactory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            } catch (final AbstractFactoryException e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /* (non-Javadoc)
         * @see org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory#createNewInstance(java.lang.Object[])
         */
        public synchronized SLGraphSession createNewInstance( final Object... parameters ) throws Exception {
            checkNotNull("parameters", parameters);
            checkCondition("correctParamSize", parameters.length == 1);
            checkCondition("correctParameterClass", parameters[0] instanceof JcrConnectionDescriptor);

            final JcrConnectionDescriptor descriptor = (JcrConnectionDescriptor)parameters[0];
            SLGraph graph = this.currentGraphMap.get(descriptor);
            if (graph == null) {
                final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(descriptor);
                graph = this.graphFactory.createGraph(provider);
                this.currentGraphMap.put(descriptor, graph);

            }
            return graph.openSession();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory#getTargetObjectType()
         */
        public Class<SLGraphSession> getTargetObjectType() {
            return SLGraphSession.class;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory#shutdown()
         */
        public void shutdown() {
            for (final Entry<JcrConnectionDescriptor, SLGraph> entry : this.currentGraphMap.entrySet()) {
                entry.getValue().shutdown();
            }
            for (final Entry<JcrConnectionDescriptor, SLGraph> entry : this.currentGraphMap.entrySet()) {
                final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(entry.getKey());
                provider.closeRepository();
            }

        }
    }

    /** The remote object server. */
    private final RemoteObjectServer remoteObjectServer;

    /**
     * Instantiates a new remote graph session server.
     * 
     * @param userAutenticator the user autenticator
     * @param portToUse the port to use
     * @param timeoutInMilliseconds the timeout in milliseconds
     */
    public RemoteGraphSessionServer(
                                     final UserAuthenticator userAutenticator, final Integer portToUse,
                                     final Long timeoutInMilliseconds ) {
        this.remoteObjectServer = new RemoteObjectServerImpl(userAutenticator, portToUse, timeoutInMilliseconds);
        this.remoteObjectServer.registerInternalObjectFactory(SLGraphSession.class, new InternalGraphSessionFactory());
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        this.remoteObjectServer.shutdown();
    }
}
