/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Class.forName;
import static org.openspotlight.common.util.Conversion.convert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.common.collection.IteratorBuilder.NextItemReferee;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.graph.internal.NodeAndLinkSupport;
import org.openspotlight.graph.internal.NodeAndLinkSupport.NodeMetadata;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.metadata.MetaLinkType;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.query.InvalidQuerySyntaxException;
import org.openspotlight.graph.query.QueryApi;
import org.openspotlight.graph.query.QueryText;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.STStorageSession.STCriteriaBuilder;
import org.openspotlight.storage.domain.node.STLinkEntry;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Provider;

public class GraphReaderImpl implements GraphReader {

	private final STPartitionFactory factory;
	private final Map<String, Context> contextCache = newHashMap();

	public GraphReaderImpl(Provider<STStorageSession> sessionProvider,
			GraphLocation location, STPartitionFactory factory) {
		this.location = location;
		this.sessionProvider = sessionProvider;
		this.factory = factory;
	}

	private final Provider<STStorageSession> sessionProvider;
	private final GraphLocation location;

	@Override
	public <L extends Link> Iterable<L> getBidirectionalLinks(
			Class<L> linkClass, Node side) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Link> getBidirectionalLinks(Node side) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Node> T getChildNode(Node node, Class<T> clazz,
			String name) {
		throw new UnsupportedOperationException();

	}

	private Iterable<Node> internalGetChildrenNodes(final Node node,
			Class<?> clazz, final String name) {
		final STStorageSession session = sessionProvider.get();
		STPartition partition = this.factory.getPartitionByName(node
				.getContextId());
		NodeMetadata md = (NodeMetadata) node;

		STNodeEntry parentStNode = md.getCached();
		if (parentStNode == null) {
			parentStNode = session.withPartition(partition).createCriteria()
					.withUniqueKeyAsString(node.getId()).buildCriteria()
					.andFindUnique(session);
		}
		Iterable<STNodeEntry> children;
		if (clazz != null) {
			children = parentStNode.getChildrenNamed(partition, session, clazz
					.getName());
		} else {
			children = parentStNode.getChildren(partition, session);
		}
		return IteratorBuilder.<Node, STNodeEntry> createIteratorBuilder()
				.withConverter(new Converter<Node, STNodeEntry>() {

					@Override
					public Node convert(STNodeEntry o) throws Exception {
						return convertToSLNode(node.getId(), o.getUniqueKey()
								.getPartition().getPartitionName(), o, false);
					}
				}).withItems(children).withReferee(
						new NextItemReferee<STNodeEntry>() {
							public boolean canAcceptAsNewItem(STNodeEntry o)
									throws Exception {
								if (name == null)
									return true;
								return name.equals(o.getPropertyAsString(
										session, NodeAndLinkSupport.NAME));

							}
						}).andBuild();

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> getChildrenNodes(Node node,
			Class<T> clazz) {
		return (Iterable<T>) internalGetChildrenNodes(node, clazz, null);
	}

	private static final String CONTEXT_CAPTION = "context_caption";

	@Override
	public Context getContext(String id) {
		Context ctx = contextCache.get(id);
		if (ctx == null) {
			STStorageSession session = this.sessionProvider.get();
			STPartition partition = factory.getPartitionByName(id);
			STNodeEntry contextNode = session.withPartition(partition)
					.createCriteria().withNodeEntry(id).buildCriteria()
					.andFindUnique(session);
			String caption = null;
			Map<String, Serializable> properties = new HashMap<String, Serializable>();
			int weigth;
			if (contextNode == null) {

				weigth = NodeAndLinkSupport
						.findInitialWeight(ContextImpl.class);
				contextNode = session.withPartition(partition)
						.createNewSimpleNode(id);
				contextNode.setIndexedProperty(session,
						NodeAndLinkSupport.WEIGTH_VALUE, convert(weigth,
								String.class));
				session.flushTransient();
			} else {
				caption = contextNode.getPropertyAsString(session,
						CONTEXT_CAPTION);
				String weigthAsString = contextNode.getPropertyAsString(
						session, NodeAndLinkSupport.WEIGTH_VALUE);
				weigth = convert(weigthAsString, Integer.class);
				Set<String> names = contextNode.getPropertyNames(session);
				for (String propertyName : names) {
					if (propertyName.equals(NodeAndLinkSupport.WEIGTH_VALUE)
							|| propertyName.equals(CONTEXT_CAPTION)) {
						continue;
					}
					properties.put(propertyName, Conversion.convert(contextNode
							.getPropertyAsBytes(session, propertyName),
							Serializable.class));
				}

			}

			ctx = new ContextImpl(id, properties, caption, weigth);
			contextCache.put(id, ctx);
		}
		return ctx;

	}

	private Node convertToSLNode(String parentId, String contextId,
			STNodeEntry rawStNode, boolean needsToVerifyType) {
		try {
			STStorageSession session = sessionProvider.get();
			String clazzName = rawStNode.getPropertyAsString(session,
					NodeAndLinkSupport.CORRECT_CLASS);
			Class<?> clazz = forName(clazzName);
			Node node = NodeAndLinkSupport.createNode(factory, session,
					contextId, parentId, (Class<? extends Node>) clazz,
					rawStNode.getPropertyAsString(session,
							NodeAndLinkSupport.NAME), needsToVerifyType, null,
					null);
			return node;
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	@Override
	public Context getContext(Node node) {
		return node != null ? getContext(node.getContextId()) : null;
	}

	@Override
	public MetaLinkType getMetaType(Link link) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MetaNodeType getMetaType(Node node) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Metadata getMetadata() {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<Node> getNode(String id) {
		STStorageSession session = sessionProvider.get();
		String contextId = StringIDSupport.getPartitionName(id);
		STPartition partition = this.factory.getPartitionByName(contextId);
		STNodeEntry parentStNode = session.withPartition(partition)
				.createCriteria().withUniqueKeyAsString(id).buildCriteria()
				.andFindUnique(session);
		if (parentStNode == null)
			return null;
		return SLCollections.iterableOf(convertToSLNode(parentStNode
				.getUniqueKey().getParentKeyAsString(), contextId,
				parentStNode, false), null);

	}

	@Override
	public Node getParentNode(Node node) {
		STStorageSession session = sessionProvider.get();
		STPartition partition = this.factory.getPartitionByName(node
				.getContextId());
		STNodeEntry parentStNode = session.withPartition(partition)
				.createCriteria().withUniqueKeyAsString(node.getId())
				.buildCriteria().andFindUnique(session);

		if (parentStNode == null)
			return null;
		return convertToSLNode(parentStNode.getUniqueKey()
				.getParentKeyAsString(), node.getContextId(), parentStNode,
				false);

	}

	@Override
	public <L extends Link> Iterable<L> getUnidirectionalLinksBySource(
			Class<L> linkClass, Node source) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<Link> getUnidirectionalLinksBySource(Node source) {
		throw new UnsupportedOperationException();

	}

	@Override
	public <L extends Link> Iterable<L> getUnidirectionalLinksByTarget(
			Class<L> linkClass, Node target) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterable<Link> getUnidirectionalLinksByTarget(Node target) {
		throw new UnsupportedOperationException();

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> findNodesByCaption(Class<T> clazz,
			String caption, boolean returnSubTypes, Context context,
			Context... aditionalContexts) throws IllegalArgumentException {
		Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
				returnSubTypes, null, null, null, caption, SLCollections
						.iterableOf(context, aditionalContexts));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> findNodesByCaption(Class<T> clazz,
			String caption, boolean returnSubTypes)
			throws IllegalArgumentException {
		Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
				returnSubTypes, null, null, null, caption, null);
		return result;
	}

	@Override
	public Iterable<Node> findNodesByCaption(String caption, Context context,
			Context... aditionalContexts) throws IllegalArgumentException {
		Iterable<Node> result = internalFindNodes(null, true, null, null, null,
				caption, SLCollections.iterableOf(context, aditionalContexts));
		return result;
	}

	@Override
	public Iterable<Node> findNodesByCaption(String caption)
			throws IllegalArgumentException {
		Iterable<Node> result = internalFindNodes(null, true, null, null, null,
				caption, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> findNodesByCustomProperty(
			Class<T> clazz, String propertyName, Serializable value,
			boolean returnSubTypes, Context context,
			Context... aditionalContexts) throws IllegalArgumentException {
		Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
				returnSubTypes, propertyName, value, null, null, SLCollections
						.iterableOf(context, aditionalContexts));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> findNodesByCustomProperty(
			Class<T> clazz, String propertyName, Serializable value,
			boolean returnSubTypes) throws IllegalArgumentException {
		Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
				returnSubTypes, propertyName, value, null, null, null);
		return result;
	}

	@Override
	public Iterable<Node> findNodesByCustomProperty(String propertyName,
			Serializable value, Context context, Context... aditionalContexts)
			throws IllegalArgumentException {
		Iterable<Node> result = internalFindNodes(null, true, propertyName,
				value, null, null, SLCollections.iterableOf(context,
						aditionalContexts));
		return result;
	}

	@Override
	public Iterable<Node> findNodesByCustomProperty(String propertyName,
			Serializable value) throws IllegalArgumentException {
		Iterable<Node> result = internalFindNodes(null, true, propertyName,
				value, null, null, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> findNodesByName(Class<T> clazz,
			String name, boolean returnSubTypes, final Context context,
			Context... aditionalContexts) throws IllegalArgumentException {
		Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
				returnSubTypes, null, null, name, null, SLCollections
						.iterableOf(context, aditionalContexts));
		return result;
	}

	private final Iterable<STPartition> filterGraphPartitions(
			STPartition[] partition) {
		// TODO filter
		return SLCollections.iterableOf(partition);

	}

	private <T extends Node> Iterable<Node> internalFindNodes(
			final Class<T> clazz, final boolean returnSubTypes,
			final String propertyName, final Serializable propertyValue,
			String nodeName, String caption,
			final Iterable<Context> initialContexts) {
		STStorageSession session = sessionProvider.get();
		ImmutableSet.Builder<Iterable<STNodeEntry>> resultBuilder = ImmutableSet
				.builder();
		Iterable<Context> contexts = findContextsIfNecessary(initialContexts);
		for (Context c : contexts) {
			STPartition partition = factory.getPartitionByName(c.getId());
			Iterable<Class<?>> types = findTypesIfNecessary(clazz, session,
					partition);
			for (Class<?> clzz : types) {
				fillResultBuilderWithQueryResults(returnSubTypes, propertyName,
						propertyValue, nodeName, caption, session,
						resultBuilder, partition, clzz);
			}
		}

		ImmutableSet.Builder<Iterable<Node>> result = ImmutableSet.builder();
		for (Iterable<STNodeEntry> results : resultBuilder.build()) {
			result.add(IteratorBuilder
					.<Node, STNodeEntry> createIteratorBuilder().withConverter(
							new Converter<Node, STNodeEntry>() {

								@Override
								public Node convert(STNodeEntry o)
										throws Exception {
									return convertToSLNode(o.getUniqueKey()
											.getParentKeyAsString(), o
											.getUniqueKey().getPartition()
											.getPartitionName(), o, false);
								}
							}).withItems(results).andBuild());

		}

		return SLCollections.iterableOfAll(result.build());
	}

	private void fillResultBuilderWithQueryResults(
			final boolean returnSubTypes, final String propertyName,
			final Serializable propertyValue, String nodeName, String caption,
			STStorageSession session,
			ImmutableSet.Builder<Iterable<STNodeEntry>> resultBuilder,
			STPartition partition, Class<?> clzz) {
		STCriteriaBuilder criteriaBuilder = session.withPartition(partition)
				.createCriteria().withNodeEntry(clzz.getName());
		if (nodeName != null) {
			criteriaBuilder.withProperty(NodeAndLinkSupport.NAME).equalsTo(
					nodeName);
		}
		if (caption != null) {
			criteriaBuilder.withProperty(NodeAndLinkSupport.CAPTION).equalsTo(
					caption);
		}
		if (propertyName != null) {
			criteriaBuilder.withProperty(propertyName).equalsTo(
					Conversion.convert(propertyValue, String.class));
		}
		if (!returnSubTypes) {
			criteriaBuilder.and()
					.withProperty(NodeAndLinkSupport.CORRECT_CLASS).equals(
							clzz.getName());
		}
		resultBuilder.add(criteriaBuilder.buildCriteria().andFind(session));
	}

	private <T> Iterable<Class<?>> findTypesIfNecessary(final Class<T> clazz,
			STStorageSession session, STPartition partition) {
		Iterable<Class<?>> typesToFind = clazz != null ? ImmutableSet
				.<Class<?>> of(NodeAndLinkSupport.findTargetClass(clazz))
				: null;
		if (typesToFind == null) {
			Iterable<String> stNodeNames = session.withPartition(partition)
					.getAllNodeNames();
			Builder<Class<?>> builder = ImmutableSet.builder();
			for (String s : stNodeNames) {
				try {
					Class<?> stClazz = NodeAndLinkSupport.findTargetClass(Class
							.forName(s));
					if (Node.class.isAssignableFrom(stClazz)) {
						builder.add(stClazz);
					}
				} catch (Exception e) {
					Exceptions.catchAndLog(e);
				}
			}
			typesToFind = builder.build();
		}
		return typesToFind;
	}

	private Iterable<Context> findContextsIfNecessary(Iterable<Context> contexts) {
		if (contexts == null || !contexts.iterator().hasNext()) {
			Iterable<STPartition> partitions = filterGraphPartitions(factory
					.getValues());
			ImmutableSet.Builder<Context> builder = ImmutableSet.builder();
			for (STPartition p : partitions) {
				builder.add(getContext(p.getPartitionName()));
			}
			contexts = builder.build();
		}
		return contexts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> findNodesByName(Class<T> clazz,
			String name, boolean returnSubTypes)
			throws IllegalArgumentException {
		Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
				returnSubTypes, null, null, name, null, null);
		return result;
	}

	@Override
	public Iterable<Node> findNodesByName(String name, Context context,
			Context... aditionalContexts) throws IllegalArgumentException {
		Iterable<Node> result = internalFindNodes(null, true, null, null, name,
				null, SLCollections.iterableOf(context, aditionalContexts));
		return result;
	}

	@Override
	public Iterable<Node> findNodesByName(String name)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Node> Iterable<T> getChildrenNodes(Node node)
			throws IllegalArgumentException {
		return (Iterable<T>) internalGetChildrenNodes(node, null, null);
	}

	@Override
	public <L extends Link> L getLink(Class<L> linkTypeClass, Node source,
			Node target, LinkType linkDirection)
			throws IllegalArgumentException {
		return NodeAndLinkSupport.createLink(factory, this.sessionProvider
				.get(), linkTypeClass, source, target, linkDirection, false);

	}

	@Override
	public Iterable<Node> getLinkedNodes(final Class<? extends Link> linkClass,
			Node node, LinkType linkDirection) throws IllegalArgumentException {
		return (Iterable<Node>) IteratorBuilder
				.<Node, Link> createIteratorBuilder().withItems(
						internalGetLinks(null, node, null, linkDirection))
				.withConverter(new Converter<Node, Link>() {
					@Override
					public Node convert(Link o) throws Exception {
						return o.getTarget();
					}
				}).withReferee(new NextItemReferee<Link>() {

					@Override
					public boolean canAcceptAsNewItem(Link o) throws Exception {
						return o.getClass().equals(linkClass);
					}
				}).andBuild();
	}

	@Override
	public <N extends Node> Iterable<N> getLinkedNodes(Node node,
			final Class<N> nodeClass, final boolean returnSubTypes,
			LinkType linkDirection) throws IllegalArgumentException {
		return (Iterable<N>) IteratorBuilder
				.<Node, Link> createIteratorBuilder().withItems(
						internalGetLinks(null, node, null, linkDirection))
				.withConverter(new Converter<Node, Link>() {
					@Override
					public Node convert(Link o) throws Exception {
						return o.getTarget();
					}
				}).withReferee(new NextItemReferee<Link>() {

					@Override
					public boolean canAcceptAsNewItem(Link o) throws Exception {
						if (!returnSubTypes)
							o.getTarget().getClass().equals(nodeClass);
						return o.getTarget().getClass().isAssignableFrom(
								nodeClass);
					}
				}).andBuild();

	}

	@Override
	public Iterable<Node> getLinkedNodes(Node node, LinkType linkDirection)
			throws IllegalArgumentException {
		return (Iterable<Node>) IteratorBuilder
				.<Node, Link> createIteratorBuilder().withItems(
						internalGetLinks(null, node, null, linkDirection))
				.withConverter(new Converter<Node, Link>() {
					@Override
					public Node convert(Link o) throws Exception {
						return o.getTarget();
					}
				}).andBuild();
	}

	@Override
	public Iterable<Link> getLinks(Node rawSource, Node rawTarget,
			LinkType linkDirection) throws IllegalArgumentException {
		return internalGetLinks(null, rawSource, rawTarget, linkDirection);
	}

	private Node findNode(STNodeEntry o) {
		return convertToSLNode(o.getUniqueKey().getParentKeyAsString(), o
				.getUniqueKey().getPartition().getPartitionName(), o, false);
	}

	@SuppressWarnings("unchecked")
	private Iterable<Link> internalGetLinks(Class<? extends Link> linkType,
			Node rawOrigin, Node rawTarget, LinkType linkDirection) {
		if (LinkType.ANY.equals(linkDirection)) {
			return SLCollections.iterableOfAll(internalGetLinks(linkType,
					rawOrigin, rawTarget, LinkType.BIDIRECTIONAL),
					internalGetLinks(linkType, rawOrigin, rawTarget,
							LinkType.UNIDIRECTIONAL));
		}

		if (rawTarget != null && LinkType.BIDIRECTIONAL.equals(linkDirection)
				&& rawOrigin.compareTo(rawTarget) < 0) {
			return internalGetLinks(linkType, rawTarget, rawOrigin,
					linkDirection);
		}
		final STStorageSession session = sessionProvider.get();
		Iterable<STLinkEntry> links;
		if (rawTarget != null && linkType != null) {
			links = SLCollections.iterableOf(
					session.getLink(session.findNodeByStringId(rawOrigin
							.getId()), session.findNodeByStringId(rawTarget
							.getId()), linkType.getName()), null);
		} else if (rawTarget != null) {
			links = session.findLinks(session.findNodeByStringId(rawOrigin
					.getId()), session.findNodeByStringId(rawTarget.getId()));
		} else if (linkType != null) {
			links = session.findLinks(session.findNodeByStringId(rawOrigin
					.getId()), linkType.getName());

		} else {
			links = session.findLinks(session.findNodeByStringId(rawOrigin
					.getId()));

		}

		return IteratorBuilder.<Link, STLinkEntry> createIteratorBuilder()
				.withConverter(
						new IteratorBuilder.Converter<Link, STLinkEntry>() {

							@Override
							public Link convert(STLinkEntry o) throws Exception {
								Class<? extends Link> linkType = (Class<? extends Link>) Class
										.forName(o.getLinkName());
								Node origin = findNode(o.getOrigin());
								Node target = findNode(o.getTarget());
								Link result = NodeAndLinkSupport.createLink(
										factory, session, linkType, origin,
										target, LinkType.UNIDIRECTIONAL, false);
								return result;
							}
						}).withReferee(new NextItemReferee<STLinkEntry>() {

					@Override
					public boolean canAcceptAsNewItem(STLinkEntry o)
							throws Exception {
						try {
							Class<?> clazz = Class.forName(o.getLinkName());
							if (!Link.class.isAssignableFrom(clazz))
								return false;
						} catch (ClassNotFoundException e) {
							return false;
						}
						return true;

					}
				}).withItems(links).andBuild();

	}

	@Override
	public Node getNode(Context context, String id)
			throws IllegalArgumentException {
		STStorageSession session = sessionProvider.get();
		String contextId = context.getId();
		STPartition partition = this.factory.getPartitionByName(contextId);
		STNodeEntry parentStNode = session.withPartition(partition)
				.createCriteria().withUniqueKeyAsString(id).buildCriteria()
				.andFindUnique(session);
		if (parentStNode == null)
			return null;
		return (Node) SLCollections.iterableOf(convertToSLNode(parentStNode
				.getUniqueKey().getParentKeyAsString(), contextId,
				parentStNode, false), null);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterable<T> listNodes(Class<T> clazz,
			boolean returnSubTypes, Context context,
			Context... aditionalContexts) throws IllegalArgumentException {
		return (Iterable<T>) internalFindNodes(clazz, returnSubTypes, null,
				null, null, null, SLCollections.iterableOf(context,
						aditionalContexts));
	}

	@Override
	public <T extends Node> Iterable<T> listNodes(Class<T> clazz,
			boolean returnSubTypes) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	@Override
	public QueryApi createQueryApi() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	@Override
	public QueryText createQueryText(String query)
			throws IllegalArgumentException, InvalidQuerySyntaxException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	@Override
	public <N extends Node> Iterable<N> getLinkedNodes(
			Class<? extends Link> linkClass, Node node, Class<N> nodeClass,
			boolean returnSubTypes, LinkType linkDirection)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

}
