package org.openspotlight.remote.client;

import org.openspotlight.common.exception.SLException;

public class CantConnectException extends SLException {

    public CantConnectException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public CantConnectException(
                                 final String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CantConnectException(
                                 final String message, final Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public CantConnectException(
                                 final Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
