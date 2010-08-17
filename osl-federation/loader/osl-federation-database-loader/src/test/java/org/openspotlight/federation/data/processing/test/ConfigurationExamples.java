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

package org.openspotlight.federation.data.processing.test;

import org.openspotlight.federation.domain.*;
import org.openspotlight.federation.domain.artifact.db.DatabaseType;

/**
 * Class with some example valid configurations
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "all" )
public class ConfigurationExamples {

    private static Repository createDatabaseRepository( final String repositoryName,
                                                        final String groupName,
                                                        final String sourceName,
                                                        final String user,
                                                        final String password,
                                                        final DatabaseType type,
                                                        final String initialLookup,
                                                        final String driver ) {
        final Repository repository = new Repository();
        repository.setName(repositoryName);
        repository.setActive(true);
        final Group group = new Group();
        group.setName(groupName);
        group.setRepository(repository);
        repository.getGroups().add(group);
        group.setActive(true);
        final DbArtifactSource artifactSource = new DbArtifactSource();
        artifactSource.setServerName("server name " + sourceName);
        artifactSource.setDatabaseName("db name " + sourceName);
        repository.getArtifactSources().add(artifactSource);
        artifactSource.setRepository(repository);
        artifactSource.setName(sourceName);
        artifactSource.setActive(true);
        artifactSource.setUser(user);
        artifactSource.setPassword(password);
        artifactSource.setMaxConnections(4);
        artifactSource.setType(type);
        artifactSource.setInitialLookup(initialLookup);
        artifactSource.setDriverClass(driver);

        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setFrom("/PUBLIC");
        mapping.setTo("/databaseArtifacts");
        artifactSource.getMappings().add(mapping);
        mapping.getIncludeds().add("**");
        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.setActive(true);
        commonProcessor.setGroup(group);
        group.getBundleTypes().add(commonProcessor);
        final BundleProcessorType customProcessor = new BundleProcessorType();
        customProcessor.setActive(true);
        customProcessor.setGroup(group);
        group.getBundleTypes().add(customProcessor);
        return repository;
    }

    public static Repository createDb2Configuration() {
        return createDatabaseRepository("db2 Repository", "db2 Group", "db2 Connection", "db2admin", "db2admin",
                                        DatabaseType.DB2, "jdbc:db2://localhost:50000/SAMPLE", "com.ibm.db2.jcc.DB2Driver");
    }

    public static Repository createH2DbConfiguration( final String dirName ) {
        return createDatabaseRepository("H2 Repository", "h2 Group", "H2 Connection", "sa", null, DatabaseType.H2,
                                        "jdbc:h2:./target/test-data/" + dirName + "/h2/db", "org.h2.Driver");
    }

    public static Repository createMySqlDbConfiguration() {
        return createDatabaseRepository("mysql Repository", "mysql Group", "mysql Connection", "root", null, DatabaseType.MY_SQL,
                                        "jdbc:mysql://localhost:3306/test", "com.mysql.jdbc.Driver");
    }

    public static Repository createOracleOciDbConfiguration() {
        return createDatabaseRepository("oracle Repository", "oracle Group", "oracle Connection", "HR", "pass",
                                        DatabaseType.ORACLE, "jdbc:oracle:oci8:@orcl", "oracle.jdbc.driver.OracleDriver");
    }

    public static Repository createPostgresqlConfiguration() {
        return createDatabaseRepository("postgresql Repository", "postgresql Group", "postgresql Connection", "postgres",
                                        "postgres", DatabaseType.POSTGRES, "jdbc:postgresql://localhost:5432/osl?charSet=UTF8",
                                        "org.postgresql.Driver");
    }

    public static Repository createSqlServerDbConfiguration() {
        return createDatabaseRepository("sqlserver Repository", "sqlserver Group", "sqlserver Connection", "sa", null,
                                        DatabaseType.SQL_SERVER, "jdbc:jtds:sqlserver://localhost:49385",
                                        "net.sourceforge.jtds.jdbc.Driver");
    }

}
