/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.federation.processing.internal;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.LocalSourceArtifactFinderByRepositoryProviderFactory;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory;
import org.openspotlight.federation.processing.BundleProcessorManagerImpl;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class BundleProcessorManagerTest {

	private static DefaultJcrDescriptor descriptor;
	private static DetailedJcrLoggerFactory loggerFactory;
	private static AuthenticatedUser user;
	private static GlobalSettings settings;
	private static BundleProcessorManagerImpl bundleProcessor;
	private static ArtifactSource source;
	private static LocalSourceArtifactFinderByRepositoryProviderFactory artifactFinderFactory;

	@BeforeClass
	public static void setupResources() throws Exception {

		ExampleBundleProcessor.allStatus.clear();
		BundleProcessorManagerTest.descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
		BundleProcessorManagerTest.bundleProcessor = BundleProcessorManagerImpl.INSTANCE;

		BundleProcessorManagerTest.source = new ArtifactSource();
		BundleProcessorManagerTest.source.setName("classpath");
		BundleProcessorManagerTest.source
				.setInitialLookup("../../../OpenSpotLight");

		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		BundleProcessorManagerTest.user = securityFactory
				.createIdentityManager(BundleProcessorManagerTest.descriptor)
				.authenticate(simpleUser, "password");
		BundleProcessorManagerTest.settings = new GlobalSettings();
		BundleProcessorManagerTest.settings
				.setDefaultSleepingIntervalInMilliseconds(1000);
		BundleProcessorManagerTest.settings.setNumberOfParallelThreads(8);
		BundleProcessorManagerTest.loggerFactory = new DetailedJcrLoggerFactory(
				BundleProcessorManagerTest.descriptor);
		BundleProcessorManagerTest.artifactFinderFactory = new LocalSourceArtifactFinderByRepositoryProviderFactory(
				BundleProcessorManagerTest.source, false);
	}

	@Test
	public void shouldProcessMappedArtifacts() throws Exception {
		final Repository repository = new Repository();
		repository.setActive(true);
		repository.setName("repository");
		final Group group = new Group();
		group.setActive(true);
		group.setName("Group name");
		group.setRepository(repository);
		repository.getGroups().add(group);
		final BundleProcessorType bundleType = new BundleProcessorType();
		bundleType.setActive(true);
		bundleType.setGroup(group);
		bundleType.setType(ExampleBundleProcessor.class);
		group.getBundleTypes().add(bundleType);
		final BundleSource bundleSource = new BundleSource();
		bundleType.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(bundleType);
		bundleSource.setRelative("/osl-federation");
		bundleSource.getIncludeds().add("**/*.java");

		BundleProcessorManagerTest.bundleProcessor.executeBundles(
				BundleProcessorManagerTest.user,
				BundleProcessorManagerTest.descriptor,
				BundleProcessorManagerTest.settings,
				BundleProcessorManagerTest.artifactFinderFactory,
				BundleProcessorManagerTest.loggerFactory, repository);
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.ERROR), Is.is(false));
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
				.is(false));
	}

}
