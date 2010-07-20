package org.openspotlight.graph;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.graph.exception.SLNodeNotFoundException;
import org.openspotlight.graph.internal.SLNodeFactory;
import org.openspotlight.graph.manipulation.SLGraphReader;
import org.openspotlight.graph.meta.SLMetaLink;
import org.openspotlight.graph.meta.SLMetaNodeType;
import org.openspotlight.graph.meta.SLMetadata;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.inject.Provider;

public class SLGraphReaderImpl implements SLGraphReader {

	private final STPartitionFactory factory;
	private final Map<String, SLContext> contextCache = newHashMap();

	public SLGraphReaderImpl(Provider<STStorageSession> sessionProvider,
			SLGraphLocation location, STPartitionFactory factory) {
		this.location = location;
		this.sessionProvider = sessionProvider;
		this.factory = factory;
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<SLLink> getBidirectionalLinks(SLNode side) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends SLNode> T getChildNode(SLNode node, Class<T> clazz,
			String name) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <T extends SLNode> Iterable<T> getChildrenNodes(SLNode node,
			Class<T> clazz) {
		throw new UnsupportedOperationException();

	}

	private static final String CONTEXT_CAPTION = "context_caption";

	@Override
	public SLContext getContext(String id) {
		SLContext ctx = contextCache.get(id);
		if (ctx == null) {
			STStorageSession session = this.sessionProvider.get();
			STPartition partition = factory.getPartitionByName(id);
			STNodeEntry contextNode = session.withPartition(partition)
					.createCriteria().withNodeEntry(id).buildCriteria()
					.andFindUnique(session);
			String caption = null;
			if (contextNode == null) {
				contextNode = session.withPartition(partition)
						.createNewSimpleNode(id);
			} else {
				caption = contextNode.getPropertyAsString(session,
						CONTEXT_CAPTION);
			}
			SLNode contextAsSLNode = convertToSLNode(null, id, contextNode);

			ctx = new SLContextImpl(caption, id, contextAsSLNode);
			contextCache.put(id, ctx);
		}
		return ctx;

	}

	private SLNode convertToSLNode(String parentId, String contextId,
			STNodeEntry rawStNode) {
		STStorageSession session = sessionProvider.get();
		SLNode node = SLNodeFactory.createNode(factory, session, contextId,
				parentId, ContextSLNode.class, rawStNode.getNodeEntryName(),
				null, null);
		return node;
	}

	@Override
	public SLContext getContext(SLNode node) {
		return node != null ? getContext(node.getContextId()) : null;
	}

	@Override
	public <L extends SLLink> L getLink(Class<L> linkClass, SLNode source,
			SLNode target, SLLinkDirection linkDirection) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<SLNode> getLinkedNodes(Class<? extends SLLink> linkClass) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<SLNode> getLinkedNodes(Class<? extends SLLink> linkClass,
			SLNode node, SLLinkDirection linkDirection) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <N extends SLNode> Iterable<N> getLinkedNodes(SLNode node,
			Class<N> nodeClass, boolean returnSubTypes,
			SLLinkDirection linkDirection) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<SLNode> getLinkedNodes(SLNode node,
			SLLinkDirection linkDirection) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<SLLink> getLinks(SLNode source, SLNode target,
			SLLinkDirection linkDirection) {
		throw new UnsupportedOperationException();

	}

	@Override
	public SLMetaLink getMetaLink(SLLink link) {
		throw new UnsupportedOperationException();

	}

	@Override
	public SLMetaNodeType getMetaType(SLNode node) {
		throw new UnsupportedOperationException();

	}

	@Override
	public SLMetadata getMetadata() {
		throw new UnsupportedOperationException();

	}

	@Override
	public SLNode getNode(String id) throws SLNodeNotFoundException {
		throw new UnsupportedOperationException();

	}

	@Override
	public SLNode getParentNode(SLNode node) {
		STStorageSession session = sessionProvider.get();
		STPartition partition = this.factory.getPartitionByName(node
				.getContextId());
		STNodeEntry parentStNode = session.withPartition(partition)
				.createCriteria().withUniqueKeyAsString(node.getId())
				.buildCriteria().andFindUnique(session);
		return convertToSLNode(parentStNode != null ? parentStNode
				.getUniqueKey().getParentKeyAsString() : null, node
				.getContextId(), parentStNode);

	}

	@Override
	public <L extends SLLink> Iterable<L> getUnidirectionalLinksBySource(
			Class<L> linkClass, SLNode source) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<SLLink> getUnidirectionalLinksBySource(SLNode source) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <L extends SLLink> Iterable<L> getUnidirectionalLinksByTarget(
			Class<L> linkClass, SLNode target) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<SLLink> getUnidirectionalLinksByTarget(SLNode target) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <T extends SLNode> Iterable<T> findNodes(Class<T> clazz,
			String name, final SLContext context,
			SLContext... aditionalContexts) {
		STStorageSession session = sessionProvider.get();
		Iterable<STNodeEntry> nodes = session.withPartition(
				factory.getPartitionByName(context.getID())).createCriteria()
				.withNodeEntry(clazz.getName())
				.withProperty(SLNodeFactory.NAME).equalsTo(name)
				.buildCriteria().andFind(session);
		Iterable<T> result = IteratorBuilder
				.<T, STNodeEntry> createIteratorBuilder().withConverter(
						new Converter<T, STNodeEntry>() {

							@Override
							public T convert(STNodeEntry o) throws Exception {
								return (T) convertToSLNode(o.getUniqueKey()
										.getParentKeyAsString(), context
										.getID(), o);
							}
						}).withItems(nodes).andBuild();

		return result;

	}

	@Override
	public Iterable<SLNode> findNodes(String name, final SLContext context,
			SLContext... aditionalContexts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends SLNode> Iterable<T> findNodes(Class<T> clazz,
			final SLContext context, SLContext... aditionalContexts) {
		STStorageSession session = sessionProvider.get();
		Iterable<STNodeEntry> nodes = session.withPartition(
				factory.getPartitionByName(context.getID())).findNamed(
				clazz.getName());
		Iterable<T> result = IteratorBuilder
				.<T, STNodeEntry> createIteratorBuilder().withConverter(
						new Converter<T, STNodeEntry>() {

							@Override
							public T convert(STNodeEntry o) throws Exception {
								return (T) convertToSLNode(o.getUniqueKey()
										.getParentKeyAsString(), context
										.getID(), o);
							}
						}).withItems(nodes).andBuild();

		return result;

	}

	@Override
	public <T extends SLNode> T findUniqueNode(Class<T> clazz, String name,
			SLContext context, SLContext... aditionalContexts) {
		throw new UnsupportedOperationException();

	}

	@Override
	public SLNode findUniqueNode(String name, SLContext context,
			SLContext... aditionalContexts) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <T extends SLNode> T findUniqueNode(Class<T> clazz,
			SLContext context, SLContext... aditionalContexts) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <N extends SLNode> Iterable<N> getLinkedNodes(
			Class<? extends SLLink> linkClass, SLNode node, Class<N> nodeClass,
			boolean returnSubTypes, SLLinkDirection linkDirection) {
		throw new UnsupportedOperationException();

	}

}
