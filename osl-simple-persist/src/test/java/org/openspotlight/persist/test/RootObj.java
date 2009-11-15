package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.SimpleNodeType;

public class RootObj implements SimpleNodeType {

    public boolean equals( final Object o ) {
        return o instanceof RootObj;
    }

}
