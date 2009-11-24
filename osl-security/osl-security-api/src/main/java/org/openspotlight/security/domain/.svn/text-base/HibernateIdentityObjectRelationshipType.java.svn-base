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

import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateIdentityObjectRelationshipType implements IdentityObjectRelationshipType
{

   public static final String findIdentityObjectRelationshipTypeByName =
       "select t from HibernateIdentityObjectRelationshipType t where t.name like :name";

   private Long id;

   private String name;

   public HibernateIdentityObjectRelationshipType()
   {
   }

   public HibernateIdentityObjectRelationshipType(String name)
   {
      this.name = name;
   }

   public HibernateIdentityObjectRelationshipType(IdentityObjectRelationshipType type)
   {
      if (type == null)
      {
         throw new IllegalArgumentException("type is null");
      }
      if (type.getName() != null)
      {
         this.name = type.getName();
      }
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
}
