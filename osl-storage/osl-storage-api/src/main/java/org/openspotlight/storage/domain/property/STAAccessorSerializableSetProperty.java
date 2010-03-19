package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorSerializableSetProperty {
    
    List<STSerializableSetProperty> getSerializableSetProperties(STStorageSession session);

    <T> STSerializableSetProperty setSerializableSetProperty(STStorageSession session, Class<T> valueType, String name, Set<T> value);

    <T> STSerializableSetProperty getSerializableSetProperty(STStorageSession session, Class<T> valueType, String name);
    
}
