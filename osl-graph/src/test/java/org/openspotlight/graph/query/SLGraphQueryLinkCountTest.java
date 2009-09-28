package org.openspotlight.graph.query;

import java.lang.reflect.Method;
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class SLGraphQueryLinkCountTest {
	
	static final Logger LOGGER = Logger.getLogger(SLGraphQueryTest.class);
	
	private SLGraph graph;
	private SLGraphSession session;

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
	
    @AfterClass
    public void finish() {
        session.close();
        graph.shutdown();
    }
    
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

    @Test
    public void selectCollectionMethodsWithAllInCaptionAndWithOneParam() {
    	
    	try {
    		
    		String id = findIFaceID(java.util.Collection.class);
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
