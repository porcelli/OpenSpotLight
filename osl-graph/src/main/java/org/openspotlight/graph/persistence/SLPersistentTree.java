package org.openspotlight.graph.persistence;

public interface SLPersistentTree {
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeException
	 */
	public SLPersistentTreeSession openSession() throws SLPersistentTreeException;
	
	/**
	 * 
	 */
	public void shutdown();

}
