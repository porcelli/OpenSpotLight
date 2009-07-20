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

package org.openspotlight.federation.data.load;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Compare.compareAll;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.Bundle;

/**
 * Artifact loader is the interface witch abstract the Artifact loading stuff,
 * such as reading a Artifact from its source and adding it to the configuration
 * meta data.
 * 
 * Each Artifact mapping has an syntax to match some bunch of Artifact using ant
 * style syntax with * and so on. So, the Artifact loader can decide if it
 * should load or not some type of Artifact, and based on mapping it should take
 * decisions if it knows any form to load the Artifacts that match the mapping
 * that it receives.
 * 
 * The Artifact loader has the responsibility to resolve each mapping.
 * 
 * FIXME create refresh method
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface ArtifactLoader {
    
    /**
     * Returns the count for each possible action during a Artifact load, such
     * as load, ignore and error.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public final class ArtifactProcessingCount implements
            Comparable<ArtifactProcessingCount> {
        
        /**
         * Representes a artifact processing witch just loaded one artifact.
         */
        public static final ArtifactProcessingCount ONE_LOADED = new ArtifactProcessingCount(
                1, 0, 0);
        /**
         * Representes a artifact processing witch just ignored one artifact.
         */
        public static final ArtifactProcessingCount ONE_IGNORED = new ArtifactProcessingCount(
                0, 1, 0);
        
        /**
         * Representes a artifact processing witch just got an error loading one
         * artifact.
         */
        public static final ArtifactProcessingCount ONE_ERROR = new ArtifactProcessingCount(
                0, 0, 1);
        
        private final long loadCount;
        
        private final long ignoreCount;
        private final long errorCount;
        private final int hashCode;
        private final String description;
        
        /**
         * Creates an processing data based on load counts for artifact loading.
         * 
         * @param loadCount
         * @param ignoreCount
         * @param errorCount
         */
        @SuppressWarnings("boxing")
        public ArtifactProcessingCount(final long loadCount,
                final long ignoreCount, final long errorCount) {
            this.loadCount = loadCount;
            this.ignoreCount = ignoreCount;
            this.errorCount = errorCount;
            this.hashCode = hashOf(loadCount, ignoreCount, errorCount);
            this.description = format(Messages
                    .getString("ArtifactLoader.loadDescription"), //$NON-NLS-1$
                    loadCount, ignoreCount, errorCount);
        }
        
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("boxing")
        public int compareTo(final ArtifactProcessingCount that) {
            return compareAll(of(this.loadCount, this.ignoreCount,
                    this.errorCount), andOf(that.loadCount, that.ignoreCount,
                    that.errorCount));
        }
        
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("boxing")
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ArtifactProcessingCount)) {
                return false;
            }
            final ArtifactProcessingCount that = (ArtifactProcessingCount) o;
            return eachEquality(of(this.loadCount, this.ignoreCount,
                    this.errorCount), andOf(that.loadCount, that.ignoreCount,
                    that.errorCount));
        }
        
        /**
         * @return the error count
         */
        public long getErrorCount() {
            return this.errorCount;
        }
        
        /**
         * @return the loading count
         */
        public long getIgnoreCount() {
            return this.ignoreCount;
        }
        
        /**
         * @return the loading count
         */
        public long getLoadCount() {
            return this.loadCount;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.description;
        }
    }
    
    /**
     * Loads all the Artifacts contained from each ArtifactMapping.
     * 
     * @param bundle
     * @return the loading count
     * @throws ConfigurationException
     */
    public ArtifactProcessingCount loadArtifactsFromMappings(Bundle bundle)
            throws ConfigurationException;
    
}
