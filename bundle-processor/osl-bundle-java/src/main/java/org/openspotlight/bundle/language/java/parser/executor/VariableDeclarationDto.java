package org.openspotlight.bundle.language.java.parser.executor;

import java.util.List;

import org.openspotlight.bundle.language.java.metamodel.node.JavaType;

public class VariableDeclarationDto {

	private final String name;

	private final List<JavaModifier> modifiers;

	private final JavaType type;

	private final String threeDots;

	private final String arrayDimensions;

	public VariableDeclarationDto(final String name,
			final List<JavaModifier> modifiers, final JavaType type,
			final String threeDots, final String arrayDimensions) {
		this.name = name;
		this.modifiers = modifiers;
		this.type = type;
		this.threeDots = threeDots;
		this.arrayDimensions = arrayDimensions;
	}

	public String getArrayDimensions() {
		return arrayDimensions;
	}

	public List<JavaModifier> getModifiers() {
		return modifiers;
	}

	public String getName() {
		return name;
	}

	public String getThreeDots() {
		return threeDots;
	}

	public JavaType getType() {
		return type;
	}

}
