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

import org.jboss.identity.idm.spi.model.IdentityObjectAttribute;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class HibernateIdentityObjectAttribute implements IdentityObjectAttribute
{

   public static final String TYPE_TEXT = "text";

   public static final String TYPE_BINARY = "binary";

   private Long id;

   private HibernateIdentityObject identityObject;

   private String name;

   private String type;

   private HibernateIdentityObjectAttributeBinaryValue binaryValue = null;

   private Set<String> textValues = new HashSet<String>();


   public HibernateIdentityObjectAttribute()
   {
   }

   public HibernateIdentityObjectAttribute(HibernateIdentityObject identityObject, String name, String type)
   {
      this.identityObject = identityObject;
      this.name = name;
      setType(type);
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

   public HibernateIdentityObject getIdentityObject()
   {
      return identityObject;
   }

   public void setIdentityObject(HibernateIdentityObject identityObject)
   {
      this.identityObject = identityObject;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String newType)
   {
      if (!newType.equals(TYPE_TEXT) && !newType.equals(TYPE_BINARY))
      {
         throw new IllegalArgumentException("Type has not supported value." + " Name=" + name + "; type="  + type);
      }
      this.type = newType;
   }

   public HibernateIdentityObjectAttributeBinaryValue getBinaryValue()
   {
      return binaryValue;
   }

   public void setBinaryValue(HibernateIdentityObjectAttributeBinaryValue binaryValue)
   {
      this.binaryValue = binaryValue;
   }

   public Set<String> getTextValues()
   {
      return Collections.unmodifiableSet(textValues);
   }

   public void setTextValues(Collection<String> textValues)
   {
      this.textValues.clear();
      this.textValues.addAll(textValues);
   }

   public void addTextValue(String value)
   {
      getTextValues().add(value);
   }

   public Object getValue()
   {
      if (type.equals(TYPE_TEXT))
      {
         if (getTextValues().size() > 0)
         {
            return getTextValues().iterator().next();
         }
         else
         {
            return null;
         }
      }
      else if (type.equals(TYPE_BINARY))
      {
         return getBinaryValue().getValue();
      }
      else
      {
         throw new IllegalStateException("Type has not supported value."  + " Name=" + name + "; type="  + type);
      }
   }

   public void addValue(Object value)
   {
      if (type.equals(TYPE_TEXT))
      {
         if (!(value instanceof String))
         {
            throw new IllegalArgumentException("String value expected with a set type." + " Name=" + name + "; type="  + type);
         }
         addTextValue((String)value);
      }
      else if (type.equals(TYPE_BINARY))
      {
         if (!(value instanceof byte[]))
         {
            throw new IllegalArgumentException("byte[] value expected with a set type."  + " Name=" + name + "; type="  + type);
         }

         setBinaryValue(new HibernateIdentityObjectAttributeBinaryValue((byte[])value));
      }
      else
      {
         throw new IllegalStateException("Type has not supported value or has not been set."  + " Name=" + name + "; type="  + type);
      }
   }

   public Collection getValues()
   {
      if (type.equals(TYPE_TEXT))
      {
         return Collections.unmodifiableSet(getTextValues());
      }
      else if (type.equals(TYPE_BINARY))
      {
         Set vals = new HashSet();
         vals.add(getBinaryValue().getValue());
         return vals;
      }
      else
      {
         throw new IllegalStateException("Type has not supported value."  + " Name=" + name + "; type="  + type);
      }
   }

   public int getSize()
   {
      if (type.equals(TYPE_TEXT))
      {
         return getTextValues().size();
      }
      else if (type.equals(TYPE_BINARY))
      {
         if (getBinaryValue() != null)
         {
            return 1;
         }
         return 0;
      }
      else
      {
         throw new IllegalStateException("Type has not supported value."  + " Name=" + name + "; type="  + type);
      }
   }
}
