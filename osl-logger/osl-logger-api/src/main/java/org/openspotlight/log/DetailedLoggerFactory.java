package org.openspotlight.log;

import org.openspotlight.common.Disposable;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DetailedLogger objects.
 */
public interface DetailedLoggerFactory extends Disposable {

    /**
     * Creates a new DetailedLogger object.
     * 
     * @return the detailed logger
     */
    public DetailedLogger createNewLogger();
}
