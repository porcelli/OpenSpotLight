package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 5:59:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class STPojoPropertyImpl implements STPojoProperty {
    public STPojoPropertyImpl(Class<?> type, STNodeEntry parent, String propertyName) {
        this.type = type;
        this.parent = parent;
        this.propertyName = propertyName;
    }

    private final Class<?> type;

    private final STNodeEntry parent;

    private final String propertyName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STPojoPropertyImpl that = (STPojoPropertyImpl) o;

        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }

    public <T extends Serializable> Class<T> getType() {
        return (Class<T>) type;
    }

    public <T extends Serializable> T getValue(STStorageSession session) {
        return session.getInternalMethods().<T>pojoPropertyGetValue(this);
    }

    public STNodeEntry getParent() {
        return parent;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
