package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLMetaLinkPropertyImpl implements SLMetaLinkProperty {
	
	private SLMetaLinkImpl metaLink;
	private SLPersistentProperty<Serializable> pProperty;

	public SLMetaLinkPropertyImpl(SLMetaLinkImpl metaLink, SLPersistentProperty<Serializable> pProperty) {
		this.metaLink = metaLink;
		this.pProperty = pProperty;
	}

	@Override
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metaLink.getMetadata();
	}

	@Override
	public SLMetaLink getMetaLink() throws SLGraphSessionException {
		return metaLink;
	}

	@Override
	public String getName() throws SLGraphSessionException {
		try {
			return SLCommonSupport.toSimpleUserPropertyName(pProperty.getName());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link property name.", e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Serializable> getType() throws SLGraphSessionException {
		try {
			return (Class<? extends Serializable>) Class.forName((String) pProperty.getValue());
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link property type.", e);
		}
	}
}
