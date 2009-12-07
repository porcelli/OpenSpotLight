package org.openspotlight.graph.server;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.remote.server.RemoteObjectServer;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

/**
 * The Class RemoteGraphSessionServer.
 */
public class RemoteGraphSessionServer {

	/**
	 * A factory for creating InternalGraphSession objects.
	 */
	private static class InternalGraphSessionFactory implements
			InternalObjectFactory<SLGraphSession> {

		private final SLGraph graph;
		private final JcrConnectionDescriptor descriptor;

		/**
		 * Instantiates a new internal graph session factory.
		 */
		public InternalGraphSessionFactory(
				final JcrConnectionDescriptor descriptor)
				throws SLInvalidCredentialException {
			try {
				this.descriptor = descriptor;
				final SLGraphFactory graphFactory = AbstractFactory
						.getDefaultInstance(SLGraphFactory.class);
				graph = graphFactory.createGraph(descriptor);
			} catch (final AbstractFactoryException e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			} catch (final SLInvalidCredentialException e) {
				throw logAndReturnNew(e, SLInvalidCredentialException.class);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory
		 * #createNewInstance(java.lang.Object[])
		 */
		public synchronized SLGraphSession createNewInstance(
				final Object... parameters) throws Exception {
			checkNotNull("parameters", parameters);
			checkCondition("correctParamSize", parameters.length == 2);
			checkCondition("correctTypeForFirstParam",
					parameters[0] instanceof String);
			checkCondition("correctTypeForSecondParam",
					parameters[1] instanceof String);
			final String user = (String) parameters[0];
			final String pass = (String) parameters[1];
			final SecurityFactory securityFactory = AbstractFactory
					.getDefaultInstance(SecurityFactory.class);
			final User simpleUser = securityFactory.createUser(user);
			final AuthenticatedUser authenticatedUser = securityFactory
					.createIdentityManager(descriptor).authenticate(simpleUser,
							pass);

			return graph.openSession(authenticatedUser);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory
		 * #getTargetObjectType()
		 */
		public Class<SLGraphSession> getTargetObjectType() {
			return SLGraphSession.class;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory
		 * #shutdown()
		 */
		public void shutdown() {
			graph.shutdown();
			final JcrConnectionProvider provider = JcrConnectionProvider
					.createFromData(descriptor);
			provider.closeRepositoryAndCleanResources();

		}
	}

	/** The remote object server. */
	private final RemoteObjectServer remoteObjectServer;

	/**
	 * Instantiates a new remote graph session server.
	 * 
	 * @param userAutenticator
	 *            the user autenticator
	 * @param portToUse
	 *            the port to use
	 * @param timeoutInMilliseconds
	 *            the timeout in milliseconds
	 */
	public RemoteGraphSessionServer(final UserAuthenticator userAutenticator,
			final Integer portToUse, final Long timeoutInMilliseconds,
			final JcrConnectionDescriptor descriptor)
			throws SLInvalidCredentialException {
		checkNotNull("userAutenticator", userAutenticator);
		checkNotNull("portToUse", portToUse);
		checkNotNull("timeoutInMilliseconds", timeoutInMilliseconds);
		checkNotNull("descriptor", descriptor);
		remoteObjectServer = RemoteObjectServerImpl.getDefault(
				userAutenticator, portToUse, timeoutInMilliseconds);
		remoteObjectServer.registerInternalObjectFactory(SLGraphSession.class,
				new InternalGraphSessionFactory(descriptor));
	}

	public void remoteAllObjectsFromServer() {
		remoteObjectServer.closeAllObjects();
	}

	/**
	 * Shutdown. This method should be called <b>only one time during the VM
	 * life cycle</b>. This is necessary due some static garbage on RMI.
	 */
	public void shutdown() {
		remoteObjectServer.shutdown();
	}
}
