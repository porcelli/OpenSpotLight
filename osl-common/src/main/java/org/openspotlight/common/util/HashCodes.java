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

package org.openspotlight.common.util;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Float.floatToIntBits;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.util.Map;

/**
 * Helper class to build hashCode methods in a secure and concise way. All the hash functions for primitive types was created
 * based on Effective Java book. to be used like this...
 * 
 * <pre>
 * private volatile hashcode;
 * 
 * public int hashCode(){
 *  int result = hashcode;
 *  if(result = 0){
 *   result = hashOf(attribute1,attribute2,..);
 *   hashcode = result;
 *  }
 *  return result;
 * }
 * 
 * </pre>
 * 
 * or like this...
 * 
 * <pre>
 * public int hashCode(){
 *  int result = hashcode;
 *  if(result = 0){
 *   result = hashOf(attribute1,attribute2,..);
 *   hashcode = result;
 *  }
 *  return result;
 * }
 * </pre>
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class HashCodes {

    /**
     * Should not be instantiated
     */
    private HashCodes() {
        logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param b
     * @return the hash code
     */
    public static int hashOf(final boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param b
     * @return the hash code
     */
    public static int hashOf(final byte b) {
        return b;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param c
     * @return the hash code
     */
    public static int hashOf(final char c) {
        return c;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param d
     * @return the hash code
     */
    public static int hashOf(final double d) {
        return hashOf(doubleToLongBits(d));
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param f
     * @return the hash code
     */
    public static int hashOf(final float f) {
        return floatToIntBits(f);
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param i
     * @return the hash code
     */
    public static int hashOf(final int i) {
        return i;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book). To be used like this:
     * 
     * <pre>
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = hashOf(attribute1,attribute2,..);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * </pre>
     * 
     * @param iterable
     * @return the hash code
     */
    public static int hashOf(final Iterable<?> iterable) {
        int result = 17;
        for (final Object attribute: iterable) {
            result = 31 * result + hashOf(attribute);
        }
        return result;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param l
     * @return the hash code
     */
    public static int hashOf(final long l) {
        return (int) (l ^ (l >>> 32));
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book). To be used like this:
     * 
     * <pre>
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = hashOf(attribute1,attribute2,..);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * </pre>
     * 
     * @param map
     * @return the hash code
     */
    public static int hashOf(final Map<?, ?> map) {
        int result = 17;
        for (final Map.Entry<?, ?> entry: map.entrySet()) {
            result = 31 * result + hashOf(entry.getKey());
            result = 31 * result + hashOf(entry.getValue());
        }
        return result;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param o
     * @return the hash code
     */
    public static int hashOf(final Object o) {
        return o == null ? 0 : o.hashCode();
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book). To be used like this:
     * 
     * <pre>
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = hashOf(attribute1,attribute2,..);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * </pre>
     * 
     * @param attributes
     * @return the hash code
     */
    public static int hashOf(final Object... attributes) {
        int result = 17;
        for (final Object attribute: attributes) {
            if (attribute instanceof Iterable<?>) {
                result = 31 * result + hashOf((Iterable<?>) attribute);
            } else if (attribute instanceof Map<?, ?>) {
                result = 31 * result + hashOf((Map<?, ?>) attribute);
            } else {
                result = 31 * result + hashOf(attribute);
            }

        }
        return result;
    }

    /**
     * Hash helper method to be used like this (based on Effective Java book):
     * 
     * <pre>
     * private volatile hashcode;
     * 
     * public int hashCode(){
     *  int result = hashcode;
     *  if(result = 0){
     *   result = 17;
     *   result = 31 * result + hashOf(attribute1);
     *   result = 31 * result + hashOf(attribute2);
     *   result = 31 * result + hashOf(attribute3);
     *   hashcode = result;
     *  }
     *  return result;
     * }
     * 
     * </pre>
     * 
     * @param s
     * @return the hash code
     */
    public static int hashOf(final short s) {
        return s;
    }

}
