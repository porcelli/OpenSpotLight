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

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Files.listFileNamesFrom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;

/**
 * Artifact loader that loads Artifact for file system.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class FileSystemArtifactLoader extends AbstractArtifactLoader {
    
    /**
     * Return all files from bundle.initialLookup directory.
     */
    @Override
    protected Set<String> getAllArtifactNames(final Bundle bundle,
            final ArtifactMapping mapping) throws ConfigurationException {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        try {
            final String basePath = bundle.getInitialLookup()
                    + mapping.getRelative();
            final Set<String> filesFromThisMapping = listFileNamesFrom(basePath);
            return filesFromThisMapping;
        } catch (final SLException e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
    /**
     * loads the content of a file found on bundle.initialLookup + artifactName
     */
    @Override
    protected byte[] loadArtifact(final Bundle bundle,
            final ArtifactMapping mapping, final String artifactName)
            throws Exception {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        checkNotEmpty("artifactName", artifactName); //$NON-NLS-1$
        final String fileName = bundle.getInitialLookup()
                + mapping.getRelative() + artifactName;
        final File file = new File(fileName);
        checkCondition("fileExists", file.exists()); //$NON-NLS-1$
        final FileInputStream fis = new FileInputStream(file);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (fis.available() > 0) {
            baos.write(fis.read());
        }
        final byte[] content = baos.toByteArray();
        fis.close();
        return content;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected int numberOfParallelThreads() {
        return 4; // not so much because of the hard drive io
    }
    
}
