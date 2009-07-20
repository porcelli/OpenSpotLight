package org.openspotlight.graph.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SLLinkAttribute {
	public static final int ALLOWS_CHANGE_TO_BIDIRECTIONAL = 1;
	public static final int ALLOWS_MULTIPLE = 2;
	int[] value();
}

