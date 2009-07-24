package test;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;

public class DuplicateNodeNameTest {
	
	public static void main(String[] args) {
		
		
		try {
			
			Repository repo = new TransientRepository();
			Credentials credentials = new SimpleCredentials("username", "password".toCharArray());
			Session session = repo.login(credentials);
			
			Node root = session.getRootNode();
			root.addNode("hello").addMixin("mix:referenceable");
			root.addNode("hello").addMixin("mix:referenceable");
			root.addNode("hello").addMixin("mix:referenceable");
			
			NodeIterator nodeIter = root.getNodes("hell*");
			while (nodeIter.hasNext()) {
				Node node = nodeIter.nextNode();
				System.out.println(node.getPath());
			}
			session.logout();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
