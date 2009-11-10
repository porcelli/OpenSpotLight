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

package org.openspotlight.jcr.provider.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.util.JcrNodeVisitor;
import org.openspotlight.federation.data.util.JcrNodeVisitor.NodeVisitor;

/**
 * Test class to be used on configuration node tests.
 * 
 * @author Luiz Fernando Teston (Feu Teston)
 */
@SuppressWarnings("all")
public class JcrNodeVisitorTest {

	public static final String TESTDATA_PATH = "./src/test/resources/configuration/";
	public static final String JACKRABBIT_DATA_PATH = "./target/test-data/JcrNodeVisitorTest/";
	public static final String REPOSITORY_DIRECTORY_PATH = JACKRABBIT_DATA_PATH
			+ "repository";
	public static final String REPOSITORY_CONFIG_PATH = TESTDATA_PATH
			+ "JcrNodeVisitorTest/jackrabbit.xml";
	public static final String DERBY_SYSTEM_HOME = JACKRABBIT_DATA_PATH
			+ "derby";

	private Session session;

	private TransientRepository repository;

	@Before
	public void initializeSomeConfiguration() throws Exception {
		delete(JACKRABBIT_DATA_PATH);

		System.setProperty("derby.system.home", DERBY_SYSTEM_HOME);
		this.repository = new TransientRepository(REPOSITORY_CONFIG_PATH,
				REPOSITORY_DIRECTORY_PATH);
		final SimpleCredentials creds = new SimpleCredentials("jsmith",
				"password".toCharArray());
		this.session = this.repository.login(creds);
		assertThat(this.session, is(notNullValue()));
	}

	@Test
	public void shouldVisitNodesInACorrectWay() throws Exception {

		Node first1 = this.session.getRootNode().addNode("first1");
		Node first2 = this.session.getRootNode().addNode("first2");
		Node first3 = this.session.getRootNode().addNode("first3");
		Node first4 = this.session.getRootNode().addNode("first4");
		Node first5 = this.session.getRootNode().addNode("first5");

		Node second1_1 = first1.addNode("second1_1");
		Node second1_2 = first1.addNode("second1_2");
		Node second1_3 = first1.addNode("second1_3");

		Node second2_1 = first2.addNode("second2_1");
		Node second2_2 = first2.addNode("second2_2");
		Node second2_3 = first2.addNode("second2_3");

		Node third1_1_1 = second1_1.addNode("third1_1_1");
		Node third1_1_2 = second1_1.addNode("third1_1_2");
		Node third1_1_3 = second1_1.addNode("third1_1_3");

		Node third2_2_1 = second2_2.addNode("third2_2_1");
		Node third2_2_2 = second2_2.addNode("third2_2_2");
		Node third2_2_3 = second2_2.addNode("third2_2_3");

		final List<String> visitedNodes = new ArrayList<String>();

		NodeVisitor visitor = new NodeVisitor() {

			public void visiting(Node n) throws RepositoryException {
				visitedNodes.add(n.getName());
			}

		};

		ItemVisitor jcrVisitor = JcrNodeVisitor.withVisitor(visitor);
		session.getRootNode().accept(jcrVisitor);
		
		assertThat(visitedNodes.contains("first1"), is(true));
		assertThat(visitedNodes.contains("first2"), is(true));
		assertThat(visitedNodes.contains("first3"), is(true));
		assertThat(visitedNodes.contains("first4"), is(true));
		assertThat(visitedNodes.contains("first5"), is(true));

		assertThat(visitedNodes.contains("second1_1"), is(true));
		assertThat(visitedNodes.contains("second1_2"), is(true));
		assertThat(visitedNodes.contains("second1_3"), is(true));

		assertThat(visitedNodes.contains("second2_1"), is(true));
		assertThat(visitedNodes.contains("second2_2"), is(true));
		assertThat(visitedNodes.contains("second2_3"), is(true));

		assertThat(visitedNodes.contains("third1_1_1"), is(true));
		assertThat(visitedNodes.contains("third1_1_2"), is(true));
		assertThat(visitedNodes.contains("third1_1_3"), is(true));

		assertThat(visitedNodes.contains("third2_2_1"), is(true));
		assertThat(visitedNodes.contains("third2_2_2"), is(true));
		assertThat(visitedNodes.contains("third2_2_3"), is(true));
	}


	@Test
	public void shouldVisitNodesUsingLevelLimmit() throws Exception {

		Node first1 = this.session.getRootNode().addNode("first1");
		Node first2 = this.session.getRootNode().addNode("first2");
		Node first3 = this.session.getRootNode().addNode("first3");
		Node first4 = this.session.getRootNode().addNode("first4");
		Node first5 = this.session.getRootNode().addNode("first5");

		Node second1_1 = first1.addNode("second1_1");
		Node second1_2 = first1.addNode("second1_2");
		Node second1_3 = first1.addNode("second1_3");

		Node second2_1 = first2.addNode("second2_1");
		Node second2_2 = first2.addNode("second2_2");
		Node second2_3 = first2.addNode("second2_3");

		Node third1_1_1 = second1_1.addNode("third1_1_1");
		Node third1_1_2 = second1_1.addNode("third1_1_2");
		Node third1_1_3 = second1_1.addNode("third1_1_3");

		Node third2_2_1 = second2_2.addNode("third2_2_1");
		Node third2_2_2 = second2_2.addNode("third2_2_2");
		Node third2_2_3 = second2_2.addNode("third2_2_3");

		final List<String> visitedNodes = new ArrayList<String>();

		NodeVisitor visitor = new NodeVisitor() {

			public void visiting(Node n) throws RepositoryException {
				visitedNodes.add(n.getName());
			}

		};

		ItemVisitor jcrVisitor = JcrNodeVisitor.withVisitorAndLevelLimmit(visitor,2);
		session.getRootNode().accept(jcrVisitor);
		
		assertThat(visitedNodes.contains("first1"), is(true));
		assertThat(visitedNodes.contains("first2"), is(true));
		assertThat(visitedNodes.contains("first3"), is(true));
		assertThat(visitedNodes.contains("first4"), is(true));
		assertThat(visitedNodes.contains("first5"), is(true));

		assertThat(visitedNodes.contains("second1_1"), is(true));
		assertThat(visitedNodes.contains("second1_2"), is(true));
		assertThat(visitedNodes.contains("second1_3"), is(true));

		assertThat(visitedNodes.contains("second2_1"), is(true));
		assertThat(visitedNodes.contains("second2_2"), is(true));
		assertThat(visitedNodes.contains("second2_3"), is(true));

		assertThat(visitedNodes.contains("third1_1_1"), is(false));
		assertThat(visitedNodes.contains("third1_1_2"), is(false));
		assertThat(visitedNodes.contains("third1_1_3"), is(false));

		assertThat(visitedNodes.contains("third2_2_1"), is(false));
		assertThat(visitedNodes.contains("third2_2_2"), is(false));
		assertThat(visitedNodes.contains("third2_2_3"), is(false));
	}

	@After
	public void shutdown() throws Exception {
		if (this.session != null) {
			this.session.logout();
		}
		if (this.repository != null) {
			this.repository.shutdown();
		}
	}

}
