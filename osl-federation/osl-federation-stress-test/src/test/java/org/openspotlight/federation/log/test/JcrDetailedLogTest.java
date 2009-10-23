package org.openspotlight.federation.log.test;


public class JcrDetailedLogTest {
    //
    //    @BeforeClass
    //    public static void setupProvider() throws Exception {
    //        Files.delete(DefaultJcrDescriptor.TEMP_DESCRIPTOR.getConfigurationDirectory());
    //        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    //    }
    //
    //    @AfterClass
    //    public static void shutdownProvider() throws Exception {
    //        if (provider != null) {
    //            provider.closeRepository();
    //        }
    //    }
    //
    //    private DetailedLogger               logger;
    //    private Session                      session;
    //    private SLGraph                      graph;
    //    private SLGraphSession               graphSession;
    //
    //    private static JcrConnectionProvider provider;
    //    private ConfigurationManager         configurationManager;
    //    private SLContext                    ctx;
    //    private SLNode                       slNode1;
    //    private SLNode                       slNode2;
    //    private SLNode                       slNode3;
    //
    //    private Configuration                configuration;
    //
    //    private Group                        group;
    //
    //    private static boolean               logged = false;
    //
    //    @Before
    //    public void setup() throws Exception {
    //        this.session = provider.openSession();
    //        this.logger = DetailedLogger.Factory.createJcrDetailedLogger(this.session);
    //        this.graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class).createGraph(provider);
    //        this.configurationManager = new JcrSessionConfigurationManager(this.session);
    //        this.graphSession = this.graph.openSession();
    //        this.ctx = this.graphSession.getContext("test");
    //        if (this.ctx == null) {
    //            this.ctx = this.graphSession.createContext("test");
    //        }
    //        this.slNode1 = this.ctx.getRootNode().addNode("slNode1");
    //        this.slNode2 = this.slNode1.addNode("slNode2");
    //        this.slNode3 = this.slNode2.addNode("slNode3");
    //        this.configuration = ConfigurationExamples.createOslValidConfiguration("JcrDetailedLogTest");
    //        this.configurationManager.save(this.configuration);
    //        this.configuration = this.configurationManager.load(LazyType.EAGER);
    //        this.group = this.configuration.getRepositoryByName("OSL Group").getGroupByName("OSL Root Group");
    //        this.shouldLogSomeInformation();
    //    }
    //
    //    @Test
    //    public void shouldLogSomeInformation() throws Exception {
    //        if (!logged) {
    //            this.logger.log(LogEventType.INFO, "hey there!", this.slNode3, this.group);
    //            this.logger.log(LogEventType.INFO, "Yeah!", this.group);
    //            this.logger.log(LogEventType.INFO, "Yeah!", this.slNode3);
    //            logged = true;
    //        }
    //    }
    //
    //    @Ignore
    //    @SuppressWarnings( "boxing" )
    //    @Test
    //    public void shouldRetrieveLogInformationByAnotherLogableObject() throws Exception {
    //        final List<LogEntry> result = this.logger.findLogByLogableObject(this.slNode1);
    //        assertThat(result.size(), is(2));
    //        assertThat(result.get(0).getType(), is(LogEventType.INFO));
    //        assertThat(result.get(0).getNodes().size(), is(not(0)));
    //        assertThat(result.get(0).getNodes().size(), is(not(1)));
    //    }
    //
    //    @SuppressWarnings( "boxing" )
    //    @Test
    //    public void shouldRetrieveLogInformationByDateInterval() throws Exception {
    //        final List<LogEntry> result = this.logger.findLogByDateInterval(new DateTime("2000-01-01").toDate(),
    //                                                                        new DateTime("2010-01-01").toDate());
    //        assertThat(result.size(), is(3));
    //
    //    }
    //
    //    @SuppressWarnings( "boxing" )
    //    @Test
    //    public void shouldRetrieveLogInformationByErrorCode() throws Exception {
    //        final List<LogEntry> result = this.logger.findLogByErrorCode(ErrorCode.NO_ERROR_CODE);
    //        assertThat(result.size(), is(3));
    //
    //    }
    //
    //    @SuppressWarnings( "boxing" )
    //    @Test
    //    public void shouldRetrieveLogInformationByLogableObject() throws Exception {
    //        final List<LogEntry> result = this.logger.findLogByLogableObject(this.group);
    //        assertThat(result.size(), is(2));
    //        assertThat(result.get(0).getType(), is(LogEventType.INFO));
    //        assertThat(result.get(0).getNodes().size(), is(not(0)));
    //        assertThat(result.get(0).getNodes().size(), is(not(1)));
    //    }
    //
    //    @SuppressWarnings( "boxing" )
    //    @Test
    //    public void shouldRetrieveLogInformationByLogEventType() throws Exception {
    //        final List<LogEntry> result = this.logger.findLogByEventType(LogEventType.INFO);
    //        assertThat(result.size(), is(3));
    //
    //    }
    //
    //    @After
    //    public void shutdown() throws Exception {
    //
    //        if (this.graphSession != null) {
    //            this.graphSession.close();
    //        }
    //        if (this.session != null) {
    //            this.session.logout();
    //        }
    //    }
}
