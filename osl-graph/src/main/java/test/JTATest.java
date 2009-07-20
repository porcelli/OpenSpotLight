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
import javax.transaction.UserTransaction;

import org.apache.jackrabbit.core.TransientRepository;

public class JTATest {
	
	public static void main(String[] args) {
		
		try {
			
			Repository repo = new TransientRepository();
			Credentials credentials = new SimpleCredentials("username", "password".toCharArray());
			Session session = repo.login(credentials);
			
			UserTransaction transaction = new JackRabbitUserTransaction(session);
			transaction.begin();
			
			Node rootNode = session.getRootNode();
			rootNode.addNode("hello");
			session.save();
			
			//transaction.rollback();
			//transaction.commit();
			
			//System.out.println(session.getRootNode().getNode("hello"));
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery("//hello" , Query.XPATH);
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
