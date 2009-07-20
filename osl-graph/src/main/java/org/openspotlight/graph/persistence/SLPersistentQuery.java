package org.openspotlight.graph.persistence;

public interface SLPersistentQuery {
	
	public static final int TYPE_XPATH = 1;
	public static final int TYPE_SQL = 2;

	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeSessionException
	 */
	public SLPersistentQueryResult execute() throws SLPersistentTreeSessionException;
	
	/**
	 * 
	 * @return
	 */
	public String getStatement();
	
	/**
	 * 
	 * @return
	 */
	public int getType();
}
