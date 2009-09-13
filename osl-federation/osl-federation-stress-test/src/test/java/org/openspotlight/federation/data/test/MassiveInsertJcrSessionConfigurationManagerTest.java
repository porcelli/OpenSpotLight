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

package org.openspotlight.federation.data.test;

import static java.text.MessageFormat.format;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;
import static org.openspotlight.common.util.Files.delete;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class to see if the Jcr configuration is working ok. This test was based
 * on tests found on DNA project http://jboss.org/dna/
 * 
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */

@SuppressWarnings("all")
public class MassiveInsertJcrSessionConfigurationManagerTest {

	public static final String _JACKRABBIT_DATA_PATH = "./target/test-data/MassiveInsertJcrSessionConfigurationManagerTest/";
	public static final String _TESDATA_PATH = "./src/test/resources/";

	private static Configuration configuration;

	public static final String DERBY_SYSTEM_HOME = _JACKRABBIT_DATA_PATH
			+ "/derby";

	private static ConfigurationManager implementation;
	private static final Logger logger = LoggerFactory
			.getLogger(MassiveInsertJcrSessionConfigurationManagerTest.class);
	private static TransientRepository repository;
	public static final String REPOSITORY_CONFIG_PATH = _TESDATA_PATH
			+ "configuration/MassiveInsertJcrSessionConfigurationManagerTest/jackrabbit.xml";

	public static final String REPOSITORY_DIRECTORY_PATH = _JACKRABBIT_DATA_PATH
			+ "repository";

	private static Session session;

	@BeforeClass
	public static void initializeSomeConfiguration() throws Exception {
		if (!"true".equals(System.getProperty("runStressTests"))) {
			logger
					.warn(format(
							"Skipping setup of stress tests because of {0} system property isn't set to {1}",
							"runStressTests", true));
			return;
		}

		delete(_JACKRABBIT_DATA_PATH);
		System.setProperty("derby.system.home", DERBY_SYSTEM_HOME);
		repository = new TransientRepository(REPOSITORY_CONFIG_PATH,
				REPOSITORY_DIRECTORY_PATH);
		final SimpleCredentials creds = new SimpleCredentials("jsmith",
				"password".toCharArray());
		session = repository.login(creds);
		assertThat(session, is(notNullValue()));
		implementation = new JcrSessionConfigurationManager(session);
	}

	@AfterClass
	public static void shutdown() throws Exception {
		if (session != null) {
			session.logout();
		}
		if (repository != null) {
			repository.shutdown();
		}
	}

	public void shouldInsertNodeData() throws Exception {
		if (!"true".equals(System.getProperty("runStressTests"))) {
			this.logger
					.warn(format(
							"Skipping stress tests because of {0} system property isn't set to {1}",
							"runStressTests", true));
			return;
		}
		final Configuration configuration = new Configuration();
		final Repository repository = new Repository(configuration,
				"repository");
		final Group rootProject = new Group(repository, "root");

		final InputStream is = getResourceFromClassPath("/data/MassiveInsertJcrSessionConfigurationManagerTest/nodeData.csv");
		assertThat(is, is(notNullValue()));
		final Map<String, Group> handleMap = new HashMap<String, Group>();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				is));
		String line = null;
		boolean first = true;
		int count = 0;
		int err = 0;
		int ok = 0;
		try {
			while ((count++ != 40000) && ((line = reader.readLine()) != null)) {

				if (first) {
					first = false;
					continue;
				}

				try {

					final StringTokenizer tok = new StringTokenizer(line, "|");
					final String t1 = tok.nextToken();
					final String t2 = tok.nextToken();
					final String t3 = tok.nextToken();
					final String t4 = tok.nextToken();

					final String t5;
					if (tok.hasMoreTokens()) {
						t5 = tok.nextToken();
					} else {
						t5 = null;
					}

					final String handle = t1;
					final String parentHandle = t5 == null ? null : t2;
					final String key = t5 == null ? t2 : t3;
					final String caption = t5 == null ? t3 : t4;
					final String type = (t5 == null ? t4 : t5).replaceAll(" ",
							"").replaceAll("\\.", "").replaceAll("-", "");

					Group node;
					if ((parentHandle == null)
							|| parentHandle.trim().equals("")) {
						node = rootProject.getGroupByName(key);
						if (node == null) {
							node = new Group(rootProject, key);
						}
						handleMap.put(handle, node);
					} else {
						final Group parent = handleMap.get(parentHandle);
						if (parent == null) {
							Assert.fail();
						}
						node = parent.getGroupByName(key);
						if (node == null) {
							node = new Group(parent, key);
						}
						handleMap.put(handle, node);
					}
					ok++;
				} catch (final Exception e) {
					System.err.println("node: " + e.getMessage() + ": " + line);
					err++;
				}
			}
			this.implementation.save(configuration);
		} finally {
			System.out.println("count " + count);
			System.out.println("err   " + err);
			System.out.println("ok    " + ok);

		}

	}

	@Test
	public void shouldInsertNodesAndLinks() throws Exception {
		this.shouldInsertNodeData();
		// this.shouldInsertLinkData();
	}
}
