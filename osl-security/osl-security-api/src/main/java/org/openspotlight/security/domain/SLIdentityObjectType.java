package org.openspotlight.security.domain;

import java.io.Serializable;

import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class SLIdentityObjectType implements IdentityObjectType,
		SimpleNodeType, Serializable {

	private String name;

	private SLIdentityObject parent;

	@KeyProperty
	public String getName() {
		return this.name;
	}

	@ParentProperty
	public SLIdentityObject getParent() {
		return this.parent;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setParent(final SLIdentityObject parent) {
		this.parent = parent;
	}

}
