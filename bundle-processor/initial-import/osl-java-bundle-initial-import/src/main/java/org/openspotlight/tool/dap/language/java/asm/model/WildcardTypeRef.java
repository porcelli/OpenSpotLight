package org.openspotlight.tool.dap.language.java.asm.model;

public class WildcardTypeRef implements TypeRef {

	private boolean isUpperBound = false;
	private TypeRef boundType = null;

	public WildcardTypeRef() {
	}

	public WildcardTypeRef(boolean isUpperBound, TypeRef boundType) {
		this.isUpperBound = isUpperBound;
		this.boundType = boundType;
	}

	public boolean isUpperBound() {
		return isUpperBound;
	}

	public void setUpperBound(boolean isUpperBound) {
		this.isUpperBound = isUpperBound;
	}

	public TypeRef getBoundType() {
		return boundType;
	}

	public void setBoundType(TypeRef boundType) {
		this.boundType = boundType;
	}

	public boolean isWildcardType() {
		return true;
	}
}
