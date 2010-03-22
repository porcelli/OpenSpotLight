package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 6:02:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class STSerializableListPropertyImpl implements STSerializableListProperty {

    public STSerializableListPropertyImpl(Class<?> valueType, STNodeEntry parent, String propertyName) {
        this.valueType = valueType;
        this.parent = parent;
        this.propertyName = propertyName;
    }

    public <T> List<T> getItems(STStorageSession session) {
        return session.getInternalMethods().serializableListPropertyGetItems(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STSerializableListPropertyImpl that = (STSerializableListPropertyImpl) o;

        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (valueType != null ? !valueType.equals(that.valueType) : that.valueType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = valueType != null ? valueType.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }

    private final Class<?> valueType;

    private final STNodeEntry parent;

    private final String propertyName;

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
