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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.annotation.SLLinkAttribute;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentNodeNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.ProxyUtil;

/**
 * The Class SLGraphSessionImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLGraphSessionImpl implements SLGraphSession {
	
	/** The tree session. */
	private SLPersistentTreeSession treeSession;
	
	/** The event poster. */
	private SLGraphSessionEventPoster eventPoster;
	
	/** The encoder. */
	private SLEncoder encoder;
	
	/** The encoder factory. */
	private SLEncoderFactory encoderFactory;
	
	/**
	 * Instantiates a new sL graph session impl.
	 * 
	 * @param treeSession the tree session
	 */
	public SLGraphSessionImpl(SLPersistentTreeSession treeSession) {
		this.treeSession = treeSession;
		Collection<SLGraphSessionEventListener> listeners = new ArrayList<SLGraphSessionEventListener>();
		listeners.add(new SLObjectMarkListener());
		listeners.add(new SLTransientObjectListener());
		listeners.add(new SLMetadataListener());
		this.eventPoster = new SLGraphSessionEventPosterImpl(listeners);
		this.encoderFactory = new SLEncoderFactoryImpl();
		this.encoder = encoderFactory.getUUIDEncoder();
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#createContext(java.lang.Long)
	 */
	public SLContext createContext(Long id) throws SLContextAlreadyExistsException, SLGraphSessionException {
		SLContext context = null;
		try {
			SLPersistentNode contextsPersistentNode = SLCommonSupport.getContextsPersistentNode(treeSession);
			if (contextsPersistentNode.getNode("" + id) == null) {
				SLPersistentNode contextRootPersistentNode = contextsPersistentNode.addNode("" + id);
				context = new SLContextImpl(this, contextRootPersistentNode, eventPoster);
			}
			else {
				throw new SLContextAlreadyExistsException(id);				
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to create context node.", e);
		}
		return context;
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getContext(java.lang.Long)
	 */
	public SLContext getContext(Long id) throws SLGraphSessionException {
		try {
			SLContext context = null;
			SLPersistentNode contextsPersistentNode = SLCommonSupport.getContextsPersistentNode(treeSession);
			SLPersistentNode contextRootPersistentNode = contextsPersistentNode.getNode("" + id);
			if (contextRootPersistentNode != null) {
				context = new SLContextImpl(this, contextRootPersistentNode, eventPoster);
			}
			return context;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt retrieve context node.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#close()
	 */
	public void close() {
		treeSession.close();
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#save()
	 */
	public void save() throws SLGraphSessionException {
		try {
			eventPoster.post(new SLGraphSessionEvent(SLGraphSessionEvent.TYPE_BEFORE_SAVE, this));
			treeSession.save();
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to save the session.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#clear()
	 */
	public void clear() throws SLGraphSessionException {
		try {
			treeSession.clear();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to clear OSL repository.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodeByID(java.lang.String)
	 */
	public SLNode getNodeByID(String id) throws SLNodeNotFoundException, SLGraphSessionException {
		final int INDEX_CONTEXT_ID = 2;
		try {
			SLNode node = null;
			SLPersistentNode pNode = treeSession.getNodeByID(id);
			String[] names = pNode.getPath().substring(1).split("/");
			Long contextID = new Long(names[INDEX_CONTEXT_ID]);
			SLContext context = getContext(contextID);
			SLEncoder fakeEncoder = getEncoderFactory().getFakeEncoder();
			for (int i = INDEX_CONTEXT_ID + 1; i < names.length; i++) {
				if (node == null) {
					node = context.getRootNode().getNode(SLNode.class, names[i], fakeEncoder);
				}
				else {
					node = node.getNode(SLNode.class, names[i], fakeEncoder);
				}
			}
			return node;
		}
		catch (SLPersistentNodeNotFoundException e) {
			throw new SLNodeNotFoundException(id, e);
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node by id.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#addLink(java.lang.Class, org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode, boolean)
	 */
	public <L extends SLLink> L addLink(Class<L> linkClass, SLNode source, SLNode target, boolean bidirecional) throws SLGraphSessionException {
		return addLink(linkClass, source, target, bidirecional, SLPersistenceMode.NORMAL);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#addLink(java.lang.Class, org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode, boolean)
	 */
	public <L extends SLLink> L addLink(Class<L> linkClass, SLNode source, SLNode target, boolean bidirecional, SLPersistenceMode persistenceMode) throws SLGraphSessionException {

		try  {

			SLPersistentNode linkNode = null;
			
			boolean allowsMultiple = allowsMultiple(linkClass);
			boolean allowsChangeToBidirecional = allowsChangeToBidirecional(linkClass);

			if (allowsMultiple && allowsChangeToBidirecional) {
				throw new SLGraphSessionException("ALLOWS_CHANGE_TO_BIDIRECTIONAL and ALLOWS_MULTIPLE attributes are not supported at once.");
			}

			SLPersistentNode pairKeyNode = getPairKeyNode(linkClass, source, target);
			int direction = getDirection(source, target, bidirecional);
			
			boolean status = false;
			
			if (allowsMultiple) {
				status = true;
			}
			else {
				
				linkNode = getLinkNodeByDirection(pairKeyNode, direction);
				
				if (linkNode == null) {
					if (allowsChangeToBidirecional) {
						linkNode = findUniqueLinkNode(pairKeyNode);
						if (linkNode == null) {
							status = true;
						}
						else {
							SLPersistentProperty<Integer> directionProp = linkNode.getProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION);
							if (directionProp.getValue() != SLConsts.DIRECTION_BOTH) {
								directionProp.setValue(SLConsts.DIRECTION_BOTH);
							}
						}
					}
					else {
						status = true;
					}
				}
			}
			
			if (status) {
				linkNode = addLinkNode(pairKeyNode, direction);
			}
			
			SLLink link = new SLLinkImpl(this, linkNode, eventPoster);
			L linkProxy = ProxyUtil.createLinkProxy(linkClass, link);
			eventPoster.post(new SLLinkEvent(SLLinkEvent.TYPE_LINK_ADDED, linkProxy, linkNode, persistenceMode));
			return linkProxy;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to add link.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByPredicate(org.openspotlight.graph.SLNodePredicate)
	 */
	public Collection<SLNode> getNodesByPredicate(SLNodePredicate predicate) throws SLGraphSessionException {
		try {
			Collection<SLNode> nodes = new ArrayList<SLNode>();
			SLPersistentQuery query = treeSession.createQuery("//osl/contexts/*//descendant::node()", SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> pNodes = result.getNodes();
			for (SLPersistentNode pNode : pNodes) {
				SLNode node = getNodeByID(pNode.getID());
				if (node!= null && predicate.evaluate(node)) {
					nodes.add(node);
				}
			}
			return nodes;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve nodes by predicate.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(org.openspotlight.graph.SLNode)
	 */
	public Collection<SLNode> getNodesByLink(SLNode node) throws SLGraphSessionException {
		return getNodesByLink(node, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(org.openspotlight.graph.SLNode, int)
	 */
	public Collection<SLNode> getNodesByLink(SLNode node, int direction) throws SLGraphSessionException {
		return getNodesByLink(node, SLNode.class, true, direction);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(org.openspotlight.graph.SLNode, java.lang.Class, boolean)
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException {
		return getNodesByLink(node, nodeClass, returnSubTypes, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(org.openspotlight.graph.SLNode, java.lang.Class, boolean, int)
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes, int direction) throws SLGraphSessionException {
		try {
			Collection<? extends SLLink> links = getLinks(node, null, direction); 
			return filterNodesFromLinks(links, node, nodeClass, returnSubTypes);
		}
		catch (SLGraphSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve nodes by link.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(java.lang.Class)
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		return getNodesByLink(linkClass, null);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(java.lang.Class, org.openspotlight.graph.SLNode)
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node) throws SLGraphSessionException {
		return getNodesByLink(linkClass, node, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(java.lang.Class, org.openspotlight.graph.SLNode, int)
	 */
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, int direction) throws SLGraphSessionException {
		return getNodesByLink(linkClass, node, SLNode.class, true, direction);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(java.lang.Class, org.openspotlight.graph.SLNode, java.lang.Class, boolean)
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException {
		return getNodesByLink(linkClass, node, nodeClass, returnSubTypes, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getNodesByLink(java.lang.Class, org.openspotlight.graph.SLNode, java.lang.Class, boolean, int)
	 */
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes, int direction) throws SLGraphSessionException {
		try {
			Collection<? extends SLLink> links = getLinks(linkClass, node, null, direction); 
			return filterNodesFromLinks(links, node, nodeClass, returnSubTypes);
		}
		catch (SLGraphSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve nodes by link.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getBidirectionalLinks(java.lang.Class, org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode)
	 */
	public <L extends SLLink> Collection<L> getBidirectionalLinks(Class<L> linkClass, SLNode side1, SLNode side2) throws SLGraphSessionException {
		return getLinks(linkClass, side1, side2, SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getBidirectionalLinksBySide(java.lang.Class, org.openspotlight.graph.SLNode)
	 */
	public <L extends SLLink> Collection<L> getBidirectionalLinksBySide(Class<L> linkClass, SLNode side) throws SLGraphSessionException {
		return getLinks(linkClass, side, null, SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getBidirectionalLinks(org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode)
	 */
	public Collection<SLLink> getBidirectionalLinks(SLNode side1, SLNode side2) throws SLGraphSessionException {
		return getLinks(side1, side2, SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getBidirectionalLinksBySide(org.openspotlight.graph.SLNode)
	 */
	public Collection<SLLink> getBidirectionalLinksBySide(SLNode side) throws SLGraphSessionException {
		return getLinks(side, null, SLLink.DIRECTION_BI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getUnidirectionalLinks(java.lang.Class, org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode)
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(linkClass, source, target, SLLink.DIRECTION_UNI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getUnidirectionalLinksBySource(java.lang.Class, org.openspotlight.graph.SLNode)
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinksBySource(Class<L> linkClass, SLNode source) throws SLGraphSessionException {
		return getLinks(linkClass, source, null, SLLink.DIRECTION_UNI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getUnidirectionalLinksByTarget(java.lang.Class, org.openspotlight.graph.SLNode)
	 */
	public <L extends SLLink> Collection<L> getUnidirectionalLinksByTarget(Class<L> linkClass, SLNode target) throws SLGraphSessionException {
		return getLinks(linkClass, null, target, SLLink.DIRECTION_UNI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getUnidirectionalLinks(org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode)
	 */
	public Collection<SLLink> getUnidirectionalLinks(SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(source, target, SLLink.DIRECTION_UNI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getUnidirectionalLinksBySource(org.openspotlight.graph.SLNode)
	 */
	public Collection<SLLink> getUnidirectionalLinksBySource(SLNode source) throws SLGraphSessionException {
		return getLinks(source, null, SLLink.DIRECTION_UNI);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getUnidirectionalLinksByTarget(org.openspotlight.graph.SLNode)
	 */
	public Collection<SLLink> getUnidirectionalLinksByTarget(SLNode target) throws SLGraphSessionException {
		return getLinks(null, target, SLLink.DIRECTION_UNI);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getLinks(org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode)
	 */
	public Collection<SLLink> getLinks(SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(source, target, SLLink.DIRECTION_ANY);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getLinks(org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode, int)
	 */
	public Collection<SLLink> getLinks(SLNode source, SLNode target, int directionType) throws SLGraphSessionException {
		Collection<SLLink> links = new ArrayList<SLLink>();
		Collection<Class<? extends SLLink>> linkClasses = getLinkClasses();
		for (Class<? extends SLLink> linkClass : linkClasses) {
			links.addAll(getLinks(linkClass, source, target, directionType));
		}
		return links;
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getLinks(java.lang.Class, org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode)
	 */
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(linkClass, source, target, SLLink.DIRECTION_ANY);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getLinks(java.lang.Class, org.openspotlight.graph.SLNode, org.openspotlight.graph.SLNode, int)
	 */
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target, int direction) throws SLGraphSessionException {
		
		try {
			
			Collection<L> links = new TreeSet<L>();
			
			// query format:
			// //osl/links/JavaClassToJavaMethod/*[(@a=sourceID or @b=sourceID) and (@a=targetID or @b=targetID)]/*[@direction=AB or @direcionBA] order by @linkCount ascending
			
			StringBuilder statement = new StringBuilder();
			statement.append("//osl/links/").append(linkClass.getName()).append("/*");
			
			if (source != null || target != null) {
				
				statement.append('[');

				// filter source ...
				if (source != null) {
					statement.append('(')
						.append('@').append(SLConsts.PROPERTY_NAME_A_NODE_ID).append("='").append(source.getID()).append("'")
						.append(" or ")
						.append('@').append(SLConsts.PROPERTY_NAME_B_NODE_ID).append("='").append(source.getID()).append("'")
						.append(')');
				}
				
				if (source != null && target != null) {
					statement.append(" and ");
				}
				
				// filter target ...
				if (target != null) {
					statement.append('(')
					.append('@').append(SLConsts.PROPERTY_NAME_A_NODE_ID).append("='").append(target.getID()).append("'")
					.append(" or ")
					.append('@').append(SLConsts.PROPERTY_NAME_B_NODE_ID).append("='").append(target.getID()).append("'")
					.append(')');
				}
				
				statement.append(']');
			}
			
			statement.append("/*");
			
			StringBuilder directionFilter = new StringBuilder();
			if ((direction == (direction | SLLink.DIRECTION_UNI)) || (direction == (direction | SLLink.DIRECTION_UNI_REVERSAL))) {
				directionFilter.append('@').append(SLConsts.PROPERTY_NAME_DIRECTION).append('=').append(SLConsts.DIRECTION_AB)
					.append(" or @").append(SLConsts.PROPERTY_NAME_DIRECTION).append('=').append(SLConsts.DIRECTION_BA);
			}
			if (direction == (direction | SLLink.DIRECTION_BI)) {
				if (directionFilter.length() > 0) directionFilter.append(" or ");
				directionFilter.append('@').append(SLConsts.PROPERTY_NAME_DIRECTION).append('=').append(SLConsts.DIRECTION_BOTH);
			}
			
			// add direction filter ...
			statement.append('[').append(directionFilter).append(']');
			
			// order by link count (the order the links are added by the user) ...
			statement.append(" order by @").append(SLConsts.PROPERTY_NAME_LINK_COUNT).append(" ascending");
			
			// execute query ...
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> linkNodes = result.getNodes();
			
			for (SLPersistentNode linkNode : linkNodes) {
				
				SLPersistentNode pairKeyNode = linkNode.getParent();
				
				SLPersistentProperty<String> aNodeIDProp = pairKeyNode.getProperty(String.class, SLConsts.PROPERTY_NAME_A_NODE_ID);
				SLPersistentProperty<String> bNodeIDProp = pairKeyNode.getProperty(String.class, SLConsts.PROPERTY_NAME_B_NODE_ID);
				SLPersistentProperty<Integer> directionProp = linkNode.getProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION);
				
				SLNode aNode = getNodeByID(aNodeIDProp.getValue());
				SLNode bNode = getNodeByID(bNodeIDProp.getValue());
				
				boolean status = false;
				
				if (source == null && target == null) {
					if (directionProp.getValue() == SLConsts.DIRECTION_AB || directionProp.getValue() == SLConsts.DIRECTION_BA) {
						status = direction == (direction | SLLink.DIRECTION_UNI);
					}
					else {
						status = direction == (direction | SLLink.DIRECTION_BI);
					}
				}
				else {
					
					if (directionProp.getValue() == SLConsts.DIRECTION_BOTH) {
						status = direction == (direction | SLLink.DIRECTION_BI);	
					}
					else {

						SLNode s = null;
						SLNode t = null;
						
						if (directionProp.getValue() == SLConsts.DIRECTION_AB) {
							s = aNode;
							t = bNode;
						}
						else if (directionProp.getValue() == SLConsts.DIRECTION_BA) {
							s = bNode;
							t = aNode;
						}
						
						status = (direction == (direction | SLLink.DIRECTION_UNI) && (s.equals(source) || t.equals(target)))
							|| (direction == (direction | SLLink.DIRECTION_UNI_REVERSAL) && (s.equals(target) || t.equals(source)));
					}
				}
				
				if (status) {
					SLLink link = new SLLinkImpl(this, linkNode, eventPoster);
					links.add(ProxyUtil.createLinkProxy(linkClass, link));
				}
			}
			
			return links;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve links.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getMetadata()
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return new SLMetadataImpl(treeSession);
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#getDefaultEncoder()
	 */
	public SLEncoder getDefaultEncoder() throws SLGraphSessionException {
		return encoder;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphSession#setDefaultEncoder(org.openspotlight.graph.SLEncoder)
	 */
	public void setDefaultEncoder(SLEncoder encoder) throws SLGraphSessionException {
		this.encoder = encoder;
	}
	
	public SLEncoderFactory getEncoderFactory() throws SLGraphSessionException {
		return encoderFactory;
	}


	/**
	 * Adds the link node.
	 * 
	 * @param pairKeyNode the pair key node
	 * @param direction the direction
	 * 
	 * @return the sL persistent node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private SLPersistentNode addLinkNode(SLPersistentNode pairKeyNode, int direction) throws SLPersistentTreeSessionException {
		long linkCount = incLinkCount(pairKeyNode);
		String name = SLCommonSupport.getLinkIndexNodeName(linkCount);
		SLPersistentNode linkNode = pairKeyNode.addNode(name);
		linkNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, linkCount);
		linkNode.setProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION, direction);
		return linkNode;
	}
	
	/**
	 * Find unique link node.
	 * 
	 * @param pairKeyNode the pair key node
	 * 
	 * @return the sL persistent node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private SLPersistentNode findUniqueLinkNode(SLPersistentNode pairKeyNode) throws SLPersistentTreeSessionException {
		return pairKeyNode.getNodes().isEmpty() ? null : pairKeyNode.getNodes().iterator().next();
	}
	
	/**
	 * Gets the link node by direction.
	 * 
	 * @param pairKeyNode the pair key node
	 * @param direction the direction
	 * 
	 * @return the link node by direction
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private SLPersistentNode getLinkNodeByDirection(SLPersistentNode pairKeyNode, int direction) throws SLPersistentTreeSessionException {
		SLPersistentNode linkNode = null;
		for (SLPersistentNode node : pairKeyNode.getNodes()) {
			SLPersistentProperty<Long> directionProp = node.getProperty(Long.class, SLConsts.PROPERTY_NAME_DIRECTION);
			if (directionProp.getValue() == direction) {
				linkNode = node;
			}
		}
		return linkNode;
	}
	
	/**
	 * Inc link count.
	 * 
	 * @param linkKeyPairNode the link key pair node
	 * 
	 * @return the long
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private long incLinkCount(SLPersistentNode linkKeyPairNode) throws SLPersistentTreeSessionException {
		SLPersistentProperty<Long> linkCountProp = linkKeyPairNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT);
		long linkCount = linkCountProp.getValue() + 1;
		linkCountProp.setValue(linkCount);
		return linkCount;
	}
	
	/**
	 * Gets the pair key node.
	 * 
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the pair key node
	 * 
	 * @throws SLException the SL exception
	 */
	private SLPersistentNode getPairKeyNode(Class<? extends SLLink> linkClass, SLNode source, SLNode target) throws SLException {

		SLNode a = getANode(source, target); 
		SLNode b = getBNode(source, target);
		String aIDName = SLCommonSupport.getNameInIDForm(a);
		String bIDName = SLCommonSupport.getNameInIDForm(b);
		
		StringBuilder pairKey = new StringBuilder();
		pairKey.append(aIDName).append('.').append(bIDName);
		SLPersistentNode linkClassNode = SLCommonSupport.getLinkClassNode(treeSession, linkClass);
		SLPersistentNode pairKeyNode = linkClassNode.getNode(pairKey.toString());
		
		if (pairKeyNode == null) {
			pairKeyNode = linkClassNode.addNode(pairKey.toString());
			pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_A_NODE_ID, a.getID());
			pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_B_NODE_ID, b.getID());
			pairKeyNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, 0L);
		}
		
		return pairKeyNode;
	}
	
	/**
	 * Gets the link classes.
	 * 
	 * @return the link classes
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@SuppressWarnings("unchecked")
	private Collection<Class<? extends SLLink>> getLinkClasses() throws SLGraphSessionException {
		try {
			Collection<Class<? extends SLLink>> linkClasses = new ArrayList<Class<? extends SLLink>>(); 
			SLPersistentQuery query = treeSession.createQuery("//osl/links/*", SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> linkClassNodes = result.getNodes();
			for (SLPersistentNode linkClassNode : linkClassNodes) {
				Class<? extends SLLink> linkClass = (Class<? extends SLLink>) Class.forName(linkClassNode.getName());
				linkClasses.add(linkClass);
			}
			return linkClasses;
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve link classes.", e);
		}
	}
	
	/**
	 * Gets the direction.
	 * 
	 * @param source the source
	 * @param target the target
	 * @param bidirecional the bidirecional
	 * 
	 * @return the direction
	 * 
	 * @throws SLException the SL exception
	 */
	private int getDirection(SLNode source, SLNode target, boolean bidirecional) throws SLException {
		if (bidirecional) return SLConsts.DIRECTION_BOTH;
		else return getANode(source, target).equals(source) ? SLConsts.DIRECTION_AB : SLConsts.DIRECTION_BA;
	}
	
	/**
	 * Gets the a node.
	 * 
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the a node
	 * 
	 * @throws SLException the SL exception
	 */
	private SLNode getANode(SLNode source, SLNode target) throws SLException {
		return source.getID().compareTo(target.getID()) < 0 ? source : target; 
	}

	/**
	 * Gets the b node.
	 * 
	 * @param source the source
	 * @param target the target
	 * 
	 * @return the b node
	 * 
	 * @throws SLException the SL exception
	 */
	private SLNode getBNode(SLNode source, SLNode target) throws SLException {
		return source.getID().compareTo(target.getID()) < 0 ? target : source; 
	}
	
	/**
	 * Allows multiple.
	 * 
	 * @param linkTypeClass the link type class
	 * 
	 * @return true, if successful
	 */
	private boolean allowsMultiple(Class<? extends SLLink> linkTypeClass) {
		SLLinkAttribute attribute = linkTypeClass.getAnnotation(SLLinkAttribute.class);
		return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_MULTIPLE) > -1;
	}

	/**
	 * Allows change to bidirecional.
	 * 
	 * @param linkTypeClass the link type class
	 * 
	 * @return true, if successful
	 */
	private boolean allowsChangeToBidirecional(Class<? extends SLLink> linkTypeClass) {
		SLLinkAttribute attribute = linkTypeClass.getAnnotation(SLLinkAttribute.class);
		return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_CHANGE_TO_BIDIRECTIONAL) > -1;
	}
	
	/**
	 * Filter nodes from links.
	 * 
	 * @param links the links
	 * @param node the node
	 * @param nodeClass the node class
	 * @param returnSubTypes the return sub types
	 * 
	 * @return the set< n>
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private <N extends SLNode> Set<N> filterNodesFromLinks(Collection<? extends SLLink> links, SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException {
		Set<N> nodes = new HashSet<N>();
		for (SLLink link : links) {
			if (node == null) {
				SLNode side1 = link.getSides()[0];
				SLNode side2 = link.getSides()[1];
				if (nodeOfType(side1, nodeClass, returnSubTypes)) {
					nodes.add(nodeClass.cast(side1));
				}
				if (nodeOfType(side2, nodeClass, returnSubTypes)) {
					nodes.add(nodeClass.cast(side2));
				}
			}
			else {
				SLNode otherSide = link.getOtherSide(node);
				if (nodeOfType(otherSide, nodeClass, returnSubTypes)) {
					nodes.add(nodeClass.cast(otherSide));
				}
			}
		}
		return nodes;
	}
	
	/**
	 * Node of type.
	 * 
	 * @param node the node
	 * @param nodeClass the node class
	 * @param returnSubTypes the return sub types
	 * 
	 * @return true, if successful
	 */
	private boolean nodeOfType(SLNode node, Class<? extends SLNode> nodeClass, boolean returnSubTypes) {
		return (returnSubTypes && nodeClass.isAssignableFrom(node.getClass().getInterfaces()[0]))
			|| (!returnSubTypes && nodeClass.equals(node.getClass().getInterfaces()[0]));
	}
 }