package org.openspotlight.graph.persistence;

public class SLPersistentPropertyEvent extends SLPersistentEvent {
	
	public static final int TYPE_PROPERTY_SET = 1;
	public static final int TYPE_PROPERTY_REMOVED = 2;
	
	private SLPersistentProperty<?> property;

	public SLPersistentPropertyEvent(int type, SLPersistentProperty<?> property) {
		super(type);
		this.property = property;
	}

	public SLPersistentProperty<?> getProperty() {
		return property;
	}
}
