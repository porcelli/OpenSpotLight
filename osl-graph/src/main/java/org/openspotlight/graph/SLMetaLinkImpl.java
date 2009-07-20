package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLMetaLinkImpl implements SLMetaLink {
	
	private SLPersistentNode metaLinkNode;
	private SLMetaLinkType metaLinkType;
	private Class<? extends SLNode> sourceType;
	private Class<? extends SLNode> targetType;
	private List<Class<? extends SLNode>> sideTypes;
	private boolean bidirectional;
	
	SLMetaLinkImpl(SLPersistentNode metaLinkNode, SLMetaLinkType metaLinkType, Class<? extends SLNode> sourceType, Class<? extends SLNode> targetType, List<Class<? extends SLNode>> sideTypes, boolean bidirectional) {
		this.metaLinkNode = metaLinkNode;
		this.metaLinkType = metaLinkType;
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.sideTypes = sideTypes;
		this.bidirectional = bidirectional;
	}

	@Override
	public SLMetaLinkType getMetaLinkType() throws SLGraphSessionException {
		return metaLinkType;
	}

	@Override
	public Class<? extends SLNode> getSourceType() throws SLGraphSessionException {
		if (bidirectional) {
			// this method cannot be used on bidirecional meta links, because source and targets types are relatives.
			// on unidirecional links, source and target types are well defined.
			throw new UnsupportedOperationException("SLMetaLink.getSource() cannot be used on bidirecional meta links.");
		}
		return sourceType;
	}

	@Override
	public Class<? extends SLNode> getTargetType() throws SLGraphSessionException {
		if (bidirectional) {
			// this method cannot be used on bidirecional meta links, because source and targets types are relatives.
			// on unidirecional links, source and target types are well defined.
			throw new UnsupportedOperationException("SLMetaLink.getTarget() cannot be used on bidirecional meta links.");
		}
		return targetType;
	}

	
	@Override
	public Class<? extends SLNode> getOtherSideType(Class<? extends SLNode> sideType) throws SLInvalidMetaLinkSideTypeException, SLGraphSessionException {
		Class<? extends SLNode> otherSideType = null;
		if (sideType.equals(sourceType)) otherSideType = targetType;
		else if (sideType.equals(targetType)) otherSideType = sourceType;
		else throw new SLInvalidMetaLinkSideTypeException(); 
		return otherSideType;
	}

	@Override
	public List<Class<? extends SLNode>> getSideTypes() throws SLGraphSessionException {
		return sideTypes;
	}

	@Override
	public boolean isBidirectional() throws SLGraphSessionException {
		return bidirectional;
	}

	@Override
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metaLinkType.getMetadata();
	}

	@Override
	public Collection<SLMetaLinkProperty> getMetaProperties() throws SLGraphSessionException {
		try {
			Collection<SLMetaLinkProperty> metaProperties = new HashSet<SLMetaLinkProperty>();
			Collection<SLPersistentProperty<Serializable>> pProperties = metaLinkNode.getProperties(SLConsts.PROPERTY_PREFIX_USER.concat(".*"));
			for (SLPersistentProperty<Serializable> pProperty : pProperties) {
				SLMetaLinkProperty metaProperty = new SLMetaLinkPropertyImpl(this, pProperty);
				metaProperties.add(metaProperty);
			}
			return metaProperties;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link properties.", e);
		}
	}

	@Override
	public SLMetaLinkProperty getMetaProperty(String name) throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<Serializable> pProperty = null;
			try {
				pProperty = metaLinkNode.getProperty(Serializable.class, propName);	
			}
			catch (SLPersistentPropertyNotFoundException e) {}
			SLMetaLinkProperty metaProperty = null;
			if (pProperty != null) {
				metaProperty = new SLMetaLinkPropertyImpl(this, pProperty);
			}
			return metaProperty;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link property.", e);
		}
	}
}


