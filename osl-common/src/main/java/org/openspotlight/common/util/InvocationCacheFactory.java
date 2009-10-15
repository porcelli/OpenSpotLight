package org.openspotlight.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * This factory is used to create lazy behavior on method invocations. For the first method invocation with some of the
 * parameters, the target method is called, and its response is cached. So, on the next invocations with the same parameters, the
 * result is returned from the cache. It is possible to instantiate the new cached instance if there's a default constructor. Also
 * it's possible to call any constructor. The only restriction is: the target class MUST BE NON FINAL. It caches returned objects
 * and also thrown exceptions. It caches only public method invocations.
 * 
 * @author feu
 */
public final class InvocationCacheFactory {

    /**
     * Internal cache factory class. It uses CGLib inside to subclass the target class.
     * 
     * @author feu
     */
    private static class CachedInterceptor implements MethodInterceptor {

        /**
         * Parameter key to be used as a key inside the cache map for method invocation.
         * 
         * @author feu
         */
        private static final class Key {

            /** The hashcode. */
            private final int      hashcode;

            /** The parameters. */
            private final Object[] parameters;

            /** The key. */
            private final String   key;

            /**
             * Constructor with final fields.
             * 
             * @param key the key
             * @param parameters the parameters
             */
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

        /**
         * This class is used to wrap thrown exceptions.
         */
        private static final class ThrowableWrapped {

            /** The throwable. */
            final Throwable throwable;

            /**
             * Instantiates a new throwable wrapped.
             * 
             * @param toWrap the to wrap
             */
            public ThrowableWrapped(
                                     final Throwable toWrap ) {
                this.throwable = toWrap;

            }
        }

        /**
         * This enum specifies the behavior applied on the new object. It should behave like a wrapped object when the default
         * constructor is used, or like enhanced object when the non default constructor is called.
         */
        enum UseEnhanced {

            /** use enhanced. */
            USE_ENHANCED,

            /** use wrapped. */
            USE_WRAPPED
        }

        /**
         * Gets the method unique name.
         * 
         * @param arg1 the arg1
         * @return the method unique name
         */
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

        /**
         * Checks if is equals in a null pointer safe way.
         * 
         * @param o1 the o1
         * @param o2 the o2
         * @return true, if is equals
         */
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

        /** The use enhanced method. */
        private final UseEnhanced      useEnhancedMethod;

        /** The source. */
        private Object                 source;

        /** The cache. */
        private final Map<Key, Object> cache      = new HashMap<Key, Object>();

        /** The Constant NULL_VALUE. */
        private static final Object    NULL_VALUE = new Object();

        /** The Constant VOID_VALUE. */
        private static final Object    VOID_VALUE = new Object();

        /**
         * Instantiates a new cached interceptor using the behavior described on {@link UseEnhanced}.
         * 
         * @param useEnhancedMethod the use enhanced method
         */
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

        /**
         * Invoke the method itself.
         * 
         * @param method the method
         * @param parameters the parameters
         * @param proxy the proxy
         * @return the object
         * @throws Throwable the throwable
         * @throws IllegalAccessException the illegal access exception
         * @throws InvocationTargetException the invocation target exception
         */
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

        /**
         * Sets the source.
         * 
         * @param source the new source
         */
        public void setSource( final Object source ) {
            this.source = source;
        }

    }

    /**
     * Creates a new InvocationCache object.
     * 
     * @param <T> the type been subclassed.
     * @param superClass the super class
     * @param argumentTypes the argument types
     * @param arguments the arguments
     * @return the T
     */
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

    /**
     * Wrap into cached.
     * 
     * @param <T> the type been subclassed.
     * @param toWrap the to wrap
     * @return the t
     */
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

    /**
     * do not create a new cache factory.
     */
    private InvocationCacheFactory() {
        throw new UnsupportedOperationException();
    }

}
