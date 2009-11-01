package org.openspotlight.graph;

import java.security.GeneralSecurityException;

public class SLInvalidCredentialsException extends GeneralSecurityException {

    private static final long serialVersionUID = -4771685926075854224L;

    /**
     * Default inherited constructor.
     */
    public SLInvalidCredentialsException() {
        super();
    }

    /**
     * Default inherited constructor.
     * 
     * @param message
     */
    public SLInvalidCredentialsException(
                                        final String message ) {
        super(message);
    }

    /**
     * Default inherited constructor.
     * 
     * @param message
     * @param cause
     */
    public SLInvalidCredentialsException(
                                        final String message, final Throwable cause ) {
        super(message, cause);
    }

    /**
     * Default inherited constructor.
     * 
     * @param cause
     */
    public SLInvalidCredentialsException(
                                        final Throwable cause ) {
        super(cause);
    }

}
