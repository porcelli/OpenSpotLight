package org.openspotlight.graph;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.openspotlight.graph.persistence.SLPersistentEventListener;
import org.openspotlight.graph.persistence.SLPersistentEventListenerImpl;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeFactory;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.util.AbstractFactory;

public class SLGraphFactoryImpl extends SLGraphFactory {

	@Override
	public SLGraph createGraph() throws SLGraphFactoryException {
		try {
			SLPersistentTreeFactory factory = AbstractFactory.getDefaultInstance(SLPersistentTreeFactory.class);
			SLPersistentTree tree = factory.createPersistentTree();
			return new SLGraphImpl(tree);
		}
		catch (Exception e) {
			throw new SLGraphFactoryException("Couldn't create SL graph.", e);
		}
	}

	@Override
	SLGraphSession createGraphSession(SLPersistentTreeSession treeSession) {
		return new SLGraphSessionImpl(treeSession);
	}
	
	@Override
	SLNode createNode(SLContext context, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster) throws SLGraphFactoryException {
		return new SLNodeImpl(context, null, persistentNode, eventPoster);
	}

	@Override
	SLNode createNode(SLContext context, SLNode parent, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster) throws SLGraphFactoryException {
		return new SLNodeImpl(context, parent, persistentNode, eventPoster);
	}
	
	@Override
	<T extends SLNode> T createNode(Class<T> clazz, SLContext context, SLNode parent, SLPersistentNode persistentNode) throws SLGraphFactoryException {
		try {
			Constructor<T> constructor = clazz.getConstructor(SLContext.class, SLNode.class, SLPersistentNode.class);
			return constructor.newInstance(context, parent, persistentNode);
		}
		catch (Exception e) {
			throw new SLGraphFactoryException("Couldn't instantiate node type " + clazz.getName(), e);
		}
	}
	
	@Override
	Class<? extends SLContext> getContextImplClass() throws SLGraphFactoryException {
		return SLContextImpl.class;
	}

	@Override
	<V extends Serializable> SLNodeProperty<V> createProperty(SLNode node, SLPersistentProperty<V> persistentProperty) throws SLGraphFactoryException {
		return new SLNodePropertyImpl<V>(node, persistentProperty);
	}

	@Override
	SLPersistentEventListener getEventListener() {
		return new SLPersistentEventListenerImpl();
		
	}
}

