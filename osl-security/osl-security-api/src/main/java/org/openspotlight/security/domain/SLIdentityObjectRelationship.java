package org.openspotlight.security.domain;

import java.io.Serializable;

import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationship;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

public class SLIdentityObjectRelationship implements SimpleNodeType,
		Serializable, IdentityObjectRelationship {

	private String typeAsString;

	private String fromIdentityObjectId;

	private String toIdentityObjectId;

	private IdentityObject fromIdentityObject;

	private IdentityObject toIdentityObject;

	private String name;

	private IdentityObjectRelationshipType type;

	@TransientProperty
	public IdentityObject getFromIdentityObject() {
		return this.fromIdentityObject;
	}

	@KeyProperty
	public String getFromIdentityObjectId() {
		return this.fromIdentityObjectId;
	}

	@KeyProperty
	public String getName() {
		return this.name;
	}

	@TransientProperty
	public IdentityObject getToIdentityObject() {
		return this.toIdentityObject;
	}

	@KeyProperty
	public String getToIdentityObjectId() {
		return this.toIdentityObjectId;
	}

	public IdentityObjectRelationshipType getType() {
		return this.type;
	}

	public String getTypeAsString() {
		return this.typeAsString;
	}

	public void setFromIdentityObject(final IdentityObject fromIdentityObject) {
		this.fromIdentityObject = fromIdentityObject;
		if (fromIdentityObject != null) {
			this.fromIdentityObjectId = fromIdentityObject.getId();
		}
	}

	public void setFromIdentityObjectId(final String fromIdentityObjectId) {
		this.fromIdentityObjectId = fromIdentityObjectId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setToIdentityObject(final IdentityObject toIdentityObject) {
		this.toIdentityObject = toIdentityObject;
		if (toIdentityObject != null) {
			this.toIdentityObjectId = toIdentityObject.getId();
		}
	}

	public void setToIdentityObjectId(final String toIdentityObjectId) {
		this.toIdentityObjectId = toIdentityObjectId;
	}

	public void setType(final IdentityObjectRelationshipType type) {
		this.type = type;
		this.typeAsString = type == null ? null : type.getName();
	}

	public void setTypeAsString(final String typeAsString) {
		this.typeAsString = typeAsString;
	}

}
