package org.openspotlight.graph.client.internal;

public @interface CachedInvocation {
    long timeoutInMilliseconds() default 0;
}
