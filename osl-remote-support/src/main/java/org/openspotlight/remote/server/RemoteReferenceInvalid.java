package org.openspotlight.remote.server;

import org.openspotlight.common.exception.SLException;

public class RemoteReferenceInvalid extends SLException {

    private static final long serialVersionUID = 1112800963858874985L;

    public RemoteReferenceInvalid() {
        // TODO Auto-generated constructor stub
    }

    public RemoteReferenceInvalid(
                                   String message ) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public RemoteReferenceInvalid(
                                   String message, Throwable cause ) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public RemoteReferenceInvalid(
                                   Throwable cause ) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
