package org.openspotlight.graph;

import java.util.Collection;

public class SLGraphSessionEventPosterImpl implements SLGraphSessionEventPoster {
	
	private Collection<SLGraphSessionEventListener> listeners;
	
	SLGraphSessionEventPosterImpl(Collection<SLGraphSessionEventListener> listeners) {
		this.listeners = listeners;
	}

	//@Override
	public void post(SLGraphSessionEvent event) throws SLGraphSessionException {
		if (event.getType() == SLGraphSessionEvent.TYPE_BEFORE_SAVE) {
			for (SLGraphSessionEventListener listener : listeners) {
				listener.beforeSave(event);
			}
		}
	}

	//@Override
	public void post(SLNodeEvent event) throws SLGraphSessionException {
		if (event.getType() == SLNodeEvent.TYPE_NODE_ADDED) {
			for (SLGraphSessionEventListener listener : listeners) {
				listener.nodeAdded(event);
			}
		}
	}

	//@Override
	public void post(SLLinkEvent event) throws SLGraphSessionException {
		if (event.getType() == SLLinkEvent.TYPE_LINK_ADDED) {
			for (SLGraphSessionEventListener listener : listeners) {
				listener.linkAdded(event);
			}
		}
	}
}
