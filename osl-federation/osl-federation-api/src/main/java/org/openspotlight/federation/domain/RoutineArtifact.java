package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name( "routine" )
public class RoutineArtifact extends CustomArtifact implements SimpleNodeType, Serializable {
    private String                tableName;
    private String                catalogName;
    private RoutineType           type;
    private String                schemaName;

    private Set<RoutineParameter> parameters = new HashSet<RoutineParameter>();

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

    public RoutineType getType() {
        return this.type;
    }

    public void loadProperties() {
        final StringTokenizer tok = new StringTokenizer(this.getArtifactCompleteName(), "/"); //$NON-NLS-1$
        if (tok.countTokens() == 4) {
            this.setSchemaName(tok.nextToken());
            final String type = tok.nextToken();
            this.setType(RoutineType.valueOf(type));
            this.setCatalogName(tok.nextToken());
            this.setTableName(tok.nextToken());
        } else {
            this.setSchemaName(tok.nextToken());
            final String type = tok.nextToken();
            this.setType(RoutineType.valueOf(type));

            this.setTableName(tok.nextToken());
        }
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

    public void setType( final RoutineType type ) {
        this.type = type;
    }

}
