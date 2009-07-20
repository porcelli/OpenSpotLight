package org.openspotlight.graph.persistence;

import java.io.File;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;

public class SLPersistentTreeFactoryImpl extends SLPersistentTreeFactory {

	@Override
	public SLPersistentTree createPersistentTree() throws SLPersistentTreeFactoryException {
		try {
			deleteDir(new File("/tmp/repository"));
			Credentials credentials = new SimpleCredentials("username", "password".toCharArray());
			RepositoryConfig config = RepositoryConfig.create(new File(".", "src/main/resources/repository.xml").getAbsolutePath(), "/tmp/repository");
			Repository repo = RepositoryImpl.create(config);
			return new SLPersistentTreeImpl(repo, credentials);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeFactoryException("Couldn't create persistent tree.", e);
		}
	}
	
	public boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}

