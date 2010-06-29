package org.openspotlight.graph.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by User: feu - Date: Jun 29, 2010 - Time: 2:36:47 PM
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.TYPE} )
public @interface SLInitialWeight {
    int value();
}
