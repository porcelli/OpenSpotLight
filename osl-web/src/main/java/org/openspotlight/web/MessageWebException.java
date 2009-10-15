package org.openspotlight.web;

/**
 * The Class MessageWebException.
 */
public class MessageWebException extends WebException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7353002152865337000L;

    /**
     * Instantiates a new message web exception.
     * 
     * @param message the message
     */
    public MessageWebException(
                                final String message ) {
        super(message);
    }

    /**
     * Instantiates a new message web exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public MessageWebException(
                                final String message, final Throwable cause ) {
        super(message, cause);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.web.WebException#toJsonString()
     */
    @Override
    public String toJsonString() {
        return "{error:'" + this.getMessage() + "'}";
    }

}
