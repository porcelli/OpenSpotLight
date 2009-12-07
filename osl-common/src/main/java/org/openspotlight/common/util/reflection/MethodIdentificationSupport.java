package org.openspotlight.common.util.reflection;

import java.lang.reflect.Method;

/**
 * This class contains some useful static methods and inner classes to be used to identify a method or a method with specific
 * parameters. This class should be used only on API stuffs.
 *
 * @author feu
 */
public class MethodIdentificationSupport {

    /**
     * Parameter key to be used as a key inside the cache map for method invocation.
     *
     * @author feu
     */
    public static final class MethodWithParametersKey {

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
        public MethodWithParametersKey(
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
            if (!(obj instanceof MethodWithParametersKey)) {
                return false;
            }
            final MethodWithParametersKey that = (MethodWithParametersKey)obj;
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
     * This enum specifies the behavior applied on the new object. It should behave like a wrapped object when the default
     * constructor is used, or like enhanced object when the non default constructor is called.
     */
    public static enum UseEnhanced {

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
    public static String getMethodUniqueName( final Method arg1 ) {
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

}
