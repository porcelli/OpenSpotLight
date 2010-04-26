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
package org.openspotlight.graph.persistence;

import javax.jcr.Session;

import org.openspotlight.common.concurrent.LockContainer;

/**
 * The Interface SLPersistentTreeSession.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLPersistentTreeSession extends LockContainer {

    /**
     * Clear.
     * 
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public void clear() throws SLPersistentTreeSessionException;

    /**
     * Close.
     */
    public void close();

    /**
     * Creates the query.
     * 
     * @param statement the statement
     * @param type the type
     * @return the sL persistent query
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public SLPersistentQuery createQuery( String statement,
                                          int type )
            throws SLPersistentTreeSessionException;

    /**
     * Gets the node by id.
     * 
     * @param id the id
     * @return the node by id
     * @throws SLPersistentNodeNotFoundException the SL persistent node not found exception
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public SLPersistentNode getNodeByID( String id )
            throws SLPersistentNodeNotFoundException,
            SLPersistentTreeSessionException;

    /**
     * Gets the node by path.
     * 
     * @param path the path
     * @return the node by path
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public SLPersistentNode getNodeByPath( String path )
            throws SLPersistentTreeSessionException;

    /**
     * Gets the root node.
     * 
     * @return the root node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public SLPersistentNode getRootNode()
            throws SLPersistentTreeSessionException;

    /**
     * Gets the XPath root path.
     * 
     * @return the XPath root path
     */
    public String getXPathRootPath();

    /**
     * Save.
     * 
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public void save() throws SLPersistentTreeSessionException;

    /**
     * This getter is necessary just for Simple Persist.
     * 
     * @return jcr session
     */
    @Deprecated
    public Session getJCRSession();


}
