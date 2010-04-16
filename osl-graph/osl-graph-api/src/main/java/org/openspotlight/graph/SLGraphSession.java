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

import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.concurrent.NeedsSyncronizationCollection;
import org.openspotlight.graph.exception.SLNodeNotFoundException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.remote.annotation.DisposeMethod;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.User;

/**
 * The Interface SLGraphSession. All methods in this interface throw a
 * {@link org.openspotlight.graph.exception.SLGraphSessionException} if any problem ocurr on persistence level. Any security
 * violation throws {@link org.openspotlight.security.SLInvalidCredentialException}.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLGraphSession extends LockContainer {

    /**
     * Adds the link.
     * 
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @param bidirecional the bidirecional
     * @return the l
     */
    public <L extends SLLink> L addLink( Class<L> linkClass,
                                         SLNode source,
                                         SLNode target,
                                         boolean bidirecional );

    /**
     * Adds the link.
     * 
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @param bidirecional the bidirecional
     * @param persistenceMode the persistence mode
     * @return the l
     */
    public <L extends SLLink> L addLink( Class<L> linkClass,
                                         SLNode source,
                                         SLNode target,
                                         boolean bidirecional,
                                         SLPersistenceMode persistenceMode );

    /**
     * Clear.
     */
    public void clear();

    /**
     * Close.
     */
    @DisposeMethod( callOnTimeout = true )
    public void close();

    /**
     * Creates the context.
     * 
     * @param id the id
     * @return the sL context
     */
    public SLContext createContext( String id );

    /**
     * Creates the api query.
     * 
     * @return the sL query
     */
    public SLQueryApi createQueryApi();

    /**
     * Creates the text query.
     * 
     * @param slqlInput the slql input
     * @throws SLInvalidQuerySyntaxException invalid syntax
     * @return the sL query
     */
    public SLQueryText createQueryText( String slqlInput )
        throws SLInvalidQuerySyntaxException;

    /**
     * Gets the bidirectional links.
     * 
     * @param linkClass the link class
     * @param side1 the side1
     * @param side2 the side2
     * @return the bidirectional links
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getBidirectionalLinks(
                                                                                      Class<L> linkClass,
                                                                                      SLNode side1,
                                                                                      SLNode side2 );

    /**
     * Gets the bidirectional links.
     * 
     * @param side1 the side1
     * @param side2 the side2
     * @return the bidirectional links
     */
    public NeedsSyncronizationCollection<SLLink> getBidirectionalLinks(
                                                                        SLNode side1,
                                                                        SLNode side2 );

    /**
     * Gets the bidirectional links by side.
     * 
     * @param linkClass the link class
     * @param side the side
     * @return the bidirectional links by side
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getBidirectionalLinksBySide(
                                                                                            Class<L> linkClass,
                                                                                            SLNode side );

    /**
     * Gets the bidirectional links by side.
     * 
     * @param side the side
     * @return the bidirectional links by side
     */
    public NeedsSyncronizationCollection<SLLink> getBidirectionalLinksBySide(
                                                                              SLNode side );

    /**
     * Gets the context.
     * 
     * @param id the id
     * @return the context
     */
    public SLContext getContext( String id );

    /**
     * Gets the default encoder.
     * 
     * @return the default encoder
     */
    public SLEncoder getDefaultEncoder();

    /**
     * Gets the encoder factory.
     * 
     * @return the encoder factory
     */
    public SLEncoderFactory getEncoderFactory();

    /**
     * Gets the links.
     * 
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @return the links
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getLinks(
                                                                         Class<L> linkClass,
                                                                         SLNode source,
                                                                         SLNode target );

    /**
     * Gets the links.
     * 
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @param directionType the direction type
     * @return the links
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getLinks(
                                                                         Class<L> linkClass,
                                                                         SLNode source,
                                                                         SLNode target,
                                                                         int directionType );

    /**
     * Gets the links.
     * 
     * @param source the source
     * @param target the target
     * @return the links
     */
    public NeedsSyncronizationCollection<SLLink> getLinks( SLNode source,
                                                           SLNode target );

    /**
     * Gets the links.
     * 
     * @param source the source
     * @param target the target
     * @param directionType the direction type
     * @return the links
     */
    public NeedsSyncronizationCollection<SLLink> getLinks( SLNode source,
                                                           SLNode target,
                                                           int directionType );

    /**
     * Gets the metadata.
     * 
     * @return the metadata
     */
    public SLMetadata getMetadata();

    /**
     * Gets the node by id.
     * 
     * @param id the id
     * @throws SLNodeNotFoundException node not found
     * @return the node by id
     */
    public SLNode getNodeByID( String id ) throws SLNodeNotFoundException;

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @return the nodes by link
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(
                                                                 Class<? extends SLLink> linkClass );

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @param node the node
     * @return the nodes by link
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(
                                                                 Class<? extends SLLink> linkClass,
                                                                 SLNode node );

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @param node the node
     * @param nodeClass the node class
     * @param returnSubTypes the return sub types
     * @return the nodes by link
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(
                                                                               Class<? extends SLLink> linkClass,
                                                                               SLNode node,
                                                                               Class<N> nodeClass,
                                                                               boolean returnSubTypes );

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @param node the node
     * @param nodeClass the node class
     * @param returnSubTypes the return sub types
     * @param direction the direction
     * @return the nodes by link
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(
                                                                               Class<? extends SLLink> linkClass,
                                                                               SLNode node,
                                                                               Class<N> nodeClass,
                                                                               boolean returnSubTypes,
                                                                               int direction );

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @param node the node
     * @param direction the direction
     * @return the nodes by link
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink(
                                                                 Class<? extends SLLink> linkClass,
                                                                 SLNode node,
                                                                 int direction );

    /**
     * Gets the nodes by link.
     * 
     * @param node the node
     * @return the nodes by link
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink( SLNode node );

    /**
     * Gets the nodes by link.
     * 
     * @param node the node
     * @param nodeClass the node class
     * @param returnSubTypes the return sub types
     * @return the nodes by link
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(
                                                                               SLNode node,
                                                                               Class<N> nodeClass,
                                                                               boolean returnSubTypes );

    /**
     * Gets the nodes by link.
     * 
     * @param node the node
     * @param nodeClass the node class
     * @param returnSubTypes the return sub types
     * @param direction the direction
     * @return the nodes by link
     */
    public <N extends SLNode> NeedsSyncronizationCollection<N> getNodesByLink(
                                                                               SLNode node,
                                                                               Class<N> nodeClass,
                                                                               boolean returnSubTypes,
                                                                               int direction );

    /**
     * Gets the nodes by link.
     * 
     * @param node the node
     * @param direction the direction
     * @return the nodes by link
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByLink( SLNode node,
                                                                 int direction );

    /**
     * Gets the nodes by predicate.
     * 
     * @param predicate the predicate
     * @return the nodes by predicate
     */
    public NeedsSyncronizationCollection<SLNode> getNodesByPredicate(
                                                                      SLNodePredicate predicate );

    /**
     * Gets the policy enforcement.
     * 
     * @return the policy enforcement
     */
    public PolicyEnforcement getPolicyEnforcement();

    /**
     * Gets the unidirectional links.
     * 
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @return the unidirectional links
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getUnidirectionalLinks(
                                                                                       Class<L> linkClass,
                                                                                       SLNode source,
                                                                                       SLNode target );

    /**
     * Gets the unidirectional links.
     * 
     * @param source the source
     * @param target the target
     * @return the unidirectional links
     */
    public NeedsSyncronizationCollection<SLLink> getUnidirectionalLinks(
                                                                         SLNode source,
                                                                         SLNode target );

    /**
     * Gets the unidirectional links by source.
     * 
     * @param linkClass the link class
     * @param source the source
     * @return the unidirectional links by source
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getUnidirectionalLinksBySource(
                                                                                               Class<L> linkClass,
                                                                                               SLNode source );

    /**
     * Gets the unidirectional links by source.
     * 
     * @param source the source
     * @return the unidirectional links by source
     */
    public NeedsSyncronizationCollection<SLLink> getUnidirectionalLinksBySource(
                                                                                 SLNode source );

    /**
     * Gets the unidirectional links by target.
     * 
     * @param linkClass the link class
     * @param target the target
     * @return the unidirectional links by target
     */
    public <L extends SLLink> NeedsSyncronizationCollection<L> getUnidirectionalLinksByTarget(
                                                                                               Class<L> linkClass,
                                                                                               SLNode target );

    /**
     * Gets the unidirectional links by target.
     * 
     * @param target the target
     * @return the unidirectional links by target
     */
    public NeedsSyncronizationCollection<SLLink> getUnidirectionalLinksByTarget(
                                                                                 SLNode target );

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public User getUser();

    /**
     * Save.
     */
    public void save();

    /**
     * Executes a full text search on graph.
     * 
     * @param text text to be searched
     * @return the nodes
     */
    public NeedsSyncronizationCollection<SLNode> searchNodes( String text );

    /**
     * Clean cache.
     */
    public void cleanCache();
}
