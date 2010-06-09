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

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.concurrent.LockedCollections;
import org.openspotlight.common.concurrent.NeedsSyncronizationCollection;
import org.openspotlight.common.concurrent.NeedsSyncronizationSet;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.annotation.SLLinkAttribute;
import org.openspotlight.graph.event.*;
import org.openspotlight.graph.exception.SLGraphException;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.exception.SLNodeNotFoundException;
import org.openspotlight.graph.listeners.*;
import org.openspotlight.graph.persistence.*;
import org.openspotlight.graph.query.*;
import org.openspotlight.graph.query.parser.SLQueryTextInternalBuilder;
import org.openspotlight.graph.util.ProxyUtil;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.SLInvalidCredentialException;
import org.openspotlight.security.authz.*;
import org.openspotlight.security.authz.graph.GraphElement;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Class SLGraphSessionImpl.
 *
 * @author Vitor Hugo Chagas
 */
public class SLGraphSessionImpl implements SLGraphSession {

    public SimplePersistCapable<STNodeEntry, STStorageSession> getLineReferenceSimplePersist() {
        return lineReferenceSimplePersist;
    }

    private final SimplePersistCapable<STNodeEntry, STStorageSession> lineReferenceSimplePersist;


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Lock lock;

    /**
     * The tree session.
     */
    private final SLPersistentTreeSession treeSession;

    /**
     * The event poster.
     */
    private final SLGraphSessionEventPoster eventPoster;

    /**
     * The encoder.
     */
    private SLEncoder encoder;

    /**
     * The encoder factory.
     */
    private final SLEncoderFactory encoderFactory;

    /**
     * The slql query builder.
     */
    private final SLQueryTextInternalBuilder queryBuilder;

    /**
     * The slql query cache.
     */
    private final SLQueryCache queryCache;

    /**
     * The user.
     */
    private final AuthenticatedUser user;

    /**
     * The policy enforcement.
     */
    private final PolicyEnforcement policyEnforcement;

    /**
     * Instantiates a new sL graph session impl.
     *
     * @param treeSession                the tree session
     * @param policyEnforcement          the policy enforcement
     * @param user                       the user
     * @param simplePersistFactory
     */
    public SLGraphSessionImpl(
            final SLPersistentTreeSession treeSession, final PolicyEnforcement policyEnforcement,
            final AuthenticatedUser user, SimplePersistFactory simplePersistFactory) {
        this.lineReferenceSimplePersist = simplePersistFactory.createSimplePersist(SLPartition.LINE_REFERENCE);

        Assertions.checkNotNull("treeSession", treeSession);
        Assertions.checkNotNull("policyEnforcement", policyEnforcement);
        Assertions.checkNotNull("user", user);

        this.treeSession = treeSession;
        lock = treeSession.getLockObject();
        this.user = user;
        this.policyEnforcement = policyEnforcement;
        final Collection<SLGraphSessionEventListener> listeners = new CopyOnWriteArrayList<SLGraphSessionEventListener>();
        listeners.add(new SLObjectMarkListener(this));
        listeners.add(new SLTransientObjectListener(this));
        listeners.add(new SLLinkCountListener(this));
        listeners.add(new SLCollatorListener(this));
        listeners.add(new SLMetadataListener(this));
        eventPoster = new SLGraphSessionEventPosterImpl(listeners, this);
        encoderFactory = new SLEncoderFactoryImpl();
        encoder = encoderFactory.getUUIDEncoder();
        queryBuilder = new SLQueryTextInternalBuilder();
        queryCache = new SLQueryCacheImpl(this.treeSession, this);
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> L addLink(final Class<L> linkClass,
                                        final SLNode source,
                                        final SLNode target,
                                        final boolean bidirecional) {
        synchronized (lock) {
            return this.addLink(linkClass, source, target, bidirecional, SLPersistenceMode.NORMAL);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> L addLink(final Class<L> linkClass,
                                        final SLNode source,
                                        final SLNode target,
                                        final boolean bidirecional,
                                        final SLPersistenceMode persistenceMode) {
        synchronized (lock) {
            Assertions.checkNotNull("source", source);
            Assertions.checkNotNull("target", target);

            try {

                if (!hasPrivileges(GraphElement.LINK, Action.WRITE)) {
                    throw new SLInvalidCredentialException("User does not have privilegies to add links.");
                }

                SLPersistentNode linkNode = null;

                boolean changedToBidirectional = false;
                final boolean allowsMultiple = allowsMultiple(linkClass);
                final boolean allowsChangeToBidirecional = allowsChangeToBidirecional(linkClass);

                if (allowsMultiple && allowsChangeToBidirecional) {
                    throw new SLGraphSessionException(
                            "ALLOWS_CHANGE_TO_BIDIRECTIONAL and ALLOWS_MULTIPLE attributes are not supported at once.");
                }

                final SLPersistentNode pairKeyNode = getPairKeyNode(linkClass, source, target);
                final int direction = getDirection(source, target, bidirecional);

                boolean newLink = false;

                if (allowsMultiple) {
                    newLink = true;
                } else {

                    linkNode = getLinkNodeByDirection(pairKeyNode, direction);

                    if (linkNode == null) {
                        if (allowsChangeToBidirecional) {
                            linkNode = findUniqueLinkNode(pairKeyNode);
                            if (linkNode == null) {
                                newLink = true;
                            } else {
                                final SLPersistentProperty<Integer> directionProp = linkNode.getProperty(
                                        Integer.class,
                                        SLConsts.PROPERTY_NAME_DIRECTION);
                                if (directionProp.getValue() != SLConsts.DIRECTION_BOTH) {
                                    directionProp.setValue(SLConsts.DIRECTION_BOTH);
                                    changedToBidirectional = true;
                                }
                            }
                        } else {
                            newLink = true;
                        }
                    }
                }

                if (newLink) {
                    linkNode = addLinkNode(pairKeyNode, linkClass, source, target, direction);
                }

                final SLLink link;
                if (bidirecional) {
                    link = new SLLinkImpl(this, linkNode, eventPoster, new SLNode[]{source, target});
                } else {
                    link = new SLLinkImpl(this, linkNode, eventPoster, source, target);
                }
                final L linkProxy = ProxyUtil.createLinkProxy(linkClass, link);
                final SLLinkEvent event = new SLLinkAddedEvent(linkProxy, linkNode, persistenceMode);
                event.setNewLink(newLink);
                event.setChangedToBidirectional(changedToBidirectional);
                eventPoster.post(event);
                return linkProxy;
            } catch (final SLException e) {
                throw new SLGraphSessionException("Error on attempt to add link.", e);
            }
        }
    }

    /**
     * Adds the link node.
     *
     * @param pairKeyNode the pair key node
     * @param linkType    the link type
     * @param source      the source
     * @param target      the target
     * @param direction   the direction
     * @return the sL persistent node
     * @throws SLPersistentTreeSessionException
     *          the SL persistent tree session exception
     */
    @SuppressWarnings("unchecked")
    private SLPersistentNode addLinkNode(final SLPersistentNode pairKeyNode,
                                         final Class<? extends SLLink> linkType,
                                         final SLNode source,
                                         final SLNode target,
                                         final int direction) throws SLPersistentTreeSessionException {
        final long linkCount = incLinkCount(pairKeyNode);
        final String name = SLCommonSupport.getLinkIndexNodeName(linkCount);
        final SLPersistentNode linkNode = pairKeyNode.addNode(name);
        final Class<? extends SLNode> sourceType = (Class<? extends SLNode>) source.getClass().getInterfaces()[0];
        final Class<? extends SLNode> targetType = (Class<? extends SLNode>) target.getClass().getInterfaces()[0];
        SLCommonSupport.setInternalStringProperty(linkNode, SLConsts.PROPERTY_NAME_SOURCE_ID, source.getID());
        SLCommonSupport.setInternalStringProperty(linkNode, SLConsts.PROPERTY_NAME_TARGET_ID, target.getID());
        SLCommonSupport.setInternalIntegerProperty(linkNode, SLConsts.PROPERTY_NAME_LINK_TYPE_HASH, linkType.getName().hashCode());
        SLCommonSupport.setInternalIntegerProperty(linkNode, SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH,
                sourceType.getName().hashCode());
        SLCommonSupport.setInternalIntegerProperty(linkNode, SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH,
                targetType.getName().hashCode());
        linkNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, linkCount);
        linkNode.setProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION, direction);
        return linkNode;
    }

    /**
     * Allows change to bidirecional.
     *
     * @param linkTypeClass the link type class
     * @return true, if successful
     */
    private boolean allowsChangeToBidirecional(final Class<? extends SLLink> linkTypeClass) {
        final SLLinkAttribute attribute = linkTypeClass.getAnnotation(SLLinkAttribute.class);
        return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_CHANGE_TO_BIDIRECTIONAL) > -1;
    }

    /**
     * Allows multiple.
     *
     * @param linkTypeClass the link type class
     * @return true, if successful
     */
    private boolean allowsMultiple(final Class<? extends SLLink> linkTypeClass) {
        final SLLinkAttribute attribute = linkTypeClass.getAnnotation(SLLinkAttribute.class);
        return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_MULTIPLE) > -1;
    }

    /**
     * {@inheritDoc}
     */
    public void cleanCache() {
        try {
            queryCache.flush();
        } catch (final SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to flush cache.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        synchronized (lock) {
            try {
                eventPoster.sessionCleaned();
                treeSession.clear();
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to clear OSL repository.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        try {
            this.save();
            treeSession.close();
        } catch (Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLContext createContext(final String id) {
        synchronized (lock) {
            try {
                if (!hasPrivileges(GraphElement.CONTEXT, Action.WRITE)) {
                    throw new SLInvalidCredentialException("User does not have privilegies to newPair contexts.");
                }

                final SLPersistentNode contextsPersistentNode = SLCommonSupport.getContextsPersistentNode(treeSession);

                SLPersistentNode contextRootPersistentNode = contextsPersistentNode.getNode(id);
                if (contextRootPersistentNode == null) {
                    contextRootPersistentNode = contextsPersistentNode.addNode(id);
                    final String propName = SLCommonSupport.toUserPropertyName(SLConsts.PROPERTY_CAPTION_NAME);
                    contextRootPersistentNode.setProperty(String.class, propName, id);

                }
                return new SLContextImpl(this, contextRootPersistentNode, eventPoster);
            } catch (final SLPersistentTreeSessionException e) {
                Exceptions.catchAndLog(e);
                throw new SLGraphSessionException("Error on attempt to newPair context node.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryApi createQueryApi() {
        synchronized (lock) {
            return new SLQueryApiImpl(this, treeSession, queryCache);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryText createQueryText(final String slqlInput) throws SLInvalidQuerySyntaxException {
        final SLQueryTextInternal query = queryBuilder.build(slqlInput);
        synchronized (lock) {
            return new SLQueryTextImpl(this, treeSession, query);
        }
    }

    /**
     * Filter nodes from links.
     *
     * @param links          the links
     * @param node           the node
     * @param nodeClass      the node class
     * @param returnSubTypes the return sub types
     * @return the set< n>
     */
    private <N extends SLNode> NeedsSyncronizationSet<N> filterNodesFromLinks(final Collection<? extends SLLink> links,
                                                                              final SLNode node,
                                                                              final Class<N> nodeClass,
                                                                              final boolean returnSubTypes) {
        final Set<N> nodes = new HashSet<N>();
        for (final SLLink link : links) {
            if (node == null) {
                final SLNode side1 = link.getSides()[0];
                final SLNode side2 = link.getSides()[1];
                if (nodeOfType(side1, nodeClass, returnSubTypes)) {
                    nodes.add(nodeClass.cast(side1));
                }
                if (nodeOfType(side2, nodeClass, returnSubTypes)) {
                    nodes.add(nodeClass.cast(side2));
                }
            } else {
                final SLNode otherSide = link.getOtherSide(node);
                if (nodeOfType(otherSide, nodeClass, returnSubTypes)) {
                    nodes.add(nodeClass.cast(otherSide));
                }
            }
        }

        return LockedCollections.createSetWithLock(this, nodes);
    }

    /**
     * Find unique link node.
     *
     * @param pairKeyNode the pair key node
     * @return the sL persistent node
     * @throws SLPersistentTreeSessionException
     *          the SL persistent tree session exception
     */
    private SLPersistentNode findUniqueLinkNode(final SLPersistentNode pairKeyNode) throws SLPersistentTreeSessionException {
        return pairKeyNode.getNodes().isEmpty() ? null : pairKeyNode.getNodes().iterator().next();
    }

    /**
     * Gets the a node.
     *
     * @param source the source
     * @param target the target
     * @return the a node
     */
    private SLNode getANode(final SLNode source,
                            final SLNode target) {
        return source.getID().compareTo(target.getID()) < 0 ? source : target;
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getBidirectionalLinks(final Class<L> linkClass,
                                                                                     final SLNode side1,
                                                                                     final SLNode side2) {
        synchronized (lock) {
            return this.getLinks(linkClass, side1, side2, SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getBidirectionalLinks(final SLNode side1,
                                                                       final SLNode side2) {
        synchronized (lock) {
            return this.getLinks(side1, side2, SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getBidirectionalLinksBySide(final Class<L> linkClass,
                                                                                           final SLNode side) {
        synchronized (lock) {
            return this.getLinks(linkClass, side, null, SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getBidirectionalLinksBySide(final SLNode side) {
        synchronized (lock) {
            return this.getLinks(side, null, SLLink.DIRECTION_BI);
        }
    }

    /**
     * Gets the b node.
     *
     * @param source the source
     * @param target the target
     * @return the b node
     */
    private SLNode getBNode(final SLNode source,
                            final SLNode target) {
        return source.getID().compareTo(target.getID()) < 0 ? target : source;
    }

    /**
     * {@inheritDoc}
     */
    public SLContext getContext(final String id) {
        synchronized (lock) {
            return createContext(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLEncoder getDefaultEncoder() {
        return encoder;
    }

    /**
     * Gets the direction.
     *
     * @param source       the source
     * @param target       the target
     * @param bidirecional the bidirecional
     * @return the direction
     */
    private int getDirection(final SLNode source,
                             final SLNode target,
                             final boolean bidirecional) {
        if (bidirecional) {
            return SLConsts.DIRECTION_BOTH;
        } else {
            return getANode(source, target).equals(source) ? SLConsts.DIRECTION_AB : SLConsts.DIRECTION_BA;
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLEncoderFactory getEncoderFactory() {
        return encoderFactory;
    }

    /**
     * Gets the link classes.
     *
     * @return the link classes
     */
    @SuppressWarnings("unchecked")
    private Collection<Class<? extends SLLink>> getLinkClasses() {
        try {
            final Collection<Class<? extends SLLink>> linkClasses = new ArrayList<Class<? extends SLLink>>();
            final SLPersistentQuery query = treeSession.createQuery(treeSession.getXPathRootPath() + "/links/*",
                    SLPersistentQuery.TYPE_XPATH);
            final SLPersistentQueryResult result = query.execute();
            final Collection<SLPersistentNode> linkClassNodes = result.getNodes();
            for (final SLPersistentNode linkClassNode : linkClassNodes) {
                final Class<? extends SLLink> linkClass = (Class<? extends SLLink>) Class.forName(linkClassNode.getName());
                linkClasses.add(linkClass);
            }
            return linkClasses;
        } catch (final Exception e) {
            throw new SLGraphSessionException("Error on attempt to retrieve link classes.", e);
        }
    }

    /**
     * Gets the link node by direction.
     *
     * @param pairKeyNode the pair key node
     * @param direction   the direction
     * @return the link node by direction
     * @throws SLPersistentTreeSessionException
     *          the SL persistent tree session exception
     */
    private SLPersistentNode getLinkNodeByDirection(final SLPersistentNode pairKeyNode,
                                                    final int direction) throws SLPersistentTreeSessionException {
        SLPersistentNode linkNode = null;
        for (final SLPersistentNode node : pairKeyNode.getNodes()) {
            final SLPersistentProperty<Long> directionProp = node.getProperty(Long.class, SLConsts.PROPERTY_NAME_DIRECTION);
            if (directionProp.getValue() == direction) {
                linkNode = node;
            }
        }
        return linkNode;
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getLinks(final Class<L> linkClass,
                                                                        final SLNode source,
                                                                        final SLNode target) {
        synchronized (lock) {
            return this.getLinks(linkClass, source, target, SLLink.DIRECTION_ANY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getLinks(final Class<L> linkClass,
                                                                        final SLNode source,
                                                                        final SLNode target,
                                                                        final int direction) {
        synchronized (lock) {

            try {
                final NeedsSyncronizationSet<L> links = LockedCollections.createSetWithLock(this, new TreeSet<L>());

                // query format:
                // //osl/links/JavaClassToJavaMethod/*[(@a=sourceID or
                // @b=sourceID)
                // and (@a=targetID or @b=targetID)]/*[@direction=AB or
                // @direcionBA]
                // order by @linkCount ascending

                final StringBuilder statement = new StringBuilder();
                statement.append(treeSession.getXPathRootPath()).append("/links/").append(linkClass.getName()).append("/*");

                if (source != null || target != null) {

                    statement.append('[');

                    // filter source ...
                    if (source != null) {
                        statement.append('(').append('@').append(SLConsts.PROPERTY_NAME_A_NODE_ID).append("='").append(
                                source.getID()).append(
                                "'").append(
                                " or ").append(
                                '@').append(
                                SLConsts.PROPERTY_NAME_B_NODE_ID).append(
                                "='").append(
                                source.getID()).append(
                                "'").append(
                                ')');
                    }

                    if (source != null && target != null) {
                        statement.append(" and ");
                    }

                    // filter target ...
                    if (target != null) {
                        statement.append('(').append('@').append(SLConsts.PROPERTY_NAME_A_NODE_ID).append("='").append(
                                target.getID()).append(
                                "'").append(
                                " or ").append(
                                '@').append(
                                SLConsts.PROPERTY_NAME_B_NODE_ID).append(
                                "='").append(
                                target.getID()).append(
                                "'").append(
                                ')');
                    }

                    statement.append(']');
                }

                statement.append("/*");

                final StringBuilder directionFilter = new StringBuilder();
                if (direction == (direction | SLLink.DIRECTION_UNI) || direction == (direction | SLLink.DIRECTION_UNI_REVERSAL)) {
                    directionFilter.append('@').append(SLConsts.PROPERTY_NAME_DIRECTION).append('=').append(SLConsts.DIRECTION_AB).append(
                            " or @").append(
                            SLConsts.PROPERTY_NAME_DIRECTION).append(
                            '=').append(
                            SLConsts.DIRECTION_BA);
                }
                if (direction == (direction | SLLink.DIRECTION_BI)) {
                    if (directionFilter.length() > 0) {
                        directionFilter.append(" or ");
                    }
                    directionFilter.append('@').append(SLConsts.PROPERTY_NAME_DIRECTION).append('=').append(
                            SLConsts.DIRECTION_BOTH);
                }

                // add direction filter ...
                statement.append('[').append(directionFilter).append(']');

                // order by link count (the order the links are added by the
                // user)
                // ...
                statement.append(" order by @").append(SLConsts.PROPERTY_NAME_LINK_COUNT).append(" ascending");

                // execute query ...
                final SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
                final SLPersistentQueryResult result = query.execute();
                final Collection<SLPersistentNode> linkNodes = result.getNodes();

                for (final SLPersistentNode linkNode : linkNodes) {

                    final SLPersistentNode pairKeyNode = linkNode.getParent();

                    final SLPersistentProperty<String> aNodeIDProp = pairKeyNode.getProperty(String.class,
                            SLConsts.PROPERTY_NAME_A_NODE_ID);
                    final SLPersistentProperty<String> bNodeIDProp = pairKeyNode.getProperty(String.class,
                            SLConsts.PROPERTY_NAME_B_NODE_ID);
                    final SLPersistentProperty<Integer> directionProp = linkNode.getProperty(Integer.class,
                            SLConsts.PROPERTY_NAME_DIRECTION);

                    SLNode aNode = null;
                    SLNode bNode = null;
                    try {
                        aNode = getNodeByID(aNodeIDProp.getValue());
                        bNode = getNodeByID(bNodeIDProp.getValue());
                    } catch (SLNodeNotFoundException e) {
                        throw new SLGraphSessionException("Error on attempt to retrieve links.", e);
                    }

                    boolean status;

                    if (source == null && target == null) {
                        if (directionProp.getValue() == SLConsts.DIRECTION_AB
                                || directionProp.getValue() == SLConsts.DIRECTION_BA) {
                            status = direction == (direction | SLLink.DIRECTION_UNI);
                        } else {
                            status = direction == (direction | SLLink.DIRECTION_BI);
                        }
                    } else {

                        if (directionProp.getValue() == SLConsts.DIRECTION_BOTH) {
                            status = direction == (direction | SLLink.DIRECTION_BI);
                        } else {

                            SLNode s = null;
                            SLNode t = null;

                            if (directionProp.getValue() == SLConsts.DIRECTION_AB) {
                                s = aNode;
                                t = bNode;
                            } else if (directionProp.getValue() == SLConsts.DIRECTION_BA) {
                                s = bNode;
                                t = aNode;
                            }

                            status = direction == (direction | SLLink.DIRECTION_UNI) && (s.equals(source) || t.equals(target))
                                    || direction == (direction | SLLink.DIRECTION_UNI_REVERSAL)
                                    && (s.equals(target) || t.equals(source));
                        }
                    }

                    if (status) {
                        final SLLink link = new SLLinkImpl(this, linkNode, eventPoster);
                        links.add(ProxyUtil.createLinkProxy(linkClass, link));
                    }
                }

                return links;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve links.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getLinks(final SLNode source,
                                                          final SLNode target) {
        synchronized (lock) {
            return this.getLinks(source, target, SLLink.DIRECTION_ANY);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getLinks(final SLNode source,
                                                          final SLNode target,
                                                          final int directionType) {
        synchronized (lock) {
            final NeedsSyncronizationCollection<SLLink> links = LockedCollections.createCollectionWithLock(
                    this,
                    new ArrayList<SLLink>());
            final Collection<Class<? extends SLLink>> linkClasses = getLinkClasses();
            for (final Class<? extends SLLink> linkClass : linkClasses) {
                links.addAll(this.getLinks(linkClass, source, target, directionType));
            }
            return links;
        }
    }

    public Lock getLockObject() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    public SLMetadata getMetadata() {
        synchronized (lock) {
            return new SLMetadataImpl(treeSession);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLNode getNodeByID(final String id) throws SLNodeNotFoundException {
        synchronized (lock) {
            final int INDEX_CONTEXT_ID = 3;
            try {
                SLNode node = null;
                final SLPersistentNode pNode = treeSession.getNodeByID(id);
                final String[] names = pNode.getPath().substring(1).split("/");
                final String contextID = names[INDEX_CONTEXT_ID];
                final SLContext context = getContext(contextID);
                final SLEncoder fakeEncoder = getEncoderFactory().getFakeEncoder();
                for (int i = INDEX_CONTEXT_ID + 1; i < names.length; i++) {
                    if (node == null) {
                        final SLNode rootNode = context.getRootNode();
                        node = context.getRootNode().getNode(SLNode.class, names[i], fakeEncoder);
                        final Class<? extends SLNode> nodeType = this.getNodeType(rootNode);
                        node = ProxyUtil.createNodeProxy(nodeType, node);
                    } else {
                        node = node.getNode(SLNode.class, names[i], fakeEncoder);
                        final Class<? extends SLNode> nodeType = this.getNodeType(node);
                        node = ProxyUtil.createNodeProxy(nodeType, node);
                    }
                }
                return node;
            } catch (final SLPersistentNodeNotFoundException e) {
                throw new SLNodeNotFoundException(id, e);
            } catch (final SLException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve node by id.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(final Class<? extends SLLink> linkClass) {
        synchronized (lock) {
            return this.getNodesByLink(linkClass, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(final Class<? extends SLLink> linkClass,
                                                                final SLNode node) {
        synchronized (lock) {
            return this.getNodesByLink(linkClass, node, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(final Class<? extends SLLink> linkClass,
                                                                              final SLNode node,
                                                                              final Class<N> nodeClass,
                                                                              final boolean returnSubTypes) {
        synchronized (lock) {
            return this.getNodesByLink(linkClass, node, nodeClass, returnSubTypes, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(final Class<? extends SLLink> linkClass,
                                                                              final SLNode node,
                                                                              final Class<N> nodeClass,
                                                                              final boolean returnSubTypes,
                                                                              final int direction) {
        synchronized (lock) {
            try {
                final Collection<? extends SLLink> links = this.getLinks(linkClass, node, null, direction);
                return this.filterNodesFromLinks(links, node, nodeClass, returnSubTypes);
            } catch (final SLGraphSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve nodes by link.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(final Class<? extends SLLink> linkClass,
                                                                final SLNode node,
                                                                final int direction) {
        synchronized (lock) {
            return this.getNodesByLink(linkClass, node, SLNode.class, true, direction);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(final SLNode node) {
        synchronized (lock) {
            return this.getNodesByLink(node, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(final SLNode node,
                                                                              final Class<N> nodeClass,
                                                                              final boolean returnSubTypes) {
        synchronized (lock) {
            return this.getNodesByLink(node, nodeClass, returnSubTypes, SLLink.DIRECTION_UNI | SLLink.DIRECTION_BI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(final SLNode node,
                                                                              final Class<N> nodeClass,
                                                                              final boolean returnSubTypes,
                                                                              final int direction) {
        synchronized (lock) {
            try {
                final Collection<? extends SLLink> links = this.getLinks(node, null, direction);
                return this.filterNodesFromLinks(links, node, nodeClass, returnSubTypes);
            } catch (final SLGraphSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve nodes by link.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(final SLNode node,
                                                                final int direction) {
        synchronized (lock) {
            return this.getNodesByLink(node, SLNode.class, true, direction);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByPredicate(final SLNodePredicate predicate) {
        synchronized (lock) {
            try {
                final NeedsSyncronizationCollection<SLNode> nodes = LockedCollections.createCollectionWithLock(
                        this,
                        new ArrayList<SLNode>());
                final SLPersistentQuery query = treeSession.createQuery(treeSession.getXPathRootPath()
                        + "/contexts/*//descendant::node()",
                        SLPersistentQuery.TYPE_XPATH);
                final SLPersistentQueryResult result = query.execute();
                final Collection<SLPersistentNode> pNodes = result.getNodes();
                for (final SLPersistentNode pNode : pNodes) {
                    final SLNode node = getNodeByID(pNode.getID());
                    if (node != null && predicate.evaluate(node)) {
                        nodes.add(node);
                    }
                }
                return nodes;
            } catch (final SLException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve nodes by predicate.", e);
            }
        }
    }

    /**
     * Gets the node type.
     *
     * @param node the node
     * @return the node type
     * @throws SLPersistentTreeSessionException
     *          the SL persistent tree session exception
     */
    private Class<? extends SLNode> getNodeType(final SLNode node) throws SLPersistentTreeSessionException {
        final SLPersistentNode pNode = SLCommonSupport.getPNode(node);
        final String typeName = SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_TYPE);
        return this.getNodeType(typeName);
    }

    /**
     * Gets the node type.
     *
     * @param typeName the type name
     * @return the node type
     */
    private Class<? extends SLNode> getNodeType(final String typeName) {
        Class<? extends SLNode> nodeType = null;
        if (typeName != null) {
            try {
                if (Class.forName(typeName).isAssignableFrom(SLNode.class)) {
                    nodeType = (Class<? extends SLNode>) Class.forName(typeName);
                }
            } catch (final ClassNotFoundException ignored) {
            }
        }

        if (nodeType != null) {
            return nodeType;
        }

        return SLNode.class;
    }

    /**
     * Gets the pair key node.
     *
     * @param linkClass the link class
     * @param source    the source
     * @param target    the target
     * @return the pair key node
     * @throws SLException the SL exception
     */
    private SLPersistentNode getPairKeyNode(final Class<? extends SLLink> linkClass,
                                            final SLNode source,
                                            final SLNode target) throws SLException {

        final SLNode a = getANode(source, target);
        final SLNode b = getBNode(source, target);
        final String aIDName = SLCommonSupport.getNameInIDForm(a);
        final String bIDName = SLCommonSupport.getNameInIDForm(b);

        final StringBuilder pairKey = new StringBuilder();
        pairKey.append(aIDName).append('.').append(bIDName);
        final SLPersistentNode linkClassNode = SLCommonSupport.getLinkClassNode(treeSession, linkClass);
        SLPersistentNode pairKeyNode = linkClassNode.getNode(pairKey.toString());

        if (pairKeyNode == null) {
            pairKeyNode = linkClassNode.addNode(pairKey.toString());
            pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_A_NODE_ID, a.getID());
            pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_B_NODE_ID, b.getID());
            pairKeyNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, 0L);
        }

        return pairKeyNode;
    }

    /**
     * {@inheritDoc}
     */
    public PolicyEnforcement getPolicyEnforcement() {
        return policyEnforcement;
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getUnidirectionalLinks(final Class<L> linkClass,
                                                                                      final SLNode source,
                                                                                      final SLNode target) {
        synchronized (lock) {
            return this.getLinks(linkClass, source, target, SLLink.DIRECTION_UNI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getUnidirectionalLinks(final SLNode source,
                                                                        final SLNode target) {
        synchronized (lock) {
            return this.getLinks(source, target, SLLink.DIRECTION_UNI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getUnidirectionalLinksBySource(final Class<L> linkClass,
                                                                                              final SLNode source) {
        synchronized (lock) {
            return this.getLinks(linkClass, source, null, SLLink.DIRECTION_UNI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getUnidirectionalLinksBySource(final SLNode source) {
        synchronized (lock) {
            return this.getLinks(source, null, SLLink.DIRECTION_UNI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getUnidirectionalLinksByTarget(final Class<L> linkClass,
                                                                                              final SLNode target) {
        synchronized (lock) {
            return this.getLinks(linkClass, null, target, SLLink.DIRECTION_UNI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLLink> getUnidirectionalLinksByTarget(final SLNode target) {
        synchronized (lock) {
            return this.getLinks(null, target, SLLink.DIRECTION_UNI);
        }
    }

    /**
     * {@inheritDoc}
     */
    public User getUser() {
        return user;
    }

    /**
     * Checks for privileges.
     *
     * @param element the element
     * @param action  the action
     * @return true, if successful
     */
    private boolean hasPrivileges(final GraphElement element,
                                  final Action action) {
        final EnforcementContext enforcementContext = new EnforcementContext();
        enforcementContext.setAttribute("user", user);
        enforcementContext.setAttribute("graphElement", element);
        enforcementContext.setAttribute("action", action);
        enforcementContext.setAttribute("graphSession", this);

        try {
            final EnforcementResponse response = policyEnforcement.checkAccess(enforcementContext);
            return response.equals(EnforcementResponse.GRANTED);
        } catch (final EnforcementException e) {
            Exceptions.catchAndLog(e);
            return false;
        }
    }

    /**
     * Inc link count.
     *
     * @param linkKeyPairNode the link key pair node
     * @return the long
     * @throws SLPersistentTreeSessionException
     *          the SL persistent tree session exception
     */
    private long incLinkCount(final SLPersistentNode linkKeyPairNode) throws SLPersistentTreeSessionException {
        final SLPersistentProperty<Long> linkCountProp = linkKeyPairNode.getProperty(Long.class,
                SLConsts.PROPERTY_NAME_LINK_COUNT);
        final long linkCount = linkCountProp.getValue() + 1;
        linkCountProp.setValue(linkCount);
        return linkCount;
    }

    /**
     * Node of type.
     *
     * @param node           the node
     * @param nodeClass      the node class
     * @param returnSubTypes the return sub types
     * @return true, if successful
     */
    private boolean nodeOfType(final SLNode node,
                               final Class<? extends SLNode> nodeClass,
                               final boolean returnSubTypes) {
        return returnSubTypes && nodeClass.isAssignableFrom(node.getClass().getInterfaces()[0]) || !returnSubTypes
                && nodeClass.equals(node.getClass().getInterfaces()[0]);
    }

    /**
     * {@inheritDoc}
     */
    public void save() {
        synchronized (lock) {
            try {
                if (!hasPrivileges(GraphElement.SESSION, Action.OPERATE)) {
                    throw new SLInvalidCredentialException("User does not have privilegies to save session.");
                }
                eventPoster.post(new SLGraphSessionSaveEvent(this));
                logger.info("Starting to save graph");
                treeSession.save();
                logger.info("Fisnihed to save graph");
            } catch (SLPersistentTreeSessionException e) {
                Exceptions.catchAndLog(e);
                throw new SLGraphSessionException("Error on attempt to save the session.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public NeedsSyncronizationCollection<SLNode> searchNodes(final String text) {
        try {
            final SLQueryApi query = createQueryApi();
            query.select().type(SLNode.class.getName()).subTypes().selectEnd().where().type(SLNode.class.getName()).subTypes().each().property(
                    SLConsts.PROPERTY_CAPTION_NAME).contains().value(
                    text).typeEnd().whereEnd().collator(
                    Collator.PRIMARY);
            return query.execute().getNodes();
        } catch (final SLGraphException e) {
            throw new SLGraphSessionException("Error on attempt to execute search.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultEncoder(final SLEncoder encoder) {
        this.encoder = encoder;
    }
}
