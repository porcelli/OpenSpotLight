/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.storage.domain.node;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.AbstractStorageSession;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;

/**
 * Created by IntelliJ IDEA. User: feuteston Date: 29/03/2010 Time: 08:49:51 To change this template use File | Settings | File
 * Templates.
 */
public class PropertyImpl implements Property {

    public static PropertyImpl createSimple(final String name,
                                               final PropertyContainer parent) {
        final PropertyImpl property = new PropertyImpl(name, parent, false, false);
        return property;
    }

    public static PropertyImpl createIndexed(final String name,
                                                final PropertyContainer parent) {
        final PropertyImpl property = new PropertyImpl(name, parent, true, false);
        return property;
    }

    public static PropertyImpl createKey(final String name,
                                            final PropertyContainer parent) {
        final PropertyImpl property = new PropertyImpl(name, parent, true, true);
        return property;
    }

    private PropertyImpl(final String name, final PropertyContainer parent, final boolean indexed, final boolean key) {
        this.name = name;
        this.parent = parent;
        this.indexed = indexed;
        this.key = key;
    }

    private final String name;

    @Override
    public PropertyContainer getParent() {
        return parent;
    }

    private final PropertyValue     propertyValue = new PropertyValue();

    private final PropertyContainer parent;

    @Override
    public boolean isKey() {
        return key;
    }

    @Override
    public boolean isIndexed() {
        return indexed;
    }

    private final boolean indexed;

    private final boolean key;

    @Override
    public void setStringValue(final StorageSession session,
                                final String value) {
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        ((AbstractStorageSession<?>) session).propertySetProperty(this, propertyValue.getValueAsBytes());
    }

    @Override
    public void setBytesValue(final StorageSession session,
                               final byte[] value) {
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        ((AbstractStorageSession<?>) session).propertySetProperty(this, propertyValue.getValueAsBytes());
    }

    @Override
    public void setStreamValue(final StorageSession session,
                                final InputStream value) {
        propertyValue.setDirty(true);
        propertyValue.setValue(value);
        ((AbstractStorageSession<?>) session).propertySetProperty(this, propertyValue.getValueAsBytes());
    }

    private void refreshPropertyIfNecessary(final StorageSession session) {
        if (!propertyValue.isDirty() && !propertyValue.isLoaded()) {
            propertyValue.setValue(((AbstractStorageSession<?>) session).propertyGetValue(this));
            propertyValue.setLoaded(true);
            propertyValue.setDirty(false);
        }

    }

    @Override
    public String getValueAsString(final StorageSession session) {
        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsString();
    }

    @Override
    public byte[] getValueAsBytes(final StorageSession session) {
        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsBytes();
    }

    @Override
    public InputStream getValueAsStream(final StorageSession session) {
        refreshPropertyIfNecessary(session);
        return propertyValue.getValueAsStream();
    }

    @Override
    public String getPropertyName() {
        return name;
    }

    public void setStringValueOnLoad(final StorageSession session,
                                      final String value) {
        propertyValue.setValue(value);
        propertyValue.setDirty(false);
        propertyValue.setLoaded(true);

    }

    public void setBytesValueOnLoad(final StorageSession session,
                                     final byte[] value) {
        propertyValue.setValue(value);
        propertyValue.setDirty(false);
        propertyValue.setLoaded(true);
    }

    public void setStreamValueOnLoad(final StorageSession session,
                                      final InputStream value) {
        propertyValue.setValue(value);
        propertyValue.setDirty(false);
        propertyValue.setLoaded(true);
    }

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

    public String getTransientValueAsString(final StorageSession session) {
        return propertyValue.getValueAsString();
    }

    public byte[] getTransientValueAsBytes(final StorageSession session) {
        return propertyValue.getValueAsBytes();
    }

    public InputStream getTransientValueAsStream(final StorageSession session) {
        return propertyValue.getValueAsStream();
    }

    private class PropertyValue {

        private boolean dirty;

        private boolean loaded;

        public boolean isLoaded() {
            return loaded;
        }

        public void setLoaded(final boolean loaded) {
            this.loaded = loaded;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(final boolean dirty) {
            this.dirty = dirty;
        }

        private byte[] asBytes(final String s) {
            return s != null ? s.getBytes() : null;
        }

        private <T> T getWeakValue(final Reference<T> ref) {
            return ref != null ? ref.get() : null;
        }

        private String asString(final byte[] b) {
            return b != null ? new String(b) : null;
        }

        private InputStream asStream(final byte[] b) {
            return b != null ? new ByteArrayInputStream(b) : null;
        }

        private <T> Reference<T> asWeakRef(final T t) {
            return t != null ? new SoftReference<T>(t) : null;
        }

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

        private Reference<String>      weakValueAsString;
        private Reference<InputStream> weakValueAsStream;
        private byte[]                 realValue;

        private void nullEverything() {
            weakValueAsString = null;
            weakValueAsStream = null;
            realValue = null;
        }

        public void setValue(final String value) {
            nullEverything();
            realValue = asBytes(value);
            weakValueAsString = asWeakRef(value);
        }

        public void setValue(final InputStream value) {
            nullEverything();
            realValue = asBytes(value);
        }

        public void setValue(final byte[] value) {
            nullEverything();
            realValue = value;
        }

        public String getValueAsString() {
            String value = getWeakValue(weakValueAsString);
            if (value == null) {
                value = asString(realValue);
                weakValueAsString = asWeakRef(value);
            }
            return value;
        }

        public InputStream getValueAsStream() {
            InputStream value = getWeakValue(weakValueAsStream);
            if (value == null) {
                value = asStream(realValue);
                weakValueAsStream = asWeakRef(value);
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

        public byte[] getValueAsBytes() {
            return realValue;
        }

    }

}
