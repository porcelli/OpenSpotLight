package org.openspotlight.graph.test;

import org.openspotlight.graph.SLGraphModule;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.mongodb.test.MongoModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoGraphTest extends AbstractGraphTest {

	private final Mongo mongo;
	private final STRepositoryPath repositoryPath = new STRepositoryPath(
			"repository");

	public MongoGraphTest() {
		try {
			mongo = new Mongo();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void clearData() throws Exception {
		BasicDBObject dbObject = new BasicDBObject();
		for (String dbName : mongo.getDatabaseNames()) {
			DB db = mongo.getDB(dbName);
			for (String colName : db.getCollectionNames()) {
				db.getCollection(colName).remove(dbObject);
			}
		}
	}

	@Override
	protected Injector createInjector() throws Exception {
		return Guice.createInjector(new MongoModule(
				STStorageSession.STFlushMode.EXPLICIT, mongo, repositoryPath,
				SLPartition.FACTORY), new SLGraphModule());
	}

}
