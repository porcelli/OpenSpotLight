package org.openspotlight.graph.persistence;

import java.io.Serializable;
import java.lang.reflect.Array;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.openspotlight.SLException;
import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.util.JCRUtil;
import org.openspotlight.graph.util.SerializationUtil;

public class SLPersistentPropertyImpl<V extends Serializable> implements SLPersistentProperty<V> {
	
	private SLPersistentNode persistentNode;
	private Property jcrProperty;
	private Class<V> clazz;
	private SLPersistentEventPoster eventPoster;
	
	public SLPersistentPropertyImpl(SLPersistentNode persistentNode, Class<V> clazz, Property jcrProperty, boolean loadValue, SLPersistentEventPoster eventPoster) 
		throws SLPersistentPropertyNotFoundException, SLPersistentTreeSessionException {
		this.persistentNode = persistentNode;
		this.clazz = clazz;
		this.jcrProperty = jcrProperty;
		this.eventPoster = eventPoster;
		if (loadValue) getValue();
	}

	//@Override
	public SLPersistentNode getNode() {
		return persistentNode;
	}

	//@Override
	public String getName() throws SLPersistentTreeSessionException {
		try {
			return jcrProperty.getName();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent property name.", e);
		}
	}

	//@Override
	public V getValue() throws SLInvalidPersistentPropertyTypeException, SLPersistentTreeSessionException {
		try {
			V value = null;
			// if the property value is an array ...
			if (jcrProperty.getDefinition().isMultiple()) {
				if (!clazz.isArray()) {
					throw new SLInvalidPersistentPropertyTypeException("Persistent property is an array.");	
				}
				value = getJCRArrayPropertyValue();
			}
			// if the property value is not an array ...
			else if (!jcrProperty.getDefinition().isMultiple()) {
				if (clazz.isArray()) {
					throw new SLInvalidPersistentPropertyTypeException("Persistent property is not an array.");
				}
				value = getJCRPropertyValue();
			}
			return value;
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve property value.", e);
		}
	}
	
	//@Override
	public void setValue(V value) throws SLPersistentTreeSessionException {
		try {
			Session session = jcrProperty.getSession();
			if (value.getClass().isArray()) {
				Value[] jcrValues = JCRUtil.createValues(session, value);
				jcrProperty.setValue(jcrValues);
			}
			else {
				Value jcrValue = JCRUtil.createValue(session, value);
				jcrProperty.setValue(jcrValue);
			}
			eventPoster.post(new SLPersistentPropertyEvent(SLPersistentPropertyEvent.TYPE_PROPERTY_SET, this));
		}
		catch (Exception e) {
			throw new SLPersistentTreeSessionException("Error on attempt to set persistent property.", e);
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws SLInvalidPersistentPropertyTypeException
	 * @throws SLException
	 * @throws RepositoryException
	 */
	private V getJCRPropertyValue() throws SLInvalidPersistentPropertyTypeException, SLException, RepositoryException {
		V value = null;
		if (jcrProperty.getType() == PropertyType.LONG) {
			if (clazz.equals(Integer.class)) {
				Integer intValue = (int) jcrProperty.getValue().getLong();
				value = clazz.cast(intValue);
			}
			else if (clazz.isAssignableFrom(Long.class)) {
				value = clazz.cast(new Long(jcrProperty.getValue().getLong()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Integer.class, Long.class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.DOUBLE) {
			if (clazz.equals(Float.class)) {
				Float floatValue = (float) jcrProperty.getValue().getDouble();
				value = clazz.cast(floatValue);
			}
			else if (clazz.isAssignableFrom(Double.class)) {
				value = clazz.cast(new Double(jcrProperty.getValue().getDouble()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Float.class, Double.class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.BOOLEAN) {
			if (clazz.isAssignableFrom(Boolean.class)) {
				value = clazz.cast(new Boolean(jcrProperty.getBoolean()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Boolean.class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.STRING) {
			if (clazz.isAssignableFrom(String.class)) {
				value = clazz.cast(new String(jcrProperty.getString()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, String.class);
			}
		}
		else {
			Object object = SerializationUtil.deserialize(jcrProperty.getStream());
			if (clazz.isAssignableFrom(object.getClass())) {
				value = clazz.cast(object);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, object.getClass());
			}
		}
		return value;
	}
	
	/**
	 * 
	 * @return
	 * @throws SLInvalidPersistentPropertyTypeException
	 * @throws SLException
	 * @throws RepositoryException
	 */
	private V getJCRArrayPropertyValue() throws SLInvalidPersistentPropertyTypeException, SLException, RepositoryException {
		V value = null;
		Value[] jcrValues = jcrProperty.getValues();
		if (jcrProperty.getType() == PropertyType.LONG) {
			if (clazz.equals(Integer[].class)) {
				Integer[] intValues = new Integer[jcrValues.length];
				for (int i = 0; i < intValues.length; i++) {
					intValues[i] = (int) jcrValues[i].getLong();
				}
				value = clazz.cast(intValues);
			}
			else if (clazz.isAssignableFrom(Long[].class)) {
				Long[] longValues = new Long[jcrValues.length];
				for (int i = 0; i < longValues.length; i++) {
					longValues[i] = jcrValues[i].getLong();
				}
				value = clazz.cast(longValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Integer[].class, Long[].class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.DOUBLE) {
			if (clazz.equals(Float[].class)) {
				Float[] floatValues = new Float[jcrValues.length];
				for (int i = 0; i < floatValues.length; i++) {
					floatValues[i] = (float) jcrValues[i].getDouble();
				}
				value = clazz.cast(floatValues);
			}
			else if (clazz.isAssignableFrom(Double[].class)) {
				Double[] doubleValues = new Double[jcrValues.length];
				for (int i = 0; i < doubleValues.length; i++) {
					doubleValues[i] = jcrValues[i].getDouble();
				}
				value = clazz.cast(doubleValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Float[].class, Double[].class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.BOOLEAN) {
			if (clazz.isAssignableFrom(Boolean.class)) {
				Boolean[] booleanValues = new Boolean[jcrValues.length];
				for (int i = 0; i < booleanValues.length; i++) {
					booleanValues[i] = jcrValues[i].getBoolean();
				}
				value = clazz.cast(booleanValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Boolean[].class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.STRING) {
			if (clazz.isAssignableFrom(String.class)) {
				String[] stringValues = new String[jcrValues.length];
				for (int i = 0; i < stringValues.length; i++) {
					stringValues[i] = jcrValues[i].getString();
				}
				value = clazz.cast(stringValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, String[].class);
			}
		}
		else {
			value = clazz.cast(Array.newInstance(clazz.getComponentType(), jcrValues.length));
			for (int i = 0; i < jcrValues.length; i++) {
				Object object = SerializationUtil.deserialize(jcrValues[i].getStream());
				if (clazz.getComponentType().isAssignableFrom(object.getClass())) {
					Array.set(value, i, object);
				}
				else {
					throw new SLInvalidPersistentPropertyTypeException("Error on attempt to set array position. Cannot cast " + 
						object.getClass() + " to " + clazz.getComponentType());
				}
			}
		}
		return value;
	}

	//@Override
	public void remove() throws SLPersistentTreeSessionException {
		try {
			jcrProperty.remove();
			eventPoster.post(new SLPersistentPropertyEvent(SLPersistentPropertyEvent.TYPE_PROPERTY_REMOVED, this));
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to remove persistent property.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	//@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null) return false;
			SLPersistentProperty persistentProperty = (SLPersistentProperty) obj;
			String name1 = persistentProperty.getNode().getID() + ":" + getName();
			String name2 = getNode().getID() + ":" + getName();
			return name1.equals(name2);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute persistent property equals method.");
		}
	}
	
	//@Override
	public int hashCode() {
		try {
			return (getNode().getID() + ":" + getName()).hashCode();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate persistent property hash code.");
		}
	}
}
