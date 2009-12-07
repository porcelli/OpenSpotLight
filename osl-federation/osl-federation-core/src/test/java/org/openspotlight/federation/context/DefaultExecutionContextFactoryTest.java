package org.openspotlight.federation.context;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.LogEventType;

public class DefaultExecutionContextFactoryTest {

	private ExecutionContext context;

	private final ExecutionContextFactory factory = DefaultExecutionContextFactory
			.createFactory();

	@After
	public void closeResources() throws Exception {
		factory.closeResources();
	}

	@Before
	public void setupContext() throws Exception {
		context = factory.createExecutionContext("testUser", "testPassword",
				DefaultJcrDescriptor.TEMP_DESCRIPTOR, "test");
	}

	@Test
	public void shouldUseAllResourcesInsideContext() throws Exception {
		final ArtifactFinder<StreamArtifact> streamArtifactFinder = context
				.getArtifactFinder(StreamArtifact.class);
		Assert.assertThat(streamArtifactFinder, Is.is(IsNull.notNullValue()));
		final ArtifactFinder<TableArtifact> tableArtifactFinder = context
				.getArtifactFinder(TableArtifact.class);
		Assert.assertThat(tableArtifactFinder, Is.is(IsNull.notNullValue()));
		final ConfigurationManager configurationManager = context
				.getDefaultConfigurationManager();
		Assert.assertThat(configurationManager, Is.is(IsNull.notNullValue()));
		final JcrConnectionProvider connectionProvider = context
				.getDefaultConnectionProvider();
		Assert.assertThat(connectionProvider, Is.is(IsNull.notNullValue()));
		final SLGraphSession graphSession = context.getGraphSession();
		Assert.assertThat(graphSession, Is.is(IsNull.notNullValue()));
		final DetailedLogger logger = context.getLogger();
		Assert.assertThat(logger, Is.is(IsNull.notNullValue()));
		streamArtifactFinder.findByPath("/tmp");
		tableArtifactFinder.findByPath("/tmp");

		configurationManager.saveGlobalSettings(new GlobalSettings());

		graphSession.createContext("new context");

		logger.log(context.getUser(), LogEventType.DEBUG, "test");

	}

}
