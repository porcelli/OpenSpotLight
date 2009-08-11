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

import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Column;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.TableArtifact;
import org.openspotlight.federation.data.load.db.BasicDatabaseMetadataLoader;
import org.openspotlight.federation.data.load.db.DatabaseMetadataLoader;
import org.openspotlight.federation.data.load.db.DatabaseMetadataLoader.ColumnDescription;
import org.openspotlight.federation.data.load.db.DatabaseMetadataLoader.ScriptDescription;
import org.openspotlight.federation.data.load.db.DatabaseMetadataLoader.TableDescription;

/**
 * Artifact loader that loads Stream artifacts from databases.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class DatabaseArtifactLoader extends AbstractArtifactLoader {
    
    /**
     * Cached information for databases. As is needed to retrieve names from
     * each
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    protected static class CachedInformation implements GlobalExecutionContext {
        private final Map<String, DetailedInformation> detailedInformationMap = new ConcurrentHashMap<String, DetailedInformation>();
        private final Set<String> artifactNames = new CopyOnWriteArraySet<String>();
        
        public Set<String> getArtifactNames() {
            return this.artifactNames;
        }
        
        public DetailedInformation getInformationForMapping(
                final String mappingName) {
            DetailedInformation information = this.detailedInformationMap
                    .get(mappingName);
            if (information == null) {
                information = new DetailedInformation();
                this.detailedInformationMap.put(mappingName, information);
            }
            return information;
            
        }
        
        public void globalExecutionAboutToStart(final Bundle bundle) {
            // nothing to do here
            
        }
        
        public void globalExecutionFinished(final Bundle bundle) {
            // nothing to do here
            
        }
        
    }
    
    protected static class DetailedInformation {
        private final Map<String, TableDescription> tables = new ConcurrentHashMap<String, TableDescription>();
        private final Map<String, ScriptDescription> scripts = new ConcurrentHashMap<String, ScriptDescription>();
        
        public Map<String, ScriptDescription> getScripts() {
            return this.scripts;
        }
        
        public Map<String, TableDescription> getTables() {
            return this.tables;
        }
        
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected GlobalExecutionContext createGlobalExecutionContext() {
        return new CachedInformation();
    }
    
    /**
     * Return all scripts from the database, and fills a cache for later use.
     */
    @Override
    public Set<String> getAllArtifactNames(final Bundle bundle,
            final ArtifactMapping mapping, final GlobalExecutionContext context)
            throws ConfigurationException {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        if (!(bundle instanceof DbBundle)) {
            return emptySet();
        }
        final CachedInformation cachedInformation = (CachedInformation) context;
        Connection connection = null;
        try {
            final DbBundle dbBundle = (DbBundle) bundle;
            forName(dbBundle.getDriverClass());
            if (dbBundle.getUser() == null) {
                connection = getConnection(dbBundle.getInitialLookup());
            } else {
                connection = getConnection(dbBundle.getInitialLookup(),
                        dbBundle.getUser(), dbBundle.getPassword());
            }
            
            final DatabaseMetadataLoader loader = new BasicDatabaseMetadataLoader(
                    dbBundle.getType(), connection);
            final ScriptDescription[] allTypes = loader.loadAllTypes();
            final TableDescription[] tableMetadata = loader.loadTableMetadata();
            final Map<String, TableDescription> tableMetadataMap = new HashMap<String, TableDescription>(
                    tableMetadata.length);
            for (final TableDescription d : tableMetadata) {
                tableMetadataMap.put(d.toString(), d);
            }
            for (final ArtifactMapping innerMapping : bundle
                    .getArtifactMappings()) {
                final DetailedInformation information = cachedInformation
                        .getInformationForMapping(innerMapping.getRelative());
                
                for (final ScriptDescription desc : allTypes) {
                    if (desc.toString().startsWith(innerMapping.getRelative())) {
                        final String name = removeBegginingFrom(innerMapping
                                .getRelative(), desc.toString());
                        information.getScripts().put(name, desc);
                        cachedInformation.getArtifactNames().add(name);
                        
                    }
                }
                for (final Map.Entry<String, TableDescription> entry : tableMetadataMap
                        .entrySet()) {
                    if (entry.getKey().startsWith(innerMapping.getRelative())) {
                        final String name = removeBegginingFrom(innerMapping
                                .getRelative(), entry.getKey());
                        information.getTables().put(name, entry.getValue());
                        cachedInformation.getArtifactNames().add(name);
                    }
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
        
        return cachedInformation.getArtifactNames();
        
    }
    
    /**
     * loads the content of a named script using the cache filled before
     */
    @Override
    public byte[] loadArtifact(final Bundle bundle,
            final ArtifactMapping mapping, final String artifactName,
            final GlobalExecutionContext globalContext,
            final ThreadExecutionContext localContext) throws Exception {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        checkNotNull("mapping", mapping); //$NON-NLS-1$
        checkNotEmpty("artifactName", artifactName); //$NON-NLS-1$
        if (!(bundle instanceof DbBundle)) {
            return new byte[0];
        }
        final CachedInformation cachedInformation = (CachedInformation) globalContext;
        
        final DetailedInformation information = cachedInformation
                .getInformationForMapping(mapping.getRelative());
        
        final TableDescription tableDescription = information.getTables().get(
                artifactName);
        if (tableDescription != null) {
            this.loadTableArtifact(bundle, mapping, tableDescription);
        }
        final ScriptDescription scriptDescription = information.getScripts()
                .get(artifactName);
        if (scriptDescription == null) {
            return new byte[0];
        } else {
            return scriptDescription.getSql().getBytes();
        }
        
    }
    
    private void loadTableArtifact(final Bundle bundle,
            final ArtifactMapping mapping,
            final TableDescription tableDescription) {
        final String name = removeBegginingFrom(mapping.getRelative(),
                tableDescription.toString());
        TableArtifact table = (TableArtifact) bundle
                .getCustomArtifactByName(name);
        if (table == null) {
            table = new TableArtifact(bundle, tableDescription.toString());
        }
        for (final String columnName : table.getColumnNames()) {
            if (!tableDescription.getColumns().containsKey(columnName)) {
                final Column column = table.getColumnByName(columnName);
                table.removeColumn(column);
            }
        }
        
        for (final ColumnDescription colDesc : tableDescription.getColumns()
                .values()) {
            Column column = table.getColumnByName(colDesc.getColumnName());
            if (column == null) {
                column = new Column(table, colDesc.getColumnName());
            }
            column.setColumnSize(colDesc.getColumnSize());
            column.setDecimalSize(colDesc.getDecimalSize());
            column.setNullable(colDesc.getNullable());
            column.setType(colDesc.getType());
        }
    }
}
