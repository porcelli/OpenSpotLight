package org.openspotlight.graph.persistence;

import java.util.Collection;

public class SLPersistentQueryResultImpl implements SLPersistentQueryResult {
	
	private Collection<SLPersistentNode> persistentNodes;
	
	public SLPersistentQueryResultImpl(Collection<SLPersistentNode> persistentNodes) {
		this.persistentNodes = persistentNodes;
	}

	@Override
	public Collection<SLPersistentNode> getNodes() {
		return persistentNodes;
	}

	@Override
	public int getRowCount() {
		return persistentNodes.size();
	}
}
