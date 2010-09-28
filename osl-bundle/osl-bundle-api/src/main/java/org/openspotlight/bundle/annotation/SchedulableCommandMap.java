package org.openspotlight.bundle.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openspotlight.bundle.context.ExecutionContext;

import com.google.inject.BindingAnnotation;

/**
 * This annotation is used to inject the schedulable commands supported by a
 * given {@link ExecutionContext}
 * 
 * @author feu
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@BindingAnnotation
public @interface SchedulableCommandMap {

}
