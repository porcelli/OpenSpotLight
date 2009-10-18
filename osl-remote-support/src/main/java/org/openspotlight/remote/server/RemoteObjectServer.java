package org.openspotlight.remote.server;

import org.openspotlight.remote.internal.UserToken;

/**
 * The Interface RemoteObjectServer.
 */
public interface RemoteObjectServer {

    /**
     * Creates the user token.
     * 
     * @param user the user
     * @param password the password
     * @param clientHost the client host
     * @return the user token
     * @throws AccessDeniedException the access denied exception
     */
    public UserToken createUserToken( final String user,
                                      final String password,
                                      final String clientHost ) throws AccessDeniedException;

    /**
     * Shutdown.
     */
    public void shutdown();
}
