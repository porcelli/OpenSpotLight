package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class LevelThreeObj implements SimpleNodeType {
    private String      key;

    private String      property;

    private LevelTwoObj parentObj;

    @KeyProperty
    public String getKey() {
        return this.key;
    }

    @ParentProperty
    public LevelTwoObj getLevelTwoObj() {
        return this.parentObj;
    }

    public String getProperty() {
        return this.property;
    }

    public void setKey( final String key ) {
        this.key = key;
    }

    public void setLevelTwoObj( final LevelTwoObj parentObj ) {
        this.parentObj = parentObj;
    }

    public void setProperty( final String property ) {
        this.property = property;
    }
}
