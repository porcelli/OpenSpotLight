package org.openspotlight.common.exception;

/**
 * Exception thrown in configuration problems
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class ConfigurationException extends SLException {
    
    public ConfigurationException() {
        super();
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
    
    /**
	 * 
	 */
    private static final long serialVersionUID = -4771685926075854224L;
    
}
