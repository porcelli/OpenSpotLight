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
package org.openspotlight.graph.query;

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.graph.Node;

/**
 * The Class SLQueryResultImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class QueryResultImpl implements QueryResult {
    /** The nodes. */
    private final List<Node> nodes;

    /** The query id. */
    private final String     queryId;

    /**
     * Instantiates a new sL query result impl.
     * 
     * @param nodes the nodes
     */
    public QueryResultImpl(final List<Node> nodes, final String queryId) {
        this.nodes = nodes != null ? nodes : new ArrayList<Node>();
        this.queryId = queryId;

    }

    /**
     * Instantiates a new sL query result impl.
     * 
     * @param nodes the nodes
     */
    public QueryResultImpl(final Node[] nodes, final String queryId) {
        this.queryId = queryId;
        this.nodes = nodes != null ? new ArrayList<Node>(nodes.length)
                : new ArrayList();
        for (final Node Node: nodes) {
            this.nodes.add(Node);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQueryResult#getNodes()
     */
    @Override
    public List<Node> getNodes()
        throws QueryException {
        return nodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueryId() {
        return queryId;
    }
}
