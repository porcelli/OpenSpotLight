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
 * The main contract here is the creation of {@link StorageNode} instances using a builder pattern.<br>
 * 
 * @author feuteston
 * @author porcelli
 */
public interface NodeFactory {

    /**
     * The builder interface for {@link StorageNode}.
     * 
     * @author feuteston
     * @author porcelli
     */
    interface NodeBuilder {

        /**
         * Creates the {@link StorageNode}.
         * 
         * @return the new storage node
         * @throws RuntimeException if a storage related exception occur
         */
        StorageNode andCreate()
            throws RuntimeException;

        /**
         * Add to the builder a parent reference.
         * 
         * @param parent the parent node
         * @return the builder
         * @throws IllegalStateException if parent key already added on builder
         * @throws IllegalArgumentException if input param is null
         */
        NodeBuilder withParent(StorageNode parent)
            throws IllegalArgumentException, IllegalStateException;

        /**
         * Add to the builder a parent reference.
         * 
         * @param parent the parent node key as string
         * @return the builder
         * @throws IllegalStateException if parent key already added on builder
         * @throws IllegalArgumentException if input param is null or empty
         */
        NodeBuilder withParent(String parentKey)
            throws IllegalArgumentException, IllegalStateException;

        /**
         * Add to the builder a simple key-value pair.
         * 
         * @param name
         * @param value
         * @return the builder
         * @throws IllegalStateException if key name already added on builder
         * @throws IllegalArgumentException if any input param is null or empty
         */
        NodeBuilder withSimpleKey(String name, String value)
            throws IllegalArgumentException, IllegalStateException;
    }

    /**
     * Start point to create a {@link StorageNode}.
     * 
     * @param session the storage session
     * @param type the node type
     * @return the node builder
     * @throws IllegalArgumentException if any input param is null or empty
     */
    NodeBuilder createWithType(StorageSession session, String type)
        throws IllegalArgumentException;
}
