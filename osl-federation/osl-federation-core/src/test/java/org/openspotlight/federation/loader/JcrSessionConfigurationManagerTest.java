package org.openspotlight.federation.loader;

import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

/**
 * The Class JcrSessionConfigurationManagerTest.
 */
public class JcrSessionConfigurationManagerTest extends AbstractConfigurationManagerTest {

    private static JcrConnectionProvider provider;

    @BeforeClass
    public static void setupJcrRepo() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    }

    private Session session;

    @After
    public void closeSession() throws Exception {
        if (this.session != null && this.session.isLive()) {
            this.session.logout();
            this.session = null;
        }
    }

    @Override
    protected ConfigurationManager createNewConfigurationManager() {
        return JcrSessionConfigurationManagerFactory.createMutableUsingSession(this.session);
    }

    @Before
    public void setupSession() throws Exception {
        this.session = provider.openSession();
    }

}
