package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 6:08:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class STStreamPropertyImpl implements STStreamProperty {
    public STStreamPropertyImpl(STNodeEntry parent, String propertyName) {
        this.parent = parent;
        this.propertyName = propertyName;
    }

    private final STNodeEntry parent;

    private final String propertyName;

    public STNodeEntry getParent() {
        return parent;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public InputStream getValue(STStorageSession session) {
        return session.getInternalMethods().streamPropertyGetValue(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STStreamPropertyImpl that = (STStreamPropertyImpl) o;

        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }
}
