/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.identity.idm.impl.store.hibernate;

import org.jboss.identity.idm.common.exception.IdentityException;
import org.jboss.identity.idm.impl.model.hibernate.*;
import org.jboss.identity.idm.impl.store.FeaturesMetaDataImpl;
import org.jboss.identity.idm.spi.configuration.IdentityStoreConfigurationContext;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityObjectAttributeMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityObjectTypeMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.IdentityStoreConfigurationMetaData;
import org.jboss.identity.idm.spi.configuration.metadata.RealmConfigurationMetaData;
import org.jboss.identity.idm.spi.exception.OperationNotSupportedException;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectAttribute;
import org.jboss.identity.idm.spi.model.IdentityObjectCredential;
import org.jboss.identity.idm.spi.model.IdentityObjectCredentialType;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationship;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;
import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.jboss.identity.idm.spi.search.IdentityObjectSearchCriteria;
import org.jboss.identity.idm.spi.store.FeaturesMetaData;
import org.jboss.identity.idm.spi.store.IdentityObjectSearchCriteriaType;
import org.jboss.identity.idm.spi.store.IdentityStore;
import org.jboss.identity.idm.spi.store.IdentityStoreInvocationContext;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateIdentityStoreImpl implements IdentityStore, Serializable
{

   //TODO: logging

   private final String QUERY_RELATIONSHIP_BY_FROM_TO =
      "select r from HibernateIdentityObjectRelationship r where r.fromIdentityObject = :fromIO and " +
         "r.toIdentityObject = :toIO";

   private final String QUERY_RELATIONSHIP_BY_FROM_TO_TYPE =
      "select r from HibernateIdentityObjectRelationship r where r.fromIdentityObject = :fromIO and " +
         "r.toIdentityObject = :toIO and r.type.name = :typeName";

   private final String QUERY_RELATIONSHIP_BY_FROM_TO_TYPE_NAME =
      "select r from HibernateIdentityObjectRelationship r where r.fromIdentityObject = :fromIO and " +
         "r.toIdentityObject = :toIO and r.type.name = :typeName and r.name.name = :name";

   private final String QUERY_RELATIONSHIP_BY_IDENTITIES =
      "select r from HibernateIdentityObjectRelationship r where " +
         "(r.fromIdentityObject = :IO1 and r.toIdentityObject = :IO2) or " +
         "(r.fromIdentityObject = :IO2 and r.toIdentityObject = :IO1)";

   public static final String HIBERNATE_SESSION_FACTORY_REGISTRY_NAME = "hibernateSessionFactoryRegistryName";

   public static final String HIBERNATE_CONFIGURATION = "hibernateConfiguration";

   public static final String ADD_HIBERNATE_MAPPINGS = "addHibernateMappings";

   public static final String HIBERNATE_SESSION_FACTORY_JNDI_NAME = "hibernateSessionFactoryJNDIName";

   public static final String POPULATE_MEMBERSHIP_TYPES = "populateRelationshipTypes";

   public static final String POPULATE_IDENTITY_OBJECT_TYPES = "populateIdentityObjectTypes";

   public static final String IS_REALM_AWARE = "isRealmAware";
   
   public static final String MANAGE_TRANSACTION_DURING_BOOTSTRAP = "manageTransactionDuringBootstrap";

   public static final String ALLOW_NOT_DEFINED_ATTRIBUTES = "allowNotDefinedAttributes";

   public static final String ALLOW_NOT_DEFINED_IDENTITY_OBJECT_TYPES_OPTION = "allowNotDefinedIdentityObjectTypes";

   public static final String DEFAULT_REALM_NAME = HibernateIdentityStoreImpl.class.getName() + ".DEFAULT_REALM";

   public static final String CREDENTIAL_TYPE_PASSWORD = "PASSWORD";

   public static final String CREDENTIAL_TYPE_BINARY = "BINARY";

   private String id;

   private FeaturesMetaData supportedFeatures;

   private SessionFactory sessionFactory;

   private boolean isRealmAware = false;

   private boolean isAllowNotDefinedAttributes = false;

   private boolean isAllowNotDefinedIdentityObjectTypes = false;

   private boolean isManageTransactionDuringBootstrap = true;

   // TODO: rewrite this into some more handy object
   private IdentityStoreConfigurationMetaData configurationMD;

   private static Set<IdentityObjectSearchCriteriaType> supportedIdentityObjectSearchCriteria =
      new HashSet<IdentityObjectSearchCriteriaType>();

   private static Set<String> supportedCredentialTypes = new HashSet<String>();

   // <IdentityObjectType name, Set<Attribute name>>
   private Map<String, Set<String>> attributeMappings = new HashMap<String, Set<String>>();

   // <IdentityObjectType name, <Attribute name, MD>
   private Map<String, Map<String, IdentityObjectAttributeMetaData>> attributesMetaData = new HashMap<String, Map<String, IdentityObjectAttributeMetaData>>();

   // <IdentityObjectType name, <Attribute store mapping, Attribute name>
   private Map<String, Map<String, String>> reverseAttributeMappings = new HashMap<String, Map<String, String>>();
   
   private static final long serialVersionUID = -130355852189832805L;

   static {
      // List all supported criteria classes

      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.ATTRIBUTE_FILTER);
      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.NAME_FILTER);
      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.PAGE);
      supportedIdentityObjectSearchCriteria.add(IdentityObjectSearchCriteriaType.SORT);

      // credential types supported by this impl
      supportedCredentialTypes.add(CREDENTIAL_TYPE_PASSWORD);
      supportedCredentialTypes.add(CREDENTIAL_TYPE_BINARY);
      
   }

   public HibernateIdentityStoreImpl(String id)
   {
      this.id = id;
   }

   public void bootstrap(IdentityStoreConfigurationContext configurationContext) throws IdentityException
   {
      this.configurationMD = configurationContext.getStoreConfigurationMetaData();

      id = configurationMD.getId();

      supportedFeatures = new FeaturesMetaDataImpl(configurationMD, supportedIdentityObjectSearchCriteria, true, true, new HashSet<String>());


      String populateMembershipTypes = configurationMD.getOptionSingleValue(POPULATE_MEMBERSHIP_TYPES);
      String populateIdentityObjectTypes = configurationMD.getOptionSingleValue(POPULATE_IDENTITY_OBJECT_TYPES);

      String manageTransactionDuringBootstrap = configurationMD.getOptionSingleValue(MANAGE_TRANSACTION_DURING_BOOTSTRAP);

      if (manageTransactionDuringBootstrap != null && manageTransactionDuringBootstrap.equalsIgnoreCase("false"))
      {
         this.isAllowNotDefinedAttributes = false;
      }

      sessionFactory = bootstrapHibernateSessionFactory(configurationContext);

      Session hibernateSession = sessionFactory.openSession();

      // Attribute mappings - helper structures

      for (IdentityObjectTypeMetaData identityObjectTypeMetaData : configurationMD.getSupportedIdentityTypes())
      {
         Set<String> names = new HashSet<String>();
         Map<String, IdentityObjectAttributeMetaData> metadataMap = new HashMap<String, IdentityObjectAttributeMetaData>();
         Map<String, String> reverseMap = new HashMap<String, String>();
         for (IdentityObjectAttributeMetaData attributeMetaData : identityObjectTypeMetaData.getAttributes())
         {
            names.add(attributeMetaData.getName());
            metadataMap.put(attributeMetaData.getName(), attributeMetaData);
            if (attributeMetaData.getStoreMapping() != null)
            {
               reverseMap.put(attributeMetaData.getStoreMapping(), attributeMetaData.getName());
            }
         }

         // Use unmodifiableSet as it'll be exposed directly 
         attributeMappings.put(identityObjectTypeMetaData.getName(), Collections.unmodifiableSet(names));

         attributesMetaData.put(identityObjectTypeMetaData.getName(), metadataMap);

         reverseAttributeMappings.put(identityObjectTypeMetaData.getName(), reverseMap);
      }

      attributeMappings = Collections.unmodifiableMap(attributeMappings);

      if (isManageTransactionDuringBootstrap())
      {
         hibernateSession.getTransaction().begin();
      }

      if (populateMembershipTypes != null && populateMembershipTypes.equalsIgnoreCase("true"))
      {
         List<String> memberships = new LinkedList<String>();

         for (String membership : configurationMD.getSupportedRelationshipTypes())
         {
            memberships.add(membership);
         }

         try
         {
            populateRelationshipTypes(hibernateSession, memberships.toArray(new String[memberships.size()]));
         }
         catch (Exception e)
         {
            throw new IdentityException("Failed to populate relationship types", e);
         }


      }

      if (populateIdentityObjectTypes != null && populateIdentityObjectTypes.equalsIgnoreCase("true"))
      {
         List<String> types = new LinkedList<String>();

         for (IdentityObjectTypeMetaData metaData : configurationMD.getSupportedIdentityTypes())
         {
            types.add(metaData.getName());
         }

         try
         {
            populateObjectTypes(hibernateSession, types.toArray(new String[types.size()]));
         }
         catch (Exception e)
         {
            throw new IdentityException("Failed to populate identity object types", e);
         }

      }

      if (supportedCredentialTypes != null && supportedCredentialTypes.size() > 0)
      {
         try
         {
            populateCredentialTypes(hibernateSession, supportedCredentialTypes.toArray(new String[supportedCredentialTypes.size()]));
         }
         catch (Exception e)
         {
            throw new IdentityException("Failed to populated credential types");
         }
      }

      String realmAware = configurationMD.getOptionSingleValue(IS_REALM_AWARE);

      if (realmAware != null && realmAware.equalsIgnoreCase("true"))
      {
         this.isRealmAware = true;
      }

      String allowNotDefineAttributes = configurationMD.getOptionSingleValue(ALLOW_NOT_DEFINED_ATTRIBUTES);

      if (allowNotDefineAttributes != null && allowNotDefineAttributes.equalsIgnoreCase("true"))
      {
         this.isAllowNotDefinedAttributes = true;
      }

      String allowNotDefinedIOT = configurationMD.getOptionSingleValue(ALLOW_NOT_DEFINED_IDENTITY_OBJECT_TYPES_OPTION);

      if (allowNotDefinedIOT != null && allowNotDefinedIOT.equalsIgnoreCase("true"))
      {
         this.isAllowNotDefinedIdentityObjectTypes = true;
      }

      // Default realm

      HibernateRealm realm = null;

      try
      {


         realm = (HibernateRealm)hibernateSession.
            createCriteria(HibernateRealm.class).add(Restrictions.eq("name", DEFAULT_REALM_NAME)).uniqueResult();


      }
      catch (HibernateException e)
      {
         // Realm does not exist
      }




      if (realm == null)
      {
         addRealm(hibernateSession, DEFAULT_REALM_NAME);
      }

      // If store is realm aware than creat all configured realms



      if (isRealmAware())
      {
         Set<String> realmNames = new HashSet<String>();

         for (RealmConfigurationMetaData realmMD : configurationContext.getConfigurationMetaData().getRealms())
         {
            realmNames.add(realmMD.getId());
         }

         for (String rid : realmNames)
         {
            realm = (HibernateRealm)hibernateSession.createCriteria(HibernateRealm.class).
               add(Restrictions.eq("name",rid)).setCacheable(true).uniqueResult();

            if (realm == null)
            {
               addRealm(hibernateSession, rid);
            }
         }
      }

      if (isManageTransactionDuringBootstrap())
      {
         hibernateSession.getTransaction().commit();
      }

      hibernateSession.flush();

      hibernateSession.close();

   }

   protected SessionFactory bootstrapHibernateSessionFactory(IdentityStoreConfigurationContext configurationContext) throws IdentityException
   {

      String sfJNDIName = configurationContext.getStoreConfigurationMetaData().
         getOptionSingleValue(HIBERNATE_SESSION_FACTORY_JNDI_NAME);
      String sfRegistryName = configurationContext.getStoreConfigurationMetaData().
         getOptionSingleValue(HIBERNATE_SESSION_FACTORY_REGISTRY_NAME);
      String addMappedClasses = configurationContext.getStoreConfigurationMetaData().
         getOptionSingleValue(ADD_HIBERNATE_MAPPINGS);
      String hibernateConfiguration = configurationContext.getStoreConfigurationMetaData().
         getOptionSingleValue(HIBERNATE_CONFIGURATION);

      if (sfJNDIName != null)
      {
         try
         {
            return (SessionFactory)new InitialContext().lookup(sfJNDIName);
         }
         catch (NamingException e)
         {
            throw new IdentityException("Cannot obtain hibernate SessionFactory from provided JNDI name: " + sfJNDIName, e);
         }
      }
      else if (sfRegistryName != null)
      {
         Object registryObject = configurationContext.getConfigurationRegistry().getObject(sfRegistryName);

         if (registryObject == null)
         {
            throw new IdentityException("Cannot obtain hibernate SessionFactory from provided registry name: " + sfRegistryName);
         }

         if (!(registryObject instanceof SessionFactory))
         {
            throw new IdentityException("Cannot obtain hibernate SessionFactory from provided registry name: " + sfRegistryName
            + "; Registered object is not an instance of SessionFactory: " + registryObject.getClass().getName());
         }

         return (SessionFactory)registryObject;


      }
      else if (hibernateConfiguration != null)
      {

         try
         {
            Configuration config = new Configuration().configure(hibernateConfiguration);


            if (addMappedClasses != null && addMappedClasses.equals("false"))
            {
               return config.buildSessionFactory();
            }
            else
            {

               return config
                  .addResource("mappings/HibernateIdentityObject.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectCredentialBinaryValue.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectAttributeBinaryValue.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectAttribute.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectCredential.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectCredentialType.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectRelationship.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectRelationshipName.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectRelationshipType.hbm.xml")
                  .addResource("mappings/HibernateIdentityObjectType.hbm.xml")
                  .addResource("mappings/HibernateRealm.hbm.xml")
                  .buildSessionFactory();
            }
         }
         catch (Exception e)
         {
            throw new IdentityException("Cannot obtain hibernate SessionFactory using provided hibernate configuration: "+ hibernateConfiguration, e);
         }

      }
      throw new IdentityException("Cannot obtain hibernate SessionFactory. None of supported options specified: "
         + HIBERNATE_SESSION_FACTORY_JNDI_NAME + ", " + HIBERNATE_SESSION_FACTORY_REGISTRY_NAME + ", " + HIBERNATE_CONFIGURATION);


   }


   public IdentityStoreSession createIdentityStoreSession() throws IdentityException
   {
      try
      {
         return new HibernateIdentityStoreSessionImpl(sessionFactory);
      }
      catch (Exception e)
      {
         throw new IdentityException("Failed to obtain Hibernate SessionFactory",e);
      }
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public FeaturesMetaData getSupportedFeatures()
   {
      return supportedFeatures;
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext invocationCtx, String name, IdentityObjectType identityObjectType) throws IdentityException
   {
      return createIdentityObject(invocationCtx, name, identityObjectType, null);
   }

   public IdentityObject createIdentityObject(IdentityStoreInvocationContext ctx,
                                              String name,
                                              IdentityObjectType identityObjectType,
                                              Map<String, String[]> attributes) throws IdentityException
   {

      if (name == null)
      {
         throw new IllegalArgumentException("IdentityObject name is null");
      }

      checkIOType(identityObjectType);

      Session session = getHibernateSession(ctx);

      HibernateRealm realm = getRealm(session, ctx);

      // Check if object with a given name and type is not present already
      List<?> results = session.createQuery(HibernateIdentityObject.findIdentityObjectByNameAndType)
         .setParameter("realmName", realm.getName())
         .setParameter("name", name)
         .setParameter("typeName", identityObjectType.getName())
         .setCacheable(true)
         .list();

      if (results.size() != 0)
      {
         throw new IdentityException("IdentityObject already present in this IdentityStore:" +
            "name=" + name + "; type=" + identityObjectType.getName() + "; realm=" + realm);
      }

      HibernateIdentityObjectType hibernateType = getHibernateIdentityObjectType(ctx, identityObjectType);

      HibernateIdentityObject io = new HibernateIdentityObject(name, hibernateType, realm);

      if (attributes != null)
      {
         for (Map.Entry<String, String[]> entry : attributes.entrySet())
         {
            io.addTextAttribute(entry.getKey(), entry.getValue());
         }
      }

      try
      {
         getHibernateSession(ctx).persist(io);
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot persist new IdentityObject" + io, e);
      }


      return io;
   }

   public void removeIdentityObject(IdentityStoreInvocationContext ctx, IdentityObject identity) throws IdentityException
   {
      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      Session hibernateSession = getHibernateSession(ctx);

      try
      {
    	  
          // Remove all related relationships
    	  HibernateIdentityObjectRelationship[] from = new HibernateIdentityObjectRelationship[hibernateObject.getFromRelationships().size()];
         for (HibernateIdentityObjectRelationship relationship : hibernateObject.getFromRelationships().toArray(from))
         {
            relationship.getFromIdentityObject().getFromRelationships().remove(relationship);
            relationship.getToIdentityObject().getToRelationships().remove(relationship);
            hibernateSession.delete(relationship);
            hibernateSession.flush();
         }
         
         HibernateIdentityObjectRelationship[] to =  new HibernateIdentityObjectRelationship[hibernateObject.getToRelationships().size()];      
         for (HibernateIdentityObjectRelationship relationship : hibernateObject.getToRelationships().toArray(to))
         {
            relationship.getFromIdentityObject().getFromRelationships().remove(relationship);
            relationship.getToIdentityObject().getToRelationships().remove(relationship);

            hibernateSession.delete(relationship);
            hibernateSession.flush();

         }

         hibernateSession.delete(hibernateObject);
         hibernateSession.flush();
         
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot remove IdentityObject" + identity, e);
      }
   }

   public int getIdentityObjectsCount(IdentityStoreInvocationContext ctx, IdentityObjectType identityType) throws IdentityException
   {
      checkIOType(identityType);

      HibernateIdentityObjectType jpaType = getHibernateIdentityObjectType(ctx, identityType);

      Session hibernateSession = getHibernateSession(ctx);

      int count;
      try
      {
         count = ((Number)hibernateSession
            .createQuery(HibernateIdentityObject.countIdentityObjectsByType)
            .setParameter("typeName", jpaType.getName())
            .setParameter("realmName", getRealmName(ctx))
            .setCacheable(true)
            .uniqueResult()).intValue();
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot count stored IdentityObjects with type: " + identityType.getName(), e);
      }

      return count;
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext ctx, String name, IdentityObjectType type) throws IdentityException
   {

      if (name == null)
      {
         throw new IllegalArgumentException("IdentityObject name is null");
      }

      checkIOType(type);

      HibernateIdentityObjectType hibernateType = getHibernateIdentityObjectType(ctx, type);

      HibernateIdentityObject hibernateObject = null;

      try
      {
         hibernateObject = (HibernateIdentityObject)getHibernateSession(ctx).
            createCriteria(HibernateIdentityObject.class)
            .add(Restrictions.eq("name", name))
            .createAlias("realm", "rm")
            .add(Restrictions.eq("rm.name", getRealmName(ctx)))
            .createAlias("identityType", "type")
            .add(Restrictions.eq("type.name", hibernateType.getName()))
            .setCacheable(true)
            .uniqueResult();
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot find IdentityObject with name '" + name + "' and type '" + type.getName() + "'", e);
      }

      return hibernateObject;
   }

   public IdentityObject findIdentityObject(IdentityStoreInvocationContext ctx, String id) throws IdentityException
   {
      if (id == null)
      {
         throw new IllegalArgumentException("id is null");
      }

      HibernateIdentityObject hibernateObject;

      try
      {
         hibernateObject = (HibernateIdentityObject)getHibernateSession(ctx).get(HibernateIdentityObject.class, new Long(id));
      }
      catch(Exception e)
      {
         throw new IdentityException("Cannot find IdentityObject with id: " + id, e);
      }

      return hibernateObject;
   }



   @SuppressWarnings("unchecked")
   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext ctx,
                                                        IdentityObjectType identityType,
                                                        IdentityObjectSearchCriteria criteria) throws IdentityException
   {
      checkIOType(identityType);

      HibernateIdentityObjectType hibernateType = getHibernateIdentityObjectType(ctx, identityType);

      List<IdentityObject> results;

      Session hibernateSession = getHibernateSession(ctx);

      try
      {

         Criteria hc = hibernateSession.createCriteria(HibernateIdentityObject.class)
            .setCacheable(true)
            .createAlias("realm", "rm")
            .add(Restrictions.eq("rm.name", getRealmName(ctx)))
            .createAlias("identityType", "type")
            .add(Restrictions.eq("type.name", hibernateType.getName()));

         if (criteria != null && criteria.isSorted())
         {
            if (criteria.isAscending())
            {
               hc.addOrder(Order.asc("name"));
            }
            else
            {
               hc.addOrder(Order.desc("name"));
            }
         }

         if (criteria != null && criteria.isPaged())
         {
            if (criteria.getMaxResults() > 0)
            {
               hc.setMaxResults(criteria.getMaxResults());
            }
            hc.setFirstResult(criteria.getFirstResult());

         }

         if (criteria != null && criteria.getFilter() != null)
         {
            hc.add(Restrictions.like("name", criteria.getFilter().replaceAll("\\*", "%")));
         }
         else
         {
            hc.add(Restrictions.like("name", "%"));
         }



         results = (List<IdentityObject>)hc.list();
         Hibernate.initialize(results);

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot find IdentityObjects with type '" + identityType.getName() + "'", e);
      }

      if (criteria != null && criteria.isFiltered())
      {
         filterByAttributesValues(results, criteria.getValues());
      }

      return results;
   }



   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext ctx, IdentityObjectType identityType) throws IdentityException
   {
      return findIdentityObject(ctx, identityType, null);
   }


   @SuppressWarnings("unchecked")
   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext ctx,
                                                        IdentityObject identity,
                                                        IdentityObjectRelationshipType relationshipType,
                                                        boolean parent,
                                                        IdentityObjectSearchCriteria criteria) throws IdentityException
   {
      //TODO:test

      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      List<IdentityObject> results;

      boolean orderByName = false;
      boolean ascending = true;

      if (criteria != null && criteria.isSorted())
      {
         orderByName = true;
         ascending = criteria.isAscending();
      }

      try
      {
         org.hibernate.Query q = null;

         StringBuilder hqlString = new StringBuilder("");




         if (parent)
         {

            if (relationshipType != null)
            {

               hqlString.append("select distinct ior.toIdentityObject from HibernateIdentityObjectRelationship ior where " +
                  "ior.toIdentityObject.name like :nameFilter and ior.type.name = :relType and ior.fromIdentityObject = :identity");
            }
            else
            {
               hqlString.append("select distinct ior.toIdentityObject from HibernateIdentityObjectRelationship ior where " +
                  "ior.toIdentityObject.name like :nameFilter and ior.fromIdentityObject = :identity");
            }
            if (orderByName)
            {
               hqlString.append(" orderBy ior.toIdentityObject.name");
               if (ascending)
               {
                  hqlString.append(" asc");
               }
            }
         }
         else
         {
            if (relationshipType != null)
            {
               hqlString.append("select distinct ior.fromIdentityObject from HibernateIdentityObjectRelationship ior where " +
                  "ior.fromIdentityObject.name like :nameFilter and ior.type.name = :relType and ior.toIdentityObject = :identity");
            }
            else
            {
              hqlString.append("select distinct ior.fromIdentityObject from HibernateIdentityObjectRelationship ior where " +
                  "ior.fromIdentityObject.name like :nameFilter and ior.toIdentityObject = :identity");
            }

            if (orderByName)
            {
               hqlString.append(" orderBy ior.toIdentityObject.name");
               if (ascending)
               {
                  hqlString.append(" asc");
               }
            }
         }



         q = getHibernateSession(ctx).createQuery(hqlString.toString())
            .setParameter("identity",hibernateObject).setCacheable(true);

         if (relationshipType != null)
         {
            q.setParameter("relType", relationshipType.getName());
         }

         if (criteria != null && criteria.getFilter() != null)
         {
            q.setParameter("nameFilter", criteria.getFilter().replaceAll("\\*", "%"));
         }
         else
         {
            q.setParameter("nameFilter", "%");
         }


         if (criteria != null && criteria.isPaged())
         {
            q.setFirstResult(criteria.getFirstResult());
            if (criteria.getMaxResults() > 0)
            {
               q.setMaxResults(criteria.getMaxResults());
            }
         }


         q.setCacheable(true);
         results = q.list();
         Hibernate.initialize(results);


      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot find IdentityObjects", e);
      }

      if (criteria != null && criteria.isFiltered())
      {
         filterByAttributesValues(results, criteria.getValues());
      }

      return results;
   }

   public Collection<IdentityObject> findIdentityObject(IdentityStoreInvocationContext ctx,
                                                        IdentityObject identity,
                                                        IdentityObjectRelationshipType relationshipType,
                                                        boolean parent) throws IdentityException
   {

      return findIdentityObject(ctx, identity, relationshipType, parent, null);
   }

   public IdentityObjectRelationship createRelationship(IdentityStoreInvocationContext ctx,
                                                        IdentityObject fromIdentity,
                                                        IdentityObject toIdentity,
                                                        IdentityObjectRelationshipType relationshipType,
                                                        String name, boolean createNames) throws IdentityException
   {

      if (relationshipType == null)
      {
         throw new IllegalArgumentException("RelationshipType is null");
      }
      HibernateIdentityObject fromIO = safeGet(ctx, fromIdentity);      
      HibernateIdentityObject toIO = safeGet(ctx, toIdentity);
      HibernateIdentityObjectRelationshipType type = getHibernateIdentityObjectRelationshipType(ctx, relationshipType);
      
      if (!getSupportedFeatures().isRelationshipTypeSupported(fromIO.getIdentityType(), toIO.getIdentityType(), relationshipType))
      {
         if (!isAllowNotDefinedIdentityObjectTypes())
         {
            throw new IdentityException("Relationship not supported. RelationshipType[ " + relationshipType.getName() + " ] " +
               "beetween: [ " + fromIO.getIdentityType().getName() + " ] and [ " + toIO.getIdentityType().getName() + " ]");
         }
      }

      HibernateIdentityObjectRelationship relationship = null;

      if (name != null)
      {

         HibernateIdentityObjectRelationshipName relationshipName =
            (HibernateIdentityObjectRelationshipName)getHibernateSession(ctx).
               createCriteria(HibernateIdentityObjectRelationshipName.class).setCacheable(true).add(Restrictions.eq("name", name)).
               uniqueResult();

         if (relationshipName == null)
         {
            throw new IdentityException("Relationship name not present in the store");
         }

         relationship = new HibernateIdentityObjectRelationship(type, fromIO, toIO, relationshipName);
      }
      else
      {
         relationship = new HibernateIdentityObjectRelationship(type, fromIO, toIO);
      }


      try
      {
         Session session = getHibernateSession(ctx);
         session.persist(relationship);
         session.flush();

      }
      catch (HibernateException e)
      {
         throw new IdentityException("Cannot create relationship: ", e);
      }

      return relationship;

   }



   public void removeRelationship(IdentityStoreInvocationContext ctx, IdentityObject fromIdentity, IdentityObject toIdentity, IdentityObjectRelationshipType relationshipType, String name) throws IdentityException
   {

      if (relationshipType == null)
      {
         throw new IllegalArgumentException("RelationshipType is null");
      }

      HibernateIdentityObject fromIO = safeGet(ctx, fromIdentity);
      HibernateIdentityObject toIO = safeGet(ctx, toIdentity);
      HibernateIdentityObjectRelationshipType type = getHibernateIdentityObjectRelationshipType(ctx, relationshipType);

      org.hibernate.Query query = null;

      if (name == null)
      {
         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setCacheable(true);
      }
      else
      {
         HibernateIdentityObjectRelationshipName relationshipName =
            (HibernateIdentityObjectRelationshipName)getHibernateSession(ctx)
               .createCriteria(HibernateIdentityObjectRelationshipName.class).add(Restrictions.eq("name", name))
               .uniqueResult();

         if (relationshipName == null)
         {
            throw new IdentityException("Relationship name not present in the store");
         }

         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE_NAME)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setParameter("name", name)
            .setCacheable(true);
      }


      HibernateIdentityObjectRelationship relationship = (HibernateIdentityObjectRelationship)query.uniqueResult();

      if (relationship == null)
      {
         throw new IdentityException("Relationship not present in the store");
      }

     try
      {
         fromIO.getFromRelationships().remove(relationship);
         toIO.getToRelationships().remove(relationship);
         getHibernateSession(ctx).delete(relationship);
         getHibernateSession(ctx).flush();
      }
      catch (HibernateException e)
      {
         throw new IdentityException("Cannot remove relationship");
      }


   }

   public void removeRelationships(IdentityStoreInvocationContext ctx, IdentityObject identity1, IdentityObject identity2, boolean named) throws IdentityException
   {
      HibernateIdentityObject hio1 = safeGet(ctx, identity1);
      HibernateIdentityObject hio2 = safeGet(ctx, identity2);

      org.hibernate.Query query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_IDENTITIES)
         .setParameter("IO1", hio1)
         .setParameter("IO2", hio2);

      List results = query.list();
      Hibernate.initialize(results);

      for (Iterator iterator = results.iterator(); iterator.hasNext();)
      {
         HibernateIdentityObjectRelationship relationship = (HibernateIdentityObjectRelationship) iterator.next();

         if ((named && relationship.getName() != null) ||
            (!named && relationship.getName() == null))
         {
            try
            {
               relationship.getFromIdentityObject().getFromRelationships().remove(relationship);
               relationship.getToIdentityObject().getToRelationships().remove(relationship);
               getHibernateSession(ctx).delete(relationship);
               getHibernateSession(ctx).flush();
            }
            catch (HibernateException e)
            {
               throw new IdentityException("Cannot remove relationship");
            }
         }
      }
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext ctx,
                                                               IdentityObject fromIdentity,
                                                               IdentityObject toIdentity,
                                                               IdentityObjectRelationshipType relationshipType) throws IdentityException
   {

      HibernateIdentityObject hio1 = safeGet(ctx, fromIdentity);
      HibernateIdentityObject hio2 = safeGet(ctx, toIdentity);

      org.hibernate.Query query = null;

      if (relationshipType == null)
      {
         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO);
      }
      else
      {
         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE)
            .setParameter("typeName", relationshipType.getName());

      }

      query.setParameter("fromIO", hio1)
         .setParameter("toIO", hio2);


      List<HibernateIdentityObjectRelationship> results = query.list();
      Hibernate.initialize(results);

      return new HashSet<IdentityObjectRelationship>(results);
   }

   public Set<IdentityObjectRelationship> resolveRelationships(IdentityStoreInvocationContext ctx,
                                                               IdentityObject identity,
                                                               IdentityObjectRelationshipType type,
                                                               boolean parent,
                                                               boolean named,
                                                               String name) throws IdentityException
   {
      HibernateIdentityObject hio = safeGet(ctx, identity);


      Criteria criteria = getHibernateSession(ctx).createCriteria(HibernateIdentityObjectRelationship.class);
      criteria.setCacheable(true);

      if (type != null)
      {
         HibernateIdentityObjectRelationshipType hibernateType = getHibernateIdentityObjectRelationshipType(ctx, type);

         criteria.add(Restrictions.eq("type", hibernateType));
      }

      if (name != null)
      {
         criteria.add(Restrictions.eq("name.name", name));
      }
      else if (named)
      {
         criteria.add(Restrictions.isNotNull("name"));
      }
      else
      {
         criteria.add(Restrictions.isNull("name"));
      }

      if (parent)
      {
         criteria.add(Restrictions.eq("fromIdentityObject", hio));
      }
      else
      {
         criteria.add(Restrictions.eq("toIdentityObject", hio));
      }

      List<HibernateIdentityObjectRelationship> results = criteria.list();

      Hibernate.initialize(results);

      return new HashSet<IdentityObjectRelationship>(results);
   }

   public String createRelationshipName(IdentityStoreInvocationContext ctx, String name) throws IdentityException, OperationNotSupportedException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name is null");
      }

      Session hibernateSession = getHibernateSession(ctx);

      HibernateRealm realm = getRealm(hibernateSession, ctx);

      try
      {
         HibernateIdentityObjectRelationshipName hiorn = (HibernateIdentityObjectRelationshipName)hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNameByName)
            .setParameter("name", name).setParameter("realmName", realm.getName()).uniqueResult();

         if (hiorn != null)
         {
            throw new IdentityException("Relationship name already exists");
         }

         hiorn = new HibernateIdentityObjectRelationshipName(name, realm);
         getHibernateSession(ctx).persist(hiorn);
         getHibernateSession(ctx).flush();


      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot create new relationship name: " + name, e);
      }


      return name;
   }

   public String removeRelationshipName(IdentityStoreInvocationContext ctx, String name)  throws IdentityException, OperationNotSupportedException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name is null");
      }

      Session hibernateSession = getHibernateSession(ctx);



      try
      {
         HibernateIdentityObjectRelationshipName hiorn = (HibernateIdentityObjectRelationshipName)hibernateSession
            .createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNameByName)
            .setParameter("name", name)
            .setParameter("realmName", getRealmName(ctx))
            .uniqueResult();

         if (hiorn == null)
         {
            throw new IdentityException("Relationship name doesn't exist");
         }

         List<HibernateIdentityObjectRelationship> rels = (List<HibernateIdentityObjectRelationship>)hibernateSession.
            createCriteria(HibernateIdentityObjectRelationship.class)
            .add(Restrictions.eq("name", hiorn)).setCacheable(true).list();

         Hibernate.initialize(rels);

         //Remove all present usages
         for (HibernateIdentityObjectRelationship rel : rels)
         {
            getHibernateSession(ctx).delete(rel);
         }

         getHibernateSession(ctx).delete(hiorn);
         getHibernateSession(ctx).flush();

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot remove new relationship name: " + name, e);
      }


      return name;
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObjectSearchCriteria criteria) throws IdentityException, OperationNotSupportedException
   {

      Set<String> names = null;

      Session hibernateSession = getHibernateSession(ctx);

      try
      {
         Query q = null;

         if (criteria != null && criteria.isSorted())
         {
            if (criteria.isAscending())
            {
               q = hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNamesOrderedByNameAsc);
            }
            else
            {
               q = hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNamesOrderedByNameDesc);
            }
         }
         else
         {
            q = hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNames).setCacheable(true);;
         }

         q.setParameter("realmName", getRealmName(ctx));

         if (criteria != null && criteria.getFilter() != null)
         {
            q.setParameter("nameFilter", criteria.getFilter().replaceAll("\\*", "%"));
         }
         else
         {
            q.setParameter("nameFilter", "%");
         }


         if (criteria != null && criteria.isPaged())
         {
            q.setFirstResult(criteria.getMaxResults());
            if (criteria.getFirstResult() > 0)
            {
               q.setMaxResults(criteria.getMaxResults());
            }
         }

         List<String> results = (List<String>)q.list();

         Hibernate.initialize(results);

         names = new HashSet<String>(results);

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot get relationship names. ", e);
      }

      return names;
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx) throws IdentityException, OperationNotSupportedException
   {
      return getRelationshipNames(ctx, (IdentityObjectSearchCriteria)null);
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObject identity, IdentityObjectSearchCriteria criteria) throws IdentityException, OperationNotSupportedException
   {

      Set<String> names;

      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      Session hibernateSession = getHibernateSession(ctx);

      try
      {
         Query q = null;

         if (criteria != null)
         {
            if (criteria.isAscending())
            {
               q = hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNamesForIdentityObjectOrderedByNameAsc).setCacheable(true);
            }
            else
            {
               q = hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNamesForIdentityObjectOrdereByNameDesc).setCacheable(true);
            }
         }
         else
         {
            q = hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNamesForIdentityObject).setCacheable(true);
         }

         q.setParameter("identityObject", hibernateObject);

         if (criteria != null && criteria.isPaged())
         {
            q.setFirstResult(criteria.getFirstResult());
            if (criteria.getMaxResults() > 0)
            {
               q.setMaxResults(criteria.getMaxResults());
            }
         }

         List<String> results = (List<String>)q.list();

         Hibernate.initialize(results);

         names = new HashSet<String>(results);

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot get relationship names. ", e);
      }

      return names;
   }

   public Set<String> getRelationshipNames(IdentityStoreInvocationContext ctx, IdentityObject identity) throws IdentityException, OperationNotSupportedException
   {
      return getRelationshipNames(ctx, identity, null);
   }

   public Map<String, String> getRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name) throws IdentityException, OperationNotSupportedException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name is null");
      }

      Session hibernateSession = getHibernateSession(ctx);


      try
      {
         HibernateIdentityObjectRelationshipName hiorn = (HibernateIdentityObjectRelationshipName)hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNameByName)
            .setParameter("name", name).setParameter("realmName", getRealmName(ctx)).uniqueResult();

         if (hiorn == null)
         {
            throw new IdentityException("Relationship name doesn't exist");
         }

         Hibernate.initialize(hiorn.getProperties());

         return new HashMap<String, String>(hiorn.getProperties());

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot get relationship name properties: " + name, e);
      }
   }

   public void setRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name, Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name is null");
      }

      Session hibernateSession = getHibernateSession(ctx);


      try
      {
         HibernateIdentityObjectRelationshipName hiorn = (HibernateIdentityObjectRelationshipName)hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNameByName)
                     .setParameter("name", name).setParameter("realmName", getRealmName(ctx)).uniqueResult();

         if (hiorn == null)
         {
            throw new IdentityException("Relationship name doesn't exist");
         }

         hiorn.getProperties().putAll(properties);

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot set relationship name properties: " + name, e);
      }
   }

   public void removeRelationshipNameProperties(IdentityStoreInvocationContext ctx, String name, Set<String> properties) throws IdentityException, OperationNotSupportedException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name is null");
      }

      Session hibernateSession = getHibernateSession(ctx);


      try
      {
         HibernateIdentityObjectRelationshipName hiorn = (HibernateIdentityObjectRelationshipName)hibernateSession.createQuery(HibernateIdentityObjectRelationshipName.findIdentityObjectRelationshipNameByName)
                     .setParameter("name", name).setParameter("realmName", getRealmName(ctx)).uniqueResult();

         if (hiorn == null)
         {
            throw new IdentityException("Relationship name doesn't exist");
         }

         Hibernate.initialize(hiorn.getProperties());


         for (String property : properties)
         {
            hiorn.getProperties().remove(property);
         }

      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot remove relationship name properties: " + name, e);
      }
   }

   public Map<String, String> getRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship) throws IdentityException, OperationNotSupportedException
   {
      HibernateIdentityObject fromIO = safeGet(ctx, relationship.getFromIdentityObject());
      HibernateIdentityObject toIO = safeGet(ctx, relationship.getToIdentityObject());
      HibernateIdentityObjectRelationshipType type = getHibernateIdentityObjectRelationshipType(ctx, relationship.getType());

      Query query = null;

      if (relationship.getName() == null)
      {
         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName());
      }
      else
      {
         HibernateIdentityObjectRelationshipName relationshipName =
            (HibernateIdentityObjectRelationshipName)getHibernateSession(ctx)
               .createCriteria(HibernateIdentityObjectRelationshipName.class).add(Restrictions.eq("name", relationship.getName()))
               .setCacheable(true).uniqueResult();

         if (relationshipName == null)
         {
            throw new IdentityException("Relationship name not present in the store");
         }

         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE_NAME)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setParameter("name", relationship.getName())
            .setCacheable(true);
      }


      try
      {
         HibernateIdentityObjectRelationship hibernateRelationship = (HibernateIdentityObjectRelationship)query.uniqueResult();

         Hibernate.initialize(hibernateRelationship.getProperties());
         
         return new HashMap<String, String>(hibernateRelationship.getProperties());
      }
      catch (HibernateException e)
      {
         throw new IdentityException("Cannot obtain relationship properties: ", e);
      }
   }

   public void setRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship, Map<String, String> properties) throws IdentityException, OperationNotSupportedException
   {
      HibernateIdentityObject fromIO = safeGet(ctx, relationship.getFromIdentityObject());
      HibernateIdentityObject toIO = safeGet(ctx, relationship.getToIdentityObject());
      HibernateIdentityObjectRelationshipType type = getHibernateIdentityObjectRelationshipType(ctx, relationship.getType());

      Query query = null;

      if (relationship.getName() == null)
      {
         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setCacheable(true);
      }
      else
      {
         HibernateIdentityObjectRelationshipName relationshipName =
            (HibernateIdentityObjectRelationshipName)getHibernateSession(ctx)
               .createCriteria(HibernateIdentityObjectRelationshipName.class).add(Restrictions.eq("name", relationship.getName()))
               .setCacheable(true)
               .uniqueResult();

         if (relationshipName == null)
         {
            throw new IdentityException("Relationship name not present in the store");
         }

         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE_NAME)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setParameter("name", relationship.getName())
            .setCacheable(true);
      }


      try
      {
         HibernateIdentityObjectRelationship hibernateRelationship = (HibernateIdentityObjectRelationship)query.uniqueResult();

         hibernateRelationship.getProperties().putAll(properties);
      }
      catch (HibernateException e)
      {
         throw new IdentityException("Cannot update relationship properties: ", e);
      }
   }

   public void removeRelationshipProperties(IdentityStoreInvocationContext ctx, IdentityObjectRelationship relationship, Set<String> properties) throws IdentityException, OperationNotSupportedException
   {
      HibernateIdentityObject fromIO = safeGet(ctx, relationship.getFromIdentityObject());
      HibernateIdentityObject toIO = safeGet(ctx, relationship.getToIdentityObject());
      HibernateIdentityObjectRelationshipType type = getHibernateIdentityObjectRelationshipType(ctx, relationship.getType());

      Query query = null;

      if (relationship.getName() == null)
      {
         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setCacheable(true);
      }
      else
      {
         HibernateIdentityObjectRelationshipName relationshipName =
            (HibernateIdentityObjectRelationshipName)getHibernateSession(ctx)
               .createCriteria(HibernateIdentityObjectRelationshipName.class).add(Restrictions.eq("name", relationship.getName()))
               .setCacheable(true)
               .uniqueResult();

         if (relationshipName == null)
         {
            throw new IdentityException("Relationship name not present in the store");
         }

         query = getHibernateSession(ctx).createQuery(QUERY_RELATIONSHIP_BY_FROM_TO_TYPE_NAME)
            .setParameter("fromIO", fromIO)
            .setParameter("toIO", toIO)
            .setParameter("typeName", type.getName())
            .setParameter("name", relationship.getName())
            .setCacheable(true);
      }


      try
      {
         HibernateIdentityObjectRelationship hibernateRelationship = (HibernateIdentityObjectRelationship)query.uniqueResult();

         Hibernate.initialize(hibernateRelationship.getProperties());

         for (String property : properties)
         {
            hibernateRelationship.getProperties().remove(property);
         }
      }
      catch (HibernateException e)
      {
         throw new IdentityException("Cannot update relationship properties: ", e);
      }
   }


   // Attribute store

   public Set<String> getSupportedAttributeNames(IdentityStoreInvocationContext ctx, IdentityObjectType identityType) throws IdentityException
   {
      checkIOType(identityType);

      if (attributeMappings.containsKey(identityType.getName()))
      {
         return attributeMappings.get(identityType.getName());
      }

      return new HashSet<String>();

   }

   public IdentityObjectAttribute getAttribute(IdentityStoreInvocationContext ctx, IdentityObject identity, String name) throws IdentityException
   {
      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      Set<HibernateIdentityObjectAttribute> storeAttributes =  hibernateObject.getAttributes();

      Hibernate.initialize(storeAttributes);

      // Remap the names
      for (HibernateIdentityObjectAttribute attribute : storeAttributes)
      {
         String mappedName = resolveAttributeNameFromStoreMapping(identity.getIdentityType(), name);
         if (mappedName != null)
         {
            return attribute;
         }
      }

      return null;
   }

   public Map<String, IdentityObjectAttribute> getAttributes(IdentityStoreInvocationContext ctx, IdentityObject identity) throws IdentityException
   {

      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);



      Map<String, IdentityObjectAttribute> result = new HashMap<String, IdentityObjectAttribute>();

      if (hibernateObject == null)
      {
         return result;
      }

      Set<HibernateIdentityObjectAttribute> storeAttributes =  hibernateObject.getAttributes();

      Hibernate.initialize(storeAttributes);


      // Remap the names
      for (HibernateIdentityObjectAttribute attribute : storeAttributes)
      {
         String name = resolveAttributeNameFromStoreMapping(identity.getIdentityType(), attribute.getName());
         if (name != null)
         {
            result.put(name, attribute);
         }
      }

      return result;

   }

   public Map<String, IdentityObjectAttributeMetaData> getAttributesMetaData(IdentityStoreInvocationContext invocationContext,
                                                                            IdentityObjectType identityType)
   {
      return attributesMetaData.get(identityType.getName());
   }



   public void updateAttributes(IdentityStoreInvocationContext ctx, IdentityObject identity, IdentityObjectAttribute[] attributes) throws IdentityException
   {

      if (attributes == null)
      {
         throw new IllegalArgumentException("attributes are null");
      }

      Map<String, IdentityObjectAttribute> mappedAttributes = new HashMap<String, IdentityObjectAttribute>();

      Map<String, IdentityObjectAttributeMetaData> mdMap = attributesMetaData.get(identity.getIdentityType().getName());

      for (IdentityObjectAttribute attribute : attributes)
      {
         String name = resolveAttributeStoreMapping(identity.getIdentityType(), attribute.getName());
         mappedAttributes.put(name, attribute);



         if (mdMap == null || !mdMap.containsKey(attribute.getName()))
         {
            if (!isAllowNotDefinedAttributes)
            {
               throw new IdentityException("Cannot add not defined attribute. Use '" + ALLOW_NOT_DEFINED_ATTRIBUTES +
                  "' option if needed. Attribute name: " + attribute.getName());
            }
         }


         if (mdMap != null && mdMap.containsKey(attribute.getName()))
         {

            IdentityObjectAttributeMetaData amd = mdMap.get(attribute.getName());

            if (!amd.isMultivalued() && attribute.getSize() > 1)
            {
               throw new IdentityException("Cannot assigned multiply values to single valued attribute: " + attribute.getName());
            }
            if (amd.isReadonly())
            {
               throw new IdentityException("Cannot update readonly attribute: " + attribute.getName());
            }

            if (amd.isUnique())
            {
               IdentityObject checkIdentity = findIdentityObjectByUniqueAttribute(ctx,
                  identity.getIdentityType(),
                  attribute);

               if (checkIdentity != null && !checkIdentity.getName().equals(identity.getName()))
               {
                  throw new IdentityException("Unique attribute '" + attribute.getName() + " value already set for identityObject: " +
                  checkIdentity);
               }
            }

            String type = amd.getType();

            // check if all values have proper type

            for (Object value : attribute.getValues())
            {
               if (type.equals(IdentityObjectAttributeMetaData.TEXT_TYPE) && !(value instanceof String))
               {
                  throw new IdentityException("Cannot update text type attribute with not String type value: "
                     + attribute.getName() + " / " + value);
               }
               if (type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE) && !(value instanceof byte[]))
               {
                  throw new IdentityException("Cannot update binary type attribute with not byte[] type value: "
                     + attribute.getName() + " / " + value);
               }
            }
            if (type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE) && attribute.getValues().size() > 1)
            {
               throw new IdentityException("Cannot add binary type attribute with more than one value - this implementation" +
                  "support only single value binary attributes: " + attribute.getName() );
            }
         }
      }


      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      Hibernate.initialize(hibernateObject.getAttributes());

      for (String name : mappedAttributes.keySet())
      {
         IdentityObjectAttribute attribute = mappedAttributes.get(name);

         IdentityObjectAttributeMetaData amd = null;

         if (mdMap != null)
         {
            amd = mdMap.get(attribute.getName());
         }

         // Default to text
         String type = amd != null ? amd.getType() : IdentityObjectAttributeMetaData.TEXT_TYPE;

         boolean present = false;

         for (HibernateIdentityObjectAttribute storeAttribute : hibernateObject.getAttributes())
         {
            if (storeAttribute.getName().equals(name))
            {
               present = true;
               if (storeAttribute.getType().equals(HibernateIdentityObjectAttribute.TYPE_TEXT))
               {
                  if (!type.equals(IdentityObjectAttributeMetaData.TEXT_TYPE))
                  {
                     throw new IdentityException("Wrong attribute mapping. Attribute persisted as text is mapped with: "
                     + type + ". Attribute name: " + name);
                  }


                  Set<String> v = new HashSet<String>();
                  for (Object value : attribute.getValues())
                  {
                     v.add(value.toString());
                  }

                  storeAttribute.setTextValues(v);
               }
               else if (storeAttribute.equals(HibernateIdentityObjectAttribute.TYPE_BINARY))
               {

                  if (!type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE))
                  {
                     throw new IdentityException("Wrong attribute mapping. Attribute persisted as binary is mapped with: "
                     + type + ". Attribute name: " + name);
                  }
                  HibernateIdentityObjectAttributeBinaryValue bv = new HibernateIdentityObjectAttributeBinaryValue((byte[])attribute.getValue());
                  getHibernateSession(ctx).persist(bv);
                  storeAttribute.setBinaryValue(bv);
               }
               else
               {
                  throw new IdentityException("Internal identity store error");
               }
               break;
            }
         }

         if (!present && attribute.getValues() != null && attribute.getValues().size() > 0)
         {
            HibernateIdentityObjectAttribute newAttribute = new HibernateIdentityObjectAttribute(hibernateObject, name, type);
            if (type.equals(HibernateIdentityObjectAttribute.TYPE_TEXT))
            {
               newAttribute.setTextValues(attribute.getValues());
            }
            else if (type.equals(HibernateIdentityObjectAttribute.TYPE_BINARY))
            {
               HibernateIdentityObjectAttributeBinaryValue bv = new HibernateIdentityObjectAttributeBinaryValue((byte[])attribute.getValue());
               getHibernateSession(ctx).persist(bv);
               newAttribute.setBinaryValue(bv);
            }
            hibernateObject.addAttribute(newAttribute);
         }

      }

   }

   public void addAttributes(IdentityStoreInvocationContext ctx, IdentityObject identity, IdentityObjectAttribute[] attributes) throws IdentityException
   {

      if (attributes == null)
      {
         throw new IllegalArgumentException("attributes are null");
      }

      Map<String, IdentityObjectAttribute> mappedAttributes = new HashMap<String, IdentityObjectAttribute>();

      Map<String, IdentityObjectAttributeMetaData> mdMap = attributesMetaData.get(identity.getIdentityType().getName());

      Session hibernateSession = getHibernateSession(ctx);

      for (IdentityObjectAttribute attribute : attributes)
      {
         String name = resolveAttributeStoreMapping(identity.getIdentityType(), attribute.getName());
         mappedAttributes.put(name, attribute);


         if ((mdMap == null || !mdMap.containsKey(attribute.getName())) &&
            !isAllowNotDefinedAttributes)
         {
            throw new IdentityException("Cannot add not defined attribute. Use '" + ALLOW_NOT_DEFINED_ATTRIBUTES +
               "' option if needed. Attribute name: " + attribute.getName());

         }

         IdentityObjectAttributeMetaData amd = null;

         if (mdMap != null)
         {
            amd = mdMap.get(attribute.getName());
         }

         if (amd != null)
         {

            if (!amd.isMultivalued() && attribute.getSize() > 1)
            {
               throw new IdentityException("Cannot add multiply values to single valued attribute: " + attribute.getName());
            }
            if (amd.isReadonly())
            {
               throw new IdentityException("Cannot add readonly attribute: " + attribute.getName());
            }

            if (amd.isUnique())
            {
               IdentityObject checkIdentity = findIdentityObjectByUniqueAttribute(ctx,
                  identity.getIdentityType(),
                  attribute);

               if (checkIdentity != null && !checkIdentity.getName().equals(identity.getName()))
               {
                  throw new IdentityException("Unique attribute '" + attribute.getName() + " value already set for identityObject: " +
                  checkIdentity);
               }
            }

            String type = amd.getType();

            // check if all values have proper type

            for (Object value : attribute.getValues())
            {
               if (type.equals(IdentityObjectAttributeMetaData.TEXT_TYPE) && !(value instanceof String))
               {
                  throw new IdentityException("Cannot add text type attribute with not String type value: "
                     + attribute.getName() + " / " + value);
               }
               if (type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE) && !(value instanceof byte[]))
               {
                  throw new IdentityException("Cannot add binary type attribute with not byte[] type value: "
                     + attribute.getName() + " / " + value);
               }

            }
            if (type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE) && attribute.getValues().size() > 1)
            {
               throw new IdentityException("Cannot add binary type attribute with more than one value - this implementation" +
                  "support only single value binary attributes: " + attribute.getName() );
            }
         }
      }

      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      Hibernate.initialize(hibernateObject.getAttributes());

      for (String name : mappedAttributes.keySet())
      {
         IdentityObjectAttribute attribute = mappedAttributes.get(name);

         IdentityObjectAttributeMetaData amd = mdMap != null ? mdMap.get(attribute.getName()) : null;

         // Default to text
         String type = amd != null ? amd.getType() : IdentityObjectAttributeMetaData.TEXT_TYPE;

         HibernateIdentityObjectAttribute hibernateAttribute = null;

         for (HibernateIdentityObjectAttribute storeAttribute : hibernateObject.getAttributes())
         {
            if (storeAttribute.getName().equals(name))
            {
               hibernateAttribute = storeAttribute;
               break;
            }
         }

         if (hibernateAttribute != null)
         {
            if (hibernateAttribute.getType().equals(HibernateIdentityObjectAttribute.TYPE_TEXT))
               {
                  if (!type.equals(IdentityObjectAttributeMetaData.TEXT_TYPE))
                  {
                     throw new IdentityException("Wrong attribute mapping. Attribute persisted as text is mapped with: "
                     + type + ". Attribute name: " + name);
                  }


                  Set<String> mergedValues = new HashSet<String>(hibernateAttribute.getValues());
                  for (Object value : attribute.getValues())
                  {
                     mergedValues.add(value.toString());
                  }

                  hibernateAttribute.setTextValues(mergedValues);
               }
               else if (hibernateAttribute.getType().equals(HibernateIdentityObjectAttribute.TYPE_BINARY))
               {

                  if (!type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE))
                  {
                     throw new IdentityException("Wrong attribute mapping. Attribute persisted as binary is mapped with: "
                     + type + ". Attribute name: " + name);
                  }

                  HibernateIdentityObjectAttributeBinaryValue bv = new HibernateIdentityObjectAttributeBinaryValue((byte[])attribute.getValue());
                  getHibernateSession(ctx).persist(bv);
                  hibernateAttribute.setBinaryValue(bv);
               }
               else
               {
                  throw new IdentityException("Internal identity store error");
               }
               break;

         }
         else
         {
            if (type.equals(IdentityObjectAttributeMetaData.TEXT_TYPE))
            {
               Set<String> values = new HashSet<String>();

               for (Object value: attribute.getValues())
               {
                  values.add(value.toString());
               }
               hibernateAttribute = new HibernateIdentityObjectAttribute(hibernateObject, name, HibernateIdentityObjectAttribute.TYPE_TEXT);
               hibernateAttribute.setTextValues(values);
            }
            else if (type.equals(IdentityObjectAttributeMetaData.BINARY_TYPE))
            {
               Set<byte[]> values = new HashSet<byte[]>();

               for (Object value: attribute.getValues())
               {
                  values.add((byte[])value);
               }
               hibernateAttribute = new HibernateIdentityObjectAttribute(hibernateObject, name, HibernateIdentityObjectAttribute.TYPE_BINARY);
               HibernateIdentityObjectAttributeBinaryValue bv = new HibernateIdentityObjectAttributeBinaryValue((byte[])attribute.getValue());
               getHibernateSession(ctx).persist(bv);
               hibernateAttribute.setBinaryValue(bv);
            }


            hibernateObject.addAttribute(hibernateAttribute);

         }
      }
   }

   public void removeAttributes(IdentityStoreInvocationContext ctx, IdentityObject identity, String[] attributes) throws IdentityException
   {

      if (attributes == null)
      {
         throw new IllegalArgumentException("attributes are null");
      }

      String[] mappedAttributes = new String[attributes.length];

      for (int i = 0; i < attributes.length; i++)
      {
         String name = resolveAttributeStoreMapping(identity.getIdentityType(), attributes[i]);
         mappedAttributes[i] = name;

         Map<String, IdentityObjectAttributeMetaData> mdMap = attributesMetaData.get(identity.getIdentityType().getName());

         if (mdMap != null)
         {
            IdentityObjectAttributeMetaData amd = mdMap.get(attributes[i]);
            if (amd != null && amd.isRequired())
            {
               throw new IdentityException("Cannot remove required attribute: " + attributes[i]);
            }
         }
         else
         {
            if (!isAllowNotDefinedAttributes)
            {
               throw new IdentityException("Cannot remove not defined attribute. Use '" + ALLOW_NOT_DEFINED_ATTRIBUTES +
                  "' option if needed. Attribute name: " + attributes[i]);
            }
         }

      }

      HibernateIdentityObject hibernateObject = safeGet(ctx, identity);

      Hibernate.initialize(hibernateObject.getAttributes());

      for (String attr : mappedAttributes)
      {
         hibernateObject.removeAttribute(attr);
      }
   }

   public IdentityObject findIdentityObjectByUniqueAttribute(IdentityStoreInvocationContext invocationCtx, IdentityObjectType identityObjectType, IdentityObjectAttribute attribute) throws IdentityException
   {
      if (attribute == null)
      {
         throw new IllegalArgumentException("attribute is null");
      }

      checkIOType(identityObjectType);


      //TODO: check both binary and text with multivalue

      String attrMappedName = resolveAttributeStoreMapping(identityObjectType, attribute.getName());

      HibernateIdentityObjectType hiot = getHibernateIdentityObjectType(invocationCtx, identityObjectType);

      Session session = getHibernateSession(invocationCtx);



      if (attribute.getValues() == null || attribute.getValues().size() == 0)
      {
         return null;
      }

      boolean attrDuctTypeText = true;

      if (attribute.getValue() instanceof byte[])
      {
         attrDuctTypeText = false;
      }

      StringBuffer queryString = new StringBuffer("select a from HibernateIdentityObjectAttribute a where a.identityObject.identityType = :identityType " +
         "and a.name = :attributeName");

      if (attrDuctTypeText)
      {
         for (int i = 0; i < attribute.getValues().size(); i++)
         {
            String paramName = " :value" + i;
            queryString.append(" and").append(paramName).append(" = any elements(a.textValues)");

         }
      }
      else
      {
         queryString.append(" and :value = a.binaryValue");
      }


      Query q = session.createQuery(queryString.toString());
      q.setParameter("identityType", hiot)
      .setParameter("attributeName", attrMappedName);

      if (attrDuctTypeText)
      {
         int i = 0;
         for (Object o : attribute.getValues())
         {
            String value = o.toString();
            String paramName = "value" + i;
            q.setParameter(paramName, value);
            i++;
         }
      }
      else
      {
         q.setParameter("value", attribute.getValue());
      }

       List<HibernateIdentityObjectAttribute> attrs = (List<HibernateIdentityObjectAttribute>)q.list();

      if (attrs.size() == 0)
      {
         return null;
      }
      if (attrs.size() > 1)
      {
         throw new IdentityException("Illegal state - more than one IdentityObject with the same unique attribute value: " + attribute);
      }

      return attrs.get(0).getIdentityObject();

   }

   public boolean validateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject, IdentityObjectCredential credential) throws IdentityException
   {
      if (credential == null)
      {
         throw new IllegalArgumentException();
      }

      HibernateIdentityObject hibernateObject = safeGet(ctx, identityObject);

      if (supportedFeatures.isCredentialSupported(hibernateObject.getIdentityType(),credential.getType()))
      {

         HibernateIdentityObjectCredential hibernateCredential = null;

         hibernateCredential = (HibernateIdentityObjectCredential)getHibernateSession(ctx)
            .createCriteria(HibernateIdentityObjectCredential.class)
            .createAlias("type", "t")
            .add(Restrictions.eq("t.name", credential.getType().getName()))
            .add(Restrictions.eq("identityObject", hibernateObject))
            .setCacheable(true)
            .uniqueResult();

         if (hibernateCredential == null)
         {
            return false;
         }

         // Handle generic impl

         Object value = null;

         if (credential.getEncodedValue() != null)
         {
            value = credential.getEncodedValue();
         }
         else
         {
            //TODO: support for empty password should be configurable
            value = credential.getValue();
         }

         if (value instanceof String && hibernateCredential.getTextValue() != null)
         {
            return value.toString().equals(hibernateCredential.getTextValue());
         }
         else if (value instanceof byte[] && hibernateCredential.getBinaryValue() != null)
         {
            return Arrays.equals((byte[])value, hibernateCredential.getBinaryValue().getValue());
         }
         else
         {
            throw new IdentityException("Not supported credential value: " + value.getClass());
         }
      }
      else
      {
         throw new IdentityException("CredentialType not supported for a given IdentityObjectType");
      }
   }


   public void updateCredential(IdentityStoreInvocationContext ctx, IdentityObject identityObject, IdentityObjectCredential credential) throws IdentityException
   {

      if (credential == null)
      {
         throw new IllegalArgumentException();
      }

      HibernateIdentityObject hibernateObject = safeGet(ctx, identityObject);

      Session hibernateSession = getHibernateSession(ctx);

      if (supportedFeatures.isCredentialSupported(hibernateObject.getIdentityType(),credential.getType()))
      {

         HibernateIdentityObjectCredentialType hibernateCredentialType = getHibernateIdentityObjectCredentialType(ctx, credential.getType());

         if (hibernateCredentialType == null)
         {
            throw new IllegalStateException("Credential type not present in this store: " + credential.getType().getName());
         }

         HibernateIdentityObjectCredential hibernateCredential = hibernateObject.getCredential(credential.getType());

         if (hibernateCredential == null)
         {
            hibernateCredential = new HibernateIdentityObjectCredential();
            hibernateCredential.setType(hibernateCredentialType);
            hibernateObject.addCredential(hibernateCredential);
         }


         Object value = null;

         // Handle generic impl

         if (credential.getEncodedValue() != null)
         {
            value = credential.getEncodedValue();
         }
         else
         {
            //TODO: support for empty password should be configurable
            value = credential.getValue();
         }

         if (value instanceof String)
         {
            hibernateCredential.setTextValue(value.toString());
         }
         else if (value instanceof byte[])
         {
            HibernateIdentityObjectCredentialBinaryValue bv = new HibernateIdentityObjectCredentialBinaryValue((byte[])value);
            getHibernateSession(ctx).persist(bv);
            hibernateCredential.setBinaryValue(bv);
         }
         else
         {
            throw new IdentityException("Not supported credential value: " + value.getClass());
         }

         hibernateSession.persist(hibernateCredential);

         hibernateObject.addCredential(hibernateCredential);

         hibernateSession.flush();

      }
      else
      {
         throw new IdentityException("CredentialType not supported for a given IdentityObjectType");
      }
   }






   // Internal

   public void addIdentityObjectType(IdentityStoreInvocationContext ctx, IdentityObjectType type) throws IdentityException
   {
      HibernateIdentityObjectType hibernateType = new HibernateIdentityObjectType(type);
      getHibernateSession(ctx).persist(hibernateType);
      getHibernateSession(ctx).flush();

   }


   public void addIdentityObjectRelationshipType(IdentityStoreInvocationContext ctx, IdentityObjectRelationshipType type) throws IdentityException
   {
      HibernateIdentityObjectRelationshipType hibernateType = new HibernateIdentityObjectRelationshipType(type);
      getHibernateSession(ctx).persist(hibernateType);
      getHibernateSession(ctx).flush();
   }


   protected Session getHibernateSession(IdentityStoreInvocationContext ctx) throws IdentityException
   {
      try
      {
         return ((Session)ctx.getIdentityStoreSession().getSessionContext());
      }
      catch (Exception e)
      {
         throw new IdentityException("Cannot obtain Hibernate Session", e);
      }
   }

   private void checkIOInstance(IdentityObject io)
   {
      if (io == null)
      {
         throw new IllegalArgumentException("IdentityObject is null");
      }


   }

   private HibernateIdentityObject safeGet(IdentityStoreInvocationContext ctx, IdentityObject io) throws IdentityException
   {
      checkIOInstance(io);

      if (io instanceof HibernateIdentityObject)
      {
         return (HibernateIdentityObject)io;
      }

      return getHibernateIdentityObject(ctx, io);

   }


   private void checkIOType(IdentityObjectType iot) throws IdentityException
   {
      if (iot == null)
      {
         throw new IllegalArgumentException("IdentityObjectType is null");
      }


      if (!getSupportedFeatures().isIdentityObjectTypeSupported(iot))
      {
         if (!isAllowNotDefinedIdentityObjectTypes())
         {
            throw new IdentityException("IdentityType not supported by this IdentityStore implementation: " + iot);
         }
      }
   }

   private HibernateIdentityObjectType getHibernateIdentityObjectType(IdentityStoreInvocationContext ctx, IdentityObjectType type) throws IdentityException
   {

      checkIOType(type);

      HibernateIdentityObjectType hibernateType = null;

      Session hibernateSession = getHibernateSession(ctx);

      try
      {

         Criteria crit = hibernateSession.createCriteria(HibernateIdentityObjectType.class)
            .add(Restrictions.eq("name", type.getName()))
            .setCacheable(true);

         hibernateType = (HibernateIdentityObjectType)crit.uniqueResult();


         if (hibernateType  == null)
         {
            if (isAllowNotDefinedIdentityObjectTypes())
            {
               populateObjectTypes(hibernateSession, new String[]{type.getName()});
            }

            hibernateType = (HibernateIdentityObjectType)crit.uniqueResult();
         }


      }
      catch (Exception e)
      {
         throw new IdentityException("IdentityObjectType[" + type.getName() + "] not present in the store.", e);
      }

      if (hibernateType == null)
      {
         throw new IdentityException("IdentityObjectType[" + type.getName() + "] not present in the store.");
      }
                                                             
      return hibernateType;
   }

   private HibernateIdentityObject getHibernateIdentityObject(IdentityStoreInvocationContext ctx, IdentityObject io) throws IdentityException
   {

      HibernateIdentityObject hibernateObject = null;

      Session hibernateSession = getHibernateSession(ctx);

      try
      {

         hibernateObject = (HibernateIdentityObject)hibernateSession.createCriteria(HibernateIdentityObject.class)
            .add(Restrictions.eq("name", io.getName()))
            .createAlias("identityType", "type")
            .add(Restrictions.eq("type.name", io.getIdentityType().getName()))
            .createAlias("realm", "rm")
            .add(Restrictions.eq("rm.name", getRealmName(ctx)))
            .setCacheable(true)
            .uniqueResult();

      }
      catch (Exception e)
      {
         throw new IdentityException("IdentityObject[ " + io.getName() + " | " + io.getIdentityType().getName() + "] not present in the store.", e);
      }

      return hibernateObject;
   }

   private HibernateIdentityObjectRelationshipType getHibernateIdentityObjectRelationshipType(IdentityStoreInvocationContext ctx, IdentityObjectRelationshipType iot) throws IdentityException
   {

      HibernateIdentityObjectRelationshipType relationshipType = null;

      Session hibernateSession = getHibernateSession(ctx);

      try
      {

         relationshipType = (HibernateIdentityObjectRelationshipType)hibernateSession
            .createCriteria(HibernateIdentityObjectRelationshipType.class)
            .add(Restrictions.eq("name", iot.getName()))
            .setCacheable(true)
            .uniqueResult();
      }
      catch (Exception e)
      {
         throw new IdentityException("IdentityObjectRelationshipType[ " + iot.getName() + "] not present in the store.");
      }

      return relationshipType;
   }

   private HibernateIdentityObjectCredentialType getHibernateIdentityObjectCredentialType(IdentityStoreInvocationContext ctx, IdentityObjectCredentialType credentialType) throws IdentityException
   {
      Session session = getHibernateSession(ctx);

      HibernateIdentityObjectCredentialType hibernateType = null;

      try
      {
         hibernateType = (HibernateIdentityObjectCredentialType)session
            .createCriteria(HibernateIdentityObjectCredentialType.class)
            .add(Restrictions.eq("name", credentialType.getName()))
            .setCacheable(true)
            .uniqueResult();
      }
      catch (HibernateException e)
      {
         throw new IdentityException("IdentityObjectCredentialType[ " + credentialType.getName() + "] not present in the store.");
      }

      return hibernateType;

   }

   public void populateObjectTypes(Session hibernateSession, String[] typeNames) throws Exception
   {



      for (String typeName : typeNames)
      {

         //Check if present

         HibernateIdentityObjectType hibernateType = (HibernateIdentityObjectType)hibernateSession.
            createCriteria(HibernateIdentityObjectType.class).add(Restrictions.eq("name", typeName)).uniqueResult();

         if (hibernateType == null)
         {
            hibernateType = new HibernateIdentityObjectType(typeName);
            hibernateSession.persist(hibernateType);
         }

      }

   }

   public void populateRelationshipTypes(Session hibernateSession, String[] typeNames) throws Exception
   {

      for (String typeName : typeNames)
      {
         HibernateIdentityObjectRelationshipType hibernateType = (HibernateIdentityObjectRelationshipType)hibernateSession.
            createCriteria(HibernateIdentityObjectRelationshipType.class).add(Restrictions.eq("name", typeName)).uniqueResult();

         if (hibernateType == null)
         {
            hibernateType = new HibernateIdentityObjectRelationshipType(typeName);
            hibernateSession.persist(hibernateType);
         }
         
      }

   }

   public void populateCredentialTypes(Session hibernateSession, String[] typeNames) throws Exception
   {


      for (String typeName : typeNames)
      {
         HibernateIdentityObjectCredentialType hibernateType = (HibernateIdentityObjectCredentialType)hibernateSession.
            createCriteria(HibernateIdentityObjectCredentialType.class)
            .add(Restrictions.eq("name", typeName))
            .uniqueResult();

         if (hibernateType == null)
         {
            hibernateType = new HibernateIdentityObjectCredentialType(typeName);
            hibernateSession.persist(hibernateType);
         }

      }

   }



   public void addRealm(Session hibernateSession, String realmName) throws IdentityException
   {

      try
      {

         HibernateRealm realm = new HibernateRealm(realmName);
         hibernateSession.persist(realm);


      }
      catch (Exception e)
      {
         throw new IdentityException("Failed to create store realm", e);
      }
   }


   private HibernateRealm getRealm(Session hibernateSession, IdentityStoreInvocationContext ctx) throws IdentityException
   {
      if (getRealmName(ctx) == null)
      {
         throw new IllegalStateException("Realm Id not present");
      }

      HibernateRealm realm = null;

      // If store is not realm aware return null to create/get objects accessible from other realms 
      if (!isRealmAware())
      {
         realm = (HibernateRealm)hibernateSession.
            createCriteria(HibernateRealm.class)
            .add(Restrictions.eq("name", DEFAULT_REALM_NAME))
            .setCacheable(true)
            .uniqueResult();
    
         if (realm == null)
         {
            throw new IllegalStateException("Default store realm is not present: " + DEFAULT_REALM_NAME);
         }

      }
      else
      {
         realm = (HibernateRealm)hibernateSession.
            createCriteria(HibernateRealm.class)
            .add(Restrictions.eq("name", getRealmName(ctx)))
            .setCacheable(true)
            .uniqueResult();

         // TODO: other way to not lazy initialize realm? special method called on every new session creation
         if (realm == null)
         {
            HibernateRealm newRealm = new HibernateRealm(getRealmName(ctx));
            hibernateSession.persist(newRealm);
            return newRealm;
         }
      }

      

      return realm;
   }
   
   private String getRealmName(IdentityStoreInvocationContext ctx)
   {
      if (isRealmAware())
      {
         return ctx.getRealmId();
      }
      else
      {
         return DEFAULT_REALM_NAME;
      }
   }

   private boolean isRealmAware()
   {
      return isRealmAware;
   }
   
   private boolean isAllowNotDefinedAttributes()
   {
      return isAllowNotDefinedAttributes;
   }

   /**
    * Resolve store mapping for attribute name. If attribute is not mapped and store doesn't allow not defined
    * attributes throw exception
    * @param type
    * @param name
    * @return
    */
   private String resolveAttributeStoreMapping(IdentityObjectType type, String name) throws IdentityException
   {
      String mapping = null;

      if (attributesMetaData.containsKey(type.getName()))
      {
         IdentityObjectAttributeMetaData amd = attributesMetaData.get(type.getName()).get(name);

         if (amd != null)
         {
            mapping = amd.getStoreMapping() != null ? amd.getStoreMapping() : amd.getName();
            return mapping;
         }
      }

      if (isAllowNotDefinedAttributes())
      {
         mapping = name;
         return mapping;
      }

      throw new IdentityException("Attribute name is not configured in this store");
   }

   private String resolveAttributeNameFromStoreMapping(IdentityObjectType type, String mapping)
   {
      if (reverseAttributeMappings.containsKey(type.getName()))
      {
         Map<String, String> map = reverseAttributeMappings.get(type.getName());

         if (map != null)
         {
            String name = map.containsKey(mapping) ? map.get(mapping) : mapping;
            return name;
         }
      }

      if (isAllowNotDefinedAttributes())
      {
         return mapping;
      }
      return null;
   }

   //TODO: this kills performance and is present here only as "quick" hack to have the feature present and let to add test cases
   //TODO: needs to be redone at the hibernate query level
   private void filterByAttributesValues(Collection<IdentityObject> objects, Map<String, String[]> attrs)
   {
      Set<IdentityObject> toRemove = new HashSet<IdentityObject>();

      for (IdentityObject object : objects)
      {
         Map<String, Collection> presentAttrs = ((HibernateIdentityObject)object).getAttributesAsMap();
         for (Map.Entry<String, String[]> entry : attrs.entrySet())
         {
            if (presentAttrs.containsKey(entry.getKey()))
            {
               Set<String> given = new HashSet<String>(Arrays.asList(entry.getValue()));
               Collection present = presentAttrs.get(entry.getKey());

               for (String s : given)
               {
                  if (!present.contains(s))
                  {
                     toRemove.add(object);
                     break;
                  }
               }

            }
            else
            {
               toRemove.add(object);
               break;

            }
         }
      }

      for (IdentityObject identityObject : toRemove)
      {
         objects.remove(identityObject);
      }
   }

   protected boolean isAllowNotDefinedIdentityObjectTypes()
   {
      return isAllowNotDefinedIdentityObjectTypes;
   }

   public boolean isManageTransactionDuringBootstrap()
   {
      return isManageTransactionDuringBootstrap;
   }
}
