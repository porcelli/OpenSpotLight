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

import static java.util.Collections.unmodifiableSet;
import static org.apache.tools.ant.types.selectors.SelectorUtils.match;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.types.selectors.SelectorUtils;

public class PatternMatcher {
    
    /**
     * Result class with the results of a filter matching using the Ant
     * expression syntax.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static final class FilterResult implements Serializable {
        
        /**
		 * 
		 */
        private static final long serialVersionUID = -6700182758852743670L;
        
        private final Set<String> allNames;
        
        private final Set<String> includedNames;
        private final Set<String> excludedNames;
        private final Set<String> ignoredNames;
        
        FilterResult(final Set<String> allNames,
                final Set<String> includedNames,
                final Set<String> excludedNames, final Set<String> ignoredNames) {
            this.allNames = unmodifiableSet(allNames);
            this.includedNames = unmodifiableSet(includedNames);
            this.excludedNames = unmodifiableSet(excludedNames);
            this.ignoredNames = unmodifiableSet(ignoredNames);
        }
        
        public Set<String> getAllNames() {
            return this.allNames;
        }
        
        public Set<String> getExcludedNames() {
            return this.excludedNames;
        }
        
        public Set<String> getIgnoredNames() {
            return this.ignoredNames;
        }
        
        public Set<String> getIncludedNames() {
            return this.includedNames;
        }
        
    }
    
    /**
     * Filter the names using the Apache Ant expression syntax (and also the
     * {@link SelectorUtils} class.
     * 
     * @param namesToFilter
     * @param includedPatterns
     * @param excludedPatterns
     * @param caseSensitive
     * @return
     */
    public static FilterResult filterNamesByPattern(
            final Set<String> namesToFilter,
            final Set<String> includedPatterns,
            final Set<String> excludedPatterns, final boolean caseSensitive) {
        checkNotNull("namesToFilter", namesToFilter); //$NON-NLS-1$
        checkNotNull("includedPatterns", includedPatterns); //$NON-NLS-1$
        checkNotNull("excludedPatterns", excludedPatterns); //$NON-NLS-1$
        
        final Set<String> includedNames = new HashSet<String>();
        final Set<String> excludedNames = new HashSet<String>();
        
        for (final String nameToFilter : namesToFilter) {
            for (final String included : includedPatterns) {
                if (match(included, nameToFilter, caseSensitive)) {
                    includedNames.add(nameToFilter);
                }
            }
            for (final String excluded : excludedPatterns) {
                if (match(excluded, nameToFilter, caseSensitive)) {
                    excludedNames.add(nameToFilter);
                    includedNames.remove(nameToFilter);
                }
            }
        }
        final Set<String> ignoredNames = new HashSet<String>(namesToFilter);
        ignoredNames.removeAll(includedNames);
        ignoredNames.removeAll(excludedNames);
        final FilterResult result = new FilterResult(namesToFilter,
                includedNames, excludedNames, ignoredNames);
        return result;
    }
    
    /**
     * Should not be instantiated
     */
    private PatternMatcher() {
        logAndThrow(new IllegalStateException(Messages
                .getString("invalidConstructor"))); //$NON-NLS-1$
    }
    
}
