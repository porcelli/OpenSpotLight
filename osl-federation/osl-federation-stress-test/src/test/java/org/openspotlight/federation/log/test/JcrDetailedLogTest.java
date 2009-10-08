package org.openspotlight.federation.log.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.processing.test.ConfigurationExamples;
import org.openspotlight.federation.log.DetailedLogger;
import org.openspotlight.federation.log.DetailedLogger.ErrorCode;
import org.openspotlight.federation.log.DetailedLogger.EventType;
import org.openspotlight.federation.log.DetailedLogger.LogEntry;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class JcrDetailedLogTest {

    @BeforeClass
    public static void setupProvider() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    }

    @AfterClass
    public static void shutdownProvider() throws Exception {
        provider.closeRepository();
    }

    private DetailedLogger               logger;
    private Session                      session;
    private SLGraph                      graph;
    private SLGraphSession               graphSession;

    private static JcrConnectionProvider provider;
    private ConfigurationManager         configurationManager;
    private SLContext                    ctx;
    private SLNode                       slNode1;
    private SLNode                       slNode2;
    private SLNode                       slNode3;

    private Configuration                configuration;

    private Group                        group;

    @Before
    public void setup() throws Exception {
        this.session = provider.openSession();
        this.logger = DetailedLogger.Factory.createJcrDetailedLogger(this.session);
        this.graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class).createGraph(this.provider);
        this.configurationManager = new JcrSessionConfigurationManager(this.session);
        this.graphSession = this.graph.openSession();
        this.ctx = this.graphSession.getContext("test");
        if (this.ctx == null) {
            this.ctx = this.graphSession.createContext("test");
        }
        this.slNode1 = this.ctx.getRootNode().addNode("slNode1");
        this.slNode2 = this.slNode1.addNode("slNode2");
        this.slNode3 = this.slNode2.addNode("slNode3");
        this.configuration = ConfigurationExamples.createOslValidConfiguration("JcrDetailedLogTest");
        this.configurationManager.save(this.configuration);
        this.configuration = this.configurationManager.load(LazyType.EAGER);
        this.group = this.configuration.getRepositoryByName("OSL Group").getGroupByName("OSL Root Group");
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void shouldCreateAndRetrieveLogInformationByDateInterval() throws Exception {
        this.logger.log(EventType.INFO, "hey there!", this.slNode3, this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.slNode3);

        List<LogEntry> result = this.logger.findLogByLogableObject(this.group);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getType(), is(EventType.INFO));

        result = this.logger.findLogByErrorCode(ErrorCode.NO_ERROR_CODE);
        assertThat(result.size(), is(3));

        result = this.logger.findLogByLogableObject(this.slNode1);
        assertThat(result.size(), is(2));

        result = this.logger.findLogByEventType(EventType.INFO);
        assertThat(result.size(), is(3));

        result = this.logger.findLogByDateInterval(new DateTime("2000-01-01").toDate(), new DateTime("2010-01-01").toDate());
        assertThat(result.size(), is(3));

    }

    @SuppressWarnings( "boxing" )
    @Test
    public void shouldCreateAndRetrieveLogInformationByErrorCode() throws Exception {
        this.logger.log(EventType.INFO, "hey there!", this.slNode3, this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.slNode3);

        final List<LogEntry> result = this.logger.findLogByErrorCode(ErrorCode.NO_ERROR_CODE);
        assertThat(result.size(), is(3));

    }

    @SuppressWarnings( "boxing" )
    @Test
    public void shouldCreateAndRetrieveLogInformationByEventType() throws Exception {
        this.logger.log(EventType.INFO, "hey there!", this.slNode3, this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.slNode3);

        final List<LogEntry> result = this.logger.findLogByEventType(EventType.INFO);
        assertThat(result.size(), is(3));

    }

    @SuppressWarnings( "boxing" )
    @Test
    public void shouldCreateAndRetrieveLogInformationByLogableObject() throws Exception {
        this.logger.log(EventType.INFO, "hey there!", this.slNode3, this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.slNode3);

        List<LogEntry> result = this.logger.findLogByLogableObject(this.group);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getType(), is(EventType.INFO));

        result = this.logger.findLogByLogableObject(this.slNode1);
        assertThat(result.size(), is(2));

    }

    @Test
    public void shouldCreateLogInformation() throws Exception {
        this.logger.log(EventType.INFO, "hey there!", this.slNode3, this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.group);
        this.logger.log(EventType.INFO, "Yeah!", this.slNode3);
    }

    @After
    public void shutdown() throws Exception {
        this.graphSession.close();
        this.session.logout();
    }
}
