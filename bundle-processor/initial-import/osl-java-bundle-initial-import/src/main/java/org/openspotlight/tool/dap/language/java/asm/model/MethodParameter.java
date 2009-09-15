package org.openspotlight.tool.dap.language.java.asm.model;

public class MethodParameter {
	private int position = -1;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	private TypeRef dataType = null;

	public TypeRef getDataType() {
		return dataType;
	}

	public void setDataType(TypeRef dataType) {
		this.dataType = dataType;
	}
}
