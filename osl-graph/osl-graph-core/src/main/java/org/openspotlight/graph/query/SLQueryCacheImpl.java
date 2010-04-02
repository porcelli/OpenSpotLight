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
package org.openspotlight.graph.query;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.graph.*;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.exception.SLNodeNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.SLQuery.SortMode;
import org.openspotlight.graph.util.ProxyUtil;

import java.util.Collection;
import java.util.List;

// TODO: Auto-generated Javadoc
//FIXME maybe it needs some synchronization
/**
 * The Class SLQueryCacheImpl. Default Implementations of {@link SLQueryCache}.
 * 
 * @author porcelli
 */
public class SLQueryCacheImpl implements SLQueryCache {

    /** The tree session. */
    private final SLPersistentTreeSession treeSession;

    /** The session. */
    private final SLGraphSession          session;

    /**
     * Instantiates a new query cache impl.
     * 
     * @param treeSession the tree session
     * @param session the session
     */
    public SLQueryCacheImpl(
                             final SLPersistentTreeSession treeSession,
                             final SLGraphSession session ) {
        this.treeSession = treeSession;
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    public void add2Cache( final String queryId,
                           final Collection<PNodeWrapper> nodes )
        throws SLPersistentTreeSessionException, SLNodeNotFoundException,
            SLGraphSessionException {
        final SLPersistentNode pcacheRootNode = SLCommonSupport
                                                               .getQueryCacheNode(treeSession);
        final SLPersistentNode queryCache = pcacheRootNode.addNode(queryId);
        int i = 0;
        for (final PNodeWrapper pNodeWrapper : nodes) {
            final SLPersistentNode refNode = queryCache.addNode(pNodeWrapper
                                                                            .getID());
            refNode.setProperty(Integer.class, "order", i);
            i++;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String buildQueryId( final List<SLSelect> selects,
                                final Integer collatorStrength,
                                final String[] inputNodesIDs,
                                final SortMode sortMode,
                                final Integer limit,
                                final Integer offset )
        throws SLException {
        final StringBuilder sb = new StringBuilder();
        sb.append("select:[");
        for (final SLSelect activeSelect : selects) {
            sb.append(activeSelect.toString());
        }
        sb.append("]|\ninput:[");
        if (inputNodesIDs != null) {
            for (final String nodeId : inputNodesIDs) {
                sb.append(nodeId);
            }
        }
        sb.append("]|\ncolator:[").append(collatorStrength).append("]");
        sb.append("]|\nsort:[").append(sortMode).append("]");
        sb.append("]|\nlimit:[");
        if (limit != null) {
            sb.append(limit);
            sb.append("]|\noffset:[");
            if (offset != null) {
                sb.append(offset);
                sb.append("]");
            } else {
                sb.append("]");
            }
        } else {
            sb.append("]");
        }

        return Sha1.getSha1SignatureEncodedAsHexa(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResultImpl getCache( final String queryId )
        throws SLPersistentTreeSessionException, SLNodeNotFoundException,
        SLGraphSessionException {
        final SLPersistentNode pcacheRootNode = SLCommonSupport
                                                               .getQueryCacheNode(treeSession);
        SLPersistentNode queryCache;
        try {
            queryCache = pcacheRootNode.getNode(queryId);
            if (queryCache != null) {
                final SLNode[] nodes = new SLNode[queryCache.getNodes().size()];
                for (final SLPersistentNode activeId : queryCache.getNodes()) {
                    final SLNode node = session.getNodeByID(activeId.getName());
                    final SLNode nodeProxy = ProxyUtil.createNodeProxy(
                                                                       SLNode.class, node);
                    nodes[activeId.getProperty(Integer.class, "order")
                                  .getValue()] = nodeProxy;
                }
                return new SLQueryResultImpl(treeSession, nodes, queryId);
            }
        } catch (final Exception e) {
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() throws SLGraphSessionException, SLPersistentTreeSessionException {
        SLCommonSupport
                       .getQueryCacheNode(treeSession).remove();
    }
}
