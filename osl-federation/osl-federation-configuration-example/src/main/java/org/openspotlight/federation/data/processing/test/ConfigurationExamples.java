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
 * OpenSpotLight - Plataforma de Governan�a de TI de C—digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui�‹o de direito autoral declarada e atribu’da pelo autor.
 * Todas as contribui�›es de terceiros est‹o distribu’das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa Ž software livre; voc� pode redistribu’-lo e/ou modific‡-lo sob os 
 * termos da Licen�a Pœblica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa Ž distribu’do na expectativa de que seja œtil, porŽm, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl’cita de COMERCIABILIDADE OU ADEQUA‚ÌO A UMA
 * FINALIDADE ESPECêFICA. Consulte a Licen�a Pœblica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c—pia da Licen�a Pœblica Geral Menor do GNU junto com este
 * programa; se n‹o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.federation.data.processing.test;

import org.openspotlight.federation.domain.ArtifactMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.DatabaseType;
import org.openspotlight.federation.domain.DbArtifactSource;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;

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
        repository.getGroups().put(group.getName(), group);
        group.setActive(true);
        final DbArtifactSource artifactSource = new DbArtifactSource();
        group.getArtifactSources().add(artifactSource);
        artifactSource.setGroup(group);
        artifactSource.setName(sourceName);
        artifactSource.setActive(true);
        artifactSource.setUser(user);
        artifactSource.setPassword(password);
        artifactSource.setMaxConnections(4);
        artifactSource.setType(type);
        artifactSource.setInitialLookup(initialLookup);
        artifactSource.setDriverClass(driver);

        final ArtifactMapping mapping = new ArtifactMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setRelative("*/");
        group.getMappings().add(mapping);
        mapping.getIncludeds().add("*");
        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.setActive(true);
        commonProcessor.setArtifactSource(artifactSource);
        commonProcessor.setType(LogPrinterBundleProcessor.class);
        artifactSource.getBundleProcessorTypes().add(commonProcessor);
        final BundleProcessorType customProcessor = new BundleProcessorType();
        customProcessor.setActive(true);
        customProcessor.setArtifactSource(artifactSource);
        artifactSource.getBundleProcessorTypes().add(customProcessor);
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
        return createDatabaseRepository("mysql Repository", "mysql Group", "mysql Connection", "root", null, DatabaseType.MYSQL,
                                        "jdbc:mysql://localhost:3306/test", "com.mysql.jdbc.Driver");
    }

    public static Repository createOracleOciDbConfiguration() {
        return createDatabaseRepository("oracle Repository", "oracle Group", "oracle Connection", "HR", "pass",
                                        DatabaseType.ORACLE, "jdbc:oracle:oci8:@orcl", "oracle.jdbc.driver.OracleDriver");
    }

    public static Repository createOslValidConfiguration( final String dirName ) throws Exception {
        throw new Exception("implement this");
        //        final String basePath = new File("../../../").getCanonicalPath() + "/";
        //        final Configuration configuration = new Configuration();
        //        final Repository oslRepository = new Repository(configuration, "OSL Group");
        //        configuration.setNumberOfParallelThreads(4);
        //        oslRepository.setActive(true);
        //        final Group oslRootProject = new Group(oslRepository, "OSL Root Group");
        //        oslRootProject.setActive(true);
        //        oslRootProject.setGraphRoot(Boolean.TRUE);
        //
        //        final Group oslCommonsProject = new Group(oslRootProject, "OSL Commons Library");
        //        oslCommonsProject.setActive(true);
        //        oslCommonsProject.setGraphRoot(Boolean.TRUE);
        //        final ArtifactSource oslCommonsJavaSourceBundle = new ArtifactSource(oslCommonsProject, "java source for OSL Bundle");
        //        oslCommonsJavaSourceBundle.setActive(true);
        //        oslCommonsJavaSourceBundle.setInitialLookup(basePath);
        //        final ArtifactMapping oslCommonsArtifactMapping = new ArtifactMapping(oslCommonsJavaSourceBundle, "osl-common/");
        //        final Included oslCommonsIncludedJavaFilesForSrcMainJava = new Included(oslCommonsArtifactMapping,
        //                                                                                "src/main/java/**/*.java");
        //        final Included oslCommonsIncludedJavaFilesForSrcTestJava = new Included(oslCommonsArtifactMapping,
        //                                                                                "src/test/java/**/*.java");
        //        final BundleProcessorType oslCommonProcessor = new BundleProcessorType(oslCommonsJavaSourceBundle,
        //                                                                               "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        //        oslCommonProcessor.setActive(true);
        //        final Group oslFederationProject = new Group(oslRootProject, "OSL Federation Library");
        //        oslFederationProject.setActive(true);
        //
        //        oslFederationProject.setGraphRoot(Boolean.TRUE);
        //        final ArtifactSource oslFederationJavaSourceBundle = new JavaArtifactSource(oslFederationProject,
        //                                                                                    "java source for OSL Bundle");
        //        oslFederationJavaSourceBundle.setActive(true);
        //        final BundleProcessorType oslFederationProcessor = new BundleProcessorType(oslFederationJavaSourceBundle,
        //                                                                                   "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        //        oslFederationProcessor.setActive(true);
        //
        //        oslFederationJavaSourceBundle.setInitialLookup(basePath);
        //        final ArtifactMapping oslFederationArtifactMapping = new ArtifactMapping(oslFederationJavaSourceBundle,
        //                                                                                 "osl-federation/loader/osl-federation-filesystem-loader/");
        //        final Included oslFederationIncludedJavaFilesForSrcMainJava = new Included(oslFederationArtifactMapping,
        //                                                                                   "src/main/java/**/*.java");
        //        final Included oslFederationIncludedJavaFilesForSrcTestJava = new Included(oslFederationArtifactMapping,
        //                                                                                   "src/test/java/**/*.java");
        //
        //        final Group oslGraphProject = new Group(oslRootProject, "OSL Graph Library");
        //        oslGraphProject.setActive(true);
        //        oslGraphProject.setGraphRoot(Boolean.TRUE);
        //        final ArtifactSource oslGraphJavaSourceBundle = new JavaArtifactSource(oslGraphProject, "java source for OSL Bundle");
        //        oslGraphJavaSourceBundle.setActive(true);
        //        oslGraphJavaSourceBundle.setInitialLookup(basePath);
        //        final ArtifactMapping oslGraphArtifactMapping = new ArtifactMapping(oslGraphJavaSourceBundle, "osl-graph/");
        //        final Included oslGraphIncludedJavaFilesForSrcMainJava = new Included(oslGraphArtifactMapping, "src/main/java/**/*.java");
        //        new File("./target/test-data/" + dirName + "/").mkdirs();
        //        final Included oslGraphIncludedJavaFilesForSrcTestJava = new Included(oslGraphArtifactMapping, "src/test/java/**/*.java");
        //        final BundleProcessorType oslGraphProcessor = new BundleProcessorType(oslGraphJavaSourceBundle,
        //                                                                              "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        //        oslGraphProcessor.setActive(true);
        //        return configuration;
    }

    public static Repository createOslValidDnaFileConnectorConfiguration( final String dirName ) throws Exception {
        throw new Exception("implement this");

        //        
        //        final String basePath = new File("../../../").getCanonicalPath() + "/";
        //        final Configuration configuration = new Configuration();
        //        final Repository oslRepository = new Repository(configuration, "OSL Group");
        //        configuration.setNumberOfParallelThreads(4);
        //        oslRepository.setActive(true);
        //        final Group oslRootProject = new Group(oslRepository, "OSL Root Group");
        //        oslRootProject.setActive(true);
        //        oslRootProject.setGraphRoot(Boolean.TRUE);
        //
        //        final Group oslCommonsProject = new Group(oslRootProject, "OSL Commons Library");
        //        oslCommonsProject.setActive(true);
        //        final ArtifactSource oslCommonsJavaSourceBundle = new DnaFileArtifactSource(oslCommonsProject,
        //                                                                                    "java source for OSL Bundle");
        //        oslCommonsJavaSourceBundle.setActive(true);
        //        oslCommonsJavaSourceBundle.setInitialLookup(basePath);
        //        final ArtifactMapping oslCommonsArtifactMapping = new ArtifactMapping(oslCommonsJavaSourceBundle, "osl-common/");
        //        final Included oslCommonsIncludedJavaFilesForSrcMainJava = new Included(oslCommonsArtifactMapping,
        //                                                                                "src/main/java/**/*.java");
        //        final Included oslCommonsIncludedJavaFilesForSrcTestJava = new Included(oslCommonsArtifactMapping,
        //                                                                                "src/test/java/**/*.java");
        //        final BundleProcessorType oslCommonProcessor = new BundleProcessorType(oslCommonsJavaSourceBundle,
        //                                                                               "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        //        oslCommonProcessor.setActive(true);
        //
        //        final Group oslGraphProject = new Group(oslRootProject, "OSL Graph Library");
        //        oslGraphProject.setActive(true);
        //        final ArtifactSource oslGraphJavaSourceBundle = new JavaArtifactSource(oslGraphProject, "java source for OSL Bundle");
        //        oslGraphJavaSourceBundle.setActive(true);
        //        oslGraphJavaSourceBundle.setInitialLookup(basePath);
        //        final ArtifactMapping oslGraphArtifactMapping = new ArtifactMapping(oslGraphJavaSourceBundle, "osl-graph/");
        //        final Included oslGraphIncludedJavaFilesForSrcMainJava = new Included(oslGraphArtifactMapping, "src/main/java/**/*.java");
        //        new File("./target/test-data/" + dirName + "/").mkdirs();
        //        final Included oslGraphIncludedJavaFilesForSrcTestJava = new Included(oslGraphArtifactMapping, "src/test/java/**/*.java");
        //        final BundleProcessorType oslGraphProcessor = new BundleProcessorType(oslGraphJavaSourceBundle,
        //                                                                              "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        //        oslGraphProcessor.setActive(true);
        //        return configuration;
    }

    public static Repository createOslValidDnaSvnConnectorConfiguration( final String dirName ) throws Exception {
        throw new Exception("implement this");
        //        final Configuration configuration = new Configuration();
        //        final Repository oslRepository = new Repository(configuration, "Hamcrest Repository");
        //        configuration.setNumberOfParallelThreads(4);
        //        oslRepository.setActive(true);
        //        final Group oslRootProject = new Group(oslRepository, "Hamcrest Group");
        //        oslRootProject.setActive(true);
        //        final Group oslCommonsProject = new Group(oslRootProject, "Hamcrest SubProject");
        //        oslCommonsProject.setGraphRoot(Boolean.TRUE);
        //
        //        oslCommonsProject.setActive(true);
        //        final DnaSvnArtifactSource bundle = new DnaSvnArtifactSource(oslCommonsProject, "Svn bundle");
        //
        //        bundle.setInitialLookup("http://hamcrest.googlecode.com/svn/trunk/hamcrest-java/");
        //        bundle.setUser("anonymous");
        //        bundle.setPassword("");
        //        bundle.setActive(true);
        //        final ArtifactMapping artifactMapping = new ArtifactMapping(bundle, "src/");
        //        new Included(artifactMapping, "**/*.java");
        //
        //        final BundleProcessorType oslCommonProcessor = new BundleProcessorType(bundle,
        //                                                                               "org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
        //        oslCommonProcessor.setActive(true);
        //
        //        return configuration;
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
