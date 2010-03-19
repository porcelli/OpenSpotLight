package org.openspotlight.storage;

import org.openspotlight.storage.domain.node.STANodeEntryFactory;


/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store
 * any kind of connection state. This implementation must not be shared between threads.
 */
public interface STStorageSession extends STANodeEntryFactory {

    /**
     * This method was created to avoid casts to the Persistent storage internal classes.
     * @param <T>
     * @return Internal API classes for internal storage session management.
     */
    public <T> T get();

}
