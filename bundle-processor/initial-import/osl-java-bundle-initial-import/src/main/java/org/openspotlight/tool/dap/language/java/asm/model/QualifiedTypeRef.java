package org.openspotlight.tool.dap.language.java.asm.model;

public class QualifiedTypeRef implements TypeRef {

	private TypeRef type = null;
	private String typeName = null;

	public QualifiedTypeRef(TypeRef type, String typeName) {
		this.type = type;
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public TypeRef getType() {
		return type;
	}

	public void setType(TypeRef type) {
		this.type = type;
	}

}