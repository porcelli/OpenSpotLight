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

/**
 * The Interface SLGraphSession.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLGraphSession {
	
	/**
	 * Creates the context.
	 * 
	 * @param id the id
	 * 
	 * @return the sL context
	 * 
	 * @throws SLContextAlreadyExistsException the SL context already exists exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLContext createContext(Long id) throws SLContextAlreadyExistsException, SLGraphSessionException;
	
	/**
	 * Gets the context.
	 * 
	 * @param id the id
	 * 
	 * @return the context
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLContext getContext(Long id) throws SLGraphSessionException;
	
	/**
	 * Save.
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void save() throws SLGraphSessionException;
	
	/**
	 * Close.
	 */
	public void close();

	/**
	 * Clear.
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public void clear() throws SLGraphSessionException;

	/**
	 * Gets the node by id.
	 * 
	 * @param id the id
	 * 
	 * @return the node by id
	 * 
	 * @throws SLNodeNotFoundException the SL node not found exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLNode getNodeByID(String id) throws SLNodeNotFoundException, SLGraphSessionException;
	
	/**
	 * Adds the link.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * @param bidirecional the bidirecional
	 * 
	 * @return the l
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> L addLink(Class<L> linkClass, SLNode source, SLNode target, boolean bidirecional) throws SLGraphSessionException;
	
	
	/**
	 * Adds the link.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * @param bidirecional the bidirecional
	 * @param persistenceMode the persistence mode
	 * 
	 * @return the l
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> L addLink(Class<L> linkClass, SLNode source, SLNode target, boolean bidirecional, SLPersistenceMode persistenceMode) throws SLGraphSessionException;
	
	/**
	 * Gets the nodes by predicate.
	 * 
	 * @param predicate the predicate
	 * 
	 * @return the nodes by predicate
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLNode> getNodesByPredicate(SLNodePredicate predicate) throws SLGraphSessionException;
	
	/**
	 * Gets the nodes by link.
	 * 
	 * @param node the node
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLNode> getNodesByLink(SLNode node) throws SLGraphSessionException;

	/**
	 * Gets the nodes by link.
	 * 
	 * @param node the node
	 * @param direction the direction
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLNode> getNodesByLink(SLNode node, int direction) throws SLGraphSessionException;
		
	/**
	 * Gets the nodes by link.
	 * 
	 * @param node the node
	 * @param nodeClass the node class
	 * @param returnSubTypes the return sub types
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException;
	
	/**
	 * Gets the nodes by link.
	 * 
	 * @param node the node
	 * @param nodeClass the node class
	 * @param returnSubTypes the return sub types
	 * @param direction the direction
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes, int direction) throws SLGraphSessionException;

	/**
	 * Gets the nodes by link.
	 * 
	 * @param linkClass the link class
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass) throws SLGraphSessionException;

	/**
	 * Gets the nodes by link.
	 * 
	 * @param linkClass the link class
	 * @param node the node
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node) throws SLGraphSessionException;
	
	/**
	 * Gets the nodes by link.
	 * 
	 * @param linkClass the link class
	 * @param node the node
	 * @param direction the direction
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, int direction) throws SLGraphSessionException;

	/**
	 * Gets the nodes by link.
	 * 
	 * @param linkClass the link class
	 * @param node the node
	 * @param nodeClass the node class
	 * @param returnSubTypes the return sub types
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException;
	
	/**
	 * Gets the nodes by link.
	 * 
	 * @param linkClass the link class
	 * @param node the node
	 * @param nodeClass the node class
	 * @param returnSubTypes the return sub types
	 * @param direction the direction
	 * 
	 * @return the nodes by link
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes, int direction) throws SLGraphSessionException;

	/**
	 * Gets the bidirectional links.
	 * 
	 * @param linkClass the link class
	 * @param side1 the side1
	 * @param side2 the side2
	 * 
	 * @return the bidirectional links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getBidirectionalLinks(Class<L> linkClass, SLNode side1, SLNode side2) throws SLGraphSessionException;

	/**
	 * Gets the bidirectional links by side.
	 * 
	 * @param linkClass the link class
	 * @param side the side
	 * 
	 * @return the bidirectional links by side
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getBidirectionalLinksBySide(Class<L> linkClass, SLNode side) throws SLGraphSessionException;

	/**
	 * Gets the bidirectional links.
	 * 
	 * @param side1 the side1
	 * @param side2 the side2
	 * 
	 * @return the bidirectional links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getBidirectionalLinks(SLNode side1, SLNode side2) throws SLGraphSessionException;
	
	/**
	 * Gets the bidirectional links by side.
	 * 
	 * @param side the side
	 * 
	 * @return the bidirectional links by side
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getBidirectionalLinksBySide(SLNode side) throws SLGraphSessionException;
	
	/**
	 * Gets the unidirectional links.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the unidirectional links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException;

	/**
	 * Gets the unidirectional links by source.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * 
	 * @return the unidirectional links by source
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinksBySource(Class<L> linkClass, SLNode source) throws SLGraphSessionException;

	/**
	 * Gets the unidirectional links by target.
	 * 
	 * @param linkClass the link class
	 * @param target the target
	 * 
	 * @return the unidirectional links by target
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinksByTarget(Class<L> linkClass, SLNode target) throws SLGraphSessionException;

	/**
	 * Gets the unidirectional links.
	 * 
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the unidirectional links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getUnidirectionalLinks(SLNode source, SLNode target) throws SLGraphSessionException;

	/**
	 * Gets the unidirectional links by source.
	 * 
	 * @param source the source
	 * 
	 * @return the unidirectional links by source
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getUnidirectionalLinksBySource(SLNode source) throws SLGraphSessionException;

	/**
	 * Gets the unidirectional links by target.
	 * 
	 * @param target the target
	 * 
	 * @return the unidirectional links by target
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getUnidirectionalLinksByTarget(SLNode target) throws SLGraphSessionException;
	
	/**
	 * Gets the links.
	 * 
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getLinks(SLNode source, SLNode target) throws SLGraphSessionException;
	
	/**
	 * Gets the links.
	 * 
	 * @param source the source
	 * @param target the target
	 * @param directionType the direction type
	 * 
	 * @return the links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public Collection<SLLink> getLinks(SLNode source, SLNode target, int directionType) throws SLGraphSessionException;
	
	/**
	 * Gets the links.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException;
	
	/**
	 * Gets the links.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * @param directionType the direction type
	 * 
	 * @return the links
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target, int directionType) throws SLGraphSessionException;

	/**
	 * Gets the metadata.
	 * 
	 * @return the metadata
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException;
	
}
