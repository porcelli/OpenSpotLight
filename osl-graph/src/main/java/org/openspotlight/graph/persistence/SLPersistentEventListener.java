package org.openspotlight.graph.persistence;

public interface SLPersistentEventListener {
	
	/**
	 * 
	 * @param event
	 * @throws SLPersistentTreeSessionException
	 */
	public void nodeAdded(SLPersistentNodeEvent event) throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLPersistentTreeSessionException
	 */
	public void nodeRemoved(SLPersistentNodeEvent event) throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLPersistentTreeSessionException
	 */
	public void propertySet(SLPersistentPropertyEvent event) throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLPersistentTreeSessionException
	 */
	public void propertyRemoved(SLPersistentPropertyEvent event) throws SLPersistentTreeSessionException;
}
