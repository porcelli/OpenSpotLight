package org.openspotlight.remote.server;

import org.openspotlight.common.exception.SLException;

public class UserTokenInvalid extends SLException {

    private static final long serialVersionUID = -8860904224886777869L;

    public UserTokenInvalid() {
        // TODO Auto-generated constructor stub
    }

    public UserTokenInvalid(
                             String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public UserTokenInvalid(
                             String message, Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public UserTokenInvalid(
                             Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
