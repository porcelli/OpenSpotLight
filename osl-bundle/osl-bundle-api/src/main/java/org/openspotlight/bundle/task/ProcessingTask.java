package org.openspotlight.bundle.task;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextProvider;

public abstract class ProcessingTask extends BaseTask implements
		ConfigurableTask {

	private final ExecutionContextProvider provider;
	protected ProcessingTask(ExecutionContextProvider provider) {
		this.provider = provider;
	}

	@Override
	public Void call() throws Exception {
		provider.setupBeforeGet();
		try {
			ExecutionContext context = provider.get();
			execute(context);
			return null;
		} finally {
			provider.release();
		}
	}

	protected abstract void execute(ExecutionContext context)
			throws Exception;

}
