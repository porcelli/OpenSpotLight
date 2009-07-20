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

public class Test4 {

	
	public static void main(String[] args) {
		
		try {
			RepositoryConfig repositoryConfig = RepositoryConfig
				.create("/Users/vitorchagas/local/projects/workspace/openspotlight/spotlight-graph/repository.xml",	"/tmp/repository");
			Repository repo = RepositoryImpl.create(repositoryConfig);
			Credentials credentials = new SimpleCredentials("username",	"password".toCharArray());
			Session session = repo.login(credentials);
			Node root = session.getRootNode();

	        // create versionable node
			Node hello = root.addNode("hello");
			hello.addMixin("mix:versionable");
			
			hello.checkout();
			System.out.println(hello.getBaseVersion());
			session.save();
			
			System.out.println(hello.getBaseVersion());;
			
			hello = repo.login(credentials).getRootNode().getNode("hello");
			hello.restore(hello.getBaseVersion(), true);
			hello.checkout();
			hello.setProperty("message", "Hello!");
			hello.save();
			hello.checkin();
			
			
			Workspace workspace = repo.login(credentials).getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery("//hello[@message='Hello!']", Query.XPATH);
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
