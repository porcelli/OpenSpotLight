package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Set;

public interface SLLink extends Comparable<SLLink> {
	
	public static final int SIDE_SOURCE = 4;							// 100
	public static final int SIDE_TARGET = 2;							// 010
	public static final int SIDE_BOTH = SIDE_SOURCE | SIDE_TARGET;		// 001
	
	public static final int DIRECTION_UNI = 4;							// 100
	public static final int DIRECTION_UNI_REVERSAL = 2;					// 010
	public static final int DIRECTION_BI = 1;							// 001
	
	public static final int DIRECTION_ANY = DIRECTION_UNI | DIRECTION_UNI_REVERSAL | DIRECTION_BI;				
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public String getID() throws SLGraphSessionException;
	
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
	public SLNode getOtherSide(SLNode side) throws SLInvalidLinkSideException, SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode[] getSides() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public boolean isBidirectional() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <V>
	 * @param clazz
	 * @param name
	 * @param value
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <V extends Serializable> SLLinkProperty<V> setProperty(Class<V> clazz, String name, V value) throws SLGraphSessionException;

	/**
	 * 
	 * @param <V>
	 * @param clazz
	 * @param name
	 * @return
	 * @throws SLLinkPropertyNotFoundException
	 * @throws SLInvalidLinkPropertyTypeException
	 * @throws SLGraphSessionException
	 */
	public <V extends Serializable> SLLinkProperty<V> getProperty(Class<V> clazz, String name) throws SLLinkPropertyNotFoundException, SLInvalidLinkPropertyTypeException, SLGraphSessionException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLLinkPropertyNotFoundException
	 * @throws SLGraphSessionException
	 */
	public String getPropertyValueAsString(String name) throws SLLinkPropertyNotFoundException, SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Set<SLLinkProperty<Serializable>> getProperties() throws SLGraphSessionException;
	
	/**
	 * 
	 * @throws SLGraphSessionException
	 */
	public void remove() throws SLGraphSessionException;
}
