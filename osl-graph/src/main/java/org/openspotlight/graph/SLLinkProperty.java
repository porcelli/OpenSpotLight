package org.openspotlight.graph;

import java.io.Serializable;

public interface SLLinkProperty<V extends Serializable> {
	
	/**
	 * 
	 * @return
	 */
	public SLLink getLink();
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public String getName() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLInvalidLinkPropertyTypeException
	 * @throws SLGraphSessionException
	 */
	public V getValue() throws SLInvalidLinkPropertyTypeException, SLGraphSessionException;

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

