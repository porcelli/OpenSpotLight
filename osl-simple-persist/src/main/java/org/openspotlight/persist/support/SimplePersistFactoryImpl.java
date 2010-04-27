package org.openspotlight.persist.support;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

/**
 * Created by User: feu - Date: Apr 20, 2010 - Time: 9:58:43 AM
 */
@Singleton
public class SimplePersistFactoryImpl implements SimplePersistFactory{

    private final Provider<STStorageSession> sessionProvider;

    @Inject
    public SimplePersistFactoryImpl(Provider<STStorageSession> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public SimplePersistCapable<STNodeEntry,STStorageSession> createSimplePersist(STPartition partition) {
        return new SimplePersistImpl(sessionProvider.get(),partition);
    }
}
