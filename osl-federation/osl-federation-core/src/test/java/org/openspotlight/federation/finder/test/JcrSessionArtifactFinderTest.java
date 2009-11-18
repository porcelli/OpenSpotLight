package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Repository;
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

    private static ArtifactSource        artifactSource;

    private static Repository            repository;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setup() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        final Session session = provider.openSession();
        artifactSource = new ArtifactSource();
        artifactSource.setName("classpath");
        artifactSource.setInitialLookup("./src");
        repository = new Repository();
        repository.setName("name");

        final FileSystemStreamArtifactFinder fileSystemFinder = new FileSystemStreamArtifactFinder();
        final Set<StreamArtifact> artifacts = fileSystemFinder.listByPath(artifactSource, null);
        SimplePersistSupport.convertBeansToJcrs(JcrSessionArtifactFinder.getArtifactRootPathFor(repository), session, artifacts);
        session.save();
        session.logout();
    }

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
        this.streamArtifactFinder = JcrSessionArtifactFinder.createArtifactFinder(StreamArtifact.class, repository, this.session);
    }

    @Test
    public void shouldFindArtifacts() throws Exception {
        final StreamArtifact sa = this.streamArtifactFinder.findByPath(artifactSource,
                                                                       "/test/java/org/openspotlight/federation/finder/test/JcrSessionArtifactFinderTest.java");
        assertThat(sa, is(notNullValue()));
        assertThat(sa.getContent(), is(notNullValue()));

    }

    @Test
    public void shouldListArtifactNames() throws Exception {
        final Set<String> artifacts = this.streamArtifactFinder.retrieveAllArtifactNames(artifactSource, null);

        assertThat(artifacts, is(notNullValue()));
        assertThat(artifacts.size(), is(not(0)));
        for (final String s : artifacts) {
            assertThat(s, is(notNullValue()));
        }
    }

    @Test
    public void shouldListArtifacts() throws Exception {
        final Set<StreamArtifact> artifacts = this.streamArtifactFinder.listByPath(artifactSource,
                                                                                   "/main/java/org/openspotlight/federation");

        assertThat(artifacts, is(notNullValue()));
        assertThat(artifacts.size(), is(not(0)));
        for (final StreamArtifact sa : artifacts) {
            assertThat(sa, is(notNullValue()));
            assertThat(sa.getContent(), is(notNullValue()));
        }
    }

}
