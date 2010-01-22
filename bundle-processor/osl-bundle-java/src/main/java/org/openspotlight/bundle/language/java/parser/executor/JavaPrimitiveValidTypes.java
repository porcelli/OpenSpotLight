package org.openspotlight.bundle.language.java.parser.executor;

import org.openspotlight.common.util.Assertions;

public enum JavaPrimitiveValidTypes {
	BOOLEAN, CHAR, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE;
	public static boolean isPrimitive(final String name) {
		Assertions.checkNotEmpty("name", name);
		try {
			return valueOf(name.toUpperCase()) != null;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}
}
