package org.openspotlight.tool.dap.language.java.asm.model;

public class SimpleTypeRef implements TypeRef {
	private String packageName = null;
	private String typeName = null;

	public SimpleTypeRef(String packageName, String typeName) {
		this.packageName = packageName;
		this.typeName = typeName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}