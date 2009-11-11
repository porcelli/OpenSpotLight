package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name( "routine" )
public class RoutineArtifact extends CustomArtifact implements SimpleNodeType, Serializable {
    private String                tableName;
    private String                catalogName;

    private String                schemaName;

    //    private Status      status;
    /* FIXME maybe implement this
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

     */
    private Set<RoutineParameter> parameters;

    @Override
    public boolean contentEquals( final Artifact other ) {
        if (!(other instanceof RoutineArtifact)) {
            return false;
        }
        final RoutineArtifact that = (RoutineArtifact)other;
        return Equals.eachEquality(this.getParameters(), that.getParameters());
    }

    public String getCatalogName() {
        return this.catalogName;
    }

    public Set<RoutineParameter> getParameters() {
        return this.parameters;
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setCatalogName( final String catalogName ) {
        this.catalogName = catalogName;
    }

    public void setParameters( final Set<RoutineParameter> parameters ) {
        this.parameters = parameters;
    }

    public void setSchemaName( final String schemaName ) {
        this.schemaName = schemaName;
    }

    public void setTableName( final String tableName ) {
        this.tableName = tableName;
    }

}
