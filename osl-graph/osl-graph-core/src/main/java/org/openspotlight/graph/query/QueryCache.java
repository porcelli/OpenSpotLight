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
import java.util.List;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.Query.SortMode;
import org.openspotlight.storage.domain.StorageNode;

/**
 * The Interface SLQueryCache.
 * 
 * @author porcelli
 */
public interface QueryCache {

    /**
     * Adds content to the cache.
     * 
     * @param queryId the query id
     * @param nodes the nodes
     */
    public abstract void add2Cache(final String queryId,
                                   final Collection<StorageNode> nodes);

    /**
     * Builds a unique query id.
     * 
     * @param selects the selects
     * @param collatorStrength the collator strength
     * @param inputNodesIDs the input nodes i ds
     * @param sortMode the sort mode
     * @param limit the limit
     * @param offset the offset
     * @return the string
     * @throws SLException the SL exception
     */
    public abstract String buildQueryId(final List<Select> selects,
                                        final Integer collatorStrength, final String[] inputNodesIDs,
                                        final SortMode sortMode, final Integer limit, final Integer offset)
            throws SLException;

    /**
     * Gets the cache content. Returns null if not found.
     * 
     * @param queryId the query id
     * @return the cache
     */
    public abstract QueryResult getCache(final String queryId);

    /**
     * Flush cache.
     */
    public void flush();
}
