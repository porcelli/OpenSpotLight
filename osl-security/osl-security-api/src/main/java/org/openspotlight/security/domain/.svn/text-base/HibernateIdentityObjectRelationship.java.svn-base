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

import org.jboss.identity.idm.spi.model.IdentityObjectRelationship;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;

import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateIdentityObjectRelationship implements IdentityObjectRelationship
{
   public static final String findIdentityObjectRelationshipsByType =
      "select r from HibernateIdentityObjectRelationship r where r.type.name = :typeName";

   public static final String findIdentityObjectRelationshipNamesByType =
     "select r.name from HibernateIdentityObjectRelationship r where r.type.name = :typeName";


   private Long id;

   private HibernateIdentityObjectRelationshipName name;

   private HibernateIdentityObjectRelationshipType type;

   private HibernateIdentityObject fromIdentityObject;

   private HibernateIdentityObject toIdentityObject;

   private Map<String, String> properties = new HashMap<String, String>();

   public HibernateIdentityObjectRelationship()
   {
   }

   public HibernateIdentityObjectRelationship(HibernateIdentityObjectRelationshipType type, HibernateIdentityObject fromIdentityObject, HibernateIdentityObject toIdentityObject)
   {
      this.type = type;
      this.fromIdentityObject = fromIdentityObject;
      fromIdentityObject.getFromRelationships().add(this);
      this.toIdentityObject = toIdentityObject;
      toIdentityObject.getToRelationships().add(this);
   }

   public HibernateIdentityObjectRelationship(HibernateIdentityObjectRelationshipType type, HibernateIdentityObject fromIdentityObject, HibernateIdentityObject toIdentityObject, HibernateIdentityObjectRelationshipName name)
   {
      this.type = type;
      this.fromIdentityObject = fromIdentityObject;
      fromIdentityObject.getFromRelationships().add(this);
      this.toIdentityObject = toIdentityObject;
      toIdentityObject.getToRelationships().add(this);
      this.name = name;
   }

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public IdentityObjectRelationshipType getType()
   {
      return type;
   }

   public void setType(HibernateIdentityObjectRelationshipType type)
   {
      this.type = type;
   }

   public HibernateIdentityObject getFromIdentityObject()
   {
      return fromIdentityObject;
   }

   public void setFromIdentityObject(HibernateIdentityObject fromIdentityObject)
   {
      this.fromIdentityObject = fromIdentityObject;
   }

   public HibernateIdentityObject getToIdentityObject()
   {
      return toIdentityObject;
   }

   public void setToIdentityObject(HibernateIdentityObject toIdentityObject)
   {
      this.toIdentityObject = toIdentityObject;
   }

   public String getName()
   {
      if (name != null)
      {
         return name.getName();
      }
      return null;
   }

   public void setName(HibernateIdentityObjectRelationshipName name)
   {
      this.name = name;
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
