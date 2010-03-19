package org.openspotlight.storage;

import org.openspotlight.storage.domain.node.STANodeEntryFactory;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.property.*;

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

        <T> STSetProperty nodeEntryGetSetProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name);

        <T> STSetProperty nodeEntrySetSetProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name, Set<T> value);

        List<STSetProperty> nodeEntryGetSetProperties(STNodeEntry stNodeEntry, STStorageSession session);

        STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, STStorageSession session, String name);

        List<STListProperty> nodeEntryGetListProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <T> STListProperty nodeEntrySetListProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name, List<T> value);

        <T> STListProperty nodeEntryGetListProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name);

        List<STSimpleProperty> nodeEntryGetSimpleProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <T> STSimpleProperty nodeEntrySetSimpleProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> type, String name, T value);

        <T> STSimpleProperty nodeEntryGetSimpleProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> type, String name);

        List<STStreamProperty> nodeEntryGetStreamProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <T> STStreamProperty nodeEntrySetStreamProperty(STNodeEntry stNodeEntry, STStorageSession session, String name, T value);

        STStreamProperty nodeEntryGetStreamProperty(STNodeEntry stNodeEntry, STStorageSession session, String name);

        List<STMapProperty> nodeEntryGetMapProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <K, V> STMapProperty nodeEntrySetMapProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value);

        <K, V> STMapProperty nodeEntryGetMapProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<K> keyType, Class<V> valueType, String name);

        List<STSerializableListProperty> nodeEntryGetSerializableListProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <T> STSerializableListProperty nodeEntrySetSerializableListProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name, List<T> value);

        <T> STSerializableListProperty nodeEntryGetSerializableListProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name);

        List<STSerializableMapProperty> nodeEntryGetSerializableMapProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <K, V> STSerializableMapProperty nodeEntrySetSerializableMapProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value);

        <K, V> STSerializableMapProperty nodeEntryGetSerializableMapProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<K> keyType, Class<V> valueType, String name);

        List<STSerializableSetProperty> nodeEntryGetSerializableSetProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <T> STSerializableSetProperty nodeEntrySetSerializableSetProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name, Set<T> value);

        <T> STSerializableSetProperty nodeEntryGetSerializableSetProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> valueType, String name);

        List<STPojoProperty> nodeEntryGetPojoProperties(STNodeEntry stNodeEntry, STStorageSession session);

        <T> STPojoProperty nodeEntrySetPojoProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> type, String name, T value);

        <T> STPojoProperty nodeEntryGetPojoProperty(STNodeEntry stNodeEntry, STStorageSession session, Class<T> type, String name);
    }
}
