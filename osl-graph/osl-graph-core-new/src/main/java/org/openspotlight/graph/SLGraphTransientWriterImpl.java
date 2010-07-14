package org.openspotlight.graph;

import org.openspotlight.graph.manipulation.SLGraphTransientWriter;

public class SLGraphTransientWriterImpl implements SLGraphTransientWriter{

	@Override
	public <L extends SLLink> L createTransientBidirectionalLink(
			Class<L> linkClass, SLNode source, SLNode target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <L extends SLLink> L createTransientLink(Class<L> linkClass,
			SLNode source, SLNode target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> T createTransientNode(SLNode parent,
			Class<T> clazz, String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
