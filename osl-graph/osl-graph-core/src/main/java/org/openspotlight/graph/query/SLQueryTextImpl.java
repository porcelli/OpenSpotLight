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

import org.apache.log4j.Logger;
import org.openspotlight.graph.SLSimpleGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;

import java.util.Collection;
import java.util.Map;

/**
 * The Class SLQueryTextImpl.
 * 
 * @author porcelli
 */
public class SLQueryTextImpl extends AbstractSLQuery implements SLQueryText {

    /** The Constant LOGGER. */
    static final Logger               LOGGER = Logger.getLogger(SLQueryTextImpl.class);

    private final Lock                lock;

    /** The internal query. */
    private final SLQueryTextInternal query;

    /**
     * Instantiates a new SLQueryTextImpl.
     * 
     * @param session the session
     * @param treeSession the tree session
     * @param textQuery the text query
     */
    public SLQueryTextImpl(
                            final SLSimpleGraphSession session, final SLPersistentTreeSession treeSession,
                            final SLQueryTextInternal textQuery ) {
        super(session, treeSession);
        lock = session.getLockObject();
        query = textQuery;
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Collection<SLNode> inputNodes,
                                  final Map<String, ?> variableValues )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Collection<SLNode> inputNodes,
                                  final Map<String, ?> variableValues,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Collection<SLNode> inputNodes,
                                  final Map<String, ?> variableValues,
                                  final SortMode sortMode,
                                  final boolean showSLQL )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Collection<SLNode> inputNodes,
                                  final Map<String, ?> variableValues,
                                  final SortMode sortMode,
                                  final boolean showSLQL,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Map<String, ?> variableValues )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute((String[])null, variableValues, SortMode.NOT_SORTED, false, null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Map<String, ?> variableValues,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute((String[])null, variableValues, SortMode.NOT_SORTED, false, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Map<String, ?> variableValues,
                                  final SortMode sortMode,
                                  final boolean showSLQL )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute((String[])null, variableValues, sortMode, showSLQL, null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final Map<String, ?> variableValues,
                                  final SortMode sortMode,
                                  final boolean showSLQL,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute((String[])null, variableValues, sortMode, showSLQL, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final String[] inputNodesIDs,
                                  final Map<String, ?> variableValues )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(inputNodesIDs, variableValues, SortMode.NOT_SORTED, false, null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final String[] inputNodesIDs,
                                  final Map<String, ?> variableValues,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(inputNodesIDs, variableValues, SortMode.NOT_SORTED, false, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final String[] inputNodesIDs,
                                  final Map<String, ?> variableValues,
                                  final SortMode sortMode,
                                  final boolean showSLQL )
        throws SLInvalidQueryElementException, SLInvalidQuerySyntaxException, SLQueryException {
        synchronized (lock) {
            return this.execute(inputNodesIDs, variableValues, sortMode, showSLQL, null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( final String[] inputNodesIDs,
                                  final Map<String, ?> variableValues,
                                  final SortMode sortMode,
                                  final boolean showSLQL,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return query.execute(session, variableValues, inputNodesIDs, sortMode, showSLQL, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SLQueryResult execute( final String[] inputNodesIDs,
                                  final SortMode sortMode,
                                  final boolean showSLQL,
                                  final Integer limit,
                                  final Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        synchronized (lock) {
            return this.execute(inputNodesIDs, null, sortMode, showSLQL, limit, offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult executeTarget() throws SLInvalidQueryElementException, SLInvalidQuerySyntaxException, SLQueryException {
        synchronized (lock) {
            return this.executeTarget(SortMode.NOT_SORTED, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult executeTarget( final SortMode sortMode,
                                        final boolean showSLQL )
        throws SLInvalidQueryElementException, SLInvalidQuerySyntaxException, SLQueryException {
        synchronized (lock) {
            if (query.getTarget() != null) {
                return query.getTarget().execute(session, null, null, sortMode, showSLQL, null, null);
            }
            return new SLQueryResultImpl(this, (SLNode[])null, null);
        }
    }

    public Lock getLockObject() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    public String getOutputModelName() {
        synchronized (lock) {
            return query.getOutputModelName();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLQLVariable> getVariables() {
        synchronized (lock) {
            return query.getVariables();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasOutputModel() {
        synchronized (lock) {
            return query.hasOutputModel();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasTarget() {
        synchronized (lock) {
            return query.hasTarget();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasVariables() {
        synchronized (lock) {
            return query.hasVariables();
        }
    }
}
