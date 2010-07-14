package org.openspotlight.graph.test.node;

import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.annotation.SLDefineHierarchy;
import org.openspotlight.graph.annotation.SLTransientProperty;

@SLDefineHierarchy
public abstract class JavaType extends SLNode {

	private String typeName;
	
	private boolean publicClass;
	
	private Integer someNumber;

	private String transientValue;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isPublicClass() {
		return publicClass;
	}

	public void setPublicClass(boolean publicClass) {
		this.publicClass = publicClass;
	}

	public Integer getSomeNumber() {
		return someNumber;
	}

	public void setSomeNumber(Integer someNumber) {
		this.someNumber = someNumber;
	}

	@SLTransientProperty
	public String getTransientValue() {
		return transientValue;
	}

	public void setTransientValue(String transientValue) {
		this.transientValue = transientValue;
	}
	
	
	
}
