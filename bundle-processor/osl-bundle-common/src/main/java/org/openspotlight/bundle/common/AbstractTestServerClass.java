package org.openspotlight.bundle.common;

import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.remote.server.UserAuthenticator;

import javax.jcr.Node;
import javax.jcr.Repository;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public abstract class AbstractTestServerClass {

	protected abstract void doWork(JcrConnectionProvider provider)
	throws Exception;

	public void doWorkAndExposeServers(){
		final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(
				getDescriptor());
		provider.closeRepositoryAndCleanResources();
		final Repository repository = provider.openRepository();
		try{
			doWork(provider);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
		exposeJcrOnRmi(repository);
		exportDataOnXml(provider);
		exposeGraphServerAndStillWaiting();
	}

	private void exportDataOnXml(final JcrConnectionProvider provider) {
		try{
			final SessionWithLock session = provider
			.openSession();
			final Node node = session.getRootNode().getNode(
					SLConsts.DEFAULT_JCR_ROOT_NAME);
			final String exportedFileName = getExportedFileName();
			if (exportedFileName != null) {
				if (exportedFileName.contains("/")) {
					new File(exportedFileName.substring(0, exportedFileName
							.lastIndexOf("/"))).mkdirs();
				}
				final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
						new FileOutputStream(exportedFileName));
				session.exportSystemView(node.getPath(), bufferedOutputStream, false,
						false);
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	protected final void exposeGraphServerAndStillWaiting() {
		RemoteGraphSessionServer server = null;
		try {
			try {
				server = new RemoteGraphSessionServer(new UserAuthenticator() {

					public boolean canConnect(final String userName,
							final String password, final String clientHost) {
						return true;
					}

					public boolean equals(final Object o) {
						return this.getClass().equals(o.getClass());
					}
				}, 7070, 60 * 1000 * 10L, getDescriptor());
				System.err.println("Server waiting connections on port 7070");
				if(!shutdownAtFinish()){
					while (true) {
						Thread.sleep(5000);
					}
				}
				System.exit(0);
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		} finally {
			if (server != null) {
				server.shutdown();
			}
		}

	}

	protected final void exposeJcrOnRmi(final Repository repository){
		try {
			final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
			final RemoteRepository remote = saFactory
			.getRemoteRepository(repository);

			final Registry registry = LocateRegistry
			.createRegistry(Registry.REGISTRY_PORT);
			registry.bind("jackrabbit.repository", remote);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	protected abstract JcrConnectionDescriptor getDescriptor();

	protected abstract String getExportedFileName();

	protected abstract boolean shutdownAtFinish();

}
