package org.openspotlight.storage;

import org.openspotlight.storage.domain.node.STANodeEntryFactory;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.property.*;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store
 * any kind of connection state. This implementation must not be shared between threads.
 */
public interface STStorageSession extends STANodeEntryFactory {

    /**
     * This method was created to avoid casts to the Persistent storage internal classes.
     *
     * @param <T>
     * @return Internal API classes for internal storage session management.
     */
    public <T> T get();

    STNodeEntryBuilder createWithName(String name);

    STStorageSessionInternalMethods getInternalMethods();

    interface STStorageSessionInternalMethods {

        <T> STSetProperty nodeEntryGetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        <T> STSetProperty nodeEntrySetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value);

        List<STSetProperty> nodeEntryGetSetProperties(STNodeEntry stNodeEntry);

        STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name);

        List<STListProperty> nodeEntryGetListProperties(STNodeEntry stNodeEntry);

        <T> STListProperty nodeEntrySetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value);

        <T> STListProperty nodeEntryGetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        List<STSimpleProperty> nodeEntryGetSimpleProperties(STNodeEntry stNodeEntry);

        <T> STSimpleProperty nodeEntrySetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value);

        <T> STSimpleProperty nodeEntryGetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name);

        List<STStreamProperty> nodeEntryGetStreamProperties(STNodeEntry stNodeEntry);

        <T> STStreamProperty nodeEntrySetStreamProperty(STNodeEntry stNodeEntry, String name, T value);

        STStreamProperty nodeEntryGetStreamProperty(STNodeEntry stNodeEntry, String name);

        List<STMapProperty> nodeEntryGetMapProperties(STNodeEntry stNodeEntry);

        <K, V> STMapProperty nodeEntrySetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value);

        <K, V> STMapProperty nodeEntryGetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name);

        List<STSerializableListProperty> nodeEntryGetSerializableListProperties(STNodeEntry stNodeEntry);

        <T> STSerializableListProperty nodeEntrySetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value);

        <T> STSerializableListProperty nodeEntryGetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        List<STSerializableMapProperty> nodeEntryGetSerializableMapProperties(STNodeEntry stNodeEntry);

        <K, V> STSerializableMapProperty nodeEntrySetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value);

        <K, V> STSerializableMapProperty nodeEntryGetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name);

        List<STSerializableSetProperty> nodeEntryGetSerializableSetProperties(STNodeEntry stNodeEntry);

        <T> STSerializableSetProperty nodeEntrySetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value);

        <T> STSerializableSetProperty nodeEntryGetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        List<STPojoProperty> nodeEntryGetPojoProperties(STNodeEntry stNodeEntry);

        <T> STPojoProperty nodeEntrySetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value);

        <T> STPojoProperty nodeEntryGetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name);

        <T> List<T> listPropertyGetItems(org.openspotlight.storage.domain.property.STListProperty stListProperty);

        <T> Set<T> setPropertyGetItems(org.openspotlight.storage.domain.property.STSetProperty stSetProperty);

        <K, T> Map<K, T> mapPropertyGetMap(org.openspotlight.storage.domain.property.STMapProperty stMapProperty);

        <T> List<T> serializableListPropertyGetItems(org.openspotlight.storage.domain.property.STSerializableListProperty stSerializableListProperty);

        InputStream streamPropertyGetValue(org.openspotlight.storage.domain.property.STStreamProperty stStreamProperty);

        <T extends Serializable> T pojoPropertyGetValue(org.openspotlight.storage.domain.property.STPojoProperty stPojoProperty);

        <T> Set<T> serializableSetPropertyGetItems(org.openspotlight.storage.domain.property.STSerializableSetProperty stSerializableSetProperty);

        <K,T> Map<K,T> serializableMapGetMap(org.openspotlight.storage.domain.property.STSerializableMapProperty stSerializableMapProperty);

        <T extends Serializable> T simplePropertyGetValue(org.openspotlight.storage.domain.property.STSimpleProperty stSimpleProperty);
    }
}
