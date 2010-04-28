package org.openspotlight.security.idm;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 9:41:22 AM
 */
public class UserProviderImpl implements UserProvider {

    public UserProviderImpl(
                             AuthenticatedUser currentUser ) {
        this.currentUser = currentUser;
    }

    private final AuthenticatedUser currentUser;

    public AuthenticatedUser getCurrentUser() {
        return currentUser;
    }
}
