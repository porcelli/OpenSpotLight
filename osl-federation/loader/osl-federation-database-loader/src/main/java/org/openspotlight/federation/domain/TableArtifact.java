package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name( "table" )
public class TableArtifact extends CustomArtifact implements SimpleNodeType, Serializable {
    private String      tableName;
    private String      catalogName;
    private String      schemaName;
    //    private Status      status;
    /* FIXME maybe implement this
        private void loadProperties() {
            final StringTokenizer tok = new StringTokenizer(this.getRelativeName(), "/"); //$NON-NLS-1$
            if (tok.countTokens() == 4) {
                this.instanceMetadata.setProperty(SCHEMA_NAME, tok.nextToken());
                this.instanceMetadata.setProperty(CATALOG_NAME, tok.nextToken());
                tok.nextToken();// puts away its table type
                this.instanceMetadata.setProperty(TABLE_NAME, tok.nextToken());
            } else {

                this.instanceMetadata.setProperty(SCHEMA_NAME, tok.nextToken());
                tok.nextToken();// puts away its table type
                this.instanceMetadata.setProperty(TABLE_NAME, tok.nextToken());
            }
        }

     */
    private Set<Column> columns;

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
