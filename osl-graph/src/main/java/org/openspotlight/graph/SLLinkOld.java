/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Set;

/**
 * The Interface SLLinkOld.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLLinkOld {
	
	/** The Constant DIRECTION_AB. */
	public static final int DIRECTION_AB = 4;	// 100
	
	/** The Constant DIRECTION_BA. */
	public static final int DIRECTION_BA = 2;	// 010
	
	/** The Constant DIRECTION_BOTH. */
	public static final int DIRECTION_BOTH = 1;	// 001
	
	/** The Constant DIRECTION_ANY. */
	public static final int DIRECTION_ANY = DIRECTION_AB | DIRECTION_BA | DIRECTION_BOTH;  
	
	/** The Constant SIDE_A. */
	public static final int SIDE_A = 1;
	
	/** The Constant SIDE_B. */
	public static final int SIDE_B = 2;
	

	/**
	 * Gets the link type.
	 * 
	 * @return the link type
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Class<? extends SLLinkType> getLinkType() throws SLGraphSessionException;
	
	/**
	 * Gets the direction.
	 * 
	 * @return the direction
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public int getDirection() throws SLGraphSessionException;
	
	/**
	 * Gets the.
	 * 
	 * @param side the side
	 * 
	 * @return the sL node
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode get(int side) throws SLGraphSessionException;
	
	/**
	 * Gets the a.
	 * 
	 * @return the a
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode getA() throws SLGraphSessionException;
	
	/**
	 * Gets the b.
	 * 
	 * @return the b
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode getB() throws SLGraphSessionException;

	/**
	 * Gets the.
	 * 
	 * @param clazz the clazz
	 * @param side the side
	 * 
	 * @return the n
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> N get(Class<N> clazz, int side) throws SLGraphSessionException;
	
	/**
	 * Gets the a.
	 * 
	 * @param clazz the clazz
	 * 
	 * @return the a
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> N getA(Class<N> clazz) throws SLGraphSessionException;
	
	/**
	 * Gets the b.
	 * 
	 * @param clazz the clazz
	 * 
	 * @return the b
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> N getB(Class<N> clazz) throws SLGraphSessionException;
	
	/**
	 * Sets the property.
	 * 
	 * @param clazz the clazz
	 * @param name the name
	 * @param value the value
	 * 
	 * @return the sL link property< v>
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <V extends Serializable> SLLinkProperty<V> setProperty(Class<V> clazz, String name, V value) throws SLGraphSessionException;

	/**
	 * Gets the property.
	 * 
	 * @param clazz the clazz
	 * @param name the name
	 * 
	 * @return the property
	 * 
	 * @throws SLLinkPropertyNotFoundException the SL link property not found exception
	 * @throws SLInvalidLinkPropertyTypeException the SL invalid link property type exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <V extends Serializable> SLLinkProperty<V> getProperty(Class<V> clazz, String name) throws SLLinkPropertyNotFoundException, SLInvalidLinkPropertyTypeException, SLGraphSessionException;
	
	/**
	 * Gets the property value as string.
	 * 
	 * @param name the name
	 * 
	 * @return the property value as string
	 * 
	 * @throws SLLinkPropertyNotFoundException the SL link property not found exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public String getPropertyValueAsString(String name) throws SLLinkPropertyNotFoundException, SLGraphSessionException;
	
	/**
	 * Gets the properties.
	 * 
	 * @return the properties
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Set<SLLinkProperty<Serializable>> getProperties() throws SLGraphSessionException;
}

