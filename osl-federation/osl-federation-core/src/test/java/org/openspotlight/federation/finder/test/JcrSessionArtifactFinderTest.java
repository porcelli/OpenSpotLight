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
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.FileSystemStringArtifactFinder;
import org.openspotlight.federation.finder.JcrSessionArtifactFinder;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.support.SimplePersistSupport;

public class JcrSessionArtifactFinderTest {

	/** The provider. */
	private static JcrConnectionProvider provider;

	private static ArtifactSource artifactSource;

	private static Repository repository;

	/**
	 * Setup.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setup() throws Exception {
		provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final Session session = provider.openSession();
		artifactSource = new ArtifactSource();
		artifactSource.setName("classpath");
		artifactSource.setInitialLookup("./src");
		repository = new Repository();
		repository.setName("name");
		artifactSource.setRepository(repository);
		final FileSystemStringArtifactFinder fileSystemFinder = new FileSystemStringArtifactFinder(
				artifactSource);
		final Set<StringArtifact> artifacts = fileSystemFinder.listByPath(null);
		SimplePersistSupport.convertBeansToJcrs(JcrSessionArtifactFinder
				.getArtifactRootPathFor(repository), session, artifacts);
		session.save();
		session.logout();
	}

	private ArtifactFinder<StringArtifact> streamArtifactFinder;

	/** The session. */
	private Session session = null;

	/**
	 * Close session.
	 */
	@After
	public void closeSession() {
		streamArtifactFinder = null;
		if (session != null) {
			session.logout();
			session = null;
		}
	}

	/**
	 * Setup session.
	 */
	@Before
	public void setupSession() {
		session = provider.openSession();
		streamArtifactFinder = JcrSessionArtifactFinder.createArtifactFinder(
				StringArtifact.class, repository, session);
	}

	@Test
	public void shouldFindArtifacts() throws Exception {
		final StringArtifact sa = streamArtifactFinder
				.findByPath("/test/java/org/openspotlight/federation/finder/test/JcrSessionArtifactFinderTest.java");
		assertThat(sa, is(notNullValue()));
		assertThat(sa.getContent(), is(notNullValue()));

	}

	@Test
	public void shouldListArtifactNames() throws Exception {
		final Set<String> artifacts = streamArtifactFinder
				.retrieveAllArtifactNames(null);

		assertThat(artifacts, is(notNullValue()));
		assertThat(artifacts.size(), is(not(0)));
		for (final String s : artifacts) {
			assertThat(s, is(notNullValue()));
		}
	}

	@Test
	public void shouldListArtifacts() throws Exception {
		final Set<StringArtifact> artifacts = streamArtifactFinder
				.listByPath("/main/java/org/openspotlight/federation");

		assertThat(artifacts, is(notNullValue()));
		assertThat(artifacts.size(), is(not(0)));
		for (final StringArtifact sa : artifacts) {
			assertThat(sa, is(notNullValue()));
			assertThat(sa.getContent(), is(notNullValue()));
		}
	}

}
