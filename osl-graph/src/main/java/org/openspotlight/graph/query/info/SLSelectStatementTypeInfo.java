package org.openspotlight.graph.query.info;

import java.io.Serializable;

import org.openspotlight.common.util.Equals;

public class SLSelectStatementTypeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private boolean subTypes;
	private boolean comma;
	
	public SLSelectStatementTypeInfo(String name) {
		setName(name);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSubTypes() {
		return subTypes;
	}
	public void setSubTypes(boolean subTypes) {
		this.subTypes = subTypes;
	}
	public boolean isComma() {
		return comma;
	}
	public void setComma(boolean comma) {
		this.comma = comma;
	}

	
	@Override
	public boolean equals(Object obj) {
		return Equals.eachEquality(SLSelectStatementTypeInfo.class, this, obj, "name");
	}

}
