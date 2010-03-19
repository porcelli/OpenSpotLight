package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:07:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorMapProperty {

    List<STMapProperty> getMapProperties(STStorageSession session);

    <K,V> STMapProperty setMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K,V> value);

    <K,V> STMapProperty getMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name);


}
