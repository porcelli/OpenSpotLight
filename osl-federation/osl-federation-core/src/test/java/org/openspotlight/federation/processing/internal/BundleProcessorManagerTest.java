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
import org.junit.Test;
import org.openspotlight.federation.context.TestExecutionContextFactory;
import org.openspotlight.federation.context.TestExecutionContextFactory.ArtifactFinderType;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.processing.BundleProcessorManagerImpl;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;

public class BundleProcessorManagerTest {

	@Test
	public void shouldProcessMappedArtifacts() throws Exception {
		ExampleBundleProcessor.allStatus.clear();

		final ArtifactSource source = new ArtifactSource();
		source.setName("classpath");
		source.setInitialLookup("../../../OpenSpotLight");

		final GlobalSettings settings = new GlobalSettings();
		settings.setDefaultSleepingIntervalInMilliseconds(1000);
		settings.setNumberOfParallelThreads(8);
		final Repository repository = new Repository();
		repository.setActive(true);
		repository.setName("repository");
		source.setRepository(repository);
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

		BundleProcessorManagerImpl.INSTANCE.executeBundles("username",
				"password", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
				TestExecutionContextFactory.createFactory(
						ArtifactFinderType.FILESYSTEM, source), settings,
				repository);
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.ERROR), Is.is(false));
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
				.is(false));
	}

}
