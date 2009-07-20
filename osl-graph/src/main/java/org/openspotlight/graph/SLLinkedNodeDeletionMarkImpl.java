package org.openspotlight.graph;

import org.openspotlight.SLRuntimeException;

public class SLLinkedNodeDeletionMarkImpl implements SLLinkedNodeDeletionMark {

	private Class<? extends SLLink> linkType;
	private SLNode node;
	
	SLLinkedNodeDeletionMarkImpl(Class<? extends SLLink> linkType, SLNode node) {
		this.linkType = linkType;
		this.node = node;
	}

	@Override
	public Class<? extends SLLink> getLinkType() {
		return linkType;
	}

	@Override
	public SLNode getNode() {
		return node;
	}
	
	@Override
	public int hashCode() {
		try {
			return linkType.getName().concat(node.getID()).hashCode();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkedNodeDeletionMarkImpl.hasCode()", e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (!(obj instanceof SLLinkedNodeDeletionMark)) return false;
			SLLinkedNodeDeletionMark order = (SLLinkedNodeDeletionMark) obj;
			return linkType.getName().concat(node.getID())
				.equals(order.getLinkType().getName().concat(order.getNode().getID()));
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLLinkedNodeDeletionMarkImpl.equals()", e);
		}
	}
}
