package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:08:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorSerializableListProperty {
    List<STSerializableListProperty> getSerializableListProperties(STStorageSession session);

    <T> STSerializableListProperty setSerializableListProperty(STStorageSession session, Class<T> valueType, String name, List<T> value);

    <T> STSerializableListProperty getSerializableListProperty(STStorageSession session, Class<T> valueType, String name);

    
}
