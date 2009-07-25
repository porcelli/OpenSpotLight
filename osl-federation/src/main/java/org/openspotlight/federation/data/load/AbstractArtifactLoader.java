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

import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Excluded;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.StreamArtifact;

/**
 * The AbstractArtifactLoader class is itself a {@link ArtifactLoader} that do
 * the common stuff such as filtering artifacts before processing them or
 * creating the sha-1 key for the content.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public abstract class AbstractArtifactLoader implements ArtifactLoader {
    
    /**
     * The implementation class needs to load all the possible artifact names
     * without filtering this.
     * 
     * @param bundle
     * @return
     * @throws ConfigurationException
     */
    protected abstract Set<String> getAllArtifactNames(Bundle bundle)
            throws ConfigurationException;
    
    /**
     * This method loads an artifact using its names
     * 
     * @param bundle
     * @param artifactName
     * @return
     * @throws Exception
     */
    protected abstract byte[] loadArtifact(Bundle bundle, String artifactName)
            throws Exception;
    
    /**
     * Filter the included and excluded patterns and also creates each artifact
     * and calculates the sha-1 key for the content.
     * 
     * @param bundle
     * @return a {@link ArtifactLoader.ArtifactProcessingCount} with statistical
     *         data
     * @throws ConfigurationException
     */
    public ArtifactProcessingCount loadArtifactsFromMappings(final Bundle bundle)
            throws ConfigurationException {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        int loadCount = 0;
        int errorCount = 0;
        final Set<String> includedPatterns = new HashSet<String>();
        final Set<String> excludedPatterns = new HashSet<String>();
        for (final ArtifactMapping mapping : bundle.getArtifactMappings()) {
            for (final Included included : mapping.getIncludeds()) {
                includedPatterns.add(included.getName());
            }
            for (final Excluded excluded : mapping.getExcludeds()) {
                excludedPatterns.add(excluded.getName());
            }
        }
        final Set<String> namesToFilter = this.getAllArtifactNames(bundle);
        final FilterResult result = filterNamesByPattern(namesToFilter,
                includedPatterns, excludedPatterns, false);
        final Set<String> namesToProcess = result.getIncludedNames();
        for (final String artifactName : namesToProcess) {
            try {
                final byte[] content = this.loadArtifact(bundle, artifactName);
                final String sha1 = getSha1SignatureEncodedAsBase64(content);
                final InputStream is = new ByteArrayInputStream(content);
                final StreamArtifact artifact = bundle
                        .addStreamArtifact(artifactName);
                artifact.setData(is);
                artifact.setDataSha1(sha1);
                loadCount++;
            } catch (final Exception e) {
                errorCount++;
            }
        }
        final int ignoreCount = result.getIgnoredNames().size();
        return new ArtifactProcessingCount(loadCount, ignoreCount, errorCount);
    }
}
