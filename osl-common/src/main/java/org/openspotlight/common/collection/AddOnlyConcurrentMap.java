/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
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
package org.openspotlight.common.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openspotlight.common.util.Assertions;

/**
 * This map has a support for adding a key only once. It is possible to clear its values, but it isn't possible to add values more
 * than once. Also, the replace methods are illegal.
 * 
 * @author feu
 * @param <K>
 * @param <V>
 */
public class AddOnlyConcurrentMap<K, V> implements ConcurrentMap<K, V> {

    public static <K, V> AddOnlyConcurrentMap<K, V> newMap() {

        return new AddOnlyConcurrentMap<K, V>(new ConcurrentHashMap<K, V>());
    }

    private final ConcurrentMap<K, V> wrapped;

    public AddOnlyConcurrentMap(
                                 final ConcurrentMap<K, V> wrapped) {
        Assertions.checkNotNull("wrapped", wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public synchronized void clear() {
        wrapped.clear();
    }

    @Override
    public boolean containsKey(final Object key) {
        return wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return wrapped.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return wrapped.entrySet();
    }

    @Override
    public boolean equals(final Object o) {
        return wrapped.equals(o);
    }

    @Override
    public V get(final Object key) {
        return wrapped.get(key);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return wrapped.keySet();
    }

    @Override
    public synchronized V put(final K key,
                               final V value) {
        validateKey(key);
        return wrapped.put(key, value);
    }

    @Override
    public synchronized void putAll(final Map<? extends K, ? extends V> t) {
        for (final Map.Entry<? extends K, ? extends V> entry: t.entrySet()) {
            validateKey(entry.getKey());
            wrapped.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public synchronized V putIfAbsent(final K key,
                                       final V value) {
        validateKey(key);
        return wrapped.putIfAbsent(key, value);
    }

    @Override
    public V remove(final Object key) {
        return wrapped.remove(key);
    }

    @Override
    public boolean remove(final Object key,
                           final Object value) {
        return wrapped.remove(key, value);
    }

    @Override
    public V replace(final K key,
                      final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(final K key,
                            final V oldValue,
                            final V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    private void validateKey(final K key) {
        if (containsKey(key)) { throw new IllegalStateException("this key was already associated with another value"); }
    }

    @Override
    public Collection<V> values() {
        return wrapped.values();
    }

}
