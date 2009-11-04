package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class PropertyObj implements SimpleNodeType {
    private String name;

    private int    value;

    public String getName() {
        return this.name;
    }

    @KeyProperty
    public int getValue() {
        return this.value;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setValue( final int value ) {
        this.value = value;
    }
}
