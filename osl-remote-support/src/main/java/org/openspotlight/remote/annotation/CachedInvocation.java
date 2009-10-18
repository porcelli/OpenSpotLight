package org.openspotlight.remote.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface CachedInvocation {
    long timeoutInMilliseconds() default 0;
}
