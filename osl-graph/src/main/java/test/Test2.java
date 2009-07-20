package test;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;

public class Test2 {

	public static void main(String[] args) {

		Session session = null;

		try {

			RepositoryConfig repositoryConfig = RepositoryConfig.create("/Users/vitorchagas/local/projects/workspace/openspotlight/spotlight-graph/repository.xml",	"/tmp/repository");
			Repository repo = RepositoryImpl.create(repositoryConfig);

			Credentials credentials = new SimpleCredentials("username",	"password".toCharArray());
			session = repo.login(credentials);

			Node root = session.getRootNode();
			Node hello = root.addNode("hello");
			hello.addMixin("mix:versionable");
			root.save();

			
			hello.checkout();
			hello.setProperty("message", "Hello World!");
			hello.save();
			hello.checkin();
			
			hello.checkout();
			hello.setProperty("message", "Hello!");
			hello.save();
			Version version = hello.checkin();
			version.save();
			
			System.out.println(hello.getUUID());
			
			VersionHistory history = hello.getVersionHistory();
			VersionIterator iter = history.getAllVersions();
			while (iter.hasNext()) {
				Version current = iter.nextVersion();
				System.out.println(current);
				
				System.out.println(current.getName());
				
				PropertyIterator propIter = current.getProperties();
				
				
				while (propIter.hasNext()) {
					Property prop = propIter.nextProperty();
					if (prop.getDefinition().isMultiple()) continue;
					if (prop.getType() == PropertyType.STRING) {
						System.out.println("\t" + prop.getName() + ": " + prop.getString());	
					}
					
				}
			}
			
			
			
			
			
			
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (session != null)
				session.logout();

		}
	}

}
