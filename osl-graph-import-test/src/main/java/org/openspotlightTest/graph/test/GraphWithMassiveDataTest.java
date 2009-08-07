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
// annoying package name just to exclude this class for the profiler filter...
package org.openspotlightTest.graph.test;

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test just import example data into the GraphSession.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class GraphWithMassiveDataTest {
    
    public static void main(final String... args) throws Exception {
        final long start = System.currentTimeMillis();
        
        final GraphWithMassiveDataTest g = new GraphWithMassiveDataTest();
        g.setup();
        g.shouldInsertNodeData();
        final long end = System.currentTimeMillis();
        final long spent = end - start;
        g.logger.info("Spent time " + spent + " milliseconds");
    }
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private SLNode rootNode;
    
    private SLGraphSession session;
    
    public void setup() throws Exception {
        final SLGraphFactory factory = new SLGraphFactoryImpl();
        final SLGraph graph = factory.createGraph();
        this.session = graph.openSession();
        this.rootNode = this.session.createContext(1L).getRootNode();
        
    }
    
    public void shouldInsertLinkData(final Map<String, SLNode> handleMap)
            throws Exception {
        int count = 0;
        int err = 0;
        int ok = 0;
        
        try {
            final InputStream is = getResourceFromClassPath("/data/GraphWithMassiveDataTest/linkData.csv");
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String line = null;
            boolean first = true;
            while ((count++ != 100000) && ((line = reader.readLine()) != null)) {
                if (first) {
                    first = false;
                    continue;
                }
                try {
                    final StringTokenizer tok = new StringTokenizer(line, "|");
                    final String handleA = tok.nextToken();
                    final String handleB = tok.nextToken();
                    final String type = tok.nextToken().replaceAll(" ", "")
                            .replaceAll("\\.", "").replaceAll("-", "");
                    
                    final Class<? extends SLLink> clazz = (Class<? extends SLLink>) Class
                            .forName("org.openspotlight.graph.link." + type
                                    + "Link");
                    this.session.addLink(clazz, handleMap.get(handleA),
                            handleMap.get(handleB), false);
                    ok++;
                    
                } catch (final Exception e) {
                    this.logger.error("error on node: " + e.getMessage() + ": "
                            + line, e);
                    err++;
                }
            }
        } finally {
            System.out.println("count " + count);
            System.out.println("err   " + err);
            System.out.println("ok    " + ok);
            
        }
    }
    
    public void shouldInsertNodeData() throws Exception {
        
        final Map<String, SLNode> handleMap = new HashMap<String, SLNode>();
        final InputStream is = getResourceFromClassPath("/data/GraphWithMassiveDataTest/nodeData.csv");
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
                    
                    SLNode node;
                    if ((parentHandle == null)
                            || parentHandle.trim().equals("")) {
                        node = this.rootNode.addNode(caption);
                        
                    } else {
                        final SLNode parent = handleMap.get(parentHandle);
                        if (parent == null) {
                            throw new Exception("no parent");
                        }
                        node = parent.addNode(caption);
                    }
                    handleMap.put(handle, node);
                    ok++;
                    
                } catch (final Exception e) {
                    this.logger.error("error on node: " + e.getMessage() + ": "
                            + line, e);
                    err++;
                }
            }
        } finally {
            System.out.println("count " + count);
            System.out.println("err   " + err);
            System.out.println("ok    " + ok);
            
        }
        this.shouldInsertLinkData(handleMap);
        
    }
    
}
