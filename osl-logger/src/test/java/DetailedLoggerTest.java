import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.log.DetailedLoggerFactory;
import org.openspotlight.federation.log.DetailedLoggerFactory.LogEntry;
import org.openspotlight.federation.log.DetailedLoggerFactory.LogEntry.LoggedObjectInformation;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class DetailedLoggerTest {

    public static class CustomErrorCode implements ErrorCode {

        public String getDescription() {
            return "CustomErrorCode:description";
        }

        public String getErrorCode() {
            return "CustomErrorCode:errorCode";
        }

    }

    private Session                      session;

    private SLGraphSession               graphSession;

    private static JcrConnectionProvider provider;

    private static SLGraph               graph;

    private static AuthenticatedUser     user;

    @BeforeClass
    public static void setupJcr() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class).createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");

    }

    private DetailedLogger logger;

    @After
    public void releaseAttributes() throws Exception {
        if (this.session != null) {
            if (this.session.isLive()) {
                this.session.logout();
            }
            this.session = null;
        }
        if (this.graphSession != null) {
            this.graphSession.close();
            this.graphSession = null;
        }
        this.logger = null;
    }

    @Before
    public void setupAttributes() throws Exception {
        this.session = provider.openSession();
        this.graphSession = graph.openSession(user, "tempRepo");
        this.logger = DetailedLoggerFactory.createJcrDetailedLogger(this.session);
    }

    @Test
    public void shouldLogSomeStuff() throws Exception {

        final StreamArtifact artifact = Artifact.createArtifact(StreamArtifact.class, "a/b/c/d", ChangeType.INCLUDED);
        final SLNode node = this.graphSession.createContext("ctx").getRootNode().addNode("node1");
        final SLNode node2 = node.addNode("node2");
        final SLNode node3 = node.addNode("node3");

        this.logger.log(user.getId(), "tempRepo", LogEventType.DEBUG, new CustomErrorCode(), "firstEntry", node3, artifact);
        this.logger.log(user.getId(), "tempRepo", LogEventType.DEBUG, new CustomErrorCode(), "secondEntry", artifact);
        this.logger.log(user.getId(), "tempRepo", LogEventType.DEBUG, new CustomErrorCode(), "thirdEntry", node3);

        final Query query = this.session.getWorkspace().getQueryManager().createQuery(
                                                                                      SharedConstants.DEFAULT_JCR_ROOT_NAME
                                                                                      + "//"
                                                                                      + SimplePersistSupport.getJcrNodeName(LogEntry.class),
                                                                                      Query.XPATH);
        final NodeIterator foundNodes = query.execute().getNodes();
        final Iterable<LogEntry> foundEntries = SimplePersistSupport.convertJcrsToBeans(this.session, foundNodes, LazyType.EAGER);
        boolean hasAnyEntry = false;
        boolean hasAnyObject = false;
        for (final LogEntry entry : foundEntries) {
            hasAnyEntry = true;
            for (final LoggedObjectInformation info : entry.getNodes()) {
                hasAnyObject = true;
                assertThat(info.getClassName(), is(notNullValue()));
                assertThat(info.getFriendlyDescription(), is(notNullValue()));
            }
            assertThat(entry.getType(), is(notNullValue()));
            assertThat(entry.getDate(), is(notNullValue()));
            assertThat(entry.getDetailedMessage(), is(notNullValue()));
            assertThat(entry.getErrorCode(), is(notNullValue()));
        }
        assertThat(hasAnyEntry, is(true));
        assertThat(hasAnyObject, is(true));

    }

}
