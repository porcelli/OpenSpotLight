package org.openspotlight.graph;

import java.util.Collection;

public interface SLMetaLinkType extends SLMetaElement {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Class<? extends SLLink> getType() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLMetaLink> getMetalinks() throws SLGraphSessionException;

	/**
	 * 
	 * @param sourceType
	 * @param targetType
	 * @param bidirectional
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLMetaLink> getMetaLinks(Class<? extends SLNode> sourceType, Class<? extends SLNode> targetType, Boolean bidirectional) throws SLGraphSessionException;

}
