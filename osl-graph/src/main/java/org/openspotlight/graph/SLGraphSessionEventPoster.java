package org.openspotlight.graph;

public interface SLGraphSessionEventPoster {
	
	/**
	 * 
	 * @param event
	 * @throws SLGraphSessionException
	 */
	public void post(SLGraphSessionEvent event) throws SLGraphSessionException;

	/**
	 * 
	 * @param event
	 * @throws SLGraphSessionException
	 */
	public void post(SLNodeEvent event) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLGraphSessionException
	 */
	public void post(SLLinkEvent event) throws SLGraphSessionException;
}
