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
package org.openspotlight.federation.data.load.db.test;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

import java.sql.Connection;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.db.DatabaseCustomArtifact;
import org.openspotlight.federation.domain.artifact.db.RoutineArtifact;
import org.openspotlight.federation.domain.artifact.db.RoutineType;
import org.openspotlight.federation.domain.artifact.db.TableArtifact;
import org.openspotlight.federation.domain.artifact.db.ViewArtifact;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinder;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinder.Constraints;
import org.openspotlight.federation.loader.ArtifactLoader;
import org.openspotlight.federation.loader.ArtifactLoaderFactory;

@SuppressWarnings("all")
public class DatabaseCustomTest {

	@BeforeClass
	public static void loadDriver() throws Exception {
		Class.forName("org.h2.Driver");
	}

	private ArtifactLoader artifactLoader;
	private DatabaseCustomArtifactFinder finder;
	private DbArtifactSource bundle;

	@Before
	public void setup() throws Exception {
		delete("./target/test-data"); //$NON-NLS-1$

		final GlobalSettings configuration = new GlobalSettings();
		configuration
				.setArtifactFinderRegistryClass(SampleDatabaseCustomArtifactRegistry.class);
		configuration.setDefaultSleepingIntervalInMilliseconds(500);
		configuration.setNumberOfParallelThreads(1);

		artifactLoader = ArtifactLoaderFactory.createNewLoader(configuration);
		bundle = (DbArtifactSource) createH2DbConfiguration(
				"DatabaseArtifactLoaderTest").getArtifactSources().iterator()
				.next();
		bundle
				.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
		finder = new DatabaseCustomArtifactFinder(bundle);
	}

	@Test
	public void shouldLoadProceduresAndFunctions() throws Exception {
		Connection connection = getConnection(
				"jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
				"sa", "");

		connection
				.prepareStatement(
						"create alias newExampleFunction for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.increment\" ")
				.execute();
		connection
				.prepareStatement(
						"create alias newExampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"")
				.execute();
		connection.commit();
		connection.close();

		final Iterable<Artifact> loadedArtifacts = artifactLoader
				.loadArtifactsFromSource(bundle);
		assertThat(loadedArtifacts, is(notNullValue()));
		assertThat(loadedArtifacts.iterator().hasNext(), is(true));

		final RoutineArtifact exampleProcedure = (RoutineArtifact) finder
				.findByPath("PUBLIC/PROCEDURE/DB/NEWEXAMPLEPROCEDURE");
		final RoutineArtifact exampleFunction = (RoutineArtifact) finder
				.findByPath("PUBLIC/FUNCTION/DB/NEWEXAMPLEFUNCTION");
		assertThat(exampleProcedure.getType(), is(RoutineType.PROCEDURE));
		assertThat(exampleFunction.getType(), is(RoutineType.FUNCTION));
		connection = getConnection(
				"jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
				"sa", "");

		connection.prepareStatement("drop alias newExampleProcedure ")
				.execute();
		connection.prepareStatement("drop alias newExampleFunction ").execute();
		connection.commit();
		connection.close();

	}

	@Test
	public void shouldLoadTablesAndViews() throws Exception {
		final DbArtifactSource bundle = (DbArtifactSource) createH2DbConfiguration(
				"DatabaseArtifactLoaderTest").getArtifactSources().iterator()
				.next();
		bundle
				.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
		final Connection connection = getConnection(
				"jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
				"sa", "");
		bundle
				.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
		connection
				.prepareStatement(
						"create table exampleTable(i int not null primary key, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)")
				.execute();
		connection
				.prepareStatement(
						"create view exampleView (s_was_i, dp_was_s, i_was_f, f_was_dp) as select i,s,f,dp from exampleTable")
				.execute();
		connection
				.prepareStatement(
						"create table anotherTable(i int not null primary key, i_fk int)")
				.execute();

		connection
				.prepareStatement(
						"alter table anotherTable add constraint example_fk foreign key(i_fk) references exampleTable(i)")
				.execute();

		connection.commit();
		connection.close();

		final Iterable<Artifact> loadedArtifacts = artifactLoader
				.loadArtifactsFromSource(bundle);
		final TableArtifact exampleTable = (TableArtifact) finder
				.findByPath("PUBLIC/DB/TABLE/EXAMPLETABLE");
		final TableArtifact exampleView = (TableArtifact) finder
				.findByPath("PUBLIC/DB/VIEW/EXAMPLEVIEW");
		assertThat(exampleTable, is(TableArtifact.class));
		final Set<DatabaseCustomArtifact> pks = finder
				.listByPath(Constraints.PRIMARY_KEY.toString());
		final Set<DatabaseCustomArtifact> fks = finder
				.listByPath(Constraints.FOREIGN_KEY.toString());

		assertThat(loadedArtifacts, is(notNullValue()));
		assertThat(loadedArtifacts.iterator().hasNext(), is(true));

		assertThat(fks, is(notNullValue()));
		assertThat(fks.size(), is(not(0)));
		assertThat(pks, is(notNullValue()));
		assertThat(pks.size(), is(not(0)));
		assertThat(exampleView, is(ViewArtifact.class));
	}
}
