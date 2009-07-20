package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLLinkPropertyImpl<V extends Serializable> implements SLLinkProperty<V> {
	
	private SLLink link;
	private SLPersistentProperty<V> persistentProperty;
	
	public SLLinkPropertyImpl(SLLink link, SLPersistentProperty<V> persistentProperty) {
		this.link = link;
		this.persistentProperty = persistentProperty;
	}

	//@Override
	public SLLink getLink() {
		return link;
	}

	//@Override
	public String getName() throws SLGraphSessionException {
		try {
			return SLCommonSupport.toSimpleUserPropertyName(persistentProperty.getName());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link property name.", e);
		}
	}

	//@Override
	public V getValue() throws SLInvalidLinkPropertyTypeException, SLGraphSessionException {
		try {
			return persistentProperty.getValue();
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw new SLInvalidLinkPropertyTypeException(e);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link property value.", e);
		}
	}

	//@Override
	public String getValueAsString() throws SLGraphSessionException {
		return getValue().toString();
	}

	//@Override
	public void remove() throws SLGraphSessionException {
		try {
			persistentProperty.remove();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove link property.", e);
		}
	}

	//@Override
	public void setValue(V value) throws SLGraphSessionException {
		try {
			persistentProperty.setValue(value);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to set link property value.", e);
		}
	}
}
