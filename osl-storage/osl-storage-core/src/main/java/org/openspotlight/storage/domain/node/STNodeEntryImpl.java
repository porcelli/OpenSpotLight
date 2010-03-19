package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.property.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:48:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class STNodeEntryImpl implements STNodeEntry {

    public STNodeEntryImpl(final String nodeEntryName, final STLocalKey localKey, final STUniqueKey uniqueKey) {

        this.nodeEntryName = nodeEntryName;
        this.localKey = localKey;
        this.uniqueKey = uniqueKey
                ;
    }

    private final String nodeEntryName;

    private final STLocalKey localKey;

    private final STUniqueKey uniqueKey;

    public String getNodeEntryName() {
        return nodeEntryName;
    }

    public STLocalKey getLocalKey() {
        return localKey;
    }

    public STUniqueKey getUniqueKey() {
        return uniqueKey;
    }

    public STNodeEntryBuilder createWithName(STStorageSession session, String name) {
        return session.getInternalMethods().nodeEntryCreateWithName(this, session, name);

    }

    public List<STListProperty> getListProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetListProperties(this, session);

    }

    public <T> STListProperty setListProperty(STStorageSession session, Class<T> valueType, String name, List<T> value) {
        return session.getInternalMethods().nodeEntrySetListProperty(this, session, valueType, name, value);

    }

    public <T> STListProperty getListProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetListProperty(this, session, valueType, name);

    }

    public List<STSimpleProperty> getSimpleProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSimpleProperties(this, session);

    }

    public <T> STSimpleProperty setSimpleProperty(STStorageSession session, Class<T> type, String name, T value) {
        return session.getInternalMethods().nodeEntrySetSimpleProperty(this, session, type, name, value);

    }

    public <T> STSimpleProperty getSimpleProperty(STStorageSession session, Class<T> type, String name) {
        return session.getInternalMethods().nodeEntryGetSimpleProperty(this, session, type, name);

    }

    public List<STStreamProperty> getStreamProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetStreamProperties(this, session);

    }

    public <T> STStreamProperty setStreamProperty(STStorageSession session, String name, T value) {
        return session.getInternalMethods().nodeEntrySetStreamProperty(this, session, name, value);

    }

    public <T> STStreamProperty getStreamProperty(STStorageSession session, String name) {
        return session.getInternalMethods().nodeEntryGetStreamProperty(this, session, name);

    }

    public List<STMapProperty> getMapProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetMapProperties(this, session);

    }

    public <K, V> STMapProperty setMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
        return session.getInternalMethods().nodeEntrySetMapProperty(this, session, keyType, valueType, name, value);

    }

    public <K, V> STMapProperty getMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetMapProperty(this, session, keyType, valueType, name);

    }

    public List<STSerializableListProperty> getSerializableListProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSerializableListProperties(this, session);

    }

    public <T> STSerializableListProperty setSerializableListProperty(STStorageSession session, Class<T> valueType, String name, List<T> value) {
        return session.getInternalMethods().nodeEntrySetSerializableListProperty(this, session, valueType, name, value);

    }

    public <T> STSerializableListProperty getSerializableListProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSerializableListProperty(this, session, valueType, name);

    }

    public List<STSerializableMapProperty> getSerializableMapProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSerializableMapProperties(this, session);

    }

    public <K, V> STSerializableMapProperty setSerializableMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
        return session.getInternalMethods().nodeEntrySetSerializableMapProperty(this, session, keyType, valueType, name, value);

    }

    public <K, V> STSerializableMapProperty getSerializableMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSerializableMapProperty(this, session, keyType, valueType, name);

    }

    public List<STSerializableSetProperty> getSerializableSetProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSerializableSetProperties(this, session);

    }

    public <T> STSerializableSetProperty setSerializableSetProperty(STStorageSession session, Class<T> valueType, String name, Set<T> value) {
        return session.getInternalMethods().nodeEntrySetSerializableSetProperty(this, session, valueType, name, value);

    }

    public <T> STSerializableSetProperty getSerializableSetProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSerializableSetProperty(this, session, valueType, name);

    }

    public List<STPojoProperty> getPojoProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetPojoProperties(this, session);
    }

    public <T> STPojoProperty setPojoProperty(STStorageSession session, Class<T> type, String name, T value) {
        return session.getInternalMethods().nodeEntrySetPojoProperty(this, session, type, name, value);
    }

    public <T> STPojoProperty getPojoProperty(STStorageSession session, Class<T> type, String name) {
        return session.getInternalMethods().nodeEntryGetPojoProperty(this, session, type, name);
    }

    public List<STSetProperty> getSetProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSetProperties(this, session);
    }

    public <T> STSetProperty setSetProperty(STStorageSession session, Class<T> valueType, String name, Set<T> value) {
        return session.getInternalMethods().nodeEntrySetSetProperty(this, session, valueType, name, value);

    }

    public <T> STSetProperty getSetProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSetProperty(this, session, valueType, name);
    }
}
