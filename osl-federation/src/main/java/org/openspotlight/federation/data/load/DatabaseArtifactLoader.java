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

import org.openspotlight.common.Pair;
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
                final TableDescription[] tableMetadata = loader
                        .loadTableMetadata();
                final Map<String, TableDescription> tableMetadataMap = new HashMap<String, TableDescription>(
                        tableMetadata.length);
                for (final TableDescription d : tableMetadata) {
                    tableMetadataMap.put(d.toString(), d);
                }
                for (final ArtifactMapping innerMapping : bundle
                        .getArtifactMappings()) {
                    final Map<String, Pair<ScriptDescription, TableDescription>> artifactMappingInformation = new HashMap<String, Pair<ScriptDescription, TableDescription>>();
                    cachedInformation.put(innerMapping.getRelative(),
                            artifactMappingInformation);
                    for (final ScriptDescription desc : allTypes) {
                        final TableDescription columnDesc = tableMetadataMap
                                .get(desc.toString());
                        final Pair<ScriptDescription, TableDescription> pair = new Pair<ScriptDescription, TableDescription>(
                                desc, columnDesc);
                        tableMetadataMap.remove(desc.toString());
                        artifactMappingInformation.put(removeBegginingFrom(
                                innerMapping.getRelative(), desc.toString()),
                                pair);
                    }
                    for (final Map.Entry<String, TableDescription> entry : tableMetadataMap
                            .entrySet()) {
                        if (entry.getKey().startsWith(
                                innerMapping.getRelative())) {
                            final Pair<ScriptDescription, TableDescription> pair = new Pair<ScriptDescription, TableDescription>(
                                    null, entry.getValue());
                            artifactMappingInformation.put(
                                    removeBegginingFrom(innerMapping
                                            .getRelative(), entry.getKey()),
                                    pair);
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
        }
        final Map<String, Pair<ScriptDescription, ColumnDescription>> artifactMappingInformation = (Map<String, Pair<ScriptDescription, ColumnDescription>>) cachedInformation
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
        final Map<String, Pair<ScriptDescription, TableDescription>> artifactMappingInformation = (Map<String, Pair<ScriptDescription, TableDescription>>) cachedInformation
                .get(mapping.getRelative());
        if (artifactMappingInformation != null) {
            final Pair<ScriptDescription, TableDescription> pair = artifactMappingInformation
                    .get(artifactName);
            
            if (pair.getK2() != null) {
                final TableDescription desc = pair.getK2();
                final String name = removeBegginingFrom(mapping.getRelative(),
                        desc.toString());
                TableArtifact table = (TableArtifact) bundle
                        .getCustomArtifactByName(name);
                if (table == null) {
                    table = new TableArtifact(bundle, name);
                }
                for (final String columnName : table.getColumnNames()) {
                    if (!desc.getColumns().containsKey(columnName)) {
                        final Column column = table.getColumnByName(columnName);
                        table.removeColumn(column);
                    }
                }
                for (final ColumnDescription colDesc : desc.getColumns()
                        .values()) {
                    Column column = table.getColumnByName(colDesc
                            .getColumnName());
                    if (column == null) {
                        column = new Column(table, colDesc.getColumnName());
                    }
                    column.setColumnSize(colDesc.getColumnSize());
                    column.setDecimalSize(colDesc.getDecimalSize());
                    column.setNullable(colDesc.getNullable());
                    column.setType(colDesc.getType());
                }
            }
            if (pair.getK1() != null) {
                return pair.getK1().getSql().getBytes();
            }
            
        }
        return new byte[0];
        
    }
}
