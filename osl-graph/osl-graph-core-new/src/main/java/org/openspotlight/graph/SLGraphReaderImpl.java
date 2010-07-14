package org.openspotlight.graph;

import org.openspotlight.graph.exception.SLNodeNotFoundException;
import org.openspotlight.graph.manipulation.SLGraphReader;
import org.openspotlight.graph.meta.SLMetaLink;
import org.openspotlight.graph.meta.SLMetaNodeType;
import org.openspotlight.graph.meta.SLMetadata;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLGraphReaderImpl implements SLGraphReader {

	public SLGraphReaderImpl(Provider<STStorageSession> sessionProvider,
			SLGraphLocation location) {
		this.location = location;
		this.sessionProvider = sessionProvider;
	}

	private final Provider<STStorageSession> sessionProvider;
	private final SLGraphLocation location;

	@Override
	public SLQueryApi createQueryApi() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SLQueryText createQueryText(String slqlInput)
			throws SLInvalidQuerySyntaxException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <L extends SLLink> Iterable<L> getBidirectionalLinks(
			Class<L> linkClass, SLNode side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLLink> getBidirectionalLinks(SLNode side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> T getChildNode(SLNode node, Class<T> clazz,
			String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> Iterable<T> getChildrenNodes(SLNode node,
			Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLContext getContext(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLContext getContext(SLNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <L extends SLLink> L getLink(Class<L> linkClass, SLNode source,
			SLNode target, SLLinkDirection linkDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLNode> getLinkedNodes(Class<? extends SLLink> linkClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLNode> getLinkedNodes(Class<? extends SLLink> linkClass,
			SLNode node, SLLinkDirection linkDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends SLNode> Iterable<N> getLinkedNodes(SLNode node,
			Class<N> nodeClass, boolean returnSubTypes,
			SLLinkDirection linkDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLNode> getLinkedNodes(SLNode node,
			SLLinkDirection linkDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLLink> getLinks(SLNode source, SLNode target,
			SLLinkDirection linkDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLMetaLink getMetaLink(SLLink link) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLMetaNodeType getMetaType(SLNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLMetadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLNode getNode(String id) throws SLNodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLNode getParentNode(SLNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <L extends SLLink> Iterable<L> getUnidirectionalLinksBySource(
			Class<L> linkClass, SLNode source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLLink> getUnidirectionalLinksBySource(SLNode source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <L extends SLLink> Iterable<L> getUnidirectionalLinksByTarget(
			Class<L> linkClass, SLNode target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLLink> getUnidirectionalLinksByTarget(SLNode target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> Iterable<T> findNodes(Class<T> clazz,
			String name, SLContext context, SLContext... aditionalContexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SLNode> findNodes(String name, SLContext context,
			SLContext... aditionalContexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> Iterable<T> findNodes(Class<T> clazz,
			SLContext context, SLContext... aditionalContexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> T findUniqueNode(Class<T> clazz, String name,
			SLContext context, SLContext... aditionalContexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SLNode findUniqueNode(String name, SLContext context,
			SLContext... aditionalContexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends SLNode> T findUniqueNode(Class<T> clazz,
			SLContext context, SLContext... aditionalContexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <N extends SLNode> Iterable<N> getLinkedNodes(
			Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass,
			boolean returnSubTypes, SLLinkDirection linkDirection) {
		// TODO Auto-generated method stub
		return null;
	}

}
