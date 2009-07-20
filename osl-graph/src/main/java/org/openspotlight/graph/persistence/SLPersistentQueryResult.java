package org.openspotlight.graph.persistence;

import java.util.Collection;

public interface SLPersistentQueryResult {

	/**
	 * 
	 * @return
	 */
	public Collection<SLPersistentNode> getNodes();
	
	/**
	 * 
	 * @return
	 */
	public int getRowCount();
}
