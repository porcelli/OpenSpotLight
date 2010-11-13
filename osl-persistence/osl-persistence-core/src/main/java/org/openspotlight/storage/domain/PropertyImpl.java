/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.storage.domain;

import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.StorageSessionImpl;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;

/**
 * Internal (default) implementation of {@link Property}. <br>
 * In this implementation the property values are loaded lazy once requested.
 * 
 * @author feuteston
 * @author porcelli
 */
public class PropertyImpl implements Property {

    /**
     * Internal structure that holds property values.<br>
     * This class uses {@link Reference} to optimize memory use.
     * 
     * @author feuteston
     * @author porcelli
     */
    private class PropertyValue {

        private boolean                dirty;

        private boolean                loaded;

        private byte[]                 realValue;

        private Reference<InputStream> weakValueAsStream;

        private Reference<String>      weakValueAsString;

        /**
         * Creates a {@link SoftReference} for input parameter.
         * 
         * @param <T> type of input param
         * @param t input to be enclosed by soft reference
         * @return the soft reference
         */
        private <T> Reference<T> asSoftRef(final T t) {
            return t != null ? new SoftReference<T>(t) : null;
        }

        /**
         * Returns the reference value
         * 
         * @param <T> type of input param
         * @param ref input to extract the value
         * @return value, or null if content were garbage collected
         */
        private <T> T getSoftValue(final Reference<T> ref) {
            return ref != null ? ref.get() : null;
        }

        /**
         * Converts an {@link InputStream} to a byte array
         * 
         * @param is value to be converted
         * @return value converted to byte array
         */
        private byte[] asBytes(final InputStream is) {
            if (is == null) { return null; }
            try {
                if (is.markSupported()) {
                    is.reset();
                }
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtils.copy(is, os);
                return os.toByteArray();
            } catch (final Exception e) {
                throw logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        /**
         * Converts a String to a byte array
         * 
         * @param s value to be converted
         * @return value converted to byte array
         */
        private byte[] asBytes(final String s) {
            return s != null ? s.getBytes() : null;
        }

        /**
         * Converts a byte array to {@link InputStream}
         * 
         * @param b value to be converted
         * @return value converted to input stream
         */
        private InputStream asStream(final byte[] b) {
            return b != null ? new ByteArrayInputStream(b) : null;
        }

        /**
         * Converts a byte array to String
         * 
         * @param b value to be converted
         * @return value converted to string
         */
        private String asString(final byte[] b) {
            return b != null ? new String(b) : null;
        }

        /**
         * Reset internal fields that stores values
         */
        private void nullEverything() {
            weakValueAsString = null;
            weakValueAsStream = null;
            realValue = null;
        }

        /**
         * Returns the property value as byte array.
         * 
         * @return the value as byte array
         */
        public byte[] getValueAsBytes() {
            return realValue;
        }

        /**
         * Returns the property value as {@link InputStream}.
         * 
         * @return the value as input stream
         */
        public InputStream getValueAsStream() {
            InputStream value = getSoftValue(weakValueAsStream);
            if (value == null) {
                value = asStream(realValue);
                weakValueAsStream = asSoftRef(value);
            }
            if (value != null && value.markSupported()) {
                try {
                    value.reset();
                } catch (final Exception e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);
                }
            }
            return value;
        }

        /**
         * Returns the property value as String.
         * 
         * @return the value as string
         */
        public String getValueAsString() {
            String value = getSoftValue(weakValueAsString);
            if (value == null) {
                value = asString(realValue);
                weakValueAsString = asSoftRef(value);
            }
            return value;
        }

        /**
         * Checks if property value is modified.
         * 
         * @return true if changed, false otherwise
         */
        public boolean isDirty() {
            return dirty;
        }

        /**
         * Checks if property value is loaded.
         * 
         * @return true if loaded, false otherwise
         */
        public boolean isLoaded() {
            return loaded;
        }

        /**
         * Sets the dirty
         * 
         * @param dirty true if changed, false otherwise
         */
        public void setDirty(final boolean dirty) {
            this.dirty = dirty;
        }

        /**
         * Sets the loaded.
         * 
         * @param loaded true if loaded, false otherwise
         */
        public void setLoaded(final boolean loaded) {
            this.loaded = loaded;
        }

        /**
         * Sets the property value in String format. Null is an accepted value. <br>
         * 
         * @param value the property value
         */
        public void setValue(final String value) {
            nullEverything();
            realValue = asBytes(value);
            weakValueAsString = asSoftRef(value);
        }

        /**
         * Sets the property value using {@link InputStream} format. Null is an accepted value. <br>
         * 
         * @param value the property value
         */
        public void setValue(final InputStream value) {
            nullEverything();
            realValue = asBytes(value);
        }

        /**
         * Sets the property value in byte array format. Null is an accepted value. <br>
         * 
         * @param value the property value
         */
        public void setValue(final byte[] value) {
            nullEverything();
            realValue = value;
        }

    }

    private final boolean           indexed;

    private final boolean           key;

    private final String            name;

    private final PropertyContainer parent;

    private final PropertyValue     propertyValue = new PropertyValue();

    private PropertyImpl(final String name, final PropertyContainer parent, final boolean indexed, final boolean key) {
        this.name = name;
        this.parent = parent;
        this.indexed = indexed;
        this.key = key;
    }

    /**
     * Factory method that produces a property that is also a key
     * 
     * @param name the property/key name
     * @param parent the property container reference
     * @return a new instance
     */
    public static PropertyImpl createKey(final String name, final PropertyContainer parent) {
        final PropertyImpl property = new PropertyImpl(name, parent, true, true);
        return property;
    }

    /**
     * Factory method that produces an indexed property.
     * 
     * @param name the property name
     * @param parent the property container reference
     * @return a new instance
     */
    public static PropertyImpl createIndexed(final String name, final PropertyContainer parent) {
        final PropertyImpl property = new PropertyImpl(name, parent, true, false);
        return property;
    }

    /**
     * Factory method that produces a regular property.
     * 
     * @param name the property name
     * @param parent the property container reference
     * @return a new instance
     */
    public static PropertyImpl createSimple(final String name, final PropertyContainer parent) {
        final PropertyImpl property = new PropertyImpl(name, parent, false, false);
        return property;
    }

    /**
     * Refresh the property value if its not dirty and not already loaded.
     * 
     * @param session the storage session
     */
    private void refreshPropertyIfNecessary(final StorageSession session) {
        if (!propertyValue.isDirty() && !propertyValue.isLoaded()) {
            propertyValue.setValue(((StorageSessionImpl<?, ?>) session).getPropertyValue(this));
            propertyValue.setLoaded(true);
            propertyValue.setDirty(false);
        }
    }

    /**
     * Checks if property can be setted. <br>
     * For now the unique restrition is that properties that also are keys can't be setted.
     * 
     * @throws IllegalStateException if property can't be setted
     */
    private void verifyBeforeSet()
        throws IllegalStateException {
        if (key) { throw new IllegalStateException(); }
    }

    /**
     * Clean property value, if its not dirty, when the value is bigger than 255 bytes.
     */
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

    /**
     * Returns the transient property value as String. <br>
     * <b>Note:</b> the unique difference of this operation from {@link #getValueAsString(StorageSession)} is that this method
     * does not try to reload the property value.
     * 
     * @return the transient value
     */
    public String getTransientValueAsString() {
        return propertyValue.getValueAsString();
    }

    /**
     * Returns the transient property value as Stream. <br>
     * <b>Note:</b> the unique difference of this operation from {@link #getValueAsStream(StorageSession)} is that this method
     * does not try to reload the property value.
     * 
     * @return the transient value
     */
    public InputStream getTransientValueAsStream() {
        return propertyValue.getValueAsStream();
    }

    /**
     * Returns the transient property value as byte array. <br>
     * <b>Note:</b> the unique difference of this operation from {@link #getValueAsBytes(StorageSession)} is that this method does
     * not try to reload the property value.
     * 
     * @return the transient value
     */
    public byte[] getTransientValueAsBytes() {
        return propertyValue.getValueAsBytes();
    }

    /**
     * Initialize the property value
     * 
     * @param value the value to be loaded
     */
    public void setStringValueOnLoad(final String value)
        throws IllegalArgumentException {
        propertyValue.setValue(value);
        propertyValue.setDirty(false);
        propertyValue.setLoaded(true);

    }

    /**
     * Initialize the property value
     * 
     * @param value the value to be loaded
     * @throws IllegalArgumentException if input param is null
     */
    public void setStreamValueOnLoad(final InputStream value) {
        propertyValue.setValue(value);
        propertyValue.setDirty(false);
        propertyValue.setLoaded(true);
    }

    /**
     * Initialize the property value
     * 
     * @param value the value to be loaded
     */
    public void setBytesValueOnLoad(final byte[] value) {
        propertyValue.setValue(value);
        propertyValue.setDirty(false);
        propertyValue.setLoaded(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyContainer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIndexed() {
        return indexed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueAsString(final StorageSession session)
        throws IllegalArgumentException {
        checkNotNull("session", session);

        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getValueAsStream(final StorageSession session)
        throws IllegalArgumentException {
        checkNotNull("session", session);

        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getValueAsBytes(final StorageSession session)
        throws IllegalArgumentException {
        checkNotNull("session", session);

        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStringValue(final StorageSession session,
                                final String value)
        throws IllegalArgumentException, IllegalStateException {
        checkNotNull("session", session);

        verifyBeforeSet();
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        ((StorageSessionImpl<?, ?>) session).setPropertyValue(this, propertyValue.getValueAsBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStreamValue(final StorageSession session,
                                final InputStream value)
        throws IllegalArgumentException, IllegalStateException {
        checkNotNull("session", session);

        verifyBeforeSet();
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        ((StorageSessionImpl<?, ?>) session).setPropertyValue(this, propertyValue.getValueAsBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBytesValue(final StorageSession session, final byte[] value)
        throws IllegalArgumentException, IllegalStateException {
        checkNotNull("session", session);

        verifyBeforeSet();
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        ((StorageSessionImpl<?, ?>) session).setPropertyValue(this, propertyValue.getValueAsBytes());
    }
}
