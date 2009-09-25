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
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.ProxyUtil;

/**
 * The Class SLLinkImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLLinkImpl implements SLLink {
	
	/** The session. */
	private SLGraphSession session;
	
	/** The link node. */
	private SLPersistentNode linkNode;
	
	/** The event poster. */
	private SLGraphSessionEventPoster eventPoster;
	
	/**
	 * Instantiates a new sL link impl.
	 * 
	 * @param session the session
	 * @param linkNode the link node
	 * @param eventPoster the event poster
	 */
	public SLLinkImpl(SLGraphSession session, SLPersistentNode linkNode, SLGraphSessionEventPoster eventPoster) {
		this.session = session;
		this.linkNode = linkNode;
		this.eventPoster = eventPoster;
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getLinkType()
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends SLLink> getLinkType() throws SLGraphSessionException {
		try {
			return (Class<? extends SLLink>) Class.forName(getLinkClassNode().getName());	
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link type.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getSession()
	 */
	public SLGraphSession getSession() {
		return session;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getID()
	 */
	public String getID() throws SLGraphSessionException {
		try {
			return linkNode.getID();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link ID.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getOtherSide(org.openspotlight.graph.SLNode)
	 */
	public SLNode getOtherSide(SLNode side) throws SLInvalidLinkSideException, SLGraphSessionException {
		SLNode otherSide = null;
		try {
			SLNode aNode = getANode();
			SLNode bNode = getBNode();
			if (aNode.equals(bNode) && aNode.equals(side)) {
				otherSide = side;
			}
			else {
				if (side.equals(aNode)) otherSide = bNode;
				else if (side.equals(bNode)) otherSide = aNode;
			}
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link other side.", e);
		}
		if (otherSide == null) throw new SLInvalidLinkSideException();
		return otherSide;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getSides()
	 */
	public SLNode[] getSides() throws SLGraphSessionException {
		try {
			SLNode a = getANode();
			SLNode b = getBNode();
			return new SLNode[] {a, b};
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link sides.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getSource()
	 */
	public SLNode getSource() throws SLGraphSessionException {
		if (isBidirectional()) {
			// this method cannot be used on bidirecional links, because source and targets are relatives.
			// on unidirecional links, source and target are well defined.
			throw new UnsupportedOperationException("SLLink.getSource() cannot be used on bidirecional links.");
		}
		try {
			return getDirection() == SLConsts.DIRECTION_AB ? getANode() : getBNode();
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link source.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getTarget()
	 */
	public SLNode getTarget() throws SLGraphSessionException {
		if (isBidirectional()) {
			// this method cannot be used on bidirecional links, because source and targets are relatives.
			// on unidirecional links, source and target are well defined.
			throw new UnsupportedOperationException("SLLink.getTarget() cannot be used on bidirecional links.");
		}
		try {
			return getDirection() == SLConsts.DIRECTION_AB ? getBNode() : getANode();
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link source.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#isBidirectional()
	 */
	public boolean isBidirectional() throws SLGraphSessionException {
		try {
			return getDirection() == SLConsts.DIRECTION_BOTH;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to verify if link is bidirectional.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		try {
			if (obj == null || !(obj instanceof SLLink)) return false;
			SLPersistentNode classNode1 = getLinkClassNode();
			SLPersistentNode pairNode1 = getPairKeyNode();
			String name1 = classNode1.getName().concat(pairNode1.getName());
			SLLinkImpl link = (SLLinkImpl) ProxyUtil.getLinkFromProxy(obj);
			SLPersistentNode classNode2 = link.getLinkClassNode();
			SLPersistentNode pairNode2 = link.getPairKeyNode();
			String name2 = classNode2.getName().concat(pairNode2.getName());
			return name1.equals(name2);
		}
		catch (SLException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkImpl.equals().", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		try {
			return getID().hashCode();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkImpl.hasCode().", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#setProperty(java.lang.Class, java.lang.String, java.io.Serializable)
	 */
	public <V extends Serializable> SLLinkProperty<V> setProperty(Class<V> clazz, String name, V value) throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<V> pProperty = linkNode.setProperty(clazz, propName, value);
			SLLink linkProxy = ProxyUtil.createLinkProxy(getLinkType(), this);
			SLLinkProperty<V> property = new SLLinkPropertyImpl<V>(linkProxy, pProperty);
			SLLinkPropertyEvent event = new SLLinkPropertyEvent(SLLinkPropertyEvent.TYPE_LINK_PROPERTY_SET, property, pProperty);
			eventPoster.post(event);
			return property; 
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to set link property.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getProperty(java.lang.Class, java.lang.String)
	 */
	public <V extends Serializable> SLLinkProperty<V> getProperty(Class<V> clazz, String name)
		throws SLLinkPropertyNotFoundException, SLInvalidLinkPropertyTypeException, SLGraphSessionException {
		return getProperty(clazz, name, null);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getProperty(java.lang.Class, java.lang.String, java.text.Collator)
	 */
	public <V extends Serializable> SLLinkProperty<V> getProperty(Class<V> clazz, String name, Collator collator)
		throws SLLinkPropertyNotFoundException, SLInvalidLinkPropertyTypeException, SLGraphSessionException {
		
		SLLinkProperty<V> property = null;
		
		try {
			
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<V> pProperty = SLCommonSupport.getProperty(linkNode, clazz, propName);
			
			// if property not found find collator if its strength is not identical ...
			if (pProperty == null) {
				Class<? extends SLLink> nodeType = getLinkType(); 
				if (nodeType != null) {
					Set<SLPersistentProperty<Serializable>> pProperties = linkNode.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
					for (SLPersistentProperty<Serializable> current : pProperties) {
						String currentName = SLCommonSupport.toSimplePropertyName(current.getName());
						Collator currentCollator = collator == null ? SLCollatorSupport.getPropertyCollator(nodeType, currentName) : collator;		
						if (currentCollator.compare(name, currentName) == 0) {
							pProperty = linkNode.getProperty(clazz, current.getName());
							break;
						}
					}
				}
			}
			
			if (pProperty != null) {
				SLLink linkProxy = ProxyUtil.createLinkProxy(getLinkType(), this);
				property = new SLLinkPropertyImpl<V>(linkProxy, pProperty);
			}
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw new SLInvalidNodePropertyTypeException(e);
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link property.", e);
		}
		
		if (property == null) throw new SLNodePropertyNotFoundException(name);
		return property;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getPropertyValueAsString(java.lang.String)
	 */
	public String getPropertyValueAsString(String name) throws SLLinkPropertyNotFoundException, SLGraphSessionException {
		return getProperty(Serializable.class, name).getValue().toString();
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#getProperties()
	 */
	public Set<SLLinkProperty<Serializable>> getProperties() throws SLGraphSessionException {
		try {
			Set<SLLinkProperty<Serializable>> properties = new HashSet<SLLinkProperty<Serializable>>();
			Set<SLPersistentProperty<Serializable>> persistentProperties = linkNode.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
			for (SLPersistentProperty<Serializable> persistentProperty : persistentProperties) {
				SLLink linkProxy = ProxyUtil.createLinkProxy(getLinkType(), this);	
				SLLinkProperty<Serializable> property = new SLLinkPropertyImpl<Serializable>(linkProxy, persistentProperty);
				properties.add(property);
			}
			return properties;
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node properties.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return linkNode.toString();
	}

	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SLLink l) {
		try {
			SLLinkImpl link = (SLLinkImpl) ProxyUtil.getLinkFromProxy(l);
			String linkClassName1 = getLinkClassNode().getName();
			String linkClassName2 = link.getLinkClassNode().getName();
			if (linkClassName1.equals(linkClassName2)) {
				String pairName1 = getPairKeyNode().getName();
				String pairName2 = link.getPairKeyNode().getName();
				if (pairName1.equals(pairName2)) {
					Long linkCount1 = linkNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT).getValue();
					Long linkCount2 = link.linkNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT).getValue();
					return linkCount1.compareTo(linkCount2);
				}
				else {
					return pairName1.compareTo(pairName2);
				}
			}
			else {
				return linkClassName1.compareTo(linkClassName2);
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkImpl.compareTo().", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLLink#remove()
	 */
	public void remove() throws SLGraphSessionException {
		try {
			SLLinkEvent event = new SLLinkEvent(SLLinkEvent.TYPE_LINK_REMOVED, this);
			event.setBidirectional(isBidirectional());
			if (event.isBidirectional()) {
				event.setSides(getSides());
			}
			else {
				event.setSource(getSource());
				event.setTarget(getTarget());
			}
			linkNode.remove();
			this.eventPoster.post(event);
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to remove link.", e);
		}
	}

	/**
	 * Gets the direction.
	 * 
	 * @return the direction
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private int getDirection() throws SLPersistentTreeSessionException {
		SLPersistentProperty<Integer> directionProp = linkNode.getProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION);
		return directionProp.getValue();
	}
	
	/**
	 * Gets the a node.
	 * 
	 * @return the a node
	 * 
	 * @throws SLException the SL exception
	 */
	private SLNode getANode() throws SLException {
		SLPersistentNode pairKeyNode = getPairKeyNode();
		SLPersistentProperty<String> nodeIDProp = pairKeyNode.getProperty(String.class, SLConsts.PROPERTY_NAME_A_NODE_ID);
		return session.getNodeByID(nodeIDProp.getValue());
	}

	/**
	 * Gets the b node.
	 * 
	 * @return the b node
	 * 
	 * @throws SLException the SL exception
	 */
	private SLNode getBNode() throws SLException {
		SLPersistentNode pairKeyNode = getPairKeyNode();
		SLPersistentProperty<String> nodeIDProp = pairKeyNode.getProperty(String.class, SLConsts.PROPERTY_NAME_B_NODE_ID);
		return session.getNodeByID(nodeIDProp.getValue());
	}

	/**
	 * Gets the pair key node.
	 * 
	 * @return the pair key node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private SLPersistentNode getPairKeyNode() throws SLPersistentTreeSessionException {
		return linkNode.getParent();
	}
	
	/**
	 * Gets the link class node.
	 * 
	 * @return the link class node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private SLPersistentNode getLinkClassNode() throws SLPersistentTreeSessionException {
		return getPairKeyNode().getParent();
	}
	
}

