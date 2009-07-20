package org.openspotlight.graph.persistence;


public interface SLPersistentEventPoster {

	/**
	 * 
	 * @param event
	 * @throws SLPersistentTreeSessionException
	 */
	public void post(SLPersistentNodeEvent event) throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLPersistentTreeSessionException
	 */
	public void post(SLPersistentPropertyEvent event) throws SLPersistentTreeSessionException;
	
}
