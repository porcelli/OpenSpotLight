package org.openspotlight.common.exception;

//LATER_TASK replace with Vitor's implementation
/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 *
 */
public class SLException extends Exception {

    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public SLException() {
        super();
    }

    /**
     * @param message
     */
    public SLException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SLException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public SLException(final Throwable cause) {
        super(cause);
    }

}
