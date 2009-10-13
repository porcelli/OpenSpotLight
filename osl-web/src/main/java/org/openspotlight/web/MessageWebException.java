package org.openspotlight.web;

public class MessageWebException extends WebException {

    /**
     * 
     */
    private static final long serialVersionUID = 7353002152865337000L;

    public MessageWebException(
                                final String message ) {
        super(message);
    }

    public MessageWebException(
                                final String message, final Throwable cause ) {
        super(message, cause);
    }

    @Override
    public String toJsonString() {
        return "{error:'" + this.getMessage() + "'}";
    }

}
