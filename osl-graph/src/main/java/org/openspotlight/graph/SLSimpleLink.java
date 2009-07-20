package org.openspotlight.graph;


public interface SLSimpleLink extends SLLink {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public boolean isBidirectional() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getSource() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getTarget() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param side
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getOtherSide(SLNode side) throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode[] getSides() throws SLGraphSessionException;
}
