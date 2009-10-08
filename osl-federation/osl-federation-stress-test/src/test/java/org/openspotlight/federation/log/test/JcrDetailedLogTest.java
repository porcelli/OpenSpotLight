package org.openspotlight.federation.log.test;

import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.processing.test.ConfigurationExamples;
import org.openspotlight.federation.log.DetailedLogger;
import org.openspotlight.federation.log.DetailedLogger.EventType;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class JcrDetailedLogTest {

    private DetailedLogger        logger;
    private Session               session;
    private SLGraph               graph;
    private SLGraphSession        graphSession;
    private JcrConnectionProvider provider;
    private ConfigurationManager  configurationManager;

    private SLContext             ctx;
    private SLNode                slNode1;
    private SLNode                slNode2;
    private SLNode                slNode3;
    private Configuration         configuration;
    private Group                 group;

    @Before
    public void setup() throws Exception {
        this.provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        this.session = this.provider.openSession();
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
        this.provider.closeRepository();
    }
}
