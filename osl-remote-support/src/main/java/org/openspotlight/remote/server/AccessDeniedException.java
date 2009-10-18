package org.openspotlight.remote.server;

import org.openspotlight.common.exception.SLException;

/**
 * This exception is thrown when the user can't connect to the {@link RemoteObjectServer}.
 */
public class AccessDeniedException extends SLException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8480610881209425860L;

    /**
     * Instantiates a new access denied exception.
     */
    public AccessDeniedException() {
        super();
    }

    /**
     * Instantiates a new access denied exception.
     * 
     * @param message the message
     */
    public AccessDeniedException(
                                  final String message ) {
        super(message);
    }

    /**
     * Instantiates a new access denied exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public AccessDeniedException(
                                  final String message, final Throwable cause ) {
        super(message, cause);
    }

    /**
     * Instantiates a new access denied exception.
     * 
     * @param cause the cause
     */
    public AccessDeniedException(
                                  final Throwable cause ) {
        super(cause);
    }

}
