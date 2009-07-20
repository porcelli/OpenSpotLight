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
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;

public class Test3 {
	
	public static void main(String[] args) {
		
		try {
			RepositoryConfig repositoryConfig = RepositoryConfig.create("/Users/vitorchagas/local/projects/workspace/openspotlight/spotlight-graph/repository.xml",	"/tmp/repository");
			Repository repo = RepositoryImpl.create(repositoryConfig);
			Credentials credentials = new SimpleCredentials("username",	"password".toCharArray());
			Session session = repo.login(credentials);
			Node root = session.getRootNode();
			Node parentNode = root.addNode("hello");
			parentNode.addMixin("mix:versionable");

	        // create versionable node
			Node n = parentNode.addNode("childNode");
			n.addMixin("mix:versionable");
			n.setProperty("anyProperty", 1);
			session.save();
			Version firstVersion = n.checkin();
			
			// add new version
			Node child = parentNode.getNode("childNode");
			child.checkout();
			child.setProperty("anyProperty", 8);
			child.save();
			
			//temp.restore(firstVersion, true);
			//temp.checkout();
			//temp.setProperty("anyProperty", "test");
			
			session = repo.login(credentials);
			Node temp = session.getRootNode().getNode("hello").getNode("childNode");
			temp.restore(firstVersion, true);
			temp.checkout();
			System.out.println(temp.getProperty("anyProperty").getString());
			System.out.println(temp.getBaseVersion());

			//child.checkout();
			//child.restore(firstVersion, true);
			//child.checkout();
			//child.setProperty("anyProperty", "Blah8");
			//session.save();
			//child.checkin();
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery("//hello/*[@anyProperty = 1 or @anyProperty = 8]", Query.XPATH);
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

			VersionHistory history = child.getVersionHistory();
			for (VersionIterator it = history.getAllVersions(); it.hasNext();) {
				Version version = (Version) it.next();
				System.out.println(version);
				//System.out.println(version.getCreated().getTime());
			}

			/**
			Version secondVersion = child.checkin();
			// print version history
			VersionHistory history = child.getVersionHistory();
			for (VersionIterator it = history.getAllVersions(); it.hasNext();) {
				Version version = (Version) it.next();
				System.out.println(version);
				//System.out.println(version.getCreated().getTime());
			}

			// restoring old version
			child.checkout();
			child.restore(firstVersion, true);
			System.out.println();
			System.out.println(child.getProperty("anyProperty").getString());
			System.out.println(parentNode.getNode("childNode").getProperty("anyProperty").getString());
			**/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}


