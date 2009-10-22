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

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;

/**
 * The Class SLQueryTextImpl.
 * 
 * @author porcelli
 */
public class SLQueryTextImpl extends AbstractSLQuery implements SLQueryText {

    /** The Constant LOGGER. */
    static final Logger         LOGGER = Logger.getLogger(SLQueryTextImpl.class);

    /** The internal query. */
    private SLQueryTextInternal query;

    /**
     * Instantiates a new SLQueryTextImpl.
     * 
     * @param session the session
     * @param treeSession the tree session
     * @param textQuery the text query
     */
    public SLQueryTextImpl(
                            SLGraphSession session, SLPersistentTreeSession treeSession, SLQueryTextInternal textQuery ) {
        super(session, treeSession);
        this.query = textQuery;
    }

    /**
     * {@inheritDoc}
     */
    public String getOutputModelName() {
        return query.getOutputModelName();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLQLVariable> getVariables() {
        return query.getVariables();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasOutputModel() {
        return query.hasOutputModel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasTarget() {
        return query.hasTarget();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasVariables() {
        return query.hasVariables();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SLQueryResult execute( String[] inputNodesIDs,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(inputNodesIDs, null, sortMode, showSLQL, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Map<String, ?> variableValues )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute((String[])null, variableValues, SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Map<String, ?> variableValues,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute((String[])null, variableValues, SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Collection<SLNode> inputNodes,
                                  Map<String, ?> variableValues )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Collection<SLNode> inputNodes,
                                  Map<String, ?> variableValues,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( String[] inputNodesIDs,
                                  Map<String, ?> variableValues )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(inputNodesIDs, variableValues, SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( String[] inputNodesIDs,
                                  Map<String, ?> variableValues,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(inputNodesIDs, variableValues, SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute((String[])null, variableValues, sortMode, showSLQL, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute((String[])null, variableValues, sortMode, showSLQL, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Collection<SLNode> inputNodes,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( Collection<SLNode> inputNodes,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return execute(SLQuerySupport.getNodeIDs(inputNodes), variableValues, SortMode.NOT_SORTED, false, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( String[] inputNodesIDs,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL )
        throws SLInvalidQueryElementException, SLInvalidQuerySyntaxException, SLQueryException {
        return execute(inputNodesIDs, variableValues, sortMode, showSLQL, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult execute( String[] inputNodesIDs,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset )
        throws SLInvalidQueryElementException, SLQueryException, SLInvalidQuerySyntaxException {
        return query.execute(session, variableValues, inputNodesIDs, sortMode, showSLQL, limit, offset);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult executeTarget() throws SLInvalidQueryElementException, SLInvalidQuerySyntaxException, SLQueryException {
        return executeTarget(SortMode.NOT_SORTED, false);
    }

    /**
     * {@inheritDoc}
     */
    public SLQueryResult executeTarget( SortMode sortMode,
                                        boolean showSLQL )
        throws SLInvalidQueryElementException, SLInvalidQuerySyntaxException, SLQueryException {
        if (query.getTarget() != null) {
            return query.getTarget().execute(session, null, null, sortMode, showSLQL, null, null);
        }
        return new SLQueryResultImpl((SLNode[])null, null);
    }
}
