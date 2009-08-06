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

package org.openspotlight.graph.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLPersistenceMode;
import org.openspotlight.graph.node.CobolCopyDataNode;

/**
 * This test just import example data into the GraphSession.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class GraphWithMassiveDataTest {
    
    private SLNode rootNode;
    private SLGraphSession session;
    
    @Test
    public void findTwoNodes() throws Exception {
        final CobolCopyDataNode parent = this.rootNode.addNode(
                CobolCopyDataNode.class, "n1");
        final CobolCopyDataNode child = parent.addNode(CobolCopyDataNode.class,
                "n1");
        final String id = parent.getID();
        final SLNode n = this.session.getNodeByID(id);
        n.toString();
    }
    
    @Before
    public void setup() throws Exception {
        final SLGraphFactory factory = AbstractFactory
                .getDefaultInstance(SLGraphFactory.class);
        final SLGraph graph = factory.createGraph();
        this.session = graph.openSession();
        this.rootNode = this.session.createContext(1L).getRootNode();
        
    }
    
    public void shouldInsertLinkData() throws Exception {
        final InputStream is = getResourceFromClassPath("/data/GraphWithMassiveDataTest/linkData.csv");
        assertThat(is, is(notNullValue()));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                is));
        String line = null;
        boolean first = true;
        int count = 0;
        while ((count++ != 100000) && ((line = reader.readLine()) != null)) {
            if (first) {
                first = false;
                continue;
            }
            try {
                final StringTokenizer tok = new StringTokenizer(line, ";");
                final String type = tok.nextToken().replaceAll(" ", "")
                        .replaceAll("\\.", "").replaceAll("-", "");
                final String firstNodeName = tok.nextToken();
                final String secondNodeName = tok.nextToken();
                final Class<? extends SLLink> clazz = (Class<? extends SLLink>) Class
                        .forName("org.openspotlight.graph.link." + type
                                + "Link");
                this.session.addLink(clazz, this.rootNode
                        .getNode(firstNodeName), this.rootNode
                        .getNode(secondNodeName), false);
                System.out.println("link ok: " + line);
            } catch (final Exception e) {
                System.err.println("node: " + e.getMessage() + ": " + line);
                e.printStackTrace();
            }
        }
    }
    
    public void shouldInsertNodeData() throws Exception {
        
        final InputStream is = getResourceFromClassPath("/data/GraphWithMassiveDataTest/nodeData.csv");
        assertThat(is, is(notNullValue()));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                is));
        String line = null;
        boolean first = true;
        int count = 0;
        while ((count++ != 2000) && ((line = reader.readLine()) != null)) {
            
            if (first) {
                first = false;
                continue;
            }
            try {
                final StringTokenizer tok = new StringTokenizer(line, ";");
                final String t1 = tok.nextToken();
                final String t2 = tok.nextToken();
                final String t3 = tok.nextToken();
                
                final String t4;
                if (tok.hasMoreTokens()) {
                    t4 = tok.nextToken();
                } else {
                    t4 = null;
                }
                
                final String key = t1;
                final String parentKey = t4 == null ? null : t2;
                final String type = (t4 == null ? t2 : t3).replaceAll(" ", "")
                        .replaceAll("\\.", "").replaceAll("-", "");
                final String caption = t4 == null ? t3 : t4;
                
                final Class<? extends SLNode> clazz = (Class<? extends SLNode>) Class
                        .forName("org.openspotlight.graph.node." + type
                                + "Node");
                final SLNode node;
                if ((type != null) && !"".equals(type)) {
                    node = this.rootNode.addNode(clazz, key,
                            SLPersistenceMode.TRANSIENT);
                } else {
                    final SLNode parent = this.session.getNodeByID(parentKey);
                    node = parent.addNode(clazz, key,
                            SLPersistenceMode.TRANSIENT);
                }
                clazz.getMethod("setCaption", String.class).invoke(node,
                        caption);
                // System.out.println("node ok: " + line);
            } catch (final Exception e) {
                System.err.println("node: " + e.getMessage() + ": " + line);
                e.printStackTrace();
            }
        }
        System.out.println(count);
        
    }
    
    // @Test
    public void shouldInsertNodesAndLinks() throws Exception {
        // this.shouldInsertNodeData();
        // this.shouldInsertLinkData();
    }
    
    @After
    public void shutdown() throws Exception {
        // this.rootNode.getSession().clear();
    }
    
}
