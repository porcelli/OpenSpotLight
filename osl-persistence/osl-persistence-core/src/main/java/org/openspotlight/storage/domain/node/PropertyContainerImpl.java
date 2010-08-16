package org.openspotlight.storage.domain.node;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openspotlight.storage.AbstractStorageSession;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;

import com.google.inject.internal.ImmutableSet;

public abstract class PropertyContainerImpl implements PropertyContainer {

    public void forceReload() {
        propertiesByName.clear();
        lastLoad = -1;
    }

    protected PropertyContainerImpl(final boolean resetTimeout) {
        lastLoad = resetTimeout ? -1 : System.currentTimeMillis();
    }

    private static final long           TIMEOUT          = 60 * 1000;

    protected long                      lastLoad;

    private final Map<String, Property> propertiesByName = new HashMap<String, Property>();

    @Override
    public Set<String> getPropertyNames(final StorageSession session) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.keySet());
    }

    @Override
    public Set<Property> getProperties(final StorageSession session) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.values());
    }

    protected void verifyBeforeSet(final String propertyName) {

    }

    @Override
    public Property setSimpleProperty(final StorageSession session,
                                       final String name,
                                       final String value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    @Override
    public Property setSimpleProperty(final StorageSession session,
                                       final String name,
                                       final InputStream value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStreamValue(session, value);

        return currentProperty;
    }

    @Override
    public Property setSimpleProperty(final StorageSession session,
                                       final String name,
                                       final byte[] value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setBytesValue(session, value);

        return currentProperty;

    }

    @Override
    public Property setIndexedProperty(final StorageSession session,
                                        final String name,
                                        final String value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createIndexed(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    private void reloadProperties(final StorageSession session) {
        final boolean tooOld = lastLoad < (System.currentTimeMillis() + TIMEOUT);
        final boolean empty = propertiesByName.isEmpty();
        if (tooOld && empty) {
            final Set<Property> result =
                ((AbstractStorageSession<?>) session).propertyContainerLoadProperties(this);
            for (final Property property: result) {
                propertiesByName.put(property.getPropertyName(), property);
            }
            lastLoad = System.currentTimeMillis();
        }
    }

    private void loadPropertiesOnce(final StorageSession session) {
        if (propertiesByName.isEmpty()) {
            reloadProperties(session);
        }
    }

    @Override
    public String getPropertyAsString(final StorageSession session,
                                       final String name) {
        final Property prop = getProperty(session, name);
        if (prop != null) { return prop.getValueAsString(session); }
        return null;
    }

    @Override
    public InputStream getPropertyAsStream(final StorageSession session,
                                            final String name) {

        final Property prop = getProperty(session, name);
        if (prop != null) { return prop.getValueAsStream(session); }
        return null;
    }

    @Override
    public byte[] getPropertyAsBytes(final StorageSession session,
                                      final String name) {

        final Property prop = getProperty(session, name);
        if (prop != null) { return prop.getValueAsBytes(session); }
        return null;
    }

    @Override
    public Property getProperty(final StorageSession session,
                                 final String name) {
        loadPropertiesOnce(session);
        Property result = propertiesByName.get(name);
        if (result == null) {
            reloadProperties(session);
            result = propertiesByName.get(name);
        }
        return result;
    }

}
