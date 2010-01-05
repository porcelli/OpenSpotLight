package org.openspotlight.graph;

import org.openspotlight.graph.persistence.SLPersistentNode;

public final class SLLinkRemovedEvent extends SLLinkEvent {

	public SLLinkRemovedEvent(final SLLink link) {
		super(link);
	}

	public SLLinkRemovedEvent(final SLLink link,
			final SLPersistentNode linkNode,
			final SLPersistenceMode persistenceMode) {
		super(link, linkNode, persistenceMode);
	}

}
