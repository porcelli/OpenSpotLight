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
package org.openspotlight.common.collection;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openspotlight.common.exception.SLRuntimeException;

/**
 * Created by User: feu - Date: Jun 25, 2010 - Time: 4:54:50 PM
 */
public class IteratorBuilder {

    public static <T, O> SimpleIteratorBuilder<T, O> createIteratorBuilder() {
        return new SimpleIteratorBuilder();
    }

    public static class SimpleIteratorBuilder<T, O> {

        private SimpleIteratorBuilder() {
        }

        private NextItemReferee<O> referee;
        private Iterable<O>        items;
        private Converter<T, O>    converter;

        public SimpleIteratorBuilder withReferee( NextItemReferee<O> referee ) {
            this.referee = referee;
            return this;
        }

        public SimpleIteratorBuilder withItems( Iterable<O> items ) {
            this.items = items;
            return this;
        }

        public SimpleIteratorBuilder withConverter( Converter<T, O> converter ) {
            this.converter = converter;
            return this;
        }

        public Iterable<T> andBuild() {
            return new SimpleIterable<T, O>(items, referee, converter);
        }
    }

    public static interface Converter<T, O> {
        T convert( O o ) throws Exception;
    }

    public static interface NextItemReferee<O> {
        boolean canAcceptAsNewItem( O o ) throws Exception;
    }

    private static class SimpleIterable<T, O> implements Iterable<T> {

        private final Iterable<O>        origin;

        private final NextItemReferee<O> referee;

        private final Converter<T, O>    converter;

        private SimpleIterable( Iterable<O> origin, NextItemReferee<O> referee, Converter<T, O> converter ) {
            this.origin = origin;
            this.referee = referee;
            this.converter = converter;
        }

        private class SimpleIterator implements Iterator<T> {

            private final Iterator<O> iterator;

            private O                 cachedOrigin;

            public SimpleIterator( Iterator<O> iterator ) {
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext() {
                if (referee != null) {
                    cachedOrigin = null;
                    O tmp = null;

                    if (iterator.hasNext()) {
                        tmp = iterator.next();
                    }
                    while (tmp != null) {

                        try {
                            if (referee.canAcceptAsNewItem(tmp)) {
                                cachedOrigin = tmp;
                                return true;
                            }
                        } catch (Exception e) {
                            throw logAndReturnNew(e, SLRuntimeException.class);
                        }
                        if (iterator.hasNext()) tmp = iterator.next();
                        else tmp = null;
                    }
                    return false;
                } else {
                    return iterator.hasNext();
                }
            }

            @Override
            public T next() {
                if (referee != null) {
                    try {
                        if (cachedOrigin == null) {
                            if (!hasNext()) throw new NoSuchElementException();
                        }
                        try {
                            return converter.convert(cachedOrigin);

                        } catch (Exception e) {
                            throw logAndReturnNew(e, SLRuntimeException.class);
                        }
                    } finally {
                        cachedOrigin = null;

                    }
                } else {
                    try {
                        return converter.convert(iterator.next());

                    } catch (Exception e) {
                        throw logAndReturnNew(e, SLRuntimeException.class);
                    }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public Iterator<T> iterator() {
            return new SimpleIterator(origin.iterator());

        }
    }
}
