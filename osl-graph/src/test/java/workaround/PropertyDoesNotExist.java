package workaround;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.TransientRepository;

public class PropertyDoesNotExist {
	
	public static void main(String[] args) {
		try {
			
			Repository repo = new TransientRepository();
			Session session = repo.login(new SimpleCredentials("user", "pw".toCharArray()));
			Node root = session.getRootNode();
			Node hello = null;
			
			try {
				hello = root.getNode("hello");
				hello.remove();
			}
			catch (PathNotFoundException e) {
			}
			
			hello = root.addNode("hello");
			//hello.setProperty("prop1", "test");
			hello.setProperty("prop2", "test");
			
			session.save();
			
			Query query = session.getWorkspace().getQueryManager().createQuery("//hello[not(@prop1) and @prop2 = 'test']", Query.XPATH);
			QueryResult result = query.execute();
			NodeIterator iter = result.getNodes();
			while(iter.hasNext()) {
				System.out.println(iter.nextNode().getPath());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
