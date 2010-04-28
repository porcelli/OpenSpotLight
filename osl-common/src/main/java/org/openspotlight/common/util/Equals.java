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

package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.openspotlight.common.exception.SLRuntimeException;

/**
 * Helper class to build equals methods in a secure and concise way.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * @author Vitor Hugo Chagas
 */
public class Equals {

    public static boolean eachEquality( final boolean of,
                                        final boolean andOf ) {
        return of == andOf;
    }

    public static boolean eachEquality( final boolean[] of,
                                        final boolean[] andOf ) {
        return Arrays.equals(of, andOf);
    }

    public static boolean eachEquality( final byte of,
                                        final byte andOf ) {
        return of == andOf;
    }

    public static boolean eachEquality( final byte[] of,
                                        final byte[] andOf ) {
        return Arrays.equals(of, andOf);
    }

    public static boolean eachEquality( final char of,
                                        final char andOf ) {
        return of == andOf;
    }

    /**
     * Each equality.
     * 
     * @param <T> Type of item been comparated
     * @param thisObjectType the this object type
     * @param thisObject the this object
     * @param thatObject the that object
     * @param fieldName the field name
     * @return true, if successful
     */
    public static <T> boolean eachEquality( final Class<T> thisObjectType,
                                            final T thisObject,
                                            final Object thatObject,
                                            final String fieldName ) {
        boolean status = false;
        Assertions.checkNotNull("this object type", thisObjectType);
        Assertions.checkNotNull("this object", thisObject);
        if (thatObject != null && thisObjectType.isInstance(thatObject)) {
            Field field = null;
            try {
                try {
                    field = thisObjectType.getDeclaredField(fieldName);
                } catch (final NoSuchFieldException e) {
                }
                if (field == null) {
                    field = thisObjectType.getDeclaredField(fieldName);
                }
                field.setAccessible(true);
                final Object value1 = field.get(thisObject);
                final Object value2 = field.get(thatObject);
                status = value1 == value2 || value1.equals(value2);
            } catch (final Exception e) {
                throw new SLRuntimeException("Error on attempt to perform " + thisObjectType.getName() + " equality operation.",
                                             e);
            } finally {
                if (field != null) {
                    field.setAccessible(false);
                }
            }
        }
        return status;
    }

    public static boolean eachEquality( final double of,
                                        final double andOf ) {
        return of == andOf;
    }

    public static boolean eachEquality( final double[] of,
                                        final double[] andOf ) {
        return Arrays.equals(of, andOf);
    }

    public static boolean eachEquality( final float of,
                                        final float andOf ) {
        return of == andOf;
    }

    public static boolean eachEquality( final float[] of,
                                        final float[] andOf ) {
        return Arrays.equals(of, andOf);
    }

    public static boolean eachEquality( final int of,
                                        final int andOf ) {
        return of == andOf;
    }

    public static boolean eachEquality( final int[] of,
                                        final int[] andOf ) {
        return Arrays.equals(of, andOf);
    }

    public static boolean eachEquality( final long of,
                                        final long andOf ) {
        return of == andOf;
    }

    public static boolean eachEquality( final long[] of,
                                        final long[] andOf ) {
        return Arrays.equals(of, andOf);
    }

    /**
     * Method that call equals in a null pointer safe way.
     * 
     * @param o1 the o1
     * @param o2 the o2
     * @return true if the two object are equal
     */
    public static boolean eachEquality( final Object o1,
                                        final Object o2 ) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null && o2 != null) {
            return false;
        }
        if (o2 == null && o1 != null) {
            return false;
        }
        return o1 != null && o1.equals(o2);
    }

    /**
     * Equals method to be used like this:
     * 
     * <pre>
     * import static org.openspotlight.common.util.Arrays.of;
     * import static org.openspotlight.common.util.Arrays.andOf;
     * import static org.openspotlight.common.util.Equals.equals;
     * //...
     * public void equals(Object o){
     * if(o==this)
     * return true;
     * if(!(o instanceof ThisClass))
     * return false;
     * ThisClass that = (ThisClass) o;
     * return eachEquality(of(this.attribute1,this.attribute2)
     * ,andOf(that.attribute1,that.attribute2));
     * }
     * 
     * </pre>
     * 
     * @param of the of
     * @param andOf the and of
     * @return true if all objects are equal
     */
    public static boolean eachEquality( final Object[] of,
                                        final Object[] andOf ) {
        if (of == null) {
            if (andOf == null) {
                return true;
            }
            return false;
        }
        checkCondition("sameSize", (of.length == andOf.length)); //$NON-NLS-1$
        final int size = of.length;
        for (int i = 0; i < size; i++) {
            if (!eachEquality(of[i], andOf[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean eachEquality( final short of,
                                        final short andOf ) {
        return of == andOf;
    }

    /**
     * Should not be instantiated.
     */
    private Equals() {
        logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }

}
