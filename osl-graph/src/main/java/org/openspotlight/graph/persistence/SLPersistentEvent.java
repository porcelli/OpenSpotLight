package org.openspotlight.graph.persistence;

public class SLPersistentEvent {
	
	private int type;
	
	public SLPersistentEvent(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
