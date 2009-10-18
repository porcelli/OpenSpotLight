package org.openspotlight.remote.internal;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

/**
 * The Class UserToken is necessary for doing remote invocations. This class should not be used directly, since it is stored
 * Internally by the proxy objects.
 */
public class UserToken implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7576539340354513998L;

    /** The user. */
    private final String      user;

    /** The token. */
    private final String      token;

    private final int         hashcode;

    /**
     * Instantiates a new user token.
     * 
     * @param user the user
     * @param token the token
     */
    public UserToken(
                      final String user, final String token ) {
        checkNotEmpty("user", user);
        checkNotEmpty("token", token);
        this.user = user;
        this.token = token;
        this.hashcode = hashOf(user, token);
    }

    @Override
    public boolean equals( final Object obj ) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UserToken)) {
            return false;
        }
        final UserToken that = (UserToken)obj;
        return eachEquality(of(this.user, this.token), andOf(that.user, that.token));
    }

    /**
     * Gets the token.
     * 
     * @return the token
     */
    public String getToken() {
        return this.token;
    }

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public String getUser() {
        return this.user;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

    @Override
    public String toString() {
        return "UserToken: " + this.user;
    }

}
