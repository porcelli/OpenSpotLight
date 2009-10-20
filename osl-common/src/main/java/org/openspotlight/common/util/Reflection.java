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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.common.util;

import static java.util.EnumSet.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.Pair;

/**
 * This class has a set of static methods to use for reflection purposes.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class Reflection {

    /**
     * This enum has the inheritance types between two classes.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    public static enum InheritanceType {

        /** The two types are equals. */
        SAME_CLASS,

        /** The first type are inherited from the second. */
        INHERITED_CLASS,

        /** There's no realtion between two types in the given order. */
        NO_INHERITANCE
    }

    /**
     * The Class UnwrappedCollectionTypeFromMethodReturn.
     * 
     * @param <T>
     */
    public static class UnwrappedCollectionTypeFromMethodReturn<T> {

        /** The collection type. */
        private final Class<? extends Collection<?>> collectionType;

        /** The item type. */
        private final Class<T>                       itemType;

        /**
         * Instantiates a new unwrapped collection type from method return.
         * 
         * @param collectionType the collection type
         * @param itemType the item type
         */
        UnwrappedCollectionTypeFromMethodReturn(
                                                 final Class<? extends Collection<?>> collectionType, final Class<T> itemType ) {
            checkNotNull("collectionType", collectionType);
            checkNotNull("itemType", itemType);
            this.collectionType = collectionType;
            this.itemType = itemType;
        }

        /**
         * Gets the collection type.
         * 
         * @return the collection type
         */
        public Class<? extends Collection<?>> getCollectionType() {
            return this.collectionType;
        }

        /**
         * Gets the item type.
         * 
         * @return the item type
         */
        public Class<T> getItemType() {
            return this.itemType;
        }

    }

    /**
     * The Class UnwrappedMapTypeFromMethodReturn.
     * 
     * @param <K>
     * @param <T>
     */
    public static class UnwrappedMapTypeFromMethodReturn<K, T> {

        /** The item type. */
        private final Pair<Class<K>, Class<T>> itemType;

        /**
         * Instantiates a new unwrapped map type from method return.
         * 
         * @param itemType the item type
         */
        UnwrappedMapTypeFromMethodReturn(
                                          final Pair<Class<K>, Class<T>> itemType ) {
            checkNotNull("itemType", itemType);
            this.itemType = itemType;
        }

        /**
         * Gets the item type.
         * 
         * @return the item type
         */
        public Pair<Class<K>, Class<T>> getItemType() {
            return this.itemType;
        }

    }

    /** Enum set of the inherited types. */
    public static final Set<InheritanceType> INHERITED_TYPES = of(InheritanceType.SAME_CLASS, InheritanceType.INHERITED_CLASS);

    /**
     * Search for inheritance type on the given type array.
     * 
     * @param type the type
     * @param types the types
     * @return the inheritance type between the type and found type in a array
     */
    public static InheritanceType searchInheritanceType( final Class<?> type,
                                                         final Class<?>... types ) {
        checkNotNull("type", type); //$NON-NLS-1$
        checkNotEmpty("types", types); //$NON-NLS-1$
        for (final Class<?> innerType : types) {
            if (eachEquality(type, innerType)) {
                return InheritanceType.SAME_CLASS;
            }
        }
        for (final Class<?> innerType : types) {
            if (innerType.isAssignableFrom(type)) {
                return InheritanceType.INHERITED_CLASS;
            }
        }
        return InheritanceType.NO_INHERITANCE;
    }

    /**
     * Search for a type on the given type array.
     * 
     * @param type the type
     * @param types the types
     * @return the type (same or inherited) in the given array, or null if it was not found
     */
    public static Class<?> searchType( final Class<?> type,
                                       final Class<?>... types ) {
        checkNotNull("type", type); //$NON-NLS-1$
        checkNotEmpty("types", types); //$NON-NLS-1$
        for (final Class<?> innerType : types) {
            if (eachEquality(type, innerType)) {
                return innerType;
            }
        }
        for (final Class<?> innerType : types) {
            if (innerType.isAssignableFrom(type)) {
                return innerType;
            }
        }
        return null;
    }

    /**
     * Unwrap collection from method return.
     * 
     * @param <T>
     * @param method the method
     * @return the unwrapped collection type from method return< t>
     * @throws Exception the exception
     */
    @SuppressWarnings( "unchecked" )
    public static <T> UnwrappedCollectionTypeFromMethodReturn<T> unwrapCollectionFromMethodReturn( final Method method )
        throws Exception {
        checkNotNull("method", method);
        checkCondition("correctReturnType", Collection.class.isAssignableFrom(method.getReturnType()));
        Class<T> itemType = null;
        final Type genType = method.getGenericReturnType();

        if (genType instanceof ParameterizedType) {
            final ParameterizedType paramType = (ParameterizedType)genType;
            final Type[] actualTypeArgs = paramType.getActualTypeArguments();
            final Type theItemType = actualTypeArgs[0];
            if (theItemType instanceof WildcardType) {
                final WildcardType wildCardType = (WildcardType)theItemType;
                final Type[] lowerBounds = wildCardType.getLowerBounds();
                final Type[] upperBounds = wildCardType.getUpperBounds();
                if (lowerBounds != null && lowerBounds.length > 0) {
                    itemType = (Class<T>)lowerBounds[0];
                } else if (upperBounds != null && upperBounds.length > 0) {
                    itemType = (Class<T>)upperBounds[0];
                }

            } else if (theItemType instanceof Class<?>) {
                itemType = (Class<T>)actualTypeArgs[0];

            }

        }

        final Class<? extends Collection<?>> retType = (Class<? extends Collection<?>>)method.getReturnType();

        final UnwrappedCollectionTypeFromMethodReturn<T> result = new UnwrappedCollectionTypeFromMethodReturn<T>(retType,
                                                                                                                 itemType);

        return result;
    }

    /**
     * Unwrap map from method return.
     * 
     * @param <K>
     * @param <T>
     * @param method the method
     * @return the unwrapped map type from method return< k, t>
     * @throws Exception the exception
     */
    @SuppressWarnings( "unchecked" )
    public static <K, T> UnwrappedMapTypeFromMethodReturn<K, T> unwrapMapFromMethodReturn( final Method method ) throws Exception {
        checkNotNull("method", method);
        checkCondition("correctReturnType", Map.class.isAssignableFrom(method.getReturnType()));
        Class<K> keyType = null;
        Class<T> valueType = null;
        final Type genType = method.getGenericReturnType();

        if (genType instanceof ParameterizedType) {
            final ParameterizedType paramType = (ParameterizedType)genType;
            final Type[] actualTypeArgs = paramType.getActualTypeArguments();
            final Type theItemTypeKey = actualTypeArgs[0];
            if (theItemTypeKey instanceof WildcardType) {
                final WildcardType wildCardType = (WildcardType)theItemTypeKey;
                final Type[] lowerBounds = wildCardType.getLowerBounds();
                final Type[] upperBounds = wildCardType.getUpperBounds();
                if (lowerBounds != null && lowerBounds.length > 0) {
                    keyType = (Class<K>)lowerBounds[0];
                } else if (upperBounds != null && upperBounds.length > 0) {
                    keyType = (Class<K>)upperBounds[0];
                }

            } else if (theItemTypeKey instanceof Class<?>) {
                keyType = (Class<K>)theItemTypeKey;

            }
            final Type theItemTypeValue = actualTypeArgs[1];
            if (theItemTypeValue instanceof WildcardType) {
                final WildcardType wildCardType = (WildcardType)theItemTypeValue;
                final Type[] lowerBounds = wildCardType.getLowerBounds();
                final Type[] upperBounds = wildCardType.getUpperBounds();
                if (lowerBounds != null && lowerBounds.length > 0) {
                    valueType = (Class<T>)lowerBounds[0];
                } else if (upperBounds != null && upperBounds.length > 0) {
                    valueType = (Class<T>)upperBounds[0];
                }

            } else if (theItemTypeValue instanceof Class<?>) {
                valueType = (Class<T>)theItemTypeValue;

            }

        }
        final UnwrappedMapTypeFromMethodReturn<K, T> result = new UnwrappedMapTypeFromMethodReturn<K, T>(
                                                                                                         new Pair<Class<K>, Class<T>>(
                                                                                                                                      keyType,
                                                                                                                                      valueType));

        return result;
    }

}
