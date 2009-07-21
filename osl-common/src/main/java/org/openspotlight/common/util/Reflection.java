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

import static java.util.EnumSet.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;

import java.util.Set;

/**
 * This class has a set of static methods to use for reflection purposes.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class Reflection {
    
    /**
     * This enum has the inheritance types between two classes.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum InheritanceType {
        /**
         * The two types are equals
         */
        SAME_CLASS,
        /**
         * The first type are inherited from the second
         */
        INHERITED_CLASS,
        /**
         * There's no realtion between two types in the given order
         */
        NO_INHERITANCE
    }
    
    /**
     * Enum set of the inherited types.
     */
    public static final Set<InheritanceType> INHERITED_TYPES = of(
            InheritanceType.SAME_CLASS, InheritanceType.INHERITED_CLASS);
    
    /**
     * Search for inheritance type on the given type array.
     * 
     * @param type
     * @param types
     * @return the inheritance type between the type and found type in a array
     */
    public static InheritanceType searchInheritanceType(final Class<?> type,
            final Class<?>... types) {
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
     * @param type
     * @param types
     * @return the type (same or inherited) in the given array, or null if it
     *         was not found
     */
    public static Class<?> searchType(final Class<?> type,
            final Class<?>... types) {
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
    
}
