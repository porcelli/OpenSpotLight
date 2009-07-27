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

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.load.db.BasicDatabaseMetadataLoader;
import org.openspotlight.federation.data.load.db.DatabaseMetadataLoader;
import org.openspotlight.federation.data.load.db.DatabaseMetadataLoader.ScriptDescription;

/**
 * Artifact loader that loads Stream artifacts from databases.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class DatabaseArtifactLoader extends AbstractArtifactLoader {
    
    /**
     * Return all scripts from the database, and fills a cache for later use.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Set<String> getAllArtifactNames(final Bundle bundle,
            final ArtifactMapping mapping,
            final Map<String, Object> cachedInformation)
            throws ConfigurationException {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        if (!(bundle instanceof DbBundle)) {
            return emptySet();
        }
        if (cachedInformation.size() == 0) {
            Connection connection = null;
            try {
                final DbBundle dbBundle = (DbBundle) bundle;
                Class.forName(dbBundle.getDriverClass());
                if (dbBundle.getUser() == null) {
                    connection = DriverManager.getConnection(dbBundle
                            .getInitialLookup());
                } else {
                    connection = DriverManager.getConnection(dbBundle
                            .getInitialLookup(), dbBundle.getUser(), dbBundle
                            .getPassword());
                }
                
                final DatabaseMetadataLoader loader = new BasicDatabaseMetadataLoader(
                        dbBundle.getType(), connection);
                final ScriptDescription[] allTypes = loader.loadAllTypes();
                for (final ArtifactMapping innerMapping : bundle
                        .getArtifactMappings()) {
                    final Map<String, ScriptDescription> artifactMappingInformation = new HashMap<String, ScriptDescription>();
                    cachedInformation.put(innerMapping.getRelative(),
                            artifactMappingInformation);
                    for (final ScriptDescription desc : allTypes) {
                        artifactMappingInformation.put(removeBegginingFrom(
                                innerMapping.getRelative(), desc.toString()),
                                desc);
                    }
                }
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            } finally {
                if (connection != null) {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (final Exception e2) {
                        catchAndLog(e2);
                    }
                }
            }
        }
        final Map<String, ScriptDescription> artifactMappingInformation = (Map<String, ScriptDescription>) cachedInformation
                .get(mapping.getRelative());
        if (artifactMappingInformation != null) {
            return artifactMappingInformation.keySet();
        }
        return emptySet();
        
    }
    
    /**
     * loads the content of a named script using the cache filled before
     */
    @SuppressWarnings("unchecked")
    @Override
    protected byte[] loadArtifact(final Bundle bundle,
            final ArtifactMapping mapping, final String artifactName,
            final Map<String, Object> cachedInformation) throws Exception {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        checkNotNull("mapping", mapping); //$NON-NLS-1$
        checkNotEmpty("artifactName", artifactName); //$NON-NLS-1$
        if (!(bundle instanceof DbBundle)) {
            return new byte[0];
        }
        final Map<String, ScriptDescription> artifactMappingInformation = (Map<String, ScriptDescription>) cachedInformation
                .get(mapping.getRelative());
        if (artifactMappingInformation != null) {
            final ScriptDescription desc = artifactMappingInformation
                    .get(artifactName);
            return desc.getSql().getBytes();
        }
        return new byte[0];
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected int numberOfParallelThreads() {
        return 1; // just a few selects ;-)
    }
    
}
