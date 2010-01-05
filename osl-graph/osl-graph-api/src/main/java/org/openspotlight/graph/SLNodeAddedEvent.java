package org.openspotlight.graph;

import java.util.Collection;

import org.openspotlight.graph.persistence.SLPersistentNode;

public final class SLNodeAddedEvent extends SLNodeEvent {

	public SLNodeAddedEvent(
			final SLNode node,
			final SLPersistentNode pNode,
			final SLPersistenceMode persistentMode,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		super(node, pNode, persistentMode, linkTypesForLinkDeletion,
				linkTypesForLinkedNodeDeletion);
	}

}
