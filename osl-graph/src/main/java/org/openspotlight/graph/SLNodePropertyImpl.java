package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLNodePropertyImpl<V extends Serializable> implements SLNodeProperty<V> {

	private static final long serialVersionUID = 1L;
	
	private SLNode node;
	private SLPersistentProperty<V> persistentProperty;
	
	public SLNodePropertyImpl(SLNode node, SLPersistentProperty<V> persistentProperty) {
		this.node = node;
		this.persistentProperty = persistentProperty;
	}

	@Override
	public SLNode getNode() {
		return node;
	}

	@Override
	public String getName() throws SLGraphSessionException {
		try {
			return SLCommonSupport.toSimpleUserPropertyName(persistentProperty.getName());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve the property name.", e);
		}
	}

	@Override
	public V getValue() throws SLInvalidNodePropertyTypeException, SLGraphSessionException {
		try {
			return persistentProperty.getValue();
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw new SLInvalidNodePropertyTypeException(e);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve the property value.", e);
		}
	}
	
	@Override
	public String getValueAsString() throws SLGraphSessionException {
		return getValue().toString();
	}
	
	@Override
	public void setValue(V value) throws SLGraphSessionException {
		try {
			persistentProperty.setValue(value);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to set the property value.", e);
		}
	}

	@Override
	public void remove() throws SLGraphSessionException {
		try {
			persistentProperty.remove();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove property.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null) return false;
			SLNodeProperty property = (SLNodeProperty) obj;
			String name1 = property.getNode().getID() + ":" + getName();
			String name2 = getNode().getID() + ":" + getName();
			return name1.equals(name2);
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute property equals method.");
		}
	}
	
	@Override
	public int hashCode() {
		try {
			return (getNode().getID() + ":" + getName()).hashCode();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate property hash code.");
		}
	}
}
