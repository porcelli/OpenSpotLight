package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 29/03/2010
 * Time: 08:49:51
 * To change this template use File | Settings | File Templates.
 */
public class STPropertyImpl implements STProperty {
    public STPropertyImpl(STNodeEntry parent, String propertyName, STPropertyDescription description,
                          Class<?> propertyType, Class<?> firstParameterizedType,
                          Class<?> secondParameterizedType) {
        if (propertyName.indexOf(" ") > 0) throw new IllegalArgumentException();
        if (description == null) throw new IllegalArgumentException();
        this.parent = parent;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.firstParameterizedType = firstParameterizedType;
        this.secondParameterizedType = secondParameterizedType;
        this.key = STPropertyDescription.KEY.equals(description);
        hasParameterizedTypes = true;
        serialized = description.getSerialized().equals(STPropertyDescription.STSerializedType.SERIALIZED);
        difficultToLoad = description.getLoadWeight().equals(STPropertyDescription.STLoadWeight.DIFFICULT);
        this.description = description;

        this.partition = parent.getUniqueKey().getPartition();
    }

    public STPropertyImpl(STNodeEntry parent, String propertyName, STPropertyDescription description,
                          Class<?> propertyType, Class<?> firstParameterizedType) {
        this(parent, propertyName, description, propertyType, firstParameterizedType, null);
    }

    public STPropertyImpl(STNodeEntry parent, String propertyName, STPropertyDescription description,
                          Class<?> propertyType) {
        this(parent, propertyName, description, propertyType, null, null);


    }

    private final STPropertyInternalMethods propertyInternalMethods = new STPropertyInternalMethodsImpl();

    private final STNodeEntry parent;

    private final STPartition partition;

    private final String propertyName;

    private final Class<?> propertyType;

    private final Class<?> firstParameterizedType;

    private final Class<?> secondParameterizedType;

    private final boolean hasParameterizedTypes;

    private final boolean serialized;

    private final boolean difficultToLoad;

    private final boolean key;

    private final STPropertyDescription description;

    private WeakReference<?> weakReference;

    private Object value;

    public <T> void setValue(STStorageSession session, T value) {
        if (key) throw new IllegalStateException("key properties are immutable");
        this.value = value;
        session.withPartition(partition).getInternalMethods().propertySetProperty(this, value);
    }

    public <R> R getValueAs(STStorageSession session, Class<R> type) {
        R result;
        if (value != null)
            return (R) value;
        if (getInternalMethods().isDifficultToLoad()) {
            result = (R) weakReference != null ? (R) weakReference.get() : null;
            if (result == null) {
                result = (R) session.withPartition(partition).getInternalMethods().propertyGetPropertyAs(this, type);
                if (result != null) {
                    weakReference = new WeakReference<R>((R) result);
                }
            }
        } else {
            value = session.withPartition(partition).getInternalMethods().propertyGetPropertyAs(this, type);
            result = (R) value;
        }
        resetIfStream(result);

        return result;
    }

    private <R> void resetIfStream(R result) {
        try {
            if (result instanceof InputStream) {
                InputStream is = (InputStream) result;
                if (is.markSupported()) is.reset();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public STNodeEntry getParent() {
        return parent;
    }


    public <R> R getValue(STStorageSession session) {
        R result;
        if (getInternalMethods().isDifficultToLoad()) {
            result = (R) weakReference != null ? (R) weakReference.get() : null;
            if (result == null) {
                result = (R) session.withPartition(partition).getInternalMethods().propertyGetValue(this);
                if (result != null) {
                    weakReference = new WeakReference<R>((R) result);
                }
            }
        } else {
            if (value == null) value = (R) session.withPartition(partition).getInternalMethods().propertyGetValue(this);
            result = (R) value;
        }
        resetIfStream(result);

        return result;

    }

    public String getPropertyName() {
        return propertyName;
    }

    public STPropertyInternalMethods getInternalMethods() {
        return propertyInternalMethods;
    }

    private class STPropertyInternalMethodsImpl implements STPropertyInternalMethods {

        public <R> R getTransientValue() {
            return (R) value;
        }


        public boolean isKey() {
            return key;
        }

        public <T> void setValueOnLoad(T value) {
            STPropertyImpl.this.value = value;
        }

        public void removeTransientValueIfExpensive() {
            if (difficultToLoad) value = null;
        }

        public <T> Class<T> getPropertyType() {
            return (Class<T>) propertyType;
        }

        public <T> Class<T> getFirstParameterizedType() {
            return (Class<T>) firstParameterizedType;
        }

        public <T> Class<T> getSecondParameterizedType() {
            return (Class<T>) secondParameterizedType;
        }

        public boolean hasParameterizedTypes() {
            return hasParameterizedTypes;
        }

        public boolean isSerialized() {
            return serialized;
        }

        public boolean isDifficultToLoad() {
            return difficultToLoad;
        }

        public STPropertyDescription getDescription() {
            return description;
        }
    }

}
