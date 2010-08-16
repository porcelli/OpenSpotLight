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
        this.propertiesByName.clear();
        this.lastLoad = -1;
    }

    protected PropertyContainerImpl( boolean resetTimeout ) {
        this.lastLoad = resetTimeout ? -1 : System.currentTimeMillis();
    }

    private static final long           TIMEOUT          = 60 * 1000;

    protected long                      lastLoad;

    private final Map<String, Property> propertiesByName = new HashMap<String, Property>();

    public Set<String> getPropertyNames( StorageSession session ) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.keySet());
    }

    public Set<Property> getProperties( StorageSession session ) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.values());
    }

    protected void verifyBeforeSet( String propertyName ) {

    }

    @Override
    public Property setSimpleProperty( StorageSession session,
                                       String name,
                                       String value ) {
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
    public Property setSimpleProperty( StorageSession session,
                                       String name,
                                       InputStream value ) {
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
    public Property setSimpleProperty( StorageSession session,
                                       String name,
                                       byte[] value ) {
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
    public Property setIndexedProperty( StorageSession session,
                                        String name,
                                        String value ) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createIndexed(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    private void reloadProperties( StorageSession session ) {
        boolean tooOld = this.lastLoad < (System.currentTimeMillis() + TIMEOUT);
        boolean empty = propertiesByName.isEmpty();
        if (tooOld && empty) {
            Set<Property> result = ((AbstractStorageSession<?>)session.withPartition(getPartition())).propertyContainerLoadProperties(this);
            for (Property property : result) {
                propertiesByName.put(property.getPropertyName(), property);
            }
            this.lastLoad = System.currentTimeMillis();
        }
    }

    private void loadPropertiesOnce( StorageSession session ) {
        if (propertiesByName.isEmpty()) {
            reloadProperties(session);
        }
    }

    public String getPropertyAsString( StorageSession session,
                                       String name ) {
        Property prop = getProperty(session, name);
        if (prop != null) {
            return prop.getValueAsString(session);
        }
        return null;
    }

    public InputStream getPropertyAsStream( StorageSession session,
                                            String name ) {

        Property prop = getProperty(session, name);
        if (prop != null) {
            return prop.getValueAsStream(session);
        }
        return null;
    }

    public byte[] getPropertyAsBytes( StorageSession session,
                                      String name ) {

        Property prop = getProperty(session, name);
        if (prop != null) {
            return prop.getValueAsBytes(session);
        }
        return null;
    }

    public Property getProperty( StorageSession session,
                                 String name ) {
        loadPropertiesOnce(session);
        Property result = propertiesByName.get(name);
        if (result == null) {
            reloadProperties(session);
            result = propertiesByName.get(name);
        }
        return result;
    }

}
