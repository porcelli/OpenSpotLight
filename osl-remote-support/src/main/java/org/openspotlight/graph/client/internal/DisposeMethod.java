package org.openspotlight.graph.client.internal;

public @interface DisposeMethod {
    boolean callOnTimeout() default true;
}
