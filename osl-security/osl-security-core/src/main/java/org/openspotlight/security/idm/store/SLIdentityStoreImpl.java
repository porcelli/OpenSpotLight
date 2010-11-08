/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.security.idm.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jboss.identity.idm.common.exception.IdentityException;
import org.jboss.identity.idm.impl.store.FeaturesMetaDataImpl;
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
import org.jboss.identity.idm.spi.store.IdentityObjectSearchCriteriaType;
import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.domain.SLAttributeEntry;
import org.openspotlight.security.domain.SLIdentityObject;
import org.openspotlight.security.domain.SLIdentityObjectRelationship;
import org.openspotlight.security.domain.SLIdentityObjectRelationshipType;
import org.openspotlight.security.domain.SLIdentityObjectType;
import org.openspotlight.security.domain.SLPasswordEntry;
import org.openspotlight.security.domain.SLTransientIdentityObjectAttribute;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.domain.StorageNode;

import com.google.inject.Injector;

public class SLIdentityStoreImpl implements IdentityStore, Serializable {

    private static final long                                 serialVersionUID = -1340118659974257278L;

    private IdentityStoreConfigurationMetaData                configurationMetaData;

    private String                                            id;

    private StorageNode                                       rootNode;

    private FeaturesMetaData                                  supportedFeatures;
    private SimplePersistCapable<StorageNode, StorageSession> simplePersist;

    public SLIdentityStoreImpl() {}

    @Override
    @SuppressWarnings("unchecked")
    public void addAttributes(final IdentityStoreInvocationContext invocationCtx,
                               final IdentityObject identity,
                               final IdentityObjectAttribute[] attributes)
        throws IdentityException {
        try {
            final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
            onEachAttribute: for (final IdentityObjectAttribute entry: attributes) {
                for (final SLAttributeEntry savedAttribute: identityAsSlId.getAttributes()) {
                    if (savedAttribute.getName().equals(entry.getName())) {
                        final Collection<String> newEntriesAsCollection = entry.getValues();
                        savedAttribute.getEntries().addAll(newEntriesAsCollection);
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
            addNodeToSave(invocationCtx, identityAsSlId);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    public void addNodeToSave(final IdentityStoreInvocationContext invocationCtx,
                               final SimpleNodeType node)
        throws Exception {
        getContext(invocationCtx).getSession().addNode(node);
    }

    @Override
    public void bootstrap(final IdentityStoreConfigurationContext configurationContext)
        throws IdentityException {
        try {
            configurationMetaData = configurationContext.getStoreConfigurationMetaData();
            id = configurationMetaData.getId();
            supportedFeatures = new FeaturesMetaDataImpl(configurationMetaData,
                                                         java.util.Collections.<IdentityObjectSearchCriteriaType>emptySet(),
                                                         true, true, java.util.Collections.<String>emptySet());
            final Injector injector = StaticInjector.INSTANCE.getInjector();
            final StorageSession session = injector.getInstance(StorageSession.class);

            final SimplePersistFactory factory = injector.getInstance(SimplePersistFactory.class);
            simplePersist = factory.createSimplePersist(RegularPartitions.SECURITY);

        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }

    }

    @Override
    public IdentityObject createIdentityObject(final IdentityStoreInvocationContext invocationCtx,
                                                final String name,
                                                final IdentityObjectType identityObjectType)
        throws IdentityException {
        return this.createIdentityObject(invocationCtx, name, identityObjectType, null);
    }

    @Override
    public IdentityObject createIdentityObject(final IdentityStoreInvocationContext invocationCtx,
                                                final String name,
                                                final IdentityObjectType identityObjectType,
                                                final Map<String, String[]> attributes)
        throws IdentityException {
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
                for (final Map.Entry<String, String[]> entry: attributes.entrySet()) {
                    final SLAttributeEntry attribute = new SLAttributeEntry();
                    attribute.setName(entry.getKey());
                    attribute.setParent(id);
                    attribute.setEntries(SLCollections.setOf(entry.getValue()));
                    id.getAttributes().add(attribute);
                }
            }
            addNodeToSave(invocationCtx, id);
            return id;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public IdentityStoreSession createIdentityStoreSession()
        throws IdentityException {
        return new SLIdentityStoreSessionImpl(simplePersist);
    }

    @Override
    public IdentityObjectRelationship createRelationship(final IdentityStoreInvocationContext invocationCxt,
                                                          final IdentityObject fromIdentity,
                                                          final IdentityObject toIdentity,
                                                          final IdentityObjectRelationshipType relationshipType,
                                                          final String relationshipName,
                                                          final boolean createNames)
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
            addNodeToSave(invocationCxt, relationship);
            return relationship;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public String createRelationshipName(final IdentityStoreInvocationContext ctx,
                                          final String name)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");
    }

    @Override
    public Collection<IdentityObject> findIdentityObject(final IdentityStoreInvocationContext invocationCxt,
                                                          final IdentityObject identity,
                                                          final IdentityObjectRelationshipType relationshipType,
                                                          final boolean parent,
                                                          final IdentityObjectSearchCriteria criteria)
        throws IdentityException {
        final String property = parent ? "fromIdentityObjectId" : "toIdentityObjectId";

        final Iterable<SLIdentityObjectRelationship> foundRelationShips =
            simplePersist
                .findByProperties(
                                                                                                         SLIdentityObjectRelationship.class,
                                                                                                         new String[] {property},
                                                                                                         new Object[] {identity
                                                                                                             .getId()});
        final List<String> ids = new ArrayList<String>();
        for (final SLIdentityObjectRelationship relation: foundRelationShips) {
            ids.add(parent ? relation.getFromIdentityObjectId() : relation.getToIdentityObjectId());
        }
        final List<IdentityObject> foundIdentities = new ArrayList<IdentityObject>();
        for (final String id: ids) {
            foundIdentities.add(this.findIdentityObject(invocationCxt, id));
        }
        // FIXME use search criteria
        return foundIdentities;
    }

    @Override
    public Collection<IdentityObject> findIdentityObject(final IdentityStoreInvocationContext invocationCtx,
                                                          final IdentityObjectType identityType,
                                                          final IdentityObjectSearchCriteria criteria)
        throws IdentityException {

        final Iterable<SLIdentityObject> foundNodes = simplePersist.findByProperties(rootNode, SLIdentityObject.class,
                                                                                     new String[] {"typeAsString"},
                                                                                     new Object[] {identityType.getName()});
        final List<IdentityObject> foundNodesAsCollection = new LinkedList<IdentityObject>();
        for (final IdentityObject o: foundNodes) {
            foundNodesAsCollection.add(o);
        }
        // FIXME use search criteria
        return foundNodesAsCollection;
    }

    @Override
    public IdentityObject findIdentityObject(final IdentityStoreInvocationContext invocationContext,
                                              final String id)
        throws IdentityException {

        final Iterable<SLIdentityObject> foundNodes = simplePersist.findByProperties(SLIdentityObject.class, new String[] {"id"},
                                                                                     new Object[] {id});
        if (!foundNodes.iterator().hasNext()) { return null; }
        return foundNodes.iterator().next();
    }

    @Override
    public IdentityObject findIdentityObject(final IdentityStoreInvocationContext invocationContext,
                                              final String name,
                                              final IdentityObjectType identityObjectType)
        throws IdentityException {

        final Iterable<SLIdentityObject> foundNodes = simplePersist.findByProperties(SLIdentityObject.class, new String[] {
            "name", "typeAsString"}, new Object[] {name, identityObjectType.getName()});
        if (!foundNodes.iterator().hasNext()) { return null; }
        return foundNodes.iterator().next();

    }

    @Override
    public IdentityObject findIdentityObjectByUniqueAttribute(final IdentityStoreInvocationContext invocationCtx,
                                                               final IdentityObjectType identityObjectType,
                                                               final IdentityObjectAttribute attribute)
        throws IdentityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IdentityObjectAttribute getAttribute(final IdentityStoreInvocationContext invocationContext,
                                                 final IdentityObject identity,
                                                 final String name)
        throws IdentityException {
        final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
        final Set<SLTransientIdentityObjectAttribute> allAttributes = identityAsSlId.getAttributesAsIdentityAttributes();
        for (final SLTransientIdentityObjectAttribute att: allAttributes) {
            if (name.equals(att.getName())) { return att; }
        }

        return null;
    }

    @Override
    public Map<String, IdentityObjectAttribute> getAttributes(final IdentityStoreInvocationContext invocationContext,
                                                               final IdentityObject identity)
        throws IdentityException {
        final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
        final Set<SLTransientIdentityObjectAttribute> allAttributes = identityAsSlId.getAttributesAsIdentityAttributes();
        final Map<String, IdentityObjectAttribute> result = new HashMap<String, IdentityObjectAttribute>();
        for (final SLTransientIdentityObjectAttribute att: allAttributes) {
            result.put(att.getName(), att);
        }
        return result;
    }

    @Override
    public Map<String, IdentityObjectAttributeMetaData>
        getAttributesMetaData(final IdentityStoreInvocationContext invocationContext,
                                                                               final IdentityObjectType identityType) {
        // TODO Auto-generated method stub
        return null;
    }

    public SLIdentityStoreSessionContext getContext(final IdentityStoreInvocationContext ctx)
        throws IdentityException {
        final SLIdentityStoreSessionContext sessionContext =
            (SLIdentityStoreSessionContext) ctx.getIdentityStoreSession().getSessionContext();
        if (sessionContext == null) { throw Exceptions.logAndReturn(new IllegalStateException()); }
        return sessionContext;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getIdentityObjectsCount(final IdentityStoreInvocationContext invocationCtx,
                                        final IdentityObjectType identityType)
        throws IdentityException {
        final Collection<IdentityObject> foundIds = this.findIdentityObject(invocationCtx, identityType, null);
        return foundIds.size();
    }

    @Override
    public Map<String, String> getRelationshipNameProperties(final IdentityStoreInvocationContext ctx,
                                                              final String name)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");

    }

    public Set<String> getRelationshipNames(final IdentityStoreInvocationContext ctx)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");
    }

    public Set<String> getRelationshipNames(final IdentityStoreInvocationContext ctx,
                                             final IdentityObject identity)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");
    }

    @Override
    public Set<String> getRelationshipNames(final IdentityStoreInvocationContext ctx,
                                             final IdentityObject identity,
                                             final IdentityObjectSearchCriteria criteria)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");

    }

    @Override
    public Set<String> getRelationshipNames(final IdentityStoreInvocationContext ctx,
                                             final IdentityObjectSearchCriteria criteria)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");
    }

    @Override
    public Map<String, String> getRelationshipProperties(final IdentityStoreInvocationContext ctx,
                                                          final IdentityObjectRelationship relationship)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Relationship properties are not supported");

    }

    @Override
    public Set<String> getSupportedAttributeNames(final IdentityStoreInvocationContext invocationContext,
                                                   final IdentityObjectType identityType)
        throws IdentityException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeaturesMetaData getSupportedFeatures() {
        return supportedFeatures;
    }

    private Set<IdentityObjectRelationship> internalResolveRelationships(final IdentityStoreInvocationContext invocationCxt,
                                                                          final IdentityObject fromIdentity,
                                                                          final IdentityObject toIdentity,
                                                                          final IdentityObjectRelationshipType relationshipType,
                                                                          final String name)
        throws IdentityException {

        final List<String> parameterNames = new ArrayList<String>();
        final List<Object> parameterValues = new ArrayList<Object>();
        if (name != null) {
            parameterNames.add("name");
            parameterValues.add(name);
        }
        if (relationshipType != null) {
            parameterNames.add("typeAsString");
            parameterValues.add(relationshipType.getName());
        }
        if (fromIdentity != null) {
            parameterNames.add("fromIdentityObjectId");
            parameterValues.add(fromIdentity.getId());
        }
        if (toIdentity != null) {
            parameterNames.add("toIdentityObjectId");
            parameterValues.add(toIdentity.getId());
        }

        final Iterable<SLIdentityObjectRelationship> foundNodes =
            simplePersist
                .findByProperties(
                                                                                                 SLIdentityObjectRelationship.class,
                                                                                                 parameterNames
                                                                                                     .toArray(new String[0]),
                                                                                                 parameterValues.toArray());

        final Set<IdentityObjectRelationship> result = new HashSet<IdentityObjectRelationship>();

        for (final SLIdentityObjectRelationship relationship: foundNodes) {
            final IdentityObject newFrom = this.findIdentityObject(invocationCxt, relationship.getFromIdentityObjectId());
            relationship.setFromIdentityObject(newFrom);
            final IdentityObject newTo = this.findIdentityObject(invocationCxt, relationship.getToIdentityObjectId());
            relationship.setToIdentityObject(newTo);
            result.add(relationship);
        }
        return result;
    }

    @Override
    public void removeAttributes(final IdentityStoreInvocationContext invocationCtx,
                                  final IdentityObject identity,
                                  final String[] attributeNames)
        throws IdentityException {
        try {
            final SLIdentityObject typedIdObj = (SLIdentityObject) identity;
            final Set<String> allAttributesToRemove = SLCollections.setOf(attributeNames);
            for (final SLAttributeEntry attribute: new ArrayList<SLAttributeEntry>(typedIdObj.getAttributes())) {
                if (allAttributesToRemove.contains(attribute.getName())) {
                    typedIdObj.getAttributes().remove(attribute);
                }
            }
            final SLIdentityStoreSessionContext sessionContext = getContext(invocationCtx);
            sessionContext.getSession().addNode(typedIdObj);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public void removeIdentityObject(final IdentityStoreInvocationContext invocationCtx,
                                      final IdentityObject identity)
        throws IdentityException {
        try {
            final SLIdentityObject typedIdObj = (SLIdentityObject) identity;
            final SLIdentityStoreSessionContext sessionContext = getContext(invocationCtx);

            sessionContext.getSession().remove(typedIdObj);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public void removeRelationship(final IdentityStoreInvocationContext invocationCxt,
                                    final IdentityObject fromIdentity,
                                    final IdentityObject toIdentity,
                                    final IdentityObjectRelationshipType relationshipType,
                                    final String relationshipName)
        throws IdentityException {
        try {
            final Set<IdentityObjectRelationship> result = internalResolveRelationships(invocationCxt, fromIdentity, toIdentity,
                                                                                        relationshipType, relationshipName);
            final SLIdentityStoreSessionContext sessionContext = getContext(invocationCxt);
            for (final IdentityObjectRelationship relationShip: result) {
                sessionContext.getSession().remove((SLIdentityObjectRelationship) relationShip);
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public String removeRelationshipName(final IdentityStoreInvocationContext ctx,
                                          final String name)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");
    }

    @Override
    public void removeRelationshipNameProperties(final IdentityStoreInvocationContext ctx,
                                                  final String name,
                                                  final Set<String> properties)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");

    }

    @Override
    public void removeRelationshipProperties(final IdentityStoreInvocationContext ctx,
                                              final IdentityObjectRelationship relationship,
                                              final Set<String> properties)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Relationship properties are not supported");

    }

    @Override
    public void removeRelationships(final IdentityStoreInvocationContext invocationCtx,
                                     final IdentityObject identity1,
                                     final IdentityObject identity2,
                                     final boolean named)
        throws IdentityException {
        try {
            final Set<IdentityObjectRelationship> result1 = internalResolveRelationships(invocationCtx, identity1, identity2,
                                                                                         null, null);
            final Set<IdentityObjectRelationship> result2 = internalResolveRelationships(invocationCtx, identity2, identity1,
                                                                                         null, null);
            final Set<IdentityObjectRelationship> result = new HashSet<IdentityObjectRelationship>();
            result.addAll(result1);
            result.addAll(result2);
            if (!named) {
                for (final IdentityObjectRelationship r: new ArrayList<IdentityObjectRelationship>(result)) {
                    if (r.getName() != null) {
                        result.remove(r);
                    }
                }
            }
            final SLIdentityStoreSessionContext sessionContext = getContext(invocationCtx);
            for (final IdentityObjectRelationship relationShip: result) {
                sessionContext.getSession().remove((SLIdentityObjectRelationship) relationShip);
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }

    }

    @Override
    public Set<IdentityObjectRelationship> resolveRelationships(final IdentityStoreInvocationContext invocationCxt,
                                                                 final IdentityObject fromIdentity,
                                                                 final IdentityObject toIdentity,
                                                                 final IdentityObjectRelationshipType relationshipType)
        throws IdentityException {

        return internalResolveRelationships(invocationCxt, fromIdentity, toIdentity, relationshipType, null);
    }

    @Override
    public Set<IdentityObjectRelationship> resolveRelationships(final IdentityStoreInvocationContext invocationCxt,
                                                                 final IdentityObject identity,
                                                                 final IdentityObjectRelationshipType relationshipType,
                                                                 final boolean parent,
                                                                 final boolean named,
                                                                 final String name)
        throws IdentityException {
        final IdentityObject from = parent ? identity : null;
        final IdentityObject to = parent ? null : identity;
        return internalResolveRelationships(invocationCxt, from, to, relationshipType, name);
    }

    @Override
    public void setRelationshipNameProperties(final IdentityStoreInvocationContext ctx,
                                               final String name,
                                               final Map<String, String> properties)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Named relationships are not supported");

    }

    @Override
    public void setRelationshipProperties(final IdentityStoreInvocationContext ctx,
                                           final IdentityObjectRelationship relationship,
                                           final Map<String, String> properties)
        throws IdentityException, OperationNotSupportedException {
        throw new OperationNotSupportedException("Relationship properties are not supported");

    }

    @Override
    public void updateAttributes(final IdentityStoreInvocationContext invocationCtx,
                                  final IdentityObject identity,
                                  final IdentityObjectAttribute[] attributes)
        throws IdentityException {
        try {
            final SLIdentityObject identityAsSlId = (SLIdentityObject) identity;
            for (final IdentityObjectAttribute entry: attributes) {
                final SLAttributeEntry attribute = new SLAttributeEntry();
                attribute.setName(entry.getName());
                attribute.setParent(identityAsSlId);
                @SuppressWarnings("unchecked")
                final Collection<String> entries = entry.getValues();
                attribute.setEntries(new HashSet<String>(entries));
                identityAsSlId.getAttributes().add(attribute);
            }
            addNodeToSave(invocationCtx, identityAsSlId);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public void updateCredential(final IdentityStoreInvocationContext ctx,
                                  final IdentityObject identityObject,
                                  final IdentityObjectCredential credential)
        throws IdentityException {
        try {
            final SLPasswordEntry entry = SLPasswordEntry.create(identityObject, credential);
            addNodeToSave(ctx, entry);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }
    }

    @Override
    public boolean validateCredential(final IdentityStoreInvocationContext ctx,
                                       final IdentityObject identityObject,
                                       final IdentityObjectCredential credential)
        throws IdentityException {
        try {

            final Iterable<SLPasswordEntry> foundNodes = simplePersist.findByProperties(SLPasswordEntry.class,
                                                                                        new String[] {"userId"},
                                                                                        new Object[] {identityObject.getId()});
            if (!foundNodes.iterator().hasNext()) { return false; }
            final SLPasswordEntry entry = foundNodes.iterator().next();
            return entry.isValid(identityObject, credential);

        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, IdentityException.class);
        }

    }

}
