package org.openspotlight.persist.internal;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Session;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistSupport;

/**
 * This class should wrap any {@link SimpleNodeType} lazy property. This class
 * has a control on few stuff, such as caching value as a {@link WeakReference}
 * or also saving it only when it is needed.
 * 
 * It is thread safe by default, but its wrapped property should be thread safe
 * also if you want to change its internal properties on multiple threads.
 * 
 * It should wrap only {@link Serializable} properties. If you want to store its
 * parent, it is mandatory to extend {@link StreamPropertyWithParent} instead of
 * just put an annotation on it. This is done that way by performance reasons
 * (millions of items on a {@link Collection} became really slow by using
 * reflection).
 * 
 * To create new instances of this class, use its internal
 * {@link LazyProperty.Factory} class.
 * 
 * The class {@link LazyProperty.Metadata} should not be used outside
 * {@link SimplePersistSupport}.
 * 
 * @author feu
 * 
 * @param <T>
 */
public final class LazyProperty<T> implements Serializable {

	/**
	 * Factory class.
	 * 
	 * @author feu
	 * 
	 */
	public static final class Factory {

		/**
		 * It creates an empty {@link LazyProperty}. All parameter are
		 * mandatory.
		 * 
		 * @param <T>
		 * @param propertyType
		 * @param parent
		 * @return
		 */
		public static <T> LazyProperty<T> create(final Class<T> propertyType,
				final SimpleNodeType parent) {
			Assertions.checkNotNull("propertyType", propertyType);
			Assertions.checkNotNull("parent", parent);
			return new LazyProperty<T>(propertyType, parent);
		}

		private Factory() {
		}

	}

	/**
	 * Internal metadata. Should be used only on {@link SimplePersistSupport}.
	 * This class expose internal information about cached and transient values.
	 * 
	 * @author feu
	 * 
	 */
	public final class Metadata implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -31577246921422934L;

		private Metadata() {
		}

		/**
		 * Return the cached value. If there's no cached value, the session will
		 * be used to load a new one. The session isn't mandatory, since the
		 * value should be cached, but if it isn't, it will throw a
		 * {@link NullPointerException}.
		 * 
		 * @param session
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public T getCached(final Session session) {
			try {
				lock.lock();
				final T cachedValue = cached == null ? null : cached.get();
				if (cachedValue == null) {
					if (session == null) {
						throw Exceptions
						.logAndReturn(new IllegalStateException(
								"trying to retrieve a value with a null session"));
					}
					Assertions.checkNotEmpty("parentUuid", parentUuid);
					Assertions.checkNotEmpty("propertyName", propertyName);
					try {
						final Node node = session.getNodeByUUID(parentUuid);
						final String jcrPropertyName = MessageFormat.format(
								SimplePersistSupport.LAZY_PROPERTY_VALUE,
								propertyName);
						try {
							final Property property = node
							.getProperty(jcrPropertyName);
							final InputStream is = property.getStream();

							if (is.markSupported()) {
								is.reset();
							}
							final ObjectInputStream ois = new ObjectInputStream(
									is);
							final Serializable serializable = (Serializable) ois
							.readObject();
							SimplePersistSupport.InternalMethods
							.setParentPropertyOnSerializable(
									serializable, parent);
							setCached((T) serializable);
							return (T) serializable;
						} catch (final PathNotFoundException ex) {
							return null;
						}

					} catch (final Exception e) {
						throw Exceptions.logAndReturnNew(e,
								SLRuntimeException.class);
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

		/**
		 * Return the parent uuid if this object was created by a
		 * {@link SimplePersistSupport} loading operation. It will be null
		 * otherwise.
		 * 
		 * @return
		 */
		public String getParentUuid() {
			return parentUuid;
		}

		/**
		 * Return the property name if this object was created by a
		 * {@link SimplePersistSupport} loading operation. It will be null
		 * otherwise.
		 * 
		 * @return
		 */
		public String getPropertyName() {
			return propertyName;
		}

		public Class<T> getPropertyType() {
			return propertyType;
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

		public boolean isCacheLoaded() {
			try {
				lock.lock();
				return cached != null && cached.get() != null;
			} finally {
				lock.unlock();
			}
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

		public void setParentUuid(final String parentUuid) {
			LazyProperty.this.parentUuid = parentUuid;
		}

		public void setPropertyName(final String propertyName) {
			LazyProperty.this.propertyName = propertyName;
		}

	}

	private String parentUuid;

	private String propertyName;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7214615570747274715L;

	private final Metadata metadata = new Metadata();

	private final Class<T> propertyType;

	private final SimpleNodeType parent;
	private transient WeakReference<T> cached = null;
	private T transientValue = null;
	private final ReentrantLock lock = new ReentrantLock();

	private boolean needsSave = false;

	@SuppressWarnings("unchecked")
	private LazyProperty(final Class<?> propertyType,
			final SimpleNodeType parent) {
		this.propertyType = (Class<T>) propertyType;
		this.parent = parent;

	}

	/**
	 * It will try to return, in this order, the transient value, and if there's
	 * no transient value, it will return the saved value.
	 * 
	 * @param session
	 * @return
	 */
	public T get(final Session session) {
		try {
			lock.lock();
			T value = metadata.getTransient();
			if (value == null) {
				value = metadata.getCached(session);
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
			needsSave = true;
		} finally {
			lock.unlock();
		}
	}

}
