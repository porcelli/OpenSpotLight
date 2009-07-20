package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Set;

public interface SLLinkOld {
	
	public static final int DIRECTION_AB = 4;	// 100
	public static final int DIRECTION_BA = 2;	// 010
	public static final int DIRECTION_BOTH = 1;	// 001
	public static final int DIRECTION_ANY = DIRECTION_AB | DIRECTION_BA | DIRECTION_BOTH;  
	
	public static final int SIDE_A = 1;
	public static final int SIDE_B = 2;
	

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Class<? extends SLLinkType> getLinkType() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public int getDirection() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param side
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode get(int side) throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getA() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getB() throws SLGraphSessionException;

	/**
	 * 
	 * @param <N>
	 * @param clazz
	 * @param side
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> N get(Class<N> clazz, int side) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <N>
	 * @param clazz
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> N getA(Class<N> clazz) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <N>
	 * @param clazz
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> N getB(Class<N> clazz) throws SLGraphSessionException;
	
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
}

