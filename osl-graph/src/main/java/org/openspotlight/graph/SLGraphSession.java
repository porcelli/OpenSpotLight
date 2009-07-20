package org.openspotlight.graph;

import java.util.Collection;

public interface SLGraphSession {
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SLContextAlreadyExistsException
	 * @throws SLGraphSessionException
	 */
	public SLContext createContext(Long id) throws SLContextAlreadyExistsException, SLGraphSessionException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLContext getContext(Long id) throws SLGraphSessionException;
	
	/**
	 * 
	 * @throws SLGraphSessionException
	 */
	public void save() throws SLGraphSessionException;
	
	/**
	 * 
	 */
	public void close();

	/**
	 * 
	 * @throws SLGraphSessionException
	 */
	public void clear() throws SLGraphSessionException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getNodeByID(String id) throws SLNodeNotFoundException, SLGraphSessionException;
	
	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param source
	 * @param target
	 * @param bidirecional
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> L addLink(Class<L> linkClass, SLNode source, SLNode target, boolean bidirecional) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param predicate
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLNode> getNodesByPredicate(SLNodePredicate predicate) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param node
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLNode> getNodesByLink(SLNode node) throws SLGraphSessionException;

	/**
	 * 
	 * @param node
	 * @param direction
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLNode> getNodesByLink(SLNode node, int direction) throws SLGraphSessionException;
		
	/**
	 * 
	 * @param <N>
	 * @param node
	 * @param nodeClass
	 * @param returnSubTypes
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <N>
	 * @param node
	 * @param nodeClass
	 * @param returnSubTypes
	 * @param direction
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes, int direction) throws SLGraphSessionException;

	/**
	 * 
	 * @param linkClass
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass) throws SLGraphSessionException;

	/**
	 * 
	 * @param linkClass
	 * @param node
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param linkClass
	 * @param node
	 * @param direction
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, int direction) throws SLGraphSessionException;

	/**
	 * 
	 * @param <N>
	 * @param linkClass
	 * @param node
	 * @param nodeClass
	 * @param returnSubTypes
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <N>
	 * @param linkClass
	 * @param node
	 * @param nodeClass
	 * @param returnSubTypes
	 * @param direction
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes, int direction) throws SLGraphSessionException;

	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param side1
	 * @param side2
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getBidirectionalLinks(Class<L> linkClass, SLNode side1, SLNode side2) throws SLGraphSessionException;

	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param side
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getBidirectionalLinksBySide(Class<L> linkClass, SLNode side) throws SLGraphSessionException;

	/**
	 * 
	 * @param side1
	 * @param side2
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getBidirectionalLinks(SLNode side1, SLNode side2) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param side
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getBidirectionalLinksBySide(SLNode side) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param source
	 * @param target
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException;

	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param source
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinksBySource(Class<L> linkClass, SLNode source) throws SLGraphSessionException;

	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param target
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinksByTarget(Class<L> linkClass, SLNode target) throws SLGraphSessionException;

	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getUnidirectionalLinks(SLNode source, SLNode target) throws SLGraphSessionException;

	/**
	 * 
	 * @param source
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getUnidirectionalLinksBySource(SLNode source) throws SLGraphSessionException;

	/**
	 * 
	 * @param target
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getUnidirectionalLinksByTarget(SLNode target) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getLinks(SLNode source, SLNode target) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param directionType
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLLink> getLinks(SLNode source, SLNode target, int directionType) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param source
	 * @param target
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param <L>
	 * @param linkClass
	 * @param source
	 * @param target
	 * @param directionType
	 * @return
	 * @throws SLGraphSessionException
	 */
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target, int directionType) throws SLGraphSessionException;

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException;
	
}
