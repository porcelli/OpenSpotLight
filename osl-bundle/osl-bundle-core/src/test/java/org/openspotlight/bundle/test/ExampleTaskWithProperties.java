package org.openspotlight.bundle.test;

import org.openspotlight.bundle.annotation.PropertyDef;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Oct 4, 2010
 * Time: 3:44:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExampleTaskWithProperties {

    @PropertyDef(mandatory = false,name = "Stuff")
    private boolean booleanProperty;
}
