/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 *  Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 *  or third-party contributors as indicated by the @author tags or express
 *  copyright attribution statements applied by the authors.  All third-party
 *  contributions are distributed under license by CARAVELATECH CONSULTORIA E
 *  TECNOLOGIA EM INFORMATICA LTDA.
 *
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU
 *  Lesser General Public License, as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this distribution; if not, write to:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 *
 * **********************************************************************
 *  OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 *  Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 *  EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 *  @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 *  Todas as contribuições de terceiros estão distribuídas sob licença da
 *  CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 *  Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 *  termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 *  Foundation.
 *
 *  Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 *  GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 *  FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 *  programa; se não, escreva para:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 */

package org.openspotlight.graph;

import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.manipulation.GraphTransientWriter;
import org.openspotlight.remote.annotation.DisposeMethod;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.User;

/**
 * This is the simplest session available of OpenSpotLight graph. <br>
 * Thru this session you can read any data and write transient data ({@link Node}s and {@link Link}s), this sessions has just a
 * single method that allows update data into graph server: {@link #flushChangedProperties}.
 * <p>
 * <b>Important Note</b> its important to execute {@link #shutdown} method at end of its use.
 * 
 * @author porcelli
 */
public interface SimpleGraphSession {

    /**
     * Gives access to graph reader operations.
     * 
     * @param location where the reader should look for data
     * @return the graph reader interface
     */
    GraphReader from( GraphLocation location );

    /**
     * Gives access to the interface that enables write transient data.
     * 
     * @return the transient writter interface
     */
    GraphTransientWriter toTransient();

    void flushChangedProperties( Node node );

    /**
     * Returns the active user.
     * 
     * @return the user
     */
    User getUser();

    /**
     * Returns the policy enforcement.
     * 
     * @return the policy enforcement
     */
    PolicyEnforcement getPolicyEnforcement();

    /**
     * Should be executed after session use, this method cleans the cache and prevent memory leaks.
     */
    @DisposeMethod( callOnTimeout = true )
    void shutdown();
}
