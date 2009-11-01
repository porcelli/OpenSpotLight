package org.openspotlight.security.idm.auth;

import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public interface IdentityManager {

    public AuthenticatedUser authenticate( final User user,
                                           final String password );

    public boolean isValid( final AuthenticatedUser user );

}
