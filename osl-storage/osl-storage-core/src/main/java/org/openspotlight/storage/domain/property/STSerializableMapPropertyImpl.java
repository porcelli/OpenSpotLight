package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 6:03:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class STSerializableMapPropertyImpl implements STSerializableMapProperty {
    public STSerializableMapPropertyImpl(Class<?> keyType, Class<?> valueType, STNodeEntry parent, String propertyName) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.parent = parent;
        this.propertyName = propertyName;
    }

    public final Class<?> keyType;

    public final Class<?> valueType;

    public final STNodeEntry parent;

    public final String propertyName;

    public <T> Class<T> getKeyType() {
        return (Class<T>) keyType;
    }

    public <K, T> Map<K, T> getMap(STStorageSession session) {
        return session.getInternalMethods().serializableMapGetMap(this);
    }

    public <T> Class<T> getValueType() {
        return (Class<T>) valueType;
    }

    public STNodeEntry getParent() {
        return parent;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STSerializableMapPropertyImpl that = (STSerializableMapPropertyImpl) o;

        if (keyType != null ? !keyType.equals(that.keyType) : that.keyType != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (valueType != null ? !valueType.equals(that.valueType) : that.valueType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = keyType != null ? keyType.hashCode() : 0;
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }
}
