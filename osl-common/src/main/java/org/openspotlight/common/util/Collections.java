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

package org.openspotlight.common.util;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Helper class to deal with collections
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class Collections {

    /**
     * Creates an immutable map in a null pointer safe way
     * 
     * @param <K>
     * @param <V>
     * @param base
     * @return an immutable map
     */
    public static <K, V> Map<K, V> createImmutableMap( final Map<K, V> base ) {
        Map<K, V> temp = base;
        if (temp == null) {
            temp = emptyMap();
        } else {
            temp = unmodifiableMap(new HashMap<K, V>(temp));
        }
        return temp;
    }

    /**
     * Creates an immutable set in a null pointer safe way
     * 
     * @param <E>
     * @param base
     * @return an immutable set
     */
    public static <E> Set<E> createImmutableSet( final Set<E> base ) {
        Set<E> temp = base;
        if (temp == null) {
            temp = emptySet();
        } else {
            temp = unmodifiableSet(new HashSet<E>(temp));
        }
        return temp;
    }

    /**
     * Creates the new collection.
     * 
     * @param <I>
     * @param <C>
     * @param collectionType the collection type
     * @param initialSize the initial size
     * @return the c
     */
    @SuppressWarnings( "unchecked" )
    public static <I> Collection<I> createNewCollection( final Class<? extends Collection> collectionType,
                                                         final int initialSize ) {
        if (Set.class.isAssignableFrom(collectionType)) {
            return new HashSet<I>(initialSize);
        } else if (Queue.class.isAssignableFrom(collectionType)) {
            return new PriorityQueue<I>(initialSize);
        } else if (List.class.isAssignableFrom(collectionType)) {
            return new ArrayList<I>(initialSize);
        } else {
            return new ArrayList<I>(initialSize);
        }
    }

    /**
     * Convenient method to create a typed set using varargs.
     * 
     * @param <T>
     * @param elements
     * @return a new set with the elements
     */
    public static <T> Set<T> setOf( final T... elements ) {
        final HashSet<T> set = new HashSet<T>();
        if(elements!=null){
	        for (final T e : elements) {
	            set.add(e);
	        }
        }
        return set;
    }

    /**
     * Should not be instantiated
     */
    private Collections() {
        logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }
}
