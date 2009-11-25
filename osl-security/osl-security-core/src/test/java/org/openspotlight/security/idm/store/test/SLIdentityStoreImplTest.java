package org.openspotlight.security.idm.store.test;

import java.text.MessageFormat;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.jboss.identity.idm.impl.configuration.IdentityConfigurationImpl;
import org.jboss.identity.idm.impl.configuration.IdentityStoreConfigurationContextImpl;
import org.jboss.identity.idm.impl.configuration.jaxb2.JAXB2IdentityConfiguration;
import org.jboss.identity.idm.impl.store.CommonIdentityStoreTest;
import org.jboss.identity.idm.impl.store.IdentityStoreTestContext;
import org.jboss.identity.idm.spi.configuration.IdentityConfigurationContextRegistry;
import org.jboss.identity.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityConfigurationMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityStoreConfigurationMetaData;
import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.idm.store.SLIdentityStoreImpl;
import org.openspotlight.security.idm.store.SLIdentityStoreSessionImpl;

public class SLIdentityStoreImplTest {

	private static class SLIdStoreTestContext implements
			IdentityStoreTestContext {

		private SLIdentityStoreImpl store;

		private SLIdentityStoreSessionImpl session;

		public void begin() throws Exception {

			final IdentityConfigurationMetaData configurationMD = JAXB2IdentityConfiguration
					.createConfigurationMetaData("slstore.xml");

			final IdentityConfigurationContextRegistry registry = (IdentityConfigurationContextRegistry) new IdentityConfigurationImpl()
					.configure(configurationMD);

			IdentityStoreConfigurationMetaData storeMD = null;

			for (final IdentityStoreConfigurationMetaData metaData : configurationMD
					.getIdentityStores()) {
				if (metaData.getId().equals("SLStore")) {
					storeMD = metaData;
					break;
				}
			}

			final IdentityStoreConfigurationContext context = new IdentityStoreConfigurationContextImpl(
					configurationMD, registry, storeMD);

			this.store = new SLIdentityStoreImpl();
			this.store.bootstrap(context);
			this.session = (SLIdentityStoreSessionImpl) this.store
					.createIdentityStoreSession();
		}

		public void commit() throws Exception {
			this.session.commitTransaction();

		}

		public void flush() throws Exception {
			this.session.save();

		}

		public IdentityStoreInvocationContext getCtx() {
			return new IdentityStoreInvocationContext() {

				public IdentityStoreSession getIdentityStoreSession() {
					return SLIdStoreTestContext.this.session;
				}

				public String getRealmId() {
					return "abc";
				}

				public String getSessionId() {
					return "abc";
				}
			};
		}

		public IdentityStore getStore() {
			return this.store;
		}

	}

	private static JcrConnectionProvider provider;

	@BeforeClass
	public static void setup() throws Exception {
		SLIdentityStoreImplTest.provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

	}

	private final CommonIdentityStoreTest test = new CommonIdentityStoreTest(
			new SLIdStoreTestContext());

	@Before
	public void clearAllData() throws Exception {
		final String nodeName = MessageFormat.format(
				SLIdentityStoreSessionImpl.SECURITY_NODE, "testRepository");
		final Session session = SLIdentityStoreImplTest.provider.openSession();
		try {
			final Node foundNode = session.getRootNode().getNode(nodeName);
			foundNode.remove();
			session.save();
		} catch (final PathNotFoundException e) {

		}
		session.logout();
	}

	@Test
	public void testAttributes() throws Exception {
		this.test.testAttributes();
	}

	@Test
	@Ignore
	// actually isn't possible to persist binary values on simple persist.
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