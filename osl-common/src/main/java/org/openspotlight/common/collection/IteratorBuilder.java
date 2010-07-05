package org.openspotlight.common.collection;

import org.openspotlight.common.exception.SLRuntimeException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

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
        private Iterable<O> items;
        private Converter<T, O> converter;

        public SimpleIteratorBuilder withReferee(NextItemReferee<O> referee) {
            this.referee = referee;
            return this;
        }

        public SimpleIteratorBuilder withItems(Iterable<O> items) {
            this.items = items;
            return this;
        }

        public SimpleIteratorBuilder withConverter(Converter<T, O> converter) {
            this.converter = converter;
            return this;
        }

        public Iterable<T> andBuild() {
            return new SimpleIterable<T, O>(items, referee, converter);
        }
    }

    public static interface Converter<T, O> {
        T convert(O o) throws Exception;
    }

    public static interface NextItemReferee<O> {
        boolean canAcceptAsNewItem(O o) throws Exception;
    }

    private static class SimpleIterable<T, O> implements Iterable<T> {

        private final Iterable<O> origin;

        private final NextItemReferee<O> referee;

        private final Converter<T, O> converter;

        private SimpleIterable(Iterable<O> origin, NextItemReferee<O> referee, Converter<T, O> converter) {
            this.origin = origin;
            this.referee = referee;
            this.converter = converter;
        }

        private class SimpleIterator implements Iterator<T> {

            private final Iterator<O> iterator;

            private O cachedOrigin;

            public SimpleIterator(Iterator<O> iterator) {
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
                        if (iterator.hasNext())
                            tmp = iterator.next();
                        else
                            tmp = null;
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
