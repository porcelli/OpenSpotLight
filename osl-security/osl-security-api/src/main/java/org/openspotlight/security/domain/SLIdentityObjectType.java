package org.openspotlight.security.domain;

import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;

public class SLIdentityObjectType implements IdentityObjectType{

	private String name;
	
	@KeyProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ParentProperty
	public SLIdentityObject getParent() {
		return parent;
	}

	public void setParent(SLIdentityObject parent) {
		this.parent = parent;
	}

	private SLIdentityObject parent;

}
