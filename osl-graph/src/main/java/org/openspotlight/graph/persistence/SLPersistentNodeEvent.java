package org.openspotlight.graph.persistence;


public class SLPersistentNodeEvent extends SLPersistentEvent {
	
	public static final int TYPE_NODE_ADDED = 1;
	public static final int TYPE_NODE_REMOVED = 2;

	private SLPersistentNode node;

	public SLPersistentNodeEvent(int type, SLPersistentNode node) {
		super(type);
		this.node = node;
	}

	public SLPersistentNode getNode() {
		return node;
	}
}
