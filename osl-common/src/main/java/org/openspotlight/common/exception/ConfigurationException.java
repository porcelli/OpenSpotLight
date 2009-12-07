package org.openspotlight.common.exception;

/**
 * Exception thrown in configuration problems.
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 *
 */
public class ConfigurationException extends SLRuntimeException {

    /**
	 *
	 */
    private static final long serialVersionUID = -4771685926075854224L;

    /**
     * Default inherited constructor.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Default inherited constructor.
     *
     * @param message
     */
    public ConfigurationException(final String message) {
        super(message);
    }

    /**
     * Default inherited constructor.
     *
     * @param message
     * @param cause
     */
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Default inherited constructor.
     *
     * @param cause
     */
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }

}
