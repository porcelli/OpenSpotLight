package org.openspotlight.graph.persistence;

public class SLPersistentEventListenerImpl implements SLPersistentEventListener {
	
	@Override
	public void nodeAdded(SLPersistentNodeEvent event) throws SLPersistentTreeSessionException {
		event.getNode().getParent().save();
	}

	@Override
	public void nodeRemoved(SLPersistentNodeEvent event) throws SLPersistentTreeSessionException {
		event.getNode().getParent().save();
	}

	@Override
	public void propertyRemoved(SLPersistentPropertyEvent event) throws SLPersistentTreeSessionException {
		event.getProperty().getNode().save();
	}

	@Override
	public void propertySet(SLPersistentPropertyEvent event) throws SLPersistentTreeSessionException {
		event.getProperty().getNode().save();
	}
}


