package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLMetaNodePropertyImpl implements SLMetaNodeProperty {
	
	private SLMetadata metadata;
	private SLMetaNode metaNode;
	private SLPersistentProperty<Serializable> pProperty;
	
	SLMetaNodePropertyImpl(SLMetadata metadata, SLMetaNode metaNode, SLPersistentProperty<Serializable> pProperty) {
		this.metadata = metadata;
		this.metaNode = metaNode;
		this.pProperty = pProperty;
	}

	//@Override
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metadata;
	}

	//@Override
	public SLMetaNode getMetaNode() throws SLGraphSessionException {
		return metaNode;
	}

	//@Override
	public String getName() throws SLGraphSessionException {
		try {
			return SLCommonSupport.toSimpleUserPropertyName(pProperty.getName());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node property name.", e);
		}
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Serializable> getType() throws SLGraphSessionException {
		try {
			return (Class<? extends Serializable>) Class.forName((String) pProperty.getValue());
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node property type.", e);
		}
	}
	
	//@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SLMetaNodePropertyImpl)) return false;
		SLMetaNodePropertyImpl metaProperty = (SLMetaNodePropertyImpl) obj;
		return pProperty.equals(metaProperty.pProperty);
	}
	
	//@Override
	public int hashCode() {
		return pProperty.hashCode();
	}

}

