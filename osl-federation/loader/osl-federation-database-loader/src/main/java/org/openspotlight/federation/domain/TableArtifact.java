package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name( "table" )
public class TableArtifact extends CustomArtifact implements SimpleNodeType, Serializable {
    private String      tableName;
    private String      catalogName;
    private String      schemaName;
    private Set<Column> columns = new HashSet<Column>();

    @Override
    public boolean contentEquals( final Artifact other ) {
        if (!(other instanceof TableArtifact)) {
            return false;
        }
        final TableArtifact that = (TableArtifact)other;
        return Equals.eachEquality(this.getColumns(), that.getColumns());
    }

    public String getCatalogName() {
        return this.catalogName;
    }

    public Set<Column> getColumns() {
        return this.columns;
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void loadProperties() {
        final StringTokenizer tok = new StringTokenizer(this.getArtifactCompleteName(), "/"); //$NON-NLS-1$
        if (tok.countTokens() == 4) {
            this.setSchemaName(tok.nextToken());
            this.setCatalogName(tok.nextToken());
            tok.nextToken();// puts away its table type
            this.setTableName(tok.nextToken());
        } else {

            this.setSchemaName(tok.nextToken());
            tok.nextToken();// puts away its table type
            this.setTableName(tok.nextToken());
        }
    }

    public void setCatalogName( final String catalogName ) {
        this.catalogName = catalogName;
    }

    public void setColumns( final Set<Column> columns ) {
        this.columns = columns;
    }

    public void setSchemaName( final String schemaName ) {
        this.schemaName = schemaName;
    }

    public void setTableName( final String tableName ) {
        this.tableName = tableName;
    }

}
