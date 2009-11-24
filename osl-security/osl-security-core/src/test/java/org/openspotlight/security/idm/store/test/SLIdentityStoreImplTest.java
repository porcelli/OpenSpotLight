package org.openspotlight.security.idm.store.test;

import org.jboss.identity.idm.impl.store.CommonIdentityStoreTest;
import org.jboss.identity.idm.impl.store.IdentityStoreTestContext;
import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.junit.Test;

public class SLIdentityStoreImplTest {

	private static class SLIdStoreTestContext implements
			IdentityStoreTestContext {

		public void begin() throws Exception {
			// TODO Auto-generated method stub

		}

		public void commit() throws Exception {
			// TODO Auto-generated method stub

		}

		public void flush() throws Exception {
			// TODO Auto-generated method stub

		}

		public IdentityStoreInvocationContext getCtx() {
			// TODO Auto-generated method stub
			return null;
		}

		public IdentityStore getStore() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private final CommonIdentityStoreTest test = new CommonIdentityStoreTest(
			new SLIdStoreTestContext());

	@Test
	public void testAttributes() throws Exception {
		this.test.testAttributes();
	}

	@Test
	public void testBinaryCredential() throws Exception {
		this.test.testBinaryCredential();
	}

	@Test
	public void testCriteria() throws Exception {
		this.test.testCriteria();
	}

	@Test
	public void testFindMethods() throws Exception {
		this.test.testFindMethods();
	}

	@Test
	public void testPasswordCredential() throws Exception {
		this.test.testPasswordCredential();
	}

	@Test
	public void testRelationships() throws Exception {
		this.test.testRelationships();
	}

	@Test
	public void testStorePersistence() throws Exception {
		this.test.testStorePersistence();
	}
}