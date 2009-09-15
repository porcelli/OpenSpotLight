package org.openspotlight.tool.dap.language.java.asm.model;

public class PrimitiveTypeRef implements TypeRef {

	public enum PrimitiveType {
		BOOLEAN, CHAR, BYTE, SHORT, INT, FLOAT, LONG, DOUBLE, VOID;
	}

	private PrimitiveType type = null;

	public PrimitiveTypeRef(PrimitiveType type) {
		this.type = type;
	}

	public PrimitiveType getType() {
		return type;
	}

	public void setType(PrimitiveType type) {
		this.type = type;
	}

	public boolean isPrimitive() {
		return true;
	}
}
