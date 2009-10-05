package org.openspotlight.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public final class InvocationCacheFactory {
    private static class CachedInterceptor implements MethodInterceptor {

        private static final class Key {
            private final int      hashcode;
            private final Object[] parameters;
            private final String   key;

            public Key(
                        final String key, final Object... parameters ) {
                if (parameters == null) {
                    throw new IllegalArgumentException();
                }
                if (key == null) {
                    throw new IllegalArgumentException();
                }
                if (key.length() == 0) {
                    throw new IllegalArgumentException();
                }
                this.key = key;
                this.parameters = parameters;
                int hashing = 7;
                hashing = 31 * hashing + key.hashCode();
                for (final Object parameter : parameters) {
                    hashing = 31 * hashing + (parameter == null ? 0 : parameter.hashCode());
                }
                this.hashcode = hashing;
            }

            @Override
            public boolean equals( final Object obj ) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Key)) {
                    return false;
                }
                final Key that = (Key)obj;
                if (that.parameters.length != this.parameters.length) {
                    return false;
                }
                if (!isEquals(this.key, that.key)) {
                    return false;
                }
                for (int i = 0, size = this.parameters.length; i < size; i++) {
                    if (!isEquals(this.parameters[i], that.parameters[i])) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public int hashCode() {
                return this.hashcode;
            }

        }

        private static final class ThrowableWrapped {
            final Throwable throwable;

            public ThrowableWrapped(
                                     final Throwable toWrap ) {
                this.throwable = toWrap;

            }
        }

        enum UseEnhanced {
            USE_ENHANCED,
            USE_WRAPPED
        }

        private static String getMethodUniqueName( final Method arg1 ) {
            final Class<?>[] parameterTypes = arg1.getParameterTypes();
            final StringBuilder nameBuff = new StringBuilder();
            nameBuff.append(arg1.getName());
            nameBuff.append(':');
            nameBuff.append(arg1.getReturnType().getName());
            nameBuff.append('/');
            for (int i = 0, size = parameterTypes.length; i < size; i++) {
                nameBuff.append(parameterTypes[i].getName());
                if (i != size - 1) {
                    nameBuff.append(',');
                }
            }
            return nameBuff.toString();
        }

        static boolean isEquals( final Object o1,
                                 final Object o2 ) {
            if (o1 == o2) {
                return true;
            }
            if (o1 == null) {
                return false;
            }
            if (o2 == null) {
                return false;
            }
            return o1.equals(o2);
        }

        private final UseEnhanced      useEnhancedMethod;

        private Object                 source;

        private final Map<Key, Object> cache      = new HashMap<Key, Object>();

        private static final Object    NULL_VALUE = new Object();

        private static final Object    VOID_VALUE = new Object();

        public CachedInterceptor(
                                  final UseEnhanced useEnhancedMethod ) {
            this.useEnhancedMethod = useEnhancedMethod;
        }

        public Object intercept( final Object enhancedTarget,
                                 final Method method,
                                 final Object[] parameters,
                                 final MethodProxy proxy ) throws Throwable {
            if (Modifier.isPublic(method.getModifiers())) {
                final String uniqueName = getMethodUniqueName(method);
                final Key key = new Key(uniqueName, parameters);
                Object value = this.cache.get(key);
                if (value == null) {
                    boolean invocationOk = true;
                    try {
                        value = this.invoke(method, parameters, proxy);
                    } catch (final InvocationTargetException e) {
                        invocationOk = false;
                        final Throwable toWrap = e.getTargetException();
                        this.cache.put(key, new ThrowableWrapped(toWrap));
                    } catch (final Throwable t) {
                        invocationOk = false;
                        this.cache.put(key, new ThrowableWrapped(t));
                    }
                    if (invocationOk) {
                        if (method.getReturnType().equals(Void.TYPE)) {
                            this.cache.put(key, VOID_VALUE);
                        } else if (value == null) {
                            this.cache.put(key, NULL_VALUE);
                        } else {
                            this.cache.put(key, value);
                        }
                    }
                    value = this.cache.get(key);
                }

                if (value == VOID_VALUE || value == NULL_VALUE) {
                    return null;
                } else if (value instanceof ThrowableWrapped) {
                    final ThrowableWrapped wrapped = (ThrowableWrapped)value;
                    throw wrapped.throwable;
                }
                return value;
            }
            try {
                final Object value = this.invoke(method, parameters, proxy);

                return value;
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }

        }

        private Object invoke( final Method method,
                               final Object[] parameters,
                               final MethodProxy proxy ) throws Throwable, IllegalAccessException, InvocationTargetException {
            Object value = null;
            switch (this.useEnhancedMethod) {
                case USE_ENHANCED:
                    value = proxy.invokeSuper(this.source, parameters);
                    break;
                case USE_WRAPPED:
                    value = method.invoke(this.source, parameters);
                    break;
            }
            return value;
        }

        public void setSource( final Object source ) {
            this.source = source;
        }

    }

    public static <T> T createIntoCached( final Class<T> superClass,
                                          final Class<?>[] argumentTypes,
                                          final Object[] arguments ) {
        final CachedInterceptor interceptor = new CachedInterceptor(CachedInterceptor.UseEnhanced.USE_ENHANCED);
        final Enhancer e = new Enhancer();
        e.setSuperclass(superClass);
        e.setCallback(interceptor);
        @SuppressWarnings( "unchecked" )
        final T wrapped = (T)e.create(argumentTypes, arguments);
        interceptor.setSource(wrapped);
        return wrapped;
    }

    public static <T> T wrapIntoCached( final T toWrap ) {
        final CachedInterceptor interceptor = new CachedInterceptor(CachedInterceptor.UseEnhanced.USE_WRAPPED);
        interceptor.setSource(toWrap);
        final Enhancer e = new Enhancer();
        e.setSuperclass(toWrap.getClass());
        e.setCallback(interceptor);
        @SuppressWarnings( "unchecked" )
        final T wrapped = (T)e.create();
        return wrapped;
    }

    private InvocationCacheFactory() {
        throw new UnsupportedOperationException();
    }

}
