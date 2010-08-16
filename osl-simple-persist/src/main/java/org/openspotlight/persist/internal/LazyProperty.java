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
package org.openspotlight.persist.internal;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SerializationUtil;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.key.UniqueKey;

/**
 * This class should wrap any {@link SimpleNodeType} lazy property. This class has a control on few stuff, such as caching value
 * as a {@link WeakReference} or also saving it only when it is needed.
 * <p/>
 * It is thread safe by default, but its wrapped property should be thread safe also if you want to change its internal properties
 * on multiple threads.
 * <p/>
 * It should wrap only {@link Serializable} properties. If you want to store its parent, it is mandatory to extend
 * {@link StreamPropertyWithParent} instead of just put an annotation on it. This is done that way by performance reasons
 * (millions of items on a {@link Collection} became really slow by using reflection).
 * <p/>
 * To newPair new instances of this class, use its internal {@link LazyProperty.Factory} class.
 * <p/>
 * The class {@link LazyProperty.Metadata} should not be used outside
 * 
 * @author feu
 * @param <T>
 */
public final class LazyProperty<T> implements Serializable {

    private final Class<T> type;

    private String         sha1;
    private Node           node;

    /**
     * Factory class.
     * 
     * @author feu
     */
    public static final class Factory {

        /**
         * It creates an empty {@link LazyProperty}. All parameter are mandatory.
         * 
         * @param <T>
         * @param parent
         * @return
         */
        public static <T> LazyProperty<T> create(final Class<? super T> type,
                                                  final SimpleNodeType parent) {
            Assertions.checkNotNull("parent", parent);
            return new LazyProperty<T>(parent, (Class<T>) type);
        }

        private Factory() {}

    }

    /**
     * Internal metadata. Should be used only on {@link org.openspotlight.persist.support.SimplePersistCapable}. This class expose
     * internal information about cached and transient values.
     * 
     * @author feu
     */
    public final class Metadata implements Serializable {

        public void setSavedNode(final Node node) {
            LazyProperty.this.node = node;
        }

        public Class<T> getPropertyType() {
            return type;
        }

        public String getSha1() {
            return sha1;
        }

        private String createSha1(final T content) {
            if (content == null) { return null; }
            try {
                if (content instanceof Serializable) {

                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(content);
                    oos.flush();
                    oos.close();
                    return Sha1.getSha1SignatureEncodedAsBase64(baos.toByteArray());
                } else {
                    final InputStream is = (InputStream) content;
                    return Sha1.getSha1SignatureEncodedAsBase64(is);

                }
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        /**
         * This method should not be used outside {@link org.openspotlight.persist.support.SimplePersistCapable}.
         * 
         * @param sha1
         */
        public void internalSetSha1(final String sha1) {
            LazyProperty.this.sha1 = sha1;
        }

        /**
         *
         */
        private static final long serialVersionUID = -31577246921422934L;

        private Metadata() {}

        /**
         * Return the cached value. If there's no cached value, the session will be used to load a new one. The session isn't
         * mandatory, since the value should be cached, but if it isn't, it will throw a {@link NullPointerException}.
         * 
         * @return
         */
        @SuppressWarnings("unchecked")
        public T getCached(final SimplePersistCapable<Node, StorageSession> simplePersist) {
            try {
                lock.lock();
                final T cachedValue = cached == null ? null : cached.get();
                if (cachedValue == null) {
                    if (simplePersist == null && parentKey == null) { return null; }
                    if (simplePersist == null) { throw Exceptions.logAndReturn(new IllegalStateException(
                        "trying to retrieve a value with a null session")); }
                    Assertions.checkNotNull("parentKey", parentKey);
                    Assertions.checkNotEmpty("propertyName", propertyName);
                    try {
                        if (node == null) {

                            node =
                                simplePersist
                                    .getCurrentSession()
                                    .withPartition(
                                                                                   simplePersist.getCurrentPartition())
                                    .createCriteria()
                                    .withUniqueKey(
                                                                                                                                                       parentKey)
                                    .buildCriteria()
                                    .andFindUnique(
                                                                                                                                                                                                simplePersist
                                                                                                                                                                                                    .getCurrentSession());

                        }
                        final InputStream o = node.getPropertyAsStream(simplePersist.getCurrentSession(), propertyName);
                        final InputStream is = o;
                        if (is != null && is.markSupported()) {
                            is.reset();
                        }
                        final T s = (T) SerializationUtil.deserialize(o);
                        simplePersist.getInternalMethods().beforeUnConvert(parent, (Serializable) s, null);
                        setCached(s);
                        return s;

                    } catch (final Exception e) {
                        throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
                    }
                }
                return cachedValue;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Return the lock used internally.
         * 
         * @return
         */
        public ReentrantLock getLock() {
            return lock;
        }

        /**
         * Return the parent property.
         * 
         * @return
         */
        public SimpleNodeType getParent() {
            return parent;
        }

        public UniqueKey getParentKey() {
            return parentKey;
        }

        public String getPropertyName() {
            return propertyName;
        }

        /**
         * Return the transient value if there's any one setted
         * 
         * @return
         */
        public T getTransient() {
            try {
                lock.lock();
                return transientValue;
            } finally {
                lock.unlock();

            }
        }

        public boolean isBlank() {
            return parentKey == null && transientValue == null;
        }

        public boolean isCacheLoaded() {
            try {
                lock.lock();
                return cached != null && cached.get() != null;
            } finally {
                lock.unlock();
            }
        }

        public boolean isFilled() {
            return !isBlank();
        }

        /**
         * It will clean the transient value and its needs save status.
         */
        public void markAsSaved() {
            try {
                lock.lock();
                needsSave = false;
                LazyProperty.this.transientValue = null;
            } finally {
                lock.unlock();
            }
        }

        public boolean needsSave() {
            try {
                lock.lock();
                return needsSave;
            } finally {
                lock.unlock();

            }
        }

        public void setCached(final T cached) {
            try {
                lock.lock();
                LazyProperty.this.cached = new WeakReference<T>(cached);
            } finally {
                lock.unlock();
            }
        }

        public void setParentKey(final UniqueKey parentKey) {
            LazyProperty.this.parentKey = parentKey;
        }

        public void setPropertyName(final String propertyName) {
            LazyProperty.this.propertyName = propertyName;
        }

    }

    private UniqueKey                  parentKey;

    private String                     propertyName;

    /**
     *
     */
    private static final long          serialVersionUID = 7214615570747274715L;

    private final Metadata             metadata         = new Metadata();

    private final SimpleNodeType       parent;
    private transient WeakReference<T> cached           = null;
    private T                          transientValue   = null;
    private final ReentrantLock        lock             = new ReentrantLock();

    private boolean                    needsSave        = false;

    private LazyProperty(
                          final SimpleNodeType parent, final Class<T> type) {
        this.parent = parent;

        this.type = type;
    }

    /**
     * It will try to return, in this order, the transient value, and if there's no transient value, it will return the saved
     * value.
     * 
     * @return
     */
    public T get(final SimplePersistCapable<Node, StorageSession> simplePersist) {
        try {
            lock.lock();
            T value = metadata.getTransient();
            if (value == null) {
                value = metadata.getCached(simplePersist);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * It will set the transient value and will activate the needsSave flag.
     * 
     * @param newValue
     */
    public void setTransient(final T newValue) {
        try {
            lock.lock();
            this.transientValue = newValue;
            sha1 = getMetadata().createSha1(this.transientValue);
            needsSave = true;
        } finally {
            lock.unlock();
        }
    }

}
