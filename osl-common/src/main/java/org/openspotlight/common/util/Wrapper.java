package org.openspotlight.common.util;

/**
 * Created by User: feu - Date: Apr 6, 2010 - Time: 5:38:38 PM
 */
public abstract class Wrapper<W> {
    protected W wrapped;

    public static <W> Wrapper<W> createMutable() {
        return new MutableWrapper<W>();
    }

    public static <W> Wrapper<W> createImmutable( W wrapped ) {
        return new ImmutableWrapper<W>(wrapped);
    }

    public abstract W getWrapped();

    public abstract void setWrapped( W wrapped );

    private static final class MutableWrapper<W> extends Wrapper<W> {

        public W getWrapped() {
            return wrapped;
        }

        public void setWrapped( W wrapped ) {
            this.wrapped = wrapped;
        }
    }

    private static final class ImmutableWrapper<W> extends Wrapper<W> {
        private ImmutableWrapper(
                                  W wrapped ) {
            this.wrapped = wrapped;
        }

        public W getWrapped() {
            return wrapped;
        }

        public void setWrapped( W wrapped ) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (!(o instanceof Wrapper)) return false;

        Wrapper wrapper = (Wrapper)o;

        if (wrapped != null ? !wrapped.equals(wrapper.wrapped) : wrapper.wrapped != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return wrapped != null ? wrapped.hashCode() : 0;
    }
}
