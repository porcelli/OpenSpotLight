package org.openspotlight.federation.finder.test;

import java.util.Set;

import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.FileSystemStreamArtifactFinder;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class FSArtifactFinderAndSimplePersistStressTest {

    private ArtifactSource               artifactSource;

    /** The provider. */
    private static JcrConnectionProvider provider;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setup() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    }

    /** The session. */
    private Session session = null;

    /**
     * Close session.
     */
    @After
    public void closeSession() {
        if (this.session != null) {
            this.session.logout();
            this.session = null;
        }
    }

    @Before
    public void prepareArtifactSource() throws Exception {
        this.artifactSource = new ArtifactSource();
        this.artifactSource.setName("filesystem");
        this.artifactSource.setInitialLookup("./src");
    }

    /**
     * Setup session.
     */
    @Before
    public void setupSession() {
        this.session = provider.openSession();
    }

    @Test
    public void shouldFindSourceAndStoreItOnJcr() throws Exception {
        final FileSystemStreamArtifactFinder finder = new FileSystemStreamArtifactFinder();
        final Set<StreamArtifact> lotsOfSource = finder.listByPath(this.artifactSource, null);

    }

}
