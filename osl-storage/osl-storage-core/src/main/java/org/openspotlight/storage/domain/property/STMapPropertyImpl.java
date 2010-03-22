package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 5:57:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class STMapPropertyImpl implements STMapProperty {


    public STMapPropertyImpl(Class<?> keyType, Class<?> valueType, String propertyName, STNodeEntry parent) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.propertyName = propertyName;
        this.parent = parent;
    }

    private final Class<?> keyType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STMapPropertyImpl that = (STMapPropertyImpl) o;

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
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }

    private final Class<?> valueType;

    private final String propertyName;

    private final STNodeEntry parent;


    public <T> Class<T> getKeyType() {
        return (Class<T>) keyType;
    }

    public <K, T> Map<K, T> getMap(STStorageSession session) {
        return session.getInternalMethods().mapPropertyGetMap(this);
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
}
