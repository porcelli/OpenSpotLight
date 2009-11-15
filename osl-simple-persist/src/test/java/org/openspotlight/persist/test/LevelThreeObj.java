package org.openspotlight.persist.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class LevelThreeObj implements SimpleNodeType {
    private String                    key;

    private String                    property;

    private LevelTwoObj               parentObj;

    private Map<Double, Integer>      numberMap   = new HashMap<Double, Integer>();

    private List<Boolean>             booleanList = new ArrayList<Boolean>();

    private List<ListItemObj>         objList     = new ArrayList<ListItemObj>();

    private Map<Integer, MapValueObj> objMap      = new HashMap<Integer, MapValueObj>();

    public List<Boolean> getBooleanList() {
        return this.booleanList;
    }

    @KeyProperty
    public String getKey() {
        return this.key;
    }

    @ParentProperty
    public LevelTwoObj getLevelTwoObj() {
        return this.parentObj;
    }

    public Map<Double, Integer> getNumberMap() {
        return this.numberMap;
    }

    public List<ListItemObj> getObjList() {
        return this.objList;
    }

    public Map<Integer, MapValueObj> getObjMap() {
        return this.objMap;
    }

    public String getProperty() {
        return this.property;
    }

    public void setBooleanList( final List<Boolean> booleanList ) {
        this.booleanList = booleanList;
    }

    public void setKey( final String key ) {
        this.key = key;
    }

    public void setLevelTwoObj( final LevelTwoObj parentObj ) {
        this.parentObj = parentObj;
    }

    public void setNumberMap( final Map<Double, Integer> numberMap ) {
        this.numberMap = numberMap;
    }

    public void setObjList( final List<ListItemObj> objList ) {
        this.objList = objList;
    }

    public void setObjMap( final Map<Integer, MapValueObj> objMap ) {
        this.objMap = objMap;
    }

    public void setProperty( final String property ) {
        this.property = property;
    }
}
