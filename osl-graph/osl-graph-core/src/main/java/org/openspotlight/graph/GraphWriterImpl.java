/**
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

import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;

import org.openspotlight.graph.internal.NodeAndLinkSupport;
import org.openspotlight.graph.internal.NodeAndLinkSupport.PropertyContainerMetadata;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.manipulation.GraphWriter;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.StorageSession;

import com.google.inject.Provider;

public class GraphWriterImpl implements GraphWriter {

    private final GraphReader              graphReader;
    private final Provider<StorageSession> sessionProvider;
    private final PartitionFactory         factory;
    private final String                   artifactId;
    private final List<Node>               dirtyNodes = newLinkedList();
    private final List<Link>               dirtyLinks = newLinkedList();

    public GraphWriterImpl(final PartitionFactory factory,
                           final Provider<StorageSession> sessionProvider, final String artifactId,
                           final GraphReader graphReader) {
        this.artifactId = artifactId;
        this.factory = factory;
        this.sessionProvider = sessionProvider;
        this.graphReader = graphReader;
    }

    @Override
    public void removeContext(
                              final Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeLink(
                           final Link link) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNode(
                           final Node node) {
        final StorageSession session = sessionProvider.get();
        final org.openspotlight.storage.domain.StorageNode StorageNode = NodeAndLinkSupport.retrievePreviousNode(factory,
            session, graphReader.getContext(node.getContextId()), node,
            true);
        session.removeNode(StorageNode);
    }

    @Override
    public void setContextCaption(
                                  final Context context, final String caption) {
        final ContextImpl contextImpl = (ContextImpl) context;
        contextImpl.setCaption(caption);
        final StorageSession session = sessionProvider.get();
    }

    @Override
    public void copyNodeHierarchy(
                                  final Node node, final Context target) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void moveNodeHierarchy(
                                  final Node node, final Context target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <L extends Link> L addBidirectionalLink(
                                                   final Class<L> linkClass,
                                                   final Node source, final Node target)
        throws IllegalArgumentException {
        final L newLink =
            NodeAndLinkSupport.createLink(factory, sessionProvider.get(), linkClass, source, target, LinkDirection.BIDIRECTIONAL,
                false);
        dirtyLinks.add(newLink);
        return newLink;
    }

    @Override
    public <T extends Node> T addChildNode(
                                           final Node parent, final Class<T> clazz,
                                           final String name)
        throws IllegalArgumentException {
        return addChildNode(parent, clazz, name, null, null);
    }

    @Override
    public <L extends Link> L addLink(
                                      final Class<L> linkClass, final Node source,
                                      final Node target)
        throws IllegalArgumentException {

        final L newLink =
            NodeAndLinkSupport.createLink(factory, sessionProvider.get(), linkClass, source, target,
                LinkDirection.UNIDIRECTIONAL,
                false);
        dirtyLinks.add(newLink);
        return newLink;

    }

    @SuppressWarnings("unchecked")
    @Override
    public void flush() {
        final StorageSession session = sessionProvider.get();
        for (final Node n: dirtyNodes) {
            NodeAndLinkSupport.retrievePreviousNode(factory, session, graphReader
                .getContext(n.getContextId()), n, true);
            NodeAndLinkSupport.writeTreeLineReference(session, factory, n);
        }
        session.flushTransient();
        for (final Link l: dirtyLinks) {
            final Link retrievedLink =
                NodeAndLinkSupport.createLink(factory, sessionProvider.get(), l.getLinkType(), l.getSource(), l.getTarget(),
                    l.getLinkDirection(),
                    true);
            final PropertyContainerMetadata<org.openspotlight.storage.domain.StorageLink> md =
                (PropertyContainerMetadata<org.openspotlight.storage.domain.StorageLink>) retrievedLink;
            final org.openspotlight.storage.domain.StorageLink cached = md.getCached();
            cached.setIndexedProperty(session, NodeAndLinkSupport.LINK_DIRECTION, retrievedLink.getLinkDirection().name());
            NodeAndLinkSupport.writeTreeLineReference(session, factory, l);
            session.flushTransient();
        }

    }

    @Override
    public <T extends Node> T addNode(
                                      final Context context, final Class<T> clazz,
                                      final String name)
        throws IllegalArgumentException {
        return addNode(context, clazz, name, null, null);
    }

    @Override
    public <T extends Node> T addNode(final Context context, final Class<T> clazz,
                                      final String name,
                                      final Iterable<Class<? extends Link>> linkTypesForLinkDeletion,
                                      final Iterable<Class<? extends Link>> linkTypesForLinkedNodeDeletion)
            throws IllegalArgumentException {
        final StorageSession session = sessionProvider.get();
        final T newNode = NodeAndLinkSupport.createNode(factory, session, context.getId(),
            null, clazz, name, true, linkTypesForLinkDeletion,
            linkTypesForLinkedNodeDeletion);
        dirtyNodes.add(newNode);
        return newNode;
    }

    @Override
    public <T extends Node> T addChildNode(final Node parent, final Class<T> clazz,
                                           final String name,
                                           final Iterable<Class<? extends Link>> linkTypesForLinkDeletion,
                                           final Iterable<Class<? extends Link>> linkTypesForLinkedNodeDeletion)
            throws IllegalArgumentException {
        final StorageSession session = sessionProvider.get();
        final T newNode = NodeAndLinkSupport.createNode(factory, session, parent
            .getContextId(), parent.getId(), clazz, name, true,
            linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
        dirtyNodes.add(newNode);
        return newNode;
    }

}
