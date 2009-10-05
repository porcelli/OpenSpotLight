package org.openspotlight.bundle.dap.language.java.resolver;

import java.util.List;

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
