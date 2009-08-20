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
import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Artifact loader that loads Artifact for file system.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class FileSystemArtifactLoader extends AbstractArtifactLoader {
    
    static final Logger logger = LoggerFactory.getLogger(FileSystemArtifactLoader.class);
    
    
    
    
    private static class FileGlobalExecutionContext extends DefaultGlobalExecutionContext{

    	/**
         * Return all files from bundle.initialLookup directory.
         */
        public Set<String> getAllArtifactNames(final Bundle bundle,
                final ArtifactMapping mapping)
                throws ConfigurationException {
            checkNotNull("bundle", bundle); //$NON-NLS-1$
            try {
                final String basePath = bundle.getInitialLookup()
                        + mapping.getRelative();
                boolean exists = false;
                try {
                    exists = new File(basePath).exists();
                } catch (final Exception e) {
                    exists = false;
                    catchAndLog(
                            format(
                                    Messages
                                            .getString("FileSystemArtifactLoader.ignoring"), basePath), e); //$NON-NLS-1$
                }
                if (!exists) {
                    logger.info(format(Messages
                            .getString("FileSystemArtifactLoader.ignoring"), //$NON-NLS-1$
                            basePath));
                    return emptySet();
                }
                final Set<String> filesFromThisMapping = listFileNamesFrom(basePath);
                return filesFromThisMapping;
            } catch (final SLException e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }
    	
    }
    
    private static class FileThreadExecutionContext extends DefaultThreadExecutionContext{

    	/**
         * loads the content of a file found on bundle.initialLookup + artifactName
         */
        public byte[] loadArtifactOrReturnNullToIgnore(final Bundle bundle,
                final ArtifactMapping mapping, final String artifactName,
                final GlobalExecutionContext globalContext) throws Exception {
            checkNotNull("bundle", bundle); //$NON-NLS-1$
            checkNotNull("mapping", mapping); //$NON-NLS-1$
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

    	
    }
    
	@SuppressWarnings("synthetic-access")
	@Override
	protected GlobalExecutionContext createGlobalExecutionContext() {
		return new FileGlobalExecutionContext();
	}

	@SuppressWarnings("synthetic-access")
	@Override
	protected ThreadExecutionContext createThreadExecutionContext() {
		return new FileThreadExecutionContext();
	}
    
}
