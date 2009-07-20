package org.openspotlight.graph;

import java.util.Collection;
import java.util.List;

public interface SLMetaLink extends SLMetaElement {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaLinkType getMetaLinkType() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Class<? extends SLNode> getSourceType() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Class<? extends SLNode> getTargetType() throws SLGraphSessionException;

	/**
	 * 
	 * @param sideType
	 * @return
	 * @throws SLInvalidMetaLinkSideTypeException
	 * @throws SLGraphSessionException
	 */
	public Class<? extends SLNode> getOtherSideType(Class<? extends SLNode> sideType) throws SLInvalidMetaLinkSideTypeException, SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public List<Class<? extends SLNode>> getSideTypes() throws SLGraphSessionException;
	
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
	public Collection<SLMetaLinkProperty> getMetaProperties() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaLinkProperty getMetaProperty(String name) throws SLGraphSessionException;
}

