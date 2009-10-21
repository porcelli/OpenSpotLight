package org.openspotlight.graph.client;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.remote.client.CantConnectException;
import org.openspotlight.remote.client.RemoteObjectFactory;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.InvalidReferenceTypeException;

/**
 * A factory for creating RemoteGraphSession objects.
 */
public class RemoteGraphSessionFactory {

    /**
     * The Interface RemoteGraphFactoryConnectionData.
     */
    public interface RemoteGraphFactoryConnectionData {

        /**
         * Gets the host.
         * 
         * @return the host
         */
        public String getHost();

        /**
         * Gets the password.
         * 
         * @return the password
         */
        public String getPassword();

        /**
         * Gets the port.
         * 
         * @return the port
         */
        public int getPort();

        /**
         * Gets the user name.
         * 
         * @return the user name
         */
        public String getUserName();
    }

    /** The remote object factory. */
    private final RemoteObjectFactory     remoteObjectFactory;

    /** The descriptor. */
    private final JcrConnectionDescriptor descriptor;

    /**
     * Instantiates a new remote graph session factory.
     * 
     * @param connectionData the connection data
     * @param descriptor the descriptor
     * @throws CantConnectException the cant connect exception
     * @throws AccessDeniedException the access denied exception
     */
    public RemoteGraphSessionFactory(
                                      final RemoteGraphFactoryConnectionData connectionData,
                                      final JcrConnectionDescriptor descriptor )
        throws CantConnectException, AccessDeniedException {
        this.descriptor = descriptor;
        this.remoteObjectFactory = new RemoteObjectFactory(connectionData.getHost(), connectionData.getPort(),
                                                           connectionData.getUserName(), connectionData.getPassword());
    }

    /**
     * Creates a new RemoteGraphSession object.
     * 
     * @return the SL graph session
     */
    public SLGraphSession createRemoteGraphSession() {
        try {
            return this.remoteObjectFactory.createRemoteObject(SLGraphSession.class, this.descriptor);
        } catch (final InvalidReferenceTypeException e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
}
