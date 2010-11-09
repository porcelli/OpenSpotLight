/**
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores
 * terceiros indicados pela etiqueta
 * 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de
 *         terceiros estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa
 *         é software livre; você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU
 *         conforme publicada pela Free Software Foundation. Este programa é distribuído na expectativa de que seja útil,
 *         porém, SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE
 *         ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes. Você deve ter recebido uma cópia da
 *         Licença Pública Geral Menor do GNU junto com este programa; se não, escreva para: Free Software Foundation, Inc. 51
 *         Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */

package org.openspotlight.graph;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Class.forName;
import static org.openspotlight.common.util.Conversion.convert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.common.collection.IteratorBuilder.NextItemReferee;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.common.util.SerializationUtil;
import org.openspotlight.graph.internal.NodeAndLinkSupport;
import org.openspotlight.graph.internal.NodeAndLinkSupport.PropertyContainerMetadata;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.metadata.MetaLinkType;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.query.InvalidQuerySyntaxException;
import org.openspotlight.graph.query.QueryApi;
import org.openspotlight.graph.query.QueryText;
import org.openspotlight.storage.SearchCriteria.CriteriaBuilder;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.StringKeysSupport;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Provider;

public class GraphReaderImpl implements GraphReader {

    private static final String            CONTEXT_CAPTION = "context_caption";
    private final Map<String, Context>     contextCache    = newHashMap();

    private final PartitionFactory         factory;

    private final GraphLocation            location;
    private final Provider<StorageSession> sessionProvider;

    public GraphReaderImpl(final Provider<StorageSession> sessionProvider,
                           final GraphLocation location, final PartitionFactory factory) {
        this.location = location;
        this.sessionProvider = sessionProvider;
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    private Node convertToSLNode(
                                 final String parentId, final String contextId,
                                 final StorageNode rawStNode, final boolean needsToVerifyType) {
        try {
            final StorageSession session = sessionProvider.get();
            final String clazzName = rawStNode.getPropertyValueAsString(session,
                NodeAndLinkSupport.CORRECT_CLASS);
            final Class<?> clazz = forName(clazzName);
            final Node node = NodeAndLinkSupport.createNode(factory, session,
                contextId, parentId, (Class<? extends Node>) clazz,
                rawStNode.getPropertyValueAsString(session,
                    NodeAndLinkSupport.NAME), needsToVerifyType, null,
                null);
            return node;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    private void fillResultBuilderWithQueryResults(
                                                   final boolean returnSubTypes,
                                                   final String propertyName,
                                                   final Serializable propertyValue,
                                                   final String nodeName,
                                                   final String caption,
                                                   final StorageSession session,
                                                   final ImmutableSet.Builder<Iterable<StorageNode>> resultBuilder,
                                                   final Partition partition, final Class<?> clzz) {
        final CriteriaBuilder criteriaBuilder = session.withPartition(partition)
            .createCriteria().withNodeType(clzz.getName());
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
        resultBuilder.add(criteriaBuilder.buildCriteria().andSearch(session));
    }

    private final Iterable<Partition> filterGraphPartitions(
                                                            final Partition[] partition) {
        // TODO filter
        return SLCollections.iterableOf(partition);

    }

    private Iterable<Context> findContextsIfNecessary(
                                                      Iterable<Context> contexts) {
        if (contexts == null || !contexts.iterator().hasNext()) {
            final Iterable<Partition> partitions = filterGraphPartitions(factory
                .getValues());
            final ImmutableSet.Builder<Context> builder = ImmutableSet.builder();
            for (final Partition p: partitions) {
                builder.add(getContext(p.getPartitionName()));
            }
            contexts = builder.build();
        }
        return contexts;
    }

    private Node findNode(
                          final StorageNode o) {
        return convertToSLNode(o.getKey().getParentKeyAsString(), o
            .getKey().getPartition().getPartitionName(), o, false);
    }

    private <T> Iterable<Class<?>> findTypesIfNecessary(
                                                        final Class<T> clazz,
                                                        final StorageSession session, final Partition partition) {
        Iterable<Class<?>> typesToFind = clazz != null ? ImmutableSet
            .<Class<?>>of(NodeAndLinkSupport.findTargetClass(clazz))
            : null;
        if (typesToFind == null) {
            final Iterable<String> stNodeNames = session.withPartition(partition)
                .getAllNodeTypes();
            final Builder<Class<?>> builder = ImmutableSet.builder();
            for (final String s: stNodeNames) {
                try {
                    final Class<?> stClazz = NodeAndLinkSupport.findTargetClass(Class
                        .forName(s));
                    if (Node.class.isAssignableFrom(stClazz)) {
                        builder.add(stClazz);
                    }
                } catch (final Exception e) {
                    Exceptions.catchAndLog(e);
                }
            }
            typesToFind = builder.build();
        }
        return typesToFind;
    }

    @SuppressWarnings("unchecked")
    private <T extends Node> Iterable<Node> internalFindNodes(
                                                              final Class<T> clazz, final boolean returnSubTypes,
                                                              final String propertyName, final Serializable propertyValue,
                                                              final String nodeName, final String caption,
                                                              final Iterable<Context> initialContexts) {
        final StorageSession session = sessionProvider.get();
        final ImmutableSet.Builder<Iterable<StorageNode>> resultBuilder = ImmutableSet
            .builder();
        final Iterable<Context> contexts = findContextsIfNecessary(initialContexts);
        for (final Context c: contexts) {
            final Partition partition = factory.getPartition(c.getId());
            final Iterable<Class<?>> types = findTypesIfNecessary(clazz, session,
                partition);
            for (final Class<?> clzz: types) {
                fillResultBuilderWithQueryResults(returnSubTypes, propertyName,
                    propertyValue, nodeName, caption, session,
                    resultBuilder, partition, clzz);
            }
        }

        final ImmutableSet.Builder<Iterable<Node>> result = ImmutableSet.builder();
        for (final Iterable<StorageNode> results: resultBuilder.build()) {
            result.add(IteratorBuilder
                .<Node, StorageNode>createIteratorBuilder().withConverter(
                    new Converter<Node, StorageNode>() {

                        @Override
                        public Node convert(
                                            final StorageNode o)
                            throws Exception {
                            return convertToSLNode(o.getKey()
                                .getParentKeyAsString(), o
                                .getKey().getPartition()
                                .getPartitionName(), o, false);
                        }
                    }).withItems(results).andBuild());

        }

        return SLCollections.iterableOfAll(result.build());
    }

    @SuppressWarnings("unchecked")
    private Iterable<Node> internalGetChildrenNodes(
                                                    final Node node,
                                                    final Class<?> clazz, final String name) {
        final StorageSession session = sessionProvider.get();
        final Partition partition = factory.getPartition(node
            .getContextId());
        final PropertyContainerMetadata<StorageNode> md =
            (PropertyContainerMetadata<StorageNode>) node;

        StorageNode parentStNode = md.getCached();
        if (parentStNode == null) {
            parentStNode = session.withPartition(partition).createCriteria()
                .withUniqueKeyAsString(node.getId()).buildCriteria()
                .andSearchUnique(session);
        }
        Iterable<StorageNode> children;
        if (clazz != null) {
            children = parentStNode.getChildren(partition, session, clazz
                .getName());
        } else {
            children = parentStNode.getChildren(partition, session);
        }
        return IteratorBuilder.<Node, StorageNode>createIteratorBuilder()
            .withConverter(new Converter<Node, StorageNode>() {

                @Override
                public Node convert(
                                    final StorageNode o)
                    throws Exception {
                    return convertToSLNode(node.getId(), o.getKey()
                        .getPartition().getPartitionName(), o, false);
                }
            }).withItems(children).withReferee(
                new NextItemReferee<StorageNode>() {
                    @Override
                    public boolean canAcceptAsNewItem(
                                                      final StorageNode o)
                        throws Exception {
                        if (name == null) {
                return true;
                }
                return name.equals(o.getPropertyValueAsString(
                    session, NodeAndLinkSupport.NAME));

            }
                }).andBuild();

    }

    @SuppressWarnings("unchecked")
    private Iterable<Link> internalGetLinks(
                                            final Class<? extends Link> linkType,
                                            final Node rawOrigin, final Node rawTarget, final LinkDirection linkDirection) {

        if (rawOrigin == null) { throw new NullPointerException(); }
        if (LinkDirection.ANY.equals(linkDirection)) { return SLCollections.iterableOfAll(internalGetLinks(linkType,
            rawOrigin, rawTarget, LinkDirection.BIDIRECTIONAL),
            internalGetLinks(linkType, rawOrigin, rawTarget,
                LinkDirection.UNIDIRECTIONAL)); }

        if (rawTarget != null && rawOrigin.compareTo(rawTarget) == 0) { throw new IllegalStateException(); }

        if (rawTarget != null && LinkDirection.BIDIRECTIONAL.equals(linkDirection)
            && rawOrigin.compareTo(rawTarget) < 0) { return internalGetLinks(linkType, rawTarget, rawOrigin,
            linkDirection); }
        final StorageSession session = sessionProvider.get();
        Iterable<StorageLink> links;

        final StorageNode stNode =
            NodeAndLinkSupport.retrievePreviousNode(factory, session, getContext(rawOrigin.getContextId()), rawOrigin, false);
        stNode.forceReload();

        final List<StorageLink> foundBidLinks =
            new LinkedList<StorageLink>();
        if (LinkDirection.BIDIRECTIONAL.equals(linkDirection)) {
            final List<String> linkIds =
                SerializationUtil
                    .deserialize(stNode.getPropertyValueAsStream(session, NodeAndLinkSupport.BIDIRECTIONAL_LINK_IDS));
            if (linkIds != null) {
                for (final String linkId: linkIds) {
                    final String rawAnotherOriginId = StringKeysSupport.getOriginKeyAsStringFromLinkKey(linkId);
                    if (linkType != null) {
                        final StorageLink found =
                            session.getLink(session.getNode(rawAnotherOriginId), stNode, linkType.getName());
                        if (found != null) {
                            foundBidLinks.add(found);
                        }
                    } else {
                        final Iterable<StorageLink> found =
                            session.getLinks(session.getNode(rawAnotherOriginId));
                        if (found != null) {
                            foundBidLinks.addAll(SLCollections.iterableToList(found));
                        }
                    }
                }
            }
        }

        if (rawTarget != null && linkType != null) {
            links = SLCollections.iterableOfOne(
                session.getLink(session.getNode(rawOrigin
                    .getId()), session.getNode(rawTarget
                    .getId()), linkType.getName()));
        } else if (rawTarget != null) {
            links = session.getLinks(session.getNode(rawOrigin
                .getId()), session.getNode(rawTarget.getId()));
        } else if (linkType != null) {
            links = session.getLinks(session.getNode(rawOrigin
                .getId()), linkType.getName());

        } else {
            links = session.getLinks(session.getNode(rawOrigin
                .getId()));

        }

        final Iterable<Link> result = IteratorBuilder.<Link, StorageLink>createIteratorBuilder()
            .withConverter(
                new IteratorBuilder.Converter<Link, StorageLink>() {

                    @Override
                    public Link convert(
                                        final StorageLink o)
                        throws Exception {
                        final Class<? extends Link> linkType = (Class<? extends Link>) Class
                            .forName(o.getType());
                        final Node origin = findNode(o.getSource());
                        final Node target = findNode(o.getTarget());
                        final Link result = NodeAndLinkSupport.createLink(
                            factory, session, linkType, origin,
                            target, LinkDirection.UNIDIRECTIONAL, false);
                        return result;
                    }
                }).withReferee(new NextItemReferee<StorageLink>() {

                @Override
                public boolean canAcceptAsNewItem(
                                                  final StorageLink o)
                    throws Exception {
                    try {
                        final Class<?> clazz = Class.forName(o.getType());
                        if (!Link.class.isAssignableFrom(clazz)) {
                    return false;
                    }
                    if (LinkDirection.ANY.equals(linkDirection)) {
                        return true;
                    }
                    final LinkDirection retrievedLinkDirection =
                        LinkDirection.valueOf(o.getPropertyValueAsString(session, NodeAndLinkSupport.LINK_DIRECTION));
                    return retrievedLinkDirection.equals(linkDirection);
                } catch (final ClassNotFoundException e) {
                    return false;
                }

            }
            }).withItems(SLCollections.iterableOfAll(links, foundBidLinks)).andBuild();

        return result;

    }

    @Override
    public QueryApi createQueryApi() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    @Override
    public QueryText createQueryText(
                                     final String query)
        throws IllegalArgumentException, InvalidQuerySyntaxException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> findNodesByCaption(
                                                           final Class<T> clazz,
                                                           final String caption, final boolean returnSubTypes)
        throws IllegalArgumentException {
        final Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
            returnSubTypes, null, null, null, caption, null);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> findNodesByCaption(
                                                           final Class<T> clazz,
                                                           final String caption, final boolean returnSubTypes,
                                                           final Context context,
                                                           final Context... aditionalContexts)
        throws IllegalArgumentException {
        final Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
            returnSubTypes, null, null, null, caption, SLCollections
                .iterableOf(context, aditionalContexts));
        return result;
    }

    @Override
    public Iterable<Node> findNodesByCaption(
                                             final String caption)
        throws IllegalArgumentException {
        final Iterable<Node> result = internalFindNodes(null, true, null, null, null,
            caption, null);
        return result;
    }

    @Override
    public Iterable<Node> findNodesByCaption(
                                             final String caption, final Context context,
                                             final Context... aditionalContexts)
        throws IllegalArgumentException {
        final Iterable<Node> result = internalFindNodes(null, true, null, null, null,
            caption, SLCollections.iterableOf(context, aditionalContexts));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> findNodesByCustomProperty(
                                                                  final Class<T> clazz, final String propertyName,
                                                                  final Serializable value,
                                                                  final boolean returnSubTypes)
        throws IllegalArgumentException {
        final Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
            returnSubTypes, propertyName, value, null, null, null);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> findNodesByCustomProperty(
                                                                  final Class<T> clazz, final String propertyName,
                                                                  final Serializable value,
                                                                  final boolean returnSubTypes, final Context context,
                                                                  final Context... aditionalContexts)
        throws IllegalArgumentException {
        final Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
            returnSubTypes, propertyName, value, null, null, SLCollections
                .iterableOf(context, aditionalContexts));
        return result;
    }

    @Override
    public Iterable<Node> findNodesByCustomProperty(
                                                    final String propertyName,
                                                    final Serializable value)
        throws IllegalArgumentException {
        final Iterable<Node> result = internalFindNodes(null, true, propertyName,
            value, null, null, null);
        return result;
    }

    @Override
    public Iterable<Node> findNodesByCustomProperty(
                                                    final String propertyName,
                                                    final Serializable value, final Context context,
                                                    final Context... aditionalContexts)
        throws IllegalArgumentException {
        final Iterable<Node> result = internalFindNodes(null, true, propertyName,
            value, null, null, SLCollections.iterableOf(context,
                aditionalContexts));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> findNodesByName(
                                                        final Class<T> clazz,
                                                        final String name, final boolean returnSubTypes)
        throws IllegalArgumentException {
        final Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
            returnSubTypes, null, null, name, null, null);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> findNodesByName(
                                                        final Class<T> clazz,
                                                        final String name, final boolean returnSubTypes, final Context context,
                                                        final Context... aditionalContexts)
        throws IllegalArgumentException {
        final Iterable<T> result = (Iterable<T>) internalFindNodes(clazz,
            returnSubTypes, null, null, name, null, SLCollections
                .iterableOf(context, aditionalContexts));
        return result;
    }

    @Override
    public Iterable<Node> findNodesByName(
                                          final String name)
        throws IllegalArgumentException {
        return internalFindNodes(null, true, null, null, name, null, null);
    }

    @Override
    public Iterable<Node> findNodesByName(
                                          final String name, final Context context,
                                          final Context... aditionalContexts)
        throws IllegalArgumentException {
        final Iterable<Node> result = internalFindNodes(null, true, null, null, name,
            null, SLCollections.iterableOf(context, aditionalContexts));
        return result;
    }

    @Override
    public <L extends Link> Iterable<L> getBidirectionalLinks(
                                                              final Class<L> linkClass, final Node side) {
        return (Iterable<L>) internalGetLinks(linkClass, side, null, LinkDirection.BIDIRECTIONAL);

    }

    @Override
    public Iterable<Link> getBidirectionalLinks(
                                                final Node side) {
        return internalGetLinks(null, side, null, LinkDirection.BIDIRECTIONAL);

    }

    @Override
    public <T extends Node> T getChildNode(
                                           final Node node, final Class<T> clazz,
                                           final String name) {
        return (T) internalGetChildrenNodes(node, clazz, name).iterator().next();

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> getChildrenNodes(final Context context,
                                                         final Class<T> clazz)
        throws IllegalArgumentException {
        return (Iterable<T>) internalFindNodes(clazz, true, null, null, null, null, SLCollections.iterableOfOne(context));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> getChildrenNodes(
                                                         final Node node)
        throws IllegalArgumentException {
        return (Iterable<T>) internalGetChildrenNodes(node, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> getChildrenNodes(
                                                         final Node node,
                                                         final Class<T> clazz) {
        return (Iterable<T>) internalGetChildrenNodes(node, clazz, null);
    }

    @Override
    public Context getContext(
                              final Node node) {
        return node != null ? getContext(node.getContextId()) : null;
    }

    @Override
    public Context getContext(final String id) {
        Context ctx = contextCache.get(id);
        if (ctx == null) {
            final StorageSession session = sessionProvider.get();
            final Partition partition = factory.getPartition(id);
            StorageNode contextNode = session.withPartition(partition)
                .createCriteria().withNodeType(id).buildCriteria()
                .andSearchUnique(session);
            String caption = null;
            final Map<String, Serializable> properties = new HashMap<String, Serializable>();
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
                caption = contextNode.getPropertyValueAsString(session,
                    CONTEXT_CAPTION);
                final String weigthAsString = contextNode.getPropertyValueAsString(
                    session, NodeAndLinkSupport.WEIGTH_VALUE);
                weigth = convert(weigthAsString, Integer.class);
                final Set<String> names = contextNode.getPropertyNames(session);
                for (final String propertyName: names) {
                    if (propertyName.equals(NodeAndLinkSupport.WEIGTH_VALUE)
                        || propertyName.equals(CONTEXT_CAPTION)) {
                        continue;
                    }
                    properties.put(propertyName, Conversion.convert(contextNode
                        .getPropertyValueAsBytes(session, propertyName),
                        Serializable.class));
                }

            }

            ctx = new ContextImpl(id, properties, caption, weigth);
            contextCache.put(id, ctx);
        }
        return ctx;

    }

    @Override
    public <L extends Link> L getLink(
                                      final Class<L> linkTypeClass, final Node source,
                                      final Node target, final LinkDirection linkDirection)
        throws IllegalArgumentException {
        return NodeAndLinkSupport.createLink(factory, sessionProvider
            .get(), linkTypeClass, source, target, linkDirection, false);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Node> Iterable<N> getLinkedNodes(
                                                       final Class<? extends Link> linkClass, final Node node,
                                                       final Class<N> nodeClass,
                                                       final boolean returnSubTypes, final LinkDirection linkDirection)
        throws IllegalArgumentException {
        return IteratorBuilder.<N, Link>createIteratorBuilder().withItems(internalGetLinks(linkClass, node, null, linkDirection))
            .withConverter(
                new Converter<N, Link>() {

                    @Override
                    public N convert(
                                     final Link o)
                        throws Exception {
                        return (N) o.getTarget();
                    }
                }).withReferee(new NextItemReferee<Link>() {

                @Override
                public boolean canAcceptAsNewItem(
                                                  final Link o)
                    throws Exception {
                    return nodeClass.isInstance(o.getTarget());
                }
            }).andBuild();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Node> getLinkedNodes(
                                         final Class<? extends Link> linkClass,
                                         final Node node, final LinkDirection linkDirection)
        throws IllegalArgumentException {
        return IteratorBuilder
            .<Node, Link>createIteratorBuilder().withItems(
                internalGetLinks(null, node, null, linkDirection))
            .withConverter(new Converter<Node, Link>() {
                @Override
                public Node convert(
                                    final Link o)
                    throws Exception {
                    return o.getTarget();
                }
            }).withReferee(new NextItemReferee<Link>() {

                @Override
                public boolean canAcceptAsNewItem(
                                                  final Link o)
                    throws Exception {
                    return o.getClass().equals(linkClass);
                }
            }).andBuild();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Node> Iterable<N> getLinkedNodes(
                                                       final Node node,
                                                       final Class<N> nodeClass, final boolean returnSubTypes,
                                                       final LinkDirection linkDirection)
        throws IllegalArgumentException {
        return IteratorBuilder
            .<Node, Link>createIteratorBuilder().withItems(
                internalGetLinks(null, node, null, linkDirection))
            .withConverter(new Converter<Node, Link>() {
                @Override
                public Node convert(
                                    final Link o)
                    throws Exception {
                    return o.getTarget();
                }
            }).withReferee(new NextItemReferee<Link>() {

                @Override
                public boolean canAcceptAsNewItem(
                                                  final Link o)
                    throws Exception {
                    if (!returnSubTypes) {
                        o.getTarget().getClass().equals(nodeClass);
                    }
                    return o.getTarget().getClass().isAssignableFrom(
                        nodeClass);
                }
            }).andBuild();

    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Node> getLinkedNodes(
                                         final Node node, final LinkDirection linkDirection)
        throws IllegalArgumentException {
        return IteratorBuilder
            .<Node, Link>createIteratorBuilder().withItems(
                internalGetLinks(null, node, null, linkDirection))
            .withConverter(new Converter<Node, Link>() {
                @Override
                public Node convert(
                                    final Link o)
                    throws Exception {
                    return o.getTarget();
                }
            }).andBuild();
    }

    @Override
    public Iterable<Link> getLinks(
                                   final Node rawSource, final Node rawTarget,
                                   final LinkDirection linkDirection)
        throws IllegalArgumentException {
        return internalGetLinks(null, rawSource, rawTarget, linkDirection);
    }

    @Override
    public Metadata getMetadata() {
        //TODO
        throw new UnsupportedOperationException();

    }

    @Override
    public MetaLinkType getMetaType(
                                    final Link link) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public MetaNodeType getMetaType(
                                    final Node node) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getNode(
                        final Context context, final String id)
        throws IllegalArgumentException {
        final StorageSession session = sessionProvider.get();
        final String contextId = context.getId();
        final Partition partition = factory.getPartition(contextId);
        final StorageNode parentStNode = session.withPartition(partition)
            .createCriteria().withUniqueKeyAsString(id).buildCriteria()
            .andSearchUnique(session);
        if (parentStNode == null) { return null; }
        return convertToSLNode(parentStNode
            .getKey().getParentKeyAsString(), contextId,
            parentStNode, false);

    }

    @Override
    public Iterable<Node> getNode(
                                  final String id) {
        final StorageSession session = sessionProvider.get();
        final String contextId = StringKeysSupport.getPartitionName(id);
        final Partition partition = factory.getPartition(contextId);
        final StorageNode parentStNode = session.withPartition(partition)
            .createCriteria().withUniqueKeyAsString(id).buildCriteria()
            .andSearchUnique(session);
        if (parentStNode == null) { return null; }
        return SLCollections.iterableOfOne(convertToSLNode(parentStNode
            .getKey().getParentKeyAsString(), contextId,
            parentStNode, false));

    }

    @Override
    public Node getParentNode(
                              final Node node) {
        final StorageSession session = sessionProvider.get();
        final Partition partition = factory.getPartition(node
            .getContextId());
        final StorageNode parentStNode = session.withPartition(partition)
            .createCriteria().withUniqueKeyAsString(node.getId())
            .buildCriteria().andSearchUnique(session);

        if (parentStNode == null) { return null; }
        return convertToSLNode(parentStNode.getKey()
            .getParentKeyAsString(), node.getContextId(), parentStNode,
            false);

    }

    @Override
    public TreeLineReference getTreeLineReferences(
                                                   final Element e) {
        return NodeAndLinkSupport.getTreeLineReferences(sessionProvider.get(), factory, e, null);
    }

    @Override
    public TreeLineReference getTreeLineReferences(
                                                   final Element e, final String artifactId) {
        return NodeAndLinkSupport.getTreeLineReferences(sessionProvider.get(), factory, e, artifactId);
    }

    @Override
    public <L extends Link> Iterable<L> getUnidirectionalLinksBySource(
                                                                       final Class<L> linkClass, final Node source) {
        return (Iterable<L>) internalGetLinks(linkClass, source, null, LinkDirection.UNIDIRECTIONAL);

    }

    @Override
    public Iterable<Link> getUnidirectionalLinksBySource(
                                                         final Node source) {
        return internalGetLinks(null, source, null, LinkDirection.UNIDIRECTIONAL);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> listNodes(
                                                  final Class<T> clazz,
                                                  final boolean returnSubTypes)
        throws IllegalArgumentException {
        final Iterable<Partition> partitions = filterGraphPartitions(factory.getValues());
        final LinkedList<Context> contexts = new LinkedList<Context>();
        for (final Partition p: partitions) {
            contexts.add(getContext(p.getPartitionName()));
        }
        return (Iterable<T>) internalFindNodes(clazz, returnSubTypes, null, null, null, null, contexts);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Iterable<T> listNodes(
                                                  final Class<T> clazz,
                                                  final boolean returnSubTypes, final Context context,
                                                  final Context... aditionalContexts)
        throws IllegalArgumentException {
        return (Iterable<T>) internalFindNodes(clazz, returnSubTypes, null,
            null, null, null, SLCollections.iterableOf(context,
                aditionalContexts));
    }

}
