package org.openspotlight.persist.test;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class LevelOneObj implements SimpleNodeType {

    private String  key;

    private String  property;

    private RootObj rootObj;

    public boolean equals( final Object o ) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LevelOneObj)) {
            return false;
        }
        final LevelOneObj that = (LevelOneObj)o;
        return Equals.eachEquality(Arrays.of(this.rootObj, this.key), Arrays.andOf(that.rootObj, that.key));
    }

    @KeyProperty
    public String getKey() {
        return this.key;
    }

    public String getProperty() {
        return this.property;
    }

    @ParentProperty
    public RootObj getRootObj() {
        return this.rootObj;
    }

    public void setKey( final String key ) {
        this.key = key;
    }

    public void setProperty( final String property ) {
        this.property = property;
    }

    public void setRootObj( final RootObj rootObj ) {
        this.rootObj = rootObj;
    }

}
