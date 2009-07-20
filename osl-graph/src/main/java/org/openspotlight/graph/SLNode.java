package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public interface SLNode extends Comparable<SLNode> {
	
	/**
	 * 
	 * @return
	 */
	public SLGraphSession getSession();
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLContext getContext();
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public String getID() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public String getName() throws SLGraphSessionException;

	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLNodeTypeNotInExistentHierarchy
	 * @throws SLGraphSessionException
	 */
	public SLNode addNode(String name) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException;

	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @param name
	 * @return
	 * @throws SLNodeTypeNotInExistentHierarchy
	 * @throws SLGraphSessionException
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException;
	
	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @param name
	 * @param linkTypesForLinkDeletion
	 * @param linkTypesForLinkedNodeDeletion
	 * @return
	 * @throws SLNodeTypeNotInExistentHierarchy
	 * @throws SLGraphSessionException
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name, Collection<Class<? extends SLLink>> linkTypesForLinkDeletion, Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException;
	
	/**
	 * 
	 * @throws SLGraphSessionException
	 */
	public void remove() throws SLGraphSessionException;

	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLInvalidNodeTypeException
	 * @throws SLGraphSessionException
	 */
	public SLNode getNode(String name) throws SLInvalidNodeTypeException, SLGraphSessionException;

	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @param name
	 * @return
	 * @throws SLInvalidNodeTypeException
	 * @throws SLGraphSessionException
	 */
	public <T extends SLNode> T getNode(Class<T> clazz, String name) throws SLInvalidNodeTypeException, SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Set<SLNode> getNodes() throws SLGraphSessionException;
	

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getParent() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <V>
	 * @param clazz
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <V extends Serializable> SLNodeProperty<V> setProperty(Class<V> clazz, String name, V value) throws SLGraphSessionException;
	

	/**
	 * 
	 * @param <V>
	 * @param clazz
	 * @param name
	 * @return
	 * @throws SLNodePropertyNotFoundException
	 * @throws SLInvalidNodePropertyTypeException
	 * @throws SLGraphSessionException
	 */
	public <V extends Serializable> SLNodeProperty<V> getProperty(Class<V> clazz, String name) throws SLNodePropertyNotFoundException, SLInvalidNodePropertyTypeException, SLGraphSessionException;

	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLNodePropertyNotFoundException
	 * @throws SLGraphSessionException
	 */
	public String getPropertyValueAsString(String name) throws SLNodePropertyNotFoundException, SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Set<SLNodeProperty<Serializable>> getProperties() throws SLGraphSessionException;

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLLineReference addLineReference() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLLineReference addLineReference(String name) throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLLineReference getLineReference() throws SLGraphSessionException;
		
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLLineReference getLineReference(String name) throws SLGraphSessionException;
}

