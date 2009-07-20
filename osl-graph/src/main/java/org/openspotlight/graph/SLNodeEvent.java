package org.openspotlight.graph;

import java.util.Collection;

public class SLNodeEvent extends SLGraphSessionEvent {

	public static final int TYPE_NODE_ADDED = 1;
	
	private SLNode node;
	private Collection<Class<? extends SLLink>> linkTypesForLinkDeletion;
	private Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion;

	public SLNodeEvent(int type, SLGraphSession session, SLNode node, Collection<Class<? extends SLLink>> linkTypesForLinkDeletion, Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		super(type, session);
		this.node = node;
		this.linkTypesForLinkDeletion = linkTypesForLinkDeletion;
		this.linkTypesForLinkedNodeDeletion = linkTypesForLinkedNodeDeletion;
	}
	
	public SLNode getNode() {
		return node;
	}
	
	public Collection<Class<? extends SLLink>> getLinkTypesForLinkDeletion() {
		return linkTypesForLinkDeletion;
	}
	
	public Collection<Class<? extends SLLink>> getLinkTypesForLinkedNodesDeletion() {
		return linkTypesForLinkedNodeDeletion;
	}
}
