package org.openspotlight.security.idm.auth;

import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class IdentityManagerSimpleImpl implements IdentityManager {

    public AuthenticatedUser authenticate( final User user,
                                           final String password ) {
        return new AuthenticatedUser() {

            public String getId() {
                return user.getId();
            }
        };
    }

    public boolean isValid( final AuthenticatedUser user ) {
        return true;
    }
}
