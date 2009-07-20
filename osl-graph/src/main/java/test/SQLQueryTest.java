package test;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.TransientRepository;

public class SQLQueryTest {
	
	public static void main(String[] args) {
		Session session = null;
		
		try {
		
			Repository repo = new TransientRepository();
			Credentials credentials = new SimpleCredentials("username", "password".toCharArray());
			session = repo.login(credentials);
			
			Node root = session.getRootNode();
	        Node foo = root.addNode("foo");
	        foo.setProperty("mytext", new String[]{"the quick brown fox jumps over the lazy dog."});

	        //root.save();

	        String sql = "SELECT * FROM nt:unstructured"
	                + " WHERE jcr:path LIKE '/foo/%'"
	                + " AND CONTAINS(., 'fox')";
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery(sql, Query.SQL);
			//Query query = queryManager.createQuery("//hello/world[@message='Hello World!']", Query.SQL);
			QueryResult result = query.execute();
			NodeIterator nodeIter = result.getNodes();
			
			while (nodeIter.hasNext()) {
				Node node = nodeIter.nextNode();
				System.out.println(node.getPath());
				PropertyIterator propIter = node.getProperties();
				while (propIter.hasNext()) {
					Property prop = propIter.nextProperty();
					System.out.println("\t+" + prop.getString());
				}
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (session != null) session.logout();
			
		}
	}
}
