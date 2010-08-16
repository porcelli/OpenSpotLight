/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA **********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.graph.query;

import java.util.Collection;
import java.util.Map;

import org.openspotlight.graph.Node;

/**
 * The Interface SLQueryText. This class is exposed to the user.
 * 
 * @author porcelli
 */
public interface QueryText extends SLQuery {

    /**
     * Checks for target.
     * 
     * @return true, if has target
     */
    public boolean hasTarget();

    /**
     * Checks for variables.
     * 
     * @return true, if has variables
     */
    public boolean hasVariables();

    /**
     * Checks for output model.
     * 
     * @return true, if has output model
     */
    public boolean hasOutputModel();

    /**
     * Gets the output model name.
     * 
     * @return the output model name
     */
    public String getOutputModelName();

    /**
     * Gets the variables.
     * 
     * @return the variables
     */
    public Collection<SLQLVariable> getVariables();

    /**
     * Execute.
     * 
     * @param variableValues the variable values
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Map<String, ?> variableValues)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param variableValues the variable values
     * @param limit the limit
     * @param offset the offset
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Map<String, ?> variableValues,
                                  Integer limit,
                                  Integer offset)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodes the input nodes
     * @param variableValues the variable values
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Collection<Node> inputNodes,
                                  Map<String, ?> variableValues)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodes the input nodes
     * @param variableValues the variable values
     * @param limit the limit
     * @param offset the offset
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Collection<Node> inputNodes,
                                  Map<String, ?> variableValues,
                                  Integer limit,
                                  Integer offset)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodesIDs the input nodes id
     * @param variableValues the variable values
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(String[] inputNodesIDs,
                                  Map<String, ?> variableValues)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodesIDs the input nodes i ds
     * @param variableValues the variable values
     * @param limit the limit
     * @param offset the offset
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(String[] inputNodesIDs,
                                  Map<String, ?> variableValues,
                                  Integer limit,
                                  Integer offset)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param variableValues the variable values
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param variableValues the variable values
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @param limit the limit
     * @param offset the offset
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodes the input nodes
     * @param variableValues the variable values
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Collection<Node> inputNodes,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodes the input nodes
     * @param variableValues the variable values
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @param limit the limit
     * @param offset the offset
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(Collection<Node> inputNodes,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodesIDs the input nodes id
     * @param variableValues the variable values
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(String[] inputNodesIDs,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute.
     * 
     * @param inputNodesIDs the input nodes i ds
     * @param variableValues the variable values
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @param limit the limit
     * @param offset the offset
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult execute(String[] inputNodesIDs,
                                  Map<String, ?> variableValues,
                                  SortMode sortMode,
                                  boolean showSLQL,
                                  Integer limit,
                                  Integer offset)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute target query. If there is no target query, returns empty result.
     * 
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult executeTarget()
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

    /**
     * Execute target. If there is no target query, returns empty result.
     * 
     * @param sortMode the sort mode
     * @param showSLQL the show slql
     * @return the sL query result
     * @throws SLInvalidQueryElementException the SL invalid query element exception
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws SLQueryException the SL query exception
     */
    public SLQueryResult executeTarget(SortMode sortMode,
                                        boolean showSLQL)
        throws SLInvalidQueryElementException, InvalidQuerySyntaxException, SLQueryException;

}
