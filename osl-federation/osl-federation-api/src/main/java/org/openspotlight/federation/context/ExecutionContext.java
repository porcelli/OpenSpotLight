package org.openspotlight.federation.context;

import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;

public interface ExecutionContext extends Disposable {
	public <A extends Artifact> ArtifactFinder<A> getDefaultArtifactFinder();

	public ConfigurationManager getDefaultConfigurationManager();

	public JcrConnectionProvider getDefaultConnectionProvider();

	public SLGraphSession getGraphSession();

	public DetailedLogger getLogger();
}
