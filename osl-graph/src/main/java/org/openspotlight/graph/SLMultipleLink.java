package org.openspotlight.graph;

import java.util.Collection;

public interface SLMultipleLink extends SLLink {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public abstract Collection<SLSimpleLink> getLinks() throws SLGraphSessionException;
}
