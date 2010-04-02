package org.openspotlight.bundle.language.java.parser.executor;

import org.antlr.runtime.tree.CommonTree;
import org.openspotlight.bundle.common.parser.SLCommonTree;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;

import java.util.List;

public class VariableDeclarationDto {

	private final String name;

	private final List<JavaModifier> modifiers;

	private final JavaType type;

	private final String threeDots;

	private final String arrayDimensions;

	private final SLCommonTree treeElement;

	public VariableDeclarationDto(final String name,
			final List<JavaModifier> modifiers, final JavaType type,
			final String threeDots, final String arrayDimensions,
			final CommonTree treeElement) {
		this.name = name;
		this.modifiers = modifiers;
		this.type = type;
		this.threeDots = threeDots;
		this.arrayDimensions = arrayDimensions;
		this.treeElement = (SLCommonTree) treeElement;
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

	public SLCommonTree getTreeElement() {
		return treeElement;
	}

	public JavaType getType() {
		return type;
	}

}
