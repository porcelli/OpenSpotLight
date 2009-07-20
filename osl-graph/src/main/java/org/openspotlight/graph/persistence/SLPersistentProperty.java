package org.openspotlight.graph.persistence;

import java.io.Serializable;

import org.openspotlight.graph.SLGraphSessionException;

public interface SLPersistentProperty<V extends Serializable> {

	/**
	 * 
	 * @return
	 */
	public SLPersistentNode getNode();

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
	public V getValue() throws SLInvalidPersistentPropertyTypeException, SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param value
	 * @throws SLGraphSessionException
	 */
	public void setValue(V value) throws SLPersistentTreeSessionException;

	/**
	 * 
	 * @throws SLPersistentTreeSessionException
	 */
	public void remove() throws SLPersistentTreeSessionException;
	
}

