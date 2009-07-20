/*
 * Copyright (c) 2008, Alexandre Porcelli or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Alexandre Porcelli.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.openspotlight.structure;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * This class is just an wrapper around SparseMultigraph, so we can use ConcurrentHashMap as our backed data structure and
 * guarantee thread safety. Also we need to access those data types so proper getters were created
 * 
 * @author Vinicius Carvalho
 * 
 * @param <V>
 * @param <E>
 */
public class SynchronizedSparseMultiGraph<V, E> extends SparseMultigraph<V, E> {
    private static final long serialVersionUID = -2847568184390932565L;

    public SynchronizedSparseMultiGraph() {
        this.vertices = new ConcurrentHashMap<V, Pair<Set<E>>>();
        this.edges = new ConcurrentHashMap<E, Pair<V>>();
    }

    public Map<V, Pair<Set<E>>> getInternalVertices() {
        return this.vertices;
    }

    public Map<E, Pair<V>> getInternalEdges() {
        return this.edges;
    }
}
