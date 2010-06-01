package org.openspotlight.storage.domain.node;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 29/03/2010
 * Time: 08:49:51
 * To change this template use File | Settings | File Templates.
 */
public class STPropertyImpl implements STProperty {

    private final STPropertyInternalMethods propertyInternalMethods = new STPropertyInternalMethodsImpl();

    public static STPropertyImpl createSimple(String name, STNodeEntry parent) {
        STPropertyImpl property = new STPropertyImpl(name, parent.getUniqueKey().getPartition(), parent, false, false);
        return property;
    }

    public static STPropertyImpl createIndexed(String name, STNodeEntry parent) {
        STPropertyImpl property = new STPropertyImpl(name, parent.getUniqueKey().getPartition(), parent, true, false);
        return property;
    }

    public static STPropertyImpl createKey(String name, STNodeEntry parent) {
        STPropertyImpl property = new STPropertyImpl(name, parent.getUniqueKey().getPartition(), parent, true, true);
        return property;
    }


    private STPropertyImpl(String name, STPartition partition, STNodeEntry parent, boolean indexed, boolean key) {
        this.name = name;
        this.partition = partition;
        this.parent = parent;
        this.indexed = indexed;
        this.key = key;
    }

    private final String name;

    public STNodeEntry getParent() {
        return parent;
    }

    private final PropertyValue propertyValue = new PropertyValue();

    private final STPartition partition;

    private final STNodeEntry parent;

    public boolean isKey() {
        return key;
    }

    public boolean isIndexed() {
        return indexed;
    }

    private final boolean indexed;

    private final boolean key;

    @Override
    public void setStringValue(STStorageSession session, String value) {
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        session.withPartition(partition).getInternalMethods().propertySetProperty(this, propertyValue.getValueAsBytes());
    }

    @Override
    public void setBytesValue(STStorageSession session, byte[] value) {
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        session.withPartition(partition).getInternalMethods().propertySetProperty(this, propertyValue.getValueAsBytes());
    }

    @Override
    public void setStreamValue(STStorageSession session, InputStream value) {
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        session.withPartition(partition).getInternalMethods().propertySetProperty(this, propertyValue.getValueAsBytes());
    }

    private void refreshPropertyIfNecessary(STStorageSession session) {
        if (!propertyValue.isDirty() && !propertyValue.isLoaded()) {
            propertyValue.setValue(session.withPartition(partition).getInternalMethods().propertyGetValue(this));
            propertyValue.setLoaded(true);
            propertyValue.setDirty(false);
        }

    }

    @Override
    public String getValueAsString(STStorageSession session) {
        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsString();
    }

    @Override
    public byte[] getValueAsBytes(STStorageSession session) {
        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsBytes();
    }

    @Override
    public InputStream getValueAsStream(STStorageSession session) {
        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsStream();
    }

    @Override
    public String getPropertyName() {
        return name;
    }

    public STPropertyInternalMethods getInternalMethods() {
        return propertyInternalMethods;
    }

    private class STPropertyInternalMethodsImpl implements STPropertyInternalMethods {

        @Override
        public void setStringValueOnLoad(STStorageSession session, String value) {
            propertyValue.setValue(value);
            propertyValue.setDirty(false);
            propertyValue.setLoaded(true);

        }

        @Override
        public void setBytesValueOnLoad(STStorageSession session, byte[] value) {
            propertyValue.setValue(value);
            propertyValue.setDirty(false);
            propertyValue.setLoaded(true);
        }

        @Override
        public void setStreamValueOnLoad(STStorageSession session, InputStream value) {
            propertyValue.setValue(value);
            propertyValue.setDirty(false);
            propertyValue.setLoaded(true);
        }

        @Override
        public void removeTransientValueIfExpensive() {
            if (!key && !propertyValue.isDirty() && propertyValue.isLoaded()) {
                if (propertyValue.getValueAsBytes() != null) {
                    if (propertyValue.getValueAsBytes().length > 255) {
                        propertyValue.setValue((byte[]) null);
                        propertyValue.setLoaded(false);
                    }
                }
            }
        }

        @Override
        public String getTransientValueAsString(STStorageSession session) {
            return propertyValue.getValueAsString();
        }

        @Override
        public byte[] getTransientValueAsBytes(STStorageSession session) {
            return propertyValue.getValueAsBytes();
        }

        @Override
        public InputStream getTransientValueAsStream(STStorageSession session) {
            return propertyValue.getValueAsStream();
        }
    }


    private class PropertyValue {

        private boolean dirty;

        private boolean loaded;

        public boolean isLoaded() {
            return loaded;
        }

        public void setLoaded(boolean loaded) {
            this.loaded = loaded;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        private byte[] asBytes(String s) {
            return s != null ? s.getBytes() : null;
        }

        private <T> T getWeakValue(Reference<T> ref) {
            return ref != null ? ref.get() : null;
        }

        private String asString(byte[] b) {
            return b != null ? new String(b) : null;
        }

        private InputStream asStream(byte[] b) {
            return b != null ? new ByteArrayInputStream(b) : null;
        }

        private <T> Reference<T> asWeakRef(T t) {
            return t != null ? new SoftReference<T>(t) : null;
        }

        private byte[] asBytes(InputStream is) {
            if (is == null) return null;
            try {
                if (is.markSupported()) {
                    is.reset();
                }
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtils.copy(is, os);
                return os.toByteArray();
            } catch (Exception e) {
                throw logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        private Reference<String> weakValueAsString;
        private Reference<InputStream> weakValueAsStream;
        private byte[] realValue;

        private void nullEverything() {
            weakValueAsString = null;
            weakValueAsStream = null;
            realValue = null;
        }


        public void setValue(String value) {
            nullEverything();
            this.realValue = asBytes(value);
            this.weakValueAsString = asWeakRef(value);
        }

        public void setValue(InputStream value) {
            nullEverything();
            this.realValue = asBytes(value);
        }

        public void setValue(byte[] value) {
            nullEverything();
            this.realValue = value;
        }

        public String getValueAsString() {
            String value = getWeakValue(this.weakValueAsString);
            if (value == null) {
                value = asString(this.realValue);
                this.weakValueAsString = asWeakRef(value);
            }
            return value;
        }

        public InputStream getValueAsStream() {
            InputStream value = getWeakValue(this.weakValueAsStream);
            if (value == null) {
                value = asStream(this.realValue);
                this.weakValueAsStream = asWeakRef(value);
            }
            if (value != null && value.markSupported()) {
                try {
                    value.reset();
                } catch (Exception e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);
                }
            }
            return value;
        }

        public byte[] getValueAsBytes
                () {
            return this.realValue;
        }

    }

}
