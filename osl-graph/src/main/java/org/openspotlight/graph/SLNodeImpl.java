package org.openspotlight.graph;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openspotlight.SLException;
import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.AbstractFactory;
import org.openspotlight.graph.util.ProxyUtil;
	
public class SLNodeImpl implements SLNode {
	
	private SLContext context;
	private SLNode parent;
	private SLPersistentNode pNode;
	private SLGraphSessionEventPoster eventPoster;
	
	public SLNodeImpl(SLContext context, SLNode parent, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster) {
		this.context = context;
		this.parent = parent;
		this.pNode = persistentNode;
		this.eventPoster = eventPoster;
	}

	@Override
	public SLGraphSession getSession() {
		return context.getSession();
	}

	@Override
	public SLContext getContext() {
		return context;
	}

	@Override
	public String getID() throws SLGraphSessionException {
		try {
			return pNode.getID();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve the node ID.", e);
		}
	}

	@Override
	public String getName() throws SLGraphSessionException {
		try {
			return pNode.getName();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve the node name.", e);
		}
	}

	@Override
	public SLNode addNode(String name) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		return addChildNode(SLNode.class, name, null, null);
	}

	@Override
	public <T extends SLNode> T addNode(Class<T> clazz, String name) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		return addChildNode(clazz, name, null, null);
	}
	
	@Override
	public <T extends SLNode> T addNode(Class<T> clazz, String name, Collection<Class<? extends SLLink>> linkTypesForLinkDeletion, Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		return addChildNode(clazz, name, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
	}

	@Override
	public SLNode getNode(String name) throws SLInvalidNodeTypeException, SLGraphSessionException {
		return getChildNode(SLNode.class, name);
	}

	@Override
	public <T extends SLNode> T getNode(Class<T> clazz, String name) throws SLInvalidNodeTypeException, SLGraphSessionException {
		return getChildNode(clazz, name);
	}

	@Override
	public Set<SLNode> getNodes() throws SLGraphSessionException {
		try {
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
			Set<SLNode> childNodes = new HashSet<SLNode>();
			Set<SLPersistentNode> persistentChildNodes = pNode.getNodes();
			for (SLPersistentNode persistentChildNode : persistentChildNodes) {
				SLNode childNode = factory.createNode(getContext(), this, persistentChildNode, eventPoster);
				SLNodeInvocationHandler handler = new SLNodeInvocationHandler(childNode);
				SLNode childNodeProxy = ProxyUtil.createProxy(SLNode.class, handler);
				childNodes.add(childNodeProxy);
			}
			return childNodes;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve child nodes.", e);
		}
	}

	@Override
	public void remove() throws SLGraphSessionException {
		try {
			Collection<SLNode> childNodes = new ArrayList<SLNode>();
			Iterator<SLNode> iter = getNodes().iterator();
			while (iter.hasNext()) {
				addChildNodes(childNodes, iter.next());
			}
			for (SLNode current : childNodes) {
				Collection<SLLink> links = getSession().getLinks(current, null);
				for (SLLink link : links) {
					link.remove();
				}
			}
			pNode.remove();
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to remove node.", e);
		}
	}

	@Override
	public SLNode getParent() throws SLGraphSessionException {
		return parent;
	}
	
	@Override
	public <V extends Serializable> SLNodeProperty<V> setProperty(Class<V> clazz, String name, V value)	throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<V> pProperty = pNode.setProperty(clazz, propName, value);
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
			SLNodeProperty<V> property = factory.createProperty(this, pProperty);
			addMetaNodeProperty(pProperty);
			return property;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to set property.", e);
		}
	}

	@Override
	public <V extends Serializable> SLNodeProperty<V> getProperty(Class<V> clazz, String name) 
		throws SLNodePropertyNotFoundException, SLInvalidNodePropertyTypeException, SLGraphSessionException {
		try {
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<V> persistentProperty = pNode.getProperty(clazz, propName);
			return factory.createProperty(this, persistentProperty);
		}
		catch (SLPersistentPropertyNotFoundException e) {
			throw new SLNodePropertyNotFoundException(name, e);
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw new SLInvalidNodePropertyTypeException(e);
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve property.", e);
		}
	}
	
	@Override
	public String getPropertyValueAsString(String name) throws SLNodePropertyNotFoundException, SLGraphSessionException {
		return getProperty(Serializable.class, name).getValue().toString();
	}

	@Override
	public Set<SLNodeProperty<Serializable>> getProperties() throws SLGraphSessionException {
		try {
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
			Set<SLNodeProperty<Serializable>> properties = new HashSet<SLNodeProperty<Serializable>>();
			Set<SLPersistentProperty<Serializable>> persistentProperties = pNode.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
			for (SLPersistentProperty<Serializable> persistentProperty : persistentProperties) {
				SLNodeProperty<Serializable> property = factory.createProperty(this, persistentProperty);
				properties.add(property);
			}
			return properties;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node properties.", e);
		}
	}
	
	@Override
	public SLLineReference addLineReference() throws SLGraphSessionException {
		return addLineReference("default");
	}
	
	@Override
	public SLLineReference addLineReference(String name) throws SLGraphSessionException {
		try {
			return addNode(SLLineReference.class, "lineRef." + name);
		}
		catch (SLGraphSessionException e) {
			throw new SLGraphSessionException("Error on attempt to add line reference.", e);
		}
	}
	
	@Override
	public SLLineReference getLineReference() throws SLGraphSessionException {
		return getLineReference("default");
	}

	@Override
	public SLLineReference getLineReference(String name) throws SLGraphSessionException {
		try {
			return getNode(SLLineReference.class, "lineRef." + name);
		}
		catch (SLGraphSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve line reference.", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null || !(obj instanceof SLNode)) return false;
			SLNodeInvocationHandler handler = (SLNodeInvocationHandler) Proxy.getInvocationHandler(obj);
			SLNodeImpl node = (SLNodeImpl) handler.getNode();
			return pNode.getPath().equals(node.pNode.getPath());
		}
		catch (SLException e) {
			throw new RuntimeException("Error on " + this.getClass() + " equals method.", e);
		}
	}
	
	@Override
	public int hashCode() {
		try {
			return getID().hashCode();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate node hash code.", e);
		}
	}

	@Override
	public int compareTo(SLNode node) {
		try {
			SLNodeInvocationHandler handler = (SLNodeInvocationHandler) Proxy.getInvocationHandler(node);
			SLNodeImpl n = (SLNodeImpl) handler.getNode();
			return pNode.getPath().compareTo(n.pNode.getPath());
		}
		catch (SLException e) {
			throw new SLRuntimeException("Error on attempt to compare nodes.", e);
		}
	}
	
	@Override
	public String toString() {
		return pNode.toString();
	}
	
	@SuppressWarnings("unchecked")
	private <T extends SLNode> T addChildNode(Class<T> clazz, String name, Collection<Class<? extends SLLink>> linkTypesForLinkDeletion, Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {

		try {
			Class<? extends SLNode> proxyIFace = clazz;
			SLPersistentNode pChildNode = pNode.getNode(name);
			String typePropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
			
			if (pChildNode == null) {
				pChildNode = pNode.addNode(name);
				pChildNode.setProperty(String.class, typePropName, clazz.getName());
				addMetaNode(pChildNode);
			}
			else {
				
				SLPersistentProperty<String> typeProp = pChildNode.getProperty(String.class, typePropName);
				Class<? extends SLNode> existentClass = (Class<? extends SLNode>) Class.forName(typeProp.getValue());
				
				if (!clazz.equals(existentClass)) {
					
					// if given class is sub class of existent class ...
					if (existentClass.isAssignableFrom(clazz)) {
						typeProp.setValue(clazz.getName());
					}
					
					// if given class is super class of existent class ...
					else if (clazz.isAssignableFrom(existentClass)) {
						proxyIFace = existentClass;
					}
					
					// if given and existent classes do not belong to the same hierarchy ...
					else {
						throw new SLNodeTypeNotInExistentHierarchy(clazz, existentClass);
					}
				}
			}

			SLNode node = new SLNodeImpl(getContext(), parent, pChildNode, eventPoster);
			InvocationHandler handler = new SLNodeInvocationHandler(node);
			T nodeProxy = clazz.cast(ProxyUtil.createProxy(proxyIFace, handler));
			eventPoster.post(new SLNodeEvent(SLNodeEvent.TYPE_NODE_ADDED, getSession(), nodeProxy, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion));
			return nodeProxy;
		}
		catch (SLNodeTypeNotInExistentHierarchy e) {
			throw e;
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to add node.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends SLNode> T getChildNode(Class<T> clazz, String name) throws SLInvalidNodeTypeException, SLGraphSessionException {
		T nodeProxy = null;
		Class<T> nodeClass = null;
		try {
			SLPersistentNode childPersistentNode = pNode.getNode(name);
			if (childPersistentNode != null) {
				SLPersistentProperty<String> typePersistentProperty = childPersistentNode.getProperty(String.class,	SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE));
				nodeClass = (Class<T>) Class.forName(typePersistentProperty.getValue());
				if (clazz.isAssignableFrom(nodeClass)) {
					SLNode node = new SLNodeImpl(getContext(), this, childPersistentNode, eventPoster);
					InvocationHandler handler = new SLNodeInvocationHandler(node);
					nodeProxy = ProxyUtil.createProxy(nodeClass, handler);
				}
				else {
					throw new SLInvalidNodeTypeException(clazz, nodeClass);
				}
			}
			return nodeProxy;
		}
		catch (SLInvalidNodeTypeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to get node.", e);
		}
	}
	
	private void addMetaNodeProperty(SLPersistentProperty<? extends Serializable> pProperty) throws SLPersistentTreeSessionException {
		SLPersistentNode pNode = pProperty.getNode();
		SLPersistentProperty<String> metaNodeIDProp = null;
		try {
			metaNodeIDProp = pNode.getProperty(String.class, SLConsts.PROPERTY_NAME_META_NODE_ID);
		}
		catch (SLPersistentPropertyNotFoundException e) {}
		if (metaNodeIDProp != null) {
			SLPersistentTreeSession session = pNode.getSession();
			SLPersistentNode metaNode = session.getNodeByID(metaNodeIDProp.getValue());
			metaNode.setProperty(String.class, pProperty.getName(), pProperty.getValue().getClass().getName());
		}
	}
	
	private void addMetaNode(SLPersistentNode pNode) throws SLPersistentTreeSessionException {
		String typePropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
		SLPersistentProperty<String> typeProp = pNode.getProperty(String.class, typePropName);
		String nodeClassName = typeProp.getValue();
		if (nodeClassName.equals(SLNode.class.getName())) return;
		String path = buildMetadataTypeNodePath(pNode);
		SLPersistentTreeSession treeSession = pNode.getSession();
		SLPersistentNode typeNode = treeSession.getNodeByPath(path);
		if (typeNode == null) {
			String parentPath = buildMetadataTypeNodePath(pNode.getParent());
			SLPersistentNode parentTypeNode;
			if (parentPath.equals("//osl/metadata/types")) {
				parentTypeNode = SLCommonSupport.getMetaTypesNode(treeSession); 
			}
			else {
				parentTypeNode = treeSession.getNodeByPath(parentPath);
			}
			typeNode = parentTypeNode.addNode(nodeClassName);
			pNode.setProperty(String.class, SLConsts.PROPERTY_NAME_META_NODE_ID, typeNode.getID());
		}
	}
	
	private String buildMetadataTypeNodePath(SLPersistentNode pNode) throws SLPersistentTreeSessionException {
		String typePropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
		StringBuilder statement = new StringBuilder();
		List<String> typeNames = new ArrayList<String>();
		do {
			try {
				SLPersistentProperty<String> typeProp = pNode.getProperty(String.class, typePropName);
				statement.insert(0, typeProp.getValue()).insert(0, '/');
				typeNames.add(0, typeProp.getValue());
			}
			catch (SLPersistentPropertyNotFoundException e) {
				break;
			}
		}
		while ((pNode = pNode.getParent()) != null);
		statement.insert(0, "//osl/metadata/types");
		return statement.toString();
	}
	
	private void addChildNodes(Collection<SLNode> childNodes, SLNode node) throws SLGraphSessionException {
		Collection<SLNode> nodes = node.getNodes();
		for (SLNode current : nodes) {
			addChildNodes(childNodes, current);
		}
		childNodes.add(node);
	}
	

}

