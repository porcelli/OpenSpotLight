package org.openspotlight.tool.dap.language.java.asm.model;

public class Field {
	private String name = null;
	private int access;
	private TypeRef type = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public TypeRef getType() {
		return type;
	}

	public void setType(TypeRef type) {
		this.type = type;
	}
}