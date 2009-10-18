package org.openspotlight.remote.internal;

import java.io.Serializable;

public class UserToken implements Serializable {
    private String user;
    private String token;

    public String getToken() {
        return this.token;
    }

    public String getUser() {
        return this.user;
    }

    public void setToken( final String token ) {
        this.token = token;
    }

    public void setUser( final String user ) {
        this.user = user;
    }
}
