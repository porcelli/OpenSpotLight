package org.openspotlight.graph;

import org.openspotlight.graph.persistence.SLPersistentNode;

public final class SLLinkAddedEvent extends SLLinkEvent {

	public SLLinkAddedEvent(final SLLink link) {
		super(link);
	}

	public SLLinkAddedEvent(final SLLink link, final SLPersistentNode linkNode,
			final SLPersistenceMode persistenceMode) {
		super(link, linkNode, persistenceMode);
	}

}
