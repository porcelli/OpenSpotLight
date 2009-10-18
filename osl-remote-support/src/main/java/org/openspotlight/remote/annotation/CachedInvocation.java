package org.openspotlight.remote.annotation;

public @interface CachedInvocation {
    long timeoutInMilliseconds() default 0;
}
