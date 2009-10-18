package org.openspotlight.remote.server;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.remote.internal.UserToken;

/**
 * The Class RemoteObjectServer will handle and take care of all object instances.
 */
public class RemoteObjectServerImpl implements RemoteObjectServer {

    /** The user autenticator. */
    private final UserAutenticator     userAutenticator;

    /** The port to use. */
    private final int                  portToUse;

    /** The last user access. */
    private final Map<UserToken, Date> lastUserAccess = new ConcurrentHashMap<UserToken, Date>();

    /**
     * Instantiates a new remote object server.
     * 
     * @param userAutenticator the user autenticator
     * @param portToUse the port to use
     */
    public RemoteObjectServerImpl(
                                   final UserAutenticator userAutenticator, final int portToUse ) {
        try {
            this.userAutenticator = userAutenticator;
            this.portToUse = portToUse;
            Remote.config(null, portToUse, null, 0);
            ItemServer.bind(this, "RemoteObjectServer");
        } catch (final RemoteException e) {
            throw logAndReturnNew(format("Problem starting remote object server inside port {0}", portToUse), e,
                                  ConfigurationException.class);
        }
    }

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
                                      final String clientHost ) throws AccessDeniedException {
        final boolean canConnect = this.userAutenticator.canConnect(user, password, clientHost);
        if (!canConnect) {
            throw logAndReturn(new AccessDeniedException(format("User {0} from host {1} can't connect to this server instance",
                                                                user, clientHost)));
        }

        final UserToken token = new UserToken(user, UUID.randomUUID().toString());
        this.lastUserAccess.put(token, new Date());

        // FIXME see if whe need some limit, for example: if an user connect again, invalidate the last token

        return token;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        Remote.shutdown();
    }
}
