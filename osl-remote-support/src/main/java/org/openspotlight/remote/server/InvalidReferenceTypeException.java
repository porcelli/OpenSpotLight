package org.openspotlight.remote.server;

import org.openspotlight.common.exception.SLException;

/**
 * The Class InvalidReferenceTypeException is thrown by {@link RemoteServer} when trying to create an invalid type.
 */
public class InvalidReferenceTypeException extends SLException {

    /**
     * 
     */
    private static final long serialVersionUID = 9201300058383328421L;

    /**
     * Instantiates a new invalid reference type exception.
     */
    public InvalidReferenceTypeException() {
        //
    }

    /**
     * Instantiates a new invalid reference type exception.
     * 
     * @param message the message
     */
    public InvalidReferenceTypeException(
                                          final String message ) {
        super(message);
    }

    /**
     * Instantiates a new invalid reference type exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public InvalidReferenceTypeException(
                                          final String message, final Throwable cause ) {
        super(message, cause);
    }

    /**
     * Instantiates a new invalid reference type exception.
     * 
     * @param cause the cause
     */
    public InvalidReferenceTypeException(
                                          final Throwable cause ) {
        super(cause);
    }

}
