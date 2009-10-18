package org.openspotlight.remote.annotation;

public @interface ReturnsRemoteReference {
    int timeoutInMinutes() default 5;
}
