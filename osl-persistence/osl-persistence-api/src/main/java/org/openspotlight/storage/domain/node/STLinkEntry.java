package org.openspotlight.storage.domain.node;

import java.io.InputStream;
import java.util.Set;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;

public interface STLinkEntry {

	STNodeEntry getOrigin();

	STNodeEntry getTarget();

	String getLinkName();

	String getLinkId();

	STPartition getLinkPartition();

	STProperty getProperty(STStorageSession session, String name);

	String getPropertyAsString(STStorageSession session, String name);

	InputStream getPropertyAsStream(STStorageSession session, String name);

	byte[] getPropertyAsBytes(STStorageSession session, String name);

	Set<String> getPropertyNames(STStorageSession session);

	Set<STProperty> getProperties(STStorageSession session);

	STProperty setSimpleProperty(STStorageSession session, String name,
			String value);

	STProperty setSimpleProperty(STStorageSession session, String name,
			InputStream value);

	STProperty setSimpleProperty(STStorageSession session, String name,
			byte[] value);

	STProperty setIndexedProperty(STStorageSession session, String name,
			String value);

}
