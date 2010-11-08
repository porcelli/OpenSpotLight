package org.openspotlight.federation.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 6, 2010 Time: 10:56:00 AM To change this template use File | Settings | File
 * Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface XmlConfigFileLocation {}
