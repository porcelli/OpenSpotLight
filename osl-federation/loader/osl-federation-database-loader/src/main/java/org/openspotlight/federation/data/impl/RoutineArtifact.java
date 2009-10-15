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

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.impl.Artifact.Status;

/**
 * {@link CustomArtifact} associated with table metadata.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@ThreadSafe
@StaticMetadata( keyPropertyName = "relativeName", keyPropertyType = String.class, validParentTypes = {Bundle.class, Group.class}, propertyNames = {
    "UUID", "version", "routineName", "catalogName", "schemaName", "type", "status"}, propertyTypes = {String.class,
    String.class, String.class, String.class, String.class, RoutineArtifact.RoutineType.class, Status.class}, validChildrenTypes = RoutineParameter.class )
public class RoutineArtifact extends CustomArtifact {

    /**
     * Enum to describe routine types. Its int values was taken from {@link DatabaseMetaData}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    @SuppressWarnings( "boxing" )
    public static enum RoutineType {
        /**
         * Type to describe a routine without return values from the routine itself. But it should have multiple out parameters.
         */
        PROCEDURE(1),
        /**
         * Type to descibe a routine with return value, but without out parameters.
         */
        FUNCTION(2),
        /**
         * Type to describe a routine on a situation that wasn't possible to retrieve the routine type.
         */
        DONT_KNOW(0);

        /**
         * Internal cache
         */
        private static final Map<Integer, RoutineType> cache = new HashMap<Integer, RoutineType>();
        static {
            for (final RoutineType n : values()) {
                cache.put(n.getSqlTypeValue(), n);
            }
        }

        /**
         * Static factory method
         * 
         * @param sqlType
         * @return the correct RoutineParameter type by sql int constant
         */
        public static RoutineType getTypeByInt( final int sqlType ) {
            return cache.get(sqlType);
        }

        private final int sqlTypeValue;

        private RoutineType(
                             final int sqlTypeValue ) {
            this.sqlTypeValue = sqlTypeValue;
        }

        /**
         * @return the int value equivalent to {@link Types} constants
         */
        public int getSqlTypeValue() {
            return this.sqlTypeValue;
        }
    }

    /**
     * 
     */
    private static final long      serialVersionUID = 7433764721674625809L;
    private static final String    ROUTINE_NAME     = "routineName";       //$NON-NLS-1$
    private static final String    CATALOG_NAME     = "catalogName";       //$NON-NLS-1$
    private static final String    SCHEMA_NAME      = "schemaName";        //$NON-NLS-1$
    private static final String    TYPE             = "type";              //$NON-NLS-1$

    private final InstanceMetadata instanceMetadata;

    /**
     * Creates a table artifact inside a bundle. The relative name should be in the format
     * CATALOG_NAME/SCHEMA_NAME/<PROCEDURE|FUNCTION>/ROUTINE_NAME. Note that the /TABLE/ is a constant determining the artifact
     * type.
     * 
     * @param bundle
     * @param relativeName
     */
    public RoutineArtifact(
                            final Bundle bundle, final String relativeName ) {
        super(bundle, relativeName);
        this.instanceMetadata = super.getInstanceMetadata();
        this.loadProperties();
    }

    /**
     * Creates a table artifact inside a project. The relative name should be in the format
     * CATALOG_NAME/SCHEMA_NAME/<PROCEDURE|FUNCTION>/ROUTINE_NAME. Note that the /TABLE/ is a constant determining the artifact
     * type.
     * 
     * @param project
     * @param relativeName
     */
    public RoutineArtifact(
                            final Group project, final String relativeName ) {
        super(project, relativeName);
        this.instanceMetadata = super.getInstanceMetadata();
        this.loadProperties();
    }

    /**
     * @return the catalog name
     */
    public String getCatalogName() {
        return this.instanceMetadata.getProperty(CATALOG_NAME);
    }

    /**
     * @return the table name
     */
    public String getRoutineName() {
        return this.instanceMetadata.getProperty(ROUTINE_NAME);
    }

    /**
     * Returns a RoutineParameter by its name.
     * 
     * @param name
     * @return an RoutineParameter
     */
    public final RoutineParameter getRoutineParameterByName( final String name ) {
        return this.instanceMetadata.getChildByKeyValue(RoutineParameter.class, name);
    }

    /**
     * @return all RoutineParameter names
     */
    @SuppressWarnings( "unchecked" )
    public final Set<String> getRoutineParameterNames() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(RoutineParameter.class);
    }

    /**
     * @return all RoutineParameters
     */
    public final Collection<RoutineParameter> getRoutineParameters() {
        return this.instanceMetadata.getChildrensOfType(RoutineParameter.class);
    }

    /**
     * @return the schema name
     */
    public String getSchemaName() {
        return this.instanceMetadata.getProperty(SCHEMA_NAME);
    }

    /**
     * @return the routine type
     */
    public final RoutineType getType() {
        return this.instanceMetadata.getProperty(TYPE);
    }

    /**
     * Loads the property based on the relative name.
     */
    private void loadProperties() {
        final StringTokenizer tok = new StringTokenizer(this.getRelativeName(), "/"); //$NON-NLS-1$
        if (tok.countTokens() == 4) {
            this.instanceMetadata.setProperty(SCHEMA_NAME, tok.nextToken());
            final String type = tok.nextToken();
            this.instanceMetadata.setProperty(TYPE, RoutineType.valueOf(type));
            this.instanceMetadata.setProperty(CATALOG_NAME, tok.nextToken());
            this.instanceMetadata.setProperty(ROUTINE_NAME, tok.nextToken());
        } else {
            this.instanceMetadata.setProperty(SCHEMA_NAME, tok.nextToken());
            final String type = tok.nextToken();
            this.instanceMetadata.setProperty(TYPE, RoutineType.valueOf(type));
            this.instanceMetadata.setProperty(ROUTINE_NAME, tok.nextToken());
        }
    }

    /**
     * removes a given RoutineParameter from this repository.
     * 
     * @param routineColumn
     */
    public final void removeRoutineParameter( final RoutineParameter routineColumn ) {
        this.instanceMetadata.removeChild(routineColumn);
    }

}
