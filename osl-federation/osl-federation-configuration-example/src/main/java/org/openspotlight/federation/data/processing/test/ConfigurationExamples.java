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

import java.io.File;

import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.impl.DbBundle;
import org.openspotlight.federation.data.impl.DnaFileBundle;
import org.openspotlight.federation.data.impl.DnaSvnBundle;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.JavaBundle;
import org.openspotlight.federation.data.impl.Repository;

/**
 * Class with some example valid configurations
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class ConfigurationExamples {
	public static Configuration createDb2Configuration() {
		final Configuration configuration = new Configuration();
		final Repository db2Repository = new Repository(configuration,
				"db2 Repository");
		configuration.setNumberOfParallelThreads(4);
		db2Repository.setActive(true);
		final Group db2Project = new Group(db2Repository, "db2 Group");
		db2Project.setActive(true);
		final DbBundle db2Bundle = new DbBundle(db2Project, "db2 Connection");
		db2Bundle.setActive(true);
		db2Bundle.setUser("db2admin");
		db2Bundle.setPassword("db2admin");
		db2Bundle.setMaxConnections(4);
		db2Bundle.setType(DatabaseType.DB2);
		db2Bundle.setInitialLookup("jdbc:db2://localhost:50000/SAMPLE");
		db2Bundle.setDriverClass("com.ibm.db2.jcc.DB2Driver");

		final ArtifactMapping db2ArtifactMapping = new ArtifactMapping(
				db2Bundle, "DB2ADMIN/");
		final Included db2IncludedTrigger = new Included(db2ArtifactMapping,
				"TRIGGER/*");
		final Included db2IncludedProcedure = new Included(db2ArtifactMapping,
				"PROCEDURE/*");
		final Included db2IncludedTable = new Included(db2ArtifactMapping,
				"TABLE/*");
		final Included db2IncludedSequence = new Included(db2ArtifactMapping,
				"SEQUENCE/*");
		final Included db2IncludedConstraint = new Included(db2ArtifactMapping,
				"CONSTRAINT/*");
		final Included db2IncludedFunction = new Included(db2ArtifactMapping,
				"FUNCTION/*");
		final Included db2IncludedView = new Included(db2ArtifactMapping,
				"VIEW/*");
		final Included db2IncludedIndex = new Included(db2ArtifactMapping,
				"INDEX/*");
		final BundleProcessorType db2CommonProcessor = new BundleProcessorType(
				db2Bundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		final BundleProcessorType db2CustomProcessor = new BundleProcessorType(
				db2Bundle,
				"org.openspotlight.federation.data.processing.test.LogTableCustomArtifactProcessor");

		db2CommonProcessor.setActive(true);
		db2CustomProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createH2DbConfiguration(final String dirName) {
		final Configuration configuration = new Configuration();
		final Repository h2Repository = new Repository(configuration,
				"H2 Repository");
		configuration.setNumberOfParallelThreads(4);
		h2Repository.setActive(true);
		final Group h2Project = new Group(h2Repository, "h2 Group");
		h2Project.setActive(true);
		final DbBundle h2Bundle = new DbBundle(h2Project, "H2 Connection");
		h2Bundle.setActive(true);
		h2Bundle.setUser("sa");
		h2Bundle.setMaxConnections(1);
		h2Bundle.setType(DatabaseType.H2);
		h2Bundle.setInitialLookup("jdbc:h2:./target/test-data/" + dirName
				+ "/h2/db");
		h2Bundle.setDriverClass("org.h2.Driver");
		final ArtifactMapping h2ArtifactMapping = new ArtifactMapping(h2Bundle,
				"PUBLIC/");
		final Included h2IncludedTrigger = new Included(h2ArtifactMapping,
				"TRIGGER/**/*");
		final Included h2IncludedProcedure = new Included(h2ArtifactMapping,
				"PROCEDURE/**/*");
		final Included h2IncludedTable = new Included(h2ArtifactMapping,
				"TABLE/**/*");
		final Included h2IncludedFunction = new Included(h2ArtifactMapping,
				"FUNCTION/**/*");
		final Included h2IncludedView = new Included(h2ArtifactMapping,
				"VIEW/**/*");
		final Included h2IncludedIndex = new Included(h2ArtifactMapping,
				"INDEX/**/*");
		final BundleProcessorType h2CommonProcessor = new BundleProcessorType(
				h2Bundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		final BundleProcessorType h2CustomProcessor = new BundleProcessorType(
				h2Bundle,
				"org.openspotlight.federation.data.processing.test.LogTableCustomArtifactProcessor");

		h2CommonProcessor.setActive(true);
		h2CustomProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createMySqlDbConfiguration() {
		final Configuration configuration = new Configuration();
		final Repository mysqlRepository = new Repository(configuration,
				"mysql Repository");
		configuration.setNumberOfParallelThreads(4);
		mysqlRepository.setActive(true);
		final Group mysqlProject = new Group(mysqlRepository, "mysql Group");
		mysqlProject.setActive(true);
		final DbBundle mysqlBundle = new DbBundle(mysqlProject,
				"mysql Connection");
		mysqlBundle.setActive(true);
		mysqlBundle.setUser("root");
		mysqlBundle.setMaxConnections(1);
		mysqlBundle.setType(DatabaseType.MYSQL);
		mysqlBundle.setInitialLookup("jdbc:mysql://localhost:3306/test");
		mysqlBundle.setDriverClass("com.mysql.jdbc.Driver");
		final ArtifactMapping mysqlArtifactMapping = new ArtifactMapping(
				mysqlBundle, "*/");
		final Included mysqlIncludedTrigger = new Included(
				mysqlArtifactMapping, "TRIGGER/*");
		final Included mysqlIncludedProcedure = new Included(
				mysqlArtifactMapping, "PROCEDURE/*");
		final Included mysqlIncludedTable = new Included(mysqlArtifactMapping,
				"TABLE/*");
		final Included mysqlIncludedFunction = new Included(
				mysqlArtifactMapping, "FUNCTION/*");
		final Included mysqlIncludedView = new Included(mysqlArtifactMapping,
				"VIEW/*");
		final Included mysqlIncludedIndex = new Included(mysqlArtifactMapping,
				"INDEX/*");
		final BundleProcessorType mysqlCommonProcessor = new BundleProcessorType(
				mysqlBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		final BundleProcessorType mysqlCustomProcessor = new BundleProcessorType(
				mysqlBundle,
				"org.openspotlight.federation.data.processing.test.LogTableCustomArtifactProcessor");

		mysqlCommonProcessor.setActive(true);
		mysqlCustomProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createOracleOciDbConfiguration() {
		final Configuration configuration = new Configuration();
		final Repository oracleRepository = new Repository(configuration,
				"oracle Repository");
		configuration.setNumberOfParallelThreads(4);
		oracleRepository.setActive(true);
		final Group oracleProject = new Group(oracleRepository, "oracle Group");
		oracleProject.setActive(true);
		final DbBundle oracleBundle = new DbBundle(oracleProject,
				"oracle Connection");
		oracleBundle.setActive(true);
		oracleBundle.setUser("HR");
		oracleBundle.setPassword("jakadeed");
		oracleBundle.setMaxConnections(4);
		oracleBundle.setType(DatabaseType.ORACLE);
		oracleBundle.setInitialLookup("jdbc:oracle:oci8:@orcl");
		oracleBundle.setDriverClass("oracle.jdbc.driver.OracleDriver");

		final ArtifactMapping oracleArtifactSysMapping = new ArtifactMapping(
				oracleBundle, "SYS/");
		final Included oracleIncludedPackage = new Included(
				oracleArtifactSysMapping, "PACKAGE/*");

		final ArtifactMapping oracleArtifactMapping = new ArtifactMapping(
				oracleBundle, "HR/");
		final Included oracleIncludedTrigger = new Included(
				oracleArtifactMapping, "TRIGGER/*");
		final Included oracleIncludedProcedure = new Included(
				oracleArtifactMapping, "PROCEDURE/*");
		final Included oracleIncludedTable = new Included(
				oracleArtifactMapping, "TABLE/*");
		final Included oracleIncludedSequence = new Included(
				oracleArtifactMapping, "SEQUENCE/*");
		final Included oracleIncludedConstraint = new Included(
				oracleArtifactMapping, "CONSTRAINT/*");
		final Included oracleIncludedFunction = new Included(
				oracleArtifactMapping, "FUNCTION/*");
		final Included oracleIncludedView = new Included(oracleArtifactMapping,
				"VIEW/*");
		final Included oracleIncludedIndex = new Included(
				oracleArtifactMapping, "INDEX/*");
		final BundleProcessorType oracleCommonProcessor = new BundleProcessorType(
				oracleBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		final BundleProcessorType oracleCustomProcessor = new BundleProcessorType(
				oracleBundle,
				"org.openspotlight.federation.data.processing.test.LogTableCustomArtifactProcessor");

		oracleCommonProcessor.setActive(true);
		oracleCustomProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createOslValidConfiguration(final String dirName)
			throws Exception {
		final String basePath = new File("../../../").getCanonicalPath() + "/";
		final Configuration configuration = new Configuration();
		final Repository oslRepository = new Repository(configuration,
				"OSL Group");
		configuration.setNumberOfParallelThreads(4);
		oslRepository.setActive(true);
		final Group oslRootProject = new Group(oslRepository, "OSL Root Group");
		oslRootProject.setActive(true);
		final Group oslCommonsProject = new Group(oslRootProject,
				"OSL Commons Library");
		oslCommonsProject.setActive(true);
		final Bundle oslCommonsJavaSourceBundle = new Bundle(oslCommonsProject,
				"java source for OSL Bundle");
		oslCommonsJavaSourceBundle.setActive(true);
		oslCommonsJavaSourceBundle.setInitialLookup(basePath);
		final ArtifactMapping oslCommonsArtifactMapping = new ArtifactMapping(
				oslCommonsJavaSourceBundle, "osl-common/");
		final Included oslCommonsIncludedJavaFilesForSrcMainJava = new Included(
				oslCommonsArtifactMapping, "src/main/java/**/*.java");
		final Included oslCommonsIncludedJavaFilesForSrcTestJava = new Included(
				oslCommonsArtifactMapping, "src/test/java/**/*.java");
		final BundleProcessorType oslCommonProcessor = new BundleProcessorType(
				oslCommonsJavaSourceBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		oslCommonProcessor.setActive(true);
		final Group oslFederationProject = new Group(oslRootProject,
				"OSL Federation Library");
		oslFederationProject.setActive(true);
		final Bundle oslFederationJavaSourceBundle = new JavaBundle(
				oslFederationProject, "java source for OSL Bundle");
		oslFederationJavaSourceBundle.setActive(true);
		final BundleProcessorType oslFederationProcessor = new BundleProcessorType(
				oslFederationJavaSourceBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		oslFederationProcessor.setActive(true);

		oslFederationJavaSourceBundle.setInitialLookup(basePath);
		final ArtifactMapping oslFederationArtifactMapping = new ArtifactMapping(
				oslFederationJavaSourceBundle,
				"osl-federation/loader/osl-federation-filesystem-loader/");
		final Included oslFederationIncludedJavaFilesForSrcMainJava = new Included(
				oslFederationArtifactMapping, "src/main/java/**/*.java");
		final Included oslFederationIncludedJavaFilesForSrcTestJava = new Included(
				oslFederationArtifactMapping, "src/test/java/**/*.java");

		final Group oslGraphProject = new Group(oslRootProject,
				"OSL Graph Library");
		oslGraphProject.setActive(true);
		final Bundle oslGraphJavaSourceBundle = new JavaBundle(oslGraphProject,
				"java source for OSL Bundle");
		oslGraphJavaSourceBundle.setActive(true);
		oslGraphJavaSourceBundle.setInitialLookup(basePath);
		final ArtifactMapping oslGraphArtifactMapping = new ArtifactMapping(
				oslGraphJavaSourceBundle, "osl-graph/");
		final Included oslGraphIncludedJavaFilesForSrcMainJava = new Included(
				oslGraphArtifactMapping, "src/main/java/**/*.java");
		new File("./target/test-data/" + dirName + "/").mkdirs();
		final Included oslGraphIncludedJavaFilesForSrcTestJava = new Included(
				oslGraphArtifactMapping, "src/test/java/**/*.java");
		final BundleProcessorType oslGraphProcessor = new BundleProcessorType(
				oslGraphJavaSourceBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		oslGraphProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createOslValidDnaFileConnectorConfiguration(
			final String dirName) throws Exception {
		final String basePath = new File("../../../").getCanonicalPath() + "/";
		final Configuration configuration = new Configuration();
		final Repository oslRepository = new Repository(configuration,
				"OSL Group");
		configuration.setNumberOfParallelThreads(4);
		oslRepository.setActive(true);
		final Group oslRootProject = new Group(oslRepository, "OSL Root Group");
		oslRootProject.setActive(true);
		final Group oslCommonsProject = new Group(oslRootProject,
				"OSL Commons Library");
		oslCommonsProject.setActive(true);
		final Bundle oslCommonsJavaSourceBundle = new DnaFileBundle(
				oslCommonsProject, "java source for OSL Bundle");
		oslCommonsJavaSourceBundle.setActive(true);
		oslCommonsJavaSourceBundle.setInitialLookup(basePath);
		final ArtifactMapping oslCommonsArtifactMapping = new ArtifactMapping(
				oslCommonsJavaSourceBundle, "osl-common/");
		final Included oslCommonsIncludedJavaFilesForSrcMainJava = new Included(
				oslCommonsArtifactMapping, "src/main/java/**/*.java");
		final Included oslCommonsIncludedJavaFilesForSrcTestJava = new Included(
				oslCommonsArtifactMapping, "src/test/java/**/*.java");
		final BundleProcessorType oslCommonProcessor = new BundleProcessorType(
				oslCommonsJavaSourceBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		oslCommonProcessor.setActive(true);

		final Group oslGraphProject = new Group(oslRootProject,
				"OSL Graph Library");
		oslGraphProject.setActive(true);
		final Bundle oslGraphJavaSourceBundle = new JavaBundle(oslGraphProject,
				"java source for OSL Bundle");
		oslGraphJavaSourceBundle.setActive(true);
		oslGraphJavaSourceBundle.setInitialLookup(basePath);
		final ArtifactMapping oslGraphArtifactMapping = new ArtifactMapping(
				oslGraphJavaSourceBundle, "osl-graph/");
		final Included oslGraphIncludedJavaFilesForSrcMainJava = new Included(
				oslGraphArtifactMapping, "src/main/java/**/*.java");
		new File("./target/test-data/" + dirName + "/").mkdirs();
		final Included oslGraphIncludedJavaFilesForSrcTestJava = new Included(
				oslGraphArtifactMapping, "src/test/java/**/*.java");
		final BundleProcessorType oslGraphProcessor = new BundleProcessorType(
				oslGraphJavaSourceBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		oslGraphProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createOslValidDnaSvnConnectorConfiguration(
			final String dirName) throws Exception {
		final Configuration configuration = new Configuration();
		final Repository oslRepository = new Repository(configuration,
				"Hamcrest Repository");
		configuration.setNumberOfParallelThreads(4);
		oslRepository.setActive(true);
		final Group oslRootProject = new Group(oslRepository, "Hamcrest Group");
		oslRootProject.setActive(true);
		final Group oslCommonsProject = new Group(oslRootProject,
				"Hamcrest SubProject");
		oslCommonsProject.setActive(true);
		final DnaSvnBundle bundle = new DnaSvnBundle(oslCommonsProject,
				"Svn bundle");

		bundle
				.setInitialLookup("http://hamcrest.googlecode.com/svn/trunk/hamcrest-java/");
		bundle.setUser("anonymous");
		bundle.setPassword("");
		bundle.setActive(true);
		final ArtifactMapping artifactMapping = new ArtifactMapping(bundle,
				"src/");
		new Included(artifactMapping, "**/*.java");

		final BundleProcessorType oslCommonProcessor = new BundleProcessorType(
				bundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		oslCommonProcessor.setActive(true);

		return configuration;
	}

	public static Configuration createPostgresqlConfiguration() {
		final Configuration configuration = new Configuration();
		final Repository postgresqlRepository = new Repository(configuration,
				"postgresql Repository");
		configuration.setNumberOfParallelThreads(4);
		postgresqlRepository.setActive(true);
		final Group postgresqlProject = new Group(postgresqlRepository,
				"postgresql Group");
		postgresqlProject.setActive(true);
		final DbBundle postgresqlBundle = new DbBundle(postgresqlProject,
				"postgresql Connection");
		postgresqlBundle.setActive(true);
		postgresqlBundle.setUser("postgres");
		postgresqlBundle.setPassword("postgres");
		postgresqlBundle.setMaxConnections(4);
		postgresqlBundle.setType(DatabaseType.POSTGRES);
		postgresqlBundle
				.setInitialLookup("jdbc:postgresql://localhost:5432/osl?charSet=UTF8");
		postgresqlBundle.setDriverClass("org.postgresql.Driver");

		final ArtifactMapping postgresqlArtifactMapping = new ArtifactMapping(
				postgresqlBundle, "pg_catalog/");
		final Included postgresqlIncludedTrigger = new Included(
				postgresqlArtifactMapping, "TRIGGER/*");
		final ArtifactMapping postgresqlPublicArtifactMapping = new ArtifactMapping(
				postgresqlBundle, "public/");
		final Included postgresqlIncludedPublicFunction = new Included(
				postgresqlPublicArtifactMapping, "FUNCTION/*");
		final Included postgresqlIncludedProcedure = new Included(
				postgresqlArtifactMapping, "PROCEDURE/*");
		final Included postgresqlIncludedTable = new Included(
				postgresqlArtifactMapping, "TABLE/*");
		final Included postgresqlIncludedSequence = new Included(
				postgresqlArtifactMapping, "SEQUENCE/*");
		final Included postgresqlIncludedConstraint = new Included(
				postgresqlArtifactMapping, "CONSTRAINT/*");
		final Included postgresqlIncludedFunction = new Included(
				postgresqlArtifactMapping, "FUNCTION/*");
		final Included postgresqlIncludedView = new Included(
				postgresqlArtifactMapping, "VIEW/*");
		final Included postgresqlIncludedIndex = new Included(
				postgresqlArtifactMapping, "INDEX/*");
		final BundleProcessorType postgresqlCommonProcessor = new BundleProcessorType(
				postgresqlBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		final BundleProcessorType postgresqlCustomProcessor = new BundleProcessorType(
				postgresqlBundle,
				"org.openspotlight.federation.data.processing.test.LogTableCustomArtifactProcessor");

		postgresqlCommonProcessor.setActive(true);
		postgresqlCustomProcessor.setActive(true);
		return configuration;
	}

	public static Configuration createSqlServerDbConfiguration() {
		final Configuration configuration = new Configuration();
		final Repository sqlserverRepository = new Repository(configuration,
				"sqlserver Repository");
		configuration.setNumberOfParallelThreads(4);
		sqlserverRepository.setActive(true);
		final Group sqlserverProject = new Group(sqlserverRepository,
				"sqlserver Group");
		sqlserverProject.setActive(true);
		final DbBundle sqlserverBundle = new DbBundle(sqlserverProject,
				"sqlserver Connection");
		sqlserverBundle.setActive(true);
		sqlserverBundle.setUser("sa");
		sqlserverBundle.setMaxConnections(1);
		sqlserverBundle.setType(DatabaseType.SQL_SERVER);
		sqlserverBundle
				.setInitialLookup("jdbc:jtds:sqlserver://localhost:49385");
		sqlserverBundle.setDriverClass("net.sourceforge.jtds.jdbc.Driver");
		final ArtifactMapping sqlserverArtifactMapping = new ArtifactMapping(
				sqlserverBundle, "dbo/");
		final Included sqlserverIncludedTrigger = new Included(
				sqlserverArtifactMapping, "TRIGGER/*");
		final Included sqlserverIncludedProcedure = new Included(
				sqlserverArtifactMapping, "PROCEDURE/*");
		final Included sqlserverIncludedTable = new Included(
				sqlserverArtifactMapping, "TABLE/*");
		final Included sqlserverIncludedFunction = new Included(
				sqlserverArtifactMapping, "FUNCTION/*");
		final Included sqlserverIncludedConstraint = new Included(
				sqlserverArtifactMapping, "CONSTRAINT/*");
		final Included sqlserverIncludedView = new Included(
				sqlserverArtifactMapping, "VIEW/*");
		final Included sqlserverIncludedIndex = new Included(
				sqlserverArtifactMapping, "INDEX/*");
		final BundleProcessorType sqlserverCommonProcessor = new BundleProcessorType(
				sqlserverBundle,
				"org.openspotlight.federation.data.processing.test.LogPrinterBundleProcessor");
		final BundleProcessorType sqlserverCustomProcessor = new BundleProcessorType(
				sqlserverBundle,
				"org.openspotlight.federation.data.processing.test.LogTableCustomArtifactProcessor");

		sqlserverCommonProcessor.setActive(true);
		sqlserverCustomProcessor.setActive(true);
		return configuration;
	}

}
