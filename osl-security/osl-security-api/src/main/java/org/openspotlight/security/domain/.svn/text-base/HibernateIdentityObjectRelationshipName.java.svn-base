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

package org.jboss.identity.idm.impl.model.hibernate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateIdentityObjectRelationshipName
{

   public static final String findIdentityObjectRelationshipNameByName =
      "select rn from HibernateIdentityObjectRelationshipName rn where rn.name like :name and rn.realm.name = :realmName";

   public static final String findIdentityObjectRelationshipNames =
      "select rn.name from HibernateIdentityObjectRelationshipName rn where rn.name like :nameFilter and rn.realm.name = :realmName";

   public static final String findIdentityObjectRelationshipNamesOrderedByNameAsc =
      "select rn.name from HibernateIdentityObjectRelationshipName rn where rn.name like :nameFilter and rn.realm.name = :realmName " +
         "order by rn.name asc";

   public static final String findIdentityObjectRelationshipNamesOrderedByNameDesc =
      "select rn.name from HibernateIdentityObjectRelationshipName rn where rn.name like :nameFilter and rn.realm.name = :realmName " +
         "order by rn.name desc";

   public static final String findIdentityObjectRelationshipNamesForIdentityObject =
      "select r.name.name from HibernateIdentityObjectRelationship r where " +
         "r.fromIdentityObject = :identityObject or r.toIdentityObject = :identityObject";

   public static final String findIdentityObjectRelationshipNamesForIdentityObjectOrderedByNameAsc =
      "select r.name.name from HibernateIdentityObjectRelationship r where " +
         "r.fromIdentityObject = :identityObject or r.toIdentityObject = :identityObject " +
         "order by r.name.name asc";

   public static final String findIdentityObjectRelationshipNamesForIdentityObjectOrdereByNameDesc =
      "select r.name.name from HibernateIdentityObjectRelationship r where " +
         "r.fromIdentityObject = :identityObject or r.toIdentityObject = :identityObject " +
         "order by r.name.name desc";

   private Long id;

   private String name;

   private HibernateRealm realm;

   private Map<String, String> properties = new HashMap<String, String>();

   public HibernateIdentityObjectRelationshipName()
   {
   }

   public HibernateIdentityObjectRelationshipName(String name, HibernateRealm realm)
   {
      this.name = name;
      this.realm = realm;
   }

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public HibernateRealm getRealm()
   {
      return realm;
   }

   public void setRealm(HibernateRealm realm)
   {
      this.realm = realm;
   }

   public Map<String, String> getProperties()
   {
      return properties;
   }

   public void setProperties(Map<String, String> properties)
   {
      this.properties = properties;
   }
}
