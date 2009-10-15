package org.openspotlight.federation.data;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.federation.data.load.ConfigurationManager;

/**
 * This exception is thrown by the {@link ConfigurationManager} instances. This exception describes a load invocation before
 * saving any configuration.
 */
public class NoConfigurationYetException extends SLException {

    /**
     * 
     */
    private static final long serialVersionUID = -8016348970089541307L;

    /**
     * Instantiates a new no configuration yet exception.
     */
    public NoConfigurationYetException() {
    }

    /**
     * Instantiates a new no configuration yet exception.
     * 
     * @param message the message
     */
    public NoConfigurationYetException(
                                        final String message ) {
        super(message);
    }

    /**
     * Instantiates a new no configuration yet exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public NoConfigurationYetException(
                                        final String message, final Throwable cause ) {
        super(message, cause);
    }

    /**
     * Instantiates a new no configuration yet exception.
     * 
     * @param cause the cause
     */
    public NoConfigurationYetException(
                                        final Throwable cause ) {
        super(cause);
    }

}
