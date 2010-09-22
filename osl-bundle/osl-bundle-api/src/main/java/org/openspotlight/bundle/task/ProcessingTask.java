package org.openspotlight.bundle.task;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextProvider;
import org.openspotlight.federation.domain.artifact.Artifact;

public abstract class ProcessingTask extends BaseTask implements
		ConfigurableTask {

	private final ExecutionContextProvider provider;
	private final Artifact artifact;

	protected ProcessingTask(ExecutionContextProvider provider,
			Artifact artifact) {
		this.provider = provider;
		this.artifact = artifact;
	}

	@Override
	public Void call() throws Exception {
		provider.setupBeforeGet();
		try {
			ExecutionContext context = provider.get();
			execute(context, artifact);
			return null;
		} finally {
			provider.release();
		}
	}

	protected abstract void execute(ExecutionContext context, Artifact artifact)
			throws Exception;

}
