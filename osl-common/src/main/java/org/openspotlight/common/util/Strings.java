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
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

/**
 * Helper class with convenient String methods.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class Strings {
    
    /**
     * Converts the first character to lower case
     * 
     * @param toBeCorrected
     * @return a string that starts with lower case
     */
    public static String firstLetterToLowerCase(final String toBeCorrected) {
        checkNotNull("toBeCorrected", toBeCorrected);//$NON-NLS-1$
        if (toBeCorrected.length() == 0) {
            return toBeCorrected;
        }
        if (toBeCorrected.length() == 1) {
            return toBeCorrected.toLowerCase();
        }
        final String newString = toBeCorrected.substring(0, 1).toLowerCase()
                + toBeCorrected.substring(1);
        return newString;
    }
    
    /**
     * Converts the first character to upper case
     * 
     * @param toBeCorrected
     * @return a string that starts with capital letter
     */
    public static String firstLetterToUpperCase(final String toBeCorrected) {
        checkNotNull("toBeCorrected", toBeCorrected);//$NON-NLS-1$
        if (toBeCorrected.length() == 0) {
            return toBeCorrected;
        }
        if (toBeCorrected.length() == 1) {
            return toBeCorrected.toUpperCase();
        }
        final String newString = toBeCorrected.substring(0, 1).toUpperCase()
                + toBeCorrected.substring(1);
        return newString;
    }
    
    /**
     * removes an starting string for a bigger string that starts with it.
     * 
     * @param beginning
     * @param toBeCorrected
     * @return the string without the beggining
     */
    public static String removeBegginingFrom(final String beginning,
            final String toBeCorrected) {
        checkNotEmpty("beginning", beginning);//$NON-NLS-1$
        checkNotEmpty("toBeCorrected", toBeCorrected);//$NON-NLS-1$
        checkCondition("startsWithBeginning", toBeCorrected//$NON-NLS-1$
                .startsWith(beginning));
        return toBeCorrected.substring(beginning.length());
    }
    
    /**
     * Should not be instantiated
     */
    private Strings() {
        logAndThrow(new IllegalStateException(Messages
                .getString("invalidConstructor"))); //$NON-NLS-1$
    }
    
}
