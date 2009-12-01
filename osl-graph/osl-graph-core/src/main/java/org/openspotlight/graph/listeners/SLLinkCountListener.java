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
package org.openspotlight.graph.listeners;

import static org.openspotlight.graph.SLCommonSupport.getLinkType;

import org.openspotlight.graph.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLLinkEvent;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving SLLinkCount events.
 * The class that is interested in processing a SLLinkCount
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSLLinkCountListener<code> method. When
 * the SLLinkCount event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLLinkCountEvent
 */
public class SLLinkCountListener extends SLAbstractGraphSessionEventListener {
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkAdded(org.openspotlight.graph.SLLinkEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException {
		try {
			SLLink link = event.getLink();
			Class<? extends SLLink> linkType = getLinkType(link);
			SLPersistentNode sourceNode = SLCommonSupport.getPNode(event.getSource());
			SLPersistentNode targetNode = SLCommonSupport.getPNode(event.getTarget());
			if (event.isNewLink()) {
				if (link.isBidirectional()) {
					addSourceCount(sourceNode, linkType, 1);
					addSourceCount(targetNode, linkType, 1);
					addTargetCount(sourceNode, linkType, 1);
					addTargetCount(targetNode, linkType, 1);
				}
				else {
					addSourceCount(sourceNode, linkType, 1);
					addTargetCount(targetNode, linkType, 1);
				}
			}
			else if (event.isChangedToBidirectional()) {
				addSourceCount(sourceNode, linkType, 1);
				addTargetCount(targetNode, linkType, 1);
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to add link count data.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkRemoved(org.openspotlight.graph.SLLinkEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	public void linkRemoved(SLLinkEvent event) throws SLGraphSessionException {
		try {
			SLLink link = event.getLink();
			Class<? extends SLLink> linkType = link.getLinkType();
			if (event.isBidirectional()) {
				SLPersistentNode sideNode1 = SLCommonSupport.getPNode(event.getSides()[0]);
				SLPersistentNode sideNode2 = SLCommonSupport.getPNode(event.getSides()[1]);
				addSourceCount(sideNode1, linkType, -1);
				addSourceCount(sideNode2, linkType, -1);
				addTargetCount(sideNode1, linkType, -1);
				addTargetCount(sideNode2, linkType, -1);
			}
			else {
				SLPersistentNode sourceNode = SLCommonSupport.getPNode(event.getSource());
				SLPersistentNode targetNode = SLCommonSupport.getPNode(event.getTarget());
				addSourceCount(sourceNode, linkType, -1);
				addTargetCount(targetNode, linkType, -1);
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove link count data.", e);
		}
	}
	
	/**
	 * Adds the source count.
	 * 
	 * @param pNode the node
	 * @param linkType the link type
	 * @param n the n
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private void addSourceCount(SLPersistentNode pNode, Class<? extends SLLink> linkType, int n) throws SLPersistentTreeSessionException {
		String sourceLinkCountName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_COUNT + "." + linkType.getName().hashCode());
		SLPersistentProperty<Integer> prop = SLCommonSupport.getProperty(pNode, Integer.class, sourceLinkCountName);
		Integer sourceLinkCount = prop == null ? null : prop.getValue();
		sourceLinkCount = sourceLinkCount == null ? 1 : sourceLinkCount + n;
		if (sourceLinkCount <= 0) prop.remove();
		else pNode.setProperty(Integer.class, sourceLinkCountName, sourceLinkCount); 
	}
	
	/**
	 * Adds the target count.
	 * 
	 * @param pNode the node
	 * @param linkType the link type
	 * @param n the n
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private void addTargetCount(SLPersistentNode pNode, Class<? extends SLLink> linkType, int n) throws SLPersistentTreeSessionException {
		String targetLinkCountName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_COUNT + "." + linkType.getName().hashCode());
		SLPersistentProperty<Integer> prop = SLCommonSupport.getProperty(pNode, Integer.class, targetLinkCountName);
		Integer targetLinkCount = prop == null ? null : prop.getValue();
		targetLinkCount = targetLinkCount == null ? 1 : targetLinkCount + n;
		if (targetLinkCount <= 0) prop.remove();
		else pNode.setProperty(Integer.class, targetLinkCountName, targetLinkCount);
	}
}




