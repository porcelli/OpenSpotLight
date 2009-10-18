package org.openspotlight.remote.annotation;

public @interface DisposeMethod {
    boolean callOnTimeout() default true;
}
