package org.openspotlight.graph.client;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.graph.SLGraphSession;
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

    public static final class RemoteGraphFactoryConnectionDataImpl implements RemoteGraphFactoryConnectionData {
        private final String host;

        private final String password;

        private final int    port;

        private final String userName;

        public RemoteGraphFactoryConnectionDataImpl(
                                                     final String host, final String userName, final String password,
                                                     final int port ) {
            this.host = host;
            this.password = password;
            this.port = port;
            this.userName = userName;
        }

        public String getHost() {
            return this.host;
        }

        public String getPassword() {
            return this.password;
        }

        public int getPort() {
            return this.port;
        }

        public String getUserName() {
            return this.userName;
        }
    }

    public static final int           DEFAULT_PORT = 9876;

    /** The remote object factory. */
    private final RemoteObjectFactory remoteObjectFactory;

    /**
     * Instantiates a new remote graph session factory.
     * 
     * @param connectionData the connection data
     * @param descriptor the descriptor
     * @throws CantConnectException the cant connect exception
     * @throws AccessDeniedException the access denied exception
     */
    public RemoteGraphSessionFactory(
                                      final RemoteGraphFactoryConnectionData connectionData )
        throws CantConnectException, AccessDeniedException {
        this.remoteObjectFactory = new RemoteObjectFactory(connectionData.getHost(), connectionData.getPort(),
                                                           connectionData.getUserName(), connectionData.getPassword());
    }

    /**
     * Creates a new RemoteGraphSession object.
     * 
     * @return the SL graph session
     */
    public SLGraphSession createRemoteGraphSession( final String username,
                                                    final String password ) {
        try {
            return this.remoteObjectFactory.createRemoteObject(SLGraphSession.class, username, password);
        } catch (final InvalidReferenceTypeException e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
}
