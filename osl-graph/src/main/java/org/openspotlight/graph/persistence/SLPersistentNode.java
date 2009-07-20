package org.openspotlight.graph.persistence;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.graph.SLGraphSessionException;

public interface SLPersistentNode {
	
	/**
	 * 
	 * @return
	 */
	public SLPersistentTreeSession getSession();

	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public String getID() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public String getName() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentNode getParent() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLPersistentTreeException
	 */
	public SLPersistentNode addNode(String name) throws SLPersistentNodeAlreadyExistsException, SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentNode getNode(String name) throws SLPersistentTreeSessionException;

	/**
	 * 
	 * @throws SLPersistentTreeSessionException
	 */
	public void remove() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param <V>
	 * @param name
	 * @param value
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public <V extends Serializable> SLPersistentProperty<V> setProperty(Class<V> clazz, String name, V value) throws SLPersistentTreeSessionException;
	
	
	/**
	 * 
	 * @param <V>
	 * @param clazz
	 * @param name
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public <V extends Serializable> SLPersistentProperty<V> getProperty(Class<V> clazz, String name) throws SLPersistentPropertyNotFoundException, SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Set<SLPersistentProperty<Serializable>> getProperties(String pattern) throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public Set<SLPersistentNode> getNodes() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @throws SLPersistentTreeSessionException
	 */
	public void save() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public String getPath() throws SLPersistentTreeSessionException;
	
}

