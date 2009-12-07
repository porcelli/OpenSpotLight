package org.openspotlight.security.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.identity.idm.api.Credential;
import org.jboss.identity.idm.api.CredentialType;
import org.jboss.identity.idm.impl.api.BinaryCredential;
import org.jboss.identity.idm.impl.api.PasswordCredential;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectCredential;
import org.jboss.identity.idm.spi.model.IdentityObjectCredentialType;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

public class SLPasswordEntry implements SimpleNodeType, Serializable {

    private static final long serialVersionUID = -989160074137053633L;

    @SuppressWarnings("unchecked")
	public static SLPasswordEntry create(final IdentityObject identityObject,
			final IdentityObjectCredential credential) {
		final SLPasswordEntry entry = new SLPasswordEntry();
		entry.setCredentialClass((Class<? extends Credential>) credential
				.getClass());
		final IdentityObjectCredentialType type = credential.getType();
		if (type != null) {
			entry.setCredentialTypeClass((Class<? extends CredentialType>) type
					.getClass());
			entry.setCredentialTypeName(type.getName());
		}
		entry.setUserId(identityObject.getId());
		entry.setUserName(identityObject.getName());
		if (credential instanceof PasswordCredential) {
			final PasswordCredential passwordCredential = (PasswordCredential) credential;
			entry.setPasswordValue(passwordCredential.getValue());
		} else if (credential instanceof BinaryCredential) {
			final BinaryCredential binaryCredential = (BinaryCredential) credential;
			final byte[] rawValue = binaryCredential.getValue() != null ? binaryCredential
					.getValue()
					: new byte[0];
			final List<Byte> autoboxed = new ArrayList<Byte>();
			for (final byte b : rawValue) {
				autoboxed.add(b);
			}
			entry.setAutoboxedBinaryValue(autoboxed);
		} else {
			throw Exceptions.logAndReturn(new IllegalArgumentException(
					"invalid credential type"));
		}
		return entry;
	}

	private String credentialTypeName;

	private Class<? extends CredentialType> credentialTypeClass;

	private Class<? extends Credential> credentialClass;

	private String userId;

	private String userName;

	private List<Byte> autoboxedBinaryValue = new ArrayList<Byte>();

	private String passwordValue;

	public Credential asCredential() {
		if (PasswordCredential.class.equals(this.credentialClass)) {
			final PasswordCredential credential = new PasswordCredential(
					this.passwordValue);
			return credential;
		} else if (BinaryCredential.class.equals(this.credentialClass)) {

			final List<Byte> autoboxed = this.getAutoboxedBinaryValue();
			final byte[] raw = new byte[autoboxed == null ? 0 : autoboxed
					.size()];
			for (int i = 0, size = raw.length; i < size; i++) {
				raw[i] = autoboxed.get(i);
			}

			final BinaryCredential credential = new BinaryCredential(raw);
			return credential;
		} else {
			throw Exceptions.logAndReturn(new IllegalArgumentException(
					"invalid credential type"));
		}
	}

	public List<Byte> getAutoboxedBinaryValue() {
		return this.autoboxedBinaryValue;
	}

	public Class<? extends Credential> getCredentialClass() {
		return this.credentialClass;
	}

	public Class<? extends CredentialType> getCredentialTypeClass() {
		return this.credentialTypeClass;
	}

	public String getCredentialTypeName() {
		return this.credentialTypeName;
	}

	public String getPasswordValue() {
		return this.passwordValue;
	}

	@KeyProperty
	public String getUserId() {
		return this.userId;
	}

	public String getUserName() {
		return this.userName;
	}

	@TransientProperty
	public boolean isValid(final IdentityObject identityObject,
			final IdentityObjectCredential credential) {
		if (this.credentialClass.isInstance(credential)) {
			if (identityObject.getId().equals(this.userId)) {

				if (PasswordCredential.class.equals(this.credentialClass)) {
					final PasswordCredential thisCredential = (PasswordCredential) this
							.asCredential();
					final PasswordCredential thatCredential = (PasswordCredential) credential;
					return thisCredential.getEncodedValue().equals(
							thatCredential.getEncodedValue());
				}
				if (BinaryCredential.class.equals(this.credentialClass)) {
					final BinaryCredential thisCredential = (BinaryCredential) this
							.asCredential();
					final BinaryCredential thatCredential = (BinaryCredential) credential;
					return Arrays.equals(thisCredential.getValue(),
							thatCredential.getValue());
				}
			}
		}
		return false;

	}

	public void setAutoboxedBinaryValue(final List<Byte> autoboxedBinaryValue) {
		this.autoboxedBinaryValue = autoboxedBinaryValue;
	}

	public void setCredentialClass(
			final Class<? extends Credential> credentialClass) {
		this.credentialClass = credentialClass;
	}

	public void setCredentialTypeClass(
			final Class<? extends CredentialType> credentialTypeClass) {
		this.credentialTypeClass = credentialTypeClass;
	}

	public void setCredentialTypeName(final String credentialTypeName) {
		this.credentialTypeName = credentialTypeName;
	}

	public void setPasswordValue(final String passwordValue) {
		this.passwordValue = passwordValue;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

}
