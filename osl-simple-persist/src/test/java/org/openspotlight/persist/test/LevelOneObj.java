package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;

public class LevelOneObj {

    private String  key;

    private String  property;

    private RootObj rootObj;

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
