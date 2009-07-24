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
import java.text.Collator;
import java.util.Set;

/**
 * The Interface SLLink.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLLink extends Comparable<SLLink> {
	
	/** The Constant SIDE_SOURCE. */
	public static final int SIDE_SOURCE = 4;							// 100
	
	/** The Constant SIDE_TARGET. */
	public static final int SIDE_TARGET = 2;							// 010
	
	/** The Constant SIDE_BOTH. */
	public static final int SIDE_BOTH = SIDE_SOURCE | SIDE_TARGET;		// 001
	
	/** The Constant DIRECTION_UNI. */
	public static final int DIRECTION_UNI = 4;							// 100
	
	/** The Constant DIRECTION_UNI_REVERSAL. */
	public static final int DIRECTION_UNI_REVERSAL = 2;					// 010
	
	/** The Constant DIRECTION_BI. */
	public static final int DIRECTION_BI = 1;							// 001
	
	/** The Constant DIRECTION_ANY. */
	public static final int DIRECTION_ANY = DIRECTION_UNI | DIRECTION_UNI_REVERSAL | DIRECTION_BI;
	
	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	public SLGraphSession getSession();
	
	/**
	 * Gets the iD.
	 * 
	 * @return the iD
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public String getID() throws SLGraphSessionException;
	
	/**
	 * Gets the source.
	 * 
	 * @return the source
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode getSource() throws SLGraphSessionException;
	
	/**
	 * Gets the target.
	 * 
	 * @return the target
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode getTarget() throws SLGraphSessionException;
	
	/**
	 * Gets the other side.
	 * 
	 * @param side the side
	 * 
	 * @return the other side
	 * 
	 * @throws SLInvalidLinkSideException the SL invalid link side exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode getOtherSide(SLNode side) throws SLInvalidLinkSideException, SLGraphSessionException;
	
	/**
	 * Gets the sides.
	 * 
	 * @return the sides
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode[] getSides() throws SLGraphSessionException;
	
	/**
	 * Checks if is bidirectional.
	 * 
	 * @return true, if is bidirectional
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public boolean isBidirectional() throws SLGraphSessionException;
	
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
	 * Gets the property.
	 * 
	 * @param clazz the clazz
	 * @param name the name
	 * @param collator the collator
	 * 
	 * @return the property
	 * 
	 * @throws SLLinkPropertyNotFoundException the SL link property not found exception
	 * @throws SLInvalidLinkPropertyTypeException the SL invalid link property type exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <V extends Serializable> SLLinkProperty<V> getProperty(Class<V> clazz, String name, Collator collator)
		throws SLLinkPropertyNotFoundException, SLInvalidLinkPropertyTypeException, SLGraphSessionException;

	
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
	
	/**
	 * Removes the.
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void remove() throws SLGraphSessionException;
}
