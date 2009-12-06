package org.openspotlight.federation.context;

import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;

public interface ExecutionContext extends Disposable {
	public <A extends Artifact> ArtifactFinder<A> getDefaultArtifactFinder(
			Class<A> type);

	public ConfigurationManager getDefaultConfigurationManager();

	public JcrConnectionProvider getDefaultConnectionProvider();

	public SLGraphSession getGraphSession();

	public DetailedLogger getLogger();
}
