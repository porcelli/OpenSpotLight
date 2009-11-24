package org.openspotlight.security.domain;

import java.io.Serializable;

import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class SLIdentityObjectRelationshipType implements
		IdentityObjectRelationshipType, SimpleNodeType, Serializable {

	private String name;

	private SLIdentityObjectRelationship parent;

	@KeyProperty
	public String getName() {
		return this.name;
	}

	@ParentProperty
	public SLIdentityObjectRelationship getParent() {
		return this.parent;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setParent(final SLIdentityObjectRelationship parent) {
		this.parent = parent;
	}

}
