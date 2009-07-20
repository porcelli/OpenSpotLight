package org.openspotlight.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.openspotlight.SLException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLMetaNodeImpl implements SLMetaNode {
	
	private SLMetadata metadata;
	private SLPersistentNode pMetaNode;
	
	SLMetaNodeImpl(SLMetadata metadata, SLPersistentNode pNode) {
		this.metadata = metadata;
		this.pMetaNode = pNode;
	}

	//@Override
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metadata;
	}
	
	//@Override
	@SuppressWarnings("unchecked")
	public Class<? extends SLNode> getType() throws SLGraphSessionException {
		try {
			return (Class<? extends SLNode>) Class.forName(pMetaNode.getName());
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node type.", e);
		}
	}
	
	//@Override
	public Collection<SLMetaNodeProperty> getMetaProperties() throws SLGraphSessionException {
		try {
			Collection<SLMetaNodeProperty> metaProperties = new HashSet<SLMetaNodeProperty>();
			Collection<SLPersistentProperty<Serializable>> pProperties = pMetaNode.getProperties(SLConsts.PROPERTY_PREFIX_USER.concat(".*"));
			for (SLPersistentProperty<Serializable> pProperty : pProperties) {
				SLMetaNodeProperty metaProperty = new SLMetaNodePropertyImpl(metadata, this, pProperty);
				metaProperties.add(metaProperty);
			}
			return metaProperties;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node properties.", e);
		}
	}

	//@Override
	public SLMetaNodeProperty getMetaProperty(String name) throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<Serializable> pProperty = null;
			try {
				pProperty = pMetaNode.getProperty(Serializable.class, propName);	
			}
			catch (SLPersistentPropertyNotFoundException e) {}
			SLMetaNodeProperty metaProperty = null;
			if (pProperty != null) {
				metaProperty = new SLMetaNodePropertyImpl(metadata, this, pProperty);
			}
			return metaProperty;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node property.", e);
		}
	}
	
	//@Override
	public SLMetaNode getMetaNode(Class<? extends SLNode> nodeClass) throws SLGraphSessionException {
		try {
			SLMetaNode metaNode = null;
			SLPersistentNode pChildMetaNode = pMetaNode.getNode(nodeClass.getName());
			if (pChildMetaNode != null) {
				metaNode = new SLMetaNodeImpl(metadata, pChildMetaNode);
			}
			return metaNode;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node.", e);
		}
	}

	//@Override
	public Collection<SLMetaNode> getMetaNodes() throws SLGraphSessionException {
		try {
			Collection<SLMetaNode> metaNodes = new ArrayList<SLMetaNode>();
			Collection<SLPersistentNode> pMetaNodes = pMetaNode.getNodes();
			for (SLPersistentNode pMetaNode : pMetaNodes) {
				SLMetaNode metaNode = new SLMetaNodeImpl(metadata, pMetaNode);
				metaNodes.add(metaNode);
			}
			return metaNodes;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta nodes.", e);
		}
	}
	
	//@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SLMetaNodeImpl)) return false;
		SLMetaNodeImpl metaNode = (SLMetaNodeImpl) obj;
		return pMetaNode.equals(metaNode);
	}
	
	//@Override
	public int hashCode() {
		return pMetaNode.hashCode();
	}
}


