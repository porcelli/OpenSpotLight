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

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.util.JcrNodeVisitor;
import org.openspotlight.jcr.util.JcrNodeVisitor.NodeVisitor;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Test class to be used on configuration node tests.
 * 
 * @author Luiz Fernando Teston (Feu Teston)
 */
@SuppressWarnings( "all" )
public class JcrNodeVisitorTest {

    private Session             session;

    private TransientRepository repository;

    @After
    public void closeSession() throws Exception {
        this.session.logout();
    }

    @Before
    public void initializeSomeConfiguration() throws Exception {
        final JcrConnectionProvider provicer = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        this.session = provicer.openSession();

        assertThat(this.session, is(notNullValue()));
    }

    @Test
    public void shouldVisitNodesInACorrectWay() throws Exception {

        final Node first1 = this.session.getRootNode().addNode("first1");
        final Node first2 = this.session.getRootNode().addNode("first2");
        final Node first3 = this.session.getRootNode().addNode("first3");
        final Node first4 = this.session.getRootNode().addNode("first4");
        final Node first5 = this.session.getRootNode().addNode("first5");

        final Node second1_1 = first1.addNode("second1_1");
        final Node second1_2 = first1.addNode("second1_2");
        final Node second1_3 = first1.addNode("second1_3");

        final Node second2_1 = first2.addNode("second2_1");
        final Node second2_2 = first2.addNode("second2_2");
        final Node second2_3 = first2.addNode("second2_3");

        final Node third1_1_1 = second1_1.addNode("third1_1_1");
        final Node third1_1_2 = second1_1.addNode("third1_1_2");
        final Node third1_1_3 = second1_1.addNode("third1_1_3");

        final Node third2_2_1 = second2_2.addNode("third2_2_1");
        final Node third2_2_2 = second2_2.addNode("third2_2_2");
        final Node third2_2_3 = second2_2.addNode("third2_2_3");

        final List<String> visitedNodes = new ArrayList<String>();

        final NodeVisitor visitor = new NodeVisitor() {

            public void visiting( final Node n ) throws RepositoryException {
                visitedNodes.add(n.getName());
            }

        };

        final ItemVisitor jcrVisitor = JcrNodeVisitor.withVisitor(visitor);
        this.session.getRootNode().accept(jcrVisitor);

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

        final Node first1 = this.session.getRootNode().addNode("first1");
        final Node first2 = this.session.getRootNode().addNode("first2");
        final Node first3 = this.session.getRootNode().addNode("first3");
        final Node first4 = this.session.getRootNode().addNode("first4");
        final Node first5 = this.session.getRootNode().addNode("first5");

        final Node second1_1 = first1.addNode("second1_1");
        final Node second1_2 = first1.addNode("second1_2");
        final Node second1_3 = first1.addNode("second1_3");

        final Node second2_1 = first2.addNode("second2_1");
        final Node second2_2 = first2.addNode("second2_2");
        final Node second2_3 = first2.addNode("second2_3");

        final Node third1_1_1 = second1_1.addNode("third1_1_1");
        final Node third1_1_2 = second1_1.addNode("third1_1_2");
        final Node third1_1_3 = second1_1.addNode("third1_1_3");

        final Node third2_2_1 = second2_2.addNode("third2_2_1");
        final Node third2_2_2 = second2_2.addNode("third2_2_2");
        final Node third2_2_3 = second2_2.addNode("third2_2_3");

        final List<String> visitedNodes = new ArrayList<String>();

        final NodeVisitor visitor = new NodeVisitor() {

            public void visiting( final Node n ) throws RepositoryException {
                visitedNodes.add(n.getName());
            }

        };

        final ItemVisitor jcrVisitor = JcrNodeVisitor.withVisitorAndLevelLimmit(visitor, 2);
        this.session.getRootNode().accept(jcrVisitor);

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
