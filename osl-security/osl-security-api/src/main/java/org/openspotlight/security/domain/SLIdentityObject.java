/*
 * JBoss, a division of Red Hat
 * Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openspotlight.security.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.jboss.identity.idm.common.exception.PolicyValidationException;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

public class SLIdentityObject implements IdentityObject, SimpleNodeType,
		Serializable {

    private static final long serialVersionUID = -4651245099086963026L;

    private String typeAsString;

	private Set<SLAttributeEntry> attributes = new HashSet<SLAttributeEntry>();

	private String id;

	private SLIdentityObjectType typedIdentityType;

	private String name;

	public Set<SLAttributeEntry> getAttributes() {
		return this.attributes;
	}

	@TransientProperty
	public Set<SLTransientIdentityObjectAttribute> getAttributesAsIdentityAttributes() {
		final HashSet<SLTransientIdentityObjectAttribute> result = new HashSet<SLTransientIdentityObjectAttribute>();
		for (final SLAttributeEntry entry : this.attributes) {
			result.add(entry.asIdentityAttribute());
		}
		return result;
	}

	@KeyProperty
	public String getId() {
		return this.id;
	}

	@TransientProperty
	public IdentityObjectType getIdentityType() {
		return this.typedIdentityType;
	}

	@KeyProperty
	public String getName() {
		return this.name;
	}

	public String getTypeAsString() {
		return this.typeAsString;
	}

	public SLIdentityObjectType getTypedIdentityType() {
		return this.typedIdentityType;
	}

	public void setAttributes(final Set<SLAttributeEntry> attributes) {
		this.attributes = attributes;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setIdentityType(final IdentityObjectType identityType) {
		this.typedIdentityType = (SLIdentityObjectType) identityType;
		this.typeAsString = identityType == null ? null : identityType
				.getName();
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setTypeAsString(final String typeAsString) {
		this.typeAsString = typeAsString;
	}

	public void setTypedIdentityType(
			final SLIdentityObjectType typedIdentityType) {
		this.typedIdentityType = typedIdentityType;
	}

	public void validatePolicy() throws PolicyValidationException {
		// TODO Auto-generated method stub

	}
}
