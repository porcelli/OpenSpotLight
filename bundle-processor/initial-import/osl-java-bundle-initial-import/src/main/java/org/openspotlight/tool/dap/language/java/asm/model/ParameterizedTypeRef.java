package org.openspotlight.tool.dap.language.java.asm.model;

import java.util.LinkedList;
import java.util.List;

public class ParameterizedTypeRef implements TypeRef {

	private List<TypeRef> typeArguments = new LinkedList<TypeRef>();
	private TypeRef type = null;

	public ParameterizedTypeRef(List<TypeRef> typeArguments, TypeRef type) {
		this.typeArguments = typeArguments;
		this.type = type;
	}

	public List<TypeRef> getTypeArguments() {
		return typeArguments;
	}

	public void setTypeArguments(List<TypeRef> typeArguments) {
		this.typeArguments = typeArguments;
	}

	public TypeRef getType() {
		return type;
	}

	public void setType(TypeRef type) {
		this.type = type;
	}

	public boolean isParameterizedType() {
		return true;
	}
}
