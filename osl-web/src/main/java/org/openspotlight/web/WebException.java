package org.openspotlight.web;

/**
 * The Class WebException.
 */
public abstract class WebException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9092082090455398421L;

    /**
     * Instantiates a new web exception.
     */
    public WebException() {
        super();
    }

    /**
     * Instantiates a new web exception.
     * 
     * @param message the message
     */
    public WebException(
                         final String message ) {
        super(message);
    }

    /**
     * Instantiates a new web exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public WebException(
                         final String message, final Throwable cause ) {
        super(message, cause);
    }

    /**
     * Instantiates a new web exception.
     * 
     * @param cause the cause
     */
    public WebException(
                         final Throwable cause ) {
        super(cause);
    }

    /**
     * To json string.
     * 
     * @return the string
     */
    public abstract String toJsonString();

}
