/**
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
package org.openspotlight.bundle.language.java.resolver;

import org.openspotlight.graph.Node
import java.util.List;

/**
 * The Interface TypeResolver describes a set of methods needed inside a helper class to find types during a parsing phase for
 * example.
 */
public interface TypeResolver<N extends NoNode
    /**
     * The Enum IncludedResult is used to describe if the actual type passed as an argument should be included on result.
     */
    public static enum IncludedResult {

        INCLUDE_ACTUAL_TYPE_ON_RESULT,

        DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT
    }

    /**
     * The Enum ResultOrder is used to order the result.
     */
    public static enum ResultOrder {

        /** The ASC. */
        ASC,

        /** The DESC. */
        DESC
    }

    /**
     * This method finds the best match between two types and a reference.
     * 
     * @param reference the reference
     * @param t1 the t1
     * @param t2 the t2
     * @return the best type match
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> BestTypeMatch bestMatch( final T reference,
                                                           final T t1,
                                                           final T t2 ) throws InternalJavaFinderError;

    /**
     * Count all children.
     * 
     * @param activeType the active type
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countAllChildren( final T activeType ) throws InternalJavaFinderError;

    /**
     * Count all children.
     * 
     * @param activeType the active type
     * @param includedResult the included result
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countAllChildren( final T activeType,
                                                        final IncludedResult includedResult ) throws InternalJavaFinderError;

    /**
     * Count all parents.
     * 
     * @param activeType the active type
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countAllParents( final T activeType ) throws InternalJavaFinderError;

    /**
     * Count all parents.
     * 
     * @param activeType the active type
     * @param includedResult the included result
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countAllParents( final T activeType,
                                                       final IncludedResult includedResult ) throws InternalJavaFinderError;

    /**
     * Count concrete children.
     * 
     * @param activeType the active type
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countConcreteChildren( final T activeType ) throws InternalJavaFinderError;

    /**
     * Count concrete children.
     * 
     * @param activeType the active type
     * @param includedResult the included result
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countConcreteChildren( final T activeType,
                                                             final IncludedResult includedResult ) throws InternalJavaFinderError;

    /**
     * Count concrete parents.
     * 
     * @param activeType the active type
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countConcreteParents( final T activeType ) throws InternalJavaFinderError;

    /**
     * Count concrete parents.
     * 
     * @param activeType the active type
     * @param includedResult the included result
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countConcreteParents( final T activeType,
                                                            final IncludedResult includedResult ) throws InternalJavaFinderError;

    /**
     * Count interface children.
     * 
     * @param activeType the active type
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countInterfaceChildren( final T activeType ) throws InternalJavaFinderError;

    /**
     * Count interface children.
     * 
     * @param activeType the active type
     * @param includedResult the included result
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countInterfaceChildren( final T activeType,
                                                              final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Count interface parents.
     * 
     * @param activeType the active type
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countInterfaceParents( final T activeType ) throws InternalJavaFinderError;

    /**
     * Count interface parents.
     * 
     * @param activeType the active type
     * @param includedResult the included result
     * @return the int
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> int countInterfaceParents( final T activeType,
                                                             final IncludedResult includedResult ) throws InternalJavaFinderError;

    /**
     * Gets the all children.
     * 
     * @param activeType the active type
     * @param order the order
     * @param includedResult the included result
     * @return the all children
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getAllChildren( final A activeType,
                                                                       final ResultOrder order,
                                                                       final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Gets the all parents.
     * 
     * @param activeType the active type
     * @param order the order
     * @param includedResult the included result
     * @return the all parents
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getAllParents( final A activeType,
                                                                      final ResultOrder order,
                                                                      final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Gets the concrete children.
     * 
     * @param activeType the active type
     * @param order the order
     * @param includedResult the included result
     * @return the concrete children
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getConcreteChildren( final A activeType,
                                                                            final ResultOrder order,
                                                                            final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Gets the concrete parents.
     * 
     * @param activeType the active type
     * @param order the order
     * @param includedResult the included result
     * @return the concrete parents
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends N> List<T> getConcreteParents( final A activeType,
                                                                           final ResultOrder order,
                                                                           final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Gets the direct concrete children.
     * 
     * @param activeType the active type
     * @return the direct concrete children
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getDirectConcreteChildren( final A activeType )
        throws InternalJavaFinderError;

    /**
     * Gets the direct concrete parents.
     * 
     * @param activeType the active type
     * @return the direct concrete parents
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getDirectConcreteParents( final A activeType )
        throws InternalJavaFinderError;

    /**
     * Gets the direct interface children.
     * 
     * @param activeType the active type
     * @return the direct interface children
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getDirectInterfaceChildren( final A activeType )
        throws InternalJavaFinderError;

    /**
     * Gets the direct interface parents.
     * 
     * @param activeType the active type
     * @return the direct interface parents
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getDirectInterfaceParents( final A activeType )
        throws InternalJavaFinderError;

    /**
     * Gets the interface children.
     * 
     * @param activeType the active type
     * @param order the order
     * @param includedResult the included result
     * @return the interface children
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getInterfaceChildren( final A activeType,
                                                                             final ResultOrder order,
                                                                             final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Gets the interface parents.
     * 
     * @param activeType the active type
     * @param order the order
     * @param includedResult the included result
     * @return the interface parents
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> List<T> getInterfaceParents( final A activeType,
                                                                            final ResultOrder order,
                                                                            final IncludedResult includedResult )
        throws InternalJavaFinderError;

    /**
     * Gets the primitive for.
     * 
     * @param wrappedType the wrapped type
     * @return the primitive for
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends N> T getPrimitiveFor( final A wrappedType ) throws InternalJavaFinderError;

    /**
     * Gets the type.
     * 
     * @param typeToSolve the type to solve
     * @return CachedTypeFinder.this.wrapped.the type
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> T getType( String typeToSolve ) throws InternalJavaFinderError;

    /**
     * Gets the type.
     * 
     * @param typeToSolve the type to solve
     * @param activeType the active type
     * @param parametrizedTypes the parametrized types
     * @return CachedTypeFinder.this.wrapped.the type
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends T> N getType( String typeToSolve,
                                                          A activeType,
                                                          List<? extends N> parametrizedTypes ) throws InternalJavaFinderError;

    /**
     * Gets the wrapper for.
     * 
     * @param primitiveType the primitive type
     * @return the wrapper for
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends N> T getWrapperFor( final A primitiveType ) throws InternalJavaFinderError;

    /**
     * Checks if is type of.
     * 
     * @param type the type
     * @return CachedTypeFinder.this.wrapped.true, if is type of
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> boolean isConcreteType( final T type ) throws InternalJavaFinderError;

    /**
     * Checks if is primitive type.
     * 
     * @param type the type
     * @return true, if is primitive type
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N> boolean isPrimitiveType( final T type ) throws InternalJavaFinderError;

    /**
     * Checks if is type of.
     * 
     * @param implementation the implementation
     * @param superType the super type
     * @return CachedTypeFinder.this.wrapped.true, if is type of
     * @throws InternalJavaFinderError the internal java finder error
     */
    public abstract <T extends N, A extends N> boolean isTypeOf( final T implementation,
                                                                 final A superType ) throws InternalJavaFinderError;

}
