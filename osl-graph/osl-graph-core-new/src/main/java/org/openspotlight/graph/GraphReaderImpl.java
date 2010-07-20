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
import java.util.Map;

import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.exception.NodeNotFoundException;
import org.openspotlight.graph.internal.NodeFactory;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.meta.SLMetaLink;
import org.openspotlight.graph.meta.SLMetaNodeType;
import org.openspotlight.graph.meta.SLMetadata;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.inject.Provider;

public class GraphReaderImpl implements GraphReader {

    private final STPartitionFactory   factory;
    private final Map<String, Context> contextCache = newHashMap();

    public GraphReaderImpl( Provider<STStorageSession> sessionProvider,
                            GraphLocation location, STPartitionFactory factory ) {
        this.location = location;
        this.sessionProvider = sessionProvider;
        this.factory = factory;
    }

    private final Provider<STStorageSession> sessionProvider;
    private final GraphLocation              location;

    @Override
    public SLQueryApi createQueryApi() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SLQueryText createQueryText( String slqlInput )
            throws SLInvalidQuerySyntaxException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <L extends Link> Iterable<L> getBidirectionalLinks(
                                                               Class<L> linkClass,
                                                               Node side ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Link> getBidirectionalLinks( Node side ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Node> T getChildNode( Node node,
                                            Class<T> clazz,
                                            String name ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <T extends Node> Iterable<T> getChildrenNodes( Node node,
                                                          Class<T> clazz ) {
        throw new UnsupportedOperationException();

    }

    private static final String CONTEXT_CAPTION = "context_caption";

    @Override
    public Context getContext( String id ) {
        Context ctx = contextCache.get(id);
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
                contextNode.setIndexedProperty(session,
                                               NodeFactory.CORRECT_CLASS, ContextNode.class
                                                                                           .getName());
            } else {
                caption = contextNode.getPropertyAsString(session,
                                                          CONTEXT_CAPTION);
            }
            Node contextAsSLNode = convertToSLNode(null, id, contextNode);

            ctx = new ContextImpl(caption, id, contextAsSLNode);
            contextCache.put(id, ctx);
        }
        return ctx;

    }

    private Node convertToSLNode( String parentId,
                                  String contextId,
                                  STNodeEntry rawStNode ) {
        try {
            STStorageSession session = sessionProvider.get();
            String clazzName = rawStNode.getPropertyAsString(session,
                                                             NodeFactory.CORRECT_CLASS);
            if (clazzName == null) {
                System.err.print("");
            }
            Class<?> clazz = forName(clazzName);
            Node node = NodeFactory.createNode(factory, session, contextId,
                                               parentId, (Class<? extends Node>)clazz, rawStNode
                                                                                                .getNodeEntryName(), null, null);
            return node;
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    public Context getContext( Node node ) {
        return node != null ? getContext(node.getContextId()) : null;
    }

    @Override
    public <L extends Link> L getLink( Class<L> linkClass,
                                       Node source,
                                       Node target,
                                       LinkDirection linkDirection ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterable<Node> getLinkedNodes( Class<? extends Link> linkClass ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterable<Node> getLinkedNodes( Class<? extends Link> linkClass,
                                          Node node,
                                          LinkDirection linkDirection ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <N extends Node> Iterable<N> getLinkedNodes( Node node,
                                                        Class<N> nodeClass,
                                                        boolean returnSubTypes,
                                                        LinkDirection linkDirection ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterable<Node> getLinkedNodes( Node node,
                                          LinkDirection linkDirection ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterable<Link> getLinks( Node source,
                                    Node target,
                                    LinkDirection linkDirection ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public SLMetaLink getMetaLink( Link link ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public SLMetaNodeType getMetaType( Node node ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public SLMetadata getMetadata() {
        throw new UnsupportedOperationException();

    }

    @Override
    public Node getNode( String id ) throws NodeNotFoundException {
        STStorageSession session = sessionProvider.get();
        String contextId = StringIDSupport.getPartitionName(id);
        STPartition partition = this.factory.getPartitionByName(contextId);
        STNodeEntry parentStNode = session.withPartition(partition)
                                          .createCriteria().withUniqueKeyAsString(id).buildCriteria()
                                          .andFindUnique(session);
        if (parentStNode == null)
            return null;
        return convertToSLNode(parentStNode.getUniqueKey()
                                           .getParentKeyAsString(), contextId, parentStNode);

    }

    @Override
    public Node getParentNode( Node node ) {
        STStorageSession session = sessionProvider.get();
        STPartition partition = this.factory.getPartitionByName(node
                                                                    .getContextId());
        STNodeEntry parentStNode = session.withPartition(partition)
                                          .createCriteria().withUniqueKeyAsString(node.getId())
                                          .buildCriteria().andFindUnique(session);

        if (parentStNode == null)
            return null;
        return convertToSLNode(parentStNode.getUniqueKey()
                                           .getParentKeyAsString(), node.getContextId(), parentStNode);

    }

    @Override
    public <L extends Link> Iterable<L> getUnidirectionalLinksBySource(
                                                                        Class<L> linkClass,
                                                                        Node source ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterable<Link> getUnidirectionalLinksBySource( Node source ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <L extends Link> Iterable<L> getUnidirectionalLinksByTarget(
                                                                        Class<L> linkClass,
                                                                        Node target ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterable<Link> getUnidirectionalLinksByTarget( Node target ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <T extends Node> Iterable<T> findNodes( Class<T> clazz,
                                                   String name,
                                                   final Context context,
                                                   Context... aditionalContexts ) {
        STStorageSession session = sessionProvider.get();
        Iterable<STNodeEntry> nodes = session.withPartition(
                                                            factory.getPartitionByName(context.getID())).createCriteria()
                                             .withNodeEntry(clazz.getName())
                                             .withProperty(NodeFactory.NAME).equalsTo(name)
                                             .buildCriteria().andFind(session);
        Iterable<T> result = IteratorBuilder
                                            .<T, STNodeEntry>createIteratorBuilder().withConverter(
                                                                                                   new Converter<T, STNodeEntry>() {

                                                                                                       @Override
                                                                                                       public T convert( STNodeEntry o )
                                                                                                           throws Exception {
                                                                                                           return (T)convertToSLNode(o.getUniqueKey()
                                                                                                                                      .getParentKeyAsString(), context
                                                                                                                                                                      .getID(), o);
                                                                                                       }
                                                                                                   }).withItems(nodes).andBuild();

        return result;

    }

    @Override
    public Iterable<Node> findNodes( String name,
                                     final Context context,
                                     Context... aditionalContexts ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Node> Iterable<T> findNodes( Class<T> clazz,
                                                   final Context context,
                                                   Context... aditionalContexts ) {
        STStorageSession session = sessionProvider.get();
        Iterable<STNodeEntry> nodes = session.withPartition(
                                                            factory.getPartitionByName(context.getID())).findNamed(
                                                                                                                   clazz.getName());
        Iterable<T> result = IteratorBuilder
                                            .<T, STNodeEntry>createIteratorBuilder().withConverter(
                                                                                                   new Converter<T, STNodeEntry>() {

                                                                                                       @Override
                                                                                                       public T convert( STNodeEntry o )
                                                                                                           throws Exception {
                                                                                                           return (T)convertToSLNode(o.getUniqueKey()
                                                                                                                                      .getParentKeyAsString(), context
                                                                                                                                                                      .getID(), o);
                                                                                                       }
                                                                                                   }).withItems(nodes).andBuild();

        return result;

    }

    @Override
    public <T extends Node> T findUniqueNode( Class<T> clazz,
                                              String name,
                                              Context context,
                                              Context... aditionalContexts ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Node findUniqueNode( String name,
                                Context context,
                                Context... aditionalContexts ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <T extends Node> T findUniqueNode( Class<T> clazz,
                                              Context context,
                                              Context... aditionalContexts ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public <N extends Node> Iterable<N> getLinkedNodes(
                                                        Class<? extends Link> linkClass,
                                                        Node node,
                                                        Class<N> nodeClass,
                                                        boolean returnSubTypes,
                                                        LinkDirection linkDirection ) {
        throw new UnsupportedOperationException();

    }

}
