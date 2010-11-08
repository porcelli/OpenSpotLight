/**
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores
 * terceiros indicados pela etiqueta
 * 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de
 *         terceiros estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa
 *         é software livre; você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU
 *         conforme publicada pela Free Software Foundation. Este programa é distribuído na expectativa de que seja útil,
 *         porém, SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE
 *         ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes. Você deve ter recebido uma cópia da
 *         Licença Pública Geral Menor do GNU junto com este programa; se não, escreva para: Free Software Foundation, Inc. 51
 *         Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */

package org.openspotlight.common.util;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Helper class to deal with collections
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class SLCollections {

    private static enum IteratorNextState {
        HAS_NEXT,
        HAS_NOT_NEXT,
        RESET
    }

    public static interface ReturnOneEntryCommand<T> {
        T getEntry();
    }

    /**
     * Should not be instantiated
     */
    private SLCollections() {
        logAndThrow(new IllegalStateException(Messages
            .getString("invalidConstructor"))); //$NON-NLS-1$
    }

    public static <T> boolean contains(
                                       final Iterable<T> iterable, final T item) {
        for (final T t: iterable) {
            if (item.equals(t)) { return true; }
        }
        return false;
    }

    /**
     * Creates an immutable map in a null pointer safe way
     * 
     * @param <K>
     * @param <V>
     * @param base
     * @return an immutable map
     */
    public static <K, V> Map<K, V> createImmutableMap(
                                                      final Map<K, V> base) {
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
    public static <E> Set<E> createImmutableSet(
                                                final Set<E> base) {
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
     * @param collectionType the collection type
     * @param initialSize the initial size
     * @return the c
     */
    @SuppressWarnings("unchecked")
    public static <I> Collection<I> createNewCollection(
                                                        final Class<? extends Iterable> collectionType,
                                                        final int initialSize) {
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

    public static <T> T firstOf(
                                final Iterable<T> ts) {
        T result = null;
        if (ts != null) {
            final Iterator<T> it = ts.iterator();
            if (it.hasNext()) {
                result = it.next();
            }
        }
        return result;
    }

    public static <K, V> V getOrPut(final Map<K, V> map, final K key, final V defaultValue) {
        V v = map.get(key);
        if (v == null) {
            map.put(key, defaultValue);
            v = defaultValue;
        }
        return v;
    }

    public static <T> Iterable<T> iterableOf(
                                             final T... ts) {
        final ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        for (final T tn: ts) {
            builder.add(tn);
        }
        return builder.build();
    }

    public static <T> Iterable<T> iterableOf(
                                             final T t, final T... ts) {
        final ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        builder.add(t);
        if (ts != null) {
            for (final T tn: ts) {
                builder.add(tn);
            }
        }
        return builder.build();
    }

    public static <T> Iterable<T> iterableOfAll(
                                                final Iterable<Iterable<T>> iterables) {
        return new Iterable<T>() {

            IteratorNextState nextState = IteratorNextState.RESET;

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    private Iterator<T>         currentIterator = null;
                    final Iterator<Iterable<T>> it              = iterables.iterator();

                    private boolean hasNextIterator() {
                        if (IteratorNextState.RESET.equals(nextState)) {
                            if (currentIterator == null
                                || !currentIterator.hasNext()) {
                                while (it.hasNext()) {
                                    currentIterator = it.next().iterator();
                                    if (currentIterator.hasNext()) {
                                        nextState = IteratorNextState.HAS_NEXT;
                                        return true;
                                    }

                                }
                                nextState = IteratorNextState.HAS_NOT_NEXT;
                                return false;
                            }
                            nextState = IteratorNextState.HAS_NEXT;
                            return true;
                        }
                        return IteratorNextState.HAS_NEXT.equals(nextState);
                    }

                    @Override
                    public boolean hasNext() {
                        return hasNextIterator();
                    }

                    @Override
                    public T next() {
                        try {
                            if (hasNextIterator()) {
                                final T result = currentIterator.next();
                                if (result == null) { throw new NullPointerException(); }
                                return result;
                            }
                            throw new NoSuchElementException();
                        } finally {
                            nextState = IteratorNextState.RESET;
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <T> Iterable<T> iterableOfAll(
                                                final Iterable<T> iterable,
                                                final Iterable<T>... iterables) {
        final ImmutableSet.Builder<Iterable<T>> builder = ImmutableSet.builder();
        builder.add(iterable);
        for (final Iterable<T> it: iterables) {
            builder.add(it);
        }
        return iterableOfAll(builder.build());
    }

    public static <T> Iterable<T> iterableOfOne(
                                                final ReturnOneEntryCommand<T> command) {
        final Iterable<T> result = new Iterable<T>() {

            private boolean iterated = false;
            private boolean loaded   = false;
            private T       ref      = null;

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    @Override
                    public boolean hasNext() {
                        if (iterated) {
                        return false;
                        }
                        if (!loaded) {
                            ref = command.getEntry();
                            loaded = true;
                        }
                        if (loaded && ref == null) {
                        return false;
                        }
                        return true;
                    }

                    @Override
                    public T next() {
                        if (iterated) {
                        throw new NoSuchElementException();
                        }
                        if (!loaded) {
                            ref = command.getEntry();
                            loaded = true;
                        }
                        if (loaded && ref == null) {
                        throw new NoSuchElementException();
                        }
                        if (ref != null) {
                            iterated = true;
                        }
                        return ref;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        return result;
    }

    public static <T> Iterable<T> iterableOfOne(
                                                final T t) {
        final ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        builder.add(t);
        return builder.build();
    }

    public static <T> java.util.List<T> iterableToList(
                                                       final Iterable<T> iterable) {
        if (iterable == null) { return Collections.emptyList(); }
        final Iterator<T> it = iterable.iterator();
        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        while (it.hasNext()) {
            builder.add(it.next());
        }
        return builder.build();
    }

    /**
     * Convenient method to newPair a typed set using varargs.
     * 
     * @param <T>
     * @param elements
     * @return a new set with the elements
     */
    public static <T> Set<T> setOf(
                                   final T... elements) {
        final HashSet<T> set = new HashSet<T>();
        if (elements != null) {
            for (final T e: elements) {
                set.add(e);
            }
        }
        return set;
    }
}
