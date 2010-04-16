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

/**
 * Helper class with comparable convenient methods. To be used like that:
 * 
 * <pre>
 * import static org.openspotlight.common.util.Arrays.of;
 * import static org.openspotlight.common.util.Arrays.andOf;
 * import static org.openspotlight.common.util.Compare.compareAll;
 * 
 *  //...
 * public int compareTo(That that){
 *     compareAll(of(attribute1,attribute2), andOf(that.attribute1,that.attribute2));
 * }
 * 
 * </pre>
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class Compare {

    /**
     * Comparable method to be used inside Comparable classes to compare each parameter and return the first non zero result. If
     * the objects are instances of Comparable, it will use the compareTo method. On the other side, it will use the
     * toString().compareTo() method. This can make a class comparable just calling this method with all the attributes of the
     * class, also it the parameters itself are not comparables.
     * 
     * <pre>
     * import static org.openspotlight.common.util.Arrays.of;
     * import static org.openspotlight.common.util.Arrays.andOf;
     * import static org.openspotlight.common.util.Compare.compareAll;
     * 
     *  //...
     * public int compareTo(That that){
     *     compareAll(of(attribute1,attribute2), andOf(that.attribute1,that.attribute2));
     * }
     * 
     * </pre>
     * 
     * @param <T>
     * @param of
     * @param andOf
     * @return a int resulted by the items comparison
     */
    public static <T> int compareAll( final T[] of,
                                      final T andOf[] ) {
        if ((of == null) && (andOf == null)) {
            return 0;
        }
        if (andOf == null) {
            return -1;
        }
        if (of == null) {
            return 1;
        }
        checkCondition("sameSize", of.length == andOf.length); //$NON-NLS-1$
        final int size = of.length;
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += npeSafeCompare(of[i], andOf[i]);
            if (sum != 0) {
                return sum;
            }
        }
        return sum;
    }

    /**
     * Comparable method witch don't throw null pointer exception. If the objects are instances of Comparable, it will use the
     * compareTo method. On the other side, it will use the toString().compareTo() method. This can make a class comparable just
     * calling this method with all the attributes of the class, also it the parameters itself are not comparables.
     * 
     * @param <T>
     * @param thisObject
     * @param thatObject
     * @return an int resulted by the comparison
     */
    @SuppressWarnings( "unchecked" )
    public static <T> int npeSafeCompare( final T thisObject,
                                          final T thatObject ) {
        if (thisObject == thatObject) {
            return 0;
        }
        if ((thisObject != null) && (thatObject == null)) {
            return 1;
        }
        if (thisObject == null) {
            if ((thatObject != null)) {
                return -1;
            } else {
                return 0;
            }
        }
        if (thisObject.equals(thatObject)) {
            return 0;
        }
        if ((thisObject instanceof Comparable<?>)
                && (thatObject instanceof Comparable<?>)) {
            return ((Comparable<T>)thisObject).compareTo(thatObject);
        } else {
            return npeSafeCompare(thisObject.toString(), thatObject.toString());
        }
    }

    /**
     * Should not be instantiated
     */
    private Compare() {
        logAndThrow(new IllegalStateException(Messages
                                                      .getString("invalidConstructor"))); //$NON-NLS-1$
    }
}
