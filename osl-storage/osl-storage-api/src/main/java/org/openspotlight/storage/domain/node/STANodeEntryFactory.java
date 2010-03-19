package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 3:10:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STANodeEntryFactory {

    STNodeEntryBuilder createWithName(STStorageSession session, String name);

    interface STNodeEntryBuilder{
        <T> STNodeEntryBuilder withKey(String name, Class<T> type, T value);

        STNodeEntry andCreate();
    }

}