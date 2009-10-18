package org.openspotlight.remote.server;

/**
 * The Interface UserAutenticator should be used inside the {@link RemoteObjectServer} to autenticate users from some client host.
 */
public interface UserAutenticator {

    /**
     * Can connect to this server?
     * 
     * @param userName the user name
     * @param password the password
     * @param clientHost the client host
     * @return true, if successful
     */
    public boolean canConnect( String userName,
                               String password,
                               String clientHost );

}
