package org.openspotlight.common.exception;

//FIXME replace with Vitor's implementation 
/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class SLRuntimeException extends RuntimeException {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    
    /**
     * 
     */
    public SLRuntimeException() {
        super();
        
    }
    
    /**
     * @param message
     */
    public SLRuntimeException(final String message) {
        super(message);
        
    }
    
    /**
     * @param message
     * @param cause
     */
    public SLRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
        
    }
    
    /**
     * @param cause
     */
    public SLRuntimeException(final Throwable cause) {
        super(cause);
        
    }
    
}
