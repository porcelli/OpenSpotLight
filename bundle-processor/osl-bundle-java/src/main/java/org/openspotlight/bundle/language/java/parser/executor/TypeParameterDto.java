package org.openspotlight.bundle.language.java.parser.executor;

import org.openspotlight.bundle.language.java.metamodel.node.JavaType;

import java.util.List;

public class TypeParameterDto {
	private final String name;
	private final List<JavaType> typeParameterExtends;

	public TypeParameterDto(final String string,
			final List<JavaType> typeParameters) {
		name = string;
		typeParameterExtends = typeParameters;
	}

	public String getName() {
		return name;
	}

	public List<JavaType> getTypeParameterExtends() {
		return typeParameterExtends;
	}

}
