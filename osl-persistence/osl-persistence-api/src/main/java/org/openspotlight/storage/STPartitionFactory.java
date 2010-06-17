package org.openspotlight.storage;

import org.openspotlight.storage.domain.SLPartition;

/**
 * Created by User: feu - Date: Jun 14, 2010 - Time: 3:25:37 PM
 */
public interface STPartitionFactory {

    STPartition getPartitionByName(String name);
    STPartition[] getValues();
}
