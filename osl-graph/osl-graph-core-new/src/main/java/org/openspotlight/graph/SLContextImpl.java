package org.openspotlight.graph;

public class SLContextImpl implements SLContext{

	public SLContextImpl(String caption, String id, SLNode rootNode) {
		this.caption = caption;
		this.ID = id;
		this.rootNode = rootNode;
	}

	private String caption;
	
	public final String ID;
	
	public final SLNode rootNode;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public SLNode getRootNode() {
		return rootNode;
	}

	public String getID() {
		return ID;
	}

	
}
