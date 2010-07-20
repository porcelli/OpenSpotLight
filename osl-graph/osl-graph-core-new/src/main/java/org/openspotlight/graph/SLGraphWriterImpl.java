package org.openspotlight.graph;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Collection;
import java.util.List;

import org.openspotlight.graph.internal.SLNodeFactory;
import org.openspotlight.graph.manipulation.SLGraphReader;
import org.openspotlight.graph.manipulation.SLGraphWriter;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.inject.Provider;

public class SLGraphWriterImpl implements SLGraphWriter {

	private final SLGraphReader graphReader;
	private final Provider<STStorageSession> sessionProvider;
	private final STPartitionFactory factory;
	private final String artifactId;
	private final List<SLNode> dirtyNodes = newLinkedList();

	public SLGraphWriterImpl(STPartitionFactory factory,
			Provider<STStorageSession> sessionProvider, String artifactId,
			SLGraphReader graphReader) {
		this.artifactId = artifactId;
		this.factory = factory;
		this.sessionProvider = sessionProvider;
		this.graphReader = graphReader;
	}

	@Override
	public <L extends SLLink> L createBidirectionalLink(Class<L> linkClass,
			SLNode source, SLNode target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <L extends SLLink> L createLink(Class<L> linkClass, SLNode source,
			SLNode target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends SLNode> T createNode(SLNode parent, Class<T> clazz,
			String name) {
		return createNode(parent, clazz, name, null, null);

	}

	@Override
	public <T extends SLNode> T createNode(SLNode parent, Class<T> clazz,
			String name,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		STStorageSession session = sessionProvider.get();
		T newNode = SLNodeFactory.createNode(factory, session, parent
				.getContextId(), parent.getId(), clazz, name,
				linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
		dirtyNodes.add(newNode);
		return newNode;
	}

	@Override
	public void removeContext(SLContext context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeLink(SLLink link) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeNode(SLNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save() {
		STStorageSession session = sessionProvider.get();
		for (SLNode n : this.dirtyNodes) {
			SLNodeFactory.retrievePreviousNode(factory, session, graphReader
					.getContext(n.getContextId()), n);
		}
		session.flushTransient();

	}

	@Override
	public void setContextCaption(SLContext context, String caption) {
		throw new UnsupportedOperationException();
	}

}
