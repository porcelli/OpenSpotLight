package org.openspotlight.graph;

public interface SLGraph {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphException
	 */
	public SLGraphSession openSession() throws SLGraphException;
	
	/**
	 * 
	 */
	public void shutdown();
}
