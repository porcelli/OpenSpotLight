package org.openspotlight.bundle.language.java.parser.executor;

public enum JavaModifier {
	PUBLIC, PROTECTED, PRIVATE, STATIC, ABSTRACT, FINAL, NATIVE, SYNCHRONIZED, TRANSIENT, VOLATILE, STRICTFP;
	public static JavaModifier getByName(final String name) {
		return name != null ? JavaModifier.valueOf(name.toUpperCase()) : null;
	}

}
