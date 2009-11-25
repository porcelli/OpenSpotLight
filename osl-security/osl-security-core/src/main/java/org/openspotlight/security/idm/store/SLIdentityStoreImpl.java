package org.openspotlight.security.idm.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Session;

import org.jboss.identity.idm.common.exception.IdentityException;
import org.jboss.identity.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityStoreConfigurationMetaData;
import org.jboss.identity.idm.spi.exception.OperationNotSupportedException;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectAttribute;
import org.jboss.identity.idm.spi.model.IdentityObjectCredential;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationship;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;
import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.jboss.identity.idm.spi.search.IdentityObjectSearchCriteria;
import org.jboss.identity.idm.spi.store.FeaturesMetaData;
import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.util.Collections;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.security.domain.SLAttributeEntry;
import org.openspotlight.security.domain.SLIdentityObject;
import org.openspotlight.security.domain.SLIdentityObjectRelationship;
import org.openspotlight.security.domain.SLIdentityObjectRelationshipType;
import org.openspotlight.security.domain.SLIdentityObjectType;
import org.openspotlight.security.domain.SLTransientIdentityObjectAttribute;

public class SLIdentityStoreImpl implements IdentityStore, Serializable {

	public static final String JCR_PROVIDER_NAME = "jcrProviderName";

	public static final String REPOSITORY_NAME = "repositoryName";

	private JcrConnectionProvider provider;

	private IdentityStoreConfigurationMetaData configurationMetaData;

	private String id;

	private String repositoryName;

	private DefaultJcrDescriptor providerDescriptor;

	@SuppressWarnings("unchecked")
	public void addAttributes(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObject identity,
			final IdentityObjectAttribute[] attributes)
			throws IdentityException {
		try {
			final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
			onEachAttribute: for (final IdentityObjectAttribute entry : attributes) {
				for (final SLAttributeEntry savedAttribute : identityAsSlId
						.getAttributes()) {
					if (savedAttribute.getName().equals(entry.getName())) {
						final Collection<String> newEntriesAsCollection = entry
								.getValues();
						savedAttribute.getEntries().addAll(
								newEntriesAsCollection);
						continue onEachAttribute;
					}
				}
				final SLAttributeEntry attribute = new SLAttributeEntry();
				attribute.setName(entry.getName());
				attribute.setParent(identityAsSlId);
				final Collection<String> entries = entry.getValues();
				attribute.setEntries(new HashSet<String>(entries));
				identityAsSlId.getAttributes().add(attribute);
			}
			this.addNodeToSave(invocationCtx, identityAsSlId);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}
	}

	public void addNodeToSave(
			final IdentityStoreInvocationContext invocationCtx,
			final SimpleNodeType node) throws Exception {
		this.getContext(invocationCtx).getSession().addNode(node);
	}

	public void bootstrap(
			final IdentityStoreConfigurationContext configurationContext)
			throws IdentityException {
		try {
			this.configurationMetaData = configurationContext
					.getStoreConfigurationMetaData();
			final String providerName = this.configurationMetaData
					.getOptionSingleValue(SLIdentityStoreImpl.JCR_PROVIDER_NAME);
			this.repositoryName = this.configurationMetaData
					.getOptionSingleValue(SLIdentityStoreImpl.REPOSITORY_NAME);
			this.providerDescriptor = DefaultJcrDescriptor
					.valueOf(providerName);
			this.provider = JcrConnectionProvider
					.createFromData(this.providerDescriptor);
			this.id = this.configurationMetaData.getId();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}

	}

	public IdentityObject createIdentityObject(
			final IdentityStoreInvocationContext invocationCtx,
			final String name, final IdentityObjectType identityObjectType)
			throws IdentityException {
		return this.createIdentityObject(invocationCtx, name,
				identityObjectType, null);
	}

	public IdentityObject createIdentityObject(
			final IdentityStoreInvocationContext invocationCtx,
			final String name, final IdentityObjectType identityObjectType,
			final Map<String, String[]> attributes) throws IdentityException {
		try {
			final String newId = UUID.randomUUID().toString();
			final SLIdentityObject id = new SLIdentityObject();
			id.setId(newId);
			id.setName(name);
			final SLIdentityObjectType type = new SLIdentityObjectType();
			type.setParent(id);
			type.setName(identityObjectType.getName());
			id.setIdentityType(type);
			if (attributes != null) {
				for (final Map.Entry<String, String[]> entry : attributes
						.entrySet()) {
					final SLAttributeEntry attribute = new SLAttributeEntry();
					attribute.setName(entry.getKey());
					attribute.setParent(id);
					attribute.setEntries(Collections.setOf(entry.getValue()));
					id.getAttributes().add(attribute);
				}
			}
			this.addNodeToSave(invocationCtx, id);
			return id;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}
	}

	public IdentityStoreSession createIdentityStoreSession()
			throws IdentityException {
		final Session session = this.provider.openSession();
		return new SLIdentityStoreSessionImpl(session, this.repositoryName);
	}

	public IdentityObjectRelationship createRelationship(
			final IdentityStoreInvocationContext invocationCxt,
			final IdentityObject fromIdentity, final IdentityObject toIdentity,
			final IdentityObjectRelationshipType relationshipType,
			final String relationshipName, final boolean createNames)
			throws IdentityException {
		try {
			final SLIdentityObjectRelationship relationship = new SLIdentityObjectRelationship();
			relationship.setFromIdentityObject(fromIdentity);
			relationship.setToIdentityObject(toIdentity);
			final SLIdentityObjectRelationshipType newType = new SLIdentityObjectRelationshipType();
			newType.setName(relationshipType.getName());
			newType.setParent(relationship);
			relationship.setType(newType);
			relationship.setName(relationshipName);
			this.createRelationshipName(invocationCxt, relationshipName);
			this.addNodeToSave(invocationCxt, relationship);
			this.addNodeToSave(invocationCxt, newType);

			return relationship;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}
	}

	public String createRelationshipName(
			final IdentityStoreInvocationContext ctx, final String name)
			throws IdentityException, OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");
	}

	public Collection<IdentityObject> findIdentityObject(
			final IdentityStoreInvocationContext invocationCxt,
			final IdentityObject identity,
			final IdentityObjectRelationshipType relationshipType,
			final boolean parent, final IdentityObjectSearchCriteria criteria)
			throws IdentityException {
		final String property = parent ? "fromIdentityObjectId"
				: "fromIdentityObjectIdfromIdentityObjectId";

		final SLIdentityStoreSessionContext sessionContext = this
				.getContext(invocationCxt);

		final Set<SLIdentityObjectRelationship> foundRelationShips = SimplePersistSupport
				.findNodesByProperties(sessionContext.getSession()
						.getRootNode(), sessionContext.getSession()
						.getSession(), SLIdentityObjectRelationship.class,
						LazyType.EAGER, new String[] { property },
						new Object[] { identity.getId() });
		final Set<String> ids = new HashSet<String>();
		for (final SLIdentityObjectRelationship relation : foundRelationShips) {
			ids.add(parent ? relation.getFromIdentityObjectId() : relation
					.getToIdentityObjectId());
		}
		final Set<IdentityObject> foundIdentities = new HashSet<IdentityObject>();
		for (final String id : ids) {
			foundIdentities.add(this.findIdentityObject(invocationCxt, id));
		}
		// FIXME use search criteria
		return foundIdentities;
	}

	public Collection<IdentityObject> findIdentityObject(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObjectType identityType,
			final IdentityObjectSearchCriteria criteria)
			throws IdentityException {
		final SLIdentityStoreSessionContext sessionContext = this
				.getContext(invocationCtx);

		final Set<SLIdentityObject> foundNodes = SimplePersistSupport
				.findNodesByProperties(sessionContext.getSession()
						.getRootNode(), sessionContext.getSession()
						.getSession(), SLIdentityObject.class, LazyType.EAGER,
						new String[] { "typeAsString" },
						new Object[] { identityType.getName() });
		final ArrayList<IdentityObject> foundNodesAsCollection = new ArrayList<IdentityObject>(
				foundNodes.size());
		foundNodesAsCollection.addAll(foundNodes);
		// FIXME use search criteria
		return foundNodesAsCollection;
	}

	public IdentityObject findIdentityObject(
			final IdentityStoreInvocationContext invocationContext,
			final String id) throws IdentityException {
		final SLIdentityStoreSessionContext sessionContext = this
				.getContext(invocationContext);

		final Set<SLIdentityObject> foundNodes = SimplePersistSupport
				.findNodesByProperties(sessionContext.getSession()
						.getRootNode(), sessionContext.getSession()
						.getSession(), SLIdentityObject.class, LazyType.EAGER,
						new String[] { "id" }, new Object[] { id });
		if (foundNodes.size() > 1) {
			throw Exceptions.logAndReturn(new IllegalStateException(
					"More than one result found"));
		}
		if (foundNodes.size() == 0) {
			return null;
		}
		return foundNodes.iterator().next();
	}

	public IdentityObject findIdentityObject(
			final IdentityStoreInvocationContext invocationContext,
			final String name, final IdentityObjectType identityObjectType)
			throws IdentityException {
		final SLIdentityStoreSessionContext sessionContext = this
				.getContext(invocationContext);

		final Set<SLIdentityObject> foundNodes = SimplePersistSupport
				.findNodesByProperties(sessionContext.getSession()
						.getRootNode(), sessionContext.getSession()
						.getSession(), SLIdentityObject.class, LazyType.EAGER,
						new String[] { "name", "typeAsString" }, new Object[] {
								name, identityObjectType.getName() });
		if (foundNodes.size() > 1) {
			throw Exceptions.logAndReturn(new IllegalStateException(
					"More than one result found"));
		}
		if (foundNodes.size() == 0) {
			return null;
		}
		return foundNodes.iterator().next();

	}

	public IdentityObject findIdentityObjectByUniqueAttribute(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObjectType identityObjectType,
			final IdentityObjectAttribute attribute) throws IdentityException {
		// TODO Auto-generated method stub
		return null;
	}

	public IdentityObjectAttribute getAttribute(
			final IdentityStoreInvocationContext invocationContext,
			final IdentityObject identity, final String name)
			throws IdentityException {
		final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
		final Set<SLTransientIdentityObjectAttribute> allAttributes = identityAsSlId
				.getAttributesAsIdentityAttributes();
		for (final SLTransientIdentityObjectAttribute att : allAttributes) {
			if (name.equals(att.getName())) {
				return att;
			}
		}

		return null;
	}

	public Map<String, IdentityObjectAttribute> getAttributes(
			final IdentityStoreInvocationContext invocationContext,
			final IdentityObject identity) throws IdentityException {
		final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
		final Set<SLTransientIdentityObjectAttribute> allAttributes = identityAsSlId
				.getAttributesAsIdentityAttributes();
		final Map<String, IdentityObjectAttribute> result = new HashMap<String, IdentityObjectAttribute>();
		for (final SLTransientIdentityObjectAttribute att : allAttributes) {
			result.put(att.getName(), att);
		}
		return result;
	}

	public Map<String, IdentityObjectAttributeMetaData> getAttributesMetaData(
			final IdentityStoreInvocationContext invocationContext,
			final IdentityObjectType identityType) {
		// TODO Auto-generated method stub
		return null;
	}

	public SLIdentityStoreSessionContext getContext(
			final IdentityStoreInvocationContext ctx) throws IdentityException {
		final SLIdentityStoreSessionContext sessionContext = (SLIdentityStoreSessionContext) ctx
				.getIdentityStoreSession().getSessionContext();
		if (sessionContext == null) {
			throw Exceptions.logAndReturn(new IllegalStateException());
		}
		return sessionContext;
	}

	public String getId() {
		return this.id;
	}

	public int getIdentityObjectsCount(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObjectType identityType) throws IdentityException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map<String, String> getRelationshipNameProperties(
			final IdentityStoreInvocationContext ctx, final String name)
			throws IdentityException, OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");

	}

	public Set<String> getRelationshipNames(
			final IdentityStoreInvocationContext ctx) throws IdentityException,
			OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");
	}

	public Set<String> getRelationshipNames(
			final IdentityStoreInvocationContext ctx,
			final IdentityObject identity) throws IdentityException,
			OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");
	}

	public Set<String> getRelationshipNames(
			final IdentityStoreInvocationContext ctx,
			final IdentityObject identity,
			final IdentityObjectSearchCriteria criteria)
			throws IdentityException, OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");

	}

	public Set<String> getRelationshipNames(
			final IdentityStoreInvocationContext ctx,
			final IdentityObjectSearchCriteria criteria)
			throws IdentityException, OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");
	}

	public Map<String, String> getRelationshipProperties(
			final IdentityStoreInvocationContext ctx,
			final IdentityObjectRelationship relationship)
			throws IdentityException, OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Relationship properties are not supported");

	}

	public Set<String> getSupportedAttributeNames(
			final IdentityStoreInvocationContext invocationContext,
			final IdentityObjectType identityType) throws IdentityException {
		// TODO Auto-generated method stub
		return null;
	}

	public FeaturesMetaData getSupportedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAttributes(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObject identity, final String[] attributeNames)
			throws IdentityException {
		try {
			final SLIdentityObject typedIdObj = (SLIdentityObject) identity;
			final Set<String> allAttributesToRemove = Collections
					.setOf(attributeNames);
			for (final SLAttributeEntry attribute : new ArrayList<SLAttributeEntry>(
					typedIdObj.getAttributes())) {
				if (allAttributesToRemove.contains(attribute.getName())) {
					typedIdObj.getAttributes().remove(attribute);
				}
			}
			final SLIdentityStoreSessionContext sessionContext = this
					.getContext(invocationCtx);
			sessionContext.getSession().addNode(typedIdObj);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}
	}

	public void removeIdentityObject(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObject identity) throws IdentityException {
		try {
			final SLIdentityObject typedIdObj = (SLIdentityObject) identity;
			final SLIdentityStoreSessionContext sessionContext = this
					.getContext(invocationCtx);

			sessionContext.getSession().remove(typedIdObj);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}
	}

	public void removeRelationship(
			final IdentityStoreInvocationContext invocationCxt,
			final IdentityObject fromIdentity, final IdentityObject toIdentity,
			final IdentityObjectRelationshipType relationshipType,
			final String relationshipName) throws IdentityException {
		// TODO Auto-generated method stub

	}

	public String removeRelationshipName(
			final IdentityStoreInvocationContext ctx, final String name)
			throws IdentityException, OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");
	}

	public void removeRelationshipNameProperties(
			final IdentityStoreInvocationContext ctx, final String name,
			final Set<String> properties) throws IdentityException,
			OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");

	}

	public void removeRelationshipProperties(
			final IdentityStoreInvocationContext ctx,
			final IdentityObjectRelationship relationship,
			final Set<String> properties) throws IdentityException,
			OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Relationship properties are not supported");

	}

	public void removeRelationships(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObject identity1, final IdentityObject identity2,
			final boolean named) throws IdentityException {
		// TODO Auto-generated method stub

	}

	public Set<IdentityObjectRelationship> resolveRelationships(
			final IdentityStoreInvocationContext invocationCxt,
			final IdentityObject fromIdentity, final IdentityObject toIdentity,
			final IdentityObjectRelationshipType relationshipType)
			throws IdentityException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IdentityObjectRelationship> resolveRelationships(
			final IdentityStoreInvocationContext invocationCxt,
			final IdentityObject identity,
			final IdentityObjectRelationshipType relationshipType,
			final boolean parent, final boolean named, final String name)
			throws IdentityException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRelationshipNameProperties(
			final IdentityStoreInvocationContext ctx, final String name,
			final Map<String, String> properties) throws IdentityException,
			OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Named relationships are not supported");

	}

	public void setRelationshipProperties(
			final IdentityStoreInvocationContext ctx,
			final IdentityObjectRelationship relationship,
			final Map<String, String> properties) throws IdentityException,
			OperationNotSupportedException {
		throw new OperationNotSupportedException(
				"Relationship properties are not supported");

	}

	public void updateAttributes(
			final IdentityStoreInvocationContext invocationCtx,
			final IdentityObject identity,
			final IdentityObjectAttribute[] attributes)
			throws IdentityException {
		try {
			final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
			for (final IdentityObjectAttribute entry : attributes) {
				final SLAttributeEntry attribute = new SLAttributeEntry();
				attribute.setName(entry.getName());
				attribute.setParent(identityAsSlId);
				final Collection<String> entries = entry.getValues();
				attribute.setEntries(new HashSet<String>(entries));
				identityAsSlId.getAttributes().add(attribute);
			}
			this.addNodeToSave(invocationCtx, identityAsSlId);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}
	}

	public void updateCredential(final IdentityStoreInvocationContext ctx,
			final IdentityObject identityObject,
			final IdentityObjectCredential credential) throws IdentityException {
		// TODO Auto-generated method stub

	}

	public boolean validateCredential(final IdentityStoreInvocationContext ctx,
			final IdentityObject identityObject,
			final IdentityObjectCredential credential) throws IdentityException {
		// TODO Auto-generated method stub
		return false;
	}

}
