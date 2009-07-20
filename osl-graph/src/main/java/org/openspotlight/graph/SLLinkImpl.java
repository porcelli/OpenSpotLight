package org.openspotlight.graph;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.SLException;
import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.SimpleInvocationHandler;

public class SLLinkImpl implements SLLink {
	
	private SLGraphSession session;
	private SLPersistentNode linkNode;
	
	public SLLinkImpl(SLGraphSession session, SLPersistentNode linkNode) {
		this.session = session;
		this.linkNode = linkNode;
	}

	//@Override
	public String getID() throws SLGraphSessionException {
		try {
			return linkNode.getID();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link ID.", e);
		}
	}

	//@Override
	public SLNode getOtherSide(SLNode side) throws SLInvalidLinkSideException, SLGraphSessionException {
		SLNode otherSide = null;
		try {
			SLNode aNode = getANode();
			SLNode bNode = getBNode();
			if (aNode.equals(bNode) && aNode.equals(side)) {
				otherSide = side;
			}
			else {
				if (side.equals(aNode)) otherSide = bNode;
				else if (side.equals(bNode)) otherSide = aNode;
			}
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link other side.", e);
		}
		if (otherSide == null) throw new SLInvalidLinkSideException();
		return otherSide;
	}

	//@Override
	public SLNode[] getSides() throws SLGraphSessionException {
		try {
			return new SLNode[] {getANode(), getBNode()};
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link sides.", e);
		}
	}

	//@Override
	public SLNode getSource() throws SLGraphSessionException {
		if (isBidirectional()) {
			// this method cannot be used on bidirecional links, because source and targets are relatives.
			// on unidirecional links, source and target are well defined.
			throw new UnsupportedOperationException("SLLink.getSource() cannot be used on bidirecional links.");
		}
		try {
			return getDirection() == SLConsts.DIRECTION_AB ? getANode() : getBNode();
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link source.", e);
		}
	}

	//@Override
	public SLNode getTarget() throws SLGraphSessionException {
		if (isBidirectional()) {
			// this method cannot be used on bidirecional links, because source and targets are relatives.
			// on unidirecional links, source and target are well defined.
			throw new UnsupportedOperationException("SLLink.getTarget() cannot be used on bidirecional links.");
		}
		try {
			return getDirection() == SLConsts.DIRECTION_AB ? getBNode() : getANode();
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link source.", e);
		}
	}
	
	//@Override
	public boolean isBidirectional() throws SLGraphSessionException {
		try {
			return getDirection() == SLConsts.DIRECTION_BOTH;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to verify if link is bidirectional.", e);
		}
	}
	
	//@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null || !(obj instanceof SLLink)) return false;
			SLPersistentNode classNode1 = getLinkClassNode();
			SLPersistentNode pairNode1 = getPairKeyNode();
			String name1 = classNode1.getName().concat(pairNode1.getName());
			SimpleInvocationHandler handler = (SimpleInvocationHandler) Proxy.getInvocationHandler(obj);
			SLLinkImpl link = (SLLinkImpl) handler.getTarget();
			SLPersistentNode classNode2 = link.getLinkClassNode();
			SLPersistentNode pairNode2 = link.getPairKeyNode();
			String name2 = classNode2.getName().concat(pairNode2.getName());
			return name1.equals(name2);
		}
		catch (SLException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkImpl.equals().", e);
		}
	}
	
	//@Override
	public int hashCode() {
		try {
			return getID().hashCode();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkImpl.hasCode().", e);
		}
	}
	
	//@Override
	public <V extends Serializable> SLLinkProperty<V> setProperty(Class<V> clazz, String name, V value) throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<V> pProperty = linkNode.setProperty(clazz, propName, value);
			addMetaLinkProperty(pProperty);
			return new SLLinkPropertyImpl<V>(this, pProperty);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to set link property.", e);
		}
	}

	//@Override
	public <V extends Serializable> SLLinkProperty<V> getProperty(Class<V> clazz, String name)
		throws SLLinkPropertyNotFoundException, SLInvalidLinkPropertyTypeException, SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<V> persistentProperty = linkNode.getProperty(clazz, propName);
			return new SLLinkPropertyImpl<V>(this, persistentProperty);
		}
		catch (SLPersistentPropertyNotFoundException e) {
			throw new SLLinkPropertyNotFoundException(name, e);
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw new SLInvalidLinkPropertyTypeException(e);
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link property.", e);
		}
	}

	//@Override
	public String getPropertyValueAsString(String name) throws SLLinkPropertyNotFoundException, SLGraphSessionException {
		return getProperty(Serializable.class, name).getValue().toString();
	}

	//@Override
	public Set<SLLinkProperty<Serializable>> getProperties() throws SLGraphSessionException {
		try {
			Set<SLLinkProperty<Serializable>> properties = new HashSet<SLLinkProperty<Serializable>>();
			Set<SLPersistentProperty<Serializable>> persistentProperties = linkNode.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
			for (SLPersistentProperty<Serializable> persistentProperty : persistentProperties) {
				SLLinkProperty<Serializable> property = new SLLinkPropertyImpl<Serializable>(this, persistentProperty);
				properties.add(property);
			}
			return properties;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node properties.", e);
		}
	}

	//@Override
	public String toString() {
		return linkNode.toString();
	}

	//@Override
	public int compareTo(SLLink l) {
		try {
			SimpleInvocationHandler handler = (SimpleInvocationHandler) Proxy.getInvocationHandler(l);
			SLLinkImpl link = (SLLinkImpl) handler.getTarget();
			String linkClassName1 = getLinkClassNode().getName();
			String linkClassName2 = link.getLinkClassNode().getName();
			if (linkClassName1.equals(linkClassName2)) {
				String pairName1 = getPairKeyNode().getName();
				String pairName2 = link.getPairKeyNode().getName();
				if (pairName1.equals(pairName2)) {
					Long linkCount1 = linkNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT).getValue();
					Long linkCount2 = link.linkNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT).getValue();
					return linkCount1.compareTo(linkCount2);
				}
				else {
					return pairName1.compareTo(pairName2);
				}
			}
			else {
				return linkClassName1.compareTo(linkClassName2);
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkImpl.compareTo().", e);
		}
	}

	//@Override
	public void remove() throws SLGraphSessionException {
		try {
			linkNode.remove();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove link.", e);
		}
	}

	private int getDirection() throws SLPersistentTreeSessionException {
		SLPersistentProperty<Integer> directionProp = linkNode.getProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION);
		return directionProp.getValue();
	}
	
	private SLNode getANode() throws SLException {
		SLPersistentNode pairKeyNode = getPairKeyNode();
		SLPersistentProperty<String> nodeIDProp = pairKeyNode.getProperty(String.class, SLConsts.PROPERTY_NAME_A_NODE_ID);
		return session.getNodeByID(nodeIDProp.getValue());
	}

	private SLNode getBNode() throws SLException {
		SLPersistentNode pairKeyNode = getPairKeyNode();
		SLPersistentProperty<String> nodeIDProp = pairKeyNode.getProperty(String.class, SLConsts.PROPERTY_NAME_B_NODE_ID);
		return session.getNodeByID(nodeIDProp.getValue());
	}

	private SLPersistentNode getPairKeyNode() throws SLPersistentTreeSessionException {
		return linkNode.getParent();
	}
	
	private SLPersistentNode getLinkClassNode() throws SLPersistentTreeSessionException {
		return getPairKeyNode().getParent();
	}
	
	private void addMetaLinkProperty(SLPersistentProperty<? extends Serializable> pProperty) throws SLPersistentTreeSessionException {
		SLPersistentNode linkNode = pProperty.getNode();
		String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
		SLPersistentProperty<String> metaNodeIDProp = null;
		try {
			metaNodeIDProp = linkNode.getProperty(String.class, propName);	
		}
		catch (SLPersistentPropertyNotFoundException e) {}
		SLPersistentTreeSession treeSession = linkNode.getSession();
		SLPersistentNode metaLinkNode = treeSession.getNodeByID(metaNodeIDProp.getValue());
		metaLinkNode.setProperty(String.class, pProperty.getName(), pProperty.getValue().getClass().getName());
	}
}

