package org.openspotlight.tool.dap.language.java.asm.model;

import java.util.LinkedList;
import java.util.List;

public class JavaType {

	public enum JavaTypeDef {
		CLASS, INTERFACE, ENUM, ANNOTATION;
	}

	private String packageName = null;
	private String typeName = null;
	private JavaTypeDef type = null;
	private int access;
	private TypeRef extendsDef = null;
	private List<TypeRef> implementsDef = new LinkedList<TypeRef>();
	private List<Field> fields = new LinkedList<Field>();
	private List<MethodDeclaration> methods = new LinkedList<MethodDeclaration>();

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

	public JavaTypeDef getType() {
		return type;
	}

	public void setType(JavaTypeDef type) {
		this.type = type;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public TypeRef getExtendsDef() {
		return extendsDef;
	}

	public void setExtendsDef(TypeRef extendsDef) {
		this.extendsDef = extendsDef;
	}

	public List<TypeRef> getImplementsDef() {
		return implementsDef;
	}

	public void setImplementsDef(List<TypeRef> implementsDef) {
		this.implementsDef = implementsDef;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<MethodDeclaration> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodDeclaration> methods) {
		this.methods = methods;
	}
}