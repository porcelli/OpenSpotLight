package org.openspotlight.web;

public abstract class WebException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -9092082090455398421L;

    public WebException() {
        super();
    }

    public WebException(
                         final String message ) {
        super(message);
    }

    public WebException(
                         final String message, final Throwable cause ) {
        super(message, cause);
    }

    public WebException(
                         final Throwable cause ) {
        super(cause);
    }

    public abstract String toJsonString();

}
