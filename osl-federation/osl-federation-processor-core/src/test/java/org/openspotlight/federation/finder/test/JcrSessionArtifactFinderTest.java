package org.openspotlight.federation.finder.test;

import java.util.Set;

import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.FileSystemStreamArtifactFinder;
import org.openspotlight.federation.finder.JcrSessionArtifactFinder;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.support.SimplePersistSupport;

public class JcrSessionArtifactFinderTest {

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

    private ArtifactSource                 artifactSource;

    private ArtifactFinder<StreamArtifact> streamArtifactFinder;

    /** The session. */
    private Session                        session = null;

    /**
     * Close session.
     */
    @After
    public void closeSession() {
        this.streamArtifactFinder = null;
        if (this.session != null) {
            this.session.logout();
            this.session = null;
        }
    }

    /**
     * Setup session.
     */
    @Before
    public void setupSession() {
        this.session = provider.openSession();
        this.streamArtifactFinder = JcrSessionArtifactFinder.createArtifactFinder(StreamArtifact.class, this.session);

        this.artifactSource = new ArtifactSource();
        this.artifactSource.setName("classpath:");
        this.artifactSource.setInitialLookup("./src");

        final FileSystemStreamArtifactFinder fileSystemFinder = new FileSystemStreamArtifactFinder();
        final Set<StreamArtifact> artifacts = fileSystemFinder.listByPath(this.artifactSource, null);
        SimplePersistSupport.convertBeansToJcrs("", this.session, artifacts);

    }

    @Test
    public void shouldFindArtifacts() throws Exception {

    }

}
