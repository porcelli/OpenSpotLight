package org.openspotlight.common;

/**
 * This interface should be used for anything with needs to close resources before shutting down.
 */
public interface Disposable {

    /**
     * Close resources.
     */
    public void closeResources();

}
