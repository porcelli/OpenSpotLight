package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

@Name( "routine_parameter" )
public class RoutineParameter implements SimpleNodeType, Serializable {
    private String               name;
    private ColumnType           type;
    private NullableSqlType      nullable;
    private int                  columnSize;
    private int                  decimalSize;
    private RoutineArtifact      routine;
    private RoutineParameterType parameterType;

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

    public RoutineParameterType getParameterType() {
        return this.parameterType;
    }

    @ParentProperty
    public RoutineArtifact getRoutine() {
        return this.routine;
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

    public void setParameterType( final RoutineParameterType parameterType ) {
        this.parameterType = parameterType;
    }

    public void setRoutine( final RoutineArtifact routine ) {
        this.routine = routine;
    }

    public void setType( final ColumnType type ) {
        this.type = type;
    }
}
