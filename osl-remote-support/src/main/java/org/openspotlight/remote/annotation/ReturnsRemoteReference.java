package org.openspotlight.remote.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface ReturnsRemoteReference {
    int timeoutInMinutes() default 5;
}
