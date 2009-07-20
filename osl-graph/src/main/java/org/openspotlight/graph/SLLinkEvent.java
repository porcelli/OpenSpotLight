package org.openspotlight.graph;

public class SLLinkEvent extends SLGraphSessionEvent {
	
	public static final int TYPE_LINK_ADDED = 1;
	
	private SLLink link;

	public SLLinkEvent(int type, SLGraphSession session, SLLink link) {
		super(type, session);
		this.link = link;
	}
	
	public SLLink getLink() {
		return link;
	}
}
