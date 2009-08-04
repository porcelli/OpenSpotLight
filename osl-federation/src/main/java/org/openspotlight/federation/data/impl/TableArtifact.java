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

package org.openspotlight.federation.data.impl;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * {@link CustomArtifact} associated with table metadata.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
@StaticMetadata(keyPropertyName = "relativeName", keyPropertyType = String.class, validParentTypes = {
        Bundle.class, Project.class }, propertyNames = { "UUID", "version" }, propertyTypes = {
        String.class, String.class }, validChildrenTypes = Column.class)
public class TableArtifact extends CustomArtifact {
    /**
     * 
     */
    private static final long serialVersionUID = 7433764721674625809L;
    private static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
    private static final String CATALOG_NAME = "catalogName"; //$NON-NLS-1$
    private static final String SCHEMA_NAME = "schemaName"; //$NON-NLS-1$
    
    private final InstanceMetadata instanceMetadata;
    
    /**
     * Creates a table artifact inside a bundle. The relative name should be in
     * the format CATALOG_NAME/SCHEMA_NAME/TABLE/TABLE_NAME. Note that the
     * /TABLE/ is a constant determining the artifact type.
     * 
     * @param bundle
     * @param relativeName
     */
    public TableArtifact(final Bundle bundle, final String relativeName) {
        super(bundle, relativeName);
        this.instanceMetadata = super.getInstanceMetadata();
        this.loadProperties();
    }
    
    /**
     * Creates a table artifact inside a project. The relative name should be in
     * the format CATALOG_NAME/SCHEMA_NAME/TABLE/TABLE_NAME. Note that the
     * /TABLE/ is a constant determining the artifact type.
     * 
     * @param project
     * @param relativeName
     */
    public TableArtifact(final Project project, final String relativeName) {
        super(project, relativeName);
        this.instanceMetadata = super.getInstanceMetadata();
        this.loadProperties();
    }
    
    /**
     * 
     * @return the catalog name
     */
    public String getCatalogName() {
        return this.instanceMetadata.getProperty(CATALOG_NAME);
    }
    
    /**
     * Returns a column by its name.
     * 
     * @param name
     * @return an column
     */
    public final Column getColumnByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Column.class, name);
    }
    
    /**
     * 
     * @return all column names
     */
    @SuppressWarnings("unchecked")
    public final Set<String> getColumnNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(Column.class);
    }
    
    /**
     * 
     * @return all columns
     */
    public final Collection<Column> getColumns() {
        return this.instanceMetadata.getChildrensOfType(Column.class);
    }
    
    /**
     * 
     * @return the schema name
     */
    public String getSchemaName() {
        return this.instanceMetadata.getProperty(SCHEMA_NAME);
    }
    
    /**
     * 
     * @return the table name
     */
    public String getTableName() {
        return this.instanceMetadata.getProperty(TABLE_NAME);
    }
    
    /**
     * Loads the property based on the relative name.
     */
    private void loadProperties() {
        final StringTokenizer tok = new StringTokenizer(this.getRelativeName(),
                "/"); //$NON-NLS-1$
        this.instanceMetadata.setProperty(CATALOG_NAME, tok.nextToken());
        this.instanceMetadata.setProperty(SCHEMA_NAME, tok.nextToken());
        tok.nextToken();// puts away its table type
        this.instanceMetadata.setProperty(TABLE_NAME, tok.nextToken());
    }
    
    /**
     * removes a given column from this repository.
     * 
     * @param column
     */
    public final void removeColumn(final Column column) {
        this.instanceMetadata.removeChild(column);
    }
    
}
