package org.openspotlight.storage;

import com.google.inject.Singleton;
import org.openspotlight.storage.domain.SLPartition;

/**
 * Created by User: feu - Date: Jun 14, 2010 - Time: 3:30:12 PM
 */
@Singleton
public class DefaultSTPartitionFactory implements STPartitionFactory{

    @Override
    public STPartition getPartitionByName(String name) {
        return SLPartition.valueOf(name.toUpperCase());
    }
}
