package org.openspotlight.graph;

import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.persistence.SLPersistentNode;

public class SLContextImpl implements SLContext {

	private static final long serialVersionUID = 1L;
	
	private SLGraphSession session;
	private SLNode rootNode;
	
	public SLContextImpl(SLGraphSession session, SLPersistentNode contextRootPersistentNode, SLGraphSessionEventPoster eventPoster) {
		this.session = session;
		this.rootNode = new SLNodeImpl(this, null, contextRootPersistentNode, eventPoster);
	}

	@Override
	public SLGraphSession getSession() {
		return session;
	}

	@Override
	public Long getID() throws SLGraphSessionException {
		return new Long(rootNode.getName());
	}

	@Override
	public SLNode getRootNode() throws SLGraphSessionException {
		return rootNode;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null) return false;
			SLContext context = (SLContext) obj;
			return getID().equals(context.getID());
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute SLContextImpl.equals() method.", e);
		}
	}
}
