package org.openspotlight.tool.dap.language.java.asm.model;

import java.util.LinkedList;
import java.util.List;

public class TypeParameter {

	private String name = null;
	private List<TypeRef> typeBounds = new LinkedList<TypeRef>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TypeRef> getTypeBounds() {
		return typeBounds;
	}

	public void setTypeBounds(List<TypeRef> typeBounds) {
		this.typeBounds = typeBounds;
	}
}