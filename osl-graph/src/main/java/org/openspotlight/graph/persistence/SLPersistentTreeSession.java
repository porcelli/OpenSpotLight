package org.openspotlight.graph.persistence;


public interface SLPersistentTreeSession {
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentNode getRootNode() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 */
	public void close();
	
	/**
	 * 
	 * @throws SLPersistentTreeSessionException
	 */
	public void save() throws SLPersistentTreeSessionException;

	/**
	 * 
	 * @throws SLPersistentTreeSessionException
	 */
	public void clear() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @param statement
	 * @param type
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentQuery createQuery(String statement, int type) throws SLPersistentTreeSessionException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentNode getNodeByID(String id) throws SLPersistentNodeNotFoundException, SLPersistentTreeSessionException;

	/**
	 * 
	 * @param path
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentNode getNodeByPath(String path) throws SLPersistentTreeSessionException;
}
