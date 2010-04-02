package org.openspotlight.bundle.language.java.bundle.test;

import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.remote.server.UserAuthenticator;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainClass {

	public static void main(final String... args) throws Exception {
		final long start = System.currentTimeMillis();
		JcrConnectionProvider.createFromData(
				DefaultJcrDescriptor.DEFAULT_DESCRIPTOR).openSession();
		final javax.jcr.Repository repository = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.DEFAULT_DESCRIPTOR)
				.getRepository();

		final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
		final RemoteRepository remote = saFactory
				.getRemoteRepository(repository);

		final Registry registry = LocateRegistry
				.createRegistry(Registry.REGISTRY_PORT);
		registry.bind("jackrabbit.repository", remote);
		final long end = System.currentTimeMillis();
		System.err.println("started in about " + (int) (end - start) / 1000);

		RemoteGraphSessionServer server = null;
		try {
			server = new RemoteGraphSessionServer(new UserAuthenticator() {

				public boolean canConnect(final String userName,
						final String password, final String clientHost) {
					return true;
				}

				public boolean equals(final Object o) {
					return this.getClass().equals(o.getClass());
				}
			}, 7070, 60 * 1000 * 10L, DefaultJcrDescriptor.DEFAULT_DESCRIPTOR);
			System.err.println("Server waiting connections on port 7070");
			while (true) {
				Thread.sleep(5000);
			}
		} finally {
			if (server != null) {
				server.shutdown();
			}
		}

	}

}
