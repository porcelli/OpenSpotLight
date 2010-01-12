package org.openspotlight.bundle.language.java.resolver;

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Exceptions;

public enum JavaLiteralType {
	BOOLEAN("boolean", "(true|false)"),

	CHAR("char", "^['](.*)[']$"),

	INT("int", "[+-]?((0x[01234567890ABCDEF]+)|\\d+)"),

	LONG("long", "[+-]?((0x[01234567890ABCDEF]+)|\\d+)[lL]"),

	FLOAT("float", "[+-]?(\\d+)?([.])?(\\d+)?((e|E)\\d+)?[fF]"),

	DOUBLE(
			"double",
			"[+-]?(((\\d+)?([.])?(\\d+)?((e|E)\\d+)?[dD])|((\\d+)?[.](\\d+)?((e|E)\\d+)?)|((\\d+)?[.]?(\\d+)?(e|E)(\\d+)?))"),

	STRING("java.lang.String", "^[\"](.*)[\"]$");

	public static JavaLiteralType findLiteralType(final String literal) {
		final Set<JavaLiteralType> matched = new HashSet<JavaLiteralType>();
		for (final JavaLiteralType type : JavaLiteralType.values()) {
			if (type.matches(literal)) {
				matched.add(type);
			}
		}
		if (matched.size() > 1) {
			throw Exceptions.logAndReturn(new IllegalStateException("matched "
					+ matched + " types for literal " + literal));
		}
		if (matched.size() == 0) {
			return null;
		}
		return matched.iterator().next();
	}

	private final String regex;

	private final String name;

	private JavaLiteralType(final String name, final String regex) {
		this.name = name;
		this.regex = regex;
	}

	public boolean matches(final String literal) {
		return literal.matches(regex);
	}

	public String toString() {
		return name;
	}
}
