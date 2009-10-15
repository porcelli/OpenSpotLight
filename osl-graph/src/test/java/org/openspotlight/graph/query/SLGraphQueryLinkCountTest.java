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
package org.openspotlight.graph.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.test.domain.JavaInterface;
import org.openspotlight.graph.test.domain.JavaTypeMethod;
import org.openspotlight.graph.test.domain.MethodContainsParam;
import org.openspotlight.graph.test.domain.MethodParam;
import org.openspotlight.graph.test.domain.TypeContainsMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class SLGraphQueryLinkCountTest.
 * 
 * @author Vitor Hugo Chagas
 */
@Test
public class SLGraphQueryLinkCountTest {
	
	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLGraphQueryTest.class);
	
	/** The graph. */
	private SLGraph graph;
	
	/** The session. */
	private SLGraphSession session;

	/**
	 * Quick graph population.
	 */
	@BeforeClass
	public void quickGraphPopulation() {
		try {
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            graph = factory.createTempGraph(true);
            session = graph.openSession();
            SLContext context = session.createContext("linkCountTest");
            SLNode root = context.getRootNode();
            Set<Class<?>> types = getIFaceTypeSet();
            for (Class<?> type : types) {
            	Method[] methods = type.getDeclaredMethods();
            	LOGGER.info(type.getName() + ": " + methods.length + " methods");
            	JavaInterface javaInteface = root.addNode(JavaInterface.class, type.getName());
            	javaInteface.setProperty(String.class, "caption", type.getName());
            	for (int i = 0; i < methods.length; i++) {
            		JavaTypeMethod javaMethod = javaInteface.addNode(JavaTypeMethod.class, methods[i].getName());
            		javaMethod.setProperty(String.class, "caption", methods[i].getName());
            		session.addLink(TypeContainsMethod.class, javaInteface, javaMethod, false);
            		Class<?>[] paramTypes = methods[i].getParameterTypes();
            		LOGGER.info("\t\t" + methods[i].getName() + ": " + paramTypes.length + " params");
            		for (int j = 0; j < paramTypes.length; j++) {
            			MethodParam methodParam = javaMethod.addNode(MethodParam.class, paramTypes[j].getName());
            			methodParam.setProperty(String.class, "caption", paramTypes[j].getName());
            			session.addLink(MethodContainsParam.class, javaMethod, methodParam, false);
					}
				}
			}
            session.save();
			session.close();
			session = graph.openSession();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
    /**
     * Finish.
     */
    @AfterClass
    public void finish() {
        session.close();
        graph.shutdown();
    }
    
    /**
     * Select map zero param methods.
     */
    @Test
    public void selectMapZeroParamMethods() {
    	
    	try {
    		
    		String id = findIFaceID(java.util.Map.class);
    		SLQuery query = session.createQuery();

    		query
    			.select()
    				.type(JavaTypeMethod.class.getName())
    				.byLink(TypeContainsMethod.class.getName()).b()
    			.selectEnd()
    			.select()
			    	.type(JavaTypeMethod.class.getName())
			    .selectEnd()
			    .where()
				    .type(JavaTypeMethod.class.getName())
				    	.each().link(MethodContainsParam.class.getName()).a().count().equalsTo().value(0)
				    .typeEnd()
			    .whereEnd();
    		
    		SLQueryResult result = query.execute(new String[] {id});
    		Collection<SLNode> nodes = result.getNodes();
    		QueryUtil.printResult(nodes);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
    }

    /**
     * Select collection methods with all in caption and with one param.
     */
    @Test
    public void selectCollectionMethodsWithAllInCaptionAndWithOneParam() {
    	
    	try {
    		
    		String id = findIFaceID(java.util.Collection.class);
    		SLNode node = session.getNodeByID(id);
    		Collection<SLNode> inputNodes = new ArrayList<SLNode>();
    		inputNodes.add(node);
    		
    		SLQuery query = session.createQuery();

    		query
    			.select()
    				.type(JavaTypeMethod.class.getName())
    				.byLink(TypeContainsMethod.class.getName()).b()
    			.selectEnd()
    			.select()
			    	.type(JavaTypeMethod.class.getName())
			    .selectEnd()
			    .where()
				    .type(JavaTypeMethod.class.getName())
				    	.each().property("caption").contains().value("All")
				    	.and().each().link(MethodContainsParam.class.getName()).a().count().equalsTo().value(1)
				    .typeEnd()
			    .whereEnd();
    		
    		SLQueryResult result = query.execute(new String[] {id});
    		Collection<SLNode> nodes = result.getNodes();
    		QueryUtil.printResult(nodes);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
    }

    /**
     * Find i face id.
     * 
     * @param type the type
     * 
     * @return the string
     * 
     * @throws SLGraphSessionException the SL graph session exception
     */
    private String findIFaceID(Class<?> type) throws SLGraphSessionException {
		SLQuery query = session.createQuery();
		query
			.select()
				.allTypes().onWhere()
			.selectEnd()
			.where()
				.type(JavaInterface.class.getName())
					.each().property("caption").equalsTo().value(type.getName())
				.typeEnd()
			.whereEnd();
		SLQueryResult result = query.execute();
		Collection<SLNode> nodes = result.getNodes();
		return nodes.size() > 0 ? result.getNodes().iterator().next().getID() : null;
    }
	
	/**
	 * Gets the i face type set.
	 * 
	 * @return the i face type set
	 */
	private Set<Class<?>> getIFaceTypeSet() {
		Set<Class<?>> set = new HashSet<Class<?>>();
		set.add(java.util.Collection.class);
		set.add(java.util.Map.class);
		set.add(java.util.List.class);
		set.add(java.util.Set.class);
		set.add(java.util.SortedSet.class);
		return set;
	}
}
