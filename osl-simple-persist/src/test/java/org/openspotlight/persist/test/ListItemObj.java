package org.openspotlight.persist.test;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class ListItemObj implements SimpleNodeType {
    private String name;

    private int    value;

    public boolean equals( final Object o ) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ListItemObj)) {
            return false;
        }
        final ListItemObj that = (ListItemObj)o;
        return Equals.eachEquality(Arrays.of(this.value), Arrays.andOf(that.value));
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    @KeyProperty
    public void setValue( final int value ) {
        this.value = value;
    }

}
