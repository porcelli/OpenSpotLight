package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name( "column" )
public class Column implements SimpleNodeType, Serializable {
    private String          name;
    private ColumnType      type;
    private NullableSqlType nullable;
    private int             columnSize;
    private int             decimalSize;
    private TableArtifact   table;

    public int getColumnSize() {
        return this.columnSize;
    }

    public int getDecimalSize() {
        return this.decimalSize;
    }

    @KeyProperty
    public String getName() {
        return this.name;
    }

    public NullableSqlType getNullable() {
        return this.nullable;
    }

    @ParentProperty
    public TableArtifact getTable() {
        return this.table;
    }

    public ColumnType getType() {
        return this.type;
    }

    public void setColumnSize( final int columnSize ) {
        this.columnSize = columnSize;
    }

    public void setDecimalSize( final int decimalSize ) {
        this.decimalSize = decimalSize;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setNullable( final NullableSqlType nullable ) {
        this.nullable = nullable;
    }

    public void setTable( final TableArtifact table ) {
        this.table = table;
    }

    public void setType( final ColumnType type ) {
        this.type = type;
    }
}
