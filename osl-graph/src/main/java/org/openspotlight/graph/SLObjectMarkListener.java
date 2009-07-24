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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The listener interface for receiving SLObjectMark events.
 * The class that is interested in processing a SLObjectMark
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSLObjectMarkListener<code> method. When
 * the SLObjectMark event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLObjectMarkEvent
 * @author Vitor Hugo Chagas
 */
public class SLObjectMarkListener extends SLAbstractGraphSessionEventListener {
	
	/** The links for deletion. */
	private Set<SLLink> linksForDeletion;
	
	/** The nodes for deletion. */
	private Set<SLNode> nodesForDeletion;
	
	/**
	 * Instantiates a new sL object mark listener.
	 */
	public SLObjectMarkListener() {
		linksForDeletion = new HashSet<SLLink>();
		nodesForDeletion = new HashSet<SLNode>();
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodeAdded(org.openspotlight.graph.SLNodeEvent)
	 */
	public void nodeAdded(SLNodeEvent event) throws SLGraphSessionException {
		
		SLGraphSession session = event.getSession();
		SLNode node = event.getNode();
		Collection<Class<? extends SLLink>> linkTypesForLinkDeletion = event.getLinkTypesForLinkDeletion();
		Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion = event.getLinkTypesForLinkedNodesDeletion();

		if (linkTypesForLinkDeletion != null) {
			// mark for deletion links that have the added node as side ... 
			for (Class<? extends SLLink> linkType : linkTypesForLinkDeletion) {
				Collection<? extends SLLink> links = session.getLinks(linkType, node, null, SLLink.DIRECTION_ANY);
				linksForDeletion.addAll(links);
			}
		}

		if (linkTypesForLinkedNodeDeletion != null) {
			// mark for deletion all the nodes linked to this node ...
	 		for (Class<? extends SLLink> linkType : linkTypesForLinkedNodeDeletion) {
				Collection<SLNode> nodes = session.getNodesByLink(linkType, node);
				nodesForDeletion.addAll(nodes);
			}
		}
		
		// unmark the added node (if it's present in the set) ...
		nodesForDeletion.remove(node);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkAdded(org.openspotlight.graph.SLLinkEvent)
	 */
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException {
		// unmark link and its sides ...
		SLLink link = event.getLink();
		SLNode[] sides = link.getSides();
		linksForDeletion.remove(link);
		nodesForDeletion.remove(sides[0]);
		nodesForDeletion.remove(sides[1]);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#beforeSave(org.openspotlight.graph.SLGraphSessionEvent)
	 */
	public void beforeSave(SLGraphSessionEvent event) throws SLGraphSessionException {
		
		// delete links ...
		for (SLLink link : linksForDeletion) {
			link.remove();
		}
		
		// delete nodes ...
		for (SLNode node : nodesForDeletion) {
			node.remove();
		}
	}
}
