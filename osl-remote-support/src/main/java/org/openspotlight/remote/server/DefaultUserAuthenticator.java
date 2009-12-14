package org.openspotlight.remote.server;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.User;

public class DefaultUserAuthenticator implements UserAuthenticator {

	private final JcrConnectionDescriptor descriptor;
	private final SecurityFactory securityFactory;

	public DefaultUserAuthenticator(final JcrConnectionDescriptor descriptor) {
		try {
			this.descriptor = descriptor;
			securityFactory = AbstractFactory
					.getDefaultInstance(SecurityFactory.class);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public boolean canConnect(final String userName, final String password,
			final String clientHost) {
		try {
			final User simpleUser = securityFactory.createUser(userName);
			securityFactory.createIdentityManager(descriptor).authenticate(
					simpleUser, password);
			return true;
		} catch (final Exception e) {
			Exceptions.catchAndLog(e);
			return false;
		}
	}

}
