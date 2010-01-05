package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.persistence.SLPersistentProperty;

public final class SLLinkPropertySetEvent extends SLLinkPropertyEvent {

	public SLLinkPropertySetEvent(
			final SLLinkProperty<? extends Serializable> property,
			final SLPersistentProperty<? extends Serializable> pProperty) {
		super(property, pProperty);
	}

}
