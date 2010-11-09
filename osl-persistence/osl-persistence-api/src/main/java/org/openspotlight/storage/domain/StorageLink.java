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
package org.openspotlight.storage.domain;

import org.openspotlight.storage.StorageSession;

/**
 * The StorageLink is the way you correlate informations ({@link StorageNode} instances) in persitence. Any relationship between
 * nodes are materialized by a creation of a link. <br>
 * A Link is uniquely identified by three data: Type, Source and Target StorageNodes, and based on these data an algorithm is used
 * to generate an unique id.
 * <p>
 * To secure the data consistency its not possible change the unique identifiers of a StorageLink. If you need so, you'll have to
 * delete it and create a new one.
 * </p>
 * <p>
 * StorageLinks should be created using {@link StorageSession#addLink(StorageNode, StorageNode, String)}.
 * </p>
 * <p>
 * Along with {@link StorageNode}, links are are the core of persitence data model.
 * </p>
 * 
 * @author feuteston
 * @author porcelli
 */
public interface StorageLink extends StorageDataMarker, PropertyContainer {

    /**
     * Returns the link type
     * 
     * @return the link type
     */
    String getType();

    /**
     * Returns the source node of this link.
     * 
     * @return the source node
     */
    StorageNode getSource();

    /**
     * Returns the target node of this link.
     * 
     * @return the target node
     */
    StorageNode getTarget();

    /**
     * Removes the link. <br>
     * <p>
     * <b>Important Note:</b> this is just a sugar method that, in fact, executes the
     * {@link StorageSession#removeLink(StorageLink)}.
     * 
     * @param session the storage session
     */
    void remove(StorageSession session);

}
