package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.persistence.SLPersistentProperty;

public final class SLNodePropertySetEvent extends SLNodePropertyEvent {

	public SLNodePropertySetEvent(
			final SLNodeProperty<? extends Serializable> property,
			final SLPersistentProperty<? extends Serializable> pProperty) {
		super(property, pProperty);
	}

}
