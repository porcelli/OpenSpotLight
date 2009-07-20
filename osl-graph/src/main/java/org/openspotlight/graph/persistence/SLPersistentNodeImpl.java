package org.openspotlight.graph.persistence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.openspotlight.graph.util.JCRUtil;

public class SLPersistentNodeImpl implements SLPersistentNode {
	
	private SLPersistentTreeSession session;
	private Node jcrNode;
	private SLPersistentNode parent;
	private SLPersistentEventPoster eventPoster;
	
	public SLPersistentNodeImpl(SLPersistentTreeSession session, SLPersistentNode parent, Node jcrNode, SLPersistentEventPoster eventPoster) {
		this.session = session;
		this.parent = parent;
		this.jcrNode = jcrNode;
		this.eventPoster = eventPoster;
	}

	@Override
	public SLPersistentTreeSession getSession() {
		return session;
	}

	@Override
	public String getID() throws SLPersistentTreeSessionException {
		try {
			return jcrNode.getUUID();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve the persistent node ID.", e);
		}
	}
	
	@Override
	public String getName() throws SLPersistentTreeSessionException {
		try {
			return jcrNode.getName();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve the persistent node name.", e);
		}
	}

	@Override
	public SLPersistentNode getParent() throws SLPersistentTreeSessionException {
		return parent;
	}

	@Override
	public SLPersistentNode addNode(String name) throws SLPersistentNodeAlreadyExistsException, SLPersistentTreeSessionException {
		SLPersistentNode persistentNode = null;
		try {
			Node jcrChildNode = jcrNode.addNode(name);
			jcrChildNode.addMixin("mix:referenceable");
			persistentNode = new SLPersistentNodeImpl(session, this, jcrChildNode, eventPoster);
			eventPoster.post(new SLPersistentNodeEvent(SLPersistentNodeEvent.TYPE_NODE_ADDED, persistentNode));
		}
		catch (ItemExistsException e) {
			throw new SLPersistentNodeAlreadyExistsException(name);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Couldn't add persistent node " + name, e);
		}
		return persistentNode;
	}

	@Override
	public SLPersistentNode getNode(String name) throws SLPersistentTreeSessionException {
		SLPersistentNode childPersistentNode = null;
		try {
			Node jcrChildNode = jcrNode.getNode(name);
			childPersistentNode = new SLPersistentNodeImpl(session, this, jcrChildNode, eventPoster);
		}
		catch (PathNotFoundException e) {
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Coudn't retrieve persistent node.", e);
		}
		return childPersistentNode;
	}

	@Override
	public void remove() throws SLPersistentTreeSessionException {
		try {
			jcrNode.remove();
			eventPoster.post(new SLPersistentNodeEvent(SLPersistentNodeEvent.TYPE_NODE_REMOVED, this));
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to remove persistent node.", e);
		}
	}

	@Override
	public <V extends Serializable> SLPersistentProperty<V> setProperty(Class<V> clazz, String name, V value) 
		throws SLPersistentTreeSessionException {
		SLPersistentProperty<V> persistentProperty = null;
		try {
			Property jcrProperty;
			Session session = jcrNode.getSession();
			if (value.getClass().isArray()) {
				Value[] jcrValues = JCRUtil.createValues(session, value);
				jcrProperty = jcrNode.setProperty(name, jcrValues);
			}
			else {
				Value jcrValue = JCRUtil.createValue(session, value);
				jcrProperty = jcrNode.setProperty(name, jcrValue);
			}
			persistentProperty = new SLPersistentPropertyImpl<V>(this, clazz, jcrProperty, false, eventPoster);
			eventPoster.post(new SLPersistentPropertyEvent(SLPersistentPropertyEvent.TYPE_PROPERTY_SET, persistentProperty));
		}
		catch (Exception e) {
			throw new SLPersistentTreeSessionException("Error on attempt to set persistent property " + name, e);
		}
		return persistentProperty;
	}
	
	@Override
	public <V extends Serializable> SLPersistentProperty<V> getProperty(Class<V> clazz, String name)
		throws SLPersistentPropertyNotFoundException, SLPersistentTreeSessionException {
		SLPersistentProperty<V> persistentProperty = null;
		try {
			Property jcrProperty = jcrNode.getProperty(name);
			persistentProperty = new SLPersistentPropertyImpl<V>(this, clazz, jcrProperty, true, eventPoster);
		}
		catch (PathNotFoundException e) {
			throw new SLPersistentPropertyNotFoundException(name);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent property " + name, e);
		}
		return persistentProperty;
	}

	@Override
	public Set<SLPersistentProperty<Serializable>> getProperties(String pattern) throws SLPersistentTreeSessionException {
		try {
			Set<SLPersistentProperty<Serializable>> persistentProperties = new HashSet<SLPersistentProperty<Serializable>>();
			PropertyIterator iter = jcrNode.getProperties(pattern);
			while (iter.hasNext()) {
				Property jcrProperty = iter.nextProperty();
				SLPersistentProperty<Serializable> persistentProperty = new SLPersistentPropertyImpl<Serializable>(this, Serializable.class, jcrProperty, true, eventPoster);
				persistentProperties.add(persistentProperty);
			}
			return persistentProperties;
		}
		catch (Exception e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent node properties.", e);
		}
	}

	@Override
	public Set<SLPersistentNode> getNodes() throws SLPersistentTreeSessionException {
		try {
			Set<SLPersistentNode> persistentNodes = new HashSet<SLPersistentNode>();
			NodeIterator iter = jcrNode.getNodes();
			while (iter.hasNext()) {
				Node childNode = iter.nextNode();
				SLPersistentNode childPersistentNode = new SLPersistentNodeImpl(session, this, childNode, eventPoster);
				persistentNodes.add(childPersistentNode);
			}
			return persistentNodes;
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent child nodes.", e);
		}
	}

	@Override
	public void save() throws SLPersistentTreeSessionException {
		try {
			jcrNode.save();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to save persistent node.", e);
		}
	}

	@Override
	public String getPath() throws SLPersistentTreeSessionException {
		try {
			return jcrNode.getPath();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent node path.", e);
		}
	}

	@Override
	public String toString() {
		return jcrNode.toString();
	}
}

