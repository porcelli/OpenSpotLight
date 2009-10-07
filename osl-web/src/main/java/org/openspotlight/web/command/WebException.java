package org.openspotlight.web.command;

public abstract class WebException extends Exception {

    private WebException() {
        super();
    }

    private WebException(
                          final String message ) {
        super(message);
    }

    private WebException(
                          final String message, final Throwable cause ) {
        super(message, cause);
    }

    private WebException(
                          final Throwable cause ) {
        super(cause);
    }

    public abstract String toJsonString();

}
