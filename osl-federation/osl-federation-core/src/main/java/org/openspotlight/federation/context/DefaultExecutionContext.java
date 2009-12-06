package org.openspotlight.federation.context;

import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;

public class DefaultExecutionContext implements ExecutionContext {

	public void closeResources() {
		// TODO Auto-generated method stub

	}

	public <A extends Artifact> ArtifactFinder<A> getDefaultArtifactFinder() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConfigurationManager getDefaultConfigurationManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public JcrConnectionProvider getDefaultConnectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public SLGraphSession getGraphSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailedLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
