package org.openspotlight.graph;

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.graph.annotation.SLTransient;

public class SLTransientObjectListener implements SLGraphSessionEventListener {
	
	private Set<SLLink> transientLinks;
	private Set<SLNode> transientNodes;
	
	public SLTransientObjectListener() {
		transientLinks = new HashSet<SLLink>();
		transientNodes = new HashSet<SLNode>();
	}

	@Override
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException {
		SLLink link = event.getLink();
		if (isTransient(link)) {
			transientLinks.add(link);
		}
	}

	@Override
	public void nodeAdded(SLNodeEvent event) throws SLGraphSessionException {
		SLNode node = event.getNode();
		if (isTransient(node)) {
			transientNodes.add(node);
		}
	}

	@Override
	public void beforeSave(SLGraphSessionEvent event) throws SLGraphSessionException {
		for (SLLink link : transientLinks) {
			link.remove();
		}
		for (SLNode node : transientNodes) {
			node.remove();
		}
	}
	
	private boolean isTransient(Object object) {
		return object.getClass().getInterfaces()[0].getAnnotation(SLTransient.class) != null;
	}
}

