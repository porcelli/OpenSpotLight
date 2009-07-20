package org.openspotlight.graph;

import java.io.Serializable;

public interface SLNodeProperty<V extends Serializable> extends Serializable {

	/**
	 * 
	 * @return
	 */
	public SLNode getNode();
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException 
	 */
	public String getName() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public V getValue() throws SLInvalidNodePropertyTypeException, SLGraphSessionException;

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public String getValueAsString() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param value
	 * @throws SLGraphSessionException
	 */
	public void setValue(V value) throws SLGraphSessionException;
	
	/**
	 * 
	 * @throws SLGraphSessionException
	 */
	public void remove() throws SLGraphSessionException;
}



