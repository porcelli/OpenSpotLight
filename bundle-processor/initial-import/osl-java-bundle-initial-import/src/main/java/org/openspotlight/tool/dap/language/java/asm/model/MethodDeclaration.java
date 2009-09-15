package org.openspotlight.tool.dap.language.java.asm.model;

import java.util.LinkedList;
import java.util.List;

public class MethodDeclaration {
	private String name = null;
	private int access;
	private TypeRef returnType = null;
	private List<MethodParameter> parameters = new LinkedList<MethodParameter>();
	private boolean isConstructor = false;
	private List<TypeRef> thrownExceptions = new LinkedList<TypeRef>();
	private List<TypeParameter> typeParameters = new LinkedList<TypeParameter>();

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public List<TypeParameter> getTypeParameters() {
		return typeParameters;
	}

	public void setTypeParameters(List<TypeParameter> typeParameters) {
		this.typeParameters = typeParameters;
	}

	public List<TypeRef> getThrownExceptions() {
		return thrownExceptions;
	}

	public void setThrownExceptions(List<TypeRef> thrownExceptions) {
		this.thrownExceptions = thrownExceptions;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeRef getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeRef returnType) {
		this.returnType = returnType;
	}

	public List<MethodParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<MethodParameter> parameters) {
		this.parameters = parameters;
	}
}