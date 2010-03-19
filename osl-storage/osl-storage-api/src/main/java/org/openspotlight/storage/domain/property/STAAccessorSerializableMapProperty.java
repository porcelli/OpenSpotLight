package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:09:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorSerializableMapProperty {


    List<STSerializableMapProperty> getSerializableMapProperties(STStorageSession session);

    <K,V> STSerializableMapProperty setSerializableMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K,V> value);

    <K,V> STSerializableMapProperty getSerializableMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name);
    
}
