package org.openspotlight.federation.context;

import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.security.idm.AuthenticatedUser;

public interface ExecutionContext extends Disposable {
	public <A extends Artifact> ArtifactFinder<A> getArtifactFinder(
			Class<A> type);

	public ConfigurationManager getDefaultConfigurationManager();

	public JcrConnectionProvider getDefaultConnectionProvider();

	public SLGraphSession getGraphSession();

	public DetailedLogger getLogger();

	public String getRepository();

	public AuthenticatedUser getUser();
}
