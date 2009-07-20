package org.openspotlight.graph;

import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.util.AbstractFactory;

public class SLGraphImpl implements SLGraph {
	
	private SLPersistentTree tree;
	
	public SLGraphImpl(SLPersistentTree tree) {
		this.tree = tree;
	}

	@Override
	public SLGraphSession openSession() throws SLGraphException {
		try {
			SLPersistentTreeSession treeSession = tree.openSession();
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
			return factory.createGraphSession(treeSession);
		}
		catch (Exception e) {
			throw new SLGraphException("Could not open SL graph session.", e);
		}
	}

	@Override
	public void shutdown() {
		tree.shutdown();
	}
}
