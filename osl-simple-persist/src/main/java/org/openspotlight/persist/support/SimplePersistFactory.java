package org.openspotlight.persist.support;

import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;

/**
 * Created by User: feu - Date: Apr 20, 2010 - Time: 9:56:03 AM
 */
public interface SimplePersistFactory {
    SimplePersistCapable<STNodeEntry,STStorageSession> createSimplePersist(STPartition partition);
}
