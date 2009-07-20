package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.persistence.SLPersistentEventListener;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.util.AbstractFactory;

public abstract class SLGraphFactory extends AbstractFactory {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphFactoryException
	 */
	public abstract SLGraph createGraph() throws SLGraphFactoryException;
	

	/**
	 * 
	 * @param treeSession
	 * @return
	 * @throws SLGraphFactoryException
	 */
	abstract SLGraphSession createGraphSession(SLPersistentTreeSession treeSession) throws SLGraphFactoryException;

	/**
	 * 
	 * @param context
	 * @param persistentNode
	 * @param eventPoster
	 * @return
	 * @throws SLGraphFactoryException
	 */
	abstract SLNode createNode(SLContext context, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster)	throws SLGraphFactoryException;

	/**
	 * 
	 * @param context
	 * @param parent
	 * @param persistentNode
	 * @param eventPoster
	 * @return
	 * @throws SLGraphFactoryException
	 */
	abstract SLNode createNode(SLContext context, SLNode parent, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster) throws SLGraphFactoryException;

	/**
	 * 
	 * @param <T>
	 * @param context
	 * @param clazz
	 * @param parent
	 * @param persistentNode
	 * @return
	 * @throws SLGraphFactoryException
	 */
	abstract <T extends SLNode> T createNode(Class<T> clazz, SLContext context, SLNode parent, SLPersistentNode persistentNode) throws SLGraphFactoryException;

	/**
	 * 
	 * @return
	 * @throws SLGraphFactoryException
	 */
	abstract Class<? extends SLContext> getContextImplClass() throws SLGraphFactoryException;
	
	/**
	 * 
	 * @param <V>
	 * @param SLNode
	 * @param persistentProperty
	 * @return
	 * @throws SLGraphFactoryException
	 */
	abstract <V extends Serializable> SLNodeProperty<V> createProperty(SLNode node, SLPersistentProperty<V> persistentProperty) throws SLGraphFactoryException;
	
	/**
	 * 
	 * @return
	 */
	abstract SLPersistentEventListener getEventListener();
}
