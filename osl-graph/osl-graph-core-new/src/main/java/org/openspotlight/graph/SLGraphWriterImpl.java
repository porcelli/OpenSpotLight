package org.openspotlight.graph;

import java.util.Collection;

import org.openspotlight.graph.manipulation.SLGraphWriter;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLGraphWriterImpl implements SLGraphWriter{

	private final Provider<STStorageSession> sessionProvider;
	private final String artifactId;
	
	public SLGraphWriterImpl(Provider<STStorageSession> sessionProvider, String artifactId){
		this.artifactId = artifactId;
		this.sessionProvider = sessionProvider;
	}
	
	@Override
	public <L extends SLLink> L createBidirectionalLink(Class<L> linkClass,
			SLNode source, SLNode target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <L extends SLLink> L createLink(Class<L> linkClass, SLNode source,
			SLNode target) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> T createNode(SLNode parent, Class<T> clazz,
			String name) {
		return null;
	}

	@Override
	public <T extends SLNode> T createNode(SLNode parent, Class<T> clazz,
			String name,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	
	public void removeContext(SLContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLink(SLLink link) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNode(SLNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContextCaption(SLContext context, String caption) {
		// TODO Auto-generated method stub
		
	}

}
