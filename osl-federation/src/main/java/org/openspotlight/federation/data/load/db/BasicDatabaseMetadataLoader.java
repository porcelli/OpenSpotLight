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

package org.openspotlight.federation.data.load.db;

import static java.util.Arrays.asList;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openspotlight.common.exception.ConfigurationException;

/**
 * Abstract class to be used as a base for database artifact loader
 * implementation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class BasicDatabaseMetadataLoader implements DatabaseMetadataLoader {
    
    private static final ScriptDescription[] EMPTY_DESCRIPTION = new ScriptDescription[0];
    
    private final Map<ScriptType, ScriptDescription[]> sqlMetadataScripts = new EnumMap<ScriptType, ScriptDescription[]>(
            ScriptType.class);
    
    /**
     * Valid database connection.
     */
    private final Connection connection;
    
    private final DatabaseType databaseType;
    
    private final String databaseUrl;
    
    private final DatabaseMetadataScript databaseMetadataScript;
    
    /**
     * Since this is a database data extractor, it needs a database connection.
     * This class won't close the connection. So, The caller class needs to take
     * care of releasing resources after its use.
     * 
     * @param databaseType
     * @param connection
     *            an open and valid connection
     * @throws ConfigurationException
     *             if connection is closed
     * 
     */
    public BasicDatabaseMetadataLoader(final DatabaseType databaseType,
            final Connection connection) throws ConfigurationException {
        try {
            checkNotNull("connection", connection); //$NON-NLS-1$
            checkNotNull("databaseType", databaseType); //$NON-NLS-1$
            checkCondition("connectionOpen", !connection.isClosed()); //$NON-NLS-1$
            this.databaseMetadataScript = DatabaseMetadataScriptManager.INSTANCE
                    .getScriptByType(databaseType);
            checkCondition(
                    "validDatabaseType", this.databaseMetadataScript != null); //$NON-NLS-1$
            this.connection = connection;
            this.databaseType = databaseType;
            this.databaseUrl = connection.getMetaData().getURL();
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
    /**
     * Find a {@link ScriptDescription} array on cache, or load it if load is
     * needed.
     * 
     * @param scriptType
     * @return a scriptType array
     * @throws ConfigurationException
     */
    private ScriptDescription[] findScriptsForType(final ScriptType scriptType)
            throws ConfigurationException {
        assert scriptType != null;
        try {
            ScriptDescription[] descriptionArray;
            synchronized (this.sqlMetadataScripts) {
                descriptionArray = this.sqlMetadataScripts.get(scriptType);
                if (descriptionArray == null) {
                    descriptionArray = this.loadScript(scriptType);
                }
            }
            return descriptionArray;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public ScriptDescription[] loadAllTypes() throws ConfigurationException {
        final List<ScriptDescription> allTypes = new ArrayList<ScriptDescription>();
        allTypes.addAll(asList(this.loadCustomTypes()));
        allTypes.addAll(asList(this.loadIndexScripts()));
        allTypes.addAll(asList(this.loadProcedures()));
        allTypes.addAll(asList(this.loadTableScripts()));
        allTypes.addAll(asList(this.loadTriggers()));
        allTypes.addAll(asList(this.loadViewScripts()));
        
        return allTypes.toArray(new ScriptDescription[allTypes.size()]);
    }
    
    /**
     * {@inheritDoc}
     */
    public ScriptDescription[] loadCustomTypes() throws ConfigurationException {
        final ScriptDescription[] result = this
                .findScriptsForType(ScriptType.CUSTOM);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public ScriptDescription[] loadIndexScripts() throws ConfigurationException {
        final ScriptDescription[] result = this
                .findScriptsForType(ScriptType.INDEX);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public ScriptDescription[] loadProcedures() throws ConfigurationException {
        final ScriptDescription[] result = this
                .findScriptsForType(ScriptType.PROCEDURE);
        return result;
    }
    
    /**
     * Loads the {@link ScriptDescription} for a given type using the connection
     * given.
     * 
     * 
     * @param scriptType
     * @return the {@link ScriptDescription} array
     * @throws SQLException
     */
    private ScriptDescription[] loadScript(final ScriptType scriptType)
            throws SQLException {
        ScriptDescription[] descriptionArray;
        final List<ScriptDescription> descriptions = new ArrayList<ScriptDescription>();
        
        final List<String> scripts = this.databaseMetadataScript
                .findScriptListByType(scriptType);
        if (scripts != null) {
            for (final String script : scripts) {
                final PreparedStatement pstmt = this.connection
                        .prepareStatement(script);
                final ResultSet resultSet = pstmt.executeQuery();
                while (resultSet.next()) {
                    final String catalog = resultSet
                            .getString(ColumnsNamesForMetadataSelect.CATALOG
                                    .name());
                    final String name = resultSet
                            .getString(ColumnsNamesForMetadataSelect.NAME
                                    .name());
                    final String remarks = resultSet
                            .getString(ColumnsNamesForMetadataSelect.REMARKS
                                    .name());
                    final String schema = resultSet
                            .getString(ColumnsNamesForMetadataSelect.SCHEMA
                                    .name());
                    final String sql = resultSet
                            .getString(ColumnsNamesForMetadataSelect.SQL.name());
                    final ScriptDescription description = new ScriptDescription(
                            this.databaseType, this.databaseUrl, schema,
                            catalog, sql, name, scriptType, remarks);
                    descriptions.add(description);
                }
            }
            descriptionArray = descriptions
                    .toArray(new ScriptDescription[descriptions.size()]);
        } else {
            descriptionArray = EMPTY_DESCRIPTION;
        }
        this.sqlMetadataScripts.put(scriptType, descriptionArray);
        return descriptionArray;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void loadTableMetadata() throws ConfigurationException {
        // TASK create the structure for table metadata
    }
    
    /**
     * {@inheritDoc}
     */
    public ScriptDescription[] loadTableScripts() throws ConfigurationException {
        final ScriptDescription[] result = this
                .findScriptsForType(ScriptType.TABLE);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public ScriptDescription[] loadTriggers() throws ConfigurationException {
        final ScriptDescription[] result = this
                .findScriptsForType(ScriptType.TRIGGER);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public ScriptDescription[] loadViewScripts() throws ConfigurationException {
        final ScriptDescription[] result = this
                .findScriptsForType(ScriptType.VIEW);
        return result;
    }
    
}
