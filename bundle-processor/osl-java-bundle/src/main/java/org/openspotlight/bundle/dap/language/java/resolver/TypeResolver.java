package org.openspotlight.bundle.dap.language.java.resolver;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspotlight.graph.SLNode;

public interface TypeResolver<N extends SLNode> {

    public static enum IncludedResult {
        INCLUDE_ACTUAL_TYPE_ON_RESULT,
        DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT
    }

    public static enum ResultOrder {
        ASC,
        DESC
    }

    public static final class Util {
        @SuppressWarnings( "boxing" )
        private static final class CachedTypeFinder<W extends SLNode> implements TypeResolver<W> {

            private interface Command<R> {
                public R execute();
            }

            private static class ParameterKey {
                private final Object[] parameters;
                private final int      hashcode;

                ParameterKey(
                              final Object... o ) {
                    this.parameters = o;
                    this.hashcode = hashOf(this.parameters);
                }

                @Override
                public boolean equals( final Object obj ) {
                    if (obj == this) {
                        return true;
                    }
                    if (!(obj instanceof ParameterKey)) {
                        return false;
                    }
                    final ParameterKey anotherKey = (ParameterKey)obj;
                    return eachEquality(of(this.parameters), andOf(anotherKey.parameters));
                }

                @Override
                public int hashCode() {
                    return this.hashcode;
                }

            }

            private static final Object             NULL_RESULT = new Object();

            private final Map<ParameterKey, Object> cache       = new HashMap<ParameterKey, Object>();

            final TypeResolver<W>                   wrapped;

            CachedTypeFinder(
                              final TypeResolver<W> toWrap ) {
                this.wrapped = toWrap;
            }

            public <T extends W> BestTypeMatch bestMatch( final T reference,
                                                          final T t1,
                                                          final T t2 ) throws InternalJavaFinderError {

                return this.getFromCache(new Command<BestTypeMatch>() {
                    public BestTypeMatch execute() {
                        return CachedTypeFinder.this.wrapped.bestMatch(reference, t1, t2);
                    }
                }, "bestMatch/3", reference, t1, t2);
            }

            public <T extends W> int countAllChildren( final T activeType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countAllChildren(activeType);
                    }
                }, "countAllChildren/1", activeType);
            }

            public <T extends W> int countAllChildren( final T activeType,
                                                       final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countAllChildren(activeType, includedResult);
                    }
                }, "countAllChildren/2", activeType, includedResult);
            }

            public <T extends W> int countAllParents( final T activeType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countAllParents(activeType);
                    }
                }, "countAllParents/1", activeType);
            }

            public <T extends W> int countAllParents( final T activeType,
                                                      final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countAllParents(activeType, includedResult);
                    }
                }, "countAllParents/2", activeType, includedResult);
            }

            public <T extends W> int countConcreteChildren( final T activeType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countConcreteChildren(activeType);
                    }
                }, "countConcreteChildren/1", activeType);
            }

            public <T extends W> int countConcreteChildren( final T activeType,
                                                            final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countConcreteChildren(activeType, includedResult);
                    }
                }, "countConcreteChildren/2", activeType, includedResult);
            }

            public <T extends W> int countConcreteParents( final T activeType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countConcreteParents(activeType);
                    }
                }, "countConcreteParents/1", activeType);
            }

            public <T extends W> int countConcreteParents( final T activeType,
                                                           final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countConcreteParents(activeType, includedResult);
                    }
                }, "countConcreteParents/2", activeType, includedResult);
            }

            public <T extends W> int countInterfaceChildren( final T activeType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countInterfaceChildren(activeType);
                    }
                }, "countInterfaceChildren/1", activeType);

            }

            public <T extends W> int countInterfaceChildren( final T activeType,
                                                             final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countInterfaceChildren(activeType, includedResult);
                    }
                }, "countInterfaceChildren/2", activeType, includedResult);
            }

            public <T extends W> int countInterfaceParents( final T activeType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countInterfaceParents(activeType);
                    }
                }, "countInterfaceParents/1", activeType);
            }

            public <T extends W> int countInterfaceParents( final T activeType,
                                                            final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<Integer>() {
                    public Integer execute() {
                        return CachedTypeFinder.this.wrapped.countInterfaceParents(activeType, includedResult);
                    }
                }, "countInterfaceParents/2", activeType, includedResult);
            }

            public <T extends W, A extends T> List<T> getAllChildren( final A activeType,
                                                                      final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder order,
                                                                      final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getAllChildren(activeType, order, includedResult);
                    }
                }, "getAllChildren/3", activeType, order, includedResult);
            }

            public <T extends W, A extends T> List<T> getAllParents( final A activeType,
                                                                     final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder order,
                                                                     final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getAllParents(activeType, order, includedResult);
                    }
                }, "getAllParents/3", activeType, order, includedResult);
            }

            public <T extends W, A extends T> List<T> getConcreteChildren( final A activeType,
                                                                           final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder order,
                                                                           final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getConcreteChildren(activeType, order, includedResult);
                    }
                }, "getConcreteChildren/3", activeType, order, includedResult);
            }

            public <T extends W, A extends W> List<T> getConcreteParents( final A activeType,
                                                                          final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder order,
                                                                          final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.getConcreteParents(activeType, order, includedResult);
                    }
                }, "getConcreteParents/3", activeType, order, includedResult);
            }

            public <T extends W, A extends T> List<T> getDirectConcreteChildren( final A activeType )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getDirectConcreteChildren(activeType);
                    }
                }, "getDirectConcreteChildren/1", activeType);

            }

            public <T extends W, A extends T> List<T> getDirectConcreteParents( final A activeType )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getDirectConcreteParents(activeType);
                    }
                }, "getDirectConcreteParents/1", activeType);
            }

            public <T extends W, A extends T> List<T> getDirectInterfaceChildren( final A activeType )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getDirectInterfaceChildren(activeType);
                    }
                }, "getDirectInterfaceChildren/1", activeType);
            }

            public <T extends W, A extends T> List<T> getDirectInterfaceParents( final A activeType )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getDirectInterfaceParents(activeType);
                    }
                }, "getDirectInterfaceParents/1", activeType);
            }

            @SuppressWarnings( "unchecked" )
            private <T> T getFromCache( final Command<T> command,
                                        final Object... parameters ) {
                final ParameterKey k = new ParameterKey(parameters);
                final Object t = this.cache.get(k);
                if (t == null) {
                    final T newResult = command.execute();
                    if (newResult == null) {
                        this.cache.put(k, NULL_RESULT);
                        return null;
                    }
                    this.cache.put(k, newResult);
                    return newResult;

                } else if (t == NULL_RESULT) {
                    return null;
                }
                return (T)t;

            }

            public <T extends W, A extends T> List<T> getInterfaceChildren( final A activeType,
                                                                            final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder order,
                                                                            final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getInterfaceChildren(activeType, order, includedResult);
                    }
                }, "getInterfaceChildren/3", activeType, order, includedResult);
            }

            public <T extends W, A extends T> List<T> getInterfaceParents( final A activeType,
                                                                           final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder order,
                                                                           final org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult includedResult )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<List<T>>() {
                    public List<T> execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getInterfaceParents(activeType, order, includedResult);
                    }
                }, "getInterfaceParents/3", activeType, order, includedResult);
            }

            public <T extends W, A extends W> T getPrimitiveFor( final A wrappedType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<T>() {
                    public T execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getPrimitiveFor(wrappedType);
                    }
                }, "getPrimitiveFor/1", wrappedType);
            }

            public <T extends W> T getType( final String typeToSolve ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<T>() {
                    public T execute() {
                        return CachedTypeFinder.this.wrapped.<T>getType(typeToSolve);
                    }
                }, "getType/1", typeToSolve);
            }

            public <T extends W, A extends T> W getType( final String typeToSolve,
                                                         final A activeType,
                                                         final List<? extends W> parametrizedTypes )
                throws InternalJavaFinderError {
                return this.getFromCache(new Command<W>() {
                    public W execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getType(typeToSolve, activeType, parametrizedTypes);
                    }
                }, "getType/3", typeToSolve, activeType, parametrizedTypes);
            }

            public <T extends W, A extends W> T getWrapperFor( final A primitiveType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<T>() {
                    public T execute() {
                        return CachedTypeFinder.this.wrapped.<T, A>getWrapperFor(primitiveType);
                    }
                }, "getWrapperFor/1", primitiveType);

            }

            public <T extends W> boolean isConcreteType( final T type ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Boolean>() {
                    public Boolean execute() {
                        return CachedTypeFinder.this.wrapped.isConcreteType(type);
                    }
                }, "isConcreteType/1", type);
            }

            public <T extends W> boolean isPrimitiveType( final T type ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Boolean>() {
                    public Boolean execute() {
                        return CachedTypeFinder.this.wrapped.isPrimitiveType(type);
                    }
                }, "isPrimitiveType/1", type);
            }

            public <T extends W, A extends W> boolean isTypeOf( final T implementation,
                                                                final A superType ) throws InternalJavaFinderError {
                return this.getFromCache(new Command<Boolean>() {
                    public Boolean execute() {
                        return CachedTypeFinder.this.wrapped.isTypeOf(implementation, superType);
                    }
                }, "isTypeOf/2", implementation, superType);
            }
        }

        public static <T extends SLNode> TypeResolver<T> createCached( final TypeResolver<T> toWrap ) {
            return new CachedTypeFinder<T>(toWrap);
        }

    }

    public abstract <T extends N> BestTypeMatch bestMatch( final T reference,
                                                           final T t1,
                                                           final T t2 ) throws InternalJavaFinderError;

    public abstract <T extends N> int countAllChildren( final T activeType ) throws InternalJavaFinderError;

    public abstract <T extends N> int countAllChildren( final T activeType,
                                                        final IncludedResult includedResult ) throws InternalJavaFinderError;

    public abstract <T extends N> int countAllParents( final T activeType ) throws InternalJavaFinderError;

    public abstract <T extends N> int countAllParents( final T activeType,
                                                       final IncludedResult includedResult ) throws InternalJavaFinderError;

    public abstract <T extends N> int countConcreteChildren( final T activeType ) throws InternalJavaFinderError;

    public abstract <T extends N> int countConcreteChildren( final T activeType,
                                                             final IncludedResult includedResult ) throws InternalJavaFinderError;

    public abstract <T extends N> int countConcreteParents( final T activeType ) throws InternalJavaFinderError;

    public abstract <T extends N> int countConcreteParents( final T activeType,
                                                            final IncludedResult includedResult ) throws InternalJavaFinderError;

    public abstract <T extends N> int countInterfaceChildren( final T activeType ) throws InternalJavaFinderError;

    public abstract <T extends N> int countInterfaceChildren( final T activeType,
                                                              final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N> int countInterfaceParents( final T activeType ) throws InternalJavaFinderError;

    public abstract <T extends N> int countInterfaceParents( final T activeType,
                                                             final IncludedResult includedResult ) throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getAllChildren( final A activeType,
                                                                       final ResultOrder order,
                                                                       final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getAllParents( final A activeType,
                                                                      final ResultOrder order,
                                                                      final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getConcreteChildren( final A activeType,
                                                                            final ResultOrder order,
                                                                            final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends N> List<T> getConcreteParents( final A activeType,
                                                                           final ResultOrder order,
                                                                           final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getDirectConcreteChildren( final A activeType )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getDirectConcreteParents( final A activeType )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getDirectInterfaceChildren( final A activeType )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getDirectInterfaceParents( final A activeType )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getInterfaceChildren( final A activeType,
                                                                             final ResultOrder order,
                                                                             final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends T> List<T> getInterfaceParents( final A activeType,
                                                                            final ResultOrder order,
                                                                            final IncludedResult includedResult )
        throws InternalJavaFinderError;

    public abstract <T extends N, A extends N> T getPrimitiveFor( final A wrappedType ) throws InternalJavaFinderError;

    /**
     * Gets the type.
     * 
     * @param typeToSolve the type to solve
     * @return CachedTypeFinder.this.wrapped.the type
     */
    public abstract <T extends N> T getType( String typeToSolve ) throws InternalJavaFinderError;

    /**
     * Gets the type.
     * 
     * @param typeToSolve the type to solve
     * @param activeType the active type
     * @param parametrizedTypes the parametrized types
     * @return CachedTypeFinder.this.wrapped.the type
     */
    public abstract <T extends N, A extends T> N getType( String typeToSolve,
                                                          A activeType,
                                                          List<? extends N> parametrizedTypes ) throws InternalJavaFinderError;

    public abstract <T extends N, A extends N> T getWrapperFor( final A primitiveType ) throws InternalJavaFinderError;

    /**
     * Checks if is type of.
     * 
     * @param type the type
     * @param anotherType the another type
     * @return CachedTypeFinder.this.wrapped.true, if is type of
     * @throws InternalJavaFinderError
     */
    public abstract <T extends N> boolean isConcreteType( final T type ) throws InternalJavaFinderError;

    public abstract <T extends N> boolean isPrimitiveType( final T type ) throws InternalJavaFinderError;

    /**
     * Checks if is type of.
     * 
     * @param type the type
     * @param anotherType the another type
     * @return CachedTypeFinder.this.wrapped.true, if is type of
     * @throws InternalJavaFinderError
     */
    public abstract <T extends N, A extends N> boolean isTypeOf( final T implementation,
                                                                 final A superType ) throws InternalJavaFinderError;

}
