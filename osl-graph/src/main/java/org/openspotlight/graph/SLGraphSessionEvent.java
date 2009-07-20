package org.openspotlight.graph;

public class SLGraphSessionEvent {
	
	public static final int TYPE_BEFORE_SAVE = 1;
	
	protected int type;
	protected SLGraphSession session;
	
	public SLGraphSessionEvent(int type, SLGraphSession session) {
		this.type = type;
		this.session = session;
	}
	
	public int getType() {
		return type;
	}
	
	public SLGraphSession getSession() {
		return session;
	}
}

