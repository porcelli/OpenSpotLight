package org.openspotlight.tool.dap.language.java.asm.model;

public class ArrayTypeRef implements TypeRef {

	private int arrayDimensions = -1;
	private TypeRef type = null;

	public ArrayTypeRef(int arraySize, TypeRef type) {
		this.arrayDimensions = arraySize;
		this.type = type;
	}

	public int getArrayDimensions() {
		return arrayDimensions;
	}

	public void setArrayDimensions(int arrayDimensions) {
		this.arrayDimensions = arrayDimensions;
	}

	public TypeRef getType() {
		return type;
	}

	public void setType(TypeRef type) {
		this.type = type;
	}

	public boolean isArray() {
		return true;
	}
}