package org.openspotlight.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openspotlight.SLException;
import org.openspotlight.graph.annotation.SLLinkAttribute;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentNodeNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.ProxyUtil;

public class SLGraphSessionImpl implements SLGraphSession {
	
	private SLPersistentTreeSession treeSession;
	private SLGraphSessionEventPoster eventPoster;
	
	public SLGraphSessionImpl(SLPersistentTreeSession treeSession) {
		this.treeSession = treeSession;
		Collection<SLGraphSessionEventListener> listeners = new ArrayList<SLGraphSessionEventListener>();
		listeners.add(new SLObjectMarkListener());
		listeners.add(new SLTransientObjectListener());
		this.eventPoster = new SLGraphSessionEventPosterImpl(listeners);
	}
 	
	//@Override
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
	public void close() {
		treeSession.close();
	}

	//@Override
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
	public void clear() throws SLGraphSessionException {
		try {
			treeSession.clear();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to clear OSL repository.", e);
		}
	}

	//@Override
	public SLNode getNodeByID(String id) throws SLNodeNotFoundException, SLGraphSessionException {
		final int INDEX_CONTEXT_ID = 2;
		try {
			SLNode node = null;
			SLPersistentNode pNode = treeSession.getNodeByID(id);
			String[] names = pNode.getPath().substring(1).split("/");
			Long contextID = new Long(names[INDEX_CONTEXT_ID]);
			SLContext context = getContext(contextID);
			for (int i = INDEX_CONTEXT_ID + 1; i < names.length; i++) {
				if (node == null) {
					node = context.getRootNode().getNode(names[i]);
				}
				else {
					node = node.getNode(names[i]);
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
	
	//@Override
	public <L extends SLLink> L addLink(Class<L> linkClass, SLNode source, SLNode target, boolean bidirecional) throws SLGraphSessionException {

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
								SLPersistentNode metaLinkNode = addMetaLinkNode(linkClass, source, target, true);
								String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
								linkNode.setProperty(String.class, propName, metaLinkNode.getID());
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
				SLPersistentNode metaLinkNode = addMetaLinkNode(linkClass, source, target, bidirecional);
				String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
				linkNode.setProperty(String.class, propName, metaLinkNode.getID());
			}
			
			SLLink link = new SLLinkImpl(this, linkNode);
			L linkProxy = ProxyUtil.createProxy(linkClass, link);
			eventPoster.post(new SLLinkEvent(SLLinkEvent.TYPE_LINK_ADDED, this, linkProxy));
			
			return linkProxy;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to add link.", e);
		}
	}
	
	//@Override
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
	public Collection<SLNode> getNodesByLink(SLNode node) throws SLGraphSessionException {
		return getNodesByLink(node, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	//@Override
	public Collection<SLNode> getNodesByLink(SLNode node, int direction) throws SLGraphSessionException {
		return getNodesByLink(node, SLNode.class, true, direction);
	}
	
	public <N extends SLNode> Collection<N> getNodesByLink(SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException {
		return getNodesByLink(node, nodeClass, returnSubTypes, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	//@Override
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
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		return getNodesByLink(linkClass, null);
	}

	//@Override
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node) throws SLGraphSessionException {
		return getNodesByLink(linkClass, node, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	
	//@Override
	public Collection<SLNode> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, int direction) throws SLGraphSessionException {
		return getNodesByLink(linkClass, node, SLNode.class, true, direction);
	}
	
	public <N extends SLNode> Collection<N> getNodesByLink(Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass, boolean returnSubTypes) throws SLGraphSessionException {
		return getNodesByLink(linkClass, node, nodeClass, returnSubTypes, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
	}

	//@Override
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
	public <L extends SLLink> Collection<L> getBidirectionalLinks(Class<L> linkClass, SLNode side1, SLNode side2) throws SLGraphSessionException {
		return getLinks(linkClass, side1, side2, SLLink.DIRECTION_BI);
	}

	//@Override
	public <L extends SLLink> Collection<L> getBidirectionalLinksBySide(Class<L> linkClass, SLNode side) throws SLGraphSessionException {
		return getLinks(linkClass, side, null, SLLink.DIRECTION_BI);
	}

	//@Override
	public Collection<SLLink> getBidirectionalLinks(SLNode side1, SLNode side2) throws SLGraphSessionException {
		return getLinks(side1, side2, SLLink.DIRECTION_BI);
	}

	//@Override
	public Collection<SLLink> getBidirectionalLinksBySide(SLNode side) throws SLGraphSessionException {
		return getLinks(side, null, SLLink.DIRECTION_BI);
	}

	//@Override
	public <L extends SLLink> Collection<L> getUnidirectionalLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(linkClass, source, target, SLLink.DIRECTION_UNI);
	}

	//@Override
	public <L extends SLLink> Collection<L> getUnidirectionalLinksBySource(Class<L> linkClass, SLNode source) throws SLGraphSessionException {
		return getLinks(linkClass, source, null, SLLink.DIRECTION_UNI);
	}

	//@Override
	public <L extends SLLink> Collection<L> getUnidirectionalLinksByTarget(Class<L> linkClass, SLNode target) throws SLGraphSessionException {
		return getLinks(linkClass, null, target, SLLink.DIRECTION_UNI);
	}

	//@Override
	public Collection<SLLink> getUnidirectionalLinks(SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(source, target, SLLink.DIRECTION_UNI);
	}

	//@Override
	public Collection<SLLink> getUnidirectionalLinksBySource(SLNode source) throws SLGraphSessionException {
		return getLinks(source, null, SLLink.DIRECTION_UNI);
	}

	//@Override
	public Collection<SLLink> getUnidirectionalLinksByTarget(SLNode target) throws SLGraphSessionException {
		return getLinks(null, target, SLLink.DIRECTION_UNI);
	}
	
	//@Override
	public Collection<SLLink> getLinks(SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(source, target, SLLink.DIRECTION_ANY);
	}
	
	//@Override
	public Collection<SLLink> getLinks(SLNode source, SLNode target, int directionType) throws SLGraphSessionException {
		Collection<SLLink> links = new ArrayList<SLLink>();
		Collection<Class<? extends SLLink>> linkClasses = getLinkClasses();
		for (Class<? extends SLLink> linkClass : linkClasses) {
			links.addAll(getLinks(linkClass, source, target, directionType));
		}
		return links;
	}
	
	//@Override
	public <L extends SLLink> Collection<L> getLinks(Class<L> linkClass, SLNode source, SLNode target) throws SLGraphSessionException {
		return getLinks(linkClass, source, target, SLLink.DIRECTION_ANY);
	}
	
	//@Override
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
					SLLink link = new SLLinkImpl(this, linkNode);
					links.add(ProxyUtil.createProxy(linkClass, link));
				}
			}
			
			return links;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve links.", e);
		}
	}
	
	//@Override
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return new SLMetadataImpl(treeSession);
	}
	
	private SLPersistentNode addLinkNode(SLPersistentNode pairKeyNode, int direction) throws SLPersistentTreeSessionException {
		long linkCount = incLinkCount(pairKeyNode);
		String name = SLCommonSupport.getLinkIndexNodeName(linkCount);
		SLPersistentNode linkNode = pairKeyNode.addNode(name);
		linkNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, linkCount);
		linkNode.setProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION, direction);
		return linkNode;
	}
	
	private SLPersistentNode findUniqueLinkNode(SLPersistentNode pairKeyNode) throws SLPersistentTreeSessionException {
		return pairKeyNode.getNodes().isEmpty() ? null : pairKeyNode.getNodes().iterator().next();
	}
	
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
	
	private long incLinkCount(SLPersistentNode linkKeyPairNode) throws SLPersistentTreeSessionException {
		SLPersistentProperty<Long> linkCountProp = linkKeyPairNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT);
		long linkCount = linkCountProp.getValue() + 1;
		linkCountProp.setValue(linkCount);
		return linkCount;
	}
	
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
	
	private int getDirection(SLNode source, SLNode target, boolean bidirecional) throws SLException {
		if (bidirecional) return SLConsts.DIRECTION_BOTH;
		else return getANode(source, target).equals(source) ? SLConsts.DIRECTION_AB : SLConsts.DIRECTION_BA;
	}
	
	private SLNode getANode(SLNode source, SLNode target) throws SLException {
		return source.getID().compareTo(target.getID()) < 0 ? source : target; 
	}

	private SLNode getBNode(SLNode source, SLNode target) throws SLException {
		return source.getID().compareTo(target.getID()) < 0 ? target : source; 
	}
	
	private boolean allowsMultiple(Class<? extends SLLink> linkTypeClass) {
		SLLinkAttribute attribute = linkTypeClass.getAnnotation(SLLinkAttribute.class);
		return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_MULTIPLE) > -1;
	}

	private boolean allowsChangeToBidirecional(Class<? extends SLLink> linkTypeClass) {
		SLLinkAttribute attribute = linkTypeClass.getAnnotation(SLLinkAttribute.class);
		return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_CHANGE_TO_BIDIRECTIONAL) > -1;
	}
	
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
	
	private boolean nodeOfType(SLNode node, Class<? extends SLNode> nodeClass, boolean returnSubTypes) {
		return (returnSubTypes && nodeClass.isAssignableFrom(node.getClass().getInterfaces()[0]))
			|| (!returnSubTypes && nodeClass.equals(node.getClass().getInterfaces()[0]));
	}
	
	private SLPersistentNode addMetaLinkNode(Class<? extends SLLink> linkClass, SLNode source, SLNode target, boolean bidirecional) throws SLException {
		
		SLPersistentNode metaLinkNode = null;
		
		Class<?> sourceClass = source.getClass().getInterfaces()[0];
		Class<?> targetClass = target.getClass().getInterfaces()[0];

		SLPersistentNode classPairKeyNode = getClassPairKeyNode(linkClass, sourceClass, targetClass);
		int direction = getMetaLinkDirection(sourceClass, targetClass, bidirecional);
		
		StringBuilder statement = new StringBuilder();
		statement.append(classPairKeyNode.getPath())
			.append("/*[").append(SLConsts.PROPERTY_NAME_DIRECTION).append("=").append(direction).append(']');
		
		SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
		SLPersistentQueryResult result = query.execute();
		if (result.getRowCount() == 0) {
			metaLinkNode = addLinkNode(classPairKeyNode, direction);
		}
		
		return metaLinkNode;
	}

	private int getMetaLinkDirection(Class<?> sourceClass, Class<?> targetClass, boolean bidirecional) throws SLException {
		if (bidirecional) return SLConsts.DIRECTION_BOTH;
		else return getAClass(sourceClass, targetClass).equals(sourceClass) ? SLConsts.DIRECTION_AB : SLConsts.DIRECTION_BA;
	}

	private Class<?> getAClass(Class<?> sourceClass, Class<?> targetClass) throws SLException {
		return sourceClass.getName().compareTo(targetClass.getName()) < 0 ? sourceClass : targetClass; 
	}

	private Class<?> getBClass(Class<?> sourceClass, Class<?> targetClass) throws SLException {
		return sourceClass.getName().compareTo(targetClass.getName()) < 0 ? targetClass : sourceClass; 
	}

	private SLPersistentNode getClassPairKeyNode(Class<? extends SLLink> linkClass, Class<?> sourceClass, Class<?> targetClass) throws SLException {

		Class<?> aClass = getAClass(sourceClass, targetClass); 
		Class<?> bClass = getBClass(sourceClass, targetClass);
		
		StringBuilder pairKey = new StringBuilder();
		pairKey.append(aClass.getName()).append('.').append(bClass.getName());
		SLPersistentNode linkClassNode = SLCommonSupport.getMetaLinkClassNode(treeSession, linkClass);
		SLPersistentNode pairKeyNode = linkClassNode.getNode(pairKey.toString());
		
		if (pairKeyNode == null) {
			pairKeyNode = linkClassNode.addNode(pairKey.toString());
			pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_A_CLASS_NAME, aClass.getName());
			pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_B_CLASS_NAME, bClass.getName());
			pairKeyNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, 0L);
		}
		
		return pairKeyNode;
	}
 }


