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

import static java.text.DateFormat.getDateInstance;
import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class to deal with dates
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class Dates {
    
    private static final DateFormat DF = getDateInstance();
    static {
        ((SimpleDateFormat) DF).applyPattern(Messages
                .getString("Dates.defaultFormat")); //$NON-NLS-1$
    }
    
    /**
     * Creates a date using the default date format
     * 
     * @param dateString
     * @return a new date
     */
    public static Date dateFromString(final String dateString) {
        checkNotEmpty("dateString", dateString); //$NON-NLS-1$
        try {
            return DF.parse(dateString);
        } catch (final Exception e) {
            throw logAndReturn(new IllegalArgumentException(format(Messages
                    .getString("Dates.invalidDateFormat"), dateString))); //$NON-NLS-1$
        }
    }
    
    /**
     * Returns a string using the date passed on argument and the default format
     * 
     * @param date
     * @return a formatted string
     */
    public static String stringFromDate(final Date date) {
        checkNotNull("date", date); //$NON-NLS-1$
        return DF.format(date);
        
    }
    
    /**
     * Should not be instantiated
     */
    private Dates() {
        logAndThrow(new IllegalStateException(Messages
                .getString("invalidConstructor"))); //$NON-NLS-1$
    }
    
}
