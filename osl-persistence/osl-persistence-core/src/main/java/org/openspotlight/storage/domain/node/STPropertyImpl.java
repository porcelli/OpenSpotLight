package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;

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
    }

    public STPropertyImpl(STNodeEntry parent, String propertyName, STPropertyDescription description,
                          Class<?> propertyType, Class<?> firstParameterizedType) {
        if (propertyName.indexOf(" ") > 0) throw new IllegalArgumentException();
        this.parent = parent;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.firstParameterizedType = firstParameterizedType;
        this.key = STPropertyDescription.KEY.equals(description);
        this.secondParameterizedType = null;
        hasParameterizedTypes = true;
        serialized = description.getSerialized().equals(STPropertyDescription.STSerializedType.SERIALIZED);
        difficultToLoad = description.getLoadWeight().equals(STPropertyDescription.STLoadWeight.DIFFICULT);
        this.description = description;
    }

    public STPropertyImpl(STNodeEntry parent, String propertyName, STPropertyDescription description,
                          Class<?> propertyType) {
        if (propertyName.indexOf(" ") > 0) throw new IllegalArgumentException();
        this.parent = parent;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.key = STPropertyDescription.KEY.equals(description);
        this.firstParameterizedType = null;
        this.secondParameterizedType = null;
        this.hasParameterizedTypes = false;
        serialized = description.getSerialized().equals(STPropertyDescription.STSerializedType.SERIALIZED);
        difficultToLoad = description.getLoadWeight().equals(STPropertyDescription.STLoadWeight.DIFFICULT);
        this.description = description;
    }


    private final STNodeEntry parent;

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
        if (isDifficultToLoad()) {
            weakReference = new WeakReference<T>(value);
        } else {
            this.value = value;
        }
        session.getInternalMethods().propertySetProperty(this, value);
    }

    public <T, R> R getValueAs(STStorageSession session, Class<T> type) {
        R result;
        if (isDifficultToLoad()) {
            result = (R) weakReference != null ? (R) weakReference.get() : null;
            if (result == null) {
                result = (R) session.getInternalMethods().propertyGetPropertyAs(this, type);
                if (result != null) {
                    weakReference = new WeakReference<T>((T) result);
                }
            }
        } else {
            if (value == null) value = (R) session.getInternalMethods().propertyGetPropertyAs(this, type);
            result = (R) value;
        }
        return result;
    }

    public <R> R getValue(STStorageSession session) {
        R result;
        if (isDifficultToLoad()) {
            result = (R) weakReference != null ? (R) weakReference.get() : null;
            if (result == null) {
                result = (R) session.getInternalMethods().propertyGetValue(this);
                if (result != null) {
                    weakReference = new WeakReference<R>((R) result);
                }
            }
        } else {
            if (value == null) value = (R) session.getInternalMethods().propertyGetValue(this);
            result = (R) value;
        }
        return result;

    }

    public <R> R getTransientValue() {
        R result;
        if (isDifficultToLoad()) {
            result = (R) weakReference != null ? (R) weakReference.get() : null;

        } else {
            result = (R) value;
        }
        return result;
    }

    public STNodeEntry getParent() {
        return parent;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isKey() {
        return key;
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
