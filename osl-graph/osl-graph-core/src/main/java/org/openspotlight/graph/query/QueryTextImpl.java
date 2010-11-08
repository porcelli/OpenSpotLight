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

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.exception.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.manipulation.GraphReader;

/**
 * The Class SLQueryTextImpl.
 * 
 * @author porcelli
 */
public class QueryTextImpl extends AbstractSLQuery implements QueryText {

    /** The Constant LOGGER. */
    static final Logger             LOGGER = Logger.getLogger(QueryTextImpl.class);

    /** The internal query. */
    private final QueryTextInternal query;

    /**
     * Instantiates a new SLQueryTextImpl.
     * 
     * @param session the session
     * @param treeSession the tree session
     * @param textQuery the text query
     */
    public QueryTextImpl(final GraphReader session,
                         final QueryTextInternal textQuery) {
        super(session);
        query = textQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Collection<Node> inputNodes,
                               final Map<String, ?> variableValues)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute(QuerySupport.getNodeIDs(inputNodes),
                variableValues, SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Collection<Node> inputNodes,
                               final Map<String, ?> variableValues, final Integer limit,
                               final Integer offset)
        throws InvalidQueryElementException,
            QueryException, SLInvalidQuerySyntaxException {
        return this.execute(QuerySupport.getNodeIDs(inputNodes),
                variableValues, SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Collection<Node> inputNodes,
                               final Map<String, ?> variableValues, final SortMode sortMode,
                               final boolean showSLQL)
        throws InvalidQueryElementException,
            QueryException, SLInvalidQuerySyntaxException {
        return this.execute(QuerySupport.getNodeIDs(inputNodes),
                variableValues, SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Collection<Node> inputNodes,
                               final Map<String, ?> variableValues, final SortMode sortMode,
                               final boolean showSLQL, final Integer limit, final Integer offset)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute(QuerySupport.getNodeIDs(inputNodes),
                variableValues, SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Map<String, ?> variableValues)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute((String[]) null, variableValues,
                SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Map<String, ?> variableValues,
                               final Integer limit, final Integer offset)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute((String[]) null, variableValues,
                SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Map<String, ?> variableValues,
                               final SortMode sortMode, final boolean showSLQL)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute((String[]) null, variableValues, sortMode,
                showSLQL, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final Map<String, ?> variableValues,
                               final SortMode sortMode, final boolean showSLQL,
                               final Integer limit, final Integer offset)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute((String[]) null, variableValues, sortMode,
                showSLQL, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final String[] inputNodesIDs,
                               final Map<String, ?> variableValues)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute(inputNodesIDs, variableValues, SortMode.NOT_SORTED,
                false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final String[] inputNodesIDs,
                               final Map<String, ?> variableValues, final Integer limit,
                               final Integer offset)
        throws InvalidQueryElementException,
            QueryException, SLInvalidQuerySyntaxException {
        return this.execute(inputNodesIDs, variableValues, SortMode.NOT_SORTED,
                false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final String[] inputNodesIDs,
                               final Map<String, ?> variableValues, final SortMode sortMode,
                               final boolean showSLQL)
        throws InvalidQueryElementException,
            SLInvalidQuerySyntaxException, QueryException {
        return this.execute(inputNodesIDs, variableValues, sortMode, showSLQL,
                null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final String[] inputNodesIDs,
                               final Map<String, ?> variableValues, final SortMode sortMode,
                               final boolean showSLQL, final Integer limit, final Integer offset)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return query.execute(session, variableValues, inputNodesIDs, sortMode,
                showSLQL, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final String[] inputNodesIDs,
                               final SortMode sortMode, final boolean showSLQL,
                               final Integer limit, final Integer offset)
            throws InvalidQueryElementException, QueryException,
            SLInvalidQuerySyntaxException {
        return this.execute(inputNodesIDs, null, sortMode, showSLQL, limit,
                offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult executeTarget()
        throws InvalidQueryElementException,
            SLInvalidQuerySyntaxException, QueryException {
        return this.executeTarget(SortMode.NOT_SORTED, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult executeTarget(final SortMode sortMode,
                                     final boolean showSLQL)
        throws InvalidQueryElementException,
            SLInvalidQuerySyntaxException, QueryException {
        if (query.getTarget() != null) { return query.getTarget().execute(session, null, null, sortMode,
                    showSLQL, null, null); }
        return new QueryResultImpl((Node[]) null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputModelName() {
        return query.getOutputModelName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<SLQLVariable> getVariables() {
        return query.getVariables();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOutputModel() {
        return query.hasOutputModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTarget() {
        return query.hasTarget();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasVariables() {
        return query.hasVariables();
    }
}
