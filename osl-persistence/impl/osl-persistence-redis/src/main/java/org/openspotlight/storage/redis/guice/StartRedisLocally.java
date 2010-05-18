package org.openspotlight.storage.redis.guice;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by User: feu - Date: May 18, 2010 - Time: 4:07:35 PM
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
@BindingAnnotation
public @interface StartRedisLocally {
}
