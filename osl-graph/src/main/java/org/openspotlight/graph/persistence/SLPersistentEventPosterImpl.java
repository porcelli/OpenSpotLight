package org.openspotlight.graph.persistence;

public class SLPersistentEventPosterImpl implements SLPersistentEventPoster {
	
	private SLPersistentEventListener listener;
	
	public SLPersistentEventPosterImpl(SLPersistentEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void post(SLPersistentNodeEvent event) throws SLPersistentTreeSessionException {
		switch (event.getType()) {
			case SLPersistentNodeEvent.TYPE_NODE_ADDED:
				listener.nodeAdded(event);
				break;
			case SLPersistentNodeEvent.TYPE_NODE_REMOVED:
				listener.nodeRemoved(event);
				break;
			default:
				break;
		}
	}

	@Override
	public void post(SLPersistentPropertyEvent event) throws SLPersistentTreeSessionException {
		switch (event.getType()) {
			case SLPersistentPropertyEvent.TYPE_PROPERTY_SET:
				listener.propertySet(event);
				break;
			case SLPersistentPropertyEvent.TYPE_PROPERTY_REMOVED:
				listener.propertyRemoved(event);
				break;
			default:
				break;
		}
	}
}


