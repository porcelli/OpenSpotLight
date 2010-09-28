/**
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
package org.openspotlight.federation.data.load.db.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.bundle.domain.DbArtifactSource;
import org.openspotlight.bundle.domain.GlobalSettings;
import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.db.DatabaseCustomArtifact;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinder;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.federation.finder.PersistentArtifactManagerProviderImpl;
import org.openspotlight.federation.finder.db.DatabaseSupport;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * During a column changing, its table needs to be marked as changed also. This
 * test is to assert this behavior.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings("all")
public class ColumnChangingFiresTableChangeTest {

	@Before
	public void cleanDatabaseFiles() throws Exception {
		delete("./target/test-data/ColumnChangingFiresTableChangeTest"); //$NON-NLS-1$
	}

	@Test
	public void columnChangeShouldFireTableChange() throws Exception {

		final Repository repository = createH2DbConfiguration("ColumnChangingFiresTableChangeTest"); //$NON-NLS-1$

		Injector injector = Guice.createInjector(
				new JRedisStorageModule(StorageSession.FlushMode.AUTO,
						ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
						RepositoryPath.repositoryPath(repository.getName())),
				new SimplePersistModule(), new DetailedLoggerModule());
		injector.getInstance(JRedisFactory.class)
				.getFrom(RegularPartitions.FEDERATION).flushall();
		final DbArtifactSource dbBundle = (DbArtifactSource) repository
				.getArtifactSources().iterator().next(); //$NON-NLS-1$
		Connection conn = DatabaseSupport.createConnection(dbBundle);

		conn.prepareStatement(
				"newPair table EXAMPLE_TABLE_XXX(i int not null, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)") //$NON-NLS-1$
				.execute();
		conn.close();
		final GlobalSettings configuration = new GlobalSettings();
		configuration.setDefaultSleepingIntervalInMilliseconds(500);
		GlobalSettings globalSettings = new GlobalSettings();

		PersistentArtifactManagerProvider provider = new PersistentArtifactManagerProviderImpl(
				injector.getInstance(SimplePersistFactory.class),
				dbBundle.getRepository());

		refreshResources(globalSettings, dbBundle, provider);

		Iterable<DatabaseCustomArtifact> firstLoadedItems = provider.get()
				.listByInitialPath(DatabaseCustomArtifact.class, null);
		conn = DatabaseSupport.createConnection(dbBundle);

		conn.prepareStatement("drop table EXAMPLE_TABLE_XXX") //$NON-NLS-1$
				.execute();

		conn.prepareStatement(
				"newPair table EXAMPLE_TABLE_XXX(changed_columns int not null)") //$NON-NLS-1$
				.execute();
		conn.close();
		refreshResources(globalSettings, dbBundle, provider);

		Iterable<DatabaseCustomArtifact> lastLoadedItems = provider.get()
				.listByInitialPath(DatabaseCustomArtifact.class, null);
		conn = DatabaseSupport.createConnection(dbBundle);

		boolean found = false;
		all: for (final Artifact first : firstLoadedItems) {
			if (first.getArtifactName().equals("EXAMPLE_TABLE_XXX")) {
				assertThat(first.equals(first), is(true));
				assertThat(first.contentEquals(first), is(true));
				for (final Artifact last : lastLoadedItems) {
					if (last.getArtifactName().equals("EXAMPLE_TABLE_XXX")) {
						System.out.println("first:" + first.toString());
						System.out.println("last:" + last.toString());
						assertThat(last.equals(first), is(true));
						assertThat(last.contentEquals(first), is(false));
						found = true;
						break all;
					}
				}
			}
		}
		assertThat(found, is(true));
	}

	private void refreshResources(GlobalSettings globalSettings,
			DbArtifactSource dbBundle,
			PersistentArtifactManagerProvider provider) {
		// globalSettings.getLoaderRegistry().add(DatabaseCustomArtifactFinder.class);
		throw new UnsupportedOperationException();

	}

}
