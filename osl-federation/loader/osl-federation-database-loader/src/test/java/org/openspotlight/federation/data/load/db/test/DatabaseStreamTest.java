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
import org.junit.Test;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.DatabaseStreamArtifactFinder;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.federation.finder.PersistentArtifactManagerProviderImpl;
import org.openspotlight.federation.finder.db.ScriptType;
import org.openspotlight.federation.loader.ArtifactLoaderManager;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.finder.db.DatabaseSupport.createConnection;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * This test is intended to be used to test scripts to retrieve stream artifacts for a given {@link }. Most of the environments
 * used to run <code>mvn clean install</code> would not have all the database types. But there's a need to have a test for each
 * type on the source code. On this cases, the tests will be annotated with {@link } annotation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "all" )
public abstract class DatabaseStreamTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void cleanFiles() throws Exception {
        delete("./target/test-data/" + this.getClass().getSimpleName() + "/");
    }

    /**
     * Here a valid configuration to connect on the target database should be created. The necessary data to be created here are
     * the database connection and also the artifact mappings to load all artifacts been tested for a given type.
     * 
     * @return a valid database configuration
     */
    protected abstract DbArtifactSource createValidConfigurationWithMappings();

    /**
     * Fill the data necessary to run the database tests. For example, here it could be created procedure, triggers and so on.
     * 
     * @param conn
     * @throws Exception
     */
    protected void fillDatabase( final Connection conn ) throws Exception {
        //
    }

    /**
     * Here's an option to reset all filled data on the database.
     * 
     * @param conn
     * @throws Exception
     */
    protected void resetDatabase( final Connection conn ) throws Exception {
        //
    }

    ;

    /**
     * This test method will load all artifacts from the configuration and assert if all artifacts of the {@link #typesToAssert()}
     * are loaded.
     * 
     * @throws Exception
     */
    @Test
    public void shouldLoadAllValidTypes() throws Exception {

        if (this instanceof RunWhenDatabaseVendorTestsIsActive) {
            if ("true".equals(System.getProperty("runDatabaseVendorTests"))) {
                validateAllTypes();
            } else {
                logger.warn(format("Ignoring test {0} because system property {1} isn't set to true.",
                                   this.getClass().getSimpleName(), "runDatabaseVendorTests"));
            }
        } else {
            validateAllTypes();
        }

    }

    /**
     * @return the types to load during the test.
     */
    protected abstract Set<ScriptType> typesToAssert();

    private void validateAllTypes() throws Exception {
        final DbArtifactSource bundle = createValidConfigurationWithMappings();
        Connection conn = createConnection(bundle);
        fillDatabase(conn);
        if (!conn.isClosed()) {
            conn.close();
        }
        final GlobalSettings configuration = new GlobalSettings();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);
        configuration.getLoaderRegistry().add(DatabaseStreamArtifactFinder.class);

        conn = createConnection(bundle);
        resetDatabase(conn);
        if (!conn.isClosed()) {
            conn.close();
        }

        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                                                                         ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                                                                         repositoryPath("repository")),
                                                 new SimplePersistModule(), new DetailedLoggerModule(),
                                                 new DefaultExecutionContextFactoryModule());
        injector.getInstance(JRedisFactory.class).getFrom(SLPartition.GRAPH).flushall();

        SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist = injector.getInstance(SimplePersistFactory.class).createSimplePersist(
                                                                                                                                                 SLPartition.FEDERATION);

        PersistentArtifactManagerProvider provider = new PersistentArtifactManagerProviderImpl(
                                                                                               injector.getInstance(SimplePersistFactory.class),
                                                                                               bundle.getRepository());

        ArtifactLoaderManager.INSTANCE.refreshResources(configuration, bundle, provider);

        Set<StringArtifact> loadedArtifacts = provider.get().listByPath(StringArtifact.class, null);
        final Set<String> failMessages = new HashSet<String>();
        lookingTypes: for (final ScriptType typeToAssert : typesToAssert()) {
            for (final Artifact artifact : loadedArtifacts) {
                final StringArtifact streamArtifact = (StringArtifact)artifact;
                final String relativeName = streamArtifact.getArtifactCompleteName();
                if (relativeName.contains(typeToAssert.name())) {
                    assertThat(streamArtifact.getContent(), is(notNullValue()));
                    continue lookingTypes;
                }
            }
            failMessages.add(format("Type {0} was not found in any of strings: {1}", //$NON-NLS-1$
                                    typeToAssert, loadedArtifacts));
        }
        if (!failMessages.isEmpty()) {
            fail(failMessages.toString());
        }
        for (final Artifact artifact : loadedArtifacts) {
            final StringArtifact streamArtifact = (StringArtifact)artifact;
            final String name = "./target/test-data/" + this.getClass().getSimpleName() + "/"
                                + streamArtifact.getArtifactCompleteName().replaceAll(" ", "");// DB2 has
            // some
            // spaces
            final String dirName = name.substring(0, name.lastIndexOf('/'));
            new File(dirName).mkdirs();
            final OutputStream fos = new BufferedOutputStream(new FileOutputStream(name + ".sql"));
            final InputStream is = new ByteArrayInputStream(streamArtifact.getContent().get(simplePersist).getBytes());
            while (true) {
                final int data = is.read();
                if (data == -1) {
                    break;
                }
                fos.write(data);
            }
            fos.flush();
            fos.close();
        }

    }

}
