package test;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;

public class Test {
	
	public static void main(String[] args) {

		Session session = null;
		
		try {
			
			   RepositoryConfig repositoryConfig = RepositoryConfig.create(
					      "/Users/vitorchagas/local/projects/workspace/openspotlight/spotlight-graph/repository.xml", "/tmp/repository");
					    Repository repo = RepositoryImpl.create(repositoryConfig);

			Credentials credentials = new SimpleCredentials("username", "password".toCharArray());
			session = repo.login(credentials);
			
			System.out.println(session.getClass());
			
			Node root = session.getRootNode();
			
			Node hello = root.addNode("hello");
			
			Node world = hello.addNode("world");
			System.out.println(world.getPath());
			
			Property property = world.setProperty("test.message", "Hello World!!!");
			
			Property prop2 = world.getProperty("test.message");
			prop2.setValue("Just Hello!!!");
			System.out.println("propety: " + property.getValue().getString());
			System.out.println("prop2: " + prop2.getValue().getString());
			
			property.setValue(true);
			
			PropertyIterator iter = world.getProperties("test.*ge");
			while (iter.hasNext()) {
				Property current = iter.nextProperty();
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> " + current.getName());
			}
			
			
			hello.addMixin("mix:referenceable");
			world.addMixin("mix:referenceable");
			world.addNode("bye").addNode("otherBye");
			
			System.out.println("hello uuid: " + hello.getUUID());
			System.out.println("world uuid: " + world.getUUID());
			System.out.println(session.getNodeByUUID(hello.getUUID()).getUUID());
			
			System.out.println(hello.addNode(normalize(hello.getUUID() + "MtoM" + world.getUUID())));
			
			hello.addNode("java.lang");
			
			Node n = root.getNode("hello/world");
			System.out.println(n.getProperty("test.message").getValue().getString());
			
			session.save();
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery("//*//descendant::node()" , Query.XPATH);
			QueryResult result = query.execute();
			NodeIterator nodeIter = result.getNodes();
			
			while (nodeIter.hasNext()) {
				Node node = nodeIter.nextNode();
				System.out.println(node.getPath());
				PropertyIterator propIter = node.getProperties();
				while (propIter.hasNext()) {
					Property prop = propIter.nextProperty();
					if (prop.getDefinition().isMultiple()) {
						System.out.println("\t+" + prop.getName() + ":");
						Value[] values = prop.getValues();
						for (int i = 0; i < values.length; i++) {
							System.out.println("\t\t+" + values[i].getString());
						}
					}
					else {
						System.out.println("\t+" + prop.getName() + ": " + prop.getValue().getString());	
					}
					
				}
			}
			
			root.getNode("hello").remove();
			session.save();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (session != null) session.logout();
			
		}
	}
	
	private static String normalize(String name) {
		return name.replace('-', '.');
	}
}
