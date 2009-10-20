package org.openspotlight.remote.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation describes a method with will throw an {@link UnsupportedOperationException} instead of connect to the server.
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface UnsupportedRemoteMethod {

}
