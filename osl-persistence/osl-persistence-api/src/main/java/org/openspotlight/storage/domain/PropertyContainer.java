package org.openspotlight.storage.domain;

import java.io.InputStream;
import java.util.Set;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;

public interface PropertyContainer {

	String getKeyAsString();

	Partition getPartition();

	Property getProperty(StorageSession session, String name);

	String getPropertyAsString(StorageSession session, String name);

	InputStream getPropertyAsStream(StorageSession session, String name);

	byte[] getPropertyAsBytes(StorageSession session, String name);

	Set<String> getPropertyNames(StorageSession session);

	Set<Property> getProperties(StorageSession session);

	Property setSimpleProperty(StorageSession session, String name,
			String value);

	Property setSimpleProperty(StorageSession session, String name,
			InputStream value);

	Property setSimpleProperty(StorageSession session, String name,
			byte[] value);

	Property setIndexedProperty(StorageSession session, String name,
			String value);

}
