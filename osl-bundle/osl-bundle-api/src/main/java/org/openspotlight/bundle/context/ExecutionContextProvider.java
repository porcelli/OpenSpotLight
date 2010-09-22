package org.openspotlight.bundle.context;

public interface ExecutionContextProvider {

	public void setupBeforeGet();
	
	public ExecutionContext get();
	
	public void release();
	
}
