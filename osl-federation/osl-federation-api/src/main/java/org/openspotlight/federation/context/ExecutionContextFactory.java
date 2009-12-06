package org.openspotlight.federation.context;

import org.openspotlight.common.Disposable;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public interface ExecutionContextFactory extends Disposable {
	ExecutionContext createExecutionContext(String username, String password,
			JcrConnectionDescriptor descriptor, String repositoryName);
}
