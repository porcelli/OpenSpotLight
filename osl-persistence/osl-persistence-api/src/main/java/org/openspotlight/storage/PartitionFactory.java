package org.openspotlight.storage;

/**
 * Created by User: feu - Date: Jun 14, 2010 - Time: 3:25:37 PM
 */
public interface PartitionFactory {

    Partition getPartitionByName(String name);

    Partition[] getValues();
}
