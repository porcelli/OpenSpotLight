package org.openspotlight.remote.server;

import org.openspotlight.common.exception.SLException;

/**
 * The Class ErrorOnMethodInvocationException.
 */
public class InternalErrorOnMethodInvocationException extends SLException {

    /**
     * 
     */
    private static final long serialVersionUID = -7376824325120225649L;

    /**
     * Instantiates a new error on method invocation exception.
     */
    public InternalErrorOnMethodInvocationException() {
        super();
    }

    /**
     * Instantiates a new error on method invocation exception.
     * 
     * @param message the message
     */
    public InternalErrorOnMethodInvocationException(
                                             final String message ) {
        super(message);
    }

    /**
     * Instantiates a new error on method invocation exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public InternalErrorOnMethodInvocationException(
                                             final String message, final Throwable cause ) {
        super(message, cause);
    }

    /**
     * Instantiates a new error on method invocation exception.
     * 
     * @param cause the cause
     */
    public InternalErrorOnMethodInvocationException(
                                             final Throwable cause ) {
        super(cause);
    }

}
