package org.openspotlight.storage;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created by User: feu - Date: Mar 25, 2010 - Time: 2:42:01 PM
 */
@BindingAnnotation
@Target({ PARAMETER}) @Retention(RUNTIME)
public @interface DefaultPartition {
    

}
