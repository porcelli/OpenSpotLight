package org.openspotlight.persist.internal;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
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

public final class LazyProperty<T> implements Serializable {

	public static final class Factory {
		public static <T> LazyProperty<T> create(
				final Class<T> propertyType, final SimpleNodeType parent) {
			return new LazyProperty<T>(propertyType, null, parent);
		}

		public static LazyProperty<?> createUntyped(
				final Class<?> propertyType, final SimpleNodeType parent) {
			return new LazyProperty<Serializable>(propertyType, null, parent);
		}

		private Factory() {
		}

	}

	public final class Metadata implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -31577246921422934L;

		private Metadata() {
		}

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
							SimplePersistSupport
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

		public Class<?> getContainerType() {
			return containerType;
		}

		public ReentrantLock getLock() {
			return lock;
		}

		public SimpleNodeType getParent() {
			return parent;
		}

		public String getParentUuid() {
			return parentUuid;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public Class<T> getPropertyType() {
			return propertyType;
		}

		public T getTransient() {
			try {
				lock.lock();
				return transientValue;
			} finally {
				lock.unlock();

			}
		}

		public boolean isTransientLoaded() {
			try {
				lock.lock();
				return transientValue != null;
			} finally {
				lock.unlock();
			}
		}

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
	private final Class<?> containerType;

	private boolean needsSave = false;

	@SuppressWarnings("unchecked")
	private LazyProperty(final Class<?> propertyType,
			final Class<?> containerType, final SimpleNodeType parent) {
		this.propertyType = (Class<T>) propertyType;
		this.parent = parent;
		this.containerType = containerType;

	}

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
