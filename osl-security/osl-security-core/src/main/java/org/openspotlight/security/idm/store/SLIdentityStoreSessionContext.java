package org.openspotlight.security.idm.store;

public class SLIdentityStoreSessionContext {

	public SLIdentityStoreSessionContext(SLIdentityStoreSessionImpl session){
		this.session=session;
	}
	
	private final SLIdentityStoreSessionImpl session;

	public SLIdentityStoreSessionImpl getSession() {
		return session;
	}
	
	
}
