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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.artifact.db.*;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinder;
import org.openspotlight.federation.finder.DatabaseCustomArtifactFinder.Constraints;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.federation.finder.PersistentArtifactManagerProviderImpl;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.sql.Connection;
import java.util.Set;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

@SuppressWarnings("all")
public class DatabaseCustomTest {

    @BeforeClass
    public static void loadDriver() throws Exception {
        Class.forName("org.h2.Driver");
    }

    private DatabaseCustomArtifactFinder finder;
    private DbArtifactSource bundle;
    private PersistentArtifactManagerProviderImpl persistentManagerProvider;

    @Before
    public void setup() throws Exception {
        delete("./target/test-data"); //$NON-NLS-1$

        final GlobalSettings configuration = new GlobalSettings();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);

        bundle = (DbArtifactSource) createH2DbConfiguration("DatabaseArtifactLoaderTest").getArtifactSources().iterator().next();
        bundle.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        finder = new DatabaseCustomArtifactFinder();

        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(),
                new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

        PersistentArtifactManagerProvider provider = new PersistentArtifactManagerProviderImpl(
                injector.getInstance(SimplePersistFactory.class),
                bundle.getRepository());

    }

    @Test
    public void shouldLoadProceduresAndFunctions() throws Exception {
        Connection connection = getConnection("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db", "sa", "");

        connection.prepareStatement(
                "create alias newExampleFunction for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.increment\" ").execute();
        connection.prepareStatement(
                "create alias newExampleProcedure for \"org.openspotlight.federation.data.load.db.test.StaticFunctions.flagProcedure\"").execute();
        connection.commit();
        connection.close();

        final RoutineArtifact exampleProcedure = (RoutineArtifact) finder.findByPath(RoutineArtifact.class, bundle,
                "/PUBLIC/PROCEDURE/DB/NEWEXAMPLEPROCEDURE",null);
        final RoutineArtifact exampleFunction = (RoutineArtifact) finder.findByPath(RoutineArtifact.class, bundle,
                "/PUBLIC/FUNCTION/DB/NEWEXAMPLEFUNCTION",null);
        assertThat(exampleProcedure.getType(), is(RoutineType.PROCEDURE));
        assertThat(exampleFunction.getType(), is(RoutineType.FUNCTION));
        connection = getConnection("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db", "sa", "");

        connection.prepareStatement("drop alias newExampleProcedure ").execute();
        connection.prepareStatement("drop alias newExampleFunction ").execute();
        connection.commit();
        connection.close();

    }

    @Test
    public void shouldLoadTablesAndViews() throws Exception {
        final DbArtifactSource bundle = (DbArtifactSource) createH2DbConfiguration("DatabaseArtifactLoaderTest").getArtifactSources().iterator().next();
        bundle.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        final Connection connection = getConnection("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db",
                "sa", "");
        bundle.setInitialLookup("jdbc:h2:./target/test-data/DatabaseArtifactLoaderTest/h2/inclusions/db");
        connection.prepareStatement(
                "create table exampleTable(i int not null primary key, last_i_plus_2 int, s smallint, f float, dp double precision, v varchar(10) not null)").execute();
        connection.prepareStatement(
                "create view exampleView (s_was_i, dp_was_s, i_was_f, f_was_dp) as select i,s,f,dp from exampleTable").execute();
        connection.prepareStatement("create table anotherTable(i int not null primary key, i_fk int)").execute();

        connection.prepareStatement(
                "alter table anotherTable add constraint example_fk foreign key(i_fk) references exampleTable(i)").execute();

        connection.commit();
        connection.close();

        final TableArtifact exampleTable = finder.findByPath(TableArtifact.class, bundle, "/PUBLIC/DB/TABLE/EXAMPLETABLE",null);
        final TableArtifact exampleView = (TableArtifact) finder.findByPath(TableArtifact.class, bundle,
                "/PUBLIC/DB/VIEW/EXAMPLEVIEW",null);
        assertThat(exampleTable, is(TableArtifact.class));
        final Set<DatabaseCustomArtifact> pks = finder.listByPath(DatabaseCustomArtifact.class, bundle,
                Constraints.PRIMARY_KEY.toString(),null);
        final Set<DatabaseCustomArtifact> fks = finder.listByPath(DatabaseCustomArtifact.class, bundle,
                Constraints.FOREIGN_KEY.toString(),null);

        Set<String> names = finder.getInternalMethods().retrieveOriginalNames(DatabaseCustomArtifact.class, bundle, null);

        for (String name : names)
            System.err.println(name);

        assertThat(fks, is(notNullValue()));
        assertThat(fks.size(), is(not(0)));
        assertThat(pks, is(notNullValue()));
        assertThat(pks.size(), is(not(0)));
        assertThat(exampleView, is(ViewArtifact.class));
    }
}
