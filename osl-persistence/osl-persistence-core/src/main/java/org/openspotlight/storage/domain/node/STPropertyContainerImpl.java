package org.openspotlight.storage.domain.node;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openspotlight.storage.STStorageSession;

import com.google.inject.internal.ImmutableSet;

public abstract class STPropertyContainerImpl implements STPropertyContainer {

	public void forceReload() {
		this.propertiesByName.clear();
		this.lastLoad = -1;
	}

	protected STPropertyContainerImpl(boolean resetTimeout) {
		this.lastLoad = resetTimeout ? -1 : System.currentTimeMillis();
	}

	private static final long TIMEOUT = 60 * 1000;

	protected long lastLoad;

	private final Map<String, STProperty> propertiesByName = new HashMap<String, STProperty>();

	public Set<String> getPropertyNames(STStorageSession session) {
		reloadProperties(session);
		return ImmutableSet.copyOf(propertiesByName.keySet());
	}

	public Set<STProperty> getProperties(STStorageSession session) {
		reloadProperties(session);
		return ImmutableSet.copyOf(propertiesByName.values());
	}

	protected void verifyBeforeSet(String propertyName) {

	}

	@Override
	public STProperty setSimpleProperty(STStorageSession session, String name,
			String value) {
		verifyBeforeSet(name);
		STProperty currentProperty = getProperty(session, name);
		if (currentProperty == null) {
			currentProperty = STPropertyImpl.createSimple(name, this);
			propertiesByName.put(name, currentProperty);
		}
		currentProperty.setStringValue(session, value);

		return currentProperty;
	}

	@Override
	public STProperty setSimpleProperty(STStorageSession session, String name,
			InputStream value) {
		verifyBeforeSet(name);
		STProperty currentProperty = getProperty(session, name);
		if (currentProperty == null) {
			currentProperty = STPropertyImpl.createSimple(name, this);
			propertiesByName.put(name, currentProperty);
		}
		currentProperty.setStreamValue(session, value);

		return currentProperty;
	}

	@Override
	public STProperty setSimpleProperty(STStorageSession session, String name,
			byte[] value) {
		verifyBeforeSet(name);
		STProperty currentProperty = getProperty(session, name);
		if (currentProperty == null) {
			currentProperty = STPropertyImpl.createSimple(name, this);
			propertiesByName.put(name, currentProperty);
		}
		currentProperty.setBytesValue(session, value);

		return currentProperty;

	}

	@Override
	public STProperty setIndexedProperty(STStorageSession session, String name,
			String value) {
		verifyBeforeSet(name);
		STProperty currentProperty = getProperty(session, name);
		if (currentProperty == null) {
			currentProperty = STPropertyImpl.createIndexed(name, this);
			propertiesByName.put(name, currentProperty);
		}
		currentProperty.setStringValue(session, value);

		return currentProperty;
	}

	private void reloadProperties(STStorageSession session) {
		boolean tooOld = this.lastLoad < (System.currentTimeMillis() + TIMEOUT);
		boolean empty = propertiesByName.isEmpty();
		if (tooOld && empty) {
			Set<STProperty> result = session.withPartition(getPartition())
					.getInternalMethods().propertyContainerLoadProperties(this);
			for (STProperty property : result) {
				propertiesByName.put(property.getPropertyName(), property);
			}
			this.lastLoad = System.currentTimeMillis();
		}
	}

	private void loadPropertiesOnce(STStorageSession session) {
		if (propertiesByName.isEmpty()) {
			reloadProperties(session);
		}
	}

	public String getPropertyAsString(STStorageSession session, String name) {
		STProperty prop = getProperty(session, name);
		if (prop != null) {
			return prop.getValueAsString(session);
		}
		return null;
	}

	public InputStream getPropertyAsStream(STStorageSession session, String name) {

		STProperty prop = getProperty(session, name);
		if (prop != null) {
			return prop.getValueAsStream(session);
		}
		return null;
	}

	public byte[] getPropertyAsBytes(STStorageSession session, String name) {

		STProperty prop = getProperty(session, name);
		if (prop != null) {
			return prop.getValueAsBytes(session);
		}
		return null;
	}

	public STProperty getProperty(STStorageSession session, String name) {
		loadPropertiesOnce(session);
		STProperty result = propertiesByName.get(name);
		if (result == null) {
			reloadProperties(session);
			result = propertiesByName.get(name);
		}
		return result;
	}

}
