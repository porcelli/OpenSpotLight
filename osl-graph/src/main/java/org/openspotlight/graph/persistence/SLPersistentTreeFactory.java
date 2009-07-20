package org.openspotlight.graph.persistence;

import org.openspotlight.graph.util.AbstractFactory;

public abstract class SLPersistentTreeFactory extends AbstractFactory {
	
	/**
	 * 
	 * @return
	 * @throws SLPersistentTreeFactoryException
	 */
	public abstract SLPersistentTree createPersistentTree() throws SLPersistentTreeFactoryException;
}
